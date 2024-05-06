package br.com.ctmoura.jump_dp.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctmoura.jump_dp.service.LoadTestService;

@RestController
public class LoadTestController {

    @Autowired
    private LoadTestService loadTestService;

    @GetMapping("/load-test")
    public String executeLoadTest() throws SQLException {
        // Executar o teste de carga aqui
        // Por exemplo, pode ser uma chamada a um serviço que realiza a consulta SQL
        this.loadTestService.executeQuery();
        // Retornar uma mensagem indicando que o teste foi concluído
        return "Teste de carga concluído!";
    }
    
}
