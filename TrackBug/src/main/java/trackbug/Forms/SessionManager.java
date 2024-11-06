package trackbug.Forms;

import trackbug.model.NivelAcesso;
import trackbug.model.Usuario;

public class SessionManager {
    private static Usuario usuarioLogado;

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void limparSessao() {
        usuarioLogado = null;
    }

    public static boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.getNivelAcesso() == NivelAcesso.ADMIN.getNivel();
    }
}