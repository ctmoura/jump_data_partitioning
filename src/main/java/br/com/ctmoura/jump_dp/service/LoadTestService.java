package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.slf4j.MDC;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoadTestService {

    private final DataSource dataSource;

    private final ExecutionLoggerService executionLoggerService;

    private Integer queryTimeout = 30000; // 30 segundos

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

    public LoadTestService(DataSource dataSource,
            ExecutionLoggerService executionLoggerService) {
        this.dataSource = dataSource;
        this.executionLoggerService = executionLoggerService;
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

    private void executeQueryComParametroUnidadeId(String query, Long unidadeId, Long qtdeUsuarios, String experimento)
            throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
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
            log.error("Ocorreu um erro ao executar query: ", e.getMessage());
        }

        // Retrieve the request ID from MDC
        String requestId = MDC.get("X-Request-ID");

        this.executionLoggerService.logExecutionTask(requestId, unidadeId, qtdeUsuarios, experimento, startTime, endTime);
    }

}
