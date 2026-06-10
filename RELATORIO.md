# Relatório de Migração — Aplicação Console para Web

**Disciplina:** Padrões de Projeto  
**Repositório:** https://github.com/ygorpradoch/trabalhoFinal  
**Data de entrega:** Junho de 2026

---

## 1. Padrões de Projeto Identificados

### 1.1 MVC — Model-View-Controller

O padrão MVC organiza o sistema em três responsabilidades distintas. No backend, o **Model** é representado pelas entidades do domínio (`Produto` e `TipoProduto`), o **Controller** pelas classes REST que recebem as requisições HTTP, e a **View** pelo frontend Angular que renderiza os dados para o usuário.

**Exemplo no código — Controller delegando para o Service:**

```java
// ProdutoController.java
@PostMapping
public ResponseEntity<ProdutoResponse> salvar(@Valid @RequestBody ProdutoRequest request) {
    TipoProduto tipoProduto = new TipoProduto();
    tipoProduto.setId(request.getTipoProdutoId());
    Produto produto = new Produto(request.getNome(), request.getPreco(), tipoProduto);
    Produto salvo = produtoService.salvar(produto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoResponse(salvo));
}
```

O controller não contém nenhuma regra de negócio: apenas recebe a requisição, monta o objeto de domínio e delega ao service.

---

### 1.2 Repository — Abstração da Persistência

O padrão Repository isola a camada de domínio dos detalhes de acesso a dados. As interfaces `ProdutoRepository` e `TipoProdutoRepository` estendem `JpaRepository`, fornecendo operações CRUD e permitindo que a camada de serviço trabalhe com objetos de domínio sem conhecer o banco de dados.

**Exemplo no código:**

```java
// ProdutoRepository.java
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByTipoProdutoId(Long tipoProdutoId);
}

// TipoProdutoRepository.java
@Repository
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
```

Os métodos derivados (`existsByTipoProdutoId`, `existsByNomeIgnoreCase`) são gerados automaticamente pelo Spring Data JPA a partir do nome do método, sem necessidade de SQL manual.

---

### 1.3 Service Layer — Camada de Serviço

O padrão Service Layer concentra toda a lógica de negócio em classes dedicadas, mantendo os controllers finos e as entidades simples. Todo o comportamento que vai além de um CRUD puro — como validar unicidade de nome ou impedir remoção de tipo com produtos vinculados — está exclusivamente nos services.

**Exemplo no código — regra de negócio no TipoProdutoService:**

```java
// TipoProdutoService.java
@Transactional
public TipoProduto salvar(TipoProduto tipoProduto) {
    if (tipoProdutoRepository.existsByNomeIgnoreCase(tipoProduto.getNome())) {
        throw new RegraNegocioException(
            "Já existe um tipo de produto com o nome: " + tipoProduto.getNome());
    }
    return tipoProdutoRepository.save(tipoProduto);
}

@Transactional
public void remover(Long id) {
    buscarPorId(id);
    if (produtoRepository.existsByTipoProdutoId(id)) {
        throw new RegraNegocioException(
            "Não é possível remover o tipo de produto pois existem produtos vinculados a ele.");
    }
    tipoProdutoRepository.deleteById(id);
}
```

---

### 1.4 DTO — Data Transfer Object

O padrão DTO desacopla o modelo de domínio da interface da API. As classes `ProdutoRequest` e `ProdutoResponse` definem exatamente quais campos são aceitos na entrada e retornados na saída, sem expor diretamente as entidades JPA.

**Exemplo no código — validações aplicadas ao DTO de entrada:**

```java
// ProdutoRequest.java
public class ProdutoRequest {

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nome;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser um valor positivo.")
    private BigDecimal preco;

    @NotNull(message = "O tipo de produto é obrigatório.")
    private Long tipoProdutoId;
}
```

O `ProdutoResponse` inclui `tipoProdutoNome` (campo calculado a partir do relacionamento), informação que não faria sentido estar na entidade JPA diretamente.

---

### 1.5 Observer com RxJS — Frontend Angular

No frontend, o padrão Observer é implementado pelo RxJS através do tipo `Observable`. Os services Angular retornam `Observable<T>`, e os componentes se inscrevem (`subscribe`) para reagir às respostas da API de forma assíncrona, sem bloquear a interface.

**Exemplo no código — componente reagindo ao Observable:**

```typescript
// lista-produtos.ts
carregarProdutos(): void {
    this.carregando = true;
    this.produtoService.listar().subscribe({
        next: (lista) => {
            this.produtos = lista;
            this.carregando = false;
        },
        error: () => {
            this.mensagemErro = 'Erro ao carregar produtos.';
            this.carregando = false;
        }
    });
}
```

O componente não conhece os detalhes da chamada HTTP — apenas observa o resultado quando ele chega.

---

### 1.6 Exception Handler Centralizado

O `GlobalExceptionHandler` com `@RestControllerAdvice` implementa o princípio de tratamento de erros em um único ponto, evitando blocos try-catch espalhados pelos controllers e padronizando o formato das respostas de erro da API.

```java
// GlobalExceptionHandler.java
@ExceptionHandler(RegraNegocioException.class)
public ResponseEntity<Map<String, Object>> handleRegraNegocio(RegraNegocioException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
    body.put("erro", "Regra de negócio violada");
    body.put("mensagem", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
}
```

---

## 2. Dificuldades Encontradas

### 2.1 Detecção de Mudanças no Angular 21

A principal dificuldade técnica foi a ausência do `zone.js` no projeto Angular 21 gerado pelo `ng new`. Essa versão adota detecção de mudanças sem Zone.js (zoneless) por padrão, o que fez com que as chamadas HTTP retornassem dados corretamente (visível no Network do browser), mas os componentes não re-renderizassem após receber a resposta. A solução foi instalar o `zone.js` explicitamente e registrá-lo em `main.ts` com `import 'zone.js'`, além de configurar `provideZoneChangeDetection` no `app.config.ts`.

### 2.2 Maven e Java não disponíveis no PATH

O ambiente de desenvolvimento não possuía Java nem Maven instalados. Foi necessário instalar o Java 17 (Eclipse Temurin) via WinGet e o Maven 3.9.6 manualmente via download do arquivo ZIP e configuração das variáveis de ambiente `JAVA_HOME` e `PATH`, o que atrasou o início dos testes do backend.

### 2.3 Incompatibilidade de Sintaxe PowerShell

O ambiente Windows com PowerShell apresentou incompatibilidades com a sintaxe `&&` (separador de comandos do bash) e com here-documents (`<<'EOF'`), exigindo adaptação de todos os comandos encadeados para o formato PowerShell. Isso adicionou retrabalho durante a configuração do ambiente e execução de scripts.

### 2.4 Autenticação do GitHub CLI para o Kanban

A configuração automática do Kanban via script `setup-github.ps1` requereu autenticação interativa com o GitHub CLI (`gh auth login`) e escopo adicional `project`, que não estava incluso no token padrão. Como o comando `gh auth refresh` exige interação com o browser, não pôde ser automatizado, tornando a criação do GitHub Project uma etapa manual.

### 2.5 Migração da Arquitetura Console para REST

A aplicação original utilizava um `ProdutoController` orientado a menu (leitura de entrada do usuário via `Scanner`) e persistência em JSON por meio de uma classe utilitária `JsonMini`. Toda essa camada de I/O foi substituída por endpoints REST com serialização automática pelo Jackson, e a persistência foi migrada para JPA com banco H2 in-memory, exigindo repensar o ciclo de vida dos dados (que antes eram gravados em arquivo e agora residem apenas em memória durante a execução).

---

## 3. Sugestões de Melhorias Futuras

### 3.1 Banco de Dados Persistente

Substituir o H2 in-memory por PostgreSQL ou MySQL. Com isso, os dados sobreviveriam a reinicializações do servidor. Bastaria alterar a dependência no `pom.xml` e as configurações em `application.properties`, sem alterar nenhuma linha do código de negócio (pois a camada de Repository abstrai o banco).

### 3.2 Operação de Atualização (PUT)

Atualmente o sistema suporta apenas listar, cadastrar e remover. Adicionar endpoints `PUT /api/produtos/{id}` e `PUT /api/tipos-produto/{id}` com seus respectivos DTOs de atualização completaria o CRUD e tornaria o sistema utilizável em cenários reais.

### 3.3 Paginação e Busca

A listagem de produtos retorna todos os registros de uma vez. Em cenários com grande volume, isso se torna ineficiente. O Spring Data JPA suporta `Pageable` nativamente: bastaria adicionar parâmetros de página e tamanho aos endpoints GET e exibir controles de paginação no frontend Angular.

### 3.4 Autenticação e Autorização

O sistema não possui controle de acesso. Integrar Spring Security com autenticação JWT permitiria proteger os endpoints de escrita (POST, DELETE) e restringir o acesso por perfil de usuário (ex.: apenas administradores podem remover tipos de produto).

### 3.5 Testes Automatizados

A cobertura de testes é mínima. Recomenda-se adicionar testes unitários para os Services (usando Mockito para simular os Repositories) e testes de integração para os Controllers (usando `@SpringBootTest` e `MockMvc`). No frontend, testes de componente com Jasmine/Karma aumentariam a confiabilidade dos fluxos de cadastro e listagem.

### 3.6 Containerização com Docker

Criar um `Dockerfile` para o backend e um `docker-compose.yml` com backend + banco de dados facilitaria a execução do projeto em qualquer máquina sem necessidade de instalar Java, Maven ou configurar variáveis de ambiente manualmente — eliminando a dificuldade descrita no item 2.2.
