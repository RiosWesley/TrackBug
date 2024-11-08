package trackbug.model.dao.interfaces;

import trackbug.model.entity.Emprestimo;
import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoDAO {
    void criar(Emprestimo emprestimo) throws Exception;
    void registrarDevolucao(int id, LocalDateTime dataRetorno) throws Exception;
    Emprestimo buscarPorId(int id) throws Exception;
    List<Emprestimo> listarTodos() throws Exception;
    List<Emprestimo> listarAtivos() throws Exception;
    List<Emprestimo> listarAtrasados() throws Exception;
    List<Emprestimo> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) throws Exception;
    List<Emprestimo> buscarPorFuncionario(String idFuncionario) throws Exception;
    List<Emprestimo> buscarPorEquipamento(String idEquipamento) throws Exception;
    void atualizar(Emprestimo emprestimo) throws Exception;
}