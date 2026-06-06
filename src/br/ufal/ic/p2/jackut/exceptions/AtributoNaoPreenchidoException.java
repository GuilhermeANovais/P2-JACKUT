package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta obter um atributo do perfil de um usuário
 * que ainda não foi preenchido.
 */
public class AtributoNaoPreenchidoException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AtributoNaoPreenchidoException() {
        super("Atributo não preenchido.");
    }
}
