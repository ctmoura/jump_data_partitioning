# jump_data_partitioning
Projeto elaborado para implementação e experimentação das estratégias de particionamento de dados aplicadas ao JuMP.

## Ambiente utilizado para os testes

Para o experimento está sendo utilizada uma infraestrutura de containers com Docker.

### Equipamento Host

- Macbook PRO
- 2 GHz Quad-Core Intel Core i5
- 16 GB 3733 MHz LPDDR4X
- SSD 1TB

### Docker Resources

deploy:
  resources:
    limits:
      cpus: "2.0"
      memory: 3G