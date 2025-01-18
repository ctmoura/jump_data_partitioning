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
        log.trace("Iniciando execução do teste.");
        LocalDateTime startTime = LocalDateTime.now();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT\n" + //
                                "    p.\"NPU\", p.\"processoID\", p.\"ultimaAtualizacao\",\n" + //
                                "  \tc.descricao AS classe, a.descricao AS assunto,\n" + //
                                "  \tm.activity, m.\"dataInicio\", m.\"dataFinal\", m.\"usuarioID\",\n" + //
                                "    m.duration, m.\"movimentoID\", com.descricao AS complemento,\n" + //
                                "  \ts.\"nomeServidor\", s.\"tipoServidor\", d.tipo AS documento\n" + //
                                "FROM\n" + //
                                "\t\tmovimentos_18006 AS m\n" + //
                                "INNER JOIN\n" + //
                                "    processos_18006 AS p ON p.\"processoID\" = m.\"processoID\"\n" + //
                                "INNER JOIN\n" + //
                                "    classes AS c ON p.classe = c.id\n" + //
                                "LEFT JOIN\n" + //
                                "    assuntos AS a ON p.assunto = a.id\n" + //
                                "LEFT JOIN\n" + //
                                "    complementos_18006 AS com ON com.\"movimentoID\" = m.id\n" + //
                                "LEFT JOIN\n" + //
                                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                                "LEFT JOIN\n" + //
                                "\t\tdocumentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                                "WHERE '2000-01-01' <= p.\"dataPrimeiroMovimento\"\n" + //
                                "ORDER BY \"processoID\", \"dataFinal\";";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                log.trace("Executando a query.");
                try (ResultSet resultSet = statement.executeQuery()) {
                    log.trace("Iniciando leitura do resultset.");
                    while (resultSet.next()) {
                        // Processar os resultados da consulta
                    }
                    log.trace("Final da leitura do resultset.");
                }
            }
        } catch (SQLException e) {
            // Lidar com exceções
            log.error("Ocorreu um erro ao executar query. ", e.getMessage());
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Duration: {} milliseconds.", startTime.until(endTime, ChronoUnit.MILLIS));
    }
}
