package trackbug.model.entity;

import java.security.Timestamp;

public class LogEquipamento {
    private final int id;
    private final String idEquipamento;
    private final String descricao;
    private final String acao;
    private final Timestamp dataAcao;
    private final String detalhes;

    // Setters


    public LogEquipamento(String acao, int id, String idEquipamento, String descricao, Timestamp dataAcao, String detalhes) {
        this.acao = acao;
        this.id = id;
        this.idEquipamento = idEquipamento;
        this.descricao = descricao;
        this.dataAcao = dataAcao;
        this.detalhes = detalhes;
    }

    // Getters
    public int getId() { return id; }

    public String getIdEquipamento() { return idEquipamento; }
    public String getDescricao() { return descricao; }
    public String getAcao() { return acao; }
    public Timestamp getDataAcao() { return dataAcao; }
    public String getDetalhes() { return detalhes; }



}