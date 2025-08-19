<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>TouristChain Futurista</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    :root{
      /* Paletas para las olas */
      --wave1-warm: rgba(229, 243, 37, 0.837);
      --wave2-warm: rgba(241, 209, 23, 0.71);
      --wave3-warm: rgba(252, 99, 33, 0.35);

      --wave1-cool: rgba(202, 39, 39, 0.47);
      --wave2-cool: rgba(41, 237, 87, 0.671);
      --wave3-cool: rgba(20, 196, 255, 0.9);
    }

    /* ===== Fondo (tema cálido por defecto) ===== */
    body{
      margin:0; overflow-x:hidden; min-height:100vh;
      font-family: 'Segoe UI', system-ui, -apple-system, Roboto, Arial, sans-serif;
      background: radial-gradient(1200px 600px at 50% -10%, #fccf65 0%, #f87955 60%, #ffd194 100%);
      color:#111;
      transition: background .10s ease, color .10s ease;
    }
    /* Tema frío cuando el toggle está activado */
    body.theme-cool{
      background: radial-gradient(1200px 600px at 50% -10%, #b4d7f7 0%, #7fb0ff 55%, #1247f3 100%);
      color:#0e1011;
    }

    /* ===== Header vidrio ===== */
    .glassbar{
      position:sticky; top:0; z-index:50;
      background: rgba(255, 255, 255, 0.278);
      backdrop-filter: blur(10px);
      box-shadow: 0 8px 24px rgba(0,0,0,.12);
    }

    /* ===== Canvas olas ===== */
    #bgCanvas{ position:fixed; bottom:0; left:0; width:100%; height:40vh; z-index:-1; }

    /* ===== HERO: avión + botón (no toca las tarjetas) ===== */
    .hero{ position:relative; height:56vh; display:flex; align-items:center; justify-content:center; }
    .plane-stack{ position:absolute; top:22vh; left:0; right:0; display:flex; flex-direction:column; align-items:center; gap:18px; z-index:5; }
    .plane-main{
      width:640px; max-width:88vw;
      position:relative; left:50%; transform:translateX(-50%);
      transform-origin: 70% 50%;
      filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));
      will-change: transform, opacity;
    }

    /* Flotando en reposo (no cambiado) */
    @keyframes flotando{
      0%{transform:translateX(-50%) translateY(0) rotate(-2deg) scale(1)}
      50%{transform:translateX(-50%) translateY(-10px) rotate(2deg) scale(1)}
      100%{transform:translateX(-50%) translateY(0) rotate(-2deg) scale(1)}
    }
    .float-main{ animation: flotando 3s ease-in-out infinite; }

    /* Vuelo (no cambiado) */
    @keyframes approachRight{
      0%  { transform:translateX(-50%) translateY(0) scale(1) rotate(0deg); opacity:1; }
      60% { transform:translateX(calc(-50% + 140px)) translateY(-6px) scale(1.55) rotate(-4deg); opacity:1; }
      100%{ transform:translateX(calc(-50% + 160px)) translateY(-8px) scale(1.65) rotate(-5deg); opacity:1; }
    }
    @keyframes exitRight{
      0%  { transform:translateX(calc(-50% + 160px)) translateY(-8px) scale(1.65) rotate(-5deg); opacity:1; }
      100%{ transform:translateX(calc(-50% + 980px)) translateY(-60px) scale(1.0) rotate(-8deg); opacity:0; }
    }
    @keyframes approachLeft{
      0%  { transform:translateX(-50%) translateY(0) scale(1) rotate(0deg); opacity:1; }
      60% { transform:translateX(calc(-50% - 140px)) translateY(-6px) scale(1.55) rotate(4deg);  opacity:1; }
      100%{ transform:translateX(calc(-50% - 160px)) translateY(-8px) scale(1.65) rotate(5deg);  opacity:1; }
    }
    @keyframes exitLeft{
      0%  { transform:translateX(calc(-50% - 160px)) translateY(-8px) scale(1.65) rotate(5deg); opacity:1; }
      100%{ transform:translateX(calc(-50% - 980px)) translateY(-60px) scale(1.0) rotate(8deg);  opacity:0; }
    }
    .dir-right.takeoff-step1{ animation: approachRight .9s ease-out forwards; }
    .dir-right.takeoff-step2{ animation: exitRight    .8s ease-in  forwards; }
    .dir-left.takeoff-step1 { animation: approachLeft  .9s ease-out forwards; }
    .dir-left.takeoff-step2 { animation: exitLeft      .8s ease-in  forwards; }

    /* Botón CTA debajo del avión (no cambiado) */
    .btn-cta{
      background:#ffd60a; color:#111; font-weight:900; font-size:1.15rem;
      padding:1rem 2rem; border-radius:999px; box-shadow: 0 8px 18px rgba(0,0,0,.2);
      border:2px solid rgb(19, 16, 16);
      transition: transform .2s ease, filter .2s ease;
    }
    .btn-cta:hover{ transform: translateY(-1px) scale(1.03); filter:brightness(.98); }

    /* ===== Tarjetas (no cambiado) ===== */
    .card{ background: rgba(233, 29, 29, 0.2); backdrop-filter: blur(10px); border-radius: 20px; padding: 24px; }

    /* ===== Formularios / vistas (no cambiado) ===== */
    .tc-input{ width:100%; border-radius:.75rem; padding:.75rem 1rem; background:rgba(255,255,255,.94);
      outline:none; border:1px solid rgba(255,255,255,.7); }
    .tc-input:focus{ box-shadow: 0 0 0 6px rgba(250,204,21,.35); }
    .tc-label{ font-weight:600; }
    .view{ display:none; } .view.active{ display:block; animation:fadeIn .25s ease-out; }
    @keyframes fadeIn{ from{opacity:0; transform:translateY(6px)} to{opacity:1; transform:none} }
    @keyframes planeFloat{0%{transform:translateY(0)}50%{transform:translateY(-6px)}100%{transform:translateY(0)}}
    .plane-float{ animation: planeFloat 4.5s ease-in-out infinite; }

    /* === Toggle moderno (calor↔frío) === */
    .toggle-wrap{ position:relative; width:76px; height:38px; }
    .toggle{
      appearance:none; -webkit-appearance:none; outline:none; cursor:pointer;
      width:100%; height:100%; border-radius:999px; border:1px solid rgba(255,255,255,.5);
      background: linear-gradient(135deg, #ffd64d, #ff9f43);
      position:relative; overflow:hidden; transition: background .35s ease, box-shadow .35s ease;
      box-shadow: inset 0 0 12px rgba(255,255,255,.3), 0 4px 18px rgba(0,0,0,.15);
    }
    .toggle::before{ /* “paisaje” */
      content:""; position:absolute; inset:4px 28px 4px 4px; border-radius:999px;
      background: linear-gradient(180deg, #ffecc7, #ffcfad);
      transition: inset .35s ease, filter .35s ease, background .35s ease;
    }
    .toggle::after{ /* “manija” */
      content:""; position:absolute; top:3px; right:3px; width:32px; height:32px; border-radius:50%;
      background:#fff; box-shadow: 0 2px 10px rgba(0,0,0,.25); transition: left .35s ease, right .35s ease, background .35s ease;
    }
    .toggle:checked{
      background: linear-gradient(135deg, #7fb0ff, #1e3a8a);
    }
    .toggle:checked::before{
      inset:4px 4px 4px 28px;
      background: linear-gradient(180deg, #cfe8ff, #7fb0ff);
      filter: saturate(1.1);
    }
    .toggle:checked::after{
      left:3px; right:auto; background:#0e6df3;
    }
  </style>
</head>
<body>
  <!-- ===== HEADER ===== -->
  <header class="glassbar px-4 sm:px-6">
    <div class="max-w-6xl mx-auto py-3 flex items-center justify-between">
      <a href="#/inicio" class="flex items-center gap-3 select-none">
        <span class="text-2xl sm:text-3xl font-extrabold text-white">
          Tourist<span class="text-yellow-300">Chain</span>
        </span>
        <span class="hidden sm:block text-white/90">· Explora el futuro del turismo</span>
      </a>
      <nav class="flex items-center gap-5">
        <a href="#/inicio" class="text-white hover:underline">Inicio</a>
        <a href="#/login" class="text-white hover:underline">Iniciar sesión</a>
        <a href="#/registro" class="text-white hover:underline">Registrarse</a>
        <!-- Toggle moderno -->
        <div class="toggle-wrap">
          <input id="themeToggle" type="checkbox" class="toggle" title="Calor ↔ Frío">
        </div>
      </nav>
    </div>
  </header>

  <!-- ===== INICIO ===== -->
  <main id="view-inicio" class="view active">
    <!-- Hero (avión + botón debajo) -->
    <section class="hero">
      <div class="plane-stack">
        <img id="plane-main" src="avion.png" alt="Avión TouristChain"
             class="plane-main float-main dir-left">
        <button id="cta-reservar" class="btn-cta">¡EMPEZAR MI VIAJE!</button>
      </div>
      <!-- Canvas olas de fondo -->
      <canvas id="bgCanvas"></canvas>
    </section>

    <!-- Tarjetas -->
    <section class="grid grid-cols-1 md:grid-cols-3 gap-8 w-11/12 mx-auto z-10">
      <div class="card">
        <h2 class="text-3xl font-extrabold mb-3">LAS MEJORES VACACIONES</h2>
        <p>ESTE SERÁ TU MEJOR DESCANSO</p>
      </div>
      <div class="card">
        <h2 class="text-3xl font-extrabold mb-3">TODO EN UN SOLO CLICK</h2>
        <p>LA MEJOR EXPERIENCIA</p>
      </div>
    </section>

    <footer class="mt-10 mb-10 text-center opacity-80">
      <p>© 2025 TouristChain - El Turismo Futurista</p>
    </footer>
  </main>

  <!-- ===== LOGIN ===== -->
  <section id="view-login" class="view">
    <div class="mx-auto max-w-2xl px-4 pt-20 pb-16">
      <div class="p-8 rounded-2xl" style="background:rgba(131, 116, 116, 0.292); backdrop-filter:blur(10px); box-shadow:0 8px 24px rgba(0,0,0,.18);">
        <div class="flex justify-center mb-4">
          <img src="avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
        </div>
        <h2 class="text-4xl font-extrabold text-center mb-6">Iniciar sesión</h2>
        <form id="loginForm" class="space-y-5" onsubmit="return fakeSubmit(event,'login')">
          <div>
            <label class="tc-label">Correo/gmail</label>
            <input type="email" required placeholder="ejemplo@gmail.com" class="tc-input" />
          </div>
          <div>
            <label class="tc-label">Contraseña</label>
            <input type="password" required placeholder="********" class="tc-input" />
            <div class="text-right mt-1">
              <a href="#/olvide" class="text-sm font-semibold underline">¿Olvidaste tu contraseña?</a>
            </div>
          </div>
          <button class="btn-cta w-full">Ingresar</button>
        </form>
        <p class="text-center mt-6">¿No tienes cuenta?
          <a class="font-semibold underline" href="#/registro">Regístrate</a>
        </p>
        <p class="text-center mt-1"><a class="text-sm underline" href="#/inicio">Volver al Inicio</a></p>
      </div>
    </div>
  </section>

  <!-- ===== REGISTRO ===== -->
  <section id="view-registro" class="view">
    <div class="mx-auto max-w-2xl px-4 pt-20 pb-16">
      <div class="p-8 rounded-2xl" style="background:rgba(166, 247, 25, 0.2); backdrop-filter:blur(10px); box-shadow:0 8px 24px rgba(0,0,0,.18);">
        <div class="flex justify-center mb-4">
          <img src="avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
        </div>
        <h2 class="text-4xl font-extrabold text-center mb-6">Crear Cuenta</h2>
        <form id="registerForm" class="space-y-5" onsubmit="return fakeSubmit(event,'registro')">
          <div>
            <label class="tc-label">Correo</label>
            <input type="email" required placeholder="ejemplo@gmail.com" class="tc-input" />
          </div>
          <div>
            <label class="tc-label">Contraseña</label>
            <input type="password" required placeholder="********" class="tc-input" />
          </div>
          <div>
            <label class="tc-label">Confirmar Contraseña</label>
            <input type="password" required placeholder="********" class="tc-input" />
          </div>
          <div>
            <label class="tc-label">Tipo de Usuario</label>
            <select class="tc-input">
              <option>Selecciona tu rol</option>
              <option>Turista</option>
              <option>Socio</option>
              <option>Administrador</option>
            </select>
          </div>
          <button class="btn-cta w-full">Registrarse</button>
        </form>
        <p class="text-center mt-6">¿Ya tienes cuenta?
          <a class="font-semibold underline" href="#/login">Inicia sesión</a>
        </p>
        <p class="text-center mt-1"><a class="text-sm underline" href="#/inicio">Volver al Inicio</a></p>
      </div>
    </div>
  </section>

  <!-- ===== OLVIDÉ ===== -->
  <section id="view-olvide" class="view">
    <div class="mx-auto max-w-2xl px-4 pt-20 pb-16">
      <div class="p-8 rounded-2xl" style="background:rgba(255,255,255,.2); backdrop-filter:blur(10px); box-shadow:0 8px 24px rgba(0,0,0,.18);">
        <div class="flex justify-center mb-4">
          <img src="avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
        </div>
        <h2 class="text-3xl font-extrabold text-center mb-6">Recuperar contraseña</h2>
        <form id="forgotForm" class="space-y-5" onsubmit="return fakeSubmit(event,'forgot')">
          <div>
            <label class="tc-label">Correo asociado a tu cuenta</label>
            <input type="email" required placeholder="ejemplo@gmail.com" class="tc-input" />
          </div>
          <button class="btn-cta w-full">Enviar enlace de recuperación</button>
        </form>
        <p class="text-center mt-4"><a class="text-sm underline" href="#/login">Volver a Iniciar sesión</a></p>
      </div>
    </div>
  </section>

  <script>
    /* ===== Router simple por hash (sin cambios) ===== */
    const views = {
      inicio: document.getElementById('view-inicio'),
      login: document.getElementById('view-login'),
      registro: document.getElementById('view-registro'),
      olvide: document.getElementById('view-olvide'),
    };
    function showView(name){
      Object.values(views).forEach(v => v.classList.remove('active'));
      (views[name] || views.inicio).classList.add('active');
      if(name === 'inicio'){
        const p = document.getElementById('plane-main');
        p.classList.remove('takeoff-step1','takeoff-step2');
        p.classList.add('float-main');
      }
    }
    function syncFromHash(){
      const name = (location.hash || '#/inicio').replace('#/','');
      showView(name);
    }
    addEventListener('hashchange', syncFromHash);
    syncFromHash();

    /* ===== Olas (paleta ajustable por tema) ===== */
    const canvas = document.getElementById('bgCanvas');
    const ctx = canvas.getContext('2d');
    let W=0, H=0, T=0;
    function resize(){ W = canvas.width = innerWidth; H = canvas.height = innerHeight*0.38; }
    addEventListener('resize', resize); resize();

    // paleta dinámica
    let waveColors = ['rgba(255,255,255,0.55)','rgba(255,210,160,0.45)','rgba(255,180,150,0.35)'];

    function setWavePalette(mode){
      const root = getComputedStyle(document.documentElement);
      waveColors = mode === 'cool'
        ? [root.getPropertyValue('--wave1-cool'), root.getPropertyValue('--wave2-cool'), root.getPropertyValue('--wave3-cool')]
        : [root.getPropertyValue('--wave1-warm'), root.getPropertyValue('--wave2-warm'), root.getPropertyValue('--wave3-warm')];
    }

    (function draw(){
      ctx.clearRect(0,0,W,H);
      for(let i=0;i<3;i++){
        ctx.beginPath();
        for(let x=0;x<W;x++){
          const y = Math.sin((x + T*2)*0.02 + i) * 15 + H/2 + i*10;
          ctx.lineTo(x,y);
        }
        ctx.strokeStyle = waveColors[i].trim();
        ctx.lineWidth = 2+i;
        ctx.stroke();
      }
      T += 0.5; requestAnimationFrame(draw);
    })();

    /* ===== Toggle Calor ↔ Frío (tema + olas) ===== */
    const themeToggle = document.getElementById('themeToggle');

    function applyTheme(){
      const cool = themeToggle.checked;      // checked = frío
      document.body.classList.toggle('theme-cool', cool);
      setWavePalette(cool ? 'cool' : 'warm');
    }

    themeToggle.addEventListener('change', applyTheme);
    // estado inicial (cálido)
    applyTheme();

    /* ===== SOLO LO DEL BOTÓN: ir a login al click ===== */
    const cta = document.getElementById('cta-reservar');
    cta.addEventListener('click', () => {
      location.hash = '#/login';
    });

    /* === ADICIÓN: Secuencia de despegue ANTES de navegar ===
       Usamos un listener en fase de *captura* para frenar el handler anterior,
       correr la animación (approach + exit) y luego sí cambiar el hash. */
    (function addTakeoffSequence(){
      const plane = document.getElementById('plane-main');
      const btn = document.getElementById('cta-reservar');

      btn.addEventListener('click', function(ev){
        ev.preventDefault();
        ev.stopImmediatePropagation(); // cancela el listener que navega de una
        // reset de animaciones previas
        plane.classList.remove('float-main','takeoff-step1','takeoff-step2');
        void plane.offsetWidth;

        // Paso 1: acercamiento
        plane.classList.add('takeoff-step1');

        plane.addEventListener('animationend', function step1(){
          plane.removeEventListener('animationend', step1);
          plane.classList.remove('takeoff-step1');
          // Paso 2: salida
          plane.classList.add('takeoff-step2');

          plane.addEventListener('animationend', function step2(){
            plane.removeEventListener('animationend', step2);
            plane.classList.remove('takeoff-step2');
            // Ahora sí, navegar al login
            location.hash = '#/login';
          }, { once:true });
        }, { once:true });
      }, true /* <— captura */);
    })();

    /* ===== Submits demo (sin cambios) ===== */
    function fakeSubmit(ev, kind){
      ev.preventDefault();
      const btn = ev.target.querySelector('button');
      const t = btn.textContent; btn.textContent = 'Procesando…'; btn.disabled = true;
      setTimeout(() => {
        btn.textContent = t; btn.disabled = false;
        if(kind === 'registro'){ location.hash = '#/login'; }
        else if(kind === 'forgot'){ alert('Te enviamos un enlace de recuperación (demo).'); }
        else { alert('Login exitoso (demo).'); location.hash = '#/inicio'; }
      }, 900);
      return false;
    }
  </script>
</body>
</html>
