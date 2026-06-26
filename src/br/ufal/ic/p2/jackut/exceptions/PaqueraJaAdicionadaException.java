package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta adicionar como paquera alguém já adicionado.
 */
public class PaqueraJaAdicionadaException extends Exception {
    public PaqueraJaAdicionadaException() {
        super("Usuário já está adicionado como paquera.");
    }
}
