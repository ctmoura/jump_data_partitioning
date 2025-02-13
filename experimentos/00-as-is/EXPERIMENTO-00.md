# 1 - Experimento 00 - AS-IS

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
    p."NPU", p."processoID", p."ultimaAtualizacao",
  	c.descricao AS classe, a.descricao AS assunto,
  	m.activity, m."dataInicio", m."dataFinal", m."usuarioID",
    m.duration, m."movimentoID", com.descricao AS complemento,
  	s."nomeServidor", s."tipoServidor", d.tipo AS documento
FROM 
	movimentos_18006 AS m
INNER JOIN
    processos_18006 AS p ON p."processoID" = m."processoID"
INNER JOIN
    classes AS c ON p.classe = c.id
LEFT JOIN
    assuntos AS a ON p.assunto = a.id
LEFT JOIN
    complementos_18006 AS com ON com."movimentoID" = m.id
LEFT JOIN
    servidores AS s ON s."servidorID" = m."usuarioID"
LEFT JOIN
	documentos AS d ON d."id" = m."documentoID"
WHERE '2000-01-01' <= p."dataPrimeiroMovimento"
ORDER BY "processoID", "dataFinal";
```

- Total de registros retornando pela query: **3.364.537**

## 1.3 - Ambiente de testes

### 1.3.1 - Equipamento Host

- MacBook Pro 
- Apple M2 Max
- 32 GB
- SSD 1TB

### 1.3.2 - Execução em containers

Será utilizado o Docker como ferramenta de virtualização em containers para execução do servidor de banco de dados Postgres.

- Docker: version 27.4.0, build bde2b89
- Docker Compose: version v2.31.0-desktop.2

### 1.3.3 - Banco de dados

Utilizamos Postgres: version 16.2, que é o banco de dados utilizado pelo JuMP.

#### Configurações

> 01 instância de container

```yaml
services:

  postgres:
    image: postgres:16.2
    shm_size: "4g"
    deploy:
      resources:
        limits:
          cpus: "4.0"
          memory: "12g"
        reservations:
          cpus: "2.0"
          memory: "6g"
```

## 1.4 - Simulação da carga

Para simulação de cargas de execução utilizaremos a ferramenta JMeter para criar um plano de testes que possibile simular diferentes cenários de cargas dos usuários utilizando a aplicação.

Os cenários do plano de teste segue uma sequencia fibonaci para determinar a quantidade de threads (usuários simulâneos) em cada cenário, sendo que cada thread (usuário) executa 10 requisições sequenciais de disparo da query no banco de dados.

- [Apache JMeter: version 5.6.3](https://jmeter.apache.org/index.html)  


## 1.5 - Métricas avaliadas e resultados

A imagem abaixo apresentamos os gráficos da utilização de recursos durante a execução do experimento. Estes gráficos foram coletados a partir do dashboard do Docker, referente ao container de execução do banco de dados Postgres.

![Stats](./stats-geral.jpg)

A tabela abaixo apresenta os resultados consolidados das métricas coletadas durante a execução do experimento.

![Tabela de resultados](./tabela-exp-00.jpg)

> Podemos perceber, a partir do cenário de testes com 21 usuários simultâneos, o banco de dados passou falhar **45,76%** das consultas realizadas.

### 1.5.1 - Tempo de Resposta

A tebela também apresenta as durações da execução em: Menor duração, Maior duração, e	Duração média, para cada cenário do teste.

### 1.5.2 - Escalabilidade

De acordo com a tabela podemos perceber que e a arquitetura atual permitiu escalar até o cenário com 13 usuários simultâneos, e a partir do cenário com 21 usuários, o banco de dados passou falhar **45,76%** das consultas realizadas.

### 1.5.3 - Equilíbrio de Carga

A carga de execução foi distribuída de forma equilibrada, uma vez que todas as unidades possuem exatamente a mesma quantidade de registros em suas respectivas tabelas.

### 1.5.4 - Taxa de Transferência de Dados

Foi executado o seguinte comando recuperar o plano de execução da query, com as informações sobre a execução.

```sql
EXPLAIN ANALYZE 
SELECT
    p."NPU", 
    p."processoID", 
    p."ultimaAtualizacao",
    c.descricao AS classe, 
    a.descricao AS assunto,
    m.activity, 
    m."dataInicio", 
    m."dataFinal", 
    m."usuarioID",
    m.duration, 
    m."movimentoID", 
    com.descricao AS complemento,
    s."nomeServidor", 
    s."tipoServidor", 
    d.tipo AS documento
FROM 
    processos_18006 AS p
INNER JOIN
    movimentos_18006 AS m 
    ON m."processoID" = p."processoID"
INNER JOIN
    classes AS c ON p.classe = c.id
LEFT JOIN
    assuntos AS a ON p.assunto = a.id
LEFT JOIN
    complementos_18006 AS com 
    ON com."movimentoID" = m."id" 
LEFT JOIN
    servidores AS s ON s."servidorID" = m."usuarioID"
LEFT JOIN
    documentos AS d ON d."id" = m."documentoID"
WHERE 
    p."dataPrimeiroMovimento" >= '2020-01-01' 
ORDER BY 
    p."processoID", m."dataFinal";
```

- Taxa: **815.477** registros / **3,48** segundos = **234.332,47** registros por segundo**

### 1.5.5 - Custo de Redistribuição

Nessa abordagem, não existe custo de redistribuição dos dados pois eles estão armazenados em tabelas por unidade. 

### 1.5.7 - Eficiência de Consultas

A eficiência pode ser expressa como uma relação entre o tempo de execução, tempo ideal e o número de partições acessadas:

#### Fórmula:


```plaintext
Eficiência (%) = (1 - (P_Acessadas / P_Total)) * (1 - (T_Query / T_Ideal)) * 100
```

Onde:
- P_Acessadas: Quantidade de partições acessadas.
- P_Total: Total de partições disponíveis.
- T_Query: Tempo total de execução da query (Execution Time no EXPLAIN ANALYZE).
- T_Ideal: Tempo esperado para a melhor execução possível (vamos estabelecer como ideal o tempo de execução limite de **3 segundos**).

Sendo assim, temos:

- P_Acessadas: **3**
- P_Total: **9**
- T_Query: **3,48 segundos**
- T_Ideal: **3 segundos** 

> Eficiência (%) =  (1 - (3 / 9)) * (1 - (3,48 / 3)) * 100 => (1 - 0,333333333333333) * (1 - 1,16) * 100 = **-10,66%**

Nesta arquitetura, a consulta obteve uma eficiencia negativa de **-10,66%**.
