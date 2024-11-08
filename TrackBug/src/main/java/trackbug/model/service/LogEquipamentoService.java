// LogEquipamentoService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.LogEquipamentoDAO;
import trackbug.model.dao.impl.LogEquipamentoDAOImpl;
import trackbug.model.entity.LogEquipamento;
import java.util.List;

public class LogEquipamentoService {
    private final LogEquipamentoDAO logEquipamentoDAO;

    public LogEquipamentoService() {
        this.logEquipamentoDAO = new LogEquipamentoDAOImpl();
    }

    public void registrarLog(LogEquipamento log) throws Exception {
        if (log.getIdEquipamento() == null || log.getIdEquipamento().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        if (log.getAcao() == null || log.getAcao().trim().isEmpty()) {
            throw new IllegalArgumentException("Ação é obrigatória");
        }
        logEquipamentoDAO.registrar(log);
    }

    public List<LogEquipamento> buscarPorEquipamento(String idEquipamento) throws Exception {
        if (idEquipamento == null || idEquipamento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return logEquipamentoDAO.listarPorEquipamento(idEquipamento);
    }

    public List<LogEquipamento> buscarTodos() throws Exception {
        return logEquipamentoDAO.listarTodos();
    }
}


