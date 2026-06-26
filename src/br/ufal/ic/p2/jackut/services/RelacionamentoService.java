package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.*;
import br.ufal.ic.p2.jackut.models.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pelos novos tipos de relacionamento (US8).
 *
 * <p>Cobre três relações: fã/ídolo, paquera e inimizade.
 * Também implementa a verificação de inimizade para bloquear ações (US8 cross-cutting).</p>
 */
public class RelacionamentoService {

    private final Map<String, Usuario> usuarios;
    private final RecadoService recadoService;

    /**
     * Constrói o serviço com referência ao mapa de usuários e ao serviço de recados
     * (necessário para enviar a notificação automática de paquera mútua).
     *
     * @param usuarios     mapa de usuários cadastrados
     * @param recadoService serviço de recados para notificações do sistema
     */
    public RelacionamentoService(Map<String, Usuario> usuarios, RecadoService recadoService) {
        this.usuarios = usuarios;
        this.recadoService = recadoService;
    }

    // -------------------------------------------------------------------------
    // Verificação de inimizade (cross-cutting concern — US8)
    // -------------------------------------------------------------------------

    /**
     * Verifica se {@code loginAlvo} declarou {@code loginSuspeito} como inimigo.
     * Se sim, lança {@link FuncaoInvalidaException} com o nome do alvo.
     *
     * <p>Usado antes de adicionarAmigo, adicionarIdolo, adicionarPaquera, enviarRecado.</p>
     *
     * @param loginSuspeito login de quem executa a ação
     * @param loginAlvo     login de quem pode ter declarado inimizade
     * @throws FuncaoInvalidaException       se loginAlvo declarou loginSuspeito como inimigo
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     */
    public void verificarInimizade(String loginSuspeito, String loginAlvo)
            throws FuncaoInvalidaException, UsuarioNaoCadastradoException {
        Usuario alvo = buscarUsuario(loginAlvo);
        if (alvo.ehInimigo(loginSuspeito)) {
            // A mensagem usa o nome de alvo (quem declarou o inimigo)
            String nomeAlvo = obterNome(loginAlvo);
            throw new FuncaoInvalidaException(nomeAlvo);
        }
    }

    // -------------------------------------------------------------------------
    // US8 — Fã/Ídolo
    // -------------------------------------------------------------------------

    /**
     * Adiciona {@code loginIdolo} como ídolo do usuário {@code loginFa}.
     *
     * @param loginFa    login do fã
     * @param loginIdolo login do ídolo
     * @throws UsuarioNaoCadastradoException se algum dos usuários não existir
     * @throws AutoIdoloException            se tentar adicionar a si mesmo
     * @throws IdoloJaAdicionadoException    se o ídolo já estiver na lista
     */
    public void adicionarIdolo(String loginFa, String loginIdolo)
            throws UsuarioNaoCadastradoException, AutoIdoloException, IdoloJaAdicionadoException {
        if (loginFa.equals(loginIdolo)) {
            throw new AutoIdoloException();
        }
        Usuario fa = buscarUsuario(loginFa);
        buscarUsuario(loginIdolo); // valida existência do ídolo
        if (fa.ehFaDe(loginIdolo)) {
            throw new IdoloJaAdicionadoException();
        }
        fa.adicionarIdolo(loginIdolo);
    }

    /**
     * Verifica se {@code login} é fã de {@code loginIdolo}.
     *
     * @param login      login a verificar
     * @param loginIdolo login do ídolo
     * @return {@code true} se for fã
     * @throws UsuarioNaoCadastradoException se algum não existir
     */
    public boolean ehFa(String login, String loginIdolo) throws UsuarioNaoCadastradoException {
        Usuario usuario = buscarUsuario(login);
        buscarUsuario(loginIdolo); // valida existência
        return usuario.ehFaDe(loginIdolo);
    }

    /**
     * Retorna a lista de logins dos fãs do usuário especificado.
     * (Quem tem {@code login} na sua lista de ídolos.)
     *
     * @param login login do ídolo
     * @return lista de logins dos fãs (dados puros)
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public List<String> getFas(String login) throws UsuarioNaoCadastradoException {
        buscarUsuario(login); // valida existência
        List<String> fas = new ArrayList<>();
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            if (entry.getValue().ehFaDe(login)) {
                fas.add(entry.getKey());
            }
        }
        return fas;
    }

    // -------------------------------------------------------------------------
    // US8 — Paquera
    // -------------------------------------------------------------------------

    /**
     * Adiciona {@code loginPaquera} como paquera do usuário {@code login}.
     * Se a relação for mútua, envia recado automático do sistema para ambos.
     *
     * @param login        login de quem adiciona
     * @param loginPaquera login do paquera
     * @throws UsuarioNaoCadastradoException  se algum dos usuários não existir
     * @throws AutoPaqueraException           se tentar adicionar a si mesmo
     * @throws PaqueraJaAdicionadaException   se o paquera já estiver na lista
     */
    public void adicionarPaquera(String login, String loginPaquera)
            throws UsuarioNaoCadastradoException, AutoPaqueraException, PaqueraJaAdicionadaException {
        if (login.equals(loginPaquera)) {
            throw new AutoPaqueraException();
        }
        Usuario usuario = buscarUsuario(login);
        buscarUsuario(loginPaquera); // valida existência
        if (usuario.ehPaquera(loginPaquera)) {
            throw new PaqueraJaAdicionadaException();
        }
        usuario.adicionarPaquera(loginPaquera);

        // Verificar reciprocidade — se ambos se adicionaram como paquera
        Usuario outroUsuario = usuarios.get(loginPaquera);
        if (outroUsuario != null && outroUsuario.ehPaquera(login)) {
            // Enviar recado automático do sistema para ambos
            String nomeOutro = obterNome(loginPaquera);
            String nomeUsuario = obterNome(login);
            try {
                recadoService.enviarRecadoDoSistema(login, nomeOutro + " é seu paquera - Recado do Jackut.");
                recadoService.enviarRecadoDoSistema(loginPaquera, nomeUsuario + " é seu paquera - Recado do Jackut.");
            } catch (UsuarioNaoCadastradoException e) {
                // Não deve ocorrer pois os usuários foram validados acima
                throw new RuntimeException("Erro inesperado ao enviar recado do sistema", e);
            }
        }
    }

    /**
     * Verifica se {@code loginPaquera} é paquera de quem tem sessão {@code login}.
     *
     * @param login        login do usuário
     * @param loginPaquera login a verificar
     * @return {@code true} se for paquera
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public boolean ehPaquera(String login, String loginPaquera) throws UsuarioNaoCadastradoException {
        return buscarUsuario(login).ehPaquera(loginPaquera);
    }

    /**
     * Retorna a lista de logins dos paqueras do usuário (informação privada).
     *
     * @param login login do usuário
     * @return lista de logins dos paqueras (dados puros)
     * @throws UsuarioNaoCadastradoException se usuário não existir
     */
    public List<String> getPaqueras(String login) throws UsuarioNaoCadastradoException {
        return buscarUsuario(login).getPaqueras();
    }

    // -------------------------------------------------------------------------
    // US8 — Inimizade
    // -------------------------------------------------------------------------

    /**
     * Adiciona {@code loginInimigo} à lista de inimigos do usuário {@code login}.
     *
     * @param login         login de quem declara o inimigo
     * @param loginInimigo  login do inimigo declarado
     * @throws UsuarioNaoCadastradoException  se algum não existir
     * @throws AutoInimizadeException         se tentar adicionar a si mesmo
     * @throws InimigoJaAdicionadoException   se já for inimigo
     */
    public void adicionarInimigo(String login, String loginInimigo)
            throws UsuarioNaoCadastradoException, AutoInimizadeException, InimigoJaAdicionadoException {
        if (login.equals(loginInimigo)) {
            throw new AutoInimizadeException();
        }
        Usuario usuario = buscarUsuario(login);
        buscarUsuario(loginInimigo); // valida existência
        if (usuario.ehInimigo(loginInimigo)) {
            throw new InimigoJaAdicionadoException();
        }
        usuario.adicionarInimigo(loginInimigo);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        if (login == null || !usuarios.containsKey(login)) {
            throw new UsuarioNaoCadastradoException();
        }
        return usuarios.get(login);
    }

    private String obterNome(String login) {
        Usuario u = usuarios.get(login);
        if (u == null) return login;
        try {
            return u.getAtributo("nome");
        } catch (Exception e) {
            return login;
        }
    }
}
