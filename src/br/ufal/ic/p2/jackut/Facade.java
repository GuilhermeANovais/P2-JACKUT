package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Sistema;

/**
 * Fachada (Facade) do sistema Jackut.
 *
 * <p>Ponto de entrada único para o EasyAccept. Cada método público desta classe
 * corresponde a um comando da linguagem de script dos testes de aceitação.</p>
 *
 * <p>A Facade delega toda a lógica de negócio à classe {@link Sistema} e é responsável
 * por carregar o estado persistido ao ser instanciada e por expor os métodos
 * no formato esperado pela biblioteca EasyAccept.</p>
 */
public class Facade {

    /** Instância do sistema com toda a lógica de negócio e estado da aplicação. */
    private Sistema sistema;

    /**
     * Constrói a Facade, carregando o estado persistido anteriormente.
     * Se não houver arquivo de dados, inicia com estado vazio.
     */
    public Facade() {
        this.sistema = Sistema.carregar();
    }

    // -------------------------------------------------------------------------
    // Controle do sistema
    // -------------------------------------------------------------------------

    /**
     * Apaga todos os dados mantidos no sistema, incluindo o arquivo de persistência.
     */
    public void zerarSistema() {
        sistema.zerarSistema();
    }

    /**
     * Grava o cadastro em arquivo e encerra o programa.
     * Atingir o final de um script é equivalente a encontrar este comando.
     */
    public void encerrarSistema() {
        sistema.encerrarSistema();
    }

    // -------------------------------------------------------------------------
    // US1 — Criação de conta
    // -------------------------------------------------------------------------

    /**
     * Cria um usuário com os dados da conta fornecidos.
     *
     * @param login login único do usuário
     * @param senha senha de acesso
     * @param nome  nome de exibição na rede
     * @throws LoginInvalidoException se o login for vazio ou nulo
     * @throws SenhaInvalidaException se a senha for vazia ou nula
     * @throws ContaJaExisteException se já existir uma conta com esse login
     */
    public void criarUsuario(String login, String senha, String nome)
            throws LoginInvalidoException, SenhaInvalidaException, ContaJaExisteException {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para um usuário com o login e a senha fornecidos.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return identificador único da sessão aberta
     * @throws LoginOuSenhaInvalidosException se o login ou a senha forem inválidos
     */
    public String abrirSessao(String login, String senha)
            throws LoginOuSenhaInvalidosException {
        return sistema.abrirSessao(login, senha);
    }

    // -------------------------------------------------------------------------
    // US2 — Perfil
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor do atributo de um usuário, armazenado em seu perfil.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException  se o usuário não existir
     * @throws AtributoNaoPreenchidoException se o atributo não foi preenchido
     */
    public String getAtributoUsuario(String login, String atributo)
            throws UsuarioNaoCadastradoException, AtributoNaoPreenchidoException {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Modifica o valor de um atributo do perfil de um usuário.
     * Uma sessão válida (identificada por id) deve estar aberta.
     *
     * @param id       identificador da sessão ativa
     * @param atributo nome do atributo a modificar
     * @param valor    novo valor do atributo
     * @throws UsuarioNaoCadastradoException se a sessão for inválida
     */
    public void editarPerfil(String id, String atributo, String valor)
            throws UsuarioNaoCadastradoException {
        sistema.editarPerfil(id, atributo, valor);
    }

    // -------------------------------------------------------------------------
    // US3 — Amizades
    // -------------------------------------------------------------------------

    /**
     * Adiciona um amigo ao usuário aberto na sessão especificada.
     * O relacionamento só é efetivado quando o outro usuário o adicionar de volta.
     *
     * @param id    identificador da sessão ativa
     * @param amigo login do usuário a ser adicionado como amigo
     * @throws UsuarioNaoCadastradoException se a sessão for inválida ou o amigo não existir
     * @throws AutoAmizadeException          se o usuário tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se já forem amigos
     * @throws ConvitePendenteException      se já houver convite pendente para esse usuário
     */
    public void adicionarAmigo(String id, String amigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException {
        sistema.adicionarAmigo(id, amigo);
    }

    /**
     * Retorna true se os dois usuários são amigos.
     *
     * @param login login do primeiro usuário
     * @param amigo login do segundo usuário
     * @return {@code true} se forem amigos confirmados; {@code false} caso contrário
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     */
    public boolean ehAmigo(String login, String amigo)
            throws UsuarioNaoCadastradoException {
        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Retorna a lista de amigos do usuário especificado, no formato {@code {login1,login2}}.
     *
     * @param login login do usuário
     * @return string formatada com os logins dos amigos
     * @throws UsuarioNaoCadastradoException se o usuário não existir
     */
    public String getAmigos(String login) throws UsuarioNaoCadastradoException {
        return sistema.getAmigos(login);
    }

    // -------------------------------------------------------------------------
    // US4 — Recados
    // -------------------------------------------------------------------------

    /**
     * Envia o recado especificado ao destinatário especificado.
     * Uma sessão válida deve estar aberta para o usuário remetente.
     *
     * @param id           identificador da sessão ativa
     * @param destinatario login do destinatário
     * @param recado       texto do recado
     * @throws UsuarioNaoCadastradoException se a sessão for inválida ou o destinatário não existir
     * @throws AutoRecadoException           se o remetente tentar enviar recado para si mesmo
     */
    public void enviarRecado(String id, String destinatario, String recado)
            throws UsuarioNaoCadastradoException, AutoRecadoException {
        sistema.enviarRecado(id, destinatario, recado);
    }

    /**
     * Retorna o primeiro recado da fila de recados do usuário com a sessão aberta.
     *
     * @param id identificador da sessão ativa
     * @return texto do primeiro recado
     * @throws UsuarioNaoCadastradoException se a sessão for inválida
     * @throws SemRecadosException           se não houver recados na fila
     */
    public String lerRecado(String id)
            throws UsuarioNaoCadastradoException, SemRecadosException {
        return sistema.lerRecado(id);
    }
}
