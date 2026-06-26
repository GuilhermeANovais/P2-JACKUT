package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando uma operação é bloqueada por relação de inimizade.
 *
 * <p>Mensagem dinâmica: "Função inválida: {nome do inimigo} é seu inimigo."</p>
 */
public class FuncaoInvalidaException extends Exception {

    /**
     * Constrói a exceção com o nome do usuário que declarou o remetente como inimigo.
     *
     * @param nomeInimigo nome de exibição do usuário inimigo
     */
    public FuncaoInvalidaException(String nomeInimigo) {
        super("Função inválida: " + nomeInimigo + " é seu inimigo.");
    }
}
