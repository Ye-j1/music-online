/**
 * app.js — shared utilities for dashboard pages
 */
(function (global) {

  function showToast(message, type) {
    var container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }
    var toast = document.createElement('div');
    toast.className = 'toast' + (type ? ' toast-' + type : '');
    var icon = type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle';
    toast.innerHTML = '<i class="fas ' + icon + '"></i> ' + message;
    container.appendChild(toast);
    setTimeout(function () { toast.remove(); }, 3500);
  }
  global.showToast = showToast;

  function typeBadge(type) {
    var map = { ALBUM: 'badge-album', EP: 'badge-ep', SINGLE: 'badge-single' };
    return '<span class="badge ' + (map[type] || 'badge-neutral') + '">' + (type || '') + '</span>';
  }
  global.typeBadge = typeBadge;

  function fmtPrice(p) { return '£' + Number(p).toFixed(2); }
  global.fmtPrice = fmtPrice;

  function fmtDate(d) {
    if (!d) return '—';
    try { return new Date(d).toLocaleDateString('en-GB', { year: 'numeric', month: 'short', day: 'numeric' }); }
    catch (e) { return String(d); }
  }
  global.fmtDate = fmtDate;

  function escHtml(s) {
    if (!s) return '';
    return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }
  global.escHtml = escHtml;

  function buildVinylCard(v, onClick) {
    var card = document.createElement('div');
    card.className = 'vinyl-card';
    card.innerHTML =
      '<div class="vinyl-card-img">' +
      (v.imageUrl ? '<img src="' + escHtml(v.imageUrl) + '" alt="' + escHtml(v.title) + '" onerror="this.parentNode.innerHTML=\'<i class=\\\"fas fa-record-vinyl\\\"></i>\'">' : '<i class="fas fa-record-vinyl"></i>') +
      '</div><div class="vinyl-card-body">' +
      '<div class="vinyl-card-title truncate">' + escHtml(v.title) + '</div>' +
      '<div class="vinyl-card-artist truncate">' + escHtml(v.artist) + '</div>' +
      typeBadge(v.type) + '</div>' +
      '<div class="vinyl-card-footer"><span class="vinyl-price">' + fmtPrice(v.price) + '</span>' +
      '<span class="badge badge-neutral" style="font-size:10px">' + escHtml(v.condition || '') + '</span></div>';
    if (onClick) card.addEventListener('click', function () { onClick(v); });
    return card;
  }
  global.buildVinylCard = buildVinylCard;

  function requireLogin() {
    var token = localStorage.getItem('mo_token');
    var user  = localStorage.getItem('mo_user');
    if (!token || !user) { window.location.href = 'login.html'; return null; }
    try { return JSON.parse(user); } catch (e) { window.location.href = 'login.html'; return null; }
  }
  global.requireLogin = requireLogin;

  function requireAdmin() {
    var u = requireLogin();
    if (u && u.role !== 'ADMIN') { window.location.href = 'dashboard.html'; return null; }
    return u;
  }
  global.requireAdmin = requireAdmin;

  function renderSidebarUser(user) {
    var nameEl = document.getElementById('sidebar-username');
    var roleEl = document.getElementById('sidebar-role');
    if (nameEl && user) nameEl.textContent = user.username || user.email;
    if (roleEl && user) roleEl.textContent = user.role;
  }
  global.renderSidebarUser = renderSidebarUser;

  function setActiveNav(id) {
    document.querySelectorAll('.nav-item').forEach(function (el) { el.classList.remove('active'); });
    var el = document.getElementById(id);
    if (el) el.classList.add('active');
  }
  global.setActiveNav = setActiveNav;

})(typeof window !== 'undefined' ? window : globalThis);
