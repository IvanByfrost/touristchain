document.addEventListener('submit', async (ev) => {
  const form = ev.target;
  if (form.id !== 'loginForm') return;

  ev.preventDefault(); // evita la navegación completa, usamos fetch
  const btn = form.querySelector('button[type="submit"]');
  const old = btn.textContent;
  btn.disabled = true; btn.textContent = 'Procesando…';

  try {
    const res = await fetch(form.action, {
      method: 'POST',
      body: new FormData(form),
      headers: { 'Accept': 'application/json' }
    });
    const json = await res.json().catch(() => ({}));

    if (res.ok && json?.success) {
      // Ejemplo: redirige al inicio (ajusta según tu flujo)
      location.hash = '#/inicio';
    } else {
      alert(json?.message || 'Credenciales inválidas');
    }
  } catch (e) {
    console.error(e);
    alert('Error de red o servidor');
  } finally {
    btn.disabled = false; btn.textContent = old;
  }
});
