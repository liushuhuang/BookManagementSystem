const routes = [
  { id: 'dashboard', label: '工作台' },
  { id: 'categories', label: '图书分类' },
  { id: 'books', label: '图书资料' },
  { id: 'copies', label: '馆藏副本' },
  { id: 'borrow', label: '借阅办理' },
  { id: 'returns', label: '归还办理' }
];

const statusText = {
  category: { 0: '禁用', 1: '正常' },
  book: { 0: '下架', 1: '上架' },
  copy: { 0: '下架', 1: '可借', 2: '借出', 3: '损坏', 4: '遗失' },
  borrow: { 0: '借阅中', 1: '已归还', 2: '逾期', 3: '遗失' }
};

const state = {
  route: 'dashboard',
  token: localStorage.getItem('bms_token'),
  user: JSON.parse(localStorage.getItem('bms_user') || 'null')
};

const app = document.getElementById('app');

const api = {
  async request(path, options = {}) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (state.token) headers.Authorization = `Bearer ${state.token}`;
    const response = await fetch(path, Object.assign({}, options, { headers }));
    const body = await response.json().catch(() => ({ code: response.status, message: '请求失败' }));
    if (body.code !== 200) throw new Error(body.message || '请求失败');
    return body.data;
  },
  get(path) {
    return api.request(path);
  },
  post(path, body) {
    return api.request(path, { method: 'POST', body: JSON.stringify(body) });
  },
  put(path, body) {
    return api.request(path, { method: 'PUT', body: JSON.stringify(body) });
  }
};

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[char]));
}

function showMessage(text, type = 'success') {
  const box = document.getElementById('message');
  if (!box) return;
  box.textContent = text;
  box.className = `message show ${type}`;
}

function tag(text, kind = 'info') {
  return `<span class="tag ${kind}">${escapeHtml(text)}</span>`;
}

function statusTag(map, value) {
  const kind = value === 1 ? 'ok' : value === 2 ? 'warn' : value === 0 ? 'bad' : 'info';
  return tag(map[value] || value, kind);
}

function formValue(form, name) {
  const value = new FormData(form).get(name);
  return value === '' || value === null ? null : value;
}

function num(value) {
  return value === null || value === undefined || value === '' ? null : Number(value);
}

function render() {
  if (!state.token) {
    renderLogin();
    return;
  }
  app.innerHTML = `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="brand"><span class="brand-mark">书</span><span>明月图书管理</span></div>
        <nav class="nav">
          ${routes.map(route => `<button class="${route.id === state.route ? 'active' : ''}" data-route="${route.id}">${route.label}</button>`).join('')}
        </nav>
      </aside>
      <section class="main">
        <header class="topbar">
          <strong>${routes.find(route => route.id === state.route).label}</strong>
          <div class="actions">
            <span class="user-line">${escapeHtml(state.user?.username || '')}</span>
            <button class="btn" id="logoutBtn">退出</button>
          </div>
        </header>
        <main class="content">
          <div id="message" class="message"></div>
          <div id="view"></div>
        </main>
      </section>
    </div>
  `;
  document.querySelectorAll('[data-route]').forEach(btn => {
    btn.addEventListener('click', () => {
      state.route = btn.dataset.route;
      render();
    });
  });
  document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('bms_token');
    localStorage.removeItem('bms_user');
    state.token = null;
    state.user = null;
    render();
  });
  renderRoute().catch(error => showMessage(error.message, 'error'));
}

function renderLogin() {
  app.innerHTML = `
    <div class="login-page">
      <form class="login-panel" id="loginForm">
        <div>
          <h1>明月图书借阅管理系统</h1>
          <p>登录后进入业务工作台</p>
        </div>
        <div class="field">
          <label>用户名</label>
          <input name="username" value="librarian_test" autocomplete="username" required>
        </div>
        <div class="field">
          <label>密码</label>
          <input name="password" type="password" value="123456" autocomplete="current-password" required>
        </div>
        <button class="btn primary" type="submit">登录</button>
        <div id="message" class="message"></div>
      </form>
      <section class="login-visual">
        <h2>馆藏、借阅、归还统一处理</h2>
        <p>围绕图书流转状态建立可演示的业务闭环。</p>
      </section>
    </div>
  `;
  document.getElementById('loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    try {
      const data = Object.fromEntries(new FormData(event.currentTarget).entries());
      const result = await api.post('/api/v1/auth/login', data);
      state.token = result.accessToken;
      state.user = result.userInfo;
      localStorage.setItem('bms_token', state.token);
      localStorage.setItem('bms_user', JSON.stringify(state.user));
      state.route = 'dashboard';
      render();
    } catch (error) {
      showMessage(error.message, 'error');
    }
  });
}

async function renderRoute() {
  if (state.route === 'dashboard') return renderDashboard();
  if (state.route === 'categories') return renderCategories();
  if (state.route === 'books') return renderBooks();
  if (state.route === 'copies') return renderCopies();
  if (state.route === 'borrow') return renderBorrow();
  if (state.route === 'returns') return renderReturns();
}

async function renderDashboard() {
  const data = await api.get('/api/v1/statistics/dashboard');
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>工作台</h2><button class="btn" id="refresh">刷新</button></div>
    <section class="grid">
      ${metric('图书资料', data.bookCount)}
      ${metric('馆藏总册', data.copyCount)}
      ${metric('可借册数', data.availableCopyCount)}
      ${metric('借出册数', data.borrowedCopyCount)}
      ${metric('借阅中', data.borrowingRecordCount)}
      ${metric('逾期记录', data.overdueRecordCount)}
      ${metric('未缴罚款', data.unpaidFineAmount || '0.00')}
    </section>
  `;
  document.getElementById('refresh').addEventListener('click', renderDashboard);
}

function metric(label, value) {
  return `<div class="metric"><b>${escapeHtml(value)}</b><span>${escapeHtml(label)}</span></div>`;
}

async function renderCategories() {
  const rows = await api.get('/api/v1/book-categories');
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>图书分类</h2></div>
    <section class="panel">
      <form id="categoryForm" class="form-grid">
        ${field('分类名称', 'categoryName')}
        ${field('分类编码', 'categoryCode')}
        ${field('排序', 'sortOrder', 'number', '0')}
        <button class="btn primary" type="submit">新增分类</button>
      </form>
    </section>
    ${table(['ID','名称','编码','状态','操作'], rows.map(row => [
      row.id, row.categoryName, row.categoryCode, statusTag(statusText.category, row.status),
      `<button class="btn" data-category-status="${row.id}" data-next="${row.status === 1 ? 0 : 1}">${row.status === 1 ? '禁用' : '启用'}</button>`
    ]))}
  `;
  document.getElementById('categoryForm').addEventListener('submit', submitCategory);
  document.querySelectorAll('[data-category-status]').forEach(btn => btn.addEventListener('click', async () => {
    await api.put(`/api/v1/book-categories/${btn.dataset.categoryStatus}/status`, { status: Number(btn.dataset.next) });
    showMessage('分类状态已更新');
    renderCategories();
  }));
}

async function submitCategory(event) {
  event.preventDefault();
  const form = event.currentTarget;
  await api.post('/api/v1/book-categories', {
    categoryName: formValue(form, 'categoryName'),
    categoryCode: formValue(form, 'categoryCode'),
    sortOrder: num(formValue(form, 'sortOrder')) || 0
  });
  showMessage('分类已新增');
  renderCategories();
}

async function renderBooks() {
  const [books, categories] = await Promise.all([api.get('/api/v1/books'), api.get('/api/v1/book-categories')]);
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>图书资料</h2></div>
    <section class="panel">
      <form id="bookForm" class="form-grid">
        ${field('ISBN', 'isbn')}
        ${field('书名', 'bookName')}
        ${field('作者', 'author')}
        ${field('出版社', 'publisher')}
        ${selectField('分类', 'categoryId', categories.map(c => [c.id, c.categoryName]))}
        ${field('价格', 'price', 'number')}
        ${field('页数', 'pages', 'number')}
        <button class="btn primary" type="submit">新增图书</button>
      </form>
    </section>
    ${table(['ID','ISBN','书名','作者','分类','可借','借出','状态','操作'], books.map(row => [
      row.id, row.isbn, row.bookName, row.author, row.categoryName || '-', row.availableCount, row.borrowedCount,
      statusTag(statusText.book, row.status),
      `<button class="btn" data-book-status="${row.id}" data-next="${row.status === 1 ? 0 : 1}">${row.status === 1 ? '下架' : '上架'}</button>`
    ]))}
  `;
  document.getElementById('bookForm').addEventListener('submit', submitBook);
  document.querySelectorAll('[data-book-status]').forEach(btn => btn.addEventListener('click', async () => {
    await api.put(`/api/v1/books/${btn.dataset.bookStatus}/status`, { status: Number(btn.dataset.next) });
    showMessage('图书状态已更新');
    renderBooks();
  }));
}

async function submitBook(event) {
  event.preventDefault();
  const form = event.currentTarget;
  await api.post('/api/v1/books', {
    isbn: formValue(form, 'isbn'),
    bookName: formValue(form, 'bookName'),
    author: formValue(form, 'author'),
    publisher: formValue(form, 'publisher'),
    categoryId: num(formValue(form, 'categoryId')),
    price: num(formValue(form, 'price')),
    pages: num(formValue(form, 'pages'))
  });
  showMessage('图书已新增');
  renderBooks();
}

async function renderCopies() {
  const [copies, books] = await Promise.all([api.get('/api/v1/book-copies'), api.get('/api/v1/books')]);
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>馆藏副本</h2></div>
    <section class="panel">
      <form id="copyForm" class="form-grid">
        ${selectField('图书', 'bookId', books.map(b => [b.id, b.bookName]))}
        ${field('馆藏编号', 'copyCode')}
        ${field('存放位置', 'location')}
        <button class="btn primary" type="submit">新增副本</button>
      </form>
    </section>
    ${table(['ID','图书','馆藏编号','位置','状态'], copies.map(row => [
      row.id, row.bookName || row.bookId, row.copyCode, row.location || '-', statusTag(statusText.copy, row.status)
    ]))}
  `;
  document.getElementById('copyForm').addEventListener('submit', submitCopy);
}

async function submitCopy(event) {
  event.preventDefault();
  const form = event.currentTarget;
  await api.post('/api/v1/book-copies', {
    bookId: num(formValue(form, 'bookId')),
    copyCode: formValue(form, 'copyCode'),
    location: formValue(form, 'location')
  });
  showMessage('副本已新增');
  renderCopies();
}

async function renderBorrow() {
  const books = await api.get('/api/v1/books?onlyAvailable=true');
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>借阅办理</h2></div>
    <section class="panel">
      <form id="borrowForm" class="form-grid">
        ${field('读者ID', 'userId', 'number', '3')}
        ${selectField('图书', 'bookId', books.map(b => [b.id, `${b.bookName}（可借${b.availableCount}）`]))}
        ${field('指定副本ID', 'copyId', 'number')}
        <button class="btn success" type="submit">办理借阅</button>
      </form>
    </section>
    <section id="borrowRecords"></section>
  `;
  document.getElementById('borrowForm').addEventListener('submit', submitBorrow);
  await renderBorrowRecords();
}

async function submitBorrow(event) {
  event.preventDefault();
  const form = event.currentTarget;
  await api.post('/api/v1/borrow-records', {
    userId: num(formValue(form, 'userId')),
    bookId: num(formValue(form, 'bookId')),
    copyId: num(formValue(form, 'copyId'))
  });
  showMessage('借阅已办理');
  renderBorrow();
}

async function renderBorrowRecords() {
  const records = await api.get('/api/v1/borrow-records');
  document.getElementById('borrowRecords').innerHTML = table(['单号','读者','图书','副本','状态','借阅时间'], records.map(row => [
    row.borrowNo, row.realName || row.username, row.bookName, row.copyCode, statusTag(statusText.borrow, row.status), row.borrowTime || '-'
  ]));
}

async function renderReturns() {
  const rows = await api.get('/api/v1/returns/pending');
  document.getElementById('view').innerHTML = `
    <div class="view-head"><h2>归还办理</h2></div>
    ${table(['单号','读者','图书','副本','状态','操作'], rows.map(row => [
      row.borrowNo, row.realName || row.username, row.bookName, row.copyCode, statusTag(statusText.borrow, row.status),
      `<button class="btn success" data-return="${row.id}">归还</button>`
    ]))}
  `;
  document.querySelectorAll('[data-return]').forEach(btn => btn.addEventListener('click', async () => {
    await api.post('/api/v1/returns', { borrowId: Number(btn.dataset.return), copyStatus: 1 });
    showMessage('归还已完成');
    renderReturns();
  }));
}

function field(label, name, type = 'text', value = '') {
  return `
    <div class="field">
      <label>${label}</label>
      <input name="${name}" type="${type}" value="${escapeHtml(value)}">
    </div>
  `;
}

function selectField(label, name, options) {
  return `
    <div class="field">
      <label>${label}</label>
      <select name="${name}">
        ${options.map(([value, text]) => `<option value="${escapeHtml(value)}">${escapeHtml(text)}</option>`).join('')}
      </select>
    </div>
  `;
}

function table(headers, rows) {
  return `
    <div class="table-wrap">
      <table>
        <thead><tr>${headers.map(h => `<th>${escapeHtml(h)}</th>`).join('')}</tr></thead>
        <tbody>
          ${rows.length ? rows.map(row => `<tr>${row.map(cell => `<td>${renderCell(cell)}</td>`).join('')}</tr>`).join('') : `<tr><td colspan="${headers.length}">暂无数据</td></tr>`}
        </tbody>
      </table>
    </div>
  `;
}

function renderCell(cell) {
  const value = String(cell ?? '');
  return value.trim().startsWith('<') ? value : escapeHtml(value);
}

render();
