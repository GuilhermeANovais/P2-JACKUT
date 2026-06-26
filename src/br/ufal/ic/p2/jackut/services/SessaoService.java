package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.LoginOuSenhaInvalidosException;
import br.ufal.ic.p2.jackut.exceptions.UsuarioNaoCadastradoException;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço responsável pela gestão de sessões de utilizadores.
 *
 * <p>Mantém o mapeamento de IDs de sessão para logins. As sessões são
 * transientes e não são persistidas entre execuções.</p>
 */
public class SessaoService {

    /** Mapa sessão → login (transiente). */
    private final Map<String, String> sessoes;

    /** Referência ao mapa de usuários para validar credenciais. */
    private final Map<String, Usuario> usuarios;

    /**
     * Constrói o serviço de sessão com referência ao mapa de usuários.
     *
     * @param usuarios mapa de usuários cadastrados
     */
    public SessaoService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
        this.sessoes = new LinkedHashMap<>();
    }

    /**
     * Abre uma sessão para o usuário com login e senha fornecidos.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return ID único da sessão gerado
     * @throws LoginOuSenhaInvalidosException se login/senha inválidos ou usuário inexistente
     */
    public String abrirSessao(String login, String senha) throws LoginOuSenhaInvalidosException {
        if (login == null || login.isEmpty() || senha == null || senha.isEmpty()) {
            throw new LoginOuSenhaInvalidosException();
        }
        Usuario usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new LoginOuSenhaInvalidosException();
        }
        String idSessao = UUID.randomUUID().toString();
        sessoes.put(idSessao, login);
        return idSessao;
    }

    /**
     * Resolve o login do usuário a partir de um ID de sessão.
     *
     * @param idSessao ID da sessão
     * @return login do usuário
     * @throws UsuarioNaoCadastradoException se a sessão for inválida ou nula
     */
    public String resolverLogin(String idSessao) throws UsuarioNaoCadastradoException {
        if (idSessao == null || !sessoes.containsKey(idSessao)) {
            throw new UsuarioNaoCadastradoException();
        }
        return sessoes.get(idSessao);
    }

    /**
     * Invalida/remove uma sessão específica.
     *
     * @param idSessao ID da sessão a invalidar
     */
    public void invalidarSessao(String idSessao) {
        sessoes.remove(idSessao);
    }

    /**
     * Limpa todas as sessões ativas.
     */
    public void limpar() {
        sessoes.clear();
    }
}
