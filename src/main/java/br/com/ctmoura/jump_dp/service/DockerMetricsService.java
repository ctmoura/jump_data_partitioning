package br.com.ctmoura.jump_dp.service;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import br.com.ctmoura.jump_dp.dto.ContainerMetrics;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.unixdomainsockets.UnixDomainSocketFactory;

@Service
public class DockerMetricsService {

    private static final String CONTAINER_ID = "jump_data_partitioning-postgres-1"; // Nome ou ID do container
    private static final String DOCKER_API_URL = "http://localhost/containers/" + CONTAINER_ID + "/stats?stream=false";

    private final OkHttpClient client;

    public DockerMetricsService() {
        // Configura o OkHttp para usar Unix Domain Socket
        this.client = new OkHttpClient.Builder()
                .socketFactory(new UnixDomainSocketFactory(new File("/var/run/docker.sock")))
                .build();
    }

    public ContainerMetrics getContainerMetrics() {
        Request request = new Request.Builder()
                .url(DOCKER_API_URL) // O OkHttp entende que deve usar o Unix Socket
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return ContainerMetrics.builder().success(false).build();
            }
            // Parseando JSON para extrair CPU e Memória
            JSONObject json = new JSONObject(response.body().string());
            JSONObject cpuStats = json.getJSONObject("cpu_stats");
            JSONObject preCpuStats = json.getJSONObject("precpu_stats");
            JSONObject memoryStats = json.getJSONObject("memory_stats");

            // Pegando uso de CPU
            long cpuDelta = cpuStats.getJSONObject("cpu_usage").getLong("total_usage") 
                    - preCpuStats.getJSONObject("cpu_usage").getLong("total_usage");
            long systemCpuDelta = cpuStats.getLong("system_cpu_usage") - preCpuStats.getLong("system_cpu_usage");
            long numberCpus = cpuStats.getLong("online_cpus");
            double cpuUsage = (cpuDelta / (double) systemCpuDelta) * numberCpus * 100.0;

            // Pegando uso de memória
            long memoryUsage = memoryStats.getLong("usage");
            long memoryLimit = memoryStats.getLong("limit");
            double memoryPercent = (memoryUsage / (double) memoryLimit) * 100;

            JSONObject blkioStats = json.getJSONObject("blkio_stats");

            JSONArray ioStats = blkioStats.getJSONArray("io_service_bytes_recursive");

            long readBytes = 0;
            long writeBytes = 0;

            for (int i = 0; i < ioStats.length(); i++) {
                JSONObject ioStat = ioStats.getJSONObject(i);
                String op = ioStat.getString("op");
                long value = ioStat.getLong("value");

                if ("read".equals(op)) {
                    readBytes += value;
                } else if ("write".equals(op)) {
                    writeBytes += value;
                }
            }

            return ContainerMetrics.builder().success(true).cpuUsage(cpuUsage).memUsage(memoryPercent)
                    .ioBytesRead(readBytes).ioBytesWrite(writeBytes).build();
        } catch (IOException e) {
            return ContainerMetrics.builder().success(false).build();
        }
    }
}