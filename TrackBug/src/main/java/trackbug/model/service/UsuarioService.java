// File: src/main/java/trackbug/model/service/UsuarioService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.UsuarioDAO;
import trackbug.model.dao.impl.UsuarioDAOImpl;
import trackbug.model.entity.Usuario;

import java.util.List;
import java.util.regex.Pattern;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public void cadastrarUsuario(Usuario usuario) throws Exception {
        // Validações
        validarDadosUsuario(usuario);

        try {
            if (usuarioDAO.buscarPorUsername(usuario.getUsername()) != null) {
                throw new IllegalArgumentException("Nome de usuário já existe");
            }

            usuarioDAO.criar(usuario);
        } catch (Exception e) {
            throw new Exception("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public void atualizarUsuario(Usuario usuario) throws Exception {
        // Validações
        validarDadosUsuario(usuario);

        try {
            Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
            if (existente == null) {
                throw new IllegalArgumentException("Usuário não encontrado");
            }

            usuarioDAO.atualizar(usuario);
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    public void alterarStatus(int id, boolean ativo) throws Exception {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não encontrado");
            }

            usuario.setAtivo(ativo);
            usuarioDAO.atualizarStatus(usuario);
        } catch (Exception e) {
            throw new Exception("Erro ao alterar status do usuário: " + e.getMessage());
        }
    }

    public boolean autenticar(String username, String password) throws Exception {
        try {
            return usuarioDAO.autenticar(username, password);
        } catch (Exception e) {
            throw new Exception("Erro ao autenticar usuário: " + e.getMessage());
        }
    }

    public Usuario buscarPorUsername(String username) throws Exception {
        try {
            return usuarioDAO.buscarPorUsername(username);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodos() throws Exception {
        try {
            return usuarioDAO.listarTodos();
        } catch (Exception e) {
            throw new Exception("Erro ao listar usuários: " + e.getMessage());
        }
    }

    private void validarDadosUsuario(Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().trim().length() < 3) {
            throw new IllegalArgumentException("Nome de usuário deve ter no mínimo 3 caracteres");
        }

        if (usuario.getPassword() != null && usuario.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || !EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
            throw new IllegalArgumentException("E-mail inválido");
        }

        if (usuario.getNivelAcesso() <= 0) {
            throw new IllegalArgumentException("Nível de acesso inválido");
        }
    }
}