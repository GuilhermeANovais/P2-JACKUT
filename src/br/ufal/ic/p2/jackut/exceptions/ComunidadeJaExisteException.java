package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta criar uma comunidade com um nome já existente.
 */
public class ComunidadeJaExisteException extends Exception {
    public ComunidadeJaExisteException() {
        super("Comunidade com esse nome já existe.");
    }
}
