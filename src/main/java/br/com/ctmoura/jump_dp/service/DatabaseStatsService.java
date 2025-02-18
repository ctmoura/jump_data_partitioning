package br.com.ctmoura.jump_dp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatabaseStatsService {
    
    private final DataSource dataSource;

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

    public DatabaseStatsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean executeResetStats() {
        boolean reset = false;
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            try (PreparedStatement statement = connection.prepareStatement(QUERY_RESET_STATS)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    log.trace("Query executada com sucesso.");
                    reset = true;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao resetar estatisticas.", e);
        }
        return reset;
    }

    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            try (PreparedStatement statement = connection.prepareStatement(QUERY_DB_STATS)) {
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
            log.error("Erro ao recuperar estatisticas", e);
        }
        return stats;
    }

    private void executeQuery(String query) throws Exception {
        
    }

}
