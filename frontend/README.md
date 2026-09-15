# Frontend Vue.js — Rumo Premium

SPA **Vue 3 via CDN** (sem Node/build).

**Fonte da verdade:** esta pasta `frontend/`.  
No build Maven (`generate-resources`), o conteúdo é copiado para `src/main/resources/static/` (excluindo este README), evitando dessincronizar o HTML/CSS/JS servido pelo Spring.

## Como ver

Com a API no ar:

- http://localhost:8080/

## O que faz

- Criar aluno (assinatura BASICA)
- Registrar conclusão de curso
- Barra de progresso (X/12), assinatura e moedas
- Estados de loading, erro e lista vazia
