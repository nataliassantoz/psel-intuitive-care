# 📌 Projeto de Testes de Nivelamento - ANS

Este repositório contém a implementação das tarefas propostas no documento **TESTES DE NIVELAMENTO**. As atividades estão divididas em quatro tarefas principais: *web scraping*, transformação de dados, banco de dados e desenvolvimento de *API*.

> ✅ **Tecnologia utilizada:** Java 17

---

## 🚀 Execução do Projeto

### 📄  Tarefa 1 e 2 — Web Scraping e Transformação de Dados

1.  Acesse a raiz do projeto em: *psel-intuitive-care/projeto/*
2. Em seguida, execute os seguintes comandos Maven na sua IDE de preferência ou via terminal:
 ```
mvn clean
```
 ```
mvn install
```
3. A execução gerará automaticamente:
   - A pasta `anexos_ans/`, contendo os arquivos Anexo I e Anexo II (em PDF), além do arquivo compactado (.zip).
   - A pasta `anexos_csv/`, contendo o arquivo `.csv` transformado conforme solicitado, com os dados extraídos do Anexo I, e seu respectivo `.zip`.

---

### 🐘 Tarefa 3 — Banco de Dados

1. Acesse o diretório `db/`:

```bash
cd db/
```

2. **Necessário**: Antes de subir os containers, limpe os volumes e imagens anteriores com:

```bash
docker-compose down --rmi all --volumes --remove-orphans
```

3. Em seguida, construa e suba os containers com:

```bash
docker-compose up --build
```

> Isso irá configurar um banco de dados PostgreSQL e executar os scripts de criação e povoamento do banco com dados públicos da ANS.

⚠️ **A senha do banco de dados foi disponibilizada exclusivamente para fins de teste.**

---

### ⚙️ Tarefa 4 — API
1. Antes de executar os testes da Tarefa 4, é necessário configurar um ambiente virtual com Python. Siga os passos abaixo conforme seu sistema operacional:
```bash
python -m venv venv
```
> Esse comando cria uma pasta chamada venv/, que conterá os arquivos do ambiente isolado.

2. Ative o ambiente virtual:
3. 🔵 Windows:
 ```bash
venv\Scripts\activate
```
4. 🐧 Linux / 🧑‍🍳 macOS:
 ```bash
source venv/bin/activate
```
Após ativar, você verá o nome do ambiente aparecendo no início da linha do terminal, indicando que está tudo certo. Algo como: (venv)
5. Com o ambiente ativado, instale as dependências do projeto:
 ```bash
pip install -r requirements.txt
```
5. Certifique-se de que a Tarefa 3 está em execução (PostgreSQL ativo via Docker).
6. Acesse o diretório `serve_psel/` e execute o servidor:
```bash
python server.py
```
O servidor estará disponível em `http://localhost:8080`.
---

## 🧪 Testes via Postman

Na pasta `collections_postman/` há uma coleção com três *requests* para testar a aplicação:

1. 📅  **Despesas Trimestrais**
   - URL: `http://localhost:8080/despesas/trimestrais`
   - Descrição: Retorna as 10 operadoras com maiores despesas em **"EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR"** no último trimestre.

2. 📊 **Despesas Anuais**
   - URL: `http://localhost:8080/despesas/anuais`
   - Descrição: Retorna as 10 operadoras com maiores despesas na mesma categoria no ano de 2024.

3. 🔍 **Busca Textual**
   - URL: `http://localhost:8080/operadoras/busca?termo=*busca*`
   - Descrição: Realiza busca textual nas operadoras de saúde, podendo filtrar por razão social, nome fantasia, número de registro da ANS, cidade e UF.

---

## 🧪 Exemplos de Consultas SQL Utilizadas

A seguir estão explicações e exemplos reais das consultas executadas pelas rotas da *API*:

---

### 🔎 Busca Textual em Operadoras

**Rota:** `GET /operadoras/busca?termo=...`

```sql
SELECT 
    registro_ans,
    razao_social,
    nome_fantasia,
    cidade,
    uf
FROM tb_operadoras 
WHERE 
    razao_social ILIKE %s OR
    nome_fantasia ILIKE %s OR
    CAST(registro_ans AS TEXT) LIKE %s OR
    cidade ILIKE %s OR
    uf ILIKE %s
LIMIT 50;
```

---

### 💸 Despesas Trimestrais

**Rota:** `GET /despesas/trimestrais`

```sql
WITH ultimas_datas AS (
    SELECT MAX(data) AS data_mais_recente 
    FROM tb_demonstracoes_contabeis
),
periodo_trimestre AS (
    SELECT 
        data_mais_recente,
        (data_mais_recente - INTERVAL '3 months') AS data_limite
    FROM ultimas_datas
),
despesas_filtradas AS (
    SELECT 
        dc.reg_ans,
        op.razao_social,
        REPLACE(dc.vl_saldo_final::TEXT, ',', '.')::NUMERIC AS valor    
    FROM tb_demonstracoes_contabeis dc
    JOIN tb_operadoras op ON op.registro_ans = dc.reg_ans
    JOIN periodo_trimestre pt 
        ON dc.data BETWEEN pt.data_limite AND pt.data_mais_recente
    WHERE REGEXP_REPLACE(TRIM(dc.descricao), '\s+', ' ', 'g') ILIKE 
          '%EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR%'
)
SELECT 
    reg_ans,
    razao_social,
    ROUND(SUM(valor), 2) AS total_despesa
FROM despesas_filtradas
GROUP BY reg_ans, razao_social
ORDER BY total_despesa DESC
LIMIT 10;
```

---

### 💰 Despesas Anuais

**Rota:** `GET /despesas/anuais`

```sql
WITH ultimas_datas AS (
    SELECT MAX(data) AS data_mais_recente 
    FROM tb_demonstracoes_contabeis
),
periodo_anual AS (
    SELECT 
        data_mais_recente,
        (data_mais_recente - INTERVAL '1 year') AS data_limite
    FROM ultimas_datas
),
despesas_filtradas AS (
    SELECT 
        dc.reg_ans,
        op.razao_social,
        REPLACE(dc.vl_saldo_final::TEXT, ',', '.')::NUMERIC AS valor   
    FROM tb_demonstracoes_contabeis dc
    JOIN tb_operadoras op ON op.registro_ans = dc.reg_ans
    JOIN periodo_anual pa
        ON dc.data BETWEEN pa.data_limite AND pa.data_mais_recente
    WHERE REGEXP_REPLACE(TRIM(dc.descricao), '\s+', ' ', 'g') ILIKE 
          '%EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR%'
)
SELECT 
    reg_ans,
    razao_social,
    ROUND(SUM(valor), 2) AS total_despesa_anual
FROM despesas_filtradas
GROUP BY reg_ans, razao_social
ORDER BY total_despesa_anual DESC
LIMIT 10;
```

---

## :lock: Observações

- Todos os dados utilizados são públicos e extraídos dos portais oficiais da ANS.
- A senha as credenciais de banco de dados poderiam estar em um .env mas para fim de testes preferi deixar diretamente no código.

- O projeto foi desenvolvido em **Java 17**.