/* ── Auth utilities ── */

function moGetUser() {
  try { return JSON.parse(localStorage.getItem('mo_user') || 'null'); } catch (e) { return null; }
}
function moGetToken() { return localStorage.getItem('mo_token') || null; }
function moIsLoggedIn() { return !!(moGetToken() && moGetUser()); }
function moIsAdmin() { var u = moGetUser(); return u && u.role === 'ADMIN'; }

function moLogout() {
  localStorage.removeItem('mo_token');
  localStorage.removeItem('mo_user');
  window.location.href = resolveRootPath() + 'pages/home.html';
}

function resolveRootPath() {
  var p = window.location.pathname;
  if (p.indexOf('/pages/') !== -1) return '../';
  return '';
}

/* ── Toggle password visibility ── */
function togglePassword(inputId, btn) {
  var input = document.getElementById(inputId);
  var icon  = btn.querySelector('i');
  if (input.type === 'password') {
    input.type = 'text';
    icon.classList.replace('fa-eye', 'fa-eye-slash');
  } else {
    input.type = 'password';
    icon.classList.replace('fa-eye-slash', 'fa-eye');
  }
}

/* ── Show / hide forms ── */
function showRegister() {
  document.getElementById('login-form-container').style.display  = 'none';
  document.getElementById('register-form-container').style.display = 'block';
  if (window.location.hash !== '#register') history.replaceState(null, '', 'login.html#register');
}
function showLogin() {
  document.getElementById('register-form-container').style.display = 'none';
  document.getElementById('login-form-container').style.display  = 'block';
  if (window.location.hash === '#register') history.replaceState(null, '', 'login.html');
}

/* ── Role selection ── */
function selectRole(el) {
  document.querySelectorAll('.role-option').forEach(function (r) { r.classList.remove('selected'); });
  el.classList.add('selected');
  var role = el.getAttribute('data-role');
  var note = document.getElementById('retailer-note');
  if (note) note.classList.toggle('visible', role === 'retailer');
}

/* ── Get form value ── */
function getVal(id) {
  var el = document.getElementById(id);
  return el ? el.value.trim() : '';
}

/* ── Handle Login ── */
function handleLogin(e) {
  e.preventDefault();
  var email    = getVal('login-email');
  var pwEl     = document.getElementById('login-password');
  var password = pwEl ? pwEl.value : '';

  if (!email || !password) return false;

  var btn = e.target.querySelector('.auth-submit');
  var prevHtml = btn.innerHTML;
  btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing in...';
  btn.disabled = true;

  fetch(moApiBaseUrl() + '/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    body: JSON.stringify({ email: email, password: password })
  })
    .then(function (res) {
      return res.text().then(function (text) {
        var data = {};
        if (text) { try { data = JSON.parse(text); } catch (ignore) { if (!res.ok) throw new Error(text.slice(0, 120) || 'Login failed'); } }
        if (!res.ok) throw new Error(data.error || data.message || 'Login failed (' + res.status + ')');
        return data;
      });
    })
    .then(function (data) {
      if (!data.token || !data.user) throw new Error('Invalid server response');
      localStorage.setItem('mo_token', data.token);
      localStorage.setItem('mo_user', JSON.stringify({
        id:       data.user.id,
        email:    data.user.email,
        username: data.user.username,
        role:     data.user.role,
        loggedIn: true
      }));
      window.location.href = '../index.html';
    })
    .catch(function (err) {
      alert(err.message || 'Login failed. Please check your credentials and that the server is running.');
      btn.innerHTML = prevHtml;
      btn.disabled = false;
    });

  return false;
}

/* ── Handle Register ── */
function handleRegister(e) {
  e.preventDefault();
  var selected = document.querySelector('.role-option.selected');
  if (!selected) { alert('Please select an account type.'); return false; }

  var role     = selected.getAttribute('data-role');
  var email    = getVal('reg-email');
  var username = getVal('reg-username');
  var pwEl     = document.getElementById('reg-password');
  var password = pwEl ? pwEl.value : '';

  if (!email)    { alert('Please enter your email address.'); return false; }
  if (!username) { alert('Please choose a username.'); return false; }
  if (password.length < 8) { alert('Password must be at least 8 characters.'); if (pwEl) pwEl.focus(); return false; }

  var btn = e.target.querySelector('.auth-submit');
  var prevHtml = btn.innerHTML;
  btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating account...';
  btn.disabled = true;

  // Retailer must complete payment first
  if (role === 'retailer' && window.retailerPaid !== true) {
    btn.innerHTML = prevHtml; btn.disabled = false;
    alert('Please complete the $100 registration fee payment before creating a Retailer account.');
    return false;
  }

  fetch(moApiBaseUrl() + '/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    body: JSON.stringify({ email: email, username: username, password: password, role: role })
  })
    .then(function (res) {
      return res.text().then(function (text) {
        var data = {};
        if (text) { try { data = JSON.parse(text); } catch (ignore) { if (!res.ok) throw new Error(text.slice(0, 200) || 'Registration failed'); } }
        if (!res.ok) throw new Error(data.error || data.message || 'Registration failed (' + res.status + ')');
        return data;
      });
    })
    .then(function () {
      alert('Account created! You can now sign in.');
      showLogin();
      document.getElementById('register-form').reset();
      document.querySelectorAll('.role-option').forEach(function (r) { r.classList.remove('selected'); });
      // Reset payment state
      if (window.retailerPaid) {
        window.retailerPaid = false;
        var ps = document.getElementById('payment-section');
        if (ps) ps.classList.remove('visible');
        var pc = document.getElementById('payment-confirmed');
        if (pc) pc.classList.remove('visible');
      }
      var loginEmail = document.getElementById('login-email');
      if (loginEmail && email) loginEmail.value = email;
    })
    .catch(function (err) {
      alert(err.message || 'Registration failed. Please check the form and try again.');
    })
    .then(function () { btn.innerHTML = prevHtml; btn.disabled = false; });

  return false;
}

/* ── Bootstrap hash ── */
function applyAuthHash() {
  if (window.location.hash === '#register') showRegister();
}

document.addEventListener('DOMContentLoaded', function () {
  applyAuthHash();
  window.addEventListener('hashchange', applyAuthHash);
});
