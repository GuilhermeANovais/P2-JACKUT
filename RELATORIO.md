# Relatório — Milestone 1 e Milestone 2
## Projeto JACKUT · P2 · 2023.1

---

## 1. Visão Geral

O **JACKUT** é um sistema de rede social desenvolvido em Java como projeto de Programação 2 (P2). 

- **Milestone 1**: Implementação inicial cobrindo US1 a US4 (criação de conta, perfil, amizade e envio de recados).
- **Milestone 2**: Evolução funcional cobrindo US5 a US9 (comunidades, mensagens de grupos, relacionamentos estendidos como paqueras/ídolos/inimigos e encerramento de conta). Houve uma **refatoração arquitetural profunda** para remover o débito técnico (God Object) deixado no M1, dividindo a lógica em Serviços.

A validação do projeto é realizada via **EasyAccept**. Todos os **466 testes** das 18 suítes foram aprovados (**100% de sucesso**).

---

## 2. Nova Arquitetura do Sistema (Refatoração M2)

No Milestone 2, a arquitetura foi completamente refatorada. O antigo `Sistema.java` que acumulava todas as responsabilidades (God Object) foi desmembrado num design **orientado a serviços (Service Layer)** com clara separação de camadas:

```text
EasyAccept (testes)
     │
     ▼
 Facade.java              ← Ponto de entrada. Coordena serviços, resolve IDs de sessão em logins e formata saída.
     │
     ├── SessaoService          ← Gere aberturas e encerramento de sessões
     ├── UsuarioService         ← Gere CRUD e perfil de usuários
     ├── AmizadeService         ← Gere lógica de convites e amigos
     ├── RecadoService          ← Gere filas de recados privados
     ├── ComunidadeService      ← Gere criação, membros e mensagens de comunidades
     └── RelacionamentoService  ← Gere ídolos, paqueras (com gatilhos de recado) e bloqueios por inimizade
     │
     ▼
Camada de Domínio (Models) ← Entidades: Usuario, Comunidade, Recado
     │
     ▼
PersistenceManager        ← Camada de I/O. Isola a leitura/gravação em arquivo (SistemaDados DTO)
```

**Benefícios da Nova Arquitetura:**
- **Coesão e Baixo Acoplamento:** Cada serviço tem uma única responsabilidade.
- **Isolamento da `Facade`:** A `Facade` deixou de ter lógica de negócio e foca-se apenas na formatação (ex: `"{login1,login2}"`) e conversão de Sessão `->` Login.
- **Injeção de Dependência Simples:** A `Facade` inicializa os serviços passando o mapa de dados partilhados por referência.

---

## 3. User Stories Implementadas

### M1: US1 a US4 (Resumo)
- **US1 — Conta e Sessão**: Cadastro com validações únicas; sessões usam UUIDs opacos.
- **US2 — Perfil**: Atributos flexíveis geridos via HashMap; proteções de segurança (senha inacessível).
- **US3 — Amizades**: Fluxo bidirecional (convite pendente -> aceitação).
- **US4 — Recados Privados**: Filas FIFO implementadas com suporte a leitura consumível.

### M2: US5 — Comunidades (Criação)
Permite a criação e consulta de comunidades.
- **Regras:** Nome de comunidade é único (`ComunidadeJaExisteException`). O criador torna-se dono e membro automaticamente.

### M2: US6 — Adição a Comunidades
Permite entrar em comunidades existentes e listar participações.
- **Regras:** Proibido adicionar membros que já pertençam à comunidade (`UsuarioJaMembroException`).

### M2: US7 — Mensagens de Comunidades
Permite enviar mensagens a todos os membros de uma comunidade.
- **Regras:** Semelhante a recados (FIFO), mas consumida através de um método próprio `lerMensagem`. 

### M2: US8 — Paqueras, Ídolos e Inimigos
Expande o sistema de relacionamento.
- **Fã/Ídolo**: Relação unidirecional. Não requer aceitação.
- **Paquera**: Relação inicialmente secreta. Se mútua (ambos se adicionarem), o sistema **dispara um recado automático** alertando o *match*.
- **Inimizade (Bloqueio)**: Se o usuário A adicionar B como inimigo, B fica bloqueado de enviar recados, adicionar como amigo/ídolo/paquera ao usuário A. (Levanta `FuncaoInvalidaException`).

### M2: US9 — Encerramento de Conta
Permite apagar completamente a conta de um usuário.
- **Regras de Cascata:**
  - O perfil e sessão do usuário são apagados.
  - O usuário é removido de todas as comunidades que participa.
  - Todas as **comunidades das quais o usuário é dono são apagadas** (e seus membros perdem o acesso).
  - Todas as referências de amizade do usuário nas contas dos outros são apagadas.
  - **Recados enviados pelo usuário apagado** (que ainda não foram lidos) **somem das caixas de entrada** dos destinatários. (Exigiu a criação do objeto `Recado.java` para rastrear remetentes).

---

## 4. Persistência de Dados

O estado da aplicação é serializado na classe `SistemaDados` (DTO).
- O arquivo `dados.dat` guarda os mapas de **Usuários** e **Comunidades**.
- Recados e mensagens são persistidos automaticamente através da árvore de serialização da entidade `Usuario`.
- Variáveis transientes (ex: mapa de Sessões na Facade) são descartadas ao encerrar, forçando o utilizador a logar novamente noutra execução.

---

## 5. Resultado Global dos Testes

O projeto passou **integralmente em todos os testes** (100% verde).

| US | Arquivo | Testes OK |
|---|---|---|
| **US1** | `us1_1.txt`, `us1_2.txt` | 24 |
| **US2** | `us2_1.txt`, `us2_2.txt` | 49 |
| **US3** | `us3_1.txt`, `us3_2.txt` | 56 |
| **US4** | `us4_1.txt`, `us4_2.txt` | 55 |
| **US5** | `us5_1.txt`, `us5_2.txt` | 44 |
| **US6** | `us6_1.txt`, `us6_2.txt` | 35 |
| **US7** | `us7_1.txt`, `us7_2.txt` | 84 |
| **US8** | `us8_1.txt`, `us8_2.txt` | 102 |
| **US9** | `us9_1.txt`, `us9_2.txt` | 24 |
| **Total** | | **466 ✅** |

---

## 6. Como Compilar e Executar

Graças à nova infraestrutura simples, você pode compilar ou correr os testes inteiramente usando um script rápido de linha de comando.

**Passo 1: Entrar na pasta `src`**
Abra o seu terminal na pasta raiz do projeto e entre em `src`:
```powershell
cd src
```

**Passo 2: Compilar**
```powershell
javac -cp "..\lib\easyaccept.jar" -d "..\bin" (Get-ChildItem -Recurse *.java)
```

**Passo 3: Executar a suite completa**
```powershell
java -cp "..\bin;..\lib\easyaccept.jar" Main
```

*(Obs: para Mac/Linux, usar `:` em vez de `;` no classpath e `/` nos diretórios).*
