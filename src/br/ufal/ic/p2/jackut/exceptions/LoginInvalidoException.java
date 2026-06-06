package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando o login fornecido é inválido (nulo ou vazio).
 */
public class LoginInvalidoException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public LoginInvalidoException() {
        super("Login inválido.");
    }
}
