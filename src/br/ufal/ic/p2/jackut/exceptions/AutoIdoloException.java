package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta adicionar a si mesmo como ídolo.
 */
public class AutoIdoloException extends Exception {
    public AutoIdoloException() {
        super("Usuário não pode ser fã de si mesmo.");
    }
}
