package trackbug.model.service;

import trackbug.model.dao.interfaces.EmprestimoDAO;
import trackbug.model.dao.impl.EmprestimoDAOImpl;
import trackbug.model.entity.Emprestimo;
import java.time.LocalDateTime;
import java.util.List;

public class EmprestimoService {
    private final EmprestimoDAO emprestimoDAO;

    public EmprestimoService() {
        this.emprestimoDAO = new EmprestimoDAOImpl();
    }
    public void realizarEmprestimo(Emprestimo emprestimo) throws Exception {
        // Validações antes de realizar o empréstimo
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo");
        }

        if (emprestimo.getIdFuncionario() == null || emprestimo.getIdFuncionario().trim().isEmpty()) {
            throw new IllegalArgumentException("Funcionário é obrigatório");
        }

        if (emprestimo.getIdEquipamento() == null || emprestimo.getIdEquipamento().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipamento é obrigatório");
        }

        if (emprestimo.getQuantidadeEmprestimo() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Criar o empréstimo no banco de dados
        emprestimoDAO.criar(emprestimo);
    }

    public Emprestimo buscarPorId(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do empréstimo inválido");
        }
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(id);
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não encontrado");
        }
        return emprestimo;
    }

    public void registrar(Emprestimo emprestimo) throws Exception {
        validarEmprestimo(emprestimo);
        emprestimoDAO.criar(emprestimo);
    }

    public void registrarDevolucao(int id) throws Exception {
        Emprestimo emprestimo = buscarPorId(id);
        if (!emprestimo.isAtivo()) {
            throw new IllegalStateException("Empréstimo já foi devolvido");
        }
        emprestimoDAO.registrarDevolucao(id, LocalDateTime.now());
    }

    public List<Emprestimo> listarEmprestimosAtivos() throws Exception {
        return emprestimoDAO.listarAtivos();
    }

    public List<Emprestimo> listarEmprestimosAtrasados() throws Exception {
        return emprestimoDAO.listarAtrasados();
    }

    public List<Emprestimo> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) throws Exception {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
        return emprestimoDAO.buscarPorPeriodo(inicio, fim);
    }

    public List<Emprestimo> buscarPorFuncionario(String idFuncionario) throws Exception {
        if (idFuncionario == null || idFuncionario.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do funcionário é obrigatório");
        }
        return emprestimoDAO.buscarPorFuncionario(idFuncionario);
    }

    public List<Emprestimo> buscarPorEquipamento(String idEquipamento) throws Exception {
        if (idEquipamento == null || idEquipamento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return emprestimoDAO.buscarPorEquipamento(idEquipamento);
    }

    public void atualizar(Emprestimo emprestimo) throws Exception {
        validarEmprestimo(emprestimo);
        emprestimoDAO.atualizar(emprestimo);
    }

    private void validarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo");
        }
        if (emprestimo.getIdFuncionario() == null || emprestimo.getIdFuncionario().trim().isEmpty()) {
            throw new IllegalArgumentException("Funcionário é obrigatório");
        }
        if (emprestimo.getIdEquipamento() == null || emprestimo.getIdEquipamento().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipamento é obrigatório");
        }
        if (emprestimo.getQuantidadeEmprestimo() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (emprestimo.getDataSaida() == null) {
            throw new IllegalArgumentException("Data de saída é obrigatória");
        }
        if (emprestimo.getDataRetornoPrevista() == null) {
            throw new IllegalArgumentException("Data de retorno prevista é obrigatória");
        }
        if (emprestimo.getDataRetornoPrevista().isBefore(emprestimo.getDataSaida())) {
            throw new IllegalArgumentException("Data de retorno prevista não pode ser anterior à data de saída");
        }
    }

    public List<Emprestimo> listarTodos() throws Exception {
        return emprestimoDAO.listarTodos();
    }

    public boolean possuiEmprestimosAtivos(String idFuncionario) throws Exception {
        if (idFuncionario == null || idFuncionario.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do funcionário é obrigatório");
        }
        List<Emprestimo> emprestimos = emprestimoDAO.buscarPorFuncionario(idFuncionario);
        return emprestimos.stream().anyMatch(Emprestimo::isAtivo);
    }
}