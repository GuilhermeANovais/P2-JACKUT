package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta acessar um usuário que não existe no sistema,
 * ou quando uma operação é realizada com uma sessão inválida.
 */
public class UsuarioNaoCadastradoException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioNaoCadastradoException() {
        super("Usuário não cadastrado.");
    }
}
