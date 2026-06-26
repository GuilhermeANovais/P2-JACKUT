package br.ufal.ic.p2.jackut.models;

import java.io.Serializable;

/**
 * Representa um recado privado entre usuários no sistema Jackut.
 *
 * <p>Guarda o login do remetente para permitir a limpeza de recados
 * quando um usuário é removido (US9).</p>
 */
public class Recado implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Login do usuário que enviou o recado. */
    private final String loginRemetente;

    /** Texto do recado. */
    private final String texto;

    /**
     * Constrói um recado com remetente e texto.
     *
     * @param loginRemetente login de quem enviou
     * @param texto          conteúdo do recado
     */
    public Recado(String loginRemetente, String texto) {
        this.loginRemetente = loginRemetente;
        this.texto = texto;
    }

    public String getLoginRemetente() {
        return loginRemetente;
    }

    public String getTexto() {
        return texto;
    }
}
