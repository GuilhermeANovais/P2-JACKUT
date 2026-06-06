package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta criar um usuário com um login que já existe no sistema.
 */
public class ContaJaExisteException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public ContaJaExisteException() {
        super("Conta com esse nome já existe.");
    }
}
