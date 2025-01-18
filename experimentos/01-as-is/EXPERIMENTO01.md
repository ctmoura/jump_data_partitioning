# 1 - Experimento 01 - AS-IS

Este é o nosso ponto de partida para realização dos experimentos.

Nele serão coletados os resultados das métricas estabelecidas para availação comparativa, aplicadas ao cenário atual do JuMP, sem qualquer intervenção na estretégia de particionamento utilizada, modelo de dados, ou arquitetura.

Sobre o ambiente de execução dos experimentos, vale destacar que se trata de um ambiente simulado e distinto do ambiente de produção, uma vez que precisamos ter controle e dos parâmetros de concorrência, disponibilidade de recursos e performance, sem impactar o ambiente real.

Para uma comparação justa das estratégias, os recursos disponíveis de memória, cpu e aramezenamento, serão sempre equivalentes em todos os experimentos.

## 1.1 - Estratégia de particionamento

Neste experimento, que reflete a arquitetura atual do JuMP, a estratégia utilizada é a de particionamento por chave, que para cada Unidade Judiciária, novas tabelas de: processos, movimentos e complementos são criadas e cada uma delas tem um sufixo respectivo que é o identificador da unidade judiciária. Por exemplo, para um dado tribunal que possui uma unidade judiciária com o ID 1000, serão criadas as respectivas tabelas: processos_1000, movimentos_1000 e complementos_1000.

## 1.2 - Cenários de testes

Para avaliar essa estratégia será utilizada a seguinte consulta SQL de referêcia:

```sql
SELECT
    p."NPU", p."processoID", "dataPrimeiroMovimento",
  c.descricao AS classe, a.descricao AS assunto,
  activity, "dataInicio", "dataFinal", "usuarioID", "movimentoID", duration
  "nomeServidor", "tipoServidor"
FROM
    processos_18006 AS p
INNER JOIN
    movimentos_18006 AS m ON p."processoID" = m."processoID"
INNER JOIN
    classes AS c ON p.classe = c.id
INNER JOIN
    assuntos AS a ON p.assunto = a.id
INNER JOIN
    servidores ON m."usuarioID" = servidores."servidorID"
ORDER BY "processoID", "dataFinal" DESC;
```

## 1.3 - Ambiente de testes

### 1.3.1 - Banco de dados

- Postgres: version 16.2

### 1.3.2 - Equipamento Host

- MacBook Pro 
- Apple M2 Max
- 32 GB
- SSD 1TB

### 1.3.3 - Execução em ambiente de containers

Será utilizado o Docker como ferramenta de virtualização em containers para execução do servidor de banco de dados Postgres.

- Docker: version 27.4.0, build bde2b89
- Docker Compose: version v2.31.0-desktop.2

#### Recursos disponíveis

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

## 1.4 - Simulação da carga

Para simulação de cargas de execução utilizaremos a ferramenta JMeter para criar um plano de testes que possibile simular diferentes cenários de cargas dos usuários utilizando a aplicação.

- [Apache JMeter: version 5.6.3](https://jmeter.apache.org/index.html)  


## 1.5 - Métricas avaliadas e resultados

### 1.5.1 - Tempo de Processamento

- Total de registros: **123777**
- Tempo de execução: **319 ms**

### 1.5.2 - Utilização de Recursos  

- Comandos para configurar o rastreamento e a coleta de informações estatísticas sobre as atividades do banco de dados. Executado antes de rodar a consulta SQL.

```sql
SET track_activities = on; 
SET track_counts = on;

-- CONSULTA SQL DE REFERÊNCIQ
SELECT * FROM ...;

```

- CPU Usage: 65.30%
- Memory Usage: 107.9MB
- Disk (read): 41MB
- Disk (write): 4.1KB
- Network (received): 170KB
- Network (sent): 85.5MB

![Stats](./stats.png)

### 1.5.3 - Escalabilidade

Para essa métrica, implementamos uma aplicação em Java utilizando Spring Boot, que publica um endpoint REST responsável por executar a query de referência, realizar a leitura do ResultSet, capturando o timestamp inicial e final da execução para cálculo da duração.

Utilizamos a ferramenta JMeter para criar um plano de testes que possibilitou simular a carga de usuários simultâneos utilizando a aplicação.

| # Usuários | Tempo mínimo de resposta   | Tempo máximo de resposta    |
| ---------- | -------------------------- | --------------------------- |
| 1          | 1 segundo                  | 2 segundos                  |
| 50         | 15 segundos                | 45 segundos                 |
| 100        | 26 segundos                | 113 segundos                |
| 200        | 47 segundos                | 203 segundos                |
| 500        | XX segundos                | XX segundos                 |
| 1000       | XX segundos                | XX segundos                 |

### 1.5.4 - Equilíbrio de Carga

### 1.5.5 - Taxa de Transferência de Dados (Throughput)

- Comando para ativar o rastreamento de tempos de entrada/saída (I/O) em operações realizadas pelo banco de dados.

```sql
SET track_io_timing = on;

EXPLAIN ANALYZE 
    -- CONSULTA SQL DE REFERÊNCIQ
    SELECT * FROM ...;
    
```

- Taxa: **123777** / **2.750** = **45009,81 registros por segundo**

### 1.5.6 - Custo de Redistribuição

### 1.5.7 - Eficiência de Consultas

### 1.5.8 - Consistência de Dados

### 1.5.9 - Capacidade de Adaptação

### 1.5.10 - Custo Operacional