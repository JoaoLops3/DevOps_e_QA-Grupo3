const { createApp } = Vue;

createApp({
  data() {
    return {
      alunos: [],
      alunoId: null,
      progresso: null,
      nomeNovo: "",
      conclusao: {
        tituloCurso: "",
        media: 8.0,
      },
      loading: false,
      erro: "",
      sucesso: "",
    };
  },
  mounted() {
    this.carregarAlunos();
  },
  methods: {
    async carregarAlunos() {
      this.loading = true;
      this.erro = "";
      try {
        const res = await fetch("/api/alunos");
        if (!res.ok) {
          throw new Error("Não foi possível listar os alunos.");
        }
        this.alunos = await res.json();
        if (this.alunos.length > 0) {
          if (!this.alunoId || !this.alunos.some((a) => a.id === this.alunoId)) {
            this.alunoId = this.alunos[0].id;
          }
          await this.carregarProgresso();
        } else {
          this.alunoId = null;
          this.progresso = null;
        }
      } catch (e) {
        this.erro = e.message || "Falha de rede ao carregar alunos.";
      } finally {
        this.loading = false;
      }
    },

    async criarAluno() {
      if (!this.nomeNovo) {
        this.erro = "Informe o nome do aluno.";
        return;
      }
      this.loading = true;
      this.erro = "";
      this.sucesso = "";
      try {
        const res = await fetch("/api/alunos", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ nome: this.nomeNovo }),
        });
        if (!res.ok) {
          const body = await res.json().catch(() => ({}));
          throw new Error(body.detail || "Não foi possível criar o aluno.");
        }
        const criado = await res.json();
        this.nomeNovo = "";
        this.sucesso = `Aluno ${criado.nome} criado.`;
        await this.carregarAlunos();
        this.alunoId = criado.id;
        await this.carregarProgresso();
      } catch (e) {
        this.erro = e.message || "Falha ao criar aluno.";
      } finally {
        this.loading = false;
      }
    },

    async carregarProgresso() {
      if (!this.alunoId) {
        this.progresso = null;
        return;
      }
      this.erro = "";
      try {
        const res = await fetch(`/api/alunos/${this.alunoId}/progresso`);
        if (!res.ok) {
          throw new Error("Não foi possível carregar o progresso.");
        }
        this.progresso = await res.json();
      } catch (e) {
        this.erro = e.message || "Falha ao carregar progresso.";
      }
    },

    async registrarConclusao() {
      if (!this.alunoId) {
        this.erro = "Selecione ou crie um aluno antes.";
        return;
      }
      this.loading = true;
      this.erro = "";
      this.sucesso = "";
      try {
        const res = await fetch(`/api/alunos/${this.alunoId}/conclusoes`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            tituloCurso: this.conclusao.tituloCurso,
            media: this.conclusao.media,
          }),
        });
        if (!res.ok) {
          const body = await res.json().catch(() => ({}));
          const campo = body.campos ? Object.values(body.campos).join(" ") : "";
          throw new Error(campo || body.detail || "Não foi possível registrar a conclusão.");
        }
        this.progresso = await res.json();
        const contou = this.conclusao.media >= 7;
        this.sucesso = contou
          ? "Conclusão válida registrada no progresso."
          : "Conclusão salva, mas média abaixo de 7 não conta no progresso.";
        this.conclusao.tituloCurso = "";
        await this.carregarAlunos();
      } catch (e) {
        this.erro = e.message || "Falha ao registrar conclusão.";
      } finally {
        this.loading = false;
      }
    },
  },
}).mount("#app");
