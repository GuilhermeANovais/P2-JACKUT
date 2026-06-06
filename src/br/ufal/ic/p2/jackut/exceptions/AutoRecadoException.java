package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta enviar um recado para si mesmo.
 */
public class AutoRecadoException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AutoRecadoException() {
        super("Usuário não pode enviar recado para si mesmo.");
    }
}
