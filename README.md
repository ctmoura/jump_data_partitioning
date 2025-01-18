# JuMP: Data Partitioning
Este projeto foi elaborado para experimentação e avaliação das estratégias de particionamento proposta pela pesquisa de mestrado que tem
o objetivo realizar um benchmark das principais estratégias aplicadas a ferramenta JuMP.

* Aluno: `Cleber Tavares de Moura` [ctm@cin.ufpe.br](mailto:ctm@cin.ufpe.br)
* Orientador:  `Ricardo Massa` [rmfl@cin.ufpe.br](mailto:rmfl@cin.ufpe.br)

## 1. Arquitetura da Solução JuMP

A imagem abaixo apresenta uma visão arquitetural de alto nível dos componentes que compõem a solução JuMP, demonstrando o fluxo dos dados.

![Stats](./experimentos/arquitetura-solucao-jump.png)

Abaixo destacamos o fluxo dos dados:

1. Os tribunais, por meio de uma aplicação cliente do CODEX, envia diáriamente os dados processuais para o CODEX servidor;
2. Estes dados processuais são armazenados na base do CODEX;
3. Para cada processo criado e/ou movimentado, o CODEX publica as respectivas mensagens em tópicos RabbitMQ;
4. O JuMP por meio de consumidores que estão subscritos nesses tópicos, é notificado das atualizações;
5. O JuMP então realiza a atualização dos dados processuais em sua base de dados;
6. Além disso, o JuMP executa rotinas de carga de dados, realizando consultas às APIs disponibilizadas pelo CODEX;
7. As APIs do CODEX retornam os dados processuais;
8. O JuMP atualiza os dados processuais em sua base de dados.


### 1.1 Arquitetura de dados do JuMP

A arquitetura atual do JuMP possui uma única instância de banco de dados, e utiliza a estratégia de particionamento de dados física a nível de tabelas.

Para melhor entendimento, a imagem abaixo apresenta o modelo físico do banco de dados, onde podemos observar que para cada Órgão Julgador, existem respectivas tabelas de: processos, movimentos e complementos, acrescidas do sufixo "_00000", que representa o identificador único de cada Órgão Julgador.

Essas tabelas são criadas dinâmicamente, na carga inicial de dados de um Órgão Julgador, e atualizadas à medida que novas cargas são realizadas para cada Tribunal.

### 1.2 Modelo físico do banco de dados

![Stats](./schemaspy/output/diagrams/summary/relationships.real.compact.png)


## 2. Métricas de avaliação

Ao comparar a eficácia de diferentes estratégias de particionamento de dados, é essencial considerar uma variedade de métricas para avaliar o desempenho, a escalabilidade e a eficiência operacional do sistema. 

Na tabela abaixo destacamos as principais métricas para essa finalidade. Contudo, é importante destacar que algumas métricas como: Latência de Replicação, não será possível mensurar em todas as estratégias avaliadas, mas apenas naquelas que envolve replicação de dados em diferentes nós.


| #        | Métrica                                                         | Descrição | Relevância | 
| -------- | --------------------------------------------------------------- | --------- | ---------- |
| 1        | Tempo de Processamento                                          | Mede o tempo necessário para executar operações específicas, como consultas, inserções e/ou atualizações no sistema. | Avalia a eficiência das estratégias para lidar com grandes volumes de dados. Estratégias mal implementadas podem aumentar a latência. |
| 2        | Utilização de Recursos                                          | Mede o uso de recursos computacionais, como CPU, memória e disco, durante a execução de operações no sistema. | Identifica gargalos que possam ser introduzidos por estratégias de particionamento. Mostra a eficiência no uso da infraestrutura. |
| 3        | Escalabilidade                                                  | Avalia como a estratégia se comporta ao aumentar o volume de dados ou a quantidade de nós no sistema. | Sistemas com crescimento contínuo de dados exigem estratégias que possam escalar sem comprometer o desempenho. |
| 4        | Equilíbrio de Carga                                             | Mede a uniformidade na distribuição de dados e tarefas entre os nós do sistema. | Os desequilíbrios resultam em nós sobrecarregados e outros subutilizados, reduzindo a eficiência do sistema. |
| 5        | Taxa de Transferência de Dados (Throughput)                     | Mede a quantidade de operações processadas por unidade de tempo (ex.: consultas por segundo ou registros inseridos por segundo). | Avalia o desempenho global do sistema em cenários com alta concorrência. |
| 6        | Custo de Redistribuição                                         | Avalia o impacto de redistribuir dados em tempo real devido a alterações na carga ou na configuração do sistema. | Estratégias dinâmicas, como particionamento adaptativo, podem causar interrupções ou consumir recursos durante redistribuições. |
| 7        | Eficiência de Consultas                                         | Mede a eficácia da estratégia para consultas específicas, como aquelas que atravessam várias partições. | Importante para sistemas como o JuMP, onde dados são frequentemente acessados de diferentes tribunais e regiões. |
| 8        | Consistência de Dados                                           | Mede a capacidade da estratégia de manter a consistência dos dados em diferentes nós. | Garantir que não haja discrepâncias ou conflitos de dados ao lidar com alta concorrência. |
| 9        | Capacidade de Adaptação                                         | Avalia a capacidade da estratégia de ajustar dinamicamente o particionamento para lidar com padrões de acesso variáveis. | Particularmente relevante para cenários onde a carga de trabalho muda com frequência. |
| 10       | Custo Operacional                                               | Mede os custos associados à implementação e manutenção de cada estratégia. | Identifica estratégias que exigem maior esforço de administração ou mais recursos computacionais. |


## 3. Ambiente utilizado para os experimentos

Para realização dos experimentos está sendo utilizada uma infraestrutura local baseada em Docker containers.

### 3.1 Equipamento Host

- MacBook Pro 
- Apple M2 Max
- 32 GB
- SSD 1TB

### 3.2 Recursos disponíveis

Os recursos de CPU e memória do container do banco de dados foi limitado a fim de estabelecer um baseline para comparação das estratégias de particionamento.

- [docker-compose.yml](./docker-compose.yml): limites definidos para CPU e memória:

```yaml
services:

  postgres:
    image: postgres:16.2
    deploy:
      resources:
        limits:
          cpus: "2.0"
          memory: 6G
```

## 4. Estrutura dos experimentos

Para realização dos experimentos será utilizada a seguinte estrutura para documentação da avaliação e resultados de cada uma das estratégias de particionamento.


| #         | Seção                                    | Descrição |
| --------- | ---------------------------------------- | --------- |
| 1         | **Estratégia de particionamento**        | Descreve a estratégia de particionamento a ser avaliada. |
| 2         | **Cenários de testes**                   | Descreve os cenários e/ou consultas (queries) a serem avaliadas. |
| 3         | **Ambiente de testes**                   | Descreve o ambiente e recursos disponíveis para realização dos testes. |
| 4         | **Simulação da carga**                   | Descreve o processo e ferramentas utilizadas para simulação da carga. |
| 5         | **Métricas avaliadas e resultados**      | Descreve as métricas e resultados obtidos após os testes. |


## 5. Execução e resultados dos Experimentos

Esta seção apresenta os experimentos realizados e seus resultados.

### Preparação do Banco de dados

Para realização dos experimentos foi fornecida um Backup da base de dados do JuMP com uma massa de dados representativa e com os dados anonimizados. Os arquivos desse backup estão disponíveis em: [split_init.sql.zip](./data/postgresql/split_init.sql.zip).

Para descompactar esse arquivo, utilize o seguinte comando do utilitário ZIP, para unificar as partes em um único arquivo ZIP: `init.sql.zip`.

> zip -s 0 split_init.sql.zip --out init.sql.zip

Em seguida, execute o seguinte comando para descompactar o arquivo ZIP, que ira gerar o arquivo: `init.sql`

> unzip init.sql.zip

Agora a aplicação será capaz de inicializar o banco de dados a partir deste backup.

### Execução do ambiente

Primeiramente, para rodar o ambiente simulado do JuMP execute o comando abaixo responsável por iniciar os containers com docker.

> docker-compose up -d

Em seguida, execute o comando que executa a aplicação SpringBoot responsável por executar a query de consulta que será utilizada para avaliar o desempenho do sistema para os cenários de cada experimento.

> ./mvnw spring-boot:run


### 5.1 Experimento 01 - AS-IS

Neste experimento temos o objetivo de avaliar arquitetura de dados atual sem qualquer intervenção no modelo de dados e coletar métricas de performance.

[Resultados](./experimentos/01-as-is/EXPERIMENTO01.md)