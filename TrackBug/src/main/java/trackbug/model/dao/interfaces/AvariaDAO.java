// src/main/java/trackbug/model/dao/interfaces/AvariaDAO.java
package trackbug.model.dao.interfaces;

import trackbug.model.entity.Avaria;
import java.util.List;

public interface AvariaDAO {
    void registrar(Avaria avaria) throws Exception;
    List<Avaria> listarPorEquipamento(String idEquipamento) throws Exception;
    List<Avaria> listarTodas() throws Exception;
}