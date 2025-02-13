package br.com.ctmoura.jump_dp.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctmoura.jump_dp.service.LoadTestService;

@RestController
public class LoadTestController {

    private final LoadTestService loadTestService;

    public LoadTestController(LoadTestService loadTestService) {
        this.loadTestService = loadTestService;
    }

    @GetMapping("/reset-stats")
    public String executeResetStats() {
        boolean success = false;
        try {
            this.loadTestService.executeResetStats();
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Estatísticas resetadas: %s", success);
    }

    @GetMapping("/load-test-exp00")
    public String executeLoadTestExp00(@RequestParam Long origemId, @RequestParam Long qtdeUsuarios) {
        Instant start = Instant.now();
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp00(origemId, qtdeUsuarios);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

    @GetMapping("/load-test-exp01")
    public String executeLoadTestExp01(@RequestParam Long origemId, @RequestParam Long qtdeUsuarios) {
        Instant start = Instant.now();
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp01(origemId, qtdeUsuarios);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

    @GetMapping("/load-test-exp02")
    public String executeLoadTestExp02(@RequestParam Long origemId, @RequestParam Long qtdeUsuarios) {
        Instant start = Instant.now();
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp02(origemId, qtdeUsuarios);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

    @GetMapping("/load-test-exp03")
    public String executeLoadTestExp03(@RequestParam Long origemId, @RequestParam Long qtdeUsuarios) {
        Instant start = Instant.now();
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp03(origemId, qtdeUsuarios);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

    @GetMapping("/load-test-exp04")
    public String executeLoadTestExp04(@RequestParam Long origemId, @RequestParam Long qtdeUsuarios) {
        Instant start = Instant.now();
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp04(origemId, qtdeUsuarios);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

}
