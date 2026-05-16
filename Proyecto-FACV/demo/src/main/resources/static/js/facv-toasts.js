function facvShowToast(message, type) {
  type = type || 'success';
  var configs = {
    success: { icon: '✓', title: 'Acción completada', bar: '#16a34a', bg: '#dcfce7', text: '#14532d', border: '#86efac' },
    info:    { icon: 'ℹ', title: 'Información',        bar: '#2563eb', bg: '#dbeafe', text: '#1e3a8a', border: '#93c5fd' },
    warning: { icon: '⚠', title: 'Advertencia',        bar: '#d97706', bg: '#fef9c3', text: '#78350f', border: '#fde68a' },
    error:   { icon: '✕', title: 'Error',              bar: '#dc2626', bg: '#fee2e2', text: '#7f1d1d', border: '#fca5a5' }
  };

  var c = configs[type] || configs.success;
  var id = 'facv-toast-' + Date.now();
  var duration = 4500;

  var el = document.createElement('div');
  el.id = id;
  el.setAttribute('role', 'alert');
  el.style.cssText = [
    'background:' + c.bg,
    'border:1.5px solid ' + c.border,
    'border-radius:12px',
    'min-width:300px',
    'max-width:380px',
    'box-shadow:0 8px 24px rgba(0,0,0,0.12)',
    'overflow:hidden',
    'opacity:0',
    'transform:translateX(20px)',
    'transition:opacity 0.22s ease,transform 0.22s ease'
  ].join(';');

  el.innerHTML =
    '<div style="display:flex;align-items:center;padding:0.7rem 1rem 0.2rem;gap:0.5rem;">' +
      '<span style="font-size:1rem;">' + c.icon + '</span>' +
      '<strong style="color:' + c.text + ';flex:1;font-size:0.88rem;">' + c.title + '</strong>' +
      '<button onclick="facvDismissToast(\'' + id + '\')" style="background:none;border:none;cursor:pointer;color:' + c.text + ';font-size:1rem;line-height:1;padding:0;opacity:0.6;">×</button>' +
    '</div>' +
    '<div style="color:' + c.text + ';padding:0.15rem 1rem 0.7rem;font-size:0.88rem;line-height:1.5;">' + message + '</div>' +
    '<div style="height:3px;background:rgba(0,0,0,0.08);overflow:hidden;">' +
      '<div id="' + id + '-bar" style="height:100%;width:100%;background:' + c.bar + ';transition:width ' + duration + 'ms linear;"></div>' +
    '</div>';

  var container = document.getElementById('facv-toast-container');
  if (!container) return;
  container.appendChild(el);

  requestAnimationFrame(function() {
    requestAnimationFrame(function() {
      el.style.opacity = '1';
      el.style.transform = 'translateX(0)';
      var bar = document.getElementById(id + '-bar');
      if (bar) bar.style.width = '0%';
    });
  });

  setTimeout(function() { facvDismissToast(id); }, duration);
}

function facvDismissToast(id) {
  var el = document.getElementById(id);
  if (!el) return;
  el.style.opacity = '0';
  el.style.transform = 'translateX(20px)';
  setTimeout(function() { if (el.parentNode) el.parentNode.removeChild(el); }, 250);
}

document.addEventListener('DOMContentLoaded', function() {
  var map = [
    { id: 'flash-success', type: 'success' },
    { id: 'flash-error',   type: 'error'   },
    { id: 'flash-warning', type: 'warning' },
    { id: 'flash-info',    type: 'info'    }
  ];
  map.forEach(function(item) {
    var el = document.getElementById(item.id);
    if (el && el.dataset.msg) facvShowToast(el.dataset.msg, item.type);
  });
});
