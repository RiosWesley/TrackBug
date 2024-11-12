package trackbug.model.entity;

import java.time.LocalDateTime;

public class RegistroAvaria {
    private int id;
    private String idEquipamento;
    private int quantidade;
    private String descricao;
    private LocalDateTime data;

    // Getters e Setters atualizados
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIdEquipamento() { return idEquipamento; }
    public void setIdEquipamento(String idEquipamento) { this.idEquipamento = idEquipamento; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}