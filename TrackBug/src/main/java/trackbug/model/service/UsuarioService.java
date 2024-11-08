package trackbug.model.service;

import trackbug.model.dao.interfaces.UsuarioDAO;
import trackbug.model.dao.impl.UsuarioDAOImpl;
import trackbug.model.entity.Usuario;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public void cadastrar(Usuario usuario) throws Exception {
        validarUsuario(usuario);
        if (usuarioDAO.buscarPorUsername(usuario.getUsername()) != null) {
            throw new IllegalArgumentException("Username já existe");
        }
        usuarioDAO.criar(usuario);
    }

    public void atualizar(Usuario usuario) throws Exception {
        validarUsuario(usuario);
        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioDAO.atualizar(usuario);
    }

    public void atualizarStatus(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        Usuario existente = usuarioDAO.buscarPorId(usuario.getId());
        if (existente == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioDAO.atualizarStatus(usuario);
    }

    public Usuario buscarPorUsername(String username) throws Exception {
        return usuarioDAO.buscarPorUsername(username);
    }

    public List<Usuario> listarTodos() throws Exception {
        return usuarioDAO.listarTodos();
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (usuario.getNivelAcesso() <= 0) {
            throw new IllegalArgumentException("Nível de acesso inválido");
        }
    }
}