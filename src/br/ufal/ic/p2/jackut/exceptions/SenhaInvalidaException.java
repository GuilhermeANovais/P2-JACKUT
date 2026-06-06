package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando a senha fornecida é inválida (nula ou vazia).
 */
public class SenhaInvalidaException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public SenhaInvalidaException() {
        super("Senha inválida.");
    }
}
