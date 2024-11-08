package trackbug.model.service;

import trackbug.model.dao.interfaces.AvariaDAO;
import trackbug.model.dao.impl.AvariaDAOImpl;
import trackbug.model.entity.Avaria;
import trackbug.model.entity.RegistroAvaria;
import java.util.List;
import java.util.stream.Collectors;

public class AvariaService {
    private final AvariaDAO avariaDAO;

    public AvariaService() {
        this.avariaDAO = new AvariaDAOImpl();
    }

    public List<RegistroAvaria> buscarTodos(String filtro) throws Exception {
        List<Avaria> avarias = avariaDAO.listarTodas();

        // Converte Avaria para RegistroAvaria
        return avarias.stream()
                .map(this::converterParaRegistroAvaria)
                .collect(Collectors.toList());
    }

    private RegistroAvaria converterParaRegistroAvaria(Avaria avaria) {
        RegistroAvaria registro = new RegistroAvaria();
        registro.setId(avaria.getId());
        registro.setIdEquipamento(avaria.getIdEquipamento());
        registro.setQuantidade(avaria.getQuantidade());
        registro.setDescricao(avaria.getDescricao());
        registro.setData(avaria.getData());
        registro.setGravidade(avaria.getGravidade());
        registro.setStatus(avaria.getStatus());
        return registro;
    }

    public void registrarAvaria(RegistroAvaria avaria) throws Exception {
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

    public List<RegistroAvaria> listarPorEquipamento(String idEquipamento) throws Exception {
        List<Avaria> avarias = avariaDAO.listarPorEquipamento(idEquipamento);
        return avarias.stream()
                .map(this::converterParaRegistroAvaria)
                .collect(Collectors.toList());
    }
}