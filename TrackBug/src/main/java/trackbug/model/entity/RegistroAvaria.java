package trackbug.model.entity;
import java.time.LocalDateTime;


    public class RegistroAvaria {
        private int id;
        private String idEquipamento;
        private int quantidade;
        private String descricao;
        private LocalDateTime data;

        // Getters atualizados
        public int getId() {
            return id;
        }

        public LocalDateTime getData() {
            return data;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getIdEquipamento() {
            return idEquipamento;
        }

        public int getQuantidade() {
            return quantidade;
        }

        // Setters atualizados
        public void setData(LocalDateTime data) {
            this.data = data;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setIdEquipamento(String idEquipamento) {
            this.idEquipamento = idEquipamento;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }