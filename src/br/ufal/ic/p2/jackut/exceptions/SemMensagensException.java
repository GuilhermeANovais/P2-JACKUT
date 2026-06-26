package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando não há mensagens de comunidade na fila do usuário.
 */
public class SemMensagensException extends Exception {
    public SemMensagensException() {
        super("Não há mensagens.");
    }
}
