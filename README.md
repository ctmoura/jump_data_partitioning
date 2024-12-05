# jump_data_partitioning
Projeto elaborado para implementação e experimentação das estratégias de particionamento de dados aplicadas ao JuMP.

## Ambiente utilizado para os testes

Para o experimento está sendo utilizada uma infraestrutura de containers com Docker.

### Equipamento Host

- MacBook Pro 
- Apple M2 Max
- 32 GB
- SSD 1TB

### Docker Resources

Limitamos os recursos de CPU e memória do contaneir para que fosse definido um baseline para comparação das estrégias.

```json
deploy:
  resources:
    limits:
      cpus: "4.0"
      memory: 6G
```

## Premissas

O JuMP utiliza em sua arquitetura de dados atual uma estratégia de particionamento de dados física, a nível de tabela, e na mesma instância do banco de dados. Dessa forma, as tabelas de: processo, movimentos e complementos são criadas com um sufixo "_OrgaoJulgadorID", onde cada armazena os dados particionados por Órgão Julgador. Essas tabelas são criadas dinâmicamente na medida que a carga de dados é realizada para cada Tribunal.

### Modelo de dados atual

![Stats](./schemaspy/output/diagrams/summary/relationships.real.compact.png)


## Métricas

Ao comparar a eficácia de diferentes estratégias de particionamento de dados, é essencial considerar uma variedade de métricas para avaliar o desempenho, a escalabilidade e a eficiência operacional do sistema. A tabela abaixo destacamos as principais métricas para essa finalidade:


| #        | Métrica                                                         | Descrição |
| -------- | --------------------------------------------------------------- | --------- |
| 1        | Tempo de Resposta de Consulta                                   | Mede o tempo necessário para uma consulta ser executada e retornar resultados. É uma métrica fundamental para avaliar o desempenho geral do sistema. |
| 2        | Taxa de Transferência de Dados                                  | Avalia a quantidade de dados processados pelo sistema em um determinado período de tempo. Uma alta taxa de transferência indica uma boa capacidade de processamento. |
| 3        | Utilização de Recursos do Sistema                               | Monitora a utilização de recursos do sistema, como CPU, memória e disco. É importante garantir que os recursos estejam sendo utilizados de forma eficiente e que não haja gargalos. |
| 4        | Escalabilidade                                                  | Mede a capacidade do sistema de lidar com um aumento na carga de trabalho sem degradar significativamente o desempenho. Pode ser avaliada através de testes de carga e dimensionamento horizontal. |
| 5        | Distribuição de Carga entre Partições/Shards                    | Verifica se a carga de trabalho está distribuída de forma equitativa entre as partições ou shards. Uma distribuição desigual pode resultar em gargalos de desempenho. |
| 6        | Taxa de Transferência de Inserção/Atualização/Exclusão (I/A/E)  | Avalia a velocidade com que novos dados podem ser inseridos, atualizados ou excluídos no sistema. É importante garantir que essas operações sejam eficientes, especialmente em ambientes de alta concorrência. |
| 7        | Latência de Replicação (se aplicável)                           | Se o sistema envolver replicação de dados entre diferentes nós, é importante monitorar a latência de replicação para garantir que os dados estejam sempre atualizados e consistentes entre os shards. |
| 8        | Espaço em Disco Utilizado por Partição/Shard                    | Monitora o consumo de espaço em disco por cada partição ou shard. Isso ajuda a garantir um uso eficiente do armazenamento e a identificar partições que possam estar se aproximando de sua capacidade máxima. |
| 9        | Tamanho Médio de Partição/Shard                                 | Avalia o tamanho médio dos dados em cada partição ou shard. Isso pode influenciar o desempenho das consultas e operações de E/S. |
| 10       | Taxa de Fragmentação de Índices (se aplicável)                  | Em sistemas que utilizam índices, é importante monitorar a taxa de fragmentação dos índices para garantir um desempenho ótimo das consultas. |


## Resultados dos Experimentos

### Experimento 01 - AS-IS

Neste experimento temos o objetivo de avaliar arquitetura de dados atual sem qualquer intervenção no modelo de dados e coletar métricas de performance.

[Resultados](./experimentos/01-as-is/EXPERIMENTO01.md)