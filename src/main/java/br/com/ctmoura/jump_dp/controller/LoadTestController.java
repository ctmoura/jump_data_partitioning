package br.com.ctmoura.jump_dp.controller;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctmoura.jump_dp.service.LoadTestService;

@RestController
public class LoadTestController {

    private final LoadTestService loadTestService;

    public LoadTestController(LoadTestService loadTestService) {
        this.loadTestService = loadTestService;
    }

    @GetMapping("/load-test")
    public String executeLoadTest() throws SQLException {
        Instant start = Instant.now();
        // Executar o teste de carga aqui
        // Por exemplo, pode ser uma chamada a um serviço que realiza a consulta SQL
        this.loadTestService.executeQuery();
        // Retornar uma mensagem indicando que o teste foi concluído
        return String.format("Duration: %s",
                Duration.between(start, Instant.now()).toMillis());
    }

}
