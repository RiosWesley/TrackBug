package trackbug.model.dao.interfaces;

import trackbug.model.entity.Avaria;
import trackbug.model.entity.RegistroAvaria;
import java.util.List;

public interface AvariaDAO {
    void registrar(Avaria avaria) throws Exception;
    List<RegistroAvaria> listarPorEquipamento(String idEquipamento) throws Exception;
    List<RegistroAvaria> listarTodas() throws Exception;
}