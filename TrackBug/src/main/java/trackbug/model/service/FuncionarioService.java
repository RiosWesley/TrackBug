// File: src/main/java/trackbug/model/service/FuncionarioService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.FuncionarioDAO;
import trackbug.model.dao.impl.FuncionarioDAOImpl;
import trackbug.model.entity.Funcionario;

import java.util.List;

public class FuncionarioService {
    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAOImpl();
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws Exception {
        // Validações
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (funcionario.getId() == null || funcionario.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID é obrigatório");
        }

        if (funcionario.getFuncao() == null || funcionario.getFuncao().trim().isEmpty()) {
            throw new IllegalArgumentException("Função é obrigatória");
        }

        // Validar formato do ID
        if (!funcionario.getId().matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("ID deve conter apenas letras, números e hífen");
        }

        try {
            if (funcionarioDAO.buscarPorId(funcionario.getId()) != null) {
                throw new IllegalArgumentException("Já existe um funcionário com este ID");
            }

            funcionarioDAO.criar(funcionario);
        } catch (Exception e) {
            throw new Exception("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    public void atualizarFuncionario(Funcionario funcionario) throws Exception {
        // Validações
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (funcionario.getFuncao() == null || funcionario.getFuncao().trim().isEmpty()) {
            throw new IllegalArgumentException("Função é obrigatória");
        }

        try {
            if (funcionarioDAO.buscarPorId(funcionario.getId()) == null) {
                throw new IllegalArgumentException("Funcionário não encontrado");
            }

            funcionarioDAO.atualizar(funcionario);
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar funcionário: " + e.getMessage());
        }
    }

    public void excluirFuncionario(String id) throws Exception {
        try {
            if (funcionarioDAO.verificarEmprestimosAtivos(id)) {
                throw new IllegalStateException("Não é possível excluir funcionário com empréstimos ativos");
            }

            funcionarioDAO.deletar(id);
        } catch (Exception e) {
            throw new Exception("Erro ao excluir funcionário: " + e.getMessage());
        }
    }

    public Funcionario buscarPorId(String id) throws Exception {
        try {
            Funcionario funcionario = funcionarioDAO.buscarPorId(id);
            if (funcionario == null) {
                throw new IllegalArgumentException("Funcionário não encontrado");
            }
            return funcionario;
        } catch (Exception e) {
            throw new Exception("Erro ao buscar funcionário: " + e.getMessage());
        }
    }

    public List<Funcionario> listarTodos() throws Exception {
        try {
            return funcionarioDAO.listarTodos();
        } catch (Exception e) {
            throw new Exception("Erro ao listar funcionários: " + e.getMessage());
        }
    }
}