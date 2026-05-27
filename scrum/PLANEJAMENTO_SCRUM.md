# Planejamento Scrum - Migracao para Web com Spring Boot e Angular

## Visao Geral

Este documento descreve o planejamento do projeto de migracao de uma aplicacao Java console para uma aplicacao web utilizando **Spring Boot** (backend) e **Angular** (frontend).

O projeto e organizado com a metodologia **Scrum**, usando o **GitHub Projects** como quadro Kanban para acompanhamento das tarefas.

---

## Estrutura do Planejamento

### Milestones (Marcos do Projeto)

O projeto possui dois marcos principais que representam entregas significativas:

| Milestone | Descricao | Prazo |
|-----------|-----------|-------|
| **Preparacao REST** | Ambiente configurado e API REST completa com Spring Boot | 31/05/2026 |
| **Criacao Aplicacao Angular** | Frontend Angular integrado com a API REST | 14/06/2026 |

---

### Sprints

O projeto e dividido em **4 sprints de uma semana**, iniciando em **18 de maio de 2026**.

#### Sprint 1 — Ambiente e Backend Inicial
**Periodo:** 18/05 a 24/05 | **Milestone:** Preparacao REST

| Issue | Tipo | Prioridade |
|-------|------|------------|
| Configurar ambiente de desenvolvimento | config | Alta |
| Analisar e mapear projeto console existente | documentacao | Alta |
| Criar projeto Spring Boot no Spring Initializr | backend, config | Alta |
| Definir estrutura de pacotes do backend | backend | Media |

**Meta da sprint:** ambiente funcionando e projeto Spring Boot subindo sem erros.

---

#### Sprint 2 — CRUD REST com Spring Boot
**Periodo:** 25/05 a 31/05 | **Milestone:** Preparacao REST

| Issue | Tipo | Prioridade |
|-------|------|------------|
| Criar entidades JPA: TipoProduto e Produto | backend | Alta |
| Criar repositories com JpaRepository | backend | Alta |
| Implementar TipoProdutoService com CRUD | backend | Alta |
| Implementar ProdutoService com CRUD | backend | Alta |
| Criar DTOs de request e response | backend | Alta |
| Criar controllers REST para Produto e TipoProduto | backend, teste | Alta |

**Meta da sprint:** API REST completa e testada no Postman/Insomnia.

---

#### Sprint 3 — Validacoes, Frontend Angular e Integracao Inicial
**Periodo:** 01/06 a 07/06 | **Milestone:** Criacao Aplicacao Angular

| Issue | Tipo | Prioridade |
|-------|------|------------|
| Aplicar Bean Validation nos DTOs | backend | Alta |
| Criar excecao de negocio RegraNegocioException | backend | Alta |
| Criar GlobalExceptionHandler com @ControllerAdvice | backend | Alta |
| Criar projeto Angular com Angular CLI | frontend, config | Alta |
| Criar ProdutoService e TipoProdutoService no Angular | frontend | Alta |
| Configurar CORS no backend | backend, config | Alta |

**Meta da sprint:** backend robusto com tratamento de erros e projeto Angular criado e conectado a API.

---

#### Sprint 4 — Telas Angular, Integracao Final e Apresentacao
**Periodo:** 08/06 a 14/06 | **Milestone:** Criacao Aplicacao Angular

| Issue | Tipo | Prioridade |
|-------|------|------------|
| Implementar componente lista-produtos | frontend | Alta |
| Implementar componente form-produto | frontend | Alta |
| Testar fluxo completo frontend-backend | frontend, backend, teste | Alta |
| Revisar padroes de projeto e escrever relatorio | documentacao | Alta |
| Preparar e ensaiar demonstracao do sistema | documentacao | Alta |

**Meta da sprint:** sistema funcionando ponta a ponta e grupo pronto para apresentar.

---

### Labels Utilizadas

As labels organizam as issues por sprint, camada tecnica, milestone e prioridade.

| Label | Finalidade |
|-------|-----------|
| `sprint-1` a `sprint-4` | Identifica a qual sprint a issue pertence |
| `milestone-rest` | Issue pertence ao milestone Preparacao REST |
| `milestone-angular` | Issue pertence ao milestone Criacao Aplicacao Angular |
| `backend` | Tarefa de backend Spring Boot |
| `frontend` | Tarefa de frontend Angular |
| `config` | Configuracao de ambiente ou projeto |
| `documentacao` | Documentacao, README, relatorio |
| `teste` | Testes manuais ou automatizados |
| `prioridade-alta` | Alta prioridade |
| `prioridade-media` | Media prioridade |
| `prioridade-baixa` | Baixa prioridade |

---

## Configuracao do Kanban no GitHub

### Pre-requisitos

1. Ter o **GitHub CLI** instalado: https://cli.github.com/
2. Estar autenticado:
   ```bash
   gh auth login
   ```
3. Ter um repositorio criado no GitHub para o projeto.

---

### Passo 1 — Clonar ou inicializar o repositorio

Se ainda nao possui o repositorio local:

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

Ou inicialize um novo:

```bash
git init
git remote add origin https://github.com/seu-usuario/seu-repositorio.git
```

---

### Passo 2 — Executar o script de configuracao

Navegue ate a pasta `scrum` e execute o script PowerShell:

```powershell
cd scrum
.\setup-github.ps1 -Repo "seu-usuario/seu-repositorio"
```

O script realiza automaticamente:

1. **Cria as labels** — sprints, camadas tecnicas, milestones e prioridades
2. **Cria os milestones** — Preparacao REST e Criacao Aplicacao Angular com suas datas de entrega
3. **Cria todas as issues** — com titulo, descricao, criterios de aceitacao, labels e milestone vinculado
4. **Cria o GitHub Project** — quadro Kanban do repositorio

> **Atencao:** se o repositorio for de uma organizacao, use o formato `nome-org/nome-repo` no parametro `-Repo`.

---

### Passo 3 — Configurar o quadro Kanban (GitHub Projects)

Apos o script rodar, acesse o Projects do repositorio e configure as colunas do Kanban:

1. Acesse `https://github.com/seu-usuario/seu-repositorio/projects`
2. Abra o projeto criado pelo script (ou crie um novo clicando em **New project**)
3. Escolha o layout **Board** (Kanban)
4. Configure as colunas:

| Coluna | Significado |
|--------|-------------|
| **Backlog** | Issues planejadas, ainda nao iniciadas |
| **Em andamento** | Issues que o grupo esta trabalhando neste momento |
| **Em revisao** | Issues concluidas aguardando revisao do grupo ou professor |
| **Concluido** | Issues finalizadas e aceitas |

5. Para adicionar as issues ao quadro, clique em **+ Add item** em cada coluna e selecione as issues criadas pelo script.

---

### Passo 4 — Organizar o backlog por sprint

No quadro Kanban, use os filtros para visualizar por sprint:

- Filtrar por label: `sprint-1`, `sprint-2`, `sprint-3`, `sprint-4`
- Filtrar por milestone: `Preparacao REST` ou `Criacao Aplicacao Angular`

Para adicionar um campo de sprint customizado no Project:

1. Clique em **+** no cabecalho das colunas > **New field**
2. Crie um campo do tipo **Iteration** chamado `Sprint`
3. Defina as iteracoes com os periodos das sprints

---

### Passo 5 — Fluxo de trabalho durante o projeto

Durante cada sprint, o grupo deve seguir este fluxo:

```
Backlog → Em andamento → Em revisao → Concluido
```

1. No inicio da sprint, mova as issues da sprint atual de **Backlog** para **Em andamento**
2. Ao concluir o desenvolvimento de uma issue, mova para **Em revisao**
3. Apos validacao (teste ou revisao do colega), mova para **Concluido** e feche a issue com:
   ```bash
   gh issue close NUMERO --repo seu-usuario/seu-repositorio
   ```
4. Ao final da sprint, todas as issues da sprint devem estar em **Concluido**

---

### Estrutura dos Arquivos de Configuracao

```
scrum/
  config/
    labels.json       <- definicao das labels (nome, cor, descricao)
    milestones.json   <- definicao dos milestones com datas de entrega
    backlog.json      <- todas as issues organizadas por sprint e milestone
  setup-github.ps1    <- script PowerShell que carrega tudo no GitHub
  PLANEJAMENTO_SCRUM.md  <- este arquivo
```

---

## Perguntas Frequentes

**Posso executar o script mais de uma vez?**
Sim. As labels sao criadas com `--force`, entao serao sobrescritas se ja existirem. Os milestones e issues serao duplicados se ja existirem — remova os anteriores antes de rodar novamente se necessario.

**O script funciona no Mac ou Linux?**
O script e PowerShell. No Mac/Linux, instale o PowerShell (`brew install powershell`) ou converta os comandos para shell script (`setup-github.sh`).

**Como verificar se tudo foi criado corretamente?**
Acesse:
- Issues: `https://github.com/seu-usuario/seu-repositorio/issues`
- Milestones: `https://github.com/seu-usuario/seu-repositorio/milestones`
- Labels: `https://github.com/seu-usuario/seu-repositorio/labels`
- Projects: `https://github.com/seu-usuario/seu-repositorio/projects`
