package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta adicionar como amigo um usuário que já é amigo.
 */
public class UsuarioJaAmigoException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioJaAmigoException() {
        super("Usuário já está adicionado como amigo.");
    }
}
