<template>
  <div class="app-container">
    <header class="app-header">
      <h1 class="app-title">Consulta de Operadoras de Saúde</h1>
      <p class="app-subtitle">Dados atualizados do sistema ANS</p>
    </header>

    <nav class="app-nav">
      <button 
        @click="menu = 'busca'" 
        :class="{ active: menu === 'busca' }"
        class="nav-button"
      >
        <span class="icon">🔍</span> Buscar Operadoras
      </button>
      <button 
        @click="menu = 'anuais'" 
        :class="{ active: menu === 'anuais' }"
        class="nav-button"
      >
        <span class="icon">📊</span> Despesas Anuais
      </button>
      <button 
        @click="menu = 'trimestrais'" 
        :class="{ active: menu === 'trimestrais' }"
        class="nav-button"
      >
        <span class="icon">📉</span> Despesas Trimestrais
      </button>
    </nav>

    <main class="app-content">
      <transition name="fade" mode="out-in">
        <div v-if="menu === 'busca'" key="busca">
          <BuscaOperadoras />
        </div>
       <div v-else-if="menu === 'anuais'" :key="menu + '-v1'">
        <DespesasAnuais />
      </div>

        <div v-else-if="menu === 'trimestrais'" key="trimestrais">
          <DespesasTrimestrais />
        </div>
      </transition>
    </main>

    <footer class="app-footer">
      <p>© 2024 Sistema de Consulta ANS | Dados atualizados em {{ currentDate }}</p>
    </footer>
  </div>
</template>

<script>
import BuscaOperadoras from './components/BuscaOperadoras.vue';
import DespesasAnuais from './components/DespesasAnuais.vue';
import DespesasTrimestrais from './components/DespesasTrimestrais.vue';

export default {
  data() {
    return {
      menu: 'busca',
      currentDate: new Date().toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    };
  },
  components: {
    BuscaOperadoras,
    DespesasAnuais,
    DespesasTrimestrais,
  },
};
</script>

<style>
:root {
  --primary-color: #282a36;
  --secondary-color: #44475a;
  --accent-color: #6272a4;
  --light-color: #f8f8f2;
  --background-color: #ffffff;
  --hover-color: #f1f3f8;
  --border-radius: 8px;
  --box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  --transition: all 0.3s ease;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

body {
  background-color: var(--background-color);
  color: var(--primary-color);
  line-height: 1.6;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header {
  text-align: center;
  padding: 2rem 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.app-title {
  font-size: 2.2rem;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.app-subtitle {
  font-size: 1rem;
  color: var(--secondary-color);
  opacity: 0.8;
}

.app-nav {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.app-content {
  flex: 1;
  padding: 1rem 0;
}

.app-footer {
  text-align: center;
  padding: 1.5rem 0;
  margin-top: 2rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  font-size: 0.9rem;
  color: var(--secondary-color);
}

.nav-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  border: 1px solid var(--primary-color);
  background-color: var(--background-color);
  color: var(--primary-color);
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: var(--transition);
  font-weight: 500;
  font-size: 1rem;
}

.nav-button:hover {
  background-color: var(--hover-color);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow);
}

.nav-button.active {
  background-color: var(--primary-color);
  color: var(--background-color);
}

.nav-button .icon {
  font-size: 1.1rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 768px) {
  .app-nav {
    flex-direction: column;
    align-items: center;
  }
  
  .app-title {
    font-size: 1.8rem;
  }
}
</style>