package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pela lógica de amizades entre usuários.
 *
 * <p>Gerencia convites, aceitação/efetivação de amizade e consultas.
 * Não conhece sessões nem persistência.</p>
 */
public class AmizadeService {

    private final Map<String, Usuario> usuarios;

    /**
     * Constrói o serviço com referência ao mapa de usuários.
     *
     * @param usuarios mapa de usuários cadastrados
     */
    public AmizadeService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Processa um pedido de amizade do remetente para o alvo.
     *
     * <p>Se o alvo já tiver enviado convite ao remetente, efetiva a amizade mutuamente.
     * Caso contrário, registra o convite como pendente.</p>
     *
     * @param loginRemetente login de quem envia o convite
     * @param loginAmigo     login do destinatário do convite
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     * @throws AutoAmizadeException          se tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se já forem amigos confirmados
     * @throws ConvitePendenteException      se já houver convite pendente para o alvo
     */
    public void adicionarAmigo(String loginRemetente, String loginAmigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException {

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

        if (alvo.temConviteEnviadoPara(loginRemetente)) {
            // Amizade mútua: efetivar dos dois lados
            remetente.efetivarAmizade(loginAmigo);
            alvo.efetivarAmizade(loginRemetente);
        } else {
            remetente.adicionarConviteEnviado(loginAmigo);
            alvo.adicionarConviteRecebido(loginRemetente);
        }
    }

    /**
     * Verifica se dois usuários são amigos confirmados.
     *
     * @param login login do primeiro usuário
     * @param amigo login do segundo usuário
     * @return {@code true} se forem amigos
     * @throws UsuarioNaoCadastradoException se algum não existir
     */
    public boolean ehAmigo(String login, String amigo) throws UsuarioNaoCadastradoException {
        Usuario usuario = buscarUsuario(login);
        buscarUsuario(amigo); // valida existência
        return usuario.ehAmigo(amigo);
    }

    /**
     * Retorna a lista de logins dos amigos do usuário especificado.
     *
     * @param login login do usuário
     * @return lista de logins de amigos (dados puros, sem formatação)
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public List<String> getAmigos(String login) throws UsuarioNaoCadastradoException {
        return buscarUsuario(login).getAmigos();
    }

    /**
     * Remove o login especificado das listas de amizade e convites de todos os outros usuários.
     * Usado na remoção de conta (US9).
     *
     * @param loginRemovido login do usuário cujas referências devem ser apagadas
     */
    public void removerReferenciasDe(String loginRemovido) {
        for (Usuario u : usuarios.values()) {
            u.removerAmigo(loginRemovido);
        }
    }

    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }
}
