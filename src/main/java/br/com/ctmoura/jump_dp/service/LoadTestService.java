package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoadTestService {

    private final DataSource dataSource;

    private Integer queryTimeout = 30000; // 30 segundos

    public static final String QUERY_RESET_STATS = "SELECT pg_stat_reset();";

    public static final String QUERY_DB_STATS = "SELECT \n" + //
                                        "\tdatname, -- Name of the database\n" + //
                                        "\tnumbackends, -- Number of active connections (backends) currently connected to this database.\n" + //
                                        "\tblks_read, -- Number of disk blocks read from the file system\n" + //
                                        "\tblks_hit, -- Number of disk blocks found in cache\n" + //
                                        "\ttemp_files, -- Number of temporary files created by queries\n" + //
                                        "\ttemp_bytes, -- Total size (in bytes) of all temporary files used\n" + //
                                        "\tblk_read_time, -- Total time spent reading disk blocks (in milliseconds).\n" + //
                                        "\tblk_write_time, -- Total time spent writing disk blocks (in milliseconds).\n" + //
                                        "\t(blks_hit * 100.0 / NULLIF(blks_hit + blks_read, 0)) AS cache_hit_ratio\n" + //
                                        "FROM pg_stat_database WHERE datname = 'jumpdb';";

    public static final String QUERY_EXP_00 = "SELECT\n" + //
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
                "    processos_{unidadeID} AS p\n" + //
                "INNER JOIN\n" + //
                "    movimentos_{unidadeID} AS m \n" + //
                "    ON m.\"processoID\" = p.\"processoID\"\n" + //
                "INNER JOIN\n" + //
                "    classes AS c ON p.classe = c.id\n" + //
                "LEFT JOIN\n" + //
                "    assuntos AS a ON p.assunto = a.id\n" + //
                "LEFT JOIN\n" + //
                "    complementos_{unidadeID} AS com \n" + //
                "    ON com.\"movimentoID\" = m.\"id\" \n" + //
                "LEFT JOIN\n" + //
                "    servidores AS s ON s.\"servidorID\" = m.\"usuarioID\"\n" + //
                "LEFT JOIN\n" + //
                "    documentos AS d ON d.\"id\" = m.\"documentoID\"\n" + //
                "WHERE \n" + //
                "    p.\"dataPrimeiroMovimento\" >= '2020-01-01' \n" + //
                "ORDER BY \n" + //
                "    p.\"processoID\", m.\"dataFinal\";";

    public static final String QUERY_EXP_01 = "SELECT\n" + //
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

    public static final String QUERY_EXP_04 = "SELECT\n" + //
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

    public void executeQueryExp00(Long unidadeId, Long qtdeUsuarios) throws Exception {
        String query = QUERY_EXP_00.replaceAll("\\{unidadeID\\}+", unidadeId.toString());
        executeQueryComParametroUnidadeId(query, unidadeId, qtdeUsuarios, "Exp_00");
    }

    public void executeQueryExp01(Long unidadeId, Long qtdeUsuarios) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_01, unidadeId, qtdeUsuarios, "Exp_01");
    }

    public void executeQueryExp02(Long unidadeId, Long qtdeUsuarios) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_02, unidadeId, qtdeUsuarios, "Exp_02");
    }

    public void executeQueryExp03(Long unidadeId, Long qtdeUsuarios) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_03, unidadeId, qtdeUsuarios, "Exp_03");
    }

    public void executeQueryExp04(Long unidadeId, Long qtdeUsuarios) throws Exception {
        executeQueryComParametroUnidadeId(QUERY_EXP_04, unidadeId, qtdeUsuarios, "Exp_04");
    }

    public void executeResetStats() throws Exception {
        executeQuery(QUERY_RESET_STATS);
    }

    private void executeQueryComParametroUnidadeId(String query, Long unidadeId, Long qtdeUsuarios, String experimento) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                if (!"Exp_00".equals(experimento)) {
                    statement.setLong(1, unidadeId);
                    statement.setLong(2, unidadeId);
                    statement.setLong(3, unidadeId);
                }
                statement.setQueryTimeout(this.queryTimeout / 1000);
                try (ResultSet resultSet = statement.executeQuery()) {
                    endTime = LocalDateTime.now();
                }
            }
        } catch (Exception e) {
            // Lidar com exceções
            // log.error("Ocorreu um erro ao executar query. ", e.getMessage());
        }
        Map<String, Object> dbStats = this.getDatabaseStats();
        // experimento, qtdeUsuarios, unidadeId, startTime, executionStatus, durationInMillis, activeConnections, blockReadFromDisk, blockReadFromCache, totalTempFiles, totalTempFilesSize, ioRead, ioWrite, cacheHitRatio
        log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", 
            experimento,
            qtdeUsuarios,
            unidadeId,
            startTime, 
            endTime != null ? "SUCCESS" : "FAILED",
            (endTime != null ? startTime.until(endTime, ChronoUnit.MILLIS) : "0"),
            dbStats.getOrDefault("activeConnections", ""),
            dbStats.getOrDefault("blockReadFromDisk", ""),
            dbStats.getOrDefault("blockReadFromCache", ""),
            dbStats.getOrDefault("totalTempFiles", ""),
            dbStats.getOrDefault("totalTempFilesSize", ""),
            dbStats.getOrDefault("ioRead", ""),
            dbStats.getOrDefault("ioWrite", ""),
            dbStats.getOrDefault("cacheHitRatio", "")
        );

    }

    private void executeQuery(String query) throws Exception {
        log.trace("Iniciando execução da query.");
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
    }


    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(QUERY_DB_STATS)) {
                statement.setQueryTimeout(this.queryTimeout / 1000);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        stats.put("activeConnections", resultSet.getObject(2));
                        stats.put("blockReadFromDisk", resultSet.getObject(3));
                        stats.put("blockReadFromCache", resultSet.getObject(4));
                        stats.put("totalTempFiles", resultSet.getObject(5));
                        stats.put("totalTempFilesSize", resultSet.getObject(6));
                        stats.put("ioRead", resultSet.getObject(7));
                        stats.put("ioWrite", resultSet.getObject(8));
                        stats.put("cacheHitRatio", resultSet.getObject(9));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Lidar com exceções
        }
        return stats;
    }

}
