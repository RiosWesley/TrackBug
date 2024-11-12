package trackbug.model.service;

import trackbug.model.dao.interfaces.FuncionarioDAO;
import trackbug.model.dao.impl.FuncionarioDAOImpl;
import trackbug.model.entity.Funcionario;

import java.time.LocalDate;
import java.util.List;

public class FuncionarioService {
    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAOImpl();
    }

    public void cadastrarFuncionario(Funcionario funcionario) throws Exception {
        validarFuncionario(funcionario);

        if (existePorId(funcionario.getId())) {
            throw new IllegalArgumentException("Já existe um funcionário com este ID");
        }

        try {
            funcionarioDAO.criar(funcionario);
        } catch (Exception e) {
            throw new Exception("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    public void criar(Funcionario funcionario) throws Exception {
        validarFuncionario(funcionario);

        if (existePorId(funcionario.getId())) {
            throw new IllegalArgumentException("Já existe um funcionário com este ID");
        }

        try {
            funcionarioDAO.criar(funcionario);
        } catch (Exception e) {
            throw new Exception("Erro ao criar funcionário: " + e.getMessage());
        }
    }

    public void atualizar(Funcionario funcionario) throws Exception {
        validarFuncionario(funcionario);

        if (!existePorId(funcionario.getId())) {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }

        try {
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

    public boolean existePorId(String id) throws Exception {
        try {
            return funcionarioDAO.buscarPorId(id) != null;
        } catch (Exception e) {
            throw new Exception("Erro ao verificar existência do funcionário: " + e.getMessage());
        }
    }

    private void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo");
        }
        if (funcionario.getId() == null || funcionario.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do funcionário é obrigatório");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (funcionario.getFuncao() == null || funcionario.getFuncao().trim().isEmpty()) {
            throw new IllegalArgumentException("Função é obrigatória");
        }
        if (funcionario.getDataAdmissao() == null) {
            throw new IllegalArgumentException("Data de admissão é obrigatória");
        }
        if (funcionario.getDataAdmissao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de admissão não pode ser futura");
        }
    }

    public boolean possuiEmprestimosAtivos(String id) throws Exception {
        try {
            return funcionarioDAO.verificarEmprestimosAtivos(id);
        } catch (Exception e) {
            throw new Exception("Erro ao verificar empréstimos ativos: " + e.getMessage());
        }
    }
}