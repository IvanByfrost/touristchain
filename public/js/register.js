document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('registerForm');
  const msg  = document.getElementById('registerMsg'); // <div id="registerMsg"></div> en tu HTML

  if (!form) return;

  const showMsg = (text, ok = false) => {
    if (!msg) return; 
    msg.textContent = text;
    msg.className = ok ? 'text-green-700 mt-2' : 'text-red-700 mt-2';
  };

  form.addEventListener('submit', async (ev) => {
    ev.preventDefault();

    // 1) Lectura rápida y validación ligera
    const fd = new FormData(form);
    const email = (fd.get('email') || '').toString().trim();
    const pass1 = (fd.get('password') || '').toString();
    const pass2 = (fd.get('password_confirm') || '').toString();
    const role  = (fd.get('role') || '').toString();

    if (!email) { showMsg('El correo es obligatorio'); return; }
    if (pass1.length < 6) { showMsg('Mínimo 6 caracteres'); return; }
    if (pass1 !== pass2) { showMsg('Las contraseñas no coinciden'); return; }
    if (!role) { showMsg('Selecciona un rol'); return; }

    // 2) Feedback inmediato y CEDER UN FRAME
    const btn = form.querySelector('button[type="submit"]');
    const old = btn.textContent;
    btn.disabled = true; btn.textContent = 'Procesando…';
    showMsg('');

    // Cede microtarea y un frame para que el navegador pinte el cambio del botón
    await Promise.resolve();
    await new Promise(requestAnimationFrame);

    // 3) Timeout + AbortController (evita hangs)
    const ac = new AbortController();
    const t = setTimeout(() => ac.abort(), 10000); // 10s de timeout

    try {
      const res = await fetch(form.action || '<?php echo BASE_URL; ?>api/register', {
        method: 'POST',
        body: fd,
        headers: { 'Accept': 'application/json' },
        signal: ac.signal,
      });
      clearTimeout(t);

      // Cede microtarea antes del JSON (UI más fluida)
      await Promise.resolve();

      const json = await res.json().catch(() => ({}));

      if (res.ok && json?.success) {
        showMsg('Cuenta creada. Redirigiendo…', true);
        // Cede un frame para que se vea el mensaje verde
        await new Promise(requestAnimationFrame);
        location.hash = '#/login';
      } else {
        showMsg(json?.message || 'No se pudo registrar');
      }
    } catch (err) {
      clearTimeout(t);
      if (err.name === 'AbortError') showMsg('Tiempo de espera agotado. Intenta de nuevo.');
      else showMsg('Error de red o servidor.');
      console.error(err);
    } finally {
      btn.disabled = false; btn.textContent = old;
    }
  });
});
