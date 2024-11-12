// src/main/java/trackbug/controller/HistoricoAvariasController.java
package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import trackbug.model.entity.Avaria;
import trackbug.model.service.AvariaService;
import trackbug.model.service.EquipamentoService;
import trackbug.util.AlertHelper;
import trackbug.util.DateUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoricoAvariasController implements Initializable {
    @FXML private TextField pesquisaField;
    @FXML private TableView<Avaria> tabelaAvarias;
    @FXML private TableColumn<Avaria, String> colunaData;
    @FXML private TableColumn<Avaria, String> colunaEquipamento;
    @FXML private TableColumn<Avaria, Integer> colunaQuantidade;
    @FXML private TableColumn<Avaria, String> colunaDescricao;
    @FXML private Label totalRegistrosLabel;

    private final AvariaService avariaService;
    private final EquipamentoService equipamentoService;
    private ObservableList<Avaria> avarias;

    public HistoricoAvariasController() {
        this.avariaService = new AvariaService();
        this.equipamentoService = new EquipamentoService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarPesquisa();
        carregarAvarias();
    }

    private void configurarColunas() {
        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarDataHora(data.getValue().getData())
                )
        );

        colunaEquipamento.setCellValueFactory(data -> {
            try {
                String nomeEquipamento = equipamentoService.buscarNomePorId(
                        data.getValue().getIdEquipamento()
                );
                return new javafx.beans.property.SimpleStringProperty(nomeEquipamento);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty(
                        "Erro ao carregar equipamento"
                );
            }
        });

        colunaQuantidade.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidade()
                ).asObject()
        );

        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDescricao()
                )
        );

        // Configurar estilos das colunas
        colunaData.getStyleClass().add("data");
        colunaQuantidade.getStyleClass().add("quantidade");
    }

    private void configurarPesquisa() {
        pesquisaField.textProperty().addListener((obs, old, novo) -> {
            if (avarias != null) {
                FilteredList<Avaria> dadosFiltrados = new FilteredList<>(avarias);
                dadosFiltrados.setPredicate(avaria -> {
                    if (novo == null || novo.isEmpty()) {
                        return true;
                    }

                    String filtroLowerCase = novo.toLowerCase();
                    try {
                        String nomeEquipamento = equipamentoService.buscarNomePorId(
                                avaria.getIdEquipamento()
                        ).toLowerCase();

                        return nomeEquipamento.contains(filtroLowerCase) ||
                                avaria.getDescricao().toLowerCase().contains(filtroLowerCase);
                    } catch (Exception e) {
                        return false;
                    }
                });

                tabelaAvarias.setItems(dadosFiltrados);
                atualizarTotalRegistros();
            }
        });
    }

    @FXML
    private void atualizarLista() {
        pesquisaField.clear();
        carregarAvarias();
    }

    private void carregarAvarias() {
        try {
            avarias = FXCollections.observableArrayList(avariaService.listarTodas());
            tabelaAvarias.setItems(avarias);
            atualizarTotalRegistros();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar avarias", e.getMessage());
        }
    }

    private void atualizarTotalRegistros() {
        int total = tabelaAvarias.getItems().size();
        totalRegistrosLabel.setText(String.format("Total de registros: %d", total));
    }
}