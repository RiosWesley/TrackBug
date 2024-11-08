package trackbug.model.dao.interfaces;

import trackbug.model.entity.LogEquipamento;
import java.util.List;

public interface LogEquipamentoDAO {
    void registrar(LogEquipamento log) throws Exception;
    List<LogEquipamento> listarPorEquipamento(String idEquipamento) throws Exception;
    List<LogEquipamento> listarTodos() throws Exception;
}