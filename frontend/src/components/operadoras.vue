<!-- src/components/Operadoras.vue -->
<template>
  <div>
    <h2>Buscar Operadoras</h2>
    <input v-model="termo" placeholder="Digite o termo de busca" />
    <button @click="buscarOperadoras">Buscar</button>

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
    async buscarOperadoras() {
      if (!this.termo.trim()) return;
      try {
        const res = await fetch(`http://localhost:8080/operadoras/busca?termo=${this.termo}`);
        this.resultados = await res.json();
      } catch (err) {
        alert('Erro ao buscar operadoras');
        console.error(err);
      }
    },
  },
};
</script>
