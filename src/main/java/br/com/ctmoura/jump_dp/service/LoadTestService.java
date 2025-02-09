package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static final String QUERY_EXP_00_E_01 = "SELECT\n" + //
                "    p.\"NPU\", p.\"processoID\", p.\"ultimaAtualizacao\",\n" + //
                "  \tc.descricao AS classe, a.descricao AS assunto,\n" + //
                "  \tm.activity, m.\"dataInicio\", m.\"dataFinal\", m.\"usuarioID\",\n" + //
                "    m.duration, m.\"movimentoID\", com.descricao AS complemento,\n" + //
                "  \ts.\"nomeServidor\", s.\"tipoServidor\", d.tipo AS documento\n" + //
                "FROM \n" + //
                "\tprocessos_18006 AS p\n" + //
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
                "WHERE p.\"dataPrimeiroMovimento\" BETWEEN CURRENT_TIMESTAMP - interval '5 years' AND CURRENT_TIMESTAMP\n" + //
                "ORDER BY \"processoID\", \"dataFinal\";";

    public static final String QUERY_EXP_01 = "EXPLAIN ANALYSE\n" + //
                "SELECT\n" + //
                "    p.\"NPU\", \n" + //
                "    p.\"processoID\", \n" + //
                "    p.\"ultimaAtualizacao\",\n" + //
                "    c.descricao AS classe, \n" + //
                "    a.descricao AS assunto,\n" + //
                "    m.activity, \n" + //
                "    m.\"dataInicio\", \n" + //
                "    m.\"dataFinal\", \n" + //
                "    m.\"usuarioID\",\n" + //
                "    m.duration, \n" + //
                "    m.\"movimentoID\", \n" + //
                "    com.descricao AS complemento,\n" + //
                "    s.\"nomeServidor\", \n" + //
                "    s.\"tipoServidor\", \n" + //
                "    d.tipo AS documento\n" + //
                "FROM \n" + //
                "    processos_exp01 AS p\n" + //
                "INNER JOIN\n" + //
                "    movimentos_exp01 AS m \n" + //
                "    ON m.\"processoID\" = p.\"processoID\"\n" + //
                "INNER JOIN\n" + //
                "    classes AS c ON p.classe = c.id\n" + //
                "LEFT JOIN\n" + //
                "    assuntos AS a ON p.assunto = a.id\n" + //
                "LEFT JOIN\n" + //
                "    complementos_exp01 AS com \n" + //
                "    ON com.\"movimentoID\" = m.\"id\" \n" + //
                "LEFT JOIN\n" + //
                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                "LEFT JOIN\n" + //
                "    documentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                "WHERE \n" + //
                "    p.\"dataPrimeiroMovimento\" >= '2020-01-01'AND p.\"unidadeID\" = ? \n" + //
                "\tAND m.\"dataPrimeiroMovimento\" >= '2020-01-01' AND m.\"unidadeID\" = ?\n" + //
                "\tAND com.\"dataPrimeiroMovimento\" >= '2020-01-01' AND com.\"unidadeID\" = ?\n" + //
                "ORDER BY \n" + //
                "    p.\"processoID\", m.\"dataFinal\";";

    public static final String QUERY_EXP_02 = "SELECT\n" + //
                "    p.\"NPU\", \n" + //
                "    p.\"processoID\", \n" + //
                "    p.\"ultimaAtualizacao\",\n" + //
                "    c.descricao AS classe, \n" + //
                "    a.descricao AS assunto,\n" + //
                "    m.activity, \n" + //
                "    m.\"dataInicio\", \n" + //
                "    m.\"dataFinal\", \n" + //
                "    m.\"usuarioID\",\n" + //
                "    m.duration, \n" + //
                "    m.\"movimentoID\", \n" + //
                "    com.descricao AS complemento,\n" + //
                "    s.\"nomeServidor\", \n" + //
                "    s.\"tipoServidor\", \n" + //
                "    d.tipo AS documento\n" + //
                "FROM \n" + //
                "    processos_exp02 AS p\n" + //
                "INNER JOIN\n" + //
                "    movimentos_exp02 AS m \n" + //
                "    ON \n" + //
                "\tm.\"anoPrimeiroMovimento\" = p.\"anoPrimeiroMovimento\"\n" + //
                "\tAND m.\"unidadeID\" = p.\"unidadeID\"\n" + //
                "\tAND m.\"processoID\" = p.\"processoID\"\n" + //
                "INNER JOIN\n" + //
                "    classes AS c ON p.classe = c.id\n" + //
                "LEFT JOIN\n" + //
                "    assuntos AS a ON p.assunto = a.id\n" + //
                "LEFT JOIN\n" + //
                "    complementos_exp02 AS com \n" + //
                "    ON \n" + //
                "    com.\"anoPrimeiroMovimento\" = p.\"anoPrimeiroMovimento\"\n" + //
                "\tAND com.\"unidadeID\" = m.\"unidadeID\" \n" + //
                "\tAND com.\"movimentoID\" = m.\"id\" \n" + //
                "LEFT JOIN\n" + //
                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                "LEFT JOIN\n" + //
                "    documentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                "WHERE \n" + //
                "    p.\"anoPrimeiroMovimento\" >= 2020 AND p.\"unidadeID\" = ? \n" + //
                "\tAND m.\"anoPrimeiroMovimento\" >= 2020 AND m.\"unidadeID\" = ?\n" + //
                "\tAND com.\"anoPrimeiroMovimento\" >= 2020 AND com.\"unidadeID\" = ?\n" + //
                "ORDER BY \n" + //
                "    p.\"processoID\", m.\"dataFinal\";";

    public static final String QUERY_EXP_03 = "SELECT\n" + //
                "    p.\"NPU\", \n" + //
                "    p.\"processoID\", \n" + //
                "    p.\"ultimaAtualizacao\",\n" + //
                "    c.descricao AS classe, \n" + //
                "    a.descricao AS assunto,\n" + //
                "    m.activity, \n" + //
                "    m.\"dataInicio\", \n" + //
                "    m.\"dataFinal\", \n" + //
                "    m.\"usuarioID\",\n" + //
                "    m.duration, \n" + //
                "    m.\"movimentoID\", \n" + //
                "    com.descricao AS complemento,\n" + //
                "    s.\"nomeServidor\", \n" + //
                "    s.\"tipoServidor\", \n" + //
                "    d.tipo AS documento\n" + //
                "FROM \n" + //
                "    processos_exp03 AS p\n" + //
                "INNER JOIN\n" + //
                "    movimentos_exp03 AS m \n" + //
                "    ON \n" + //
                "m.\"anoPrimeiroMovimento\" = p.\"anoPrimeiroMovimento\"\n" + //
                "AND m.\"unidadeID\" = p.\"unidadeID\"\n" + //
                "AND m.\"processoID\" = p.\"processoID\"\n" + //
                "INNER JOIN\n" + //
                "    classes AS c ON p.classe = c.id\n" + //
                "LEFT JOIN\n" + //
                "    assuntos AS a ON p.assunto = a.id\n" + //
                "LEFT JOIN\n" + //
                "    complementos_exp03 AS com \n" + //
                "    ON \n" + //
                "    com.\"anoPrimeiroMovimento\" = p.\"anoPrimeiroMovimento\"\n" + //
                "AND com.\"unidadeID\" = m.\"unidadeID\" \n" + //
                "AND com.\"movimentoID\" = m.\"id\" \n" + //
                "LEFT JOIN\n" + //
                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                "LEFT JOIN\n" + //
                "    documentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                "WHERE \n" + //
                "    p.\"anoPrimeiroMovimento\" >= 2020 AND p.\"unidadeID\" = ? \n" + //
                "AND m.\"anoPrimeiroMovimento\" >= 2020 AND m.\"unidadeID\" = ?\n" + //
                "AND com.\"anoPrimeiroMovimento\" >= 2020 AND com.\"unidadeID\" = ?\n" + //
                "ORDER BY \n" + //
                "    p.\"processoID\", m.\"dataFinal\";";

    public LoadTestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeQueryExp00e01() throws Exception {
        this.executeQuery(QUERY_EXP_00_E_01);
    }

    public void executeQueryExp01(Long unidadeId) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_01, unidadeId);
    }

    public void executeQueryExp02(Long unidadeId) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_02, unidadeId);
    }

    public void executeQueryExp03(Long unidadeId) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_03, unidadeId);
    }

    private void executeQueryComParametroUnidadeId(String query, Long unidadeId) throws Exception {
        log.trace("Iniciando execução do teste.");
        LocalDateTime startTime = LocalDateTime.now();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                log.trace("Executando a query.");
                statement.setLong(1, unidadeId);
                statement.setLong(2, unidadeId);
                statement.setLong(3, unidadeId);
                statement.setQueryTimeout(this.queryTimeout / 1000);
                try (ResultSet resultSet = statement.executeQuery()) {
                    log.trace("Query executada com sucesso.");
                }
                
            }
        } catch (Exception e) {
            // Lidar com exceções
            log.error("Ocorreu um erro ao executar query. ", e.getMessage());
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("{}, {}", startTime, startTime.until(endTime, ChronoUnit.MILLIS));
    }

    private void executeQuery(String query) throws Exception {
        log.trace("Iniciando execução do teste.");
        LocalDateTime startTime = LocalDateTime.now();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                log.trace("Executando a query.");
                statement.setQueryTimeout(this.queryTimeout / 1000);
                try (ResultSet resultSet = statement.executeQuery()) {
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
