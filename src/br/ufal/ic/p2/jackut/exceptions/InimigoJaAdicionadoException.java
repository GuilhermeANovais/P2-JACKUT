package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta adicionar como inimigo alguém já adicionado.
 */
public class InimigoJaAdicionadoException extends Exception {
    public InimigoJaAdicionadoException() {
        super("Usuário já está adicionado como inimigo.");
    }
}
