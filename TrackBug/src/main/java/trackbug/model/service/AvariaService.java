package trackbug.model.service;

import trackbug.model.dao.impl.AvariaDAOImpl;
import trackbug.model.dao.interfaces.AvariaDAO;
import trackbug.model.entity.Avaria;

import java.util.List;

public class AvariaService {
    private final AvariaDAO avariaDAO;

    public AvariaService() {
        this.avariaDAO = new AvariaDAOImpl();
    }

    public void registrarAvaria(Avaria avaria) throws Exception {
        if (avaria.getIdEquipamento() == null || avaria.getIdEquipamento().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        if (avaria.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (avaria.getDescricao() == null || avaria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        avariaDAO.registrar(avaria);
    }

    public List<Avaria> listarPorEquipamento(String idEquipamento) throws Exception {
        if (idEquipamento == null || idEquipamento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return avariaDAO.listarPorEquipamento(idEquipamento);
    }

    public List<Avaria> buscarTodos() throws Exception {
        return avariaDAO.listarTodas();
    }

    public List<Avaria> buscarTodos(String filtro) throws Exception {
        // Implementação do filtro pode ser feita no próprio serviço
        List<Avaria> todas = avariaDAO.listarTodas();
        if (filtro == null || filtro.trim().isEmpty()) {
            return todas;
        }

        return todas.stream()
                .filter(avaria -> avaria.getDescricao().toLowerCase().contains(filtro.toLowerCase()) ||
                        avaria.getIdEquipamento().toLowerCase().contains(filtro.toLowerCase()))
                .toList();
    }
}
