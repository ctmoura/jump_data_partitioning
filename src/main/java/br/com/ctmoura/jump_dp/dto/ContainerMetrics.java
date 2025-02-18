package br.com.ctmoura.jump_dp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContainerMetrics {
    private Boolean success;
    private Long ioBytesRead;
    private Long ioBytesWrite;
    private Double cpuUsage;
    private Double memUsage;
}
