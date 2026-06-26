package br.ufal.ic.p2.jackut.persistence;

import br.ufal.ic.p2.jackut.models.Comunidade;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO (Data Transfer Object) que encapsula o estado persistível do sistema.
 *
 * <p>Contém apenas os dados que devem sobreviver entre execuções.
 * Sessões são excluídas por serem transientes.</p>
 */
public class SistemaDados implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Mapa de usuários cadastrados, indexado pelo login. */
    private Map<String, Usuario> usuarios;

    /** Mapa de comunidades, indexado pelo nome (PK). */
    private Map<String, Comunidade> comunidades;

    /**
     * Constrói um SistemaDados vazio.
     */
    public SistemaDados() {
        this.usuarios = new LinkedHashMap<>();
        this.comunidades = new LinkedHashMap<>();
    }

    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Map<String, Comunidade> getComunidades() {
        return comunidades;
    }

    public void setComunidades(Map<String, Comunidade> comunidades) {
        this.comunidades = comunidades;
    }
}
