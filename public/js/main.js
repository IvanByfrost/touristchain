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