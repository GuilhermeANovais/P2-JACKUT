package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta adicionar a si mesmo como inimigo.
 */
public class AutoInimizadeException extends Exception {
    public AutoInimizadeException() {
        super("Usuário não pode ser inimigo de si mesmo.");
    }
}
