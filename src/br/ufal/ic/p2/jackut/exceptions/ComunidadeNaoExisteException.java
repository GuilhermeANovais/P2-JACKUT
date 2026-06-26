package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta acessar ou modificar uma comunidade inexistente.
 */
public class ComunidadeNaoExisteException extends Exception {
    public ComunidadeNaoExisteException() {
        super("Comunidade não existe.");
    }
}
