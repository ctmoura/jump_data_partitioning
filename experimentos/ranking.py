import pandas as pd
import numpy as np

# Carregar o CSV para um data frame
csv_file = "./Experimentos_Resultados.csv"  # Nome do arquivo CSV
df = pd.read_csv(csv_file)

# Normaliza a coluna removendo o % e trocando , por .
df["Uso Máx. CPU"] = df["Uso Máx. CPU"].astype(str).str.replace("%", "")


# Convertendo colunas numéricas (substituir vírgula decimal por ponto)
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

# Criando pesos para as métricas
pesos = {
    "Nº Usuários": 2,                 # Quanto maior número de usuários, melhor
    "Taxa Erros (%)": -1,             # Quanto menor a taxa de erros, melhor
    "Duração Média": -2,              # Quanto menor a duração média, melhor
    "Tam. Arq. Temp. (GB)": -1,       # Quanto menor o tamanho, melhor
    "Cache Hit (%)": 1,               # Quanto maior, melhor
    "Uso Máx. CPU": -1,               # Quanto menor, melhor
    "Uso Máx. Memória (GB)": -1       # Quanto menor, melhor
}

# Normalizando os dados
df_norm = df.copy()
for col, peso in pesos.items():
    df_norm[col] = normalize(col, invert=(peso < 0)) * abs(peso)

# Criando uma métrica composta
df_norm["Score"] = df_norm[list(pesos.keys())].sum(axis=1)

# Calculando a média do score por estratégia
df_result = df_norm.groupby("Experimento")["Score"].mean().reset_index()
df_result = df_result.sort_values(by="Score", ascending=False)

# Exibir os resultados ordenados
print(df_result)