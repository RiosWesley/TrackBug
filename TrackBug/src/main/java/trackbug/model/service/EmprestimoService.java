// File: src/main/java/trackbug/model/service/EmprestimoService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.EmprestimoDAO;
import trackbug.model.dao.interfaces.EquipamentoDAO;
import trackbug.model.dao.interfaces.FuncionarioDAO;
import trackbug.model.dao.impl.EmprestimoDAOImpl;
import trackbug.model.dao.impl.EquipamentoDAOImpl;
import trackbug.model.dao.impl.FuncionarioDAOImpl;
import trackbug.model.entity.Emprestimo;
import trackbug.model.entity.Equipamento;

import java.time.LocalDateTime;
import java.util.List;

public class EmprestimoService {
    private final EmprestimoDAO emprestimoDAO;
    private final EquipamentoDAO equipamentoDAO;
    private final FuncionarioDAO funcionarioDAO;

    public EmprestimoService() {
        this.emprestimoDAO = new EmprestimoDAOImpl();
        this.equipamentoDAO = new EquipamentoDAOImpl();
        this.funcionarioDAO = new FuncionarioDAOImpl();
    }

    public void realizarEmprestimo(Emprestimo emprestimo) throws Exception {
        // Validações
        if (emprestimo.getQuantidadeEmprestimo() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Equipamento equipamento = equipamentoDAO.buscarPorId(emprestimo.getIdEquipamento());
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não encontrado");
        }

        if (funcionarioDAO.buscarPorId(emprestimo.getIdFuncionario()) == null) {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }

        if (emprestimo.getQuantidadeEmprestimo() > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade solicitada maior que disponível");
        }

        // Validar data de devolução
        if (emprestimo.getDataRetornoPrevista().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de devolução não pode ser anterior à data atual");
        }

        try {
            // Se for item de uso único, já marca como devolvido
            if ("Uso Único".equals(equipamento.getTipoUso())) {
                emprestimo.setDataRetornoEfetiva(LocalDateTime.now());
                emprestimo.setAtivo(false);
                emprestimo.setTipoOperacao("BAIXA");
                emprestimo.setObservacoes("Item de uso único - Baixa automática");
            } else {
                emprestimo.setAtivo(true);
                emprestimo.setTipoOperacao("SAIDA");
            }

            emprestimoDAO.criar(emprestimo);
        } catch (Exception e) {
            throw new Exception("Erro ao realizar empréstimo: " + e.getMessage());
        }
    }

    public void registrarDevolucao(int emprestimoId) throws Exception {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(emprestimoId);
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não encontrado");
        }

        if (!emprestimo.isAtivo()) {
            throw new IllegalStateException("Empréstimo já foi devolvido");
        }

        try {
            emprestimoDAO.registrarDevolucao(emprestimoId, LocalDateTime.now());
        } catch (Exception e) {
            throw new Exception("Erro ao registrar devolução: " + e.getMessage());
        }
    }

    public List<Emprestimo> listarEmprestimosAtivos() throws Exception {
        try {
            return emprestimoDAO.listarAtivos();
        } catch (Exception e) {
            throw new Exception("Erro ao listar empréstimos ativos: " + e.getMessage());
        }
    }

    public List<Emprestimo> listarEmprestimosAtrasados() throws Exception {
        try {
            return emprestimoDAO.listarAtrasados();
        } catch (Exception e) {
            throw new Exception("Erro ao listar empréstimos em atraso: " + e.getMessage());
        }
    }

    public List<Emprestimo> buscarPorFuncionario(String idFuncionario) throws Exception {
        try {
            return emprestimoDAO.buscarPorFuncionario(idFuncionario);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar empréstimos do funcionário: " + e.getMessage());
        }
    }

    public List<Emprestimo> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) throws Exception {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }

        try {
            return emprestimoDAO.buscarPorPeriodo(inicio, fim);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar empréstimos por período: " + e.getMessage());
        }
    }
}