# Usar a API do GitHub com um Personal Access Token (PAT)

Este guia mostra como usar a API do GitHub e operar com repositórios sem precisar do `gh` instalado, usando um token de acesso (PAT). Inclui exemplos práticos com `curl`, autenticação do `git` e como criar Secrets para GitHub Actions via API.

**Resumo rápido**

- Crie um PAT com os scopes necessários (ex.: `repo`, `workflow`).
- Use o header `Authorization: token YOUR_TOKEN` nas requisições HTTP.
- Para operações Git, prefira um credential helper em vez de colocar o token diretamente na URL.

---

## 1) Criar o token (PAT)

- Vá em Settings → Developer settings → Personal access tokens (classic) ou Tokens (fine‑grained).
- Selecione os scopes necessários: por exemplo `repo` para acesso a repositórios, `workflow` para Actions.

## 2) Cabeçalho HTTP

Use o token no header `Authorization`:

```bash
-H "Authorization: token $TOKEN"
```

(observação: para tokens de GitHub Apps pode-se usar `Bearer`.)

## 3) Exemplos úteis com `curl`

- Listar repositórios do usuário:

```bash
curl -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github+json" \
  https://api.github.com/user/repos
```

- Criar um *issue*:

```bash
curl -X POST -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/OWNER/REPO/issues \
  -d '{"title":"Teste","body":"Criado via API"}'
```

- Criar um Pull Request:

```bash
curl -X POST -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/OWNER/REPO/pulls \
  -d '{"title":"Minha PR","head":"branch-fonte","base":"main","body":"Descrição"}'
```

- Criar/atualizar arquivo via `contents` (conteúdo em base64):

```bash
# Linux/Mac: base64 -w0 arquivo.txt
# Exemplo com jq para montar payload
content_b64=$(base64 -w0 arquivo.txt)
payload=$(jq -n --arg m "Mensagem de commit" --arg c "$content_b64" '{message:$m,content:$c,branch:"main"}')
curl -X PUT -H "Authorization: token $TOKEN" -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/OWNER/REPO/contents/caminho/arquivo.txt \
  -d "$payload"
```

Se você está no Windows PowerShell, gere base64 com:

```powershell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Content -Raw arquivo.txt)))
```

## 4) Fazer push com `git` sem `gh`

- Não é necessário `gh` para `git push`. Para autenticar via HTTPS, você pode usar o token como senha com seu usuário GitHub:

```bash
git remote set-url origin https://USERNAME:YOUR_TOKEN@github.com/OWNER/REPO.git
git push origin nome-da-branch
```

- Atenção: evitar expor token no histórico do shell. Recomendado:
  - Usar `git credential-manager` (Windows) ou `git-credential-store`/`git-credential-cache`.
  - Ou armazenar em `~/.git-credentials` com permissões restritas.

## 5) Criar Secrets para GitHub Actions via API

Fluxo resumido:

1. GET da public key do repositório (`/actions/secrets/public-key`).
2. Encriptar o valor com a public key (libsodium / SealedBox).
3. PUT em `/actions/secrets/NAME` com `encrypted_value` e `key_id`.

Exemplo em Python (requere `pip install pynacl requests`):

```python
import base64, requests
from nacl import encoding, public

token = "YOUR_TOKEN"
owner = "OWNER"
repo = "REPO"
headers = {"Authorization":f"token {token}","Accept":"application/vnd.github+json"}

# 1) obter public key
r = requests.get(f"https://api.github.com/repos/{owner}/{repo}/actions/secrets/public-key", headers=headers)
r.raise_for_status()
data = r.json()
key_id = data["key_id"]
key = data["key"]

# 2) encriptar
public_key = public.PublicKey(key.encode('utf-8'), encoding.Base64Encoder())
sealed_box = public.SealedBox(public_key)
encrypted = sealed_box.encrypt(b"VALOR_DO_SECRET")
encrypted_value = base64.b64encode(encrypted).decode()

# 3) enviar secret
payload = {"encrypted_value": encrypted_value, "key_id": key_id}
resp = requests.put(f"https://api.github.com/repos/{owner}/{repo}/actions/secrets/MY_SECRET", headers=headers, json=payload)
print(resp.status_code, resp.text)
```

## 6) Bibliotecas úteis

- Node: `@octokit/rest` (oficial) — muito conveniente para automações.
- Python: `PyGithub` ou `requests` quando quiser controle fino.

## 7) Usando o script PowerShell do repositório

+ O repositório já tem um script em `scrum/setup-github-api.ps1` que cria labels, milestones e issues usando a API do GitHub.
+ Ele lê os arquivos de configuração em `scrum/config/labels.json`, `scrum/config/milestones.json` e `scrum/config/backlog.json`.
+ É necessário um Personal Access Token (PAT) com escopo `repo`.

### Exemplo de uso

```powershell
$env:GITHUB_TOKEN = "SEU_TOKEN"
.\scrum\setup-github-api.ps1 -Repo "ProfBezerra/gabarito-crud-grasp-produto"
```

ou

```powershell
.\scrum\setup-github-api.ps1 -Repo "ProfBezerra/gabarito-crud-grasp-produto" -Token "SEU_TOKEN"
```

### O que o script faz

- cria os labels definidos em `scrum/config/labels.json`
- cria os milestones definidos em `scrum/config/milestones.json`
- cria as issues definidas em `scrum/config/backlog.json`
- tenta criar também um projeto GitHub Kanban (se a API permitir)

## Avisos de segurança

- Nunca comite o token em repositórios públicos.
- Prefira tokens *fine‑grained* quando possível.
- Revogue tokens que eventualmente vazarem.
- Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

---

## Próximos passos

- Posso ajustar os exemplos para seu repositório `ProfBezerra/gabarito-crud-grasp-produto` (ex.: criar um Secret chamado `MY_SECRET`).

Se quiser que eu gere comandos prontos para seu repositório, responda com que ação deseja automatizar.
