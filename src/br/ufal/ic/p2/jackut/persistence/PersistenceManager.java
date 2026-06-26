package br.ufal.ic.p2.jackut.persistence;

import java.io.*;

/**
 * Gerenciador de persistência do sistema Jackut.
 *
 * <p>Única classe responsável por operações de I/O: carregar o estado
 * do disco e gravá-lo. Completamente agnóstico sobre regras de negócio.</p>
 */
public class PersistenceManager {

    private final String arquivoDados;

    /**
     * Constrói o gerenciador apontando para o ficheiro indicado.
     *
     * @param arquivoDados caminho relativo ao diretório de trabalho
     */
    public PersistenceManager(String arquivoDados) {
        this.arquivoDados = arquivoDados;
    }

    /**
     * Carrega o estado persistido a partir do disco.
     * Se o ficheiro não existir ou ocorrer erro de leitura, retorna um estado vazio.
     *
     * @return {@link SistemaDados} carregado ou novo (fail-safe)
     */
    public SistemaDados carregar() {
        File arquivo = new File(arquivoDados);
        if (!arquivo.exists()) {
            return new SistemaDados();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoDados))) {
            return (SistemaDados) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new SistemaDados();
        }
    }

    /**
     * Grava o estado atual no disco.
     *
     * @param dados estado a persistir
     * @throws RuntimeException se ocorrer erro de I/O
     */
    public void salvar(SistemaDados dados) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoDados))) {
            oos.writeObject(dados);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar dados do sistema: " + e.getMessage(), e);
        }
    }

    /**
     * Remove o ficheiro de dados do disco, se existir.
     */
    public void apagar() {
        File arquivo = new File(arquivoDados);
        if (arquivo.exists() && !arquivo.delete()) {
            System.err.println("Aviso: não foi possível remover o arquivo de dados: "
                    + arquivo.getAbsolutePath());
        }
    }
}
