package trackbug.model.service;

import trackbug.model.dao.impl.EmprestimoDAOImpl;
import trackbug.model.dao.impl.EquipamentoDAOImpl;
import trackbug.model.dao.impl.FuncionarioDAOImpl;
import trackbug.model.entity.Emprestimo;
import trackbug.model.entity.Equipamento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardService {
    private final EmprestimoService emprestimoService;
    private final EquipamentoService equipamentoService;
    private final FuncionarioService funcionarioService;
    private final DateTimeFormatter dateFormatter;

    public DashboardService() {
        this.emprestimoService = new EmprestimoService();
        this.equipamentoService = new EquipamentoService();
        this.funcionarioService = new FuncionarioService();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public Map<String, Object> getDashboardMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        // Buscar dados
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> emprestimosAtrasados = emprestimoService.listarEmprestimosAtrasados();
        List<Equipamento> equipamentos = equipamentoService.listarTodos();

        // Calcular métricas (convertendo para Long)
        metrics.put("emprestimosAtivos", (long) emprestimosAtivos.size());
        metrics.put("equipamentosCadastrados", (long) equipamentos.size());
        metrics.put("devolucoesPendentes", (long) emprestimosAtrasados.size());

        // Funcionários com empréstimos ativos
        long funcionariosComEmprestimos = emprestimosAtivos.stream()
                .map(Emprestimo::getIdFuncionario)
                .distinct()
                .count();
        metrics.put("funcionariosComEmprestimos", funcionariosComEmprestimos);

        // Equipamentos com estoque baixo
        long equipamentosBaixoEstoque = equipamentos.stream()
                .filter(e -> e.getQuantidadeAtual() <= e.getQuantidadeMinima())
                .count();
        metrics.put("equipamentosBaixoEstoque", equipamentosBaixoEstoque);

        // Calcular tendências
        metrics.put("tendencias", calcularTendencias());

        return metrics;
    }

    public List<Map<String, Object>> getEmprestimosRecentes() throws Exception {
        return emprestimoService.listarEmprestimosAtivos().stream()
                .limit(5)
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getEmprestimosPorMes() throws Exception {
        LocalDateTime dataInicial = LocalDateTime.now().minusMonths(6);
        LocalDateTime dataFinal = LocalDateTime.now();

        List<Emprestimo> emprestimos = emprestimoService.buscarPorPeriodo(dataInicial, dataFinal);

        Map<String, Long> emprestimosPorMes = emprestimos.stream()
                .collect(Collectors.groupingBy(
                        emp -> emp.getDataSaida().getMonth().toString(),
                        Collectors.counting()
                ));

        return emprestimosPorMes.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("mes", entry.getKey());
                    data.put("quantidade", entry.getValue());
                    return data;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Double> calcularTendencias() throws Exception {
        Map<String, Double> tendencias = new HashMap<>();

        LocalDateTime mesAtual = LocalDateTime.now();
        LocalDateTime mesAnterior = mesAtual.minusMonths(1);

        List<Emprestimo> emprestimesMesAtual = emprestimoService.buscarPorPeriodo(
                mesAtual.withDayOfMonth(1),
                mesAtual
        );

        List<Emprestimo> emprestimosMesAnterior = emprestimoService.buscarPorPeriodo(
                mesAnterior.withDayOfMonth(1),
                mesAnterior.withDayOfMonth(mesAnterior.getMonth().length(mesAnterior.toLocalDate().isLeapYear()))
        );

        double variacaoEmprestimos = calcularVariacaoPercentual(
                emprestimosMesAnterior.size(),
                emprestimesMesAtual.size()
        );

        tendencias.put("emprestimos", variacaoEmprestimos);

        return tendencias;
    }

    private Map<String, Object> convertToMap(Emprestimo emprestimo) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("funcionario", emprestimo.getNomeFuncionario());
            map.put("equipamento", emprestimo.getDescricaoEquipamento());
            map.put("dataPrevista", emprestimo.getDataRetornoPrevista().format(dateFormatter));
            map.put("status", LocalDateTime.now().isAfter(emprestimo.getDataRetornoPrevista()) ?
                    "Atrasado" : "Em dia");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private double calcularVariacaoPercentual(int valorAnterior, int valorAtual) {
        if (valorAnterior == 0) return 100.0;
        return ((double)(valorAtual - valorAnterior) / valorAnterior) * 100;
    }
}