package trackbug.model.entity;

public class RegistroAvaria extends Avaria {
    private static final String[] NIVEIS_GRAVIDADE = {
            "Baixa", "Média", "Alta", "Crítica"
    };

    private static final String[] STATUS_AVARIA = {
            "Pendente", "Em Análise", "Resolvida", "Descartada"
    };

    private String responsavel;
    private String solucao;
    private String observacoes;
    private double custoReparo;

    public RegistroAvaria() {
        super();
        setStatus(STATUS_AVARIA[0]); // Status inicial como "Pendente"
    }

    // Getters e Setters específicos
    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public double getCustoReparo() {
        return custoReparo;
    }

    public void setCustoReparo(double custoReparo) {
        this.custoReparo = custoReparo;
    }

    // Métodos utilitários
    public static String[] getNiveisGravidade() {
        return NIVEIS_GRAVIDADE;
    }

    public static String[] getStatusAvaria() {
        return STATUS_AVARIA;
    }

    public boolean isResolvida() {
        return STATUS_AVARIA[2].equals(getStatus());
    }

    public boolean isDescartada() {
        return STATUS_AVARIA[3].equals(getStatus());
    }

    public boolean requerAtencaoImediata() {
        return NIVEIS_GRAVIDADE[2].equals(getGravidade()) ||
                NIVEIS_GRAVIDADE[3].equals(getGravidade());
    }
}