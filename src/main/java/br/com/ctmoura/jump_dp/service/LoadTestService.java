package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoadTestService {

    private final DataSource dataSource;

    @Value("${spring.datasource.hikari.data-source-properties.socketTimeout}")
    private Integer queryTimeout;

    public LoadTestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeQuery() throws Exception {
        log.trace("Iniciando execução do teste.");
        LocalDateTime startTime = LocalDateTime.now();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT\n" + //
                                "    p.\"NPU\", p.\"processoID\", p.\"ultimaAtualizacao\",\n" + //
                                "  \tc.descricao AS classe, a.descricao AS assunto,\n" + //
                                "  \tm.activity, m.\"dataInicio\", m.\"dataFinal\", m.\"usuarioID\",\n" + //
                                "    m.duration, m.\"movimentoID\", com.descricao AS complemento,\n" + //
                                "  \ts.\"nomeServidor\", s.\"tipoServidor\", d.tipo AS documento\n" + //
                                "FROM \n" + //
                                "\tprocessos_particionada_18006 AS p\n" + //
                                "INNER JOIN\n" + //
                                "    movimentos_18006 AS m ON m.\"processoID\" = p.\"processoID\"\n" + //
                                "INNER JOIN\n" + //
                                "    classes AS c ON p.classe = c.id\n" + //
                                "LEFT JOIN\n" + //
                                "    assuntos AS a ON p.assunto = a.id\n" + //
                                "LEFT JOIN\n" + //
                                "    complementos_18006 AS com ON com.\"movimentoID\" = m.id\n" + //
                                "LEFT JOIN\n" + //
                                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                                "LEFT JOIN\n" + //
                                "\tdocumentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                                "WHERE '2000-01-01' <= p.\"dataPrimeiroMovimento\"\n" + //
                                "ORDER BY \"processoID\", \"dataFinal\";";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                log.trace("Executando a query.");
                statement.setQueryTimeout(this.queryTimeout / 1000);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // log.trace("Iniciando leitura do resultset.");
                    // resultSet.
                    // while (resultSet.next()) {
                    //     // Processar os resultados da consulta
                    // }
                    log.trace("Query executada com sucesso.");
                }
                
            }
        } catch (Exception e) {
            // Lidar com exceções
            log.error("Ocorreu um erro ao executar query. ", e.getMessage());
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Duration: {}", startTime.until(endTime, ChronoUnit.MILLIS));
    }
}
