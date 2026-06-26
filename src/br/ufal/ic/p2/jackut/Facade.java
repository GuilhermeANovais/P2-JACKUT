package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Comunidade;
import br.ufal.ic.p2.jackut.models.Usuario;
import br.ufal.ic.p2.jackut.persistence.PersistenceManager;
import br.ufal.ic.p2.jackut.persistence.SistemaDados;
import br.ufal.ic.p2.jackut.services.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fachada (Facade) do sistema Jackut — Milestone 2.
 *
 * <p>Ponto de entrada único para o EasyAccept. Coordena os serviços de domínio,
 * resolve sessões para logins e aplica a formatação de saída ({lista}).</p>
 *
 * <p>Responsabilidades desta classe:</p>
 * <ul>
 *   <li>Inicializar e conectar todos os serviços</li>
 *   <li>Resolver IDs de sessão em logins antes de chamar os serviços</li>
 *   <li>Formatar saídas no padrão EasyAccept: {@code {login1,login2}}</li>
 *   <li>Delegar 100% da lógica de negócio aos serviços</li>
 * </ul>
 */
public class Facade {

    private static final String ARQUIVO_DADOS = "dados.dat";

    private final PersistenceManager persistencia;
    private SistemaDados dados;

    // Serviços de domínio
    private SessaoService sessaoService;
    private UsuarioService usuarioService;
    private AmizadeService amizadeService;
    private RecadoService recadoService;
    private ComunidadeService comunidadeService;
    private RelacionamentoService relacionamentoService;

    /**
     * Constrói a Facade, carregando o estado persistido.
     */
    public Facade() {
        this.persistencia = new PersistenceManager(ARQUIVO_DADOS);
        this.dados = persistencia.carregar();
        inicializarServicos();
    }

    /**
     * Instancia e conecta todos os serviços a partir do estado carregado.
     */
    private void inicializarServicos() {
        Map<String, Usuario> usuarios = dados.getUsuarios();
        Map<String, Comunidade> comunidades = dados.getComunidades();

        this.sessaoService      = new SessaoService(usuarios);
        this.usuarioService     = new UsuarioService(usuarios);
        this.amizadeService     = new AmizadeService(usuarios);
        this.recadoService      = new RecadoService(usuarios);
        this.comunidadeService  = new ComunidadeService(usuarios, comunidades);
        this.relacionamentoService = new RelacionamentoService(usuarios, recadoService);
    }

    // =========================================================================
    // Controle do sistema
    // =========================================================================

    /**
     * Apaga todos os dados e reinicia o sistema em estado vazio.
     */
    public void zerarSistema() {
        persistencia.apagar();
        this.dados = new SistemaDados();
        inicializarServicos();
    }

    /**
     * Persiste o estado atual e encerra o sistema.
     */
    public void encerrarSistema() {
        persistencia.salvar(dados);
    }

    // =========================================================================
    // US1 — Criação de conta e sessão
    // =========================================================================

    /**
     * Cria um usuário com os dados fornecidos.
     *
     * @param login login único
     * @param senha senha de acesso
     * @param nome  nome de exibição
     * @throws LoginInvalidoException se login vazio/nulo
     * @throws SenhaInvalidaException se senha vazia/nula
     * @throws ContaJaExisteException se login já em uso
     */
    public void criarUsuario(String login, String senha, String nome)
            throws LoginInvalidoException, SenhaInvalidaException, ContaJaExisteException {
        usuarioService.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para o usuário com login e senha fornecidos.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return ID único da sessão
     * @throws LoginOuSenhaInvalidosException se credenciais inválidas
     */
    public String abrirSessao(String login, String senha)
            throws LoginOuSenhaInvalidosException {
        return sessaoService.abrirSessao(login, senha);
    }

    // =========================================================================
    // US2 — Perfil
    // =========================================================================

    /**
     * Retorna o valor de um atributo do perfil do usuário.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException  se usuário não existir
     * @throws AtributoNaoPreenchidoException se atributo não preenchido
     */
    public String getAtributoUsuario(String login, String atributo)
            throws UsuarioNaoCadastradoException, AtributoNaoPreenchidoException {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    /**
     * Modifica o valor de um atributo do perfil do usuário autenticado pela sessão.
     *
     * @param id       ID de sessão ativa
     * @param atributo nome do atributo
     * @param valor    novo valor
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    public void editarPerfil(String id, String atributo, String valor)
            throws UsuarioNaoCadastradoException {
        String login = resolverLogin(id);
        usuarioService.editarPerfil(login, atributo, valor);
    }

    // =========================================================================
    // US3 — Amizades
    // =========================================================================

    /**
     * Adiciona amigo ao usuário autenticado pela sessão.
     *
     * @param id    ID de sessão ativa
     * @param amigo login do usuário a adicionar como amigo
     * @throws UsuarioNaoCadastradoException se sessão inválida ou amigo inexistente
     * @throws AutoAmizadeException          se tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se já forem amigos
     * @throws ConvitePendenteException      se já houver convite pendente
     * @throws FuncaoInvalidaException       se o alvo declarou o remetente como inimigo
     */
    public void adicionarAmigo(String id, String amigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException, FuncaoInvalidaException {
        String login = resolverLogin(id);
        // Verificar inimizade: o alvo pode ter declarado o remetente como inimigo
        if (!login.equals(amigo)) {
            relacionamentoService.verificarInimizade(login, amigo);
        }
        amizadeService.adicionarAmigo(login, amigo);
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param login login do primeiro usuário
     * @param amigo login do segundo usuário
     * @return {@code true} se forem amigos
     * @throws UsuarioNaoCadastradoException se algum não existir
     */
    public boolean ehAmigo(String login, String amigo)
            throws UsuarioNaoCadastradoException {
        return amizadeService.ehAmigo(login, amigo);
    }

    /**
     * Retorna a lista de amigos do usuário no formato {@code {login1,login2}}.
     *
     * @param login login do usuário
     * @return string formatada
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public String getAmigos(String login) throws UsuarioNaoCadastradoException {
        return formatarLista(amizadeService.getAmigos(login));
    }

    // =========================================================================
    // US4 — Recados
    // =========================================================================

    /**
     * Envia um recado do usuário autenticado ao destinatário.
     *
     * @param id           ID de sessão ativa
     * @param destinatario login do destinatário
     * @param recado       texto do recado
     * @throws UsuarioNaoCadastradoException se sessão inválida ou destinatário inexistente
     * @throws AutoRecadoException           se tentar enviar para si mesmo
     * @throws FuncaoInvalidaException       se o destinatário declarou o remetente como inimigo
     */
    public void enviarRecado(String id, String destinatario, String recado)
            throws UsuarioNaoCadastradoException, AutoRecadoException, FuncaoInvalidaException {
        String login = resolverLogin(id);
        // Verificar inimizade: o destinatário pode ter declarado o remetente como inimigo
        if (!login.equals(destinatario)) {
            relacionamentoService.verificarInimizade(login, destinatario);
        }
        recadoService.enviarRecado(login, destinatario, recado);
    }

    /**
     * Lê o primeiro recado da fila do usuário autenticado.
     *
     * @param id ID de sessão ativa
     * @return texto do primeiro recado
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws SemRecadosException           se fila vazia
     */
    public String lerRecado(String id)
            throws UsuarioNaoCadastradoException, SemRecadosException {
        String login = resolverLogin(id);
        return recadoService.lerRecado(login);
    }

    // =========================================================================
    // US5 — Comunidades
    // =========================================================================

    /**
     * Cria uma comunidade. O criador torna-se o dono e primeiro membro.
     *
     * <p>Aceita parâmetro {@code sessao} (padrão US5/US6/US7) ou {@code id} (US9 override).</p>
     *
     * @param sessao    ID de sessão ativa
     * @param nome      nome único da comunidade
     * @param descricao descrição da comunidade
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws ComunidadeJaExisteException   se nome já em uso
     */
    public void criarComunidade(String sessao, String nome, String descricao)
            throws UsuarioNaoCadastradoException, ComunidadeJaExisteException {
        String login = resolverLogin(sessao);
        comunidadeService.criarComunidade(login, nome, descricao);
    }

    /**
     * Overload para compatibilidade com testes que usam parâmetro {@code id} (US9).
     *
     * @param id        ID de sessão ativa (parâmetro nomeado diferente)
     * @param nome      nome da comunidade
     * @param descricao descrição
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws ComunidadeJaExisteException   se nome já em uso
     */
    public void criarComunidade_id(String id, String nome, String descricao)
            throws UsuarioNaoCadastradoException, ComunidadeJaExisteException {
        criarComunidade(id, nome, descricao);
    }

    /**
     * Retorna a descrição de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return descrição
     * @throws ComunidadeNaoExisteException se não existir
     */
    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        return comunidadeService.getDescricaoComunidade(nome);
    }

    /**
     * Retorna o login do dono de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return login do dono
     * @throws ComunidadeNaoExisteException se não existir
     */
    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        return comunidadeService.getDonoComunidade(nome);
    }

    /**
     * Retorna os membros de uma comunidade no formato {@code {login1,login2}}.
     *
     * @param nome nome da comunidade
     * @return string formatada
     * @throws ComunidadeNaoExisteException se não existir
     */
    public String getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        return formatarLista(comunidadeService.getMembrosComunidade(nome));
    }

    // =========================================================================
    // US6 — Adição a comunidades
    // =========================================================================

    /**
     * Adiciona o usuário autenticado a uma comunidade existente.
     *
     * <p>Aceita parâmetro {@code sessao} (US6) ou {@code nome}. O US7 usa {@code comunidade}
     * em vez de {@code nome}: este overload unifica os dois casos.</p>
     *
     * @param sessao    ID de sessão ativa
     * @param nome      nome da comunidade
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws ComunidadeNaoExisteException  se comunidade não existir
     * @throws UsuarioJaMembroException      se já for membro
     */
    public void adicionarComunidade(String sessao, String nome)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException, UsuarioJaMembroException {
        String login = resolverLogin(sessao);
        comunidadeService.adicionarComunidade(login, nome);
    }

    /**
     * Overload para US7 que usa parâmetro {@code comunidade} em vez de {@code nome}.
     *
     * @param sessao     ID de sessão ativa
     * @param comunidade nome da comunidade
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws ComunidadeNaoExisteException  se comunidade não existir
     * @throws UsuarioJaMembroException      se já for membro
     */
    public void adicionarComunidade_comunidade(String sessao, String comunidade)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException, UsuarioJaMembroException {
        adicionarComunidade(sessao, comunidade);
    }

    /**
     * Retorna a lista de comunidades de um usuário, pelo login (acesso público).
     *
     * @param login login do usuário
     * @return string formatada no padrão EasyAccept: {@code "{Comunidade A,Comunidade B}"}
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public String getComunidades(String login) throws UsuarioNaoCadastradoException {
        List<String> coms = comunidadeService.getComunidades(login);
        return formatarListaString(coms);
    }

    /**
     * Overload para US6: acesso por sessão em vez de login.
     * Usado no caso de erro de sessão inválida.
     *
     * @param sessao ID de sessão
     * @return string formatada
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    public String getComunidades_sessao(String sessao) throws UsuarioNaoCadastradoException {
        String login = resolverLogin(sessao);
        return getComunidades(login);
    }

    // =========================================================================
    // US7 — Mensagens de comunidade
    // =========================================================================

    /**
     * Envia uma mensagem a uma comunidade. Todos os membros a recebem.
     *
     * @param id         ID de sessão ativa do remetente
     * @param comunidade nome da comunidade destino
     * @param mensagem   texto da mensagem
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws ComunidadeNaoExisteException  se comunidade não existir
     */
    public void enviarMensagem(String id, String comunidade, String mensagem)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException {
        String login = resolverLogin(id);
        comunidadeService.enviarMensagem(login, comunidade, mensagem);
    }

    /**
     * Lê a primeira mensagem da fila de mensagens do usuário autenticado.
     *
     * @param id ID de sessão ativa
     * @return texto da primeira mensagem
     * @throws UsuarioNaoCadastradoException se sessão inválida
     * @throws SemMensagensException         se fila vazia
     */
    public String lerMensagem(String id)
            throws UsuarioNaoCadastradoException, SemMensagensException {
        String login = resolverLogin(id);
        return comunidadeService.lerMensagem(login);
    }

    // =========================================================================
    // US8 — Novos relacionamentos
    // =========================================================================

    /**
     * Adiciona um ídolo ao usuário autenticado.
     *
     * @param id    ID de sessão ativa
     * @param idolo login do ídolo
     * @throws UsuarioNaoCadastradoException se sessão inválida ou ídolo não existir
     * @throws AutoIdoloException            se tentar adicionar a si mesmo
     * @throws IdoloJaAdicionadoException    se ídolo já adicionado
     * @throws FuncaoInvalidaException       se o ídolo declarou o fã como inimigo
     */
    public void adicionarIdolo(String id, String idolo)
            throws UsuarioNaoCadastradoException, AutoIdoloException,
                   IdoloJaAdicionadoException, FuncaoInvalidaException {
        String login = resolverLogin(id);
        if (!login.equals(idolo)) {
            relacionamentoService.verificarInimizade(login, idolo);
        }
        relacionamentoService.adicionarIdolo(login, idolo);
    }

    /**
     * Verifica se {@code login} é fã de {@code idolo}.
     *
     * @param login login do usuário
     * @param idolo login do possível ídolo
     * @return {@code true} se for fã
     * @throws UsuarioNaoCadastradoException se algum não existir
     */
    public boolean ehFa(String login, String idolo) throws UsuarioNaoCadastradoException {
        return relacionamentoService.ehFa(login, idolo);
    }

    /**
     * Retorna os fãs de um usuário no formato {@code {login1,login2}}.
     *
     * @param login login do ídolo
     * @return string formatada
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public String getFas(String login) throws UsuarioNaoCadastradoException {
        return formatarLista(relacionamentoService.getFas(login));
    }

    /**
     * Adiciona uma paquera ao usuário autenticado.
     *
     * @param id      ID de sessão ativa
     * @param paquera login do paquera
     * @throws UsuarioNaoCadastradoException  se sessão inválida ou paquera não existir
     * @throws AutoPaqueraException           se tentar adicionar a si mesmo
     * @throws PaqueraJaAdicionadaException   se já adicionado
     * @throws FuncaoInvalidaException        se o paquera declarou o usuário como inimigo
     */
    public void adicionarPaquera(String id, String paquera)
            throws UsuarioNaoCadastradoException, AutoPaqueraException,
                   PaqueraJaAdicionadaException, FuncaoInvalidaException {
        String login = resolverLogin(id);
        if (!login.equals(paquera)) {
            relacionamentoService.verificarInimizade(login, paquera);
        }
        relacionamentoService.adicionarPaquera(login, paquera);
    }

    /**
     * Verifica se {@code paquera} é paquera do usuário autenticado.
     *
     * @param id      ID de sessão ativa
     * @param paquera login a verificar
     * @return {@code true} se for paquera
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    public boolean ehPaquera(String id, String paquera) throws UsuarioNaoCadastradoException {
        String login = resolverLogin(id);
        return relacionamentoService.ehPaquera(login, paquera);
    }

    /**
     * Retorna as paqueras do usuário autenticado no formato {@code {login1,login2}}.
     *
     * @param id ID de sessão ativa
     * @return string formatada
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    public String getPaqueras(String id) throws UsuarioNaoCadastradoException {
        String login = resolverLogin(id);
        return formatarLista(relacionamentoService.getPaqueras(login));
    }

    /**
     * Adiciona um inimigo ao usuário autenticado.
     *
     * @param id      ID de sessão ativa
     * @param inimigo login do inimigo
     * @throws UsuarioNaoCadastradoException  se sessão inválida ou inimigo não existir
     * @throws AutoInimizadeException         se tentar adicionar a si mesmo
     * @throws InimigoJaAdicionadoException   se já adicionado
     */
    public void adicionarInimigo(String id, String inimigo)
            throws UsuarioNaoCadastradoException, AutoInimizadeException, InimigoJaAdicionadoException {
        String login = resolverLogin(id);
        relacionamentoService.adicionarInimigo(login, inimigo);
    }

    // =========================================================================
    // US9 — Remoção de conta
    // =========================================================================

    /**
     * Remove a conta do usuário autenticado, apagando todas as suas informações
     * do sistema (perfil, relacionamentos, comunidades, mensagens enviadas).
     *
     * @param id ID de sessão ativa
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    public void removerUsuario(String id) throws UsuarioNaoCadastradoException {
        String login = resolverLogin(id);
        // 1. Remover comunidades das quais é dono (e limpar membros)
        comunidadeService.removerComunidadesDoDono(login);
        // 2. Remover o usuário como membro das comunidades que não é dono
        comunidadeService.removerMembroDeComunidades(login);
        // 3. Limpar referências de amizade de outros usuários
        amizadeService.removerReferenciasDe(login);
        // 4. Remover recados enviados por este usuário da caixa dos destinatários
        recadoService.removerRecadosDe(login);
        // 5. Remover o usuário do sistema
        usuarioService.removerUsuario(login);
        // 6. Invalidar a sessão
        sessaoService.invalidarSessao(id);
    }

    // =========================================================================
    // Formatação de saída (responsabilidade da Facade)
    // =========================================================================

    /**
     * Formata uma lista de logins no padrão EasyAccept: {@code {login1,login2}}.
     *
     * @param lista lista de strings (logins)
     * @return string formatada
     */
    private String formatarLista(List<String> lista) {
        return "{" + String.join(",", lista) + "}";
    }

    /**
     * Formata uma lista de strings (nomes com espaços) no padrão EasyAccept com aspas implícitas.
     * Para comunidades: {@code {Comunidade A,Comunidade B}}.
     *
     * @param lista lista de strings
     * @return string formatada
     */
    private String formatarListaString(List<String> lista) {
        return "{" + String.join(",", lista) + "}";
    }

    // =========================================================================
    // Resolução de sessão
    // =========================================================================

    /**
     * Resolve o login a partir de um ID de sessão.
     *
     * @param idSessao ID de sessão
     * @return login do usuário
     * @throws UsuarioNaoCadastradoException se sessão inválida
     */
    private String resolverLogin(String idSessao) throws UsuarioNaoCadastradoException {
        return sessaoService.resolverLogin(idSessao);
    }
}
