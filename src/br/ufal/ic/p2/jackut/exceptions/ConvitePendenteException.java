package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta adicionar como amigo um usuário para o qual
 * já foi enviado um convite aguardando aceitação.
 */
public class ConvitePendenteException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public ConvitePendenteException() {
        super("Usuário já está adicionado como amigo, esperando aceitação do convite.");
    }
}
