package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoadTestService {
    
    @Autowired
    private DataSource dataSource;

    public void executeQuery() throws SQLException {
        LocalDateTime startTime = LocalDateTime.now();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT " +
                            "p.\"NPU\", p.\"processoID\", \"dataPrimeiroMovimento\", " +
                            "  c.descricao AS classe, a.descricao AS assunto, " +
                            "  activity, \"dataInicio\", \"dataFinal\", \"usuarioID\", \"movimentoID\", duration " +
                            "  \"nomeServidor\", \"tipoServidor\" " +
                            "FROM " +
                            "processos_18006 AS p " +
                            "INNER JOIN " +
                            "movimentos_18006 AS m ON p.\"processoID\" = m.\"processoID\" " +
                            "INNER JOIN " +
                            "classes AS c ON p.classe = c.id " +
                            "INNER JOIN " +
                            "assuntos AS a ON p.assunto = a.id " +
                            "INNER JOIN " +
                            "servidores ON m.\"usuarioID\" = servidores.\"servidorID\" " +
                            "ORDER BY \"processoID\", \"dataFinal\" DESC;";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Processar os resultados da consulta
                    }
                }
            }
        } catch (SQLException e) {
            // Lidar com exceções
            log.error("Ocorreu um erro ao executar query. ", e.getMessage());
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Duration: {} seconds.", startTime.until(endTime, ChronoUnit.SECONDS));
    }
}
