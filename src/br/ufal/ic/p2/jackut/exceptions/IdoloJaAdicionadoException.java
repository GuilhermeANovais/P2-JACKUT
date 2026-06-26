package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta adicionar como ídolo alguém já adicionado.
 */
public class IdoloJaAdicionadoException extends Exception {
    public IdoloJaAdicionadoException() {
        super("Usuário já está adicionado como ídolo.");
    }
}
