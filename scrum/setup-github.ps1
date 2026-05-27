# ============================================================
# setup-github.ps1
# Cria labels, milestones (sprints) e issues no GitHub
# usando o GitHub CLI (gh).
#
# Pre-requisitos:
#   - GitHub CLI instalado: https://cli.github.com/
#   - Autenticado: gh auth login
#
# Uso:
#   .\setup-github.ps1 -Repo "seu-usuario/seu-repositorio"
#
# Exemplo:
#   .\setup-github.ps1 -Repo "joaosilva/meu-projeto-web"
# ============================================================

param(
    [Parameter(Mandatory = $true)]
    [string]$Repo
)

$ErrorActionPreference = "Stop"

$ScriptDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$ConfigDir   = Join-Path $ScriptDir "config"
$LabelsFile  = Join-Path $ConfigDir "labels.json"
$MilesFile   = Join-Path $ConfigDir "milestones.json"
$BacklogFile = Join-Path $ConfigDir "backlog.json"

# ------------------------------------------------------------
# Verificacoes iniciais
# ------------------------------------------------------------
Write-Host "`n=== Verificando pre-requisitos ===" -ForegroundColor Cyan

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    Write-Error "GitHub CLI (gh) nao encontrado. Instale em: https://cli.github.com/"
}

$authStatus = gh auth status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Nao autenticado no GitHub CLI. Execute: gh auth login"
}

Write-Host "GitHub CLI autenticado com sucesso." -ForegroundColor Green
Write-Host "Repositorio alvo: $Repo" -ForegroundColor Green

# ------------------------------------------------------------
# Funcao auxiliar: exibe resultado de cada operacao
# ------------------------------------------------------------
function Show-Result {
    param([string]$Label, [bool]$Success)
    if ($Success) {
        Write-Host "  [OK] $Label" -ForegroundColor Green
    } else {
        Write-Host "  [ERRO] $Label" -ForegroundColor Red
    }
}

# ============================================================
# 1. LABELS
# ============================================================
Write-Host "`n=== Criando labels ===" -ForegroundColor Cyan

$labels = Get-Content $LabelsFile -Raw | ConvertFrom-Json

foreach ($label in $labels) {
    $result = gh label create $label.name `
        --color $label.color `
        --description $label.description `
        --repo $Repo `
        --force 2>&1
    Show-Result "Label: $($label.name)" ($LASTEXITCODE -eq 0)
}

# ============================================================
# 2. MILESTONES (Sprints)
# ============================================================
Write-Host "`n=== Criando milestones (sprints) ===" -ForegroundColor Cyan

$milestones = Get-Content $MilesFile -Raw | ConvertFrom-Json

# Mapeia titulo -> numero do milestone apos criacao
$milestoneMap = @{}

foreach ($ms in $milestones) {
    $payload = @{
        title       = $ms.title
        description = $ms.description
        due_on      = $ms.due_on
        state       = "open"
    } | ConvertTo-Json -Compress

    $response = $payload | gh api `
        "repos/$Repo/milestones" `
        --method POST `
        --input - 2>&1

    if ($LASTEXITCODE -eq 0) {
        $created = $response | ConvertFrom-Json
        $milestoneMap[$ms.title] = $created.number
        Show-Result "Milestone: $($ms.title) (#$($created.number))" $true
    } else {
        # Pode ja existir: busca pelo titulo
        $existing = gh api "repos/$Repo/milestones" --paginate 2>$null | ConvertFrom-Json |
            Where-Object { $_.title -eq $ms.title } | Select-Object -First 1
        if ($existing) {
            $milestoneMap[$ms.title] = $existing.number
            Show-Result "Milestone ja existe: $($ms.title) (#$($existing.number))" $true
        } else {
            Show-Result "Milestone: $($ms.title)" $false
        }
    }
}

# ============================================================
# 3. ISSUES (Backlog)
# ============================================================
Write-Host "`n=== Criando issues do backlog ===" -ForegroundColor Cyan

$backlog = Get-Content $BacklogFile -Raw | ConvertFrom-Json

foreach ($sprint in $backlog) {
    $msNumber = $milestoneMap[$sprint.milestone]
    Write-Host "`n  >> $($sprint.milestone)" -ForegroundColor Yellow

    foreach ($issue in $sprint.issues) {
        $labelList = $issue.labels -join ","

        $args = @(
            "issue", "create",
            "--repo", $Repo,
            "--title", $issue.title,
            "--body", $issue.body,
            "--label", $labelList
        )

        if ($msNumber) {
            $args += "--milestone"
            $args += [string]$msNumber
        }

        $result = gh @args 2>&1
        Show-Result $issue.title ($LASTEXITCODE -eq 0)
    }
}

# ============================================================
# 4. PROJECT (Kanban)
# ============================================================
Write-Host "`n=== Criando GitHub Project (Kanban) ===" -ForegroundColor Cyan

$projectName = "Migracao para Web - Spring Boot e Angular"

$projectResponse = gh project create `
    --owner ($Repo -split "/")[0] `
    --title $projectName 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Projeto criado: $projectName" -ForegroundColor Green
    Write-Host "  Acesse Projects no repositorio para ver o Kanban." -ForegroundColor DarkGray
    Write-Host "  Adicione as issues ao projeto manualmente ou via:" -ForegroundColor DarkGray
    Write-Host "  gh project item-add <NUMBER> --owner <usuario> --url <issue-url>" -ForegroundColor DarkGray
} else {
    Write-Host "  [AVISO] Nao foi possivel criar o projeto automaticamente." -ForegroundColor Yellow
    Write-Host "  Crie manualmente em: https://github.com/$Repo/projects" -ForegroundColor DarkGray
}

# ============================================================
# Resumo final
# ============================================================
Write-Host "`n=== Configuracao concluida! ===" -ForegroundColor Cyan
Write-Host "Acesse seu repositorio para ver o resultado:" -ForegroundColor White
Write-Host "  https://github.com/$Repo/issues" -ForegroundColor DarkCyan
Write-Host "  https://github.com/$Repo/milestones" -ForegroundColor DarkCyan
Write-Host "  https://github.com/$Repo/projects" -ForegroundColor DarkCyan
