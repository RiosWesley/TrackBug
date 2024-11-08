package trackbug.model.dao.interfaces;

import java.util.List;

public interface FuncionarioDAO {
    void criar(Funcionario funcionario) throws Exception;

    Funcionario buscarPorId(String id) throws Exception;

    List<Funcionario> listarTodos() throws Exception;

    void atualizar(Funcionario funcionario) throws Exception;

    void deletar(String id) throws Exception;

    boolean verificarEmprestimosAtivos(String id) throws Exception;
}