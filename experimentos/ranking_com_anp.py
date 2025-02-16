import pandas as pd
import numpy as np

# Carregar o CSV
csv_file = "./Experimentos_Resultados.csv"
df = pd.read_csv(csv_file)

# Limpeza e conversão de dados
df["Uso Máx. CPU"] = df["Uso Máx. CPU"].astype(str).str.replace("%", "").str.replace(",", ".").astype(float)
df["Taxa Erros (%)"] = df["Taxa Erros (%)"].astype(str).str.replace(",", ".").astype(float)

# Convertendo colunas numéricas
numeric_columns = [
    "Nº Usuários", "Taxa Erros (%)", "Menor Duração", "Maior Duração", "Duração Média",
    "Tam. Arq. Temp. (GB)", "Cache Hit (%)", "Uso Máx. CPU", "Uso Máx. Memória (GB)"
]
for col in numeric_columns:
    df[col] = df[col].astype(str).str.replace(",", ".").astype(float)

# Criando rankings individuais
ranking_df = df.copy()

# Definir se a métrica deve ser maximizada (+1) ou minimizada (-1)
criterios = {
    "Nº Usuários": 1,               # Quanto maior, melhor
    "Taxa Erros (%)": -1,           # Quanto menor, melhor
    "Duração Média": -1,            # Quanto menor, melhor
    "Tam. Arq. Temp. (GB)": -1,     # Quanto menor, melhor
    "Cache Hit (%)": 1,              # Quanto maior, melhor
    "Uso Máx. CPU": -1,             # Quanto menor, melhor
    "Uso Máx. Memória (GB)": -1     # Quanto menor, melhor
}

# Criar ranking para cada métrica
for col, peso in criterios.items():
    ranking_df[f"Ranking_{col}"] = ranking_df[col].rank(ascending=(peso < 0))

# Criar uma média ponderada dos rankings para obter um ranking final
ranking_cols = [f"Ranking_{col}" for col in criterios.keys()]
ranking_df["Ranking_Final"] = ranking_df[ranking_cols].mean(axis=1)

# Ordenar pelo ranking final
ranking_df = ranking_df.sort_values(by="Ranking_Final")

# Exibir os resultados ordenados
print(ranking_df)
