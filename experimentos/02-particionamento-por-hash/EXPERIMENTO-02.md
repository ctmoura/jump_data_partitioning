# 1 - Experimento 02 - Particionamento Por Hash

O **particionamento por hash** é uma estratégia de particionamento em que os dados são distribuídos entre partições com base no resultado de uma função de hash aplicada a uma ou mais colunas, como a chave primária. Em um cenário com apenas uma instância de banco de dados, essa técnica permite uma distribuição mais uniforme dos dados entre as partições, reduzindo a probabilidade de hotspots e otimizando o uso de recursos como CPU e I/O. Entre os benefícios estão a melhora no balanceamento de carga, já que as partições tendem a conter volumes de dados semelhantes, e a eficiência em consultas que acessam uma única partição, pois o mapeamento direto evita varreduras desnecessárias. No entanto, suas limitações incluem a dificuldade em realizar consultas que cruzam múltiplas partições, que podem sofrer degradação de desempenho devido à necessidade de unir dados de várias partições. Além disso, a manutenção, como adicionar novas partições ou redistribuir dados, pode ser complexa e custosa, especialmente em sistemas com alto volume de dados. Assim, o particionamento por hash é mais adequado para cenários em que a maioria das consultas depende de acesso direto a chaves específicas e os padrões de acesso são bem distribuídos.

## 1.1 - Preparação

Para avaliar essa estratégia se faz necessário executar alguns procedimentos no banco de dados para que as tabelas tenham suporte ao particionamento de dados por hash, pois o banco de dados utilizado (PostgreSQL) não suporta o particionamento em tabela pré existente.

Além disso, o modelo de dados atual está armazenando os registros de processos, movimentos e complementos em tabelas separadas por unidade judiciária, ou seja, para cada unidade judiciária existem as respectivas tabelas de processos, movimentos e complementos daquela unidade.

Para avalidar a estratégia de particionamento por hash, iremos unificar as tabelas de processos, movimentos e complementos, de cada unidade em tabelas únicas de processos, movimentos e complementos, adicionando uma coluna que representa a chave de unidade judiciária.

## 1.2 - Definição da função hash

As partições da tabela de **processos** serão criadas levando em consideração o resultado de uma função hash, que receberá como entrada os seguintes parâmteros: 

- a unidade judiciária; e 
- a data do primeiro movimento.

Já as tabelas de **movimentos** e **complementos**, suas partições serão criadas levando em consideração o resultado de uma função hash, que receberá como parâmtero de entrada: 

- a unidade judiciária.

## 1.3 - Unificação dos registros nas tabelas particionadas

Nesta etapa, iremos unificar os registros existentes em tabelas únicas. 

Como a base de dados que foi fornecida só dispunha de registros para uma única unidade judiciária (id: 18006), optamos por clonar as tabelas desta unidade para simular o cenário com múltiplas unidades judiciárias.

1. Clonando as tabelas da unidade judiciária existente 

```sql

-- Tabelas para unidade 18007

-- processos_18007

CREATE TABLE public.processos_18007 AS
SELECT
"processoID" + 1000000000 AS "processoID", -- Adiciona um offset para as chaves primárias serem únicas
"NPU", liminar, natureza, "valorCausa", "nivelSigilo", competencia, 
"situacaoMigracao", "justicaGratuita", "dataAjuizamento", assunto, classe, 
"ultimaAtualizacao", "ultimoMovimento", "dataPrimeiroMovimento", "dataUltimoMovimento", 
'18007'::bigint AS "unidadeID"
FROM public.processos_18006;

ALTER TABLE IF EXISTS public.processos_18007
    ADD CONSTRAINT processos_18007_pkey PRIMARY KEY ("processoID");
ALTER TABLE IF EXISTS public.processos_18007
    ADD CONSTRAINT processos_18007_classe_fkey FOREIGN KEY (classe)
    REFERENCES public.classes (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
ALTER TABLE IF EXISTS public.processos_18007
    ADD CONSTRAINT processos_18007_assunto_fkey FOREIGN KEY (assunto)
    REFERENCES public.assuntos (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

-- movimentos_18007

CREATE TABLE public.movimentos_18007 AS
SELECT
id + 10000000000 AS id, -- Adiciona um offset para as chaves primárias serem únicas
"processoID" + 1000000000 AS "processoID", -- Adiciona um offset para as chaves primárias serem únicas
"NPU", activity, duration, "dataInicio", "dataFinal", "usuarioID", "documentoID", "movimentoID",
'18007'::bigint AS "unidadeID"
FROM public.movimentos_18006;


ALTER TABLE IF EXISTS public.movimentos_18007
    ADD CONSTRAINT movimentos_18007_pkey PRIMARY KEY (id);
ALTER TABLE IF EXISTS public.movimentos_18007
    ADD CONSTRAINT "movimentos_18007_processoID_fkey" FOREIGN KEY ("processoID")
    REFERENCES public.processos_18007 ("processoID") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
ALTER TABLE IF EXISTS public.movimentos_18007
    ADD CONSTRAINT "movimentos_18007_movimentoID_fkey" FOREIGN KEY ("movimentoID")
    REFERENCES public.cod_movimentos (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

-- complementos_18007

CREATE TABLE public.complementos_18007 AS
SELECT
"complementoID" + 10000000000 AS "complementoID", -- Adiciona um offset para as chaves primárias serem únicas
"movimentoID" + 10000000000 AS "movimentoID", -- Adiciona um offset para as chaves primárias serem únicas
tipo, descricao,
'18007'::bigint AS "unidadeID"
FROM public.complementos_18006;

ALTER TABLE IF EXISTS public.complementos_18007
    ADD CONSTRAINT complementos_18007_pkey PRIMARY KEY ("complementoID");
ALTER TABLE IF EXISTS public.complementos_18007
    ADD CONSTRAINT "complementos_18007_movimentoID_fkey" FOREIGN KEY ("movimentoID")
    REFERENCES public.movimentos_18007 (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;


-- Tabelas para unidade 18008

-- processos_18008

CREATE TABLE public.processos_18008 AS
SELECT
"processoID" + 2000000000 AS "processoID", -- Adiciona um offset para as chaves primárias serem únicas
"NPU", liminar, natureza, "valorCausa", "nivelSigilo", competencia, 
"situacaoMigracao", "justicaGratuita", "dataAjuizamento", assunto, classe, 
"ultimaAtualizacao", "ultimoMovimento", "dataPrimeiroMovimento", "dataUltimoMovimento", 
'18008'::bigint AS "unidadeID"
FROM public.processos_18006;

ALTER TABLE IF EXISTS public.processos_18008
    ADD CONSTRAINT processos_18008_pkey PRIMARY KEY ("processoID");
ALTER TABLE IF EXISTS public.processos_18008
    ADD CONSTRAINT processos_18008_classe_fkey FOREIGN KEY (classe)
    REFERENCES public.classes (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
ALTER TABLE IF EXISTS public.processos_18008
    ADD CONSTRAINT processos_18008_assunto_fkey FOREIGN KEY (assunto)
    REFERENCES public.assuntos (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

-- movimentos_18008

CREATE TABLE public.movimentos_18008 AS
SELECT
id + 20000000000 AS id, -- Adiciona um offset para as chaves primárias serem únicas
"processoID" + 2000000000 AS "processoID", -- Adiciona um offset para as chaves primárias serem únicas
"NPU", activity, duration, "dataInicio", "dataFinal", "usuarioID", "documentoID", "movimentoID",
'18008'::bigint AS "unidadeID"
FROM public.movimentos_18006;


ALTER TABLE IF EXISTS public.movimentos_18008
    ADD CONSTRAINT movimentos_18008_pkey PRIMARY KEY (id);
ALTER TABLE IF EXISTS public.movimentos_18008
    ADD CONSTRAINT "movimentos_18008_processoID_fkey" FOREIGN KEY ("processoID")
    REFERENCES public.processos_18008 ("processoID") MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
ALTER TABLE IF EXISTS public.movimentos_18008
    ADD CONSTRAINT "movimentos_18008_movimentoID_fkey" FOREIGN KEY ("movimentoID")
    REFERENCES public.cod_movimentos (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

-- complementos_18008

CREATE TABLE public.complementos_18008 AS
SELECT
"complementoID" + 20000000000 AS "complementoID", -- Adiciona um offset para as chaves primárias serem únicas
"movimentoID" + 20000000000 AS "movimentoID", -- Adiciona um offset para as chaves primárias serem únicas
tipo, descricao,
'18008'::bigint AS "unidadeID"
FROM public.complementos_18006;

ALTER TABLE IF EXISTS public.complementos_18008
    ADD CONSTRAINT complementos_18008_pkey PRIMARY KEY ("complementoID");
ALTER TABLE IF EXISTS public.complementos_18008
    ADD CONSTRAINT "complementos_18008_movimentoID_fkey" FOREIGN KEY ("movimentoID")
    REFERENCES public.movimentos_18008 (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;

```

2. Criando a coluna **unidadeID** nas tabelas originais de complementos_18006, movimentos_18006 e processos_18006.

```sql

-- Unidade Judiciária: 18006

-- Tabelas para complementos_18006
ALTER TABLE IF EXISTS public.complementos_18006
    ADD COLUMN "unidadeID" bigint;
UPDATE public.complementos_18006 SET "unidadeID" = 18006;
ALTER TABLE IF EXISTS public.complementos_18006
    ALTER COLUMN "unidadeID" SET NOT NULL;

-- Tabelas para movimentos_18006
ALTER TABLE IF EXISTS public.movimentos_18006
    ADD COLUMN "unidadeID" bigint;
UPDATE public.movimentos_18006 SET "unidadeID" = 18006;
ALTER TABLE IF EXISTS public.movimentos_18006
    ALTER COLUMN "unidadeID" SET NOT NULL;

-- Tabelas para processos_18006
ALTER TABLE IF EXISTS public.processos_18006
    ADD COLUMN "unidadeID" bigint;
UPDATE public.processos_18006 SET "unidadeID" = 18006;
ALTER TABLE IF EXISTS public.processos_18006
    ALTER COLUMN "unidadeID" SET NOT NULL;
```

3. Criando as tabelas com particionamento por hash

O comando abaixo cria seguintes tabelas: **processos_particionada**, **movimentos_particionada** e **complementos_particionada**, com o particionamento por hash ativado:

```sql
CREATE SEQUENCE IF NOT EXISTS public."processos_particionada_processoID_seq"
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public."processos_particionada_processoID_seq"
    OWNER TO postgres;
	
CREATE TABLE IF NOT EXISTS public.processos_particionada
(
    "processoID" bigint NOT NULL DEFAULT nextval('"processos_particionada_processoID_seq"'::regclass),
    "NPU" character varying COLLATE pg_catalog."default" NOT NULL,
    liminar boolean,
    natureza character varying COLLATE pg_catalog."default",
    "valorCausa" character varying COLLATE pg_catalog."default",
    "nivelSigilo" character varying COLLATE pg_catalog."default",
    competencia character varying COLLATE pg_catalog."default",
    "situacaoMigracao" character varying COLLATE pg_catalog."default",
    "justicaGratuita" boolean,
    "dataAjuizamento" timestamp without time zone,
    assunto integer,
    classe integer,
    "ultimaAtualizacao" timestamp without time zone,
    "ultimoMovimento" bigint,
    "dataPrimeiroMovimento" timestamp without time zone,
    "dataUltimoMovimento" timestamp without time zone,
    "unidadeID" bigint NOT NULL,
    "anoPrimeiroMovimento" integer,
    CONSTRAINT processos_particionada_assunto_fkey FOREIGN KEY (assunto)
        REFERENCES public.assuntos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT processos_particionada_classe_fkey FOREIGN KEY (classe)
        REFERENCES public.classes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) PARTITION BY HASH ("unidadeID", "anoPrimeiroMovimento");

ALTER TABLE IF EXISTS public.processos_particionada
    OWNER to postgres;

ALTER SEQUENCE public."processos_particionada_processoID_seq"
    OWNED BY public.processos_particionada."processoID";

CREATE UNIQUE INDEX unq_processos_particionada
    ON public.processos_particionada USING btree
    ("processoID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST, "unidadeID" ASC NULLS LAST)
    WITH (deduplicate_items=True);

CREATE INDEX IF NOT EXISTS idx_processos_particionada_dataprimeiromovimento
    ON public.processos_particionada USING btree
    ("dataPrimeiroMovimento" ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS idx_processos_particionada_anoprimeiromovimento
    ON public.processos_particionada USING btree
    ("unidadeID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST);


-- movimentos

CREATE SEQUENCE IF NOT EXISTS public."movimentos_particionada_id_seq"
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public."movimentos_particionada_id_seq"
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.movimentos_particionada
(
    id bigint NOT NULL DEFAULT nextval('movimentos_particionada_id_seq'::regclass),
    "processoID" bigint,
    "NPU" character varying COLLATE pg_catalog."default",
    activity character varying COLLATE pg_catalog."default" NOT NULL,
    duration bigint,
    "dataInicio" timestamp without time zone,
    "dataFinal" timestamp without time zone NOT NULL,
    "usuarioID" bigint,
    "documentoID" bigint,
    "movimentoID" bigint,
    "unidadeID" bigint NOT NULL,
    "anoPrimeiroMovimento" integer,
    CONSTRAINT "movimentos_particionada_movimentoID_fkey" FOREIGN KEY ("movimentoID")
        REFERENCES public.cod_movimentos (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "movimentos_particionada_processoID_fkey" FOREIGN KEY ("processoID", "unidadeID", "anoPrimeiroMovimento")
        REFERENCES public.processos_particionada ("processoID", "unidadeID", "anoPrimeiroMovimento") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
) PARTITION BY HASH ("unidadeID", "anoPrimeiroMovimento");

ALTER TABLE IF EXISTS public.movimentos_particionada
    OWNER to postgres;

ALTER SEQUENCE public."movimentos_particionada_id_seq"
    OWNED BY public.movimentos_particionada.id;

CREATE UNIQUE INDEX unq_movimentos_particionada
    ON public.movimentos_particionada USING btree
    (id ASC NULLS LAST, "unidadeID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST)
    WITH (deduplicate_items=True);

CREATE INDEX IF NOT EXISTS idx_movimentos_particionada_anoprimeiromovimento
    ON public.movimentos_particionada USING btree
    ("unidadeID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST);

-- complementos

CREATE SEQUENCE IF NOT EXISTS public."complementos_particionada_complementoID_seq"
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public."complementos_particionada_complementoID_seq"
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.complementos_particionada
(
    "complementoID" bigint NOT NULL DEFAULT nextval('"complementos_particionada_complementoID_seq"'::regclass),
    "movimentoID" bigint,
    tipo character varying COLLATE pg_catalog."default" NOT NULL,
    descricao character varying COLLATE pg_catalog."default" NOT NULL,
    "unidadeID" bigint NOT NULL,
    "anoPrimeiroMovimento" integer,
    CONSTRAINT "complementos_particionada_movimentoID_fkey" FOREIGN KEY ("movimentoID", "unidadeID", "anoPrimeiroMovimento")
        REFERENCES public.movimentos_particionada (id, "unidadeID", "anoPrimeiroMovimento") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
) PARTITION BY HASH ("unidadeID", "anoPrimeiroMovimento");

ALTER TABLE IF EXISTS public.complementos_particionada
    OWNER to postgres;

ALTER SEQUENCE public."complementos_particionada_complementoID_seq"
    OWNED BY public.complementos_particionada."complementoID";

CREATE UNIQUE INDEX unq_complementos_particionada
    ON public.complementos_particionada USING btree
    ("complementoID" ASC NULLS LAST, "unidadeID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST)
    WITH (deduplicate_items=True);

CREATE INDEX IF NOT EXISTS idx_complementos_particionada_anoprimeiromovimento
    ON public.complementos_particionada USING btree
    ("unidadeID" ASC NULLS LAST, "anoPrimeiroMovimento" ASC NULLS LAST);

```

4. Criando as tabelas das partições e índices

Como já avaliado no experimento anterior, considerando a distribuição por ano, os registros de processos estão distribuídos em 13 partes. Sendo assim, para cada tupla ("unidadeID" + "ano") presente na distribuição dos dados, será criada uma tabela de particionamento, bem como os respectivos índices de unicidade da coluna `processoID` e o de performance na coluna `dataPrimeiroMovimento`. Totalizando 39 partições.

Os comanos abaixo criam as tabelas e índices mencionados:

```sql
CREATE TABLE processos_particionada_0 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 0);
CREATE TABLE processos_particionada_1 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 1);
CREATE TABLE processos_particionada_2 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 2);
CREATE TABLE processos_particionada_3 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 3);
CREATE TABLE processos_particionada_4 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 4);
CREATE TABLE processos_particionada_5 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 5);
CREATE TABLE processos_particionada_6 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 6);
CREATE TABLE processos_particionada_7 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 7);
CREATE TABLE processos_particionada_8 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 8);
CREATE TABLE processos_particionada_9 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 9);
CREATE TABLE processos_particionada_10 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 10);
CREATE TABLE processos_particionada_11 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 11);
CREATE TABLE processos_particionada_12 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 12);
CREATE TABLE processos_particionada_13 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 13);
CREATE TABLE processos_particionada_14 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 14);
CREATE TABLE processos_particionada_15 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 15);
CREATE TABLE processos_particionada_16 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 16);
CREATE TABLE processos_particionada_17 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 17);
CREATE TABLE processos_particionada_18 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 18);
CREATE TABLE processos_particionada_19 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 19);
CREATE TABLE processos_particionada_20 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 20);
CREATE TABLE processos_particionada_21 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 21);
CREATE TABLE processos_particionada_22 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 22);
CREATE TABLE processos_particionada_23 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 23);
CREATE TABLE processos_particionada_24 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 24);
CREATE TABLE processos_particionada_25 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 25);
CREATE TABLE processos_particionada_26 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 26);
CREATE TABLE processos_particionada_27 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 27);
CREATE TABLE processos_particionada_28 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 28);
CREATE TABLE processos_particionada_29 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 29);
CREATE TABLE processos_particionada_30 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 30);
CREATE TABLE processos_particionada_31 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 31);
CREATE TABLE processos_particionada_32 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 32);
CREATE TABLE processos_particionada_33 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 33);
CREATE TABLE processos_particionada_34 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 34);
CREATE TABLE processos_particionada_35 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 35);
CREATE TABLE processos_particionada_36 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 36);
CREATE TABLE processos_particionada_37 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 37);
CREATE TABLE processos_particionada_38 PARTITION OF processos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 38);

CREATE UNIQUE INDEX idx_unq_processos_particionada_1_processoID ON public.processos_particionada_1 ("processoID");
CREATE INDEX idx_processos_particionada_1_dataPrimeiroMovimento ON public.processos_particionada_1 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_2_processoID ON public.processos_particionada_2 ("processoID");
CREATE INDEX idx_processos_particionada_2_dataPrimeiroMovimento ON public.processos_particionada_2 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_3_processoID ON public.processos_particionada_3 ("processoID");
CREATE INDEX idx_processos_particionada_3_dataPrimeiroMovimento ON public.processos_particionada_3 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_4_processoID ON public.processos_particionada_4 ("processoID");
CREATE INDEX idx_processos_particionada_4_dataPrimeiroMovimento ON public.processos_particionada_4 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_5_processoID ON public.processos_particionada_5 ("processoID");
CREATE INDEX idx_processos_particionada_5_dataPrimeiroMovimento ON public.processos_particionada_5 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_6_processoID ON public.processos_particionada_6 ("processoID");
CREATE INDEX idx_processos_particionada_6_dataPrimeiroMovimento ON public.processos_particionada_6 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_7_processoID ON public.processos_particionada_7 ("processoID");
CREATE INDEX idx_processos_particionada_7_dataPrimeiroMovimento ON public.processos_particionada_7 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_8_processoID ON public.processos_particionada_8 ("processoID");
CREATE INDEX idx_processos_particionada_8_dataPrimeiroMovimento ON public.processos_particionada_8 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_9_processoID ON public.processos_particionada_9 ("processoID");
CREATE INDEX idx_processos_particionada_9_dataPrimeiroMovimento ON public.processos_particionada_9 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_10_processoID ON public.processos_particionada_10 ("processoID");
CREATE INDEX idx_processos_particionada_10_dataPrimeiroMovimento ON public.processos_particionada_10 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_11_processoID ON public.processos_particionada_11 ("processoID");
CREATE INDEX idx_processos_particionada_11_dataPrimeiroMovimento ON public.processos_particionada_11 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_12_processoID ON public.processos_particionada_12 ("processoID");
CREATE INDEX idx_processos_particionada_12_dataPrimeiroMovimento ON public.processos_particionada_12 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_13_processoID ON public.processos_particionada_13 ("processoID");
CREATE INDEX idx_processos_particionada_13_dataPrimeiroMovimento ON public.processos_particionada_13 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_14_processoID ON public.processos_particionada_14 ("processoID");
CREATE INDEX idx_processos_particionada_14_dataPrimeiroMovimento ON public.processos_particionada_14 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_15_processoID ON public.processos_particionada_15 ("processoID");
CREATE INDEX idx_processos_particionada_15_dataPrimeiroMovimento ON public.processos_particionada_15 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_16_processoID ON public.processos_particionada_16 ("processoID");
CREATE INDEX idx_processos_particionada_16_dataPrimeiroMovimento ON public.processos_particionada_16 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_17_processoID ON public.processos_particionada_17 ("processoID");
CREATE INDEX idx_processos_particionada_17_dataPrimeiroMovimento ON public.processos_particionada_17 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_18_processoID ON public.processos_particionada_18 ("processoID");
CREATE INDEX idx_processos_particionada_18_dataPrimeiroMovimento ON public.processos_particionada_18 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_19_processoID ON public.processos_particionada_19 ("processoID");
CREATE INDEX idx_processos_particionada_19_dataPrimeiroMovimento ON public.processos_particionada_19 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_20_processoID ON public.processos_particionada_20 ("processoID");
CREATE INDEX idx_processos_particionada_20_dataPrimeiroMovimento ON public.processos_particionada_20 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_21_processoID ON public.processos_particionada_21 ("processoID");
CREATE INDEX idx_processos_particionada_21_dataPrimeiroMovimento ON public.processos_particionada_21 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_22_processoID ON public.processos_particionada_22 ("processoID");
CREATE INDEX idx_processos_particionada_22_dataPrimeiroMovimento ON public.processos_particionada_22 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_23_processoID ON public.processos_particionada_23 ("processoID");
CREATE INDEX idx_processos_particionada_23_dataPrimeiroMovimento ON public.processos_particionada_23 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_24_processoID ON public.processos_particionada_24 ("processoID");
CREATE INDEX idx_processos_particionada_24_dataPrimeiroMovimento ON public.processos_particionada_24 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_25_processoID ON public.processos_particionada_25 ("processoID");
CREATE INDEX idx_processos_particionada_25_dataPrimeiroMovimento ON public.processos_particionada_25 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_26_processoID ON public.processos_particionada_26 ("processoID");
CREATE INDEX idx_processos_particionada_26_dataPrimeiroMovimento ON public.processos_particionada_26 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_27_processoID ON public.processos_particionada_27 ("processoID");
CREATE INDEX idx_processos_particionada_27_dataPrimeiroMovimento ON public.processos_particionada_27 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_28_processoID ON public.processos_particionada_28 ("processoID");
CREATE INDEX idx_processos_particionada_28_dataPrimeiroMovimento ON public.processos_particionada_28 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_29_processoID ON public.processos_particionada_29 ("processoID");
CREATE INDEX idx_processos_particionada_29_dataPrimeiroMovimento ON public.processos_particionada_29 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_30_processoID ON public.processos_particionada_30 ("processoID");
CREATE INDEX idx_processos_particionada_30_dataPrimeiroMovimento ON public.processos_particionada_30 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_31_processoID ON public.processos_particionada_31 ("processoID");
CREATE INDEX idx_processos_particionada_31_dataPrimeiroMovimento ON public.processos_particionada_31 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_32_processoID ON public.processos_particionada_32 ("processoID");
CREATE INDEX idx_processos_particionada_32_dataPrimeiroMovimento ON public.processos_particionada_32 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_33_processoID ON public.processos_particionada_33 ("processoID");
CREATE INDEX idx_processos_particionada_33_dataPrimeiroMovimento ON public.processos_particionada_33 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_34_processoID ON public.processos_particionada_34 ("processoID");
CREATE INDEX idx_processos_particionada_34_dataPrimeiroMovimento ON public.processos_particionada_34 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_35_processoID ON public.processos_particionada_35 ("processoID");
CREATE INDEX idx_processos_particionada_35_dataPrimeiroMovimento ON public.processos_particionada_35 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_36_processoID ON public.processos_particionada_36 ("processoID");
CREATE INDEX idx_processos_particionada_36_dataPrimeiroMovimento ON public.processos_particionada_36 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_37_processoID ON public.processos_particionada_37 ("processoID");
CREATE INDEX idx_processos_particionada_37_dataPrimeiroMovimento ON public.processos_particionada_37 ("dataPrimeiroMovimento");

CREATE UNIQUE INDEX idx_unq_processos_particionada_38_processoID ON public.processos_particionada_38 ("processoID");
CREATE INDEX idx_processos_particionada_38_dataPrimeiroMovimento ON public.processos_particionada_38 ("dataPrimeiroMovimento");


CREATE TABLE movimentos_particionada_0 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 0);
CREATE TABLE movimentos_particionada_1 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 1);
CREATE TABLE movimentos_particionada_2 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 2);
CREATE TABLE movimentos_particionada_3 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 3);
CREATE TABLE movimentos_particionada_4 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 4);
CREATE TABLE movimentos_particionada_5 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 5);
CREATE TABLE movimentos_particionada_6 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 6);
CREATE TABLE movimentos_particionada_7 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 7);
CREATE TABLE movimentos_particionada_8 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 8);
CREATE TABLE movimentos_particionada_9 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 9);
CREATE TABLE movimentos_particionada_10 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 10);
CREATE TABLE movimentos_particionada_11 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 11);
CREATE TABLE movimentos_particionada_12 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 12);
CREATE TABLE movimentos_particionada_13 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 13);
CREATE TABLE movimentos_particionada_14 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 14);
CREATE TABLE movimentos_particionada_15 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 15);
CREATE TABLE movimentos_particionada_16 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 16);
CREATE TABLE movimentos_particionada_17 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 17);
CREATE TABLE movimentos_particionada_18 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 18);
CREATE TABLE movimentos_particionada_19 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 19);
CREATE TABLE movimentos_particionada_20 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 20);
CREATE TABLE movimentos_particionada_21 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 21);
CREATE TABLE movimentos_particionada_22 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 22);
CREATE TABLE movimentos_particionada_23 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 23);
CREATE TABLE movimentos_particionada_24 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 24);
CREATE TABLE movimentos_particionada_25 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 25);
CREATE TABLE movimentos_particionada_26 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 26);
CREATE TABLE movimentos_particionada_27 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 27);
CREATE TABLE movimentos_particionada_28 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 28);
CREATE TABLE movimentos_particionada_29 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 29);
CREATE TABLE movimentos_particionada_30 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 30);
CREATE TABLE movimentos_particionada_31 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 31);
CREATE TABLE movimentos_particionada_32 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 32);
CREATE TABLE movimentos_particionada_33 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 33);
CREATE TABLE movimentos_particionada_34 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 34);
CREATE TABLE movimentos_particionada_35 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 35);
CREATE TABLE movimentos_particionada_36 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 36);
CREATE TABLE movimentos_particionada_37 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 37);
CREATE TABLE movimentos_particionada_38 PARTITION OF movimentos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 38);


CREATE TABLE complementos_particionada_0 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 0);
CREATE TABLE complementos_particionada_1 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 1);
CREATE TABLE complementos_particionada_2 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 2);
CREATE TABLE complementos_particionada_3 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 3);
CREATE TABLE complementos_particionada_4 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 4);
CREATE TABLE complementos_particionada_5 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 5);
CREATE TABLE complementos_particionada_6 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 6);
CREATE TABLE complementos_particionada_7 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 7);
CREATE TABLE complementos_particionada_8 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 8);
CREATE TABLE complementos_particionada_9 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 9);
CREATE TABLE complementos_particionada_10 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 10);
CREATE TABLE complementos_particionada_11 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 11);
CREATE TABLE complementos_particionada_12 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 12);
CREATE TABLE complementos_particionada_13 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 13);
CREATE TABLE complementos_particionada_14 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 14);
CREATE TABLE complementos_particionada_15 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 15);
CREATE TABLE complementos_particionada_16 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 16);
CREATE TABLE complementos_particionada_17 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 17);
CREATE TABLE complementos_particionada_18 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 18);
CREATE TABLE complementos_particionada_19 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 19);
CREATE TABLE complementos_particionada_20 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 20);
CREATE TABLE complementos_particionada_21 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 21);
CREATE TABLE complementos_particionada_22 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 22);
CREATE TABLE complementos_particionada_23 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 23);
CREATE TABLE complementos_particionada_24 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 24);
CREATE TABLE complementos_particionada_25 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 25);
CREATE TABLE complementos_particionada_26 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 26);
CREATE TABLE complementos_particionada_27 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 27);
CREATE TABLE complementos_particionada_28 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 28);
CREATE TABLE complementos_particionada_29 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 29);
CREATE TABLE complementos_particionada_30 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 30);
CREATE TABLE complementos_particionada_31 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 31);
CREATE TABLE complementos_particionada_32 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 32);
CREATE TABLE complementos_particionada_33 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 33);
CREATE TABLE complementos_particionada_34 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 34);
CREATE TABLE complementos_particionada_35 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 35);
CREATE TABLE complementos_particionada_36 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 36);
CREATE TABLE complementos_particionada_37 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 37);
CREATE TABLE complementos_particionada_38 PARTITION OF complementos_particionada FOR VALUES WITH (MODULUS 39, REMAINDER 38);

```

5. Migração dos dados existentes na tabela original (não particionada) para tabela particionada por hash.

O comando realizará a migração dos dados da tabela original `processos_18006` para tabela particionada `processos_particionada`. 

> Atenção: Foi necessário aplicar o filtro `"dataPrimeiroMovimento" IS NOT NULL` pois existem registros onde o campo utilizado para particionamento é nulo.

```sql

-- processos_particionada

INSERT INTO public.processos_particionada
SELECT *, EXTRACT(YEAR FROM "dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.processos_18006 WHERE "dataPrimeiroMovimento" IS NOT NULL;

INSERT INTO public.processos_particionada
SELECT *, EXTRACT(YEAR FROM "dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.processos_18007 WHERE "dataPrimeiroMovimento" IS NOT NULL;

INSERT INTO public.processos_particionada
SELECT *, EXTRACT(YEAR FROM "dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.processos_18008 WHERE "dataPrimeiroMovimento" IS NOT NULL;

-- movimentos_particionada

INSERT INTO public.movimentos_particionada
SELECT m.*, EXTRACT(YEAR FROM p."dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.movimentos_18006 m
	INNER JOIN public.processos_18006 p ON p."processoID" = m."processoID";

INSERT INTO public.movimentos_particionada
SELECT m.*, EXTRACT(YEAR FROM p."dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.movimentos_18007 m
	INNER JOIN public.processos_18007 p ON p."processoID" = m."processoID";

INSERT INTO public.movimentos_particionada
SELECT m.*, EXTRACT(YEAR FROM p."dataPrimeiroMovimento") AS "anoPrimeiroMovimento"
	FROM public.movimentos_18008 m
	INNER JOIN public.processos_18008 p ON p."processoID" = m."processoID";

-- complementos_particionada

INSERT INTO public.complementos_particionada
SELECT c.*, m."anoPrimeiroMovimento"
	FROM public.complementos_18006 c
	INNER JOIN public.movimentos_particionada m ON 
		m."unidadeID" = c."unidadeID" AND m.id = c."movimentoID";

INSERT INTO public.complementos_particionada
SELECT c.*, m."anoPrimeiroMovimento"
	FROM public.complementos_18007 c
	INNER JOIN public.movimentos_particionada m ON 
		m."unidadeID" = c."unidadeID" AND m.id = c."movimentoID";

INSERT INTO public.complementos_particionada
SELECT c.*, m."anoPrimeiroMovimento"
	FROM public.complementos_18008 c
	INNER JOIN public.movimentos_particionada m ON 
		m."unidadeID" = c."unidadeID" AND m.id = c."movimentoID";

```

- Total de registros retornando pela query e inseridos na migração: **1.051.311**

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

### 1.5.1 - Tempo de Processamento

| # Threads (Usuários em paralelo) | # Requests / Thread    | # Repetições     | Duração média | Duração mínima | Duração máxima | Duração mediana | 
| -------------------------------- | ---------------------- | ---------------- | ------------- | -------------- | -------------- | --------------- |
| 1                                | 10                     | 10               |     2604,9 ms |      2397,0 ms |      3610,0 ms |       2520,5 ms |
| 2                                | 10                     | 20               |     4653,6 ms |      3486,0 ms |      6856,0 ms |       4489,0 ms |
| 3                                | 10                     | 30               |     5982,3 ms |      2450,0 ms |     14975,0 ms |       4836,0 ms |
| 5                                | 10                     | 50               |     8405,2 ms |      2416,0 ms |     22182,0 ms |       5482,0 ms |
| 8                                | 10                     | 80               |    13600,9 ms |      2393,0 ms |     27838,0 ms |       9471,5 ms |
| 13                               | 10                     | 130              |    25298,3 ms |      4783,0 ms |     43957,0 ms |      27686,0 ms |
| 21                               | 10                     | 210              |    ------- ms |      ------ ms |     ------- ms |      ------- ms |

Constatamos que a partir do cenário com 21 threads simultâneas a estratégia utilizada não permitiu escalar o banco de dados para atender o crescimento
da demanda conforme a execução dos testes, uma vez que com o aumento de usuários em paralelo, a execução da query passou a superar o limite máximo de 
180.000 ms (3 minutos).


### 1.5.2 - Utilização de Recursos  

| # Threads (Em paralelo) | # Requests/Thread | # Repetições | Uso de CPU | Uso de RAM | Disk (read) | Disk (write) | Network I/O (received) | Network I/O (sent) | 
| ----------------------- | ----------------- | ------------ | ---------- | ---------- | ----------- | ------------ | ---------------------  | ------------------ |
| 1                       | 10                | 10           |   398,94 % |    1,75 GB |        0 KB |         0 KB |                2,47 MB |            1,98 GB |
| 2                       | 10                | 20           |   405,96 % |    2,65 GB |        0 KB |         0 KB |                2,68 MB |            3,94 GB |
| 3                       | 10                | 30           |   411,72 % |    2,99 GB |        0 KB |         0 KB |                4,73 MB |            5,92 GB |
| 5                       | 10                | 50           |   414,09 % |    3,58 GB |        0 KB |         0 KB |               12,80 MB |            9,88 GB |
| 8                       | 10                | 80           |   423,27 % |    4,99 GB |        0 KB |         0 KB |               12,70 MB |           15,80 GB |
| 13                      | 10                | 80           |   439,54 % |    6,94 GB |        0 KB |         0 KB |               23,10 MB |           25,70 GB |


Abaixo, estão os screenshots das estatísticas coletadas para cada cenário executado:

#### 1 Thread

![Stats - 1 Thread](./stats-1.jpg)

#### 2 Threads

![Stats - 2 Thread](./stats-2.jpg)

#### 3 Threads

![Stats - 3 Thread](./stats-3.jpg)

#### 5 Threads

![Stats - 5 Thread](./stats-5.jpg)

#### 8 Threads

![Stats - 8 Thread](./stats-8.jpg)

#### 8 Resultado

![Stats - 8 Thread](./stats-8.jpg)


#### 8 Threads

Não foi possível executar o cenário uma vez que o servidor não conseguiu responder as solicitações simultâneas.


### 1.5.3 - Escalabilidade

Para essa métrica, implementamos uma aplicação em Java utilizando Spring Boot, que publica um endpoint REST responsável por executar a query de referência, realizar a leitura do ResultSet, capturando o timestamp inicial e final da execução para cálculo da duração.

Utilizamos a ferramenta JMeter para criar um plano de testes que possibilitou simular a carga de usuários simultâneos utilizando a aplicação.

| # Threads (Usuários em paralelo) | # Requests / Thread    | # Repetições     | Duração média | Duração mínima | Duração máxima | Duração mediana | 
| -------------------------------- | ---------------------- | ---------------- | ------------- | -------------- | -------------- | --------------- |
| 1                                | 10                     | 10               |     9763,9 ms |      8486,0 ms |     11003,0 ms |       9686,0 ms |
| 2                                | 10                     | 20               |    14027,8 ms |      9224,0 ms |     20246,0 ms |      12232,5 ms |
| 3                                | 10                     | 30               |    18119,6 ms |      9288,0 ms |     37184,0 ms |      14618,0 ms |
| 5                                | 10                     | 50               |    28012,6 ms |      9441,0 ms |     61072,0 ms |      22456,0 ms |
| 8                                | 10                     | 80               |    ------- ms |     ------- ms |     ------- ms |      ------- ms |

Constatamos que a partir do cenário com 8 thread simultâneas a estratégia utilizada não permitiu escalar o banco de dados para atender o crescimento
da demanda conforme a execução dos testes, uma vez que com o aumento de usuários em paralelo, a execução da query passou a superar o limite máximo de 
180.000 ms (3 minutos).

### 1.5.4 - Equilíbrio de Carga

Não se aplica.

### 1.5.5 - Taxa de Transferência de Dados (Throughput)

- Comando para ativar o rastreamento de tempos de entrada/saída (I/O) em operações realizadas pelo banco de dados.

```sql
SET track_io_timing = on;

EXPLAIN ANALYZE 
    -- CONSULTA SQL DE REFERÊNCIA
    SELECT * FROM ...;
    
```

- Taxa: **3.364.537 registros** / **7,44 segundos** = **451897,94 registros por segundo**

### 1.5.6 - Custo de Redistribuição

Não se aplica.

### 1.5.7 - Eficiência de Consultas

A eficiência pode ser expressa como uma relação entre o tempo de execução e o número de partições acessadas:

#### Fórmula:

```plaintext 
Eficiência (%) = (1 / Tempo de Execução Total) * (Número de Partições Acessadas / Partições Totais) * 100
```

- Tempo de Execução Total: **10 segundos**
- Número de Partições Acessadas: **1**
- Partições Totais: **1**

> Eficiência (%) = (1 / 10) * (1 / 1) * 100 = **10%**

### 1.5.8 - Consistência de Dados

Essa métrica não se aplica a essa estratégia, uma vez que não existe movimentação de dados, seja no próprio host ou em hosts distintos.

### 1.5.9 - Capacidade de Adaptação

Essa métrica não se aplica a essa estratégia, uma vez que ela não realiza mudanças ou ajustes dinâmicamente.

### 1.5.10 - Custo Operacional

Não foi avaliado o custo operacional pois se trata da estratégia atualmente implementada.