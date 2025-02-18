package br.com.ctmoura.jump_dp.service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.ctmoura.jump_dp.dto.ContainerMetrics;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExecutionLoggerService {

    private final DatabaseStatsService databaseStatsService;
    private final DockerMetricsService dockerMetricsService;

    private static boolean ESCREVEU_HEADER = false;

    private static NumberFormat formatador = NumberFormat.getInstance(new Locale("pt", "BR"));

    static {
        formatador.setMinimumFractionDigits(2);
        formatador.setMaximumFractionDigits(2);
    }

    public ExecutionLoggerService(
            DatabaseStatsService databaseStatsService,
            DockerMetricsService dockerMetricsService) {
        super();
        this.databaseStatsService = databaseStatsService;
        this.dockerMetricsService = dockerMetricsService;NumberFormat.getInstance(new Locale("pt", "BR"));
        
        if (!ESCREVEU_HEADER) {
            log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                    "requestId", "experimento", "qtdeUsuarios", "unidadeId", "startTime", "executionStatus",
                    "durationInMillis", "activeConnections", "blockReadFromDisk", "blockReadFromCache",
                    "totalTempFiles", "totalTempFilesSize", "ioRead", "ioWrite", "cacheHitRatio", "cpuUsage",
                    "memUsage");
                    ExecutionLoggerService.ESCREVEU_HEADER = true;
        }
    }

    @Async
    public void logExecutionTask(String requestId, Long unidadeId, Long qtdeUsuarios, String experimento,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        Map<String, Object> dbStats = this.databaseStatsService.getDatabaseStats();
        ContainerMetrics containerMetrics = this.dockerMetricsService.getContainerMetrics();
        // requestId, experimento, qtdeUsuarios, unidadeId, startTime, executionStatus,
        // durationInMillis, activeConnections, blockReadFromDisk, blockReadFromCache,
        // totalTempFiles, totalTempFilesSize, ioRead, ioWrite, cacheHitRatio, cpuUsage,
        // memUsage
        log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, \"{}\", \"{}\", \"{}\"",
                requestId,
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
                containerMetrics.getIoBytesRead(),
                containerMetrics.getIoBytesWrite(),
                formatador.format(dbStats.getOrDefault("cacheHitRatio", "0.00")),
                formatador.format(containerMetrics.getCpuUsage()),
                formatador.format(containerMetrics.getMemUsage()));
    }

}
