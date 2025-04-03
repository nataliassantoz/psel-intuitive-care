<template>
  <div>
    <h2>Buscar Operadoras de Saúde</h2>
    <input v-model="termo" placeholder="Digite razão social, registro, UF, etc." />
    <button @click="buscar">Buscar</button>

    <ul v-if="resultados.length">
      <li v-for="op in resultados" :key="op.registro_ans">
        {{ op.razao_social }} ({{ op.uf }}) - {{ op.nome_fantasia }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      termo: '',
      resultados: [],
    };
  },
  methods: {
    async buscar() {
      if (!this.termo.trim()) return;
      const res = await fetch(`http://localhost:8080/operadoras/busca?termo=${this.termo}`);
      this.resultados = await res.json();
    },
  },
};
</script>
