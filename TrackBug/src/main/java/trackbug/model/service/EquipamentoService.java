// File: src/main/java/trackbug/model/service/EquipamentoService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.EquipamentoDAO;
import trackbug.model.dao.impl.EquipamentoDAOImpl;
import trackbug.model.entity.Equipamento;
import trackbug.model.dao.interfaces.LogEquipamentoDAO;
import trackbug.model.dao.impl.LogEquipamentoDAOImpl;
import trackbug.model.entity.LogEquipamento;

import java.util.List;

public class EquipamentoService {
    private final EquipamentoDAO equipamentoDAO;
    private final LogEquipamentoDAO logEquipamentoDAO;

    public EquipamentoService() {
        this.equipamentoDAO = new EquipamentoDAOImpl();
        this.logEquipamentoDAO = new LogEquipamentoDAOImpl();
    }


    public void cadastrarEquipamento(Equipamento equipamento) throws Exception {
        // Validações de negócio
        if (equipamento.getQuantidadeMinima() > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade mínima não pode ser maior que quantidade atual");
        }

        // Validar formato do ID
        if (!equipamento.getId().matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("ID deve conter apenas letras, números e hífen");
        }

        // Verificar se já existe
        if (equipamentoDAO.buscarPorId(equipamento.getId()) != null) {
            throw new IllegalArgumentException("Já existe um equipamento com este ID");
        }

        try {
            equipamentoDAO.criar(equipamento);

            // Registra o log de criação
            LogEquipamento log = new LogEquipamento();
            log.setIdEquipamento(equipamento.getId());
            log.setAcao("CRIACAO");
            log.setDescricao("Equipamento cadastrado");
            log.setDetalhes("Quantidade inicial: " + equipamento.getQuantidadeAtual());
            logEquipamentoDAO.registrar(log);
        } catch (Exception e) {
            throw new Exception("Erro ao cadastrar equipamento: " + e.getMessage());
        }
    }

    public void atualizarEquipamento(Equipamento equipamento, Equipamento equipamentoAntigo) throws Exception {
        // Validações de negócio
        if (equipamento.getQuantidadeMinima() > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade mínima não pode ser maior que quantidade atual");
        }

        try {
            equipamentoDAO.atualizar(equipamento);

            // Registra as alterações no log
            StringBuilder detalhes = new StringBuilder();
            if (!equipamento.getDescricao().equals(equipamentoAntigo.getDescricao())) {
                detalhes.append("Descrição alterada de '")
                        .append(equipamentoAntigo.getDescricao())
                        .append("' para '")
                        .append(equipamento.getDescricao())
                        .append("'\n");
            }
            if (equipamento.getQuantidadeAtual() != equipamentoAntigo.getQuantidadeAtual()) {
                detalhes.append("Quantidade alterada de ")
                        .append(equipamentoAntigo.getQuantidadeAtual())
                        .append(" para ")
                        .append(equipamento.getQuantidadeAtual())
                        .append("\n");
            }

            if (detalhes.length() > 0) {
                LogEquipamento log = new LogEquipamento();
                log.setIdEquipamento(equipamento.getId());
                log.setAcao("EDICAO");
                log.setDescricao("Equipamento atualizado");
                log.setDetalhes(detalhes.toString());
                logEquipamentoDAO.registrar(log);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar equipamento: " + e.getMessage());
        }
    }

    public void excluirEquipamento(String id) throws Exception {
        // Verificar se existe empréstimos ativos
        if (equipamentoDAO.verificarEmprestimosAtivos(id)) {
            throw new IllegalStateException("Não é possível excluir equipamento com empréstimos ativos");
        }

        try {
            equipamentoDAO.deletar(id);

            // Registra o log de exclusão
            LogEquipamento log = new LogEquipamento();
            log.setIdEquipamento(id);
            log.setAcao("EXCLUSAO");
            log.setDescricao("Equipamento excluído");
            logEquipamentoDAO.registrar(log);
        } catch (Exception e) {
            throw new Exception("Erro ao excluir equipamento: " + e.getMessage());
        }
    }

    public void registrarAvaria(String idEquipamento, int quantidade, String descricao) throws Exception {
        Equipamento equipamento = equipamentoDAO.buscarPorId(idEquipamento);
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não encontrado");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        if (quantidade > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade de avaria não pode ser maior que quantidade atual");
        }

        try {
            // Atualiza quantidade do equipamento
            equipamento.setQuantidadeAtual(equipamento.getQuantidadeAtual() - quantidade);
            equipamentoDAO.atualizar(equipamento);

            // Registra o log de avaria
            LogEquipamento log = new LogEquipamento();
            log.setIdEquipamento(idEquipamento);
            log.setAcao("AVARIA");
            log.setDescricao("Avaria registrada");
            log.setDetalhes("Quantidade: " + quantidade + "\nDescrição: " + descricao);
            logEquipamentoDAO.registrar(log);
        } catch (Exception e) {
            throw new Exception("Erro ao registrar avaria: " + e.getMessage());
        }
    }

    public List<Equipamento> listarDisponiveis() throws Exception {
        try {
            return equipamentoDAO.listarDisponiveis();
        } catch (Exception e) {
            throw new Exception("Erro ao listar equipamentos disponíveis: " + e.getMessage());
        }
    }

    public List<Equipamento> listarTodos() throws Exception {
        try {
            return equipamentoDAO.listarTodos();
        } catch (Exception e) {
            throw new Exception("Erro ao listar equipamentos: " + e.getMessage());
        }
    }

    public Equipamento buscarPorId(String id) throws Exception {
        try {
            return equipamentoDAO.buscarPorId(id);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar equipamento: " + e.getMessage());
        }
    }
    public String buscarNomePorId(String id) throws Exception {
        try {
            return equipamentoDAO.buscarNomePorId(id);
        } catch (Exception e) {
            throw new Exception("Erro ao buscar nome do equipamento: " + e.getMessage());
        }
    }
}