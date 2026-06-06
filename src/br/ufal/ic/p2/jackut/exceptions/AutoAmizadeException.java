package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta adicionar a si mesmo como amigo.
 */
public class AutoAmizadeException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AutoAmizadeException() {
        super("Usuário não pode adicionar a si mesmo como amigo.");
    }
}
