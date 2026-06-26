package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.Map;

/**
 * Serviço responsável pelo fluxo de recados privados entre usuários.
 *
 * <p>Gerencia envio (com validação de auto-recado) e leitura FIFO de recados.
 * Não conhece sessões — recebe logins já resolvidos.</p>
 */
public class RecadoService {

    private final Map<String, Usuario> usuarios;

    /**
     * Constrói o serviço com referência ao mapa de usuários.
     *
     * @param usuarios mapa de usuários cadastrados
     */
    public RecadoService(Map<String, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Envia um recado do remetente para o destinatário.
     *
     * @param loginRemetente    login de quem envia
     * @param loginDestinatario login de quem recebe
     * @param texto             texto do recado
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     * @throws AutoRecadoException           se remetente == destinatário
     */
    public void enviarRecado(String loginRemetente, String loginDestinatario, String texto)
            throws UsuarioNaoCadastradoException, AutoRecadoException {
        buscarUsuario(loginRemetente); // valida remetente
        if (loginRemetente.equals(loginDestinatario)) {
            throw new AutoRecadoException();
        }
        Usuario destinatario = buscarUsuario(loginDestinatario);
        destinatario.adicionarRecado(loginRemetente, texto);
    }

    /**
     * Lê e remove o primeiro recado da fila do usuário.
     *
     * @param login login do destinatário
     * @return texto do primeiro recado
     * @throws UsuarioNaoCadastradoException se usuário não existir
     * @throws SemRecadosException           se fila vazia
     */
    public String lerRecado(String login)
            throws UsuarioNaoCadastradoException, SemRecadosException {
        return buscarUsuario(login).lerRecado();
    }

    /**
     * Injeta um recado directamente na caixa de recados do usuário (usado pelo sistema
     * para notificações automáticas, como o recado de paquera mútua).
     *
     * @param loginDestinatario login do destinatário
     * @param texto             texto do recado do sistema
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public void enviarRecadoDoSistema(String loginDestinatario, String texto)
            throws UsuarioNaoCadastradoException {
        buscarUsuario(loginDestinatario).adicionarRecado("SISTEMA", texto);
    }

    /**
     * Remove os recados enviados por {@code loginRemetente} das caixas de todos os outros
     * usuários. Chamado pela US9 quando um usuário é removido.
     *
     * @param loginRemetente login do usuário removido
     */
    public void removerRecadosDe(String loginRemetente) {
        for (Usuario u : usuarios.values()) {
            u.removerRecadosDe(loginRemetente);
        }
    }

    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }
}
