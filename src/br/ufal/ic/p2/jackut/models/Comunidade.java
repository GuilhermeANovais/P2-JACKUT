package br.ufal.ic.p2.jackut.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa uma comunidade no sistema Jackut.
 *
 * <p>Cada comunidade tem um nome único (PK), uma descrição, um dono
 * e uma lista de membros. O dono é automaticamente o primeiro membro.</p>
 */
public class Comunidade implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nome único da comunidade — funciona como chave primária. */
    private final String nome;

    /** Descrição da comunidade. */
    private final String descricao;

    /** Login do usuário criador/dono da comunidade. */
    private final String loginDono;

    /** Lista de logins dos membros, em ordem de inserção (dono é o primeiro). */
    private final List<String> membros;

    /**
     * Constrói uma nova comunidade. O dono é automaticamente adicionado como membro.
     *
     * @param nome      nome único da comunidade
     * @param descricao descrição da comunidade
     * @param loginDono login do usuário criador
     */
    public Comunidade(String nome, String descricao, String loginDono) {
        this.nome = nome;
        this.descricao = descricao;
        this.loginDono = loginDono;
        this.membros = new ArrayList<>();
        this.membros.add(loginDono);
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLoginDono() {
        return loginDono;
    }

    /**
     * Retorna a lista imutável de membros.
     *
     * @return lista de logins dos membros
     */
    public List<String> getMembros() {
        return Collections.unmodifiableList(membros);
    }

    /**
     * Adiciona um membro à comunidade.
     *
     * @param login login do novo membro
     */
    public void adicionarMembro(String login) {
        membros.add(login);
    }

    /**
     * Verifica se o login é membro da comunidade.
     *
     * @param login login a verificar
     * @return {@code true} se for membro
     */
    public boolean ehMembro(String login) {
        return membros.contains(login);
    }

    /**
     * Remove um membro da comunidade.
     *
     * @param login login a remover
     */
    public void removerMembro(String login) {
        membros.remove(login);
    }
}
