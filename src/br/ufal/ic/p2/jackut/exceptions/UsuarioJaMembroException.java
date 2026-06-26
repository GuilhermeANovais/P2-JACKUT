package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando um usuário tenta entrar numa comunidade da qual já é membro.
 */
public class UsuarioJaMembroException extends Exception {
    public UsuarioJaMembroException() {
        super("Usuario já faz parte dessa comunidade.");
    }
}
