package trackbug.model.entity;

import java.time.LocalDateTime;

public class LogEquipamento {
    private Integer id;
    private String idEquipamento;
    private String descricao;
    private String acao;
    private LocalDateTime dataAcao;
    private String detalhes;

    // Construtores
    public LogEquipamento() {}

    public LogEquipamento(String idEquipamento, String descricao, String acao, String detalhes) {
        this.idEquipamento = idEquipamento;
        this.descricao = descricao;
        this.acao = acao;
        this.detalhes = detalhes;
        this.dataAcao = LocalDateTime.now();
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(String idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }
}