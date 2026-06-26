package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.Map;

/**
 * Serviço responsável pelo ciclo de vida de usuários.
 *
 * <p>Criação, leitura, edição de perfil e remoção de contas.
 * Valida regras de negócio puras: unicidade de login, integridade de dados.</p>
 */
public class UsuarioService {

    private final Map<String, Usuario> usuarios;

    /**
     * Constrói o serviço com referência ao mapa de usuários.
     *
     * @param usuarios mapa de usuários cadastrados
     */
    public UsuarioService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login login único
     * @param senha senha de acesso
     * @param nome  nome de exibição
     * @throws LoginInvalidoException  se login nulo ou vazio
     * @throws SenhaInvalidaException  se senha nula ou vazia
     * @throws ContaJaExisteException  se login já em uso
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
     * Busca um usuário pelo login, lançando exceção se não encontrado.
     *
     * @param login login do usuário
     * @return instância de {@link Usuario}
     * @throws UsuarioNaoCadastradoException se login não existir
     */
    public Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }

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
        Usuario usuario = buscarUsuario(login);
        return usuario.getAtributo(atributo);
    }

    /**
     * Edita um atributo do perfil do usuário identificado pelo login.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo
     * @param valor    novo valor
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public void editarPerfil(String login, String atributo, String valor)
            throws UsuarioNaoCadastradoException {
        Usuario usuario = buscarUsuario(login);
        if ("senha".equals(atributo)) {
            usuario.setSenha(valor);
        } else {
            usuario.setAtributo(atributo, valor);
        }
    }

    /**
     * Remove um usuário do sistema pelo login.
     *
     * @param login login do usuário a remover
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public void removerUsuario(String login) throws UsuarioNaoCadastradoException {
        buscarUsuario(login); // valida existência
        usuarios.remove(login);
    }
}
