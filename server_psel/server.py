from flask import Flask, jsonify, request
import psycopg2
from flask_cors import CORS  

app = Flask(__name__)
CORS(app)  

def get_db_connection():
    return psycopg2.connect(
        dbname="ans_relatorios",
        user="admin",
        password="admin",
        host="localhost",
        port="5432"
    )

def busca_operadoras_por_termo(termo):
    conn = get_db_connection()
    cur = conn.cursor()
    
    try:
        cur.execute("""
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
        """, (f'%{termo}%', f'%{termo}%', f'%{termo}%', f'%{termo}%', f'%{termo}%'))
        
        colnames = [desc[0] for desc in cur.description]
        return [dict(zip(colnames, row)) for row in cur.fetchall()]
        
    finally:
        cur.close()
        conn.close()

def execute_query(query):
    conn = get_db_connection()
    cur = conn.cursor()
    
    cur.execute(query)
    
    colnames = [desc[0] for desc in cur.description]
    rows = cur.fetchall()
    resultado = [dict(zip(colnames, row)) for row in rows]
    
    cur.close()
    conn.close()
    
    return resultado

def get_despesas_trimestrais():
    query = """
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
    """
    return execute_query(query)


def get_despesas_anuais():
    query = """
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
    """
    return execute_query(query)

@app.route('/operadoras/busca', methods=['GET'])
def busca_operadoras():
    termo = request.args.get('termo', '').strip()
    
    if not termo:
        return jsonify({"erro": "O parâmetro 'termo' é obrigatório"}), 400
    
    resultados = busca_operadoras_por_termo(termo)
    return jsonify(resultados)

@app.route('/despesas/trimestrais', methods=['GET'])
def despesas_trimestrais():
    dados = get_despesas_trimestrais()
    return jsonify(dados)

@app.route('/despesas/anuais', methods=['GET'])
def despesas_anuais():
    dados = get_despesas_anuais()
    return jsonify(dados)

if __name__ == '__main__':
    app.run(port=8080, debug=True)
