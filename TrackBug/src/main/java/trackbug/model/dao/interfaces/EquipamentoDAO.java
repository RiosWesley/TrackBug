package trackbug.model.dao.interfaces;

import trackbug.model.entity.Equipamento;
import java.util.List;

public interface EquipamentoDAO {
    void criar(Equipamento equipamento) throws Exception;
    Equipamento buscarPorId(String id) throws Exception;
    List<Equipamento> listarTodos() throws Exception;
    List<Equipamento> listarDisponiveis() throws Exception;
    void atualizar(Equipamento equipamento) throws Exception;
    void deletar(String id) throws Exception;
    void atualizarQuantidade(String id, int quantidade) throws Exception;
    void atualizarStatus(String id, String status) throws Exception;
    boolean verificarEmprestimosAtivos(String id) throws Exception;
}