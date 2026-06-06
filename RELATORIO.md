# Relatório — Milestone 1
## Projeto JACKUT · P2 · 2023.1

---

## 1. Visão Geral

O **JACKUT** é um sistema de rede social desenvolvido em Java como projeto de Programação 2 (P2). O Milestone 1 abrange a implementação das quatro primeiras User Stories, cobrindo desde a criação de contas até o envio e leitura de recados entre usuários, com persistência de dados entre execuções.

A validação é realizada por meio da biblioteca **EasyAccept**, que executa scripts de teste de aceitação contra a `Facade` do sistema. Todos os **184 testes** das 8 suítes foram aprovados.

---

## 2. User Stories Implementadas

### US1 — Criação de Conta e Sessão

Permite que um usuário crie uma conta na rede social fornecendo login, senha e nome.

**Regras implementadas:**
- Login não pode ser nulo ou vazio → `LoginInvalidoException`
- Senha não pode ser nula ou vazia → `SenhaInvalidaException`
- Não são permitidos dois usuários com o mesmo login → `ContaJaExisteException`
- Dois usuários podem ter o mesmo nome se os logins forem diferentes
- Nome vazio é permitido
- Abertura de sessão valida login e senha → `LoginOuSenhaInvalidosException` (mensagem genérica por segurança)
- ID de sessão gerado como **UUID opaco**, não revelando o login do usuário

**Suítes de teste:**

| Arquivo | Testes | Resultado |
|---|---|---|
| `us1_1.txt` | 17 | ✅ OK |
| `us1_2.txt` | 7 | ✅ OK (persistência) |

---

### US2 — Perfil de Usuário

Permite a um usuário cadastrado criar e editar atributos do seu perfil.

**Regras implementadas:**
- Usuário pode preencher qualquer atributo com qualquer nome (mapa dinâmico chave-valor)
- Atributo não preenchido lança `AtributoNaoPreenchidoException`
- Atributo `senha` é protegido: não acessível via `getAtributoUsuario` por razões de segurança
- Edição de perfil requer sessão válida → `UsuarioNaoCadastradoException`
- Consulta de atributo requer usuário existente → `UsuarioNaoCadastradoException`
- Atributo `nome` é armazenado no perfil junto com os demais atributos

**Suítes de teste:**

| Arquivo | Testes | Resultado |
|---|---|---|
| `us2_1.txt` | 36 | ✅ OK |
| `us2_2.txt` | 13 | ✅ OK (persistência) |

---

### US3 — Adição de Amigos

Permite que usuários cadastrados se adicionem como amigos. O relacionamento só é efetivado quando ambos se adicionam mutuamente.

**Regras implementadas:**
- Convite enviado fica pendente até o alvo retribuir
- Ao retribuir, a amizade é efetivada bilateralmente
- Auto-amizade proibida → `AutoAmizadeException`
- Amigo já confirmado não pode ser re-adicionado → `UsuarioJaAmigoException`
- Convite duplicado (pendente) não pode ser re-enviado → `ConvitePendenteException`
- Alvo inexistente → `UsuarioNaoCadastradoException`
- Sessão inválida → `UsuarioNaoCadastradoException`
- Lista de amigos retornada no formato `{login1,login2,...}` ou `{}`
- Verificação de auto-amizade ocorre **antes** da busca do alvo no sistema (ordem semântica correta)

**Suítes de teste:**

| Arquivo | Testes | Resultado |
|---|---|---|
| `us3_1.txt` | 46 | ✅ OK |
| `us3_2.txt` | 10 | ✅ OK (persistência) |

---

### US4 — Envio e Leitura de Recados

Permite que usuários troquem recados entre si. Os recados são armazenados em fila FIFO.

**Regras implementadas:**
- Recados podem ser enviados para qualquer usuário cadastrado
- Auto-recado proibido → `AutoRecadoException`
- Destinatário inexistente → `UsuarioNaoCadastradoException`
- Sessão inválida → `UsuarioNaoCadastradoException`
- Leitura consome o recado (remove da fila)
- Fila vazia ao tentar ler → `SemRecadosException`
- Ordem dos recados preservada (FIFO) mesmo com múltiplos remetentes

**Suítes de teste:**

| Arquivo | Testes | Resultado |
|---|---|---|
| `us4_1.txt` | 42 | ✅ OK |
| `us4_2.txt` | 13 | ✅ OK (persistência) |

---

## 3. Resultado Global dos Testes

| Suíte | Testes | Status |
|---|---|---|
| `us1_1.txt` | 17 | ✅ |
| `us1_2.txt` | 7 | ✅ |
| `us2_1.txt` | 36 | ✅ |
| `us2_2.txt` | 13 | ✅ |
| `us3_1.txt` | 46 | ✅ |
| `us3_2.txt` | 10 | ✅ |
| `us4_1.txt` | 42 | ✅ |
| `us4_2.txt` | 13 | ✅ |
| **Total** | **184** | **✅ 100%** |

---

## 4. Arquitetura do Sistema

O projeto adota o padrão **Facade** exigido pelo EasyAccept, com separação clara entre camadas:

```
EasyAccept (testes)
     │
     ▼
 Facade.java          ← ponto de entrada único para o EasyAccept
     │
     ▼
 Sistema.java         ← lógica de negócio, gerenciamento de estado e persistência
     │
     ├── Map<String, Usuario>   usuarios  (persistido via serialização)
     └── Map<String, String>    sessoes   (transiente — não persistido)
         │
         ▼
     Usuario.java     ← entidade: perfil, amigos, convites, recados
         │
         ├── Map<String, String>  perfil           (atributos dinâmicos)
         ├── List<String>         amigos
         ├── List<String>         convitesEnviados
         ├── List<String>         convitesRecebidos
         └── Queue<String>        recados          (FIFO via LinkedList)
```

### Exceções de domínio

Todas as exceções são checked e estendem `Exception` com mensagem fixa (conforme exigência do EasyAccept):

| Exceção | Mensagem |
|---|---|
| `LoginInvalidoException` | `Login inválido.` |
| `SenhaInvalidaException` | `Senha inválida.` |
| `ContaJaExisteException` | `Conta com esse nome já existe.` |
| `LoginOuSenhaInvalidosException` | `Login ou senha inválidos.` |
| `UsuarioNaoCadastradoException` | `Usuário não cadastrado.` |
| `AtributoNaoPreenchidoException` | `Atributo não preenchido.` |
| `AutoAmizadeException` | `Usuário não pode adicionar a si mesmo como amigo.` |
| `UsuarioJaAmigoException` | `Usuário já está adicionado como amigo.` |
| `ConvitePendenteException` | `Usuário já está adicionado como amigo, esperando aceitação do convite.` |
| `AutoRecadoException` | `Usuário não pode enviar recado para si mesmo.` |
| `SemRecadosException` | `Não há recados.` |

---

## 5. Persistência

O sistema utiliza **serialização Java** (`ObjectOutputStream` / `ObjectInputStream`) para persistir o estado entre execuções:

- O objeto `Sistema` completo é serializado no arquivo `dados.dat`
- O mapa `sessoes` é marcado como `transient` — não é persistido (cada execução inicia sem sessões ativas)
- O carregamento trata falhas de I/O retornando um `Sistema` vazio (fail-safe)
- `zerarSistema()` apaga o arquivo de dados e limpa memória

---

## 6. Decisões de Design Notáveis

| Decisão | Justificativa |
|---|---|
| ID de sessão como UUID | Evita colisão entre sessões e não expõe o login do usuário |
| `LinkedHashMap` para usuários | Preserva ordem de inserção |
| `LinkedList` para recados | Implementa `Queue` com operações O(1) nas pontas (FIFO) |
| Atributos do perfil como `HashMap` | Permite atributos dinâmicos sem schema fixo |
| `Collections.unmodifiableList` nos getters | Encapsulamento: listas internas não mutáveis externamente |
| Senha protegida em `getAtributoUsuario` | Segurança: senha não acessível via leitura de perfil |

---

## 7. Como Executar

### Compilação

```powershell
# A partir da raiz do projeto (P2-2023.1-JACKUT/)
javac -cp "lib/easyaccept.jar;src" -d out (Get-ChildItem -Recurse src -Filter "*.java" | ForEach-Object { $_.FullName })
```

### Execução dos Testes

```powershell
# A partir da pasta src/
cd src
java -cp ".;../out;../lib/easyaccept.jar" Main
```

### Resultado Esperado

```
Test file tests/us1_1.txt: 17 tests OK
Test file tests/us1_2.txt: 7 tests OK
Test file tests/us2_1.txt: 36 tests OK
Test file tests/us2_2.txt: 13 tests OK
Test file tests/us3_1.txt: 46 tests OK
Test file tests/us3_2.txt: 10 tests OK
Test file tests/us4_1.txt: 42 tests OK
Test file tests/us4_2.txt: 13 tests OK
```

---

## 8. Estrutura de Arquivos

```
P2-2023.1-JACKUT/
├── lib/
│   └── easyaccept.jar
├── src/
│   ├── Main.java
│   ├── tests/
│   │   ├── us1_1.txt, us1_2.txt
│   │   ├── us2_1.txt, us2_2.txt
│   │   ├── us3_1.txt, us3_2.txt
│   │   └── us4_1.txt, us4_2.txt
│   └── br/ufal/ic/p2/jackut/
│       ├── Facade.java
│       ├── exceptions/
│       │   ├── AtributoNaoPreenchidoException.java
│       │   ├── AutoAmizadeException.java
│       │   ├── AutoRecadoException.java
│       │   ├── ContaJaExisteException.java
│       │   ├── ConvitePendenteException.java
│       │   ├── LoginInvalidoException.java
│       │   ├── LoginOuSenhaInvalidosException.java
│       │   ├── SemRecadosException.java
│       │   ├── SenhaInvalidaException.java
│       │   ├── UsuarioJaAmigoException.java
│       │   └── UsuarioNaoCadastradoException.java
│       └── models/
│           ├── Sistema.java
│           └── Usuario.java
├── .gitignore
├── MILESTONE_1.md
└── P2-2023.1-JACKUT.iml
```
