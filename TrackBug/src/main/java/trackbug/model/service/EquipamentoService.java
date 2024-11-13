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

    public void salvar(Equipamento equipamento) throws Exception {
        validarEquipamento(equipamento);

        if (existePorId(equipamento.getId())) {
            throw new IllegalArgumentException("Já existe um equipamento com este código");
        }

        equipamentoDAO.criar(equipamento);

        // Registrar log de criação
        LogEquipamento log = new LogEquipamento();
        log.setIdEquipamento(equipamento.getId());
        log.setAcao("CRIACAO");
        log.setDescricao("Equipamento cadastrado");
        log.setDetalhes(String.format(
                "Descrição: %s\nQuantidade inicial: %d\nTipo: %s",
                equipamento.getDescricao(),
                equipamento.getQuantidadeAtual(),
                equipamento.isTipo() ? "Consumível" : "Emprestável"
        ));

        logEquipamentoDAO.registrar(log);
    }

    public boolean existePorId(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID não pode ser vazio");
        }
        return equipamentoDAO.buscarPorId(id) != null;
    }

    private void validarEquipamento(Equipamento equipamento) {
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não pode ser nulo");
        }
        if (equipamento.getId() == null || equipamento.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Código é obrigatório");
        }
        if (equipamento.getDescricao() == null || equipamento.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (equipamento.getDataCompra() == null) {
            throw new IllegalArgumentException("Data de compra é obrigatória");
        }
        if (equipamento.getQuantidadeAtual() < 0) {
            throw new IllegalArgumentException("Quantidade atual não pode ser negativa");
        }
        if (equipamento.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa");
        }
        if (equipamento.getQuantidadeMinima() < 0) {
            throw new IllegalArgumentException("Quantidade mínima não pode ser negativa");
        }
        if (equipamento.getQuantidadeMinima() > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade mínima não pode ser maior que quantidade atual");
        }
        if (equipamento.getTipoUso() == null || equipamento.getTipoUso().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de uso é obrigatório");
        }
    }

    public void editar(Equipamento equipamento) throws Exception {
        validarEquipamento(equipamento);
        Equipamento equipamentoAntigo = equipamentoDAO.buscarPorId(equipamento.getId());

        if (equipamentoAntigo == null) {
            throw new IllegalArgumentException("Equipamento não encontrado");
        }

        equipamentoDAO.atualizar(equipamento);

        // Registrar log de edição
        LogEquipamento log = new LogEquipamento();
        log.setIdEquipamento(equipamento.getId());
        log.setAcao("EDICAO");
        log.setDescricao("Equipamento editado");
        logEquipamentoDAO.registrar(log);
    }

    public boolean possuiEmprestimosAtivos(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return equipamentoDAO.verificarEmprestimosAtivos(id);
    }

    public void deletar(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }

        if (possuiEmprestimosAtivos(id)) {
            throw new IllegalStateException("Não é possível excluir equipamento com empréstimos ativos");
        }

        equipamentoDAO.deletar(id);

        // Registrar log de exclusão
        LogEquipamento log = new LogEquipamento();
        log.setIdEquipamento(id);
        log.setAcao("EXCLUSAO");
        log.setDescricao("Equipamento excluído");
        logEquipamentoDAO.registrar(log);
    }

    public void registrarAvaria(Equipamento equipamento, String descricao) throws Exception {
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não pode ser nulo");
        }

        registrarAvaria(equipamento.getId(), 1, descricao);
    }

    public void registrarAvaria(String idEquipamento, int quantidade, String descricao) throws Exception {
        if (idEquipamento == null || idEquipamento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }

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

        // Atualizar quantidade do equipamento
        equipamento.setQuantidadeAtual(equipamento.getQuantidadeAtual() - quantidade);
        equipamentoDAO.atualizar(equipamento);

        // Registrar log de avaria
        LogEquipamento log = new LogEquipamento();
        log.setIdEquipamento(idEquipamento);
        log.setAcao("AVARIA");
        log.setDescricao("Avaria registrada");
        log.setDetalhes("Quantidade: " + quantidade + "\nDescrição: " + descricao);
        logEquipamentoDAO.registrar(log);
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
    public String buscarNomePorId(String id) {
        try {
            Equipamento equipamento = equipamentoDAO.buscarPorId(id);
            return equipamento != null ? equipamento.getDescricao() : "Equipamento não encontrado";
        } catch (Exception e) {
            return "Erro ao buscar equipamento";
        }
    }
}