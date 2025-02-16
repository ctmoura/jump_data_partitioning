
O Analytic Network Process (ANP) é uma extensão do AHP que permite modelar interdependências e relações de feedback entre os critérios e alternativas. Ao invés de uma estrutura hierárquica pura, o ANP organiza os elementos em clusters (grupos) que podem influenciar uns aos outros. Vou esboçar os passos e a estrutura básica de como ficaria um ANP aplicado ao seu cenário:

1. **Definir os Clusters e Elementos:**
    
    - **Cluster de Critérios:** Por exemplo, “Performance”, “Confiabilidade”, “Escalabilidade” e “Custo”.
    - **Cluster de Alternativas:** Os diferentes cenários ou estratégias que você está comparando.
2. **Modelar as Relações e Interdependências:**
    
    - Identifique quais critérios influenciam outros. Por exemplo, a “Escalabilidade” pode impactar a “Performance” e vice-versa.
    - Defina relações entre os elementos dos clusters de critérios e as alternativas (como no AHP, mas agora considerando que um critério pode depender de outros).
3. **Realizar as Comparações Par a Par:**
    
    - Para cada relação identificada (tanto entre elementos dentro do mesmo cluster quanto entre clusters), realize comparações par a par para quantificar a influência relativa.
    - Por exemplo, compare “Escalabilidade” e “Performance” com relação ao impacto no cenário “Aumento de Usuários”.
4. **Construir a Supermatriz:**
    
    - Organize os resultados das comparações em uma supermatriz, onde cada célula representa a influência de um elemento sobre outro.
    - Essa supermatriz pode ter partes “intra-cluster” (dentro do mesmo grupo) e “inter-cluster” (entre grupos).
5. **Ponderar e Normalizar a Supermatriz (Supermatriz Ponderada ou Limitada):**
    
    - Ajuste os pesos para garantir que a influência total de cada cluster seja considerada.
    - A supermatriz resultante é normalizada de forma que a soma dos pesos em cada coluna seja 1.
6. **Calcular os Pesos Globais:**
    
    - Eleve a supermatriz a potências sucessivas (ou use métodos iterativos) até que ela converja a um vetor de prioridades estáveis para cada elemento.
    - Esses pesos globais indicam a importância relativa dos critérios e, consequentemente, o desempenho dos cenários avaliados.
7. **Analisar os Resultados e Fazer a Escolha:**
    
    - Os cenários (alternativas) receberão um score final baseado nos pesos globais agregados a partir das inter-relações dos critérios.
    - Esse score permite identificar qual cenário é mais robusto considerando todas as interdependências.


### Ferramentas e Implementação

Embora a implementação do ANP seja mais complexa que a do AHP devido à necessidade de lidar com uma supermatriz e suas iterações, existem ferramentas e bibliotecas (em Python, MATLAB, etc.) que podem ajudar. Alguns pacotes em Python podem ser adaptados para ANP, mas muitas vezes é necessário implementar parte da lógica de iteração e normalização manualmente ou utilizar softwares especializados como o Super Decisions.

Em resumo, o ANP oferece uma abordagem mais rica para cenários onde os critérios se influenciam mutuamente, proporcionando uma análise que reflete melhor a complexidade do sistema. Essa abordagem pode ser especialmente útil quando o número de usuários está aumentando, e os efeitos entre escalabilidade, performance, confiabilidade e outros critérios se tornam interdependentes.

## Sugestão de implementação

A seguir, segue um exemplo simplificado de como você poderia estruturar uma análise usando ANP, aproveitando a base de dados e a estrutura que você já tem em Python. Esse exemplo cria uma “supermatriz” que integra:

1. **Interdependências entre critérios:** Aqui, para simplificação, usamos uma comparação uniforme (mas, na prática, você usaria comparações par a par fundamentadas).
2. **Relação de critérios para alternativas:** Usamos os valores normalizados que você já calculou (por exemplo, a média dos valores normalizados por “Experimento”).
3. **Feedback das alternativas para os critérios:** Neste exemplo, para simplificar, consideramos que as alternativas não influenciam os critérios (ou seja, essa parte é zerada).
4. **Interdependências entre as alternativas:** Assumimos independência entre elas (usando uma matriz identidade normalizada).

Após construir a supermatriz, fazemos uma iteração para obter o limite (ou seja, a matriz de convergência) e, por fim, extraímos as prioridades finais para os experimentos (alternativas).

> **Observação:** Este exemplo é didático e simplificado. Em uma aplicação real de ANP, você definirá os valores de comparações par a par com base no julgamento dos especialistas e modelará as inter-relações de forma mais robusta.

---

```python
import numpy as np
import pandas as pd

# --- Parte 1: Preparação dos dados (conforme seu código) ---

# Converter colunas numéricas (substituir vírgula decimal por ponto)
numeric_columns = [
    "Nº Usuários", "Taxa Erros (%)", "Menor Duração", "Maior Duração", "Duração Média",
    "Tam. Arq. Temp. (GB)", "Cache Hit (%)", "Uso Máx. CPU", "Uso Máx. Memória (GB)"
]
for col in numeric_columns:
    df[col] = df[col].astype(str).str.replace(",", ".").astype(float)

# Normalizar as métricas (0 a 1)
def normalize(column, invert=False):
    min_val, max_val = df[column].min(), df[column].max()
    norm = (df[column] - min_val) / (max_val - min_val)
    return 1 - norm if invert else norm

# Definindo os pesos (lembrando que estes serão usados para compor os valores na supermatriz)
pesos = {
    "Nº Usuários": 2,                 # Quanto maior número de usuários, melhor
    "Taxa Erros (%)": -1,             # Quanto menor a taxa de erros, melhor
    "Duração Média": -2,              # Quanto menor a duração média, melhor
    "Tam. Arq. Temp. (GB)": -1,       # Quanto menor o tamanho, melhor
    "Cache Hit (%)": 1,               # Quanto maior, melhor
    "Uso Máx. CPU": -1,               # Quanto menor, melhor
    "Uso Máx. Memória (GB)": -1       # Quanto menor, melhor
}

# Normalizando os dados e ponderando conforme os pesos
df_norm = df.copy()
for col, peso in pesos.items():
    df_norm[col] = normalize(col, invert=(peso < 0)) * abs(peso)

# --- Parte 2: Estruturação do ANP ---

# Definir os clusters:
# - Critérios: os nomes das métricas (colunas) que usamos
# - Alternativas: os diferentes "Experimento"
criteria = list(pesos.keys())
alternatives = df["Experimento"].unique().tolist()

# Lista de todos os elementos (Critérios + Alternativas)
elements = criteria + alternatives
n = len(elements)

# Inicializando a supermatriz com zeros
supermatrix = pd.DataFrame(np.zeros((n, n)), index=elements, columns=elements)

# 1. Bloco de Interdependência entre Critérios (cluster critérios)
#    Para exemplificar, vamos assumir que cada critério influencia os outros com um peso fixo.
n_crit = len(criteria)
criteria_matrix = np.ones((n_crit, n_crit))
for i in range(n_crit):
    for j in range(n_crit):
        if i != j:
            criteria_matrix[i, j] = 0.5  # valor arbitrário para a influência entre critérios
    # Normaliza a linha para que a soma seja 1
    criteria_matrix[i] = criteria_matrix[i] / criteria_matrix[i].sum()

# Inserindo o bloco de critérios na supermatriz
supermatrix.loc[criteria, criteria] = criteria_matrix

# 2. Bloco de Relação: Critérios influenciam Alternativas
#    Para cada critério, calculamos a média dos valores normalizados por "Experimento"
for crit in criteria:
    exp_values = df_norm.groupby("Experimento")[crit].mean()
    # Normalizando os valores para que a soma entre os experimentos seja 1
    exp_norm = exp_values / exp_values.sum()
    for alt in alternatives:
        supermatrix.loc[crit, alt] = exp_norm[alt]

# 3. Bloco de Feedback: Alternativas influenciam Critérios
#    Neste exemplo simplificado, não consideramos essa influência (preenchemos com zero)
supermatrix.loc[alternatives, criteria] = 0

# 4. Bloco de Interdependência entre Alternativas
#    Assumindo independência, usamos uma matriz identidade normalizada
n_alt = len(alternatives)
alt_matrix = np.eye(n_alt)
alt_matrix = alt_matrix / alt_matrix.sum(axis=0, keepdims=True)
supermatrix.loc[alternatives, alternatives] = alt_matrix

# Visualize a supermatriz construída (opcional)
print("Supermatriz Inicial:")
print(supermatrix)

# --- Parte 3: Cálculo da Supermatriz Limite (Iteração até convergência) ---

W = supermatrix.values.copy()
for _ in range(100):
    W_next = np.dot(W, W)
    if np.allclose(W, W_next, atol=1e-6):
        break
    W = W_next

limit_supermatrix = pd.DataFrame(W, index=elements, columns=elements)

# --- Parte 4: Extraindo as Prioridades Finais das Alternativas ---

# Uma forma comum é observar as linhas correspondentes às alternativas.
# Aqui, calculamos a média das influências de cada alternativa (bloco alternativas->alternativas).
final_priorities = limit_supermatrix.loc[alternatives, alternatives].mean(axis=1)

# Organizando o resultado final
final_result = final_priorities.sort_values(ascending=False).reset_index()
final_result.columns = ["Experimento", "Prioridade"]

print("\nPrioridades Finais (Ranking dos Experimentos):")
print(final_result)
```

---

### Explicação Resumida

1. **Preparação dos Dados:**
    
    - Converte e normaliza as colunas numéricas, ponderando os valores conforme os pesos definidos.
2. **Construção da Supermatriz:**
    
    - **Bloco Critérios → Critérios:** Representa a influência mútua entre os critérios. Aqui, usamos um valor fixo de 0.5 para a influência entre critérios (com normalização).
    - **Bloco Critérios → Alternativas:** Para cada critério, calculamos a média dos valores normalizados por “Experimento” e normalizamos esses valores para preencher este bloco.
    - **Bloco Alternativas → Critérios:** Para simplificar, consideramos que não há feedback das alternativas para os critérios.
    - **Bloco Alternativas → Alternativas:** Usamos uma matriz identidade (normalizada), assumindo que as alternativas são independentes.
3. **Iteração para Convergência:**
    
    - A supermatriz é elevada a potências sucessivas até atingir convergência (ou seja, os pesos se estabilizam).
4. **Extração do Ranking Final:**
    
    - A partir do bloco correspondente às alternativas, calculamos as prioridades finais, que indicam o quão robusto cada cenário é na visão do modelo ANP.

Essa estrutura fornece um esqueleto para aplicar o ANP ao seu problema, considerando inter-relações entre critérios e alternativas. Dependendo do seu contexto, você poderá ajustar os valores de influência e a modelagem das interdependências para refletir melhor os julgamentos especializados.

## Referências do GPT (Só verifiquei a primeira)

Algumas referências clássicas e textos de apoio para aprofundar nos métodos AHP, ANP e suas variações são:

- **Saaty, T. L. (1980). _The Analytic Hierarchy Process: Planning, Priority Setting, Resource Allocation_. McGraw-Hill.**  
    Um dos textos fundadores sobre o AHP, que explica a metodologia, a teoria por trás das comparações par a par e a aplicação em diversos contextos.
    
- **Saaty, T. L. (1996). _Decision Making with the Analytic Network Process: The AHP and ANP in Business, Technology, and Healthcare_. RWS Publications.**  
    Este livro apresenta o ANP, explorando como lidar com interdependências entre critérios e alternativas.
    
- **Vargas, L. G. (1990). "An overview of the analytic network process." _European Journal of Operational Research, 48_(3), 249–252.**  
    Artigo que fornece uma visão geral do ANP, ideal para compreender as diferenças e vantagens em relação ao AHP.
    
- **Mikhailov, L. (2003). "Deriving priorities from fuzzy pairwise comparison judgements." _Fuzzy Sets and Systems, 134_(3), 365–385.**  
    Referência para quem deseja explorar a aplicação de lógicas fuzzy em processos de decisão, como o Fuzzy AHP.