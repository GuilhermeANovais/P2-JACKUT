package br.ufal.ic.p2.jackut.models;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gerenciador central do sistema Jackut.
 *
 * <p>Responsável por manter o estado completo da aplicação: usuários cadastrados
 * e sessões ativas. Implementa todas as regras de negócio descritas nas User Stories
 * 1 a 4 e fornece persistência via serialização Java.</p>
 *
 * <p>Sessões são transientes e não são persistidas — cada execução inicia sem sessões ativas.</p>
 */
public class Sistema implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Caminho do arquivo de persistência, relativo ao diretório de trabalho. */
    private static final String ARQUIVO_DADOS = "dados.dat";

    /**
     * Mapa de usuários cadastrados, indexado pelo login.
     * Usa LinkedHashMap para preservar a ordem de inserção.
     */
    private Map<String, Usuario> usuarios;

    /**
     * Mapa de sessões ativas, indexado pelo ID de sessão.
     * Transiente — não é serializado.
     */
    private transient Map<String, String> sessoes;

    /**
     * Constrói um novo Sistema com estado vazio.
     */
    public Sistema() {
        this.usuarios = new LinkedHashMap<>();
        this.sessoes = new LinkedHashMap<>();
    }

    // -------------------------------------------------------------------------
    // Gerenciamento de sessões (auxiliar)
    // -------------------------------------------------------------------------

    /**
     * Inicializa o mapa de sessões transiente após desserialização.
     * Chamado pela Facade após carregar o sistema do arquivo.
     */
    private void inicializarSessoes() {
        if (this.sessoes == null) {
            this.sessoes = new LinkedHashMap<>();
        }
    }

    /**
     * Resolve o login do usuário a partir de um ID de sessão.
     *
     * @param idSessao identificador da sessão
     * @return login do usuário da sessão
     * @throws UsuarioNaoCadastradoException se o ID de sessão for inválido ou nulo
     */
    private String resolverLoginPorSessao(String idSessao) throws UsuarioNaoCadastradoException {
        if (idSessao == null || !sessoes.containsKey(idSessao)) {
            throw new UsuarioNaoCadastradoException();
        }
        return sessoes.get(idSessao);
    }

    /**
     * Busca um usuário pelo login, lançando exceção se não encontrado.
     *
     * @param login login do usuário
     * @return instância de {@link Usuario}
     * @throws UsuarioNaoCadastradoException se o login não existir no sistema
     */
    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }

    // -------------------------------------------------------------------------
    // US1 — Criação de conta e sessão
    // -------------------------------------------------------------------------

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login login único do usuário
     * @param senha senha de acesso
     * @param nome  nome de exibição
     * @throws LoginInvalidoException    se o login for nulo ou vazio
     * @throws SenhaInvalidaException    se a senha for nula ou vazia
     * @throws ContaJaExisteException    se o login já estiver em uso
     */
    public void criarUsuario(String login, String senha, String nome)
            throws LoginInvalidoException, SenhaInvalidaException, ContaJaExisteException {

        if (login == null || login.isEmpty()) {
            throw new LoginInvalidoException();
        }
        if (senha == null || senha.isEmpty()) {
            throw new SenhaInvalidaException();
        }
        if (usuarios.containsKey(login)) {
            throw new ContaJaExisteException();
        }

        usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Abre uma sessão para o usuário com o login e senha fornecidos.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return identificador único da sessão aberta
     * @throws LoginOuSenhaInvalidosException se o login não existir, ou a senha estiver
     *                                        errada, vazia ou nula
     */
    public String abrirSessao(String login, String senha)
            throws LoginOuSenhaInvalidosException {

        if (login == null || login.isEmpty() || senha == null || senha.isEmpty()) {
            throw new LoginOuSenhaInvalidosException();
        }

        Usuario usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new LoginOuSenhaInvalidosException();
        }

        // ID de sessão: UUID único e opaco, independente do login
        String idSessao = UUID.randomUUID().toString();
        sessoes.put(idSessao, login);
        return idSessao;
    }

    // -------------------------------------------------------------------------
    // US2 — Perfil
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor de um atributo do perfil de um usuário.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException  se o usuário não existir
     * @throws AtributoNaoPreenchidoException se o atributo não foi preenchido
     */
    public String getAtributoUsuario(String login, String atributo)
            throws UsuarioNaoCadastradoException, AtributoNaoPreenchidoException {

        Usuario usuario = buscarUsuario(login);
        return usuario.getAtributo(atributo);
    }

    /**
     * Edita um atributo do perfil do usuário identificado pela sessão.
     *
     * @param idSessao identificador da sessão ativa
     * @param atributo nome do atributo a editar
     * @param valor    novo valor do atributo
     * @throws UsuarioNaoCadastradoException se a sessão for inválida
     */
    public void editarPerfil(String idSessao, String atributo, String valor)
            throws UsuarioNaoCadastradoException {

        String login = resolverLoginPorSessao(idSessao);
        Usuario usuario = buscarUsuario(login);
        // Senha é campo separado — não vai para o mapa de perfil
        if ("senha".equals(atributo)) {
            usuario.setSenha(valor);
        } else {
            usuario.setAtributo(atributo, valor);
        }
    }

    // -------------------------------------------------------------------------
    // US3 — Amizades
    // -------------------------------------------------------------------------

    /**
     * Adiciona um pedido de amizade do usuário da sessão para o login alvo.
     *
     * <p>O relacionamento só é efetivado quando o alvo também adicionar o remetente de volta.</p>
     *
     * @param idSessao identificador da sessão ativa
     * @param loginAmigo login do usuário a ser adicionado como amigo
     * @throws UsuarioNaoCadastradoException se a sessão for inválida ou o alvo não existir
     * @throws AutoAmizadeException          se o usuário tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se já forem amigos confirmados
     * @throws ConvitePendenteException      se já existir um convite pendente para o alvo
     */
    public void adicionarAmigo(String idSessao, String loginAmigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException {

        String loginRemetente = resolverLoginPorSessao(idSessao);

        // Auto-amizade verificada antes de buscar o alvo — semanticamente correto
        if (loginRemetente.equals(loginAmigo)) {
            throw new AutoAmizadeException();
        }

        Usuario remetente = buscarUsuario(loginRemetente);
        Usuario alvo = buscarUsuario(loginAmigo);

        if (remetente.ehAmigo(loginAmigo)) {
            throw new UsuarioJaAmigoException();
        }

        if (remetente.temConviteEnviadoPara(loginAmigo)) {
            throw new ConvitePendenteException();
        }

        // Se o alvo já enviou convite para o remetente, efetiva a amizade mutuamente
        if (alvo.temConviteEnviadoPara(loginRemetente)) {
            remetente.efetivarAmizade(loginAmigo);
            alvo.efetivarAmizade(loginRemetente);
        } else {
            // Caso contrário, registra o convite como pendente
            remetente.adicionarConviteEnviado(loginAmigo);
            alvo.adicionarConviteRecebido(loginRemetente);
        }
    }

    /**
     * Verifica se dois usuários são amigos confirmados.
     *
     * @param login  login do primeiro usuário
     * @param amigo  login do segundo usuário
     * @return {@code true} se forem amigos; {@code false} caso contrário
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     */
    public boolean ehAmigo(String login, String amigo) throws UsuarioNaoCadastradoException {
        Usuario usuario = buscarUsuario(login);
        buscarUsuario(amigo); // valida existência do segundo usuário
        return usuario.ehAmigo(amigo);
    }

    /**
     * Retorna a lista de amigos de um usuário no formato {@code {login1,login2,...}}.
     *
     * @param login login do usuário
     * @return string no formato {@code {login1,login2}} ou {@code {}} se sem amigos
     * @throws UsuarioNaoCadastradoException se o usuário não existir
     */
    public String getAmigos(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = buscarUsuario(login);
        List<String> amigos = usuario.getAmigos();

        if (amigos.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < amigos.size(); i++) {
            sb.append(amigos.get(i));
            if (i < amigos.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // US4 — Recados
    // -------------------------------------------------------------------------

    /**
     * Envia um recado do usuário da sessão para o destinatário.
     *
     * @param idSessao     identificador da sessão ativa
     * @param destinatario login do destinatário do recado
     * @param recado       texto do recado
     * @throws UsuarioNaoCadastradoException se a sessão for inválida ou o destinatário não existir
     * @throws AutoRecadoException           se o remetente tentar enviar recado para si mesmo
     */
    public void enviarRecado(String idSessao, String destinatario, String recado)
            throws UsuarioNaoCadastradoException, AutoRecadoException {

        String loginRemetente = resolverLoginPorSessao(idSessao);
        buscarUsuario(loginRemetente); // valida remetente

        if (loginRemetente.equals(destinatario)) {
            throw new AutoRecadoException();
        }

        Usuario usuarioDestinatario = buscarUsuario(destinatario);
        usuarioDestinatario.adicionarRecado(loginRemetente, recado);
    }

    /**
     * Lê e remove o primeiro recado da fila do usuário da sessão.
     *
     * @param idSessao identificador da sessão ativa
     * @return texto do primeiro recado na fila
     * @throws UsuarioNaoCadastradoException se a sessão for inválida
     * @throws SemRecadosException           se não houver recados na fila
     */
    public String lerRecado(String idSessao)
            throws UsuarioNaoCadastradoException, SemRecadosException {

        String login = resolverLoginPorSessao(idSessao);
        Usuario usuario = buscarUsuario(login);
        return usuario.lerRecado();
    }

    // -------------------------------------------------------------------------
    // Controle do sistema
    // -------------------------------------------------------------------------

    /**
     * Apaga todos os dados mantidos em memória e remove o arquivo de persistência.
     */
    public void zerarSistema() {
        usuarios.clear();
        if (sessoes != null) {
            sessoes.clear();
        }
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists() && !arquivo.delete()) {
            System.err.println("Aviso: não foi possível remover o arquivo de dados: "
                    + arquivo.getAbsolutePath());
        }
    }

    /**
     * Grava o estado atual do sistema no arquivo de persistência e encerra.
     *
     * @throws RuntimeException se ocorrer erro de I/O durante a gravação
     */
    public void encerrarSistema() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARQUIVO_DADOS))) {
            // Serializa o objeto inteiro (sessoes é transient, não será incluída)
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar dados do sistema: " + e.getMessage(), e);
        }
    }

    /**
     * Carrega o estado do sistema a partir do arquivo de persistência.
     * Se o arquivo não existir, o sistema permanece com estado vazio.
     *
     * @return instância de {@link Sistema} carregada, ou nova instância vazia se não houver arquivo
     */
    public static Sistema carregar() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) {
            return new Sistema();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ARQUIVO_DADOS))) {
            Sistema sistema = (Sistema) ois.readObject();
            sistema.inicializarSessoes();
            return sistema;
        } catch (IOException | ClassNotFoundException e) {
            // Se houver erro na leitura, inicia com estado limpo
            return new Sistema();
        }
    }
}
