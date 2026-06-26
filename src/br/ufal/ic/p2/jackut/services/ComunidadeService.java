package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Comunidade;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pela gestão de comunidades (US5, US6, US7).
 *
 * <p>Cobre criação, consulta, adição de membros, envio e leitura de mensagens.
 * Retorna dados puros (listas) — formatação é responsabilidade da Facade.</p>
 */
public class ComunidadeService {

    private final Map<String, Usuario> usuarios;
    private final Map<String, Comunidade> comunidades;

    /**
     * Constrói o serviço com referência aos mapas de usuários e comunidades.
     *
     * @param usuarios    mapa de usuários cadastrados
     * @param comunidades mapa de comunidades (nome → Comunidade)
     */
    public ComunidadeService(Map<String, Usuario> usuarios, Map<String, Comunidade> comunidades) {
        this.usuarios = usuarios;
        this.comunidades = comunidades;
    }

    // -------------------------------------------------------------------------
    // US5 — Criação de comunidades
    // -------------------------------------------------------------------------

    /**
     * Cria uma nova comunidade. O criador torna-se automaticamente o dono e primeiro membro.
     *
     * @param loginDono  login do criador
     * @param nome       nome único da comunidade
     * @param descricao  descrição da comunidade
     * @throws UsuarioNaoCadastradoException se o dono não existir
     * @throws ComunidadeJaExisteException   se já existir comunidade com esse nome
     */
    public void criarComunidade(String loginDono, String nome, String descricao)
            throws UsuarioNaoCadastradoException, ComunidadeJaExisteException {
        buscarUsuario(loginDono); // valida existência
        if (comunidades.containsKey(nome)) {
            throw new ComunidadeJaExisteException();
        }
        Comunidade comunidade = new Comunidade(nome, descricao, loginDono);
        comunidades.put(nome, comunidade);
        // Registar comunidade no perfil do dono
        buscarUsuario(loginDono).adicionarComunidade(nome);
    }

    /**
     * Retorna a descrição de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return descrição
     * @throws ComunidadeNaoExisteException se não existir
     */
    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        return buscarComunidade(nome).getDescricao();
    }

    /**
     * Retorna o login do dono de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return login do dono
     * @throws ComunidadeNaoExisteException se não existir
     */
    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        return buscarComunidade(nome).getLoginDono();
    }

    /**
     * Retorna a lista de logins dos membros de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return lista de logins dos membros (dados puros)
     * @throws ComunidadeNaoExisteException se não existir
     */
    public List<String> getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        return buscarComunidade(nome).getMembros();
    }

    // -------------------------------------------------------------------------
    // US6 — Adição de membros a comunidades
    // -------------------------------------------------------------------------

    /**
     * Adiciona o usuário especificado como membro da comunidade.
     *
     * @param login login do usuário a adicionar
     * @param nome  nome da comunidade
     * @throws UsuarioNaoCadastradoException se o usuário não existir
     * @throws ComunidadeNaoExisteException  se a comunidade não existir
     * @throws UsuarioJaMembroException      se o usuário já for membro
     */
    public void adicionarComunidade(String login, String nome)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException, UsuarioJaMembroException {
        Usuario usuario = buscarUsuario(login);
        Comunidade comunidade = buscarComunidade(nome);
        if (comunidade.ehMembro(login)) {
            throw new UsuarioJaMembroException();
        }
        comunidade.adicionarMembro(login);
        usuario.adicionarComunidade(nome);
    }

    /**
     * Retorna a lista de nomes de comunidades a que o usuário pertence.
     *
     * @param login login do usuário
     * @return lista de nomes de comunidades (dados puros)
     * @throws UsuarioNaoCadastradoException se o usuário não existir
     */
    public List<String> getComunidades(String login) throws UsuarioNaoCadastradoException {
        return buscarUsuario(login).getComunidades();
    }

    // -------------------------------------------------------------------------
    // US7 — Mensagens de comunidade
    // -------------------------------------------------------------------------

    /**
     * Envia uma mensagem a uma comunidade. Todos os membros a recebem na fila de mensagens.
     *
     * @param loginRemetente  login do remetente (deve ser usuário válido)
     * @param nomeComunidade  nome da comunidade destino
     * @param mensagem        texto da mensagem
     * @throws UsuarioNaoCadastradoException se o remetente não existir
     * @throws ComunidadeNaoExisteException  se a comunidade não existir
     */
    public void enviarMensagem(String loginRemetente, String nomeComunidade, String mensagem)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException {
        buscarUsuario(loginRemetente); // valida remetente
        Comunidade comunidade = buscarComunidade(nomeComunidade);
        for (String loginMembro : comunidade.getMembros()) {
            Usuario membro = usuarios.get(loginMembro);
            if (membro != null) {
                membro.adicionarMensagem(mensagem);
            }
        }
    }

    /**
     * Lê e remove a primeira mensagem da fila do usuário.
     *
     * @param login login do usuário
     * @return texto da primeira mensagem
     * @throws UsuarioNaoCadastradoException se o usuário não existir
     * @throws SemMensagensException         se a fila de mensagens estiver vazia
     */
    public String lerMensagem(String login)
            throws UsuarioNaoCadastradoException, SemMensagensException {
        return buscarUsuario(login).lerMensagem();
    }

    // -------------------------------------------------------------------------
    // US9 — Remoção de conta: limpeza de comunidades
    // -------------------------------------------------------------------------

    /**
     * Remove todas as comunidades das quais o usuário é dono.
     * Também remove os membros de cada comunidade apagada.
     *
     * @param loginDono login do dono das comunidades a apagar
     */
    public void removerComunidadesDoDono(String loginDono) {
        List<String> nomesParaApagar = new ArrayList<>();
        for (Map.Entry<String, Comunidade> entry : comunidades.entrySet()) {
            if (entry.getValue().getLoginDono().equals(loginDono)) {
                nomesParaApagar.add(entry.getKey());
            }
        }
        for (String nome : nomesParaApagar) {
            Comunidade comunidade = comunidades.remove(nome);
            // Remover a comunidade do perfil de todos os membros
            for (String loginMembro : comunidade.getMembros()) {
                Usuario membro = usuarios.get(loginMembro);
                if (membro != null) {
                    membro.removerComunidade(nome);
                }
            }
        }
    }

    /**
     * Remove o usuário como membro de todas as comunidades em que participa (mas não é dono).
     *
     * @param login login do usuário a remover das comunidades
     */
    public void removerMembroDeComunidades(String login) {
        Usuario usuario = usuarios.get(login);
        if (usuario == null) return;
        // Copiar lista para evitar ConcurrentModificationException
        List<String> coms = new ArrayList<>(usuario.getComunidades());
        for (String nomeCom : coms) {
            Comunidade comunidade = comunidades.get(nomeCom);
            if (comunidade != null) {
                comunidade.removerMembro(login);
            }
            usuario.removerComunidade(nomeCom);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private Comunidade buscarComunidade(String nome) throws ComunidadeNaoExisteException {
        if (nome == null || !comunidades.containsKey(nome)) {
            throw new ComunidadeNaoExisteException();
        }
        return comunidades.get(nome);
    }

    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }
}
