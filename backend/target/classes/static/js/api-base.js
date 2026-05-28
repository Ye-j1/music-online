/**
 * Resolves the Spring Boot API base URL.
 * Set window.MO_API_BASE (e.g. "http://localhost:8080") before this script
 * if the UI is not served from the same origin as the API.
 */
(function (global) {
  var DEV_API_PORTS = ['5500', '5173', '3000', '4200', '8081', '4173', '5501'];

  function trimBase(s) {
    return String(s).replace(/\/+$/, '');
  }

  global.moApiBaseUrl = function moApiBaseUrl() {
    if (typeof global.MO_API_BASE === 'string' && global.MO_API_BASE.trim()) {
      return trimBase(global.MO_API_BASE);
    }
    var loc = global.location;
    if (!loc || !loc.origin || loc.origin === 'null') {
      return 'http://localhost:8080';
    }
    if (loc.protocol === 'file:') {
      return 'http://localhost:8080';
    }
    var host = loc.hostname;
    var port = loc.port || '';
    var isLoopback = (host === 'localhost' || host === '127.0.0.1' || host === '::1' || host === '[::1]');
    if (isLoopback) {
      if (DEV_API_PORTS.indexOf(port) !== -1) return 'http://localhost:8080';
      if (port && port !== '8080' && port !== '80' && port !== '443') return 'http://localhost:8080';
    }
    return loc.origin;
  };

  /** Authenticated fetch helper */
  global.moApiFetch = function moApiFetch(path, options) {
    var opts = options || {};
    var headers = Object.assign({}, opts.headers || {});
    var token = localStorage.getItem('mo_token');
    if (token) headers['Authorization'] = 'Bearer ' + token;
    if (!headers['Content-Type'] && opts.body) headers['Content-Type'] = 'application/json';
    return fetch(moApiBaseUrl() + path, Object.assign({}, opts, { headers: headers }));
  };

})(typeof window !== 'undefined' ? window : globalThis);
