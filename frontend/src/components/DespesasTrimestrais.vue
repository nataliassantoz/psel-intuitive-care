<template>
  <div>
    <h2>Top 10 Despesas Trimestrais</h2>

    <ul v-if="dados.length">
      <li v-for="item in dados" :key="item.reg_ans">
        {{ item.razao_social }} - R$ {{ formatarValor(item.total_despesa) }}
      </li>
    </ul>

    <p v-else>Carregando. Aguarde!.</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dados: [],
    };
  },
  methods: {
    async fetchDados() {
      try {
        const res = await fetch("http://localhost:8080/despesas/trimestrais");
        if (!res.ok) throw new Error("Erro ao buscar dados");
        const json = await res.json();
        console.log("Dados trimestrais recebidos:", json); 
        this.dados = json;
      } catch (e) {
        console.error("Erro ao buscar despesas trimestrais:", e);
      }
    },
    formatarValor(valor) {
      return valor ? parseFloat(valor).toFixed(2) : "0.00";
    },
  },
  mounted() {
    this.fetchDados();
  },
};
</script>
