package br.ufal.ic.p2.jackut.models;

import br.ufal.ic.p2.jackut.exceptions.AtributoNaoPreenchidoException;
import br.ufal.ic.p2.jackut.exceptions.SemRecadosException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Representa um usuário cadastrado no sistema Jackut.
 *
 * <p>Cada usuário possui um login único, uma senha, um nome e um perfil
 * com atributos dinâmicos (mapa chave-valor). Além disso, mantém listas
 * de amigos, convites pendentes enviados e recebidos, e uma fila FIFO de recados.</p>
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Login único que identifica o usuário no sistema. */
    private final String login;

    /** Senha de acesso do usuário. */
    private String senha;

    /** Mapa de atributos dinâmicos do perfil (inclui "nome"). */
    private final Map<String, String> perfil;

    /**
     * Lista de logins dos usuários que são amigos confirmados.
     * A ordem de inserção é preservada.
     */
    private final List<String> amigos;

    /**
     * Lista de logins para os quais este usuário enviou convites
     * de amizade ainda não aceitos.
     */
    private final List<String> convitesEnviados;

    /**
     * Lista de logins que enviaram convites de amizade para este usuário
     * que ainda não foram aceitos.
     */
    private final List<String> convitesRecebidos;

    /** Fila FIFO de recados recebidos pelo usuário. */
    private final Queue<String> recados;

    /**
     * Constrói um novo usuário com login, senha e nome.
     *
     * @param login login único do usuário
     * @param senha senha de acesso
     * @param nome  nome de exibição na rede
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.perfil = new HashMap<>();
        this.perfil.put("nome", nome);
        this.amigos = new ArrayList<>();
        this.convitesEnviados = new ArrayList<>();
        this.convitesRecebidos = new ArrayList<>();
        this.recados = new LinkedList<>();
    }

    /**
     * Retorna o login do usuário.
     *
     * @return login único do usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return senha do usuário
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Atualiza a senha do usuário.
     *
     * @param senha nova senha do usuário
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Retorna o valor de um atributo do perfil do usuário.
     *
     * @param atributo nome do atributo a ser recuperado
     * @return valor do atributo
     * @throws AtributoNaoPreenchidoException se o atributo não foi preenchido
     */
    public String getAtributo(String atributo) throws AtributoNaoPreenchidoException {
        // Senha não é acessível via perfil por razões de segurança
        if ("senha".equals(atributo) || !perfil.containsKey(atributo)) {
            throw new AtributoNaoPreenchidoException();
        }
        return perfil.get(atributo);
    }

    /**
     * Define ou atualiza o valor de um atributo do perfil do usuário.
     *
     * @param atributo nome do atributo
     * @param valor    valor a ser armazenado
     */
    public void setAtributo(String atributo, String valor) {
        perfil.put(atributo, valor);
    }

    /**
     * Retorna a lista de logins dos amigos confirmados.
     *
     * @return lista de logins de amigos
     */
    public List<String> getAmigos() {
        return Collections.unmodifiableList(amigos);
    }

    /**
     * Retorna a lista de logins para os quais este usuário enviou convites pendentes.
     *
     * @return lista de logins com convites enviados
     */
    public List<String> getConvitesEnviados() {
        return Collections.unmodifiableList(convitesEnviados);
    }

    /**
     * Retorna a lista de logins que enviaram convites de amizade para este usuário.
     *
     * @return lista de logins com convites recebidos
     */
    public List<String> getConvitesRecebidos() {
        return Collections.unmodifiableList(convitesRecebidos);
    }

    /**
     * Adiciona um convite enviado para o login especificado.
     *
     * @param loginAlvo login do destinatário do convite
     */
    public void adicionarConviteEnviado(String loginAlvo) {
        convitesEnviados.add(loginAlvo);
    }

    /**
     * Adiciona um convite recebido do login especificado.
     *
     * @param loginRemetente login de quem enviou o convite
     */
    public void adicionarConviteRecebido(String loginRemetente) {
        convitesRecebidos.add(loginRemetente);
    }

    /**
     * Efetiva a amizade com o login especificado, removendo os convites pendentes.
     *
     * @param loginAmigo login do usuário que se tornará amigo
     */
    public void efetivarAmizade(String loginAmigo) {
        convitesEnviados.remove(loginAmigo);
        convitesRecebidos.remove(loginAmigo);
        amigos.add(loginAmigo);
    }

    /**
     * Verifica se o login especificado é um amigo confirmado deste usuário.
     *
     * @param loginAlvo login a verificar
     * @return {@code true} se for amigo; {@code false} caso contrário
     */
    public boolean ehAmigo(String loginAlvo) {
        return amigos.contains(loginAlvo);
    }

    /**
     * Verifica se existe um convite pendente enviado para o login especificado.
     *
     * @param loginAlvo login a verificar
     * @return {@code true} se houver convite enviado pendente; {@code false} caso contrário
     */
    public boolean temConviteEnviadoPara(String loginAlvo) {
        return convitesEnviados.contains(loginAlvo);
    }

    /**
     * Verifica se existe um convite pendente recebido do login especificado.
     *
     * @param loginRemetente login a verificar
     * @return {@code true} se houver convite recebido pendente; {@code false} caso contrário
     */
    public boolean temConviteRecebidoDe(String loginRemetente) {
        return convitesRecebidos.contains(loginRemetente);
    }

    /**
     * Adiciona um recado à fila FIFO do usuário.
     *
     * @param recado texto do recado a ser adicionado
     */
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }

    /**
     * Remove e retorna o primeiro recado da fila FIFO.
     *
     * @return texto do primeiro recado
     * @throws SemRecadosException se não houver recados na fila
     */
    public String lerRecado() throws SemRecadosException {
        if (recados.isEmpty()) {
            throw new SemRecadosException();
        }
        return recados.poll();
    }
}
