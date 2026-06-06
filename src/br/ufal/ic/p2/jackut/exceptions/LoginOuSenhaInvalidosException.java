package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando o login ou a senha estão incorretos ao tentar abrir uma sessão.
 * A mensagem é genérica por segurança, sem indicar qual dos dois está errado.
 */
public class LoginOuSenhaInvalidosException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public LoginOuSenhaInvalidosException() {
        super("Login ou senha inválidos.");
    }
}
