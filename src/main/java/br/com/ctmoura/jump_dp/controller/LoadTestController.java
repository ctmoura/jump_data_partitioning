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

    @GetMapping("/load-test")
    public String executeLoadTest() {
        Instant start = Instant.now();
        // Executar o teste de carga aqui
        // Por exemplo, pode ser uma chamada a um serviço que realiza a consulta SQL
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp00e01();
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

    @GetMapping("/load-test-exp02")
    public String executeLoadTestExp02(@RequestParam Long origemId) {
        Instant start = Instant.now();
        // Executar o teste de carga aqui
        // Por exemplo, pode ser uma chamada a um serviço que realiza a consulta SQL
        boolean success = false;
        try {
            this.loadTestService.executeQueryExp02(origemId);
            success = true;
        } catch (Exception e) {}
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Sucesso: %s, Duration: %s",
                success, Duration.between(start, Instant.now()).toMillis());
    }

}
