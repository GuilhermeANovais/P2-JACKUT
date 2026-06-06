package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando se tenta ler um recado e a fila está vazia.
 */
public class SemRecadosException extends Exception {

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public SemRecadosException() {
        super("Não há recados.");
    }
}
