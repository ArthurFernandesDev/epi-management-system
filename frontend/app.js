/* =====================================================
   EPI MANAGEMENT SYSTEM — app.js
   Consome a API Spring Boot via fetch()
   ===================================================== */

const API_BASE = 'http://localhost:8080/api';

/* ===================== ESTADO GLOBAL ===================== */
const state = {
  epis: [],
  funcionarios: [],
  entregas: [],
  currentModal: null,   // 'epi' | 'funcionario' | 'entrega'
  editingId: null,      // null = criar, número = editar
};

/* ===================== CURSOR PERSONALIZADO ===================== */
(function initCursor() {
  const cursor   = document.getElementById('cursor');
  const follower = document.getElementById('cursor-follower');
  if (!cursor || !follower) return;
  let mx = 0, my = 0, fx = 0, fy = 0;
  document.addEventListener('mousemove', e => {
    mx = e.clientX; my = e.clientY;
    cursor.style.left = mx + 'px'; cursor.style.top = my + 'px';
  });
  (function anim() {
    fx += (mx - fx) * 0.12; fy += (my - fy) * 0.12;
    follower.style.left = fx + 'px'; follower.style.top = fy + 'px';
    requestAnimationFrame(anim);
  })();
  document.querySelectorAll('a, button, input, select').forEach(el => {
    el.addEventListener('mouseenter', () => follower.classList.add('hover'));
    el.addEventListener('mouseleave', () => follower.classList.remove('hover'));
  });
})();

/* ===================== NAVBAR SCROLL ===================== */
(function initNavbar() {
  const navbar = document.getElementById('navbar');
  window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 40);
  }, { passive: true });
})();

/* ===================== BACK TO TOP ===================== */
(function initBackToTop() {
  const btn = document.getElementById('back-to-top');
  window.addEventListener('scroll', () => btn.classList.toggle('visible', window.scrollY > 300), { passive: true });
  btn.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
})();

/* ===================== REVEAL ON SCROLL ===================== */
(function initReveal() {
  const obs = new IntersectionObserver((entries) => {
    entries.forEach(e => { if (e.isIntersecting) { e.target.classList.add('visible'); obs.unobserve(e.target); } });
  }, { threshold: 0.1 });
  document.querySelectorAll('.reveal').forEach(el => obs.observe(el));
})();

/* ===================== PARALLAX ORBS ===================== */
(function initParallax() {
  const orbs = document.querySelectorAll('.orb');
  window.addEventListener('mousemove', e => {
    const dx = (e.clientX - window.innerWidth  / 2) / window.innerWidth;
    const dy = (e.clientY - window.innerHeight / 2) / window.innerHeight;
    orbs.forEach((orb, i) => {
      const f = (i + 1) * 14;
      orb.style.transform = `translate(${dx * f}px, ${dy * f}px)`;
    });
  }, { passive: true });
})();

/* ===================== TABS ===================== */
document.querySelectorAll('.nav-tab').forEach(tab => {
  tab.addEventListener('click', () => {
    const target = tab.dataset.tab;
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-section').forEach(s => s.classList.remove('active'));
    tab.classList.add('active');
    document.getElementById('tab-' + target).classList.add('active');
  });
});

/* ===================== API HELPERS ===================== */

async function apiGet(path) {
  const res = await fetch(API_BASE + path);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

async function apiPost(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Erro desconhecido' }));
    throw new Error(err.message || 'Erro na requisição');
  }
  return res.json();
}

async function apiPut(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Erro desconhecido' }));
    throw new Error(err.message || 'Erro na requisição');
  }
  return res.json();
}

async function apiDelete(path) {
  const res = await fetch(API_BASE + path, { method: 'DELETE' });
  if (!res.ok) throw new Error('Erro ao deletar');
}

/* ===================== VERIFICAR API ===================== */
async function checkApiStatus() {
  const dot   = document.getElementById('api-dot');
  const label = document.getElementById('api-label');
  try {
    await fetch(API_BASE + '/epis', { signal: AbortSignal.timeout(3000) });
    dot.className   = 'api-dot online';
    label.textContent = 'API Online';
  } catch {
    dot.className   = 'api-dot offline';
    label.textContent = 'API Offline';
  }
}

/* ===================== CARREGAR DADOS ===================== */
async function loadAll() {
  await checkApiStatus();
  await Promise.all([loadEpis(), loadFuncionarios(), loadEntregas()]);
  updateHeaderStats();
}

async function loadEpis() {
  try {
    state.epis = await apiGet('/epis');
    renderEpis(state.epis);
  } catch {
    renderError('tbody-epi', 5);
  }
}

async function loadFuncionarios() {
  try {
    state.funcionarios = await apiGet('/funcionarios');
    renderFuncionarios(state.funcionarios);
  } catch {
    renderError('tbody-funcionario', 5);
  }
}

async function loadEntregas() {
  try {
    state.entregas = await apiGet('/entregas');
    renderEntregas(state.entregas);
  } catch {
    renderError('tbody-entrega', 5);
  }
}

function updateHeaderStats() {
  animateNumber('stat-epis',     state.epis.length);
  animateNumber('stat-funcs',    state.funcionarios.length);
  animateNumber('stat-entregas', state.entregas.length);
}

function animateNumber(id, target) {
  const el = document.getElementById(id);
  if (!el) return;
  let current = 0;
  const step = Math.max(1, Math.ceil(target / 30));
  const timer = setInterval(() => {
    current = Math.min(current + step, target);
    el.textContent = current;
    if (current >= target) clearInterval(timer);
  }, 30);
}

/* ===================== RENDER: EPIs ===================== */
function renderEpis(list) {
  const tbody = document.getElementById('tbody-epi');
  if (!list.length) { tbody.innerHTML = emptyState('Nenhum EPI cadastrado', 'Clique em "Novo EPI" para adicionar.'); return; }
  tbody.innerHTML = list.map(e => `
    <tr>
      <td>#${e.id}</td>
      <td class="name-cell">${esc(e.nome)}</td>
      <td>${esc(e.descricao || '—')}</td>
      <td>${stockBadge(e.quantidadeEstoque)}</td>
      <td>
        <div class="actions-cell">
          <button class="btn-icon edit" title="Editar" onclick="openModal('epi', ${e.id})">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="btn-icon delete" title="Excluir" onclick="confirmDelete('epi', ${e.id})">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 011-1h4a1 1 0 011 1v2"/></svg>
          </button>
        </div>
      </td>
    </tr>
  `).join('');
}

/* ===================== RENDER: FUNCIONÁRIOS ===================== */
function renderFuncionarios(list) {
  const tbody = document.getElementById('tbody-funcionario');
  if (!list.length) { tbody.innerHTML = emptyState('Nenhum funcionário cadastrado', 'Clique em "Novo Funcionário" para adicionar.'); return; }
  tbody.innerHTML = list.map(f => `
    <tr>
      <td>#${f.id}</td>
      <td class="name-cell">${esc(f.nome)}</td>
      <td><span style="font-family:var(--font-mono);font-size:.8rem">${esc(f.cpf)}</span></td>
      <td><span class="funcao-tag">${esc(f.funcao)}</span></td>
      <td>
        <div class="actions-cell">
          <button class="btn-icon edit" title="Editar" onclick="openModal('funcionario', ${f.id})">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="btn-icon delete" title="Excluir" onclick="confirmDelete('funcionario', ${f.id})">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 011-1h4a1 1 0 011 1v2"/></svg>
          </button>
        </div>
      </td>
    </tr>
  `).join('');
}

/* ===================== RENDER: ENTREGAS ===================== */
function renderEntregas(list) {
  const tbody = document.getElementById('tbody-entrega');
  if (!list.length) { tbody.innerHTML = emptyState('Nenhuma entrega registrada', 'Clique em "Nova Entrega" para registrar.'); return; }
  tbody.innerHTML = list.map(e => `
    <tr>
      <td>#${e.id}</td>
      <td class="name-cell">${esc(e.funcionario?.nome || '—')}</td>
      <td>${esc(e.epi?.nome || '—')}</td>
      <td><span class="stock-badge stock-ok">${e.quantidade} un.</span></td>
      <td><span class="date-chip">${formatDate(e.dataEntrega)}</span></td>
    </tr>
  `).join('');
}

/* ===================== FILTER ===================== */
function filterTable(type, term) {
  const t = term.toLowerCase();
  let list;
  if (type === 'epi')         list = state.epis.filter(e => e.nome.toLowerCase().includes(t) || (e.descricao || '').toLowerCase().includes(t));
  if (type === 'funcionario') list = state.funcionarios.filter(f => f.nome.toLowerCase().includes(t) || f.cpf.includes(t) || f.funcao.toLowerCase().includes(t));
  if (type === 'entrega')     list = state.entregas.filter(e => (e.funcionario?.nome || '').toLowerCase().includes(t) || (e.epi?.nome || '').toLowerCase().includes(t));
  if (type === 'epi')         renderEpis(list);
  if (type === 'funcionario') renderFuncionarios(list);
  if (type === 'entrega')     renderEntregas(list);
}

/* ===================== MODAL ===================== */
function openModal(type, id = null) {
  state.currentModal = type;
  state.editingId    = id;

  const overlay = document.getElementById('modal-overlay');
  const title   = document.getElementById('modal-title');
  const body    = document.getElementById('modal-body');

  if (type === 'epi') {
    const epi = id ? state.epis.find(e => e.id === id) : null;
    title.textContent = id ? 'Editar EPI' : 'Novo EPI';
    body.innerHTML = `
      <div class="form-group">
        <label>Nome *</label>
        <input id="f-nome" type="text" placeholder="Ex: Capacete de Segurança" value="${esc(epi?.nome || '')}" />
      </div>
      <div class="form-group">
        <label>Descrição</label>
        <input id="f-descricao" type="text" placeholder="Ex: Capacete ABS classe B" value="${esc(epi?.descricao || '')}" />
      </div>
      <div class="form-group">
        <label>Quantidade em Estoque *</label>
        <input id="f-estoque" type="number" min="0" placeholder="0" value="${epi?.quantidadeEstoque ?? ''}" />
        <p class="form-hint">// Não pode ser negativo</p>
      </div>
    `;
  }

  if (type === 'funcionario') {
    const f = id ? state.funcionarios.find(f => f.id === id) : null;
    title.textContent = id ? 'Editar Funcionário' : 'Novo Funcionário';
    body.innerHTML = `
      <div class="form-group">
        <label>Nome *</label>
        <input id="f-nome" type="text" placeholder="Nome completo" value="${esc(f?.nome || '')}" />
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>CPF *</label>
          <input id="f-cpf" type="text" placeholder="000.000.000-00" value="${esc(f?.cpf || '')}" maxlength="14" oninput="maskCpf(this)" />
        </div>
        <div class="form-group">
          <label>Função *</label>
          <input id="f-funcao" type="text" placeholder="Ex: Pedreiro" value="${esc(f?.funcao || '')}" />
        </div>
      </div>
    `;
  }

  if (type === 'entrega') {
    title.textContent = 'Registrar Entrega';
    const funcsOptions = state.funcionarios.map(f => `<option value="${f.id}">${esc(f.nome)} — ${esc(f.funcao)}</option>`).join('');
    const episOptions  = state.epis.map(e => `<option value="${e.id}">${esc(e.nome)} (estoque: ${e.quantidadeEstoque})</option>`).join('');
    body.innerHTML = `
      <div class="form-group">
        <label>Funcionário *</label>
        <select id="f-funcionario"><option value="">Selecionar...</option>${funcsOptions}</select>
      </div>
      <div class="form-group">
        <label>EPI *</label>
        <select id="f-epi"><option value="">Selecionar...</option>${episOptions}</select>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>Quantidade *</label>
          <input id="f-quantidade" type="number" min="1" placeholder="1" />
          <p class="form-hint">// Mín. 1 unidade</p>
        </div>
        <div class="form-group">
          <label>Data da Entrega</label>
          <input id="f-data" type="date" value="${new Date().toISOString().split('T')[0]}" />
          <p class="form-hint">// Opcional: padrão = hoje</p>
        </div>
      </div>
    `;
  }

  overlay.classList.add('open');
  setTimeout(() => document.querySelector('.modal-body input, .modal-body select')?.focus(), 350);
}

function closeModal() {
  document.getElementById('modal-overlay').classList.remove('open');
  state.currentModal = null;
  state.editingId    = null;
}

async function saveModal() {
  const btn = document.getElementById('modal-save-btn');
  btn.disabled = true;
  btn.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="animation:spin .7s linear infinite"><path d="M12 3a9 9 0 019 9"/></svg> Salvando...`;

  try {
    if (state.currentModal === 'epi') await saveEpi();
    if (state.currentModal === 'funcionario') await saveFuncionario();
    if (state.currentModal === 'entrega') await saveEntrega();
  } finally {
    btn.disabled = false;
    btn.innerHTML = `Salvar <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`;
  }
}

async function saveEpi() {
  const nome     = document.getElementById('f-nome')?.value.trim();
  const descricao = document.getElementById('f-descricao')?.value.trim();
  const estoque  = document.getElementById('f-estoque')?.value;

  if (!nome || estoque === '') { toast('Preencha todos os campos obrigatórios.', 'error'); return; }

  const body = { nome, descricao, quantidadeEstoque: parseInt(estoque) };
  try {
    if (state.editingId) {
      await apiPut('/epis/' + state.editingId, body);
      toast('EPI atualizado com sucesso!', 'success');
    } else {
      await apiPost('/epis', body);
      toast('EPI criado com sucesso!', 'success');
    }
    closeModal();
    await loadEpis();
    updateHeaderStats();
  } catch (e) { toast(e.message, 'error'); }
}

async function saveFuncionario() {
  const nome   = document.getElementById('f-nome')?.value.trim();
  const cpf    = document.getElementById('f-cpf')?.value.trim();
  const funcao = document.getElementById('f-funcao')?.value.trim();

  if (!nome || !cpf || !funcao) { toast('Preencha todos os campos obrigatórios.', 'error'); return; }
  if (!/^\d{3}\.\d{3}\.\d{3}-\d{2}$/.test(cpf)) { toast('CPF inválido. Use o formato 000.000.000-00', 'error'); return; }

  const body = { nome, cpf, funcao };
  try {
    if (state.editingId) {
      await apiPut('/funcionarios/' + state.editingId, body);
      toast('Funcionário atualizado!', 'success');
    } else {
      await apiPost('/funcionarios', body);
      toast('Funcionário cadastrado!', 'success');
    }
    closeModal();
    await loadFuncionarios();
    updateHeaderStats();
  } catch (e) { toast(e.message, 'error'); }
}

async function saveEntrega() {
  const funcionarioId = document.getElementById('f-funcionario')?.value;
  const epiId         = document.getElementById('f-epi')?.value;
  const quantidade    = document.getElementById('f-quantidade')?.value;
  const dataEntrega   = document.getElementById('f-data')?.value;

  if (!funcionarioId || !epiId || !quantidade) { toast('Selecione funcionário, EPI e quantidade.', 'error'); return; }

  const body = {
    funcionarioId: parseInt(funcionarioId),
    epiId:         parseInt(epiId),
    quantidade:    parseInt(quantidade),
    dataEntrega:   dataEntrega || null,
  };
  try {
    await apiPost('/entregas', body);
    toast('Entrega registrada! Estoque atualizado.', 'success');
    closeModal();
    await Promise.all([loadEpis(), loadEntregas()]);
    updateHeaderStats();
  } catch (e) { toast(e.message, 'error'); }
}

/* ===================== DELETE ===================== */
function confirmDelete(type, id) {
  const overlay = document.getElementById('confirm-overlay');
  const btn     = document.getElementById('confirm-delete-btn');
  overlay.classList.add('open');
  btn.onclick = async () => {
    try {
      if (type === 'epi')         await apiDelete('/epis/' + id);
      if (type === 'funcionario') await apiDelete('/funcionarios/' + id);
      toast('Registro excluído.', 'info');
      closeConfirm();
      if (type === 'epi')         await loadEpis();
      if (type === 'funcionario') await loadFuncionarios();
      updateHeaderStats();
    } catch (e) { toast(e.message, 'error'); closeConfirm(); }
  };
}
function closeConfirm() { document.getElementById('confirm-overlay').classList.remove('open'); }

/* ===================== TOAST ===================== */
function toast(msg, type = 'info') {
  const container = document.getElementById('toast-container');
  const icons = {
    success: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`,
    error:   `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`,
    info:    `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`,
  };
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = (icons[type] || '') + msg;
  container.appendChild(el);
  setTimeout(() => {
    el.classList.add('out');
    el.addEventListener('animationend', () => el.remove());
  }, 3200);
}

/* ===================== HELPERS ===================== */
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function stockBadge(n) {
  if (n === 0)  return `<span class="stock-badge stock-zero">0 un.</span>`;
  if (n <= 5)   return `<span class="stock-badge stock-low">${n} un.</span>`;
  return              `<span class="stock-badge stock-ok">${n} un.</span>`;
}

function formatDate(d) {
  if (!d) return '—';
  const parts = d.split ? d.split('-') : [];
  if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
  return d;
}

function maskCpf(input) {
  let v = input.value.replace(/\D/g, '').slice(0, 11);
  v = v.replace(/^(\d{3})(\d)/, '$1.$2');
  v = v.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
  v = v.replace(/\.(\d{3})(\d)/, '.$1-$2');
  input.value = v;
}

function renderError(tbodyId, cols) {
  document.getElementById(tbodyId).innerHTML = `
    <tr><td colspan="${cols}">
      <div class="empty-state">
        <div class="empty-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        </div>
        <p class="empty-title">Erro ao carregar dados</p>
        <p class="empty-desc">// Verifique se a API está rodando em localhost:8080</p>
      </div>
    </td></tr>
  `;
}

function emptyState(title, desc) {
  return `<tr><td colspan="5">
    <div class="empty-state">
      <div class="empty-icon">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><line x1="9" y1="9" x2="15" y2="15"/><line x1="15" y1="9" x2="9" y2="15"/></svg>
      </div>
      <p class="empty-title">${title}</p>
      <p class="empty-desc">// ${desc}</p>
    </div>
  </td></tr>`;
}

/* ===================== CSS SPIN ANIMATION ===================== */
const styleEl = document.createElement('style');
styleEl.textContent = `@keyframes spin { to { transform: rotate(360deg); } }`;
document.head.appendChild(styleEl);

/* ===================== INIT ===================== */
document.addEventListener('DOMContentLoaded', loadAll);
