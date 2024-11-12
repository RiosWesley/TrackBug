// src/main/java/trackbug/model/entity/Avaria.java
package trackbug.model.entity;

import java.time.LocalDateTime;

public class Avaria {
    private Integer id;
    private String idEquipamento;
    private Integer quantidade;
    private String descricao;
    private LocalDateTime data;

    // Construtores
    public Avaria() {}

    public Avaria(String idEquipamento, Integer quantidade, String descricao) {
        this.idEquipamento = idEquipamento;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.data = LocalDateTime.now();
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}