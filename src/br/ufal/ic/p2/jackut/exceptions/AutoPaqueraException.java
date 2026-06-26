package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta adicionar a si mesmo como paquera.
 */
public class AutoPaqueraException extends Exception {
    public AutoPaqueraException() {
        super("Usuário não pode ser paquera de si mesmo.");
    }
}
