// File: src/main/java/trackbug/model/service/EquipamentoService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.EquipamentoDAO;
import trackbug.model.dao.impl.EquipamentoDAOImpl;
import trackbug.model.entity.Equipamento;
import trackbug.model.dao.interfaces.LogEquipamentoDAO;
import trackbug.model.dao.impl.LogEquipamentoDAOImpl;
import trackbug.model.entity.LogEquipamento;
import trackbug.util.SessionManager;

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
        // Validar medidas se presentes
        if (Double.compare(equipamento.getPeso(), 0) < 0) {
            throw new IllegalArgumentException("Peso não pode ser negativo");
        }
        if (Double.compare(equipamento.getLargura(), 0) < 0) {
            throw new IllegalArgumentException("Largura não pode ser negativa");
        }
        if (Double.compare(equipamento.getComprimento(), 0) < 0) {
            throw new IllegalArgumentException("Comprimento não pode ser negativo");
        }
    }

    private void validarRegrasEdicao(Equipamento equipamentoNovo, Equipamento equipamentoAntigo) throws Exception {
        // Verificar se mudança de tipo é permitida
        if (equipamentoAntigo.isTipo() != equipamentoNovo.isTipo() &&
                equipamentoDAO.verificarEmprestimosAtivos(equipamentoNovo.getId())) {
            throw new IllegalStateException(
                    "Não é possível alterar o tipo do equipamento enquanto houver empréstimos ativos");
        }

        // Verificar se redução de quantidade é permitida
        if (equipamentoNovo.getQuantidadeAtual() < equipamentoAntigo.getQuantidadeAtual() &&
                equipamentoDAO.verificarEmprestimosAtivos(equipamentoNovo.getId())) {
            throw new IllegalStateException(
                    "Não é possível reduzir a quantidade enquanto houver empréstimos ativos");
        }

        // Validar alteração de tipo de uso
        if (!equipamentoAntigo.getTipoUso().equals(equipamentoNovo.getTipoUso()) &&
                equipamentoDAO.verificarEmprestimosAtivos(equipamentoNovo.getId())) {
            throw new IllegalStateException(
                    "Não é possível alterar o tipo de uso enquanto houver empréstimos ativos");
        }
    }

    private void registrarLogAlteracoes(Equipamento equipamentoAntigo, Equipamento equipamentoNovo) {
        try {
            StringBuilder detalhes = new StringBuilder();
            String usuarioNome = SessionManager.getUsuarioLogado().getNome();

            // Comparar e registrar alterações
            if (!equipamentoAntigo.getDescricao().equals(equipamentoNovo.getDescricao())) {
                detalhes.append(String.format("Descrição: '%s' → '%s'\n",
                        equipamentoAntigo.getDescricao(), equipamentoNovo.getDescricao()));
            }

            if (equipamentoAntigo.getQuantidadeAtual() != equipamentoNovo.getQuantidadeAtual()) {
                detalhes.append(String.format("Quantidade: %d → %d\n",
                        equipamentoAntigo.getQuantidadeAtual(), equipamentoNovo.getQuantidadeAtual()));
            }

            if (equipamentoAntigo.getQuantidadeMinima() != equipamentoNovo.getQuantidadeMinima()) {
                detalhes.append(String.format("Quantidade Mínima: %d → %d\n",
                        equipamentoAntigo.getQuantidadeMinima(), equipamentoNovo.getQuantidadeMinima()));
            }

            if (equipamentoAntigo.isTipo() != equipamentoNovo.isTipo()) {
                detalhes.append(String.format("Tipo: %s → %s\n",
                        equipamentoAntigo.isTipo() ? "Consumível" : "Emprestável",
                        equipamentoNovo.isTipo() ? "Consumível" : "Emprestável"));
            }

            // Comparar medidas
            compararMedidas(equipamentoAntigo, equipamentoNovo, detalhes);

            // Se houve alterações, registrar no log
            if (detalhes.length() > 0) {
                LogEquipamento log = new LogEquipamento(
                        equipamentoNovo.getId(),
                        "Equipamento editado por " + usuarioNome,
                        "EDICAO",
                        detalhes.toString()
                );
                logEquipamentoDAO.registrar(log);
            }
        } catch (Exception e) {
            // Log do erro, mas não impede a atualização do equipamento
            System.err.println("Erro ao registrar log de alterações: " + e.getMessage());
        }
    }

    private void compararMedidas(Equipamento antigo, Equipamento novo, StringBuilder detalhes) {
        // Comparar peso
        if (Double.compare(antigo.getPeso(), novo.getPeso()) != 0) {
            detalhes.append(String.format("Peso: %.2f → %.2f\n",
                    antigo.getPeso(), novo.getPeso()));
        }

        // Comparar largura
        if (Double.compare(antigo.getLargura(), novo.getLargura()) != 0) {
            detalhes.append(String.format("Largura: %.2f → %.2f\n",
                    antigo.getLargura(), novo.getLargura()));
        }

        // Comparar comprimento
        if (Double.compare(antigo.getComprimento(), novo.getComprimento()) != 0) {
            detalhes.append(String.format("Comprimento: %.2f → %.2f\n",
                    antigo.getComprimento(), novo.getComprimento()));
        }
    }

    public void editar(Equipamento equipamentoNovo) throws Exception {
        // Validações iniciais
        validarEquipamento(equipamentoNovo);

        // Buscar equipamento original para comparação
        Equipamento equipamentoAntigo = equipamentoDAO.buscarPorId(equipamentoNovo.getId());
        if (equipamentoAntigo == null) {
            throw new IllegalArgumentException("Equipamento não encontrado");
        }

        try {
            // Validar regras de negócio específicas para edição
            validarRegrasEdicao(equipamentoNovo, equipamentoAntigo);

            // Atualizar equipamento
            equipamentoDAO.atualizar(equipamentoNovo);

            // Registrar log de alterações
            registrarLogAlteracoes(equipamentoAntigo, equipamentoNovo);

        } catch (Exception e) {
            throw new Exception("Erro ao editar equipamento: " + e.getMessage());
        }
    }

    public boolean possuiEmprestimosAtivos(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return equipamentoDAO.verificarEmprestimosAtivos(id);
    }

    public void deletar(String id) throws Exception {
        // Validar ID
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }

        try {
            // Verificar se equipamento existe
            Equipamento equipamento = equipamentoDAO.buscarPorId(id);
            if (equipamento == null) {
                throw new IllegalArgumentException("Equipamento não encontrado");
            }

            // Verificar se há empréstimos ativos
            if (possuiEmprestimosAtivos(id)) {
                throw new IllegalStateException("Não é possível excluir equipamento com empréstimos ativos");
            }

            // Registrar log antes da exclusão
            LogEquipamento log = new LogEquipamento(
                    id,
                    "Equipamento excluído",
                    "EXCLUSAO",
                    String.format(
                            "Descrição: %s\nQuantidade em estoque: %d\nTipo: %s",
                            equipamento.getDescricao(),
                            equipamento.getQuantidadeEstoque(),
                            equipamento.isTipo() ? "Consumível" : "Emprestável"
                    )
            );

            // Registrar o log
            logEquipamentoDAO.registrar(log);

            // Excluir o equipamento
            equipamentoDAO.deletar(id);

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e; // Re-lança exceções de validação
        } catch (Exception e) {
            throw new Exception("Erro ao excluir equipamento: " + e.getMessage());
        }
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

    public List<Equipamento> listarEmprestados() throws Exception {
        try {
            return equipamentoDAO.listarEmprestados();
        } catch (Exception e) {
            throw new Exception("Erro ao listar equipamentos disponíveis: " + e.getMessage());
        }
    }

    public List<Equipamento> listarEstoqueBaixo() throws Exception {
        try {
            return equipamentoDAO.listarEstoqueBaixo();
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