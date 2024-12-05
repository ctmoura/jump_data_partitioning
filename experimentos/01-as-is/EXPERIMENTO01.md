## Experimento 01 - AS-IS

Neste experimento temos o objetivo de avaliar arquitetura de dados atual sem qualquer intervenção no modelo de dados e coletar métricas de performance.

## Resultados

1. Tempo de Resposta de Consulta

- Query:

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

- Total de registros: **123777**
- Tempo de execução: **2 segundos e 750 ms**

2. Taxa de Transferência de Dados

```sql
SET track_io_timing = on;

EXPLAIN ANALYZE SELECT
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

- Taxa: **123777** / **2.750** = **45009,81 registros por segundo**

3. Utilização de Recursos do Sistema

```sql
SET track_activities = on; 
SET track_counts = on;

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

- CPU Usage: 65.30%
- Memory Usage: 107.9MB
- Disk (read): 41MB
- Disk (write): 4.1KB
- Network (received): 170KB
- Network (sent): 85.5MB

![Stats](./stats.png)

4. Escalabilidade

Para essa métrica, implementamos uma aplicação em Java utilizando Spring Boot, que publica um endpoint REST responsável por executar a query destacada abaixo e fazer a leitura do ResultSet, capturando o timestamp inicial e final para cálculo da duração da execução.

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

Utilizamos a ferramenta JMeter para criar um plano de testes que possibilitou simular a carga de usuários simultâneos utilizando a aplicação.

| # Usuários | Tempo mínimo de resposta   | Tempo máximo de resposta    |
| ---------- | -------------------------- | --------------------------- |
| 1          | 1 segundo                  | 2 segundos                  |
| 50         | 15 segundos                | 45 segundos                 |
| 100        | 26 segundos                | 113 segundos                |
| 200        | 47 segundos                | 203 segundos                |
| 500        | XX segundos                | XX segundos                 |
| 1000       | XX segundos                | XX segundos                 |

