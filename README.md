![FIG2](https://github.com/user-attachments/assets/36aa4e00-1e43-4918-afbb-ec608fc60978)

# 📌 Projeto de Testes de Nivelamento - ANS

Este repositório contém a implementação das tarefas propostas no documento **TESTES DE NIVELAMENTO**.  

As atividades estão organizadas em quatro tarefas principais:  

1. **Web Scraping**  
2. **Transformação de Dados**  
3. **Banco de Dados**  
4. **Desenvolvimento de API e interface WEB**  

Cada atividade está separada em branches específicas:  

- **Teste 1** → `feature/1-teste-web-projeto`  
- **Teste 2** → `feature/2-teste-transformacao-dados`  
- **Teste 3** → `feature/3-banco-de-dados`  
- **Teste 4** → `feature/4-teste-API`
- **Teste 4.2** → `feature/5-interface-web`

Todo o processo de gitflow foi tomado a partir de aberturas de pull requests, segue o link: https://github.com/nataliassantoz/psel-intuitive-care/pulls?q=is%3Apr+is%3Aclosed
# Tecnologias Utilizadas

✅ **Java** - Versão 17  
✅ **Python** - Versão 3  
✅ **PostgreSQL** - Versão 15.12  
✅ **Vue.js**  - versão 5.0.8  
✅ **Docker** - Versão Docker Compose v2.34.0  


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
Posteriormente é só executar a main, na IDE.  
3. A execução gerará automaticamente:
   - A pasta `anexos_ans/`, contendo os arquivos Anexo I e Anexo II (em PDF), além do arquivo compactado (.zip).
   - A pasta `anexos_csv/`, contendo o arquivo `.csv` transformado conforme solicitado, com os dados extraídos do Anexo I, e seu respectivo `.zip`.

---

### 🐘 Tarefa 3 — Banco de Dados

1. Acesse o diretório `db/`:

```bash
cd db/
```

2. Necessário: Antes de subir os containers, limpe os volumes e imagens anteriores com:

```bash
docker-compose down --rmi all --volumes --remove-orphans ou docker compose down
```

3. Em seguida, construa e suba os containers com:

```bash
docker compose up --build
```

> Isso irá configurar um banco de dados PostgreSQL e executar os scripts de criação e povoamento do banco com dados públicos da ANS.

⚠️ **A senha do banco de dados foi disponibilizada exclusivamente para fins de teste.**

---

### ⚙️ Tarefa 4 — API
1. Antes de executar os testes da Tarefa 4, é necessário configurar um ambiente virtual com Python. Siga os passos abaixo conforme seu sistema operacional:
```bash
python3 -m venv venv
```
> Esse comando cria uma pasta chamada venv/, que conterá os arquivos do ambiente isolado.

2. Ative o ambiente virtual:
3. 🔵 Windows:
 ```bash
venv\Scripts\activate
```
4. 🐧 Linux /  macOS:
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

7. para consumir a API e integrar o servidor python ao frontend, é necessário rodar o vue.js (**com as aplicações anteriores rodando - docker e servidor python**), primeiramente acesse o diretorio do frontend em:

``` bash
cd frontend
```

No diretorio correto nstale as dependencias com o comando:
```bash
npm install
```

Logo após é necessário rodar o script de desenvolvimento com:
```bash
npm run dev 
```
O vue.js irá rodar na porta 5173. Feito os procedimentos, basta abrir no navegador e acessar:
```bash
http://localhost:5173/
```

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
## 🧪 Interface Web

A rota http://localhost:8080/operadoras/busca?termo=*busca*, pode realizar busca e finalizar pelo botão, conforme o exemplo abaixo:

![FIG1](https://github.com/user-attachments/assets/5305e013-55c2-43b5-961c-5c9fbe701e9f)

A rota http://localhost:8080/despesas/trimestrais, Retorna as 10 operadoras com maiores despesas último trimestre de 2024.
![FIG2](https://github.com/user-attachments/assets/97552012-aa02-4579-8755-aa332580b1fe)


A rota http://localhost:8080/despesas/anuais Retorna as 10 operadoras com maiores despesas em 2024.

![FIG3](https://github.com/user-attachments/assets/5064a63c-9760-4e3a-88a0-97a63a81a843)



---

## :lock: Observações

- Todos os dados utilizados são públicos e extraídos dos portais oficiais da ANS.
- A senha as credenciais de banco de dados poderiam estar em um .env mas para fim de testes preferi deixar diretamente no código.

