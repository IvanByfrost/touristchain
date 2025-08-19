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
        <img id="plane-main" src="/img/avion.png" alt="Avión TouristChain"
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
          <img src="img/avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
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
          <img src="img/avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
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
          <img src="img/avion.png" alt="Avión" class="h-24 plane-float" style="filter: drop-shadow(0 10px 18px rgba(0,0,0,.25));"/>
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

    <!-- ===== JS INCLUDES ===== -->
    <script src="<?php echo BASE_URL; ?>js/main.js" defer></script>
</body>