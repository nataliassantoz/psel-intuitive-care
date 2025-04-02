CREATE TABLE tb_operadoras (
    registro_ans INT PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social TEXT,
    nome_fantasia TEXT,
    modalidade TEXT,
    logradouro TEXT,
    numero TEXT,
    complemento TEXT,
    bairro TEXT,
    cidade TEXT,
    uf CHAR(2),
    cep VARCHAR(10),
    ddd VARCHAR(5),
    telefone VARCHAR(20),
    fax VARCHAR(20),
    email TEXT,
    representante TEXT,
    cargo_representante TEXT,
    regiao_comercializacao INT,
    data_registro_ans DATE
);

CREATE TABLE tb_demonstracoes_contabeis (
    data DATE,
    reg_ans INT,
    cd_conta_contabil VARCHAR(20),
    descricao TEXT,
    vl_saldo_inicial DECIMAL(18,2),
    vl_saldo_final DECIMAL(18,2),
    campo_extra1 INTEGER,
    campo_extra2 INTEGER,
    FOREIGN KEY (reg_ans) REFERENCES tb_operadoras(registro_ans)
);

CREATE TABLE tmp_demonstracoes (
    data TEXT,
    reg_ans TEXT,
    cd_conta_contabil TEXT,
    descricao TEXT,
    vl_saldo_inicial TEXT,
    vl_saldo_final TEXT
);

SET datestyle = "ISO, DMY";

COPY tb_operadoras FROM '/docker-entrypoint-initdb.d/Relatorio_cadop.csv' DELIMITER ';' CSV HEADER;

COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/1T2023.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/2T2023.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/3T2023.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/4T2023.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/1T2024.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/2T2024.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/3T2024.csv' DELIMITER ';' CSV HEADER;
COPY tmp_demonstracoes FROM '/docker-entrypoint-initdb.d/4T2024.csv' DELIMITER ';' CSV HEADER;


INSERT INTO tb_demonstracoes_contabeis (
    data,
    reg_ans,
    cd_conta_contabil,
    descricao,
    vl_saldo_inicial,
    vl_saldo_final,
    campo_extra1,
    campo_extra2
)
SELECT
    data::DATE,
    reg_ans::INT,
    cd_conta_contabil,
    descricao,
    REPLACE(vl_saldo_inicial, ',', '.')::DECIMAL,
    REPLACE(vl_saldo_final, ',', '.')::DECIMAL,
    NULL,
    NULL
FROM tmp_demonstracoes
WHERE reg_ans::INT IN (
    SELECT registro_ans FROM tb_operadoras
);


