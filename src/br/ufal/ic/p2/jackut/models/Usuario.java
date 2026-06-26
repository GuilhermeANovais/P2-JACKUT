package br.ufal.ic.p2.jackut.models;

import br.ufal.ic.p2.jackut.exceptions.AtributoNaoPreenchidoException;
import br.ufal.ic.p2.jackut.exceptions.SemMensagensException;
import br.ufal.ic.p2.jackut.exceptions.SemRecadosException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Representa um usuário cadastrado no sistema Jackut.
 *
 * <p>Cada usuário possui um login único, uma senha, um nome e um perfil
 * com atributos dinâmicos (mapa chave-valor). Além disso, mantém listas
 * de amigos, convites pendentes enviados e recebidos, e uma fila FIFO de recados.</p>
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Login único que identifica o usuário no sistema. */
    private final String login;

    /** Senha de acesso do usuário. */
    private String senha;

    /** Mapa de atributos dinâmicos do perfil (inclui "nome"). */
    private final Map<String, String> perfil;

    /**
     * Lista de logins dos usuários que são amigos confirmados.
     * A ordem de inserção é preservada.
     */
    private final List<String> amigos;

    /**
     * Lista de logins para os quais este usuário enviou convites
     * de amizade ainda não aceitos.
     */
    private final List<String> convitesEnviados;

    /**
     * Lista de logins que enviaram convites de amizade para este usuário
     * que ainda não foram aceitos.
     */
    private final List<String> convitesRecebidos;

    /** Fila FIFO de recados privados recebidos pelo usuário. */
    private final Queue<Recado> recados;

    /** Fila FIFO de mensagens de comunidades recebidas pelo usuário (US7). */
    private final Queue<String> mensagens;

    /** Lista de logins dos ídolos deste usuário (US8 — fã/ídolo). */
    private final List<String> idolos;

    /** Lista de logins dos paqueras deste usuário (US8 — privado). */
    private final List<String> paqueras;

    /** Lista de logins dos inimigos deste usuário (US8 — inimizade). */
    private final List<String> inimigos;

    /** Lista de nomes das comunidades a que este usuário pertence (US6). */
    private final List<String> comunidades;

    /**
     * Constrói um novo usuário com login, senha e nome.
     *
     * @param login login único do usuário
     * @param senha senha de acesso
     * @param nome  nome de exibição na rede
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.perfil = new HashMap<>();
        this.perfil.put("nome", nome);
        this.amigos = new ArrayList<>();
        this.convitesEnviados = new ArrayList<>();
        this.convitesRecebidos = new ArrayList<>();
        this.recados = new LinkedList<>();
        this.mensagens = new LinkedList<>();
        this.idolos = new ArrayList<>();
        this.paqueras = new ArrayList<>();
        this.inimigos = new ArrayList<>();
        this.comunidades = new ArrayList<>();
    }

    /**
     * Retorna o login do usuário.
     *
     * @return login único do usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return senha do usuário
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Atualiza a senha do usuário.
     *
     * @param senha nova senha do usuário
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Retorna o valor de um atributo do perfil do usuário.
     *
     * @param atributo nome do atributo a ser recuperado
     * @return valor do atributo
     * @throws AtributoNaoPreenchidoException se o atributo não foi preenchido
     */
    public String getAtributo(String atributo) throws AtributoNaoPreenchidoException {
        // Senha não é acessível via perfil por razões de segurança
        if ("senha".equals(atributo) || !perfil.containsKey(atributo)) {
            throw new AtributoNaoPreenchidoException();
        }
        return perfil.get(atributo);
    }

    /**
     * Define ou atualiza o valor de um atributo do perfil do usuário.
     *
     * @param atributo nome do atributo
     * @param valor    valor a ser armazenado
     */
    public void setAtributo(String atributo, String valor) {
        perfil.put(atributo, valor);
    }

    /**
     * Retorna a lista de logins dos amigos confirmados.
     *
     * @return lista de logins de amigos
     */
    public List<String> getAmigos() {
        return Collections.unmodifiableList(amigos);
    }

    /**
     * Retorna a lista de logins para os quais este usuário enviou convites pendentes.
     *
     * @return lista de logins com convites enviados
     */
    public List<String> getConvitesEnviados() {
        return Collections.unmodifiableList(convitesEnviados);
    }

    /**
     * Retorna a lista de logins que enviaram convites de amizade para este usuário.
     *
     * @return lista de logins com convites recebidos
     */
    public List<String> getConvitesRecebidos() {
        return Collections.unmodifiableList(convitesRecebidos);
    }

    /**
     * Adiciona um convite enviado para o login especificado.
     *
     * @param loginAlvo login do destinatário do convite
     */
    public void adicionarConviteEnviado(String loginAlvo) {
        convitesEnviados.add(loginAlvo);
    }

    /**
     * Adiciona um convite recebido do login especificado.
     *
     * @param loginRemetente login de quem enviou o convite
     */
    public void adicionarConviteRecebido(String loginRemetente) {
        convitesRecebidos.add(loginRemetente);
    }

    /**
     * Efetiva a amizade com o login especificado, removendo os convites pendentes.
     *
     * @param loginAmigo login do usuário que se tornará amigo
     */
    public void efetivarAmizade(String loginAmigo) {
        convitesEnviados.remove(loginAmigo);
        convitesRecebidos.remove(loginAmigo);
        amigos.add(loginAmigo);
    }

    /**
     * Remove um amigo da lista de amigos confirmados e dos convites pendentes.
     * Usado na remoção de conta (US9).
     *
     * @param loginAmigo login do amigo a remover
     */
    public void removerAmigo(String loginAmigo) {
        amigos.remove(loginAmigo);
        convitesEnviados.remove(loginAmigo);
        convitesRecebidos.remove(loginAmigo);
    }

    /**
     * Verifica se o login especificado é um amigo confirmado deste usuário.
     *
     * @param loginAlvo login a verificar
     * @return {@code true} se for amigo; {@code false} caso contrário
     */
    public boolean ehAmigo(String loginAlvo) {
        return amigos.contains(loginAlvo);
    }

    /**
     * Verifica se existe um convite pendente enviado para o login especificado.
     *
     * @param loginAlvo login a verificar
     * @return {@code true} se houver convite enviado pendente; {@code false} caso contrário
     */
    public boolean temConviteEnviadoPara(String loginAlvo) {
        return convitesEnviados.contains(loginAlvo);
    }

    /**
     * Verifica se existe um convite pendente recebido do login especificado.
     *
     * @param loginRemetente login a verificar
     * @return {@code true} se houver convite recebido pendente; {@code false} caso contrário
     */
    public boolean temConviteRecebidoDe(String loginRemetente) {
        return convitesRecebidos.contains(loginRemetente);
    }

    /**
     * Adiciona um recado à fila FIFO do usuário.
     *
     * @param loginRemetente login de quem enviou o recado
     * @param texto          texto do recado a ser adicionado
     */
    public void adicionarRecado(String loginRemetente, String texto) {
        recados.add(new Recado(loginRemetente, texto));
    }

    /**
     * Remove e retorna o texto do primeiro recado da fila FIFO.
     *
     * @return texto do primeiro recado
     * @throws SemRecadosException se não houver recados na fila
     */
    public String lerRecado() throws SemRecadosException {
        if (recados.isEmpty()) {
            throw new SemRecadosException();
        }
        return recados.poll().getTexto();
    }

    /**
     * Remove todos os recados enviados pelo login especificado desta fila.
     * Usado na remoção de conta (US9): recados enviados pelo usuário removido
     * devem desaparecer das caixas dos destinatários.
     *
     * @param loginRemetente login cujos recados devem ser descartados
     */
    public void removerRecadosDe(String loginRemetente) {
        recados.removeIf(r -> r.getLoginRemetente().equals(loginRemetente));
    }

    // -------------------------------------------------------------------------
    // US7 — Mensagens de comunidade (separadas dos recados privados)
    // -------------------------------------------------------------------------

    /**
     * Adiciona uma mensagem de comunidade à fila FIFO do usuário.
     *
     * @param mensagem texto da mensagem
     */
    public void adicionarMensagem(String mensagem) {
        mensagens.add(mensagem);
    }

    /**
     * Remove e retorna a primeira mensagem de comunidade da fila FIFO.
     *
     * @return texto da primeira mensagem
     * @throws SemMensagensException se não houver mensagens na fila
     */
    public String lerMensagem() throws SemMensagensException {
        if (mensagens.isEmpty()) {
            throw new SemMensagensException();
        }
        return mensagens.poll();
    }

    // -------------------------------------------------------------------------
    // US8 — Fã/Ídolo
    // -------------------------------------------------------------------------

    /**
     * Adiciona um login à lista de ídolos deste usuário.
     *
     * @param loginIdolo login do ídolo
     */
    public void adicionarIdolo(String loginIdolo) {
        idolos.add(loginIdolo);
    }

    /**
     * Verifica se este usuário é fã do login especificado.
     *
     * @param loginIdolo login a verificar
     * @return {@code true} se for ídolo deste usuário
     */
    public boolean ehFaDe(String loginIdolo) {
        return idolos.contains(loginIdolo);
    }

    /**
     * Retorna a lista de logins dos ídolos deste usuário.
     *
     * @return lista imutável de ídolos
     */
    public List<String> getIdolos() {
        return Collections.unmodifiableList(idolos);
    }

    // -------------------------------------------------------------------------
    // US8 — Paquera
    // -------------------------------------------------------------------------

    /**
     * Adiciona um login à lista de paqueras deste usuário.
     *
     * @param loginPaquera login do paquera
     */
    public void adicionarPaquera(String loginPaquera) {
        paqueras.add(loginPaquera);
    }

    /**
     * Verifica se o login especificado é paquera deste usuário.
     *
     * @param loginPaquera login a verificar
     * @return {@code true} se for paquera
     */
    public boolean ehPaquera(String loginPaquera) {
        return paqueras.contains(loginPaquera);
    }

    /**
     * Retorna a lista de logins dos paqueras deste usuário.
     *
     * @return lista imutável de paqueras
     */
    public List<String> getPaqueras() {
        return Collections.unmodifiableList(paqueras);
    }

    // -------------------------------------------------------------------------
    // US8 — Inimizade
    // -------------------------------------------------------------------------

    /**
     * Adiciona um login à lista de inimigos deste usuário.
     *
     * @param loginInimigo login do inimigo
     */
    public void adicionarInimigo(String loginInimigo) {
        inimigos.add(loginInimigo);
    }

    /**
     * Verifica se o login especificado é inimigo deste usuário.
     *
     * @param loginInimigo login a verificar
     * @return {@code true} se for inimigo
     */
    public boolean ehInimigo(String loginInimigo) {
        return inimigos.contains(loginInimigo);
    }

    // -------------------------------------------------------------------------
    // US6 — Comunidades
    // -------------------------------------------------------------------------

    /**
     * Adiciona o nome de uma comunidade à lista de comunidades deste usuário.
     *
     * @param nomeComunidade nome da comunidade
     */
    public void adicionarComunidade(String nomeComunidade) {
        comunidades.add(nomeComunidade);
    }

    /**
     * Remove o nome de uma comunidade da lista deste usuário.
     *
     * @param nomeComunidade nome da comunidade a remover
     */
    public void removerComunidade(String nomeComunidade) {
        comunidades.remove(nomeComunidade);
    }

    /**
     * Retorna a lista de nomes das comunidades a que este usuário pertence.
     *
     * @return lista imutável de nomes de comunidades
     */
    public List<String> getComunidades() {
        return Collections.unmodifiableList(comunidades);
    }
}
