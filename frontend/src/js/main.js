import { api } from './api.js';
import QRCode from 'qrcode';
import '../scss/main.scss';

// ============================
// ESTADO GLOBAL
// ============================
const appState = {
    currentSlide: { viajes: 0, renta: 0, team: 0 },
    trips: [],
    cars: [],
    hotels: [],
    favorites: JSON.parse(localStorage.getItem('touristchain-favorites')) || [],
    reservations: JSON.parse(localStorage.getItem('touristchain-reservations')) || [],
    paymentMethods: JSON.parse(localStorage.getItem('touristchain-payment-methods')) || [],
    user: api.getUser() || {
        name: 'Invitado',
        email: '',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guest',
        loyaltyPoints: 0
    },
    theme: localStorage.getItem('touristchain-theme') || 'default',
    cookiesAccepted: localStorage.getItem('touristchain-cookies') || false,
    notifications: [],
    map: null,
    mapMarkers: [],
    scene3d: null
};

const teamData = [
    // Para usar fotos reales: coloca los archivos en public/assets/team/
    // ej. public/assets/team/cristian.jpg y cambia avatar a '/assets/team/cristian.jpg'
    { id: 1, name: 'CRISTIAN', role: 'CEO & Fundador', desc: 'Fundador y visión estratégica de TouristChain.', avatar: '/assets/team/cristian.jpg', fallback: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Cristian', social: { linkedin: '#', github: '#' } },
    { id: 2, name: 'Carlos Pérez', role: 'COO', desc: 'Operaciones y alianzas estratégicas globales.', avatar: '/assets/team/carlos.jpg', fallback: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Carlos', social: { linkedin: '#', github: '#' } },
    { id: 3, name: 'LUCI NOVA', role: 'UX/UI Designer', desc: 'Diseño centrado en experiencias futuristas.', avatar: '/assets/team/luci.jpg', fallback: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Luci', social: { linkedin: '#', github: '#' } },
    { id: 4, name: 'JOHAN CARRILLO', role: 'Backend Lead', desc: 'Arquitectura segura y APIs de alto rendimiento.', avatar: '/assets/team/johan.jpg', fallback: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Johan', social: { linkedin: '#', github: '#' } },
    { id: 5, name: 'IVAN RUIZ', role: 'QA Engineer', desc: 'Calidad y automatización de procesos.', avatar: '/assets/team/ivan.jpg', fallback: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ivan', social: { linkedin: '#', github: '#' } }
];

// Escapado anti-XSS para todo texto que venga de la BD o del usuario
function esc(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// ============================
// NOTIFICACIONES TOAST
// ============================
function showNotification(message, type = 'success') {
    const toast = document.getElementById('notification-toast');
    if (!toast) return;
    toast.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i> ${message}`;
    toast.className = `notification-toast ${type} show`;
    setTimeout(() => toast.classList.remove('show'), 3500);
}

// ============================
// NAVEGACIÓN + SESIÓN
// ============================
// Actualiza el header según sesión: nombre, menú invitado/usuario.
function updateAuthUI() {
    const logged = api.isAuthenticated();
    const user = logged ? (api.getUser() || appState.user) : null;
    const btnText = document.getElementById('cuenta-btn-text');
    if (btnText) btnText.textContent = logged && user?.name ? user.name.split(' ')[0] : 'Mi Cuenta';
    document.getElementById('cuenta-menu-guest')?.classList.toggle('tc-hidden', logged);
    document.getElementById('cuenta-menu-user')?.classList.toggle('tc-hidden', !logged);
}

function logout() {
    api.logout();
    appState.user = { name: 'Invitado', email: '', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guest', loyaltyPoints: 0 };
    updateAuthUI();
    showNotification('Sesión cerrada');
    showView('inicio');
}

function showView(viewId) {
    // Proteger dashboard: sin sesión -> login
    if (viewId === 'dashboard' && !api.isAuthenticated()) {
        viewId = 'login';
    }
    // Si ya hay sesión e intenta ir a login/registro -> dashboard
    if ((viewId === 'login' || viewId === 'registro') && api.isAuthenticated()) {
        viewId = 'dashboard';
    }
    if (viewId === 'cuenta') {
        viewId = api.isAuthenticated() ? 'dashboard' : 'login';
    }
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.querySelectorAll('.tc-nav-link').forEach(l => l.classList.remove('active'));

    const target = document.getElementById(viewId);
    if (target) {
        target.classList.add('active');
        if (window.location.hash !== '#' + viewId) history.replaceState(null, '', '#' + viewId);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        document.querySelectorAll(`.tc-nav-link[href="#${viewId}"]`).forEach(l => l.classList.add('active'));

        if (viewId === 'viajes') initViajes();
        if (viewId === 'hoteles') initHotels();
        if (viewId === 'renta') initRenta();
        if (viewId === 'socios') initPartners();
        if (viewId === 'nosotros') initTeamCarousel();
        if (viewId === 'dashboard') initDashboard();

        if (!['login', 'registro', 'forgot-password', 'terms'].includes(viewId)) checkCookies();
    }
}

function showDashboardSection(sectionId) {
    document.querySelectorAll('.dashboard-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.dashboard-menu a').forEach(a => a.classList.remove('active'));
    const section = document.getElementById(sectionId);
    if (section) section.classList.add('active');
    const link = document.querySelector(`.dashboard-menu a[href="#${sectionId}"]`);
    if (link) link.classList.add('active');
}

// ============================
// TEMA
// ============================
function applyTheme() {
    const body = document.body;
    const toggle = document.getElementById('theme-toggle');
    body.classList.remove('theme-dark');

    if (appState.theme === 'dark') {
        body.classList.add('theme-dark');
        if (toggle) toggle.innerHTML = '<i class="fas fa-moon"></i>';
    } else {
        if (toggle) toggle.innerHTML = '<i class="fas fa-sun"></i>';
    }
}

function toggleTheme() {
    appState.theme = appState.theme === 'dark' ? 'default' : 'dark';
    localStorage.setItem('touristchain-theme', appState.theme);
    applyTheme();
}

// ============================
// COOKIES
// ============================
function checkCookies() {
    if (!appState.cookiesAccepted) {
        setTimeout(() => document.getElementById('cookies-banner')?.classList.add('show'), 1500);
    }
}
function acceptCookies() {
    appState.cookiesAccepted = 'true';
    localStorage.setItem('touristchain-cookies', 'true');
    document.getElementById('cookies-banner')?.classList.remove('show');
    showNotification('Cookies aceptadas');
}
function rejectCookies() {
    appState.cookiesAccepted = 'false';
    localStorage.setItem('touristchain-cookies', 'false');
    document.getElementById('cookies-banner')?.classList.remove('show');
    showNotification('Cookies rechazadas', 'warning');
}

// ============================
// CARRUSELES
// ============================
function initCarousel(type, items, renderFn, trackId, navId) {
    const track = document.getElementById(trackId);
    const nav = document.getElementById(navId);
    if (!track || !nav) return;

    track.innerHTML = '';
    nav.innerHTML = '';
    items.forEach((item, index) => {
        const slide = document.createElement('div');
        slide.className = type === 'team' ? 'team-carousel-slide' : 'carousel-slide';
        slide.innerHTML = renderFn(item);
        track.appendChild(slide);
        const dot = document.createElement('div');
        dot.className = `carousel-dot ${index === 0 ? 'active' : ''}`;
        dot.addEventListener('click', () => goToSlide(type, index));
        nav.appendChild(dot);
    });
    appState.currentSlide[type] = 0;

    const container = track.closest('.carousel-container') || track.closest('.team-carousel-container');
    if (container) {
        container.querySelectorAll('.carousel-arrow').forEach(arrow => {
            arrow.addEventListener('click', () => arrow.classList.contains('prev') ? prevSlide(type) : nextSlide(type));
        });
    }
}
function goToSlide(type, index) {
    const trackId = type === 'team' ? 'team-carousel-track' : `${type}-carousel-track`;
    const navId = type === 'team' ? 'team-carousel-nav' : `${type}-carousel-nav`;
    const track = document.getElementById(trackId);
    const dots = document.querySelectorAll(`#${navId} .carousel-dot`);
    if (track) {
        track.style.transform = `translateX(-${index * 100}%)`;
        appState.currentSlide[type] = index;
        dots.forEach((d, i) => d.classList.toggle('active', i === index));
    }
}
function nextSlide(type) {
    const total = document.querySelectorAll(`#${type === 'team' ? 'team-carousel-track' : `${type}-carousel-track`} > div`).length;
    goToSlide(type, (appState.currentSlide[type] + 1) % total);
}
function prevSlide(type) {
    const total = document.querySelectorAll(`#${type === 'team' ? 'team-carousel-track' : `${type}-carousel-track`} > div`).length;
    goToSlide(type, (appState.currentSlide[type] - 1 + total) % total);
}

// ============================
// VIAJES
// ============================
async function loadTrips() { appState.trips = await api.getTrips().catch(() => []); }
function getFilteredTrips() {
    const search = document.getElementById('trip-search')?.value.toLowerCase() || '';
    const price = document.getElementById('trip-price-filter')?.value || '';
    const tag = document.getElementById('trip-tag-filter')?.value || '';
    return appState.trips.filter(trip => {
        if (search && !trip.destination.toLowerCase().includes(search) && !trip.description.toLowerCase().includes(search)) return false;
        if (price) { const [min, max] = price.split('-').map(Number); if (trip.price < min || trip.price > max) return false; }
        if (tag && (!trip.tags || !trip.tags.includes(tag))) return false;
        return true;
    });
}
function renderTripCard(trip) {
    const isFav = appState.favorites.some(f => f.id === trip.id && f.type === 'viaje');
    const days = Math.ceil((new Date(trip.endDate) - new Date(trip.startDate)) / 86400000) || 7;
    return `
        <div class="column is-12-tablet is-6-desktop is-4-widescreen">
            <div class="viaje-card">
                <button class="favorite-btn ${isFav ? 'active' : ''}" data-id="${trip.id}" data-type="viaje"><i class="${isFav ? 'fas' : 'far'} fa-heart"></i></button>
                <div class="viaje-imagen" style="background-image: url('${trip.image}')">
                    <div class="viaje-imagen-content"><h4>${esc(trip.destination)}</h4><p>${esc(trip.description.substring(0, 70))}...</p></div>
                </div>
                <div class="viaje-card-body">
                    <div class="rating"><i class="fas fa-star"></i> ${trip.rating || '4.5'} (${trip.reviews || 0})</div>
                    <div><span class="price">$${trip.price.toLocaleString()}</span> <span class="duration">/ ${days} días</span></div>
                    <button class="button is-cta is-fullwidth mt-3" data-id="${trip.id}" data-type="viaje"><i class="fas fa-cart-plus"></i> Reservar</button>
                    <button class="button is-outline is-fullwidth mt-2 review-open-btn" data-id="${trip.id}" data-type="viaje" data-name="${esc(trip.destination)}"><i class="fas fa-star"></i> Reseñar</button>
                </div>
            </div>
        </div>
    `;
}
function renderCarouselSlide(trip) {
    return `
        <div class="glass-card" style="height: 100%;">
            <div class="viaje-imagen" style="background-image: url('${trip.image}'); height: 240px;">
                <div class="viaje-imagen-content"><h3>${esc(trip.destination)}</h3></div>
            </div>
            <div class="viaje-card-body">
                <p>${esc(trip.description.substring(0, 100))}...</p>
                <div class="mt-3"><span class="price">$${trip.price.toLocaleString()}</span></div>
                <button class="button is-cta is-fullwidth mt-3" data-id="${trip.id}" data-type="viaje">Reservar Ahora</button>
            </div>
        </div>
    `;
}
function renderTripsGrid() {
    const grid = document.getElementById('viajes-grid');
    if (!grid) return;
    const trips = getFilteredTrips();
    grid.innerHTML = trips.length ? trips.map(renderTripCard).join('') : '<p class="has-text-centered tc-text-white">No se encontraron destinos</p>';
    bindTripButtons();
}

// ============================
// HOTELES
// ============================
async function loadHotels() { appState.hotels = await api.getHotels().catch(() => []); }
function getFilteredHotels() {
    const search = document.getElementById('hotel-search')?.value.toLowerCase() || '';
    const stars = document.getElementById('hotel-stars-filter')?.value || '';
    const city = document.getElementById('hotel-city-filter')?.value || '';
    return appState.hotels.filter(h => {
        if (search && !h.name.toLowerCase().includes(search) && !h.city.toLowerCase().includes(search)) return false;
        if (stars && h.stars !== parseInt(stars)) return false;
        if (city && !h.city.toLowerCase().includes(city.toLowerCase())) return false;
        return true;
    });
}
function renderHotelCard(hotel) {
    const stars = Array(hotel.stars).fill('<i class="fas fa-star"></i>').join('');
    return `
        <div class="column is-12-tablet is-6-desktop is-4-widescreen">
            <div class="hotel-card">
                <div class="hotel-image" style="background-image: url('${hotel.image}')">
                    <div class="hotel-image-content"><h4>${esc(hotel.name)}</h4><p>${esc(hotel.city)}</p></div>
                </div>
                <div class="hotel-card-body">
                    <div class="hotel-stars">${stars}</div>
                    <p style="opacity: 0.9; font-size: 0.9rem;">${esc(hotel.description)}</p>
                    <div class="mt-2"><span class="price">$${hotel.price}</span> <span class="duration">/ noche</span></div>
                    <div style="margin: 0.5rem 0; font-size: 0.8rem; opacity: 0.8;">${hotel.amenities.map(a => `<span class="tag is-light is-small mr-1">${esc(a)}</span>`).join('')}</div>
                    <button class="button is-cta is-fullwidth mt-2" data-id="${hotel.id}" data-type="hotel"><i class="fas fa-cart-plus"></i> Reservar</button>
                    <button class="button is-outline is-fullwidth mt-2 viewer-3d-btn" data-id="${hotel.id}" data-type="hotel"><i class="fas fa-cube"></i> Ver en 3D</button>
                </div>
            </div>
        </div>
    `;
}
async function initHotels() {
    if (appState.hotels.length === 0) await loadHotels();
    const grid = document.getElementById('hotels-grid');
    if (!grid) return;
    const hotels = getFilteredHotels();
    grid.innerHTML = hotels.length ? hotels.map(renderHotelCard).join('') : '<p class="has-text-centered tc-text-white">No se encontraron hoteles</p>';

    const cityFilter = document.getElementById('hotel-city-filter');
    if (cityFilter && cityFilter.options.length <= 1) {
        const cities = [...new Set(appState.hotels.map(h => h.city.split(',')[0]))];
        cityFilter.innerHTML = '<option value="">Ciudad</option>' + cities.map(c => `<option value="${c}">${c}</option>`).join('');
    }

    grid.querySelectorAll('.button.is-cta[data-type="hotel"]').forEach(btn => {
        btn.addEventListener('click', () => {
            const hotel = appState.hotels.find(h => h.id === parseInt(btn.dataset.id));
            if (hotel) addReservation(hotel, 'hotel');
        });
    });
    grid.querySelectorAll('.viewer-3d-btn[data-type="hotel"]').forEach(btn => {
        btn.addEventListener('click', () => open3DViewer('hotel', parseInt(btn.dataset.id)));
    });
}

// ============================
// MAPA DE COLOMBIA
// ============================
// Destinos destacados de Colombia (centro del mapa)
const colombiaSpots = [
    { name: 'Cartagena de Indias', city: 'Bolívar', lat: 10.391, lng: -75.479, price: 850, desc: 'Ciudad amurallada, playas y atardeceres en el Caribe.' },
    { name: 'Santa Marta + Tayrona', city: 'Magdalena', lat: 11.241, lng: -74.199, price: 780, desc: 'Sierra Nevada, playas del Tayrona y Ciudad Perdida.' },
    { name: 'San Andrés y Providencia', city: 'Archipiélago', lat: 12.585, lng: -81.708, price: 920, desc: 'Mar de los 7 colores, buceo y relax caribeño.' },
    { name: 'Medellín', city: 'Antioquia', lat: 6.244, lng: -75.581, price: 640, desc: 'Ciudad de la eterna primavera, cultura y metrocable.' },
    { name: 'Eje Cafetero', city: 'Quindío', lat: 4.535, lng: -75.673, price: 590, desc: 'Fincas cafeteras, Cocora y termales en la montaña.' },
    { name: 'Bogotá D.C.', city: 'Cundinamarca', lat: 4.711, lng: -74.072, price: 520, desc: 'Monserrate, museos y gastronomía en la capital.' }
];

function initMap() {
    const mapEl = document.getElementById('destinations-map');
    if (!mapEl || typeof L === 'undefined') return;

    if (appState.map) { appState.map.remove(); appState.map = null; }
    appState.mapMarkers = [];
    // Centrado en Colombia
    appState.map = L.map(mapEl).setView([4.57, -74.3], 6);
    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; OpenStreetMap &copy; CARTO', subdomains: 'abcd', maxZoom: 19
    }).addTo(appState.map);

    const colombiaIcon = L.divIcon({ html: '<i class="fas fa-map-marker-alt" style="color:#ffd60a;font-size:1.4rem;"></i>', iconSize: [22, 22], className: 'map-icon' });
    const planeIcon = L.divIcon({ html: '<i class="fas fa-plane" style="color:#4a6fff;font-size:1.2rem;"></i>', iconSize: [20, 20], className: 'map-icon' });
    const hotelIcon = L.divIcon({ html: '<i class="fas fa-hotel" style="color:#ff3860;font-size:1.2rem;"></i>', iconSize: [20, 20], className: 'map-icon' });

    colombiaSpots.forEach(spot => {
        const marker = L.marker([spot.lat, spot.lng], { icon: colombiaIcon }).addTo(appState.map);
        marker.bindPopup(`<b>🇨🇴 ${spot.name}</b><br>${spot.city}<br>${spot.desc}<br><strong>Desde $${spot.price.toLocaleString()}</strong>`);
        appState.mapMarkers.push(marker);
    });

    appState.trips.forEach(trip => {
        if (trip.coordinates) {
            const marker = L.marker([trip.coordinates.lat, trip.coordinates.lng], { icon: planeIcon }).addTo(appState.map);
            marker.bindPopup(`<b>✈️ ${trip.destination}</b><br>Desde $${trip.price.toLocaleString()}`);
            appState.mapMarkers.push(marker);
        }
    });
    appState.hotels.forEach(hotel => {
        if (hotel.coordinates) {
            const marker = L.marker([hotel.coordinates.lat, hotel.coordinates.lng], { icon: hotelIcon }).addTo(appState.map);
            marker.bindPopup(`<b>🏨 ${hotel.name}</b><br>${hotel.city}<br>$${hotel.price}/noche`);
            appState.mapMarkers.push(marker);
        }
    });

    // Botón "Ver Colombia": recentra el mapa
    if (!document.getElementById('recenter-co-btn')) {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'recenter-co-btn';
        btn.className = 'button is-small is-cta mt-2';
        btn.innerHTML = '<i class="fas fa-crosshairs"></i> Ver Colombia';
        btn.addEventListener('click', () => appState.map?.setView([4.57, -74.3], 6));
        mapEl.after(btn);
    }
}

// ============================
// RENTA DE CARROS
// ============================
async function loadCars() { appState.cars = await api.getCars().catch(() => []); }
function getFilteredCars() {
    const category = document.getElementById('car-category-filter')?.value || '';
    const sort = document.getElementById('car-sort')?.value || '';
    let cars = [...appState.cars];
    if (category) cars = cars.filter(c => c.category === category);
    if (sort === 'price-asc') cars.sort((a, b) => a.price - b.price);
    if (sort === 'price-desc') cars.sort((a, b) => b.price - a.price);
    return cars;
}
function renderCarCard(car) {
    const isFav = appState.favorites.some(f => f.id === car.id && f.type === 'car');
    return `
        <div class="column is-12-tablet is-6-desktop is-4-widescreen">
            <div class="viaje-card">
                <button class="favorite-btn ${isFav ? 'active' : ''}" data-id="${car.id}" data-type="car"><i class="${isFav ? 'fas' : 'far'} fa-heart"></i></button>
                <div class="viaje-imagen" style="background-image: url('${car.image}')">
                    <div class="viaje-imagen-content"><h4>${esc(car.name)}</h4><p>${esc(car.type)}</p></div>
                </div>
                <div class="viaje-card-body">
                    <div class="rating"><i class="fas fa-star"></i> ${car.rating || '4.5'} (${car.reviews || 0})</div>
                    <div><span class="price">$${car.price}</span> <span class="duration">/ día</span></div>
                    <ul style="opacity: 0.9; font-size: 0.85rem; margin: 0.5rem 0;">${car.features.map(f => `<li><i class="fas fa-check" style="color: #23d160;"></i> ${esc(f)}</li>`).join('')}</ul>
                    <button class="button is-cta is-fullwidth viewer-3d-btn" data-id="${car.id}" data-type="car"><i class="fas fa-cube"></i> Ver en 3D</button>
                    <button class="button is-outline is-fullwidth mt-2" data-id="${car.id}" data-type="car"><i class="fas fa-cart-plus"></i> Rentar</button>
                    <button class="button is-outline is-fullwidth mt-2 review-open-btn" data-id="${car.id}" data-type="car" data-name="${esc(car.name)}"><i class="fas fa-star"></i> Reseñar</button>
                </div>
            </div>
        </div>
    `;
}
async function initRenta() {
    if (appState.cars.length === 0) await loadCars();
    const grid = document.getElementById('cars-grid');
    if (!grid) return;
    const cars = getFilteredCars();
    grid.innerHTML = cars.length ? cars.map(renderCarCard).join('') : '<p class="has-text-centered tc-text-white">No se encontraron vehículos</p>';

    grid.querySelectorAll('.favorite-btn[data-type="car"]').forEach(btn => {
        btn.addEventListener('click', () => toggleFavorite(parseInt(btn.dataset.id), 'car'));
    });
    grid.querySelectorAll('.button.is-cta[data-type="car"]').forEach(btn => {
        btn.addEventListener('click', () => open3DViewer('car', parseInt(btn.dataset.id)));
    });
    grid.querySelectorAll('.button.is-outline[data-type="car"]').forEach(btn => {
        if (btn.classList.contains('review-open-btn')) return;
        btn.addEventListener('click', () => {
            const car = appState.cars.find(c => c.id === parseInt(btn.dataset.id));
            if (car) addReservation(car, 'car');
        });
    });
    grid.querySelectorAll('.review-open-btn[data-type="car"]').forEach(btn => {
        btn.addEventListener('click', () => openReviewModal('car', parseInt(btn.dataset.id), btn.dataset.name));
    });
}

// ============================
// VISOR FOTO 360 / PANORÁMICO (foto real arrastrable)
// ============================
function open3DViewer(type, id) {
    const modal = document.getElementById('viewer-3d-modal');
    const title = document.getElementById('viewer-3d-title');
    const info = document.getElementById('viewer-3d-info');
    const canvas = document.getElementById('viewer-3d-canvas');

    const item = type === 'car' ? appState.cars.find(c => c.id === id) : appState.hotels.find(h => h.id === id);
    if (!item) return;

    const nombre = item.name || item.destination || 'Detalle';
    title.textContent = nombre;
    info.innerHTML = `
        <h3>${nombre}</h3>
        <p>${item.description || item.type || ''}</p>
        <ul>${(item.features || item.amenities || []).map(f => `<li><i class="fas fa-check"></i> ${f}</li>`).join('')}</ul>
        <div class="mt-3"><strong style="font-size: 1.3rem; color: var(--warm-accent);">$${Number(item.price || 0).toLocaleString()}</strong> ${type === 'hotel' ? '/ noche' : '/ día'}</div>
        <p class="is-size-7 mt-2" style="opacity:.8"><i class="fas fa-hand-pointer"></i> Arrastra la foto para explorar · scroll/pellizca para zoom</p>
        <button class="button is-cta mt-3 reserve-3d" data-id="${item.id}" data-type="${type}"><i class="fas fa-cart-plus"></i> ${type === 'hotel' ? 'Reservar' : 'Rentar'}</button>
    `;
    info.querySelector('.reserve-3d').addEventListener('click', () => { addReservation(item, type); modal.classList.remove('is-active'); });

    modal.classList.add('is-active');
    canvas.innerHTML = '';
    buildPhotoViewer(item.image, canvas, nombre);
}

function buildPhotoViewer(imageUrl, container, alt = 'Vista') {
    if (appState.scene3d) {
        appState.scene3d.dispose && appState.scene3d.dispose();
        appState.scene3d = null;
    }
    container.classList.add('photo360');
    container.innerHTML = `
        <img class="photo360-img" src="${imageUrl}" alt="${alt}" draggable="false">
        <div class="photo360-controls">
            <button type="button" data-zoom="in" aria-label="Acercar"><i class="fas fa-search-plus"></i></button>
            <button type="button" data-zoom="out" aria-label="Alejar"><i class="fas fa-search-minus"></i></button>
            <button type="button" data-zoom="reset" aria-label="Restablecer"><i class="fas fa-expand"></i></button>
        </div>`;
    const img = container.querySelector('.photo360-img');
    let scale = 1, x = 0, y = 0, dragging = false, sx = 0, sy = 0, lx = 0, ly = 0;
    const apply = () => { img.style.transform = `translate(${x}px, ${y}px) scale(${scale})`; };
    const clamp = () => {
        const maxX = (img.clientWidth * scale - container.clientWidth) / 2;
        const maxY = (img.clientHeight * scale - container.clientHeight) / 2;
        x = Math.max(-Math.max(maxX, 0), Math.min(Math.max(maxX, 0), x));
        y = Math.max(-Math.max(maxY, 0), Math.min(Math.max(maxY, 0), y));
    };
    img.addEventListener('mousedown', e => { dragging = true; sx = e.clientX; sy = e.clientY; lx = x; ly = y; container.classList.add('grabbing'); });
    window.addEventListener('mouseup', () => { dragging = false; container.classList.remove('grabbing'); });
    window.addEventListener('mousemove', e => { if (!dragging) return; x = lx + (e.clientX - sx); y = ly + (e.clientY - sy); clamp(); apply(); });
    img.addEventListener('touchstart', e => { dragging = true; sx = e.touches[0].clientX; sy = e.touches[0].clientY; lx = x; ly = y; }, { passive: true });
    img.addEventListener('touchmove', e => { if (!dragging) return; x = lx + (e.touches[0].clientX - sx); y = ly + (e.touches[0].clientY - sy); clamp(); apply(); }, { passive: true });
    img.addEventListener('touchend', () => { dragging = false; });
    container.addEventListener('wheel', e => { e.preventDefault(); scale = Math.min(3, Math.max(1, scale + (e.deltaY < 0 ? 0.15 : -0.15))); if (scale === 1) { x = 0; y = 0; } clamp(); apply(); }, { passive: false });
    container.querySelectorAll('.photo360-controls button').forEach(btn => btn.addEventListener('click', () => {
        const z = btn.dataset.zoom;
        if (z === 'in') scale = Math.min(3, scale + 0.3);
        if (z === 'out') scale = Math.max(1, scale - 0.3);
        if (z === 'reset') { scale = 1; x = 0; y = 0; }
        clamp(); apply();
    }));
    img.addEventListener('error', () => { container.innerHTML = '<p class="has-text-centered p-5">No se pudo cargar la foto. Intenta de nuevo.</p>'; });
    apply();
    appState.scene3d = { dispose: () => { container.innerHTML = ''; container.classList.remove('photo360'); } };
}

function build3DScene(type, container) {
    // Compat: el visor ahora es fotográfico (buildPhotoViewer). Se conserva por si algún hotel/carro no trae imagen.
    const img = container.querySelector?.('.photo360-img')?.src;
    if (!img) container.innerHTML = '<p class="has-text-centered p-5">Vista no disponible.</p>';
}

// ============================
// SOCIOS
// ============================
async function initPartners() {
    const grid = document.getElementById('partners-grid');
    if (!grid) return;
    try {
        const partners = await api.getPartners();
        grid.innerHTML = partners.map(p => `
            <div class="column is-12-tablet is-6-desktop is-4-widescreen">
                <div class="glass-card partner-card">
                    <div class="partner-logo"><img src="${p.logo}" alt="${esc(p.name)}" loading="lazy"></div>
                    <h3>${esc(p.name)}</h3>
                    <span class="tag partner-type">${esc(p.type)}</span>
                    <p class="mt-2" style="opacity: 0.8;">${esc(p.description)}</p>
                    <div class="partner-benefits"><ul>${p.benefits.map(b => `<li><i class="fas fa-check-circle"></i> ${esc(b)}</li>`).join('')}</ul></div>
                    <a href="${p.website}" target="_blank" rel="noopener" class="button is-cta is-fullwidth mt-3"><i class="fas fa-external-link-alt"></i> Visitar</a>
                </div>
            </div>
        `).join('');
    } catch (error) { grid.innerHTML = '<p class="has-text-centered">Error cargando socios</p>'; }
}

// ============================
// EQUIPO
// ============================
function initTeamCarousel() {
    initCarousel('team', teamData, member => `
        <div class="team-member-card">
            <img src="${member.avatar}" data-fallback="${member.fallback || ''}" onerror="if(this.dataset.fallback&&this.src!==this.dataset.fallback)this.src=this.dataset.fallback" alt="${member.name}" class="team-avatar" loading="lazy">
            <h3>${member.name}</h3>
            <span class="tag is-primary" style="background: var(--accent-blue); color: white;">${member.role}</span>
            <p style="opacity: 0.8; margin-top: 0.75rem;">${member.desc}</p>
            <div class="team-social">
                <a href="${member.social?.linkedin || '#'}" target="_blank" rel="noopener" aria-label="LinkedIn de ${member.name}"><i class="fab fa-linkedin"></i></a>
                <a href="${member.social?.github || '#'}" target="_blank" rel="noopener" aria-label="GitHub de ${member.name}"><i class="fab fa-github"></i></a>
            </div>
        </div>
    `, 'team-carousel-track', 'team-carousel-nav');
}

// ============================
// FAVORITOS
// ============================
function toggleFavorite(id, type) {
    const index = appState.favorites.findIndex(f => f.id === id && f.type === type);
    if (index === -1) {
        const item = type === 'viaje' ? appState.trips.find(t => t.id === id) : type === 'car' ? appState.cars.find(c => c.id === id) : appState.hotels.find(h => h.id === id);
        appState.favorites.push({ id, type, name: item?.destination || item?.name || '', price: item?.price || 0 });
        showNotification('Agregado a favoritos ❤️');
    } else {
        appState.favorites.splice(index, 1);
        showNotification('Eliminado de favoritos', 'warning');
    }
    localStorage.setItem('touristchain-favorites', JSON.stringify(appState.favorites));
    renderTripsGrid();
    initRenta();
    initHotels();
    updateFavoritesDisplay();
    updateDashboardStats();
}
function updateFavoritesDisplay() {
    const container = document.getElementById('user-favorites');
    if (!container) return;
    if (appState.favorites.length === 0) {
        container.innerHTML = '<p class="has-text-centered">No tienes favoritos</p><div class="has-text-centered mt-3"><button class="button is-cta" id="fav-explore-btn"><i class="fas fa-search"></i> Descubrir destinos</button></div>';
        document.getElementById('fav-explore-btn')?.addEventListener('click', () => showView('viajes'));
        return;
    }
    container.innerHTML = '<div class="columns is-multiline">' + appState.favorites.map(fav => {
        let item;
        if (fav.type === 'viaje') item = appState.trips.find(t => t.id === fav.id);
        else if (fav.type === 'car') item = appState.cars.find(c => c.id === fav.id);
        else item = appState.hotels.find(h => h.id === fav.id);
        if (!item) return '';
        return `
            <div class="column is-6">
                <div class="glass-card">
                    <h4 class="tc-text-accent">${esc(fav.type === 'viaje' ? item.destination : item.name)}</h4>
                    <p style="opacity: 0.8;">$${item.price.toLocaleString()}</p>
                    <button class="button is-cta is-small mt-2 reserve-fav" data-id="${fav.id}" data-type="${fav.type}">Reservar</button>
                    <button class="button is-danger is-small mt-2 remove-fav" data-id="${fav.id}" data-type="${fav.type}"><i class="fas fa-trash"></i></button>
                </div>
            </div>
        `;
    }).join('') + '</div>';

    container.querySelectorAll('.reserve-fav').forEach(btn => {
        btn.addEventListener('click', () => {
            const item = btn.dataset.type === 'viaje' ? appState.trips.find(t => t.id === parseInt(btn.dataset.id)) : btn.dataset.type === 'car' ? appState.cars.find(c => c.id === parseInt(btn.dataset.id)) : appState.hotels.find(h => h.id === parseInt(btn.dataset.id));
            if (item) addReservation(item, btn.dataset.type);
        });
    });
    container.querySelectorAll('.remove-fav').forEach(btn => btn.addEventListener('click', () => toggleFavorite(parseInt(btn.dataset.id), btn.dataset.type)));
}

// ============================
// RESERVAS / CARRITO
// ============================
async function addReservation(item, type) {
    if (!api.isAuthenticated()) { showNotification('Inicia sesión para reservar', 'warning'); showView('login'); return; }

    const nombre = type === 'viaje' ? item.destination : type === 'car' ? `Renta: ${item.name}` : `Hotel: ${item.name}`;
    const precio = type === 'car' ? item.price * 3 : item.price;

    try {
        const booking = await api.createBooking({
            tripId: type === 'viaje' ? item.id : null,
            itemName: nombre,
            itemType: type === 'viaje' ? 'viaje' : type === 'car' ? 'renta' : 'hotel',
            price: precio,
            quantity: 1
        });
        appState.reservations.push({ id: booking.id, backendId: booking.id, nombre, precio, tipo: type === 'viaje' ? 'viaje' : type === 'car' ? 'renta' : 'hotel', fecha: new Date().toLocaleDateString(), paid: false });
        localStorage.setItem('touristchain-reservations', JSON.stringify(appState.reservations));
        createNotification('Nueva reserva', `Has agregado ${nombre} a tus reservas.`);
        showNotification('¡Agregado al carrito! 🛒');
        updateReservationsDisplay();
        updateDashboardStats();
    } catch (error) { showNotification(error.message || 'Error al reservar', 'error'); }
}
function updateReservationsDisplay() {
    const container = document.getElementById('user-reservations');
    const payBtn = document.getElementById('pay-reservations-btn');
    if (!container) return;
    const pending = appState.reservations.filter(r => !r.paid);
    if (payBtn) payBtn.style.display = pending.length ? 'block' : 'none';
    if (pending.length === 0) { container.innerHTML = '<p class="has-text-centered">No tienes reservas pendientes</p>'; return; }
    container.innerHTML = '<div class="columns is-multiline">' + pending.map((r, i) => `
        <div class="column is-12">
            <div class="glass-card">
                <div class="columns is-vcentered">
                    <div class="column is-8">
                        <h4 class="tc-text-accent">${esc(r.nombre)}</h4>
                        <p style="opacity: 0.8; font-size: 0.9rem;"><i class="fas fa-calendar"></i> ${esc(r.fecha)} · Tipo: ${esc(r.tipo)}</p>
                    </div>
                    <div class="column is-4 has-text-right">
                        <strong style="font-size: 1.4rem; color: var(--warm-accent);">$${r.precio.toLocaleString()}</strong>
                        <button class="button is-small is-danger mt-2 cancel-reservation" data-index="${i}"><i class="fas fa-trash"></i></button>
                    </div>
                </div>
            </div>
        </div>
    `).join('') + '</div>';

    container.querySelectorAll('.cancel-reservation').forEach(btn => {
        btn.addEventListener('click', async () => {
            const index = parseInt(btn.dataset.index);
            const r = pending[index];
            if (r.backendId) { try { await api.deleteBooking(r.backendId); } catch (e) {} }
            appState.reservations = appState.reservations.filter(res => res !== r);
            localStorage.setItem('touristchain-reservations', JSON.stringify(appState.reservations));
            updateReservationsDisplay();
            updateDashboardStats();
            showNotification('Reserva cancelada');
        });
    });
}

// ============================
// DASHBOARD
// ============================
async function initDashboard() {
    if (!api.isAuthenticated()) { showView('login'); return; }
    updateUserHeader();
    updateDashboardStats();
    updateReservationsDisplay();
    updateFavoritesDisplay();
    await loadItineraries();
    await loadBudgets();
    await loadUserReviews();
    await loadNotifications();
    renderPaymentMethods();
    renderHistory();
}
function updateUserHeader() {
    const user = appState.user;
    document.getElementById('user-name-display') && (document.getElementById('user-name-display').textContent = user.name);
    document.getElementById('user-email-display') && (document.getElementById('user-email-display').textContent = user.email);
    document.getElementById('user-avatar-display') && (document.getElementById('user-avatar-display').src = user.avatar);
    document.getElementById('user-points') && (document.getElementById('user-points').textContent = user.loyaltyPoints || 0);
    document.getElementById('settings-name') && (document.getElementById('settings-name').value = user.name || '');
    document.getElementById('settings-phone') && (document.getElementById('settings-phone').value = user.phone || '');
    document.getElementById('settings-bio') && (document.getElementById('settings-bio').value = user.bio || '');
    document.getElementById('settings-location') && (document.getElementById('settings-location').value = user.location || '');
    if (user.preferences) {
        document.getElementById('settings-email-notif') && (document.getElementById('settings-email-notif').checked = user.preferences.email);
        document.getElementById('settings-sms-notif') && (document.getElementById('settings-sms-notif').checked = user.preferences.sms);
        document.getElementById('settings-push-notif') && (document.getElementById('settings-push-notif').checked = user.preferences.push);
    }
}
async function updateDashboardStats() {
    if (!api.isAuthenticated()) return;
    try {
        const [bookings, payments, me] = await Promise.all([api.getBookings().catch(() => []), api.getPayments().catch(() => []), api.getMe().catch(() => appState.user)]);
        appState.user = me;
        localStorage.setItem('touristchain-user', JSON.stringify(me));
        updateUserHeader();
        const totalGastado = payments.filter(p => p.status === 'completado').reduce((s, p) => s + p.amount, 0);
        document.getElementById('stat-reservations') && (document.getElementById('stat-reservations').textContent = bookings.length);
        document.getElementById('stat-favorites') && (document.getElementById('stat-favorites').textContent = appState.favorites.length);
        document.getElementById('stat-total') && (document.getElementById('stat-total').textContent = totalGastado.toLocaleString());
        document.getElementById('stat-trips') && (document.getElementById('stat-trips').textContent = bookings.filter(b => b.itemType === 'viaje').length);
        const recent = document.getElementById('recent-activity');
        if (recent) {
            const latest = bookings.slice(-3).reverse();
            recent.innerHTML = latest.length ? latest.map(b => `
                <div class="glass-card mb-2" style="padding: 0.75rem;"><strong class="tc-text-accent">${esc(b.itemName)}</strong><p style="opacity: 0.8; font-size: 0.85rem;">$${b.total.toLocaleString()} · ${esc(b.status)}</p></div>
            `).join('') : '<p class="has-text-centered">No hay actividad reciente</p>';
        }
    } catch (error) { console.error('Error dashboard stats:', error); }
}

// ============================
// PERFIL
// ============================
async function saveProfile(e) {
    e.preventDefault();
    const profile = {
        name: document.getElementById('settings-name').value,
        phone: document.getElementById('settings-phone').value,
        bio: document.getElementById('settings-bio').value,
        location: document.getElementById('settings-location').value,
        preferences: {
            email: document.getElementById('settings-email-notif').checked,
            sms: document.getElementById('settings-sms-notif').checked,
            push: document.getElementById('settings-push-notif').checked
        }
    };
    try {
        const updated = await api.updateProfile(profile);
        appState.user = updated;
        updateUserHeader();
        showNotification('Perfil actualizado');
    } catch (error) { showNotification(error.message, 'error'); }
}
async function changePassword(e) {
    e.preventDefault();
    const current = document.getElementById('current-password').value;
    const newPass = document.getElementById('new-password').value;
    const confirm = document.getElementById('confirm-new-password').value;
    if (newPass !== confirm) { showNotification('Las contraseñas no coinciden', 'error'); return; }
    try {
        await api.changePassword(current, newPass);
        document.getElementById('password-form').reset();
        showNotification('Contraseña actualizada');
    } catch (error) { showNotification(error.message, 'error'); }
}
function changeAvatar() {
    // Abre el selector de archivo (input oculto en el dashboard)
    document.getElementById('avatar-file-input')?.click();
}
function handleAvatarFile(file) {
    if (!file) return;
    const valid = ['image/jpeg', 'image/png', 'image/webp'];
    if (!valid.includes(file.type)) { showNotification('Solo JPG, PNG o WebP', 'error'); return; }
    if (file.size > 2 * 1024 * 1024) { showNotification('La foto debe pesar menos de 2MB', 'error'); return; }
    const reader = new FileReader();
    reader.onload = () => {
        // Comprimir a 256px antes de guardar en db.json (Base64 liviano)
        const img = new Image();
        img.onload = () => {
            const size = 256;
            const canvas = document.createElement('canvas');
            canvas.width = size; canvas.height = size;
            const ctx = canvas.getContext('2d');
            const min = Math.min(img.width, img.height);
            ctx.drawImage(img, (img.width - min) / 2, (img.height - min) / 2, min, min, 0, 0, size, size);
            const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
            // Preview inmediata
            const display = document.getElementById('user-avatar-display');
            if (display) display.src = dataUrl;
            api.updateProfile({ avatar: dataUrl }).then(updated => { appState.user = updated; updateUserHeader(); showNotification('Foto de perfil actualizada'); }).catch(err => showNotification(err.message, 'error'));
        };
        img.onerror = () => showNotification('No se pudo leer la imagen', 'error');
        img.src = reader.result;
    };
    reader.readAsDataURL(file);
}
function randomAvatar() {
    const seed = Math.floor(Math.random() * 10000);
    const avatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
    api.updateProfile({ avatar }).then(updated => { appState.user = updated; updateUserHeader(); showNotification('Avatar aleatorio generado'); }).catch(err => showNotification(err.message, 'error'));
}

// ============================
// ITINERARIOS
// ============================
async function loadItineraries() {
    const container = document.getElementById('user-itineraries');
    if (!container) return;
    try {
        const items = await api.getItineraries();
        if (items.length === 0) { container.innerHTML = '<p class="has-text-centered">No tienes itinerarios</p>'; return; }
        container.innerHTML = items.map(it => `
            <div class="glass-card mb-3">
                <h4 class="tc-text-accent">${esc(it.name)}</h4>
                <p style="opacity: 0.8;"><i class="fas fa-map-marker-alt"></i> ${esc(it.destination)}</p>
                <ul style="margin-top: 0.75rem; font-size: 0.9rem;">${it.days.map(d => `<li><i class="fas fa-calendar-day" style="color: var(--warm-accent);"></i> ${esc(d)}</li>`).join('')}</ul>
                <button class="button is-small is-danger mt-2 delete-itinerary" data-id="${it.id}"><i class="fas fa-trash"></i></button>
            </div>
        `).join('');
        container.querySelectorAll('.delete-itinerary').forEach(btn => btn.addEventListener('click', async () => { await api.deleteItinerary(parseInt(btn.dataset.id)); loadItineraries(); showNotification('Itinerario eliminado'); }));
    } catch (error) { container.innerHTML = '<p class="has-text-centered">Error cargando itinerarios</p>'; }
}
async function createItinerary(e) {
    e.preventDefault();
    const name = document.getElementById('itinerary-name').value;
    const destination = document.getElementById('itinerary-destination').value;
    const days = document.getElementById('itinerary-days').value.split('\n').filter(l => l.trim());
    try { await api.createItinerary({ name, destination, days }); document.getElementById('itinerary-form').reset(); loadItineraries(); showNotification('Itinerario creado'); }
    catch (error) { showNotification(error.message, 'error'); }
}

// ============================
// PRESUPUESTOS
// ============================
async function loadBudgets() {
    const container = document.getElementById('user-budgets');
    if (!container) return;
    try {
        const items = await api.getBudgets();
        if (items.length === 0) { container.innerHTML = '<p class="has-text-centered">No tienes presupuestos guardados</p>'; return; }
        container.innerHTML = items.map(b => `
            <div class="glass-card mb-3">
                <div class="columns is-vcentered">
                    <div class="column is-8"><h4 class="tc-text-accent">${esc(b.name)}</h4><p style="opacity: 0.8;"><i class="fas fa-map-marker-alt"></i> ${esc(b.destination)} · ${b.travelers} viajeros · ${b.days} días</p></div>
                    <div class="column is-4 has-text-right"><strong style="font-size: 1.3rem; color: var(--warm-accent);">$${b.total.toLocaleString()}</strong><button class="button is-small is-danger mt-2 delete-budget" data-id="${b.id}"><i class="fas fa-trash"></i></button></div>
                </div>
            </div>
        `).join('');
        container.querySelectorAll('.delete-budget').forEach(btn => btn.addEventListener('click', async () => { await api.deleteBudget(parseInt(btn.dataset.id)); loadBudgets(); showNotification('Presupuesto eliminado'); }));
    } catch (error) { container.innerHTML = '<p class="has-text-centered">Error cargando presupuestos</p>'; }
}
async function createBudget(e) {
    e.preventDefault();
    const budget = {
        name: document.getElementById('budget-name').value,
        destination: document.getElementById('budget-destination').value,
        travelers: parseInt(document.getElementById('budget-travelers').value) || 1,
        days: parseInt(document.getElementById('budget-days').value) || 1,
        flights: parseFloat(document.getElementById('budget-flights').value) || 0,
        hotel: parseFloat(document.getElementById('budget-hotel').value) || 0,
        food: parseFloat(document.getElementById('budget-food').value) || 0,
        activities: parseFloat(document.getElementById('budget-activities').value) || 0,
        transport: parseFloat(document.getElementById('budget-transport').value) || 0
    };
    try { await api.createBudget(budget); document.getElementById('budget-form').reset(); updateBudgetTotal(); loadBudgets(); showNotification('Presupuesto guardado'); }
    catch (error) { showNotification(error.message, 'error'); }
}
function updateBudgetTotal() {
    const total = ['flights', 'hotel', 'food', 'activities', 'transport'].reduce((sum, id) => sum + (parseFloat(document.getElementById(`budget-${id}`)?.value) || 0), 0);
    const el = document.getElementById('budget-total-live');
    if (el) el.textContent = total.toLocaleString();
}

// ============================
// RESEÑAS
// ============================
async function loadUserReviews() {
    const container = document.getElementById('user-reviews');
    if (!container) return;
    try {
        const reviews = await api.getReviews();
        const mine = reviews.filter(r => r.userId === appState.user.id);
        if (mine.length === 0) {
            container.innerHTML = '<p class="has-text-centered">No has escrito reseñas</p><div class="has-text-centered mt-3"><button class="button is-cta" id="review-explore-btn"><i class="fas fa-plane"></i> Explorar y reseñar</button></div>';
            document.getElementById('review-explore-btn')?.addEventListener('click', () => showView('viajes'));
            return;
        }
        container.innerHTML = mine.map(r => `
            <div class="glass-card mb-3">
                <div class="tc-rating">${Array(5).fill(0).map((_, i) => `<i class="${i < r.rating ? 'fas' : 'far'} fa-star"></i>`).join('')}</div>
                <h4 class="tc-text-accent mt-2">${esc(r.itemName)}</h4>
                <p>${esc(r.comment)}</p>
                <button class="button is-small is-danger mt-2 delete-review" data-id="${r.id}"><i class="fas fa-trash"></i></button>
            </div>
        `).join('');
        container.querySelectorAll('.delete-review').forEach(btn => btn.addEventListener('click', async () => { await api.deleteReview(parseInt(btn.dataset.id)); loadUserReviews(); showNotification('Reseña eliminada'); }));
    } catch (error) { container.innerHTML = '<p class="has-text-centered">Error cargando reseñas</p>'; }
}
function openReviewModal(type, id, name) {
    if (!api.isAuthenticated()) { showNotification('Inicia sesión para reseñar', 'warning'); showView('login'); return; }
    document.getElementById('review-item-type').value = type;
    document.getElementById('review-item-id').value = id;
    document.getElementById('review-item-name').value = name;
    document.getElementById('review-comment').value = '';
    document.querySelectorAll('#review-rating i').forEach(s => { s.classList.replace('fas', 'far'); s.classList.remove('active'); });
    document.getElementById('review-modal').classList.add('is-active');
}
async function submitReview(e) {
    e.preventDefault();
    const rating = document.querySelectorAll('#review-rating i.fas').length;
    if (rating === 0) { showNotification('Selecciona una calificación', 'error'); return; }
    const review = {
        itemType: document.getElementById('review-item-type').value,
        itemId: parseInt(document.getElementById('review-item-id').value),
        itemName: document.getElementById('review-item-name').value,
        rating,
        comment: document.getElementById('review-comment').value
    };
    try {
        await api.createReview(review);
        document.getElementById('review-modal').classList.remove('is-active');
        loadUserReviews();
        showNotification('Reseña publicada');
    } catch (error) { showNotification(error.message, 'error'); }
}

// ============================
// NOTIFICACIONES
// ============================
async function loadNotifications() {
    if (!api.isAuthenticated()) return;
    try { appState.notifications = await api.getNotifications(); renderNotifications(); } catch (error) {}
}
function createNotification(title, message, type = 'info') {
    if (!api.isAuthenticated()) return;
    api.createNotification({ title, message, type }).then(() => loadNotifications()).catch(() => {});
}
function renderNotifications() {
    const countEl = document.getElementById('notification-count');
    const list = document.getElementById('notification-list');
    const unread = appState.notifications.filter(n => !n.read);
    if (countEl) { countEl.textContent = unread.length; countEl.style.display = unread.length ? 'flex' : 'none'; }
    if (list) {
        if (appState.notifications.length === 0) { list.innerHTML = '<p class="has-text-centered" style="padding: 1rem; opacity: 0.7;">Sin notificaciones</p>'; return; }
        list.innerHTML = appState.notifications.map(n => `
            <div class="tc-notification-item ${n.read ? '' : 'unread'}" data-id="${n.id}">
                <strong style="font-size: 0.9rem;">${esc(n.title)}</strong>
                <p style="font-size: 0.85rem; opacity: 0.7; margin: 0.2rem 0;">${esc(n.message)}</p>
                <small>${new Date(n.createdAt).toLocaleString()}</small>
            </div>
        `).join('');
        list.querySelectorAll('.tc-notification-item').forEach(item => item.addEventListener('click', async () => { await api.markNotificationRead(parseInt(item.dataset.id)); loadNotifications(); }));
    }
}

// ============================
// MÉTODOS DE PAGO
// ============================
function renderPaymentMethods() {
    const container = document.getElementById('payment-methods-list');
    if (!container) return;
    if (appState.paymentMethods.length === 0) { container.innerHTML = '<p class="has-text-centered">No tienes métodos de pago guardados</p>'; return; }
    container.innerHTML = appState.paymentMethods.map((m, i) => `
        <div class="glass-card mb-3">
            <div class="columns is-vcentered">
                <div class="column is-1"><i class="fab fa-cc-${m.brand || 'visa'} fa-2x"></i></div>
                <div class="column is-8"><p>•••• ${m.last4}</p><p class="is-size-7" style="opacity: 0.7;">Expira: ${m.expiry}</p></div>
                <div class="column is-3 has-text-right"><button class="button is-small is-danger delete-payment" data-index="${i}"><i class="fas fa-trash"></i></button></div>
            </div>
        </div>
    `).join('');
    container.querySelectorAll('.delete-payment').forEach(btn => btn.addEventListener('click', () => {
        appState.paymentMethods.splice(parseInt(btn.dataset.index), 1);
        localStorage.setItem('touristchain-payment-methods', JSON.stringify(appState.paymentMethods));
        renderPaymentMethods();
        showNotification('Método eliminado');
    }));
}
function addPaymentMethod(e) {
    e.preventDefault();
    const number = document.getElementById('new-card-number').value.replace(/\s/g, '');
    appState.paymentMethods.push({
        last4: number.slice(-4),
        expiry: document.getElementById('new-card-expiry').value,
        holder: document.getElementById('new-card-holder').value,
        brand: number.startsWith('4') ? 'visa' : 'mastercard'
    });
    localStorage.setItem('touristchain-payment-methods', JSON.stringify(appState.paymentMethods));
    document.getElementById('add-card-form').reset();
    document.getElementById('add-card-modal').classList.remove('is-active');
    renderPaymentMethods();
    showNotification('Tarjeta agregada');
}

// ============================
// HISTORIAL
// ============================
async function renderHistory() {
    const container = document.getElementById('activity-history');
    if (!container) return;
    try {
        const payments = await api.getPayments();
        if (payments.length === 0) { container.innerHTML = '<p class="has-text-centered">No hay historial</p>'; return; }
        container.innerHTML = payments.map(p => `
            <div class="glass-card mb-3">
                <div class="columns is-vcentered">
                    <div class="column is-8"><h4 class="tc-text-accent">${esc(p.description)}</h4><p style="opacity: 0.7; font-size: 0.85rem;"><i class="fas fa-receipt"></i> ${esc(p.reference)} · ${esc(p.date)}</p></div>
                    <div class="column is-4 has-text-right"><strong style="font-size: 1.2rem;">$${p.amount.toLocaleString()}</strong><span class="tag is-small ${p.status === 'completado' ? 'is-success' : 'is-warning'}">${esc(p.status)}</span></div>
                </div>
            </div>
        `).join('');
    } catch (error) { container.innerHTML = '<p class="has-text-centered">Error cargando historial</p>'; }
}

// ============================
// PAGOS
// ============================
let currentPayment = null;
let qrInterval = null;
function initPaymentModal() {
    const modal = document.getElementById('payment-modal');
    if (!modal) return;

    function showStep(step) {
        ['payment-method-step', 'nequi-form-step', 'card-form-step', 'qr-form-step', 'ticket-success-step'].forEach(id => document.getElementById(id)?.classList.add('tc-hidden'));
        document.getElementById(step)?.classList.remove('tc-hidden');
    }
    function reset() { showStep('payment-method-step'); currentPayment = null; if (qrInterval) clearInterval(qrInterval); document.getElementById('nequi-payment-form')?.reset(); document.getElementById('card-payment-form')?.reset(); }
    function close() { modal.classList.remove('is-active'); reset(); }
    function getSummary() {
        const pending = appState.reservations.filter(r => !r.paid);
        return { items: pending, total: pending.reduce((s, r) => s + r.precio, 0) };
    }
    function renderSummary() {
        const { items, total } = getSummary();
        document.getElementById('payment-items-list').innerHTML = items.map(i => `<div class="payment-item"><span>${esc(i.nombre)}</span><strong>$${i.precio.toLocaleString()}</strong></div>`).join('');
        document.getElementById('payment-total-amount').textContent = `$${total.toLocaleString()}`;
    }
    async function markPaid() {
        appState.reservations.filter(r => !r.paid).forEach(r => r.paid = true);
        localStorage.setItem('touristchain-reservations', JSON.stringify(appState.reservations));
        updateReservationsDisplay();
        updateDashboardStats();
    }
    function showTicket(method) {
        const { items, total } = getSummary();
        const code = currentPayment?.reference || 'TC-' + Math.floor(10000 + Math.random() * 90000);
        document.getElementById('ticket-code').textContent = code;
        document.getElementById('ticket-user').textContent = appState.user.name;
        document.getElementById('ticket-destination').textContent = items.length ? items[items.length - 1].nombre : 'TouristChain';
        document.getElementById('ticket-date').textContent = new Date().toLocaleDateString();
        document.getElementById('ticket-total').textContent = `$${total.toLocaleString()}`;
        document.getElementById('ticket-method').textContent = method;
        QRCode.toDataURL(JSON.stringify({ touristchain: true, reference: code, total }), { width: 120, margin: 1 }).then(url => document.getElementById('ticket-qr-image').src = url).catch(() => {});
        showStep('ticket-success-step');
    }

    document.querySelectorAll('.payment-method-btn').forEach(btn => btn.addEventListener('click', () => {
        const method = btn.dataset.method;
        if (method === 'nequi') showStep('nequi-form-step');
        if (method === 'card') showStep('card-form-step');
        if (method === 'qr') startQr();
    }));
    document.querySelectorAll('.payment-back-btn').forEach(btn => btn.addEventListener('click', () => { if (qrInterval) clearInterval(qrInterval); showStep('payment-method-step'); }));

    document.getElementById('nequi-payment-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const { total } = getSummary();
        if (total <= 0) return showNotification('No tienes reservas pendientes', 'warning');
        const phone = document.getElementById('nequi-phone').value.replace(/\D/g, '');
        if (!/^3\d{9}$/.test(phone)) return showNotification('Ingresa un número Nequi válido (10 dígitos, empieza por 3)', 'error');
        const btn = document.getElementById('nequi-pay-btn');
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enviando a tu Nequi...';
        try {
            // Simulación: espera 2s como si aprobaras en el celular
            await new Promise(r => setTimeout(r, 2000));
            currentPayment = await api.createPayment({ amount: total, method: 'nequi', description: `Pago Nequi ${phone}`, reservationIds: appState.reservations.filter(r => !r.paid).map(r => r.backendId).filter(Boolean) });
            await markPaid();
            createNotification('Pago exitoso', `Pago Nequi por $${total.toLocaleString()} aprobado.`);
            showTicket('Nequi');
            showNotification('¡Pago Nequi aprobado!');
        } catch (err) { showNotification(err.message, 'error'); }
        finally { btn.disabled = false; btn.innerHTML = 'Enviar y pagar con Nequi'; }
    });

    document.getElementById('card-payment-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const { total } = getSummary();
        if (total <= 0) return showNotification('No tienes reservas pendientes', 'warning');
        const num = document.getElementById('card-number').value.replace(/\D/g, '');
        const exp = document.getElementById('card-expiry').value;
        const cvc = document.getElementById('card-cvc').value;
        if (num.length < 15) return showNotification('Número de tarjeta inválido', 'error');
        if (!/^\d{2}\/\d{2}$/.test(exp)) return showNotification('Vencimiento inválido (MM/AA)', 'error');
        if (cvc.length < 3) return showNotification('CVC inválido', 'error');
        try {
            currentPayment = await api.createPayment({ amount: total, method: 'tarjeta', description: `Pago con tarjeta ****${num.slice(-4)}`, reservationIds: appState.reservations.filter(r => !r.paid).map(r => r.backendId).filter(Boolean) });
            await markPaid();
            createNotification('Pago exitoso', `Pago con tarjeta por $${total.toLocaleString()} aprobado.`);
            showTicket('Tarjeta');
            showNotification('¡Pago con tarjeta aprobado!');
        } catch (err) { showNotification(err.message, 'error'); }
    });

    async function startQr() {
        const { total } = getSummary();
        if (total <= 0) { showNotification('No tienes reservas pendientes', 'warning'); showStep('payment-method-step'); return; }
        showStep('qr-form-step');
        try {
            const result = await api.createQrPayment({ amount: total, description: 'Pago QR', reservationIds: appState.reservations.filter(r => !r.paid).map(r => r.backendId).filter(Boolean) });
            currentPayment = result.payment;
            document.getElementById('qr-payment-image').src = result.qrImage;
            document.getElementById('qr-payment-reference').textContent = result.payment.reference;
            document.getElementById('qr-status-message').textContent = 'Esperando pago...';
            qrInterval = setInterval(async () => {
                const status = await api.checkPaymentStatus(result.payment.id);
                if (status.status === 'completado') {
                    clearInterval(qrInterval);
                    await markPaid();
                    createNotification('Pago QR confirmado', `Pago por $${total.toLocaleString()} confirmado.`);
                    showTicket('QR');
                    showNotification('¡Pago QR confirmado!');
                }
            }, 3000);
        } catch (err) { showNotification(err.message, 'error'); showStep('payment-method-step'); }
    }

    document.getElementById('simulate-qr-scan')?.addEventListener('click', async () => {
        if (!currentPayment?.qrToken) return;
        try { await fetch(`${api.baseUrl}/payments/qr/${currentPayment.qrToken}/confirm`); } catch (err) {}
    });
    document.getElementById('close-payment-modal')?.addEventListener('click', close);
    document.getElementById('close-ticket-btn')?.addEventListener('click', close);
    modal.addEventListener('click', e => { if (e.target === modal || e.target.classList.contains('modal-background')) close(); });

    window.openPaymentModal = () => {
        if (!api.isAuthenticated()) { showNotification('Inicia sesión para pagar', 'warning'); showView('login'); return; }
        const { total } = getSummary();
        if (total <= 0) { showNotification('No tienes reservas pendientes', 'warning'); return; }
        renderSummary();
        reset();
        modal.classList.add('is-active');
    };
}

// ============================
// CHATBOT
// ============================
function initChatbot() {
    const chatbot = document.getElementById('ai-chatbot');
    const fab = document.getElementById('ai-chatbot-fab');
    const toggleBtn = document.getElementById('ai-chatbot-toggle');
    const form = document.getElementById('ai-chatbot-form');
    const input = document.getElementById('ai-chatbot-input');
    const messages = document.getElementById('ai-chatbot-messages');
    const suggestions = document.getElementById('ai-chatbot-suggestions');
    if (!chatbot || !fab) return;
    let open = false;
    function toggleChat() { open = !open; chatbot.classList.toggle('open', open); if (open) setTimeout(() => input.focus(), 100); }
    fab.addEventListener('click', toggleChat);
    toggleBtn.addEventListener('click', toggleChat);
    function addMsg(text, sender) {
        const div = document.createElement('div');
        div.className = `ai-message ${sender}`;
        div.innerHTML = `<div class="ai-message-bubble">${escapeHtml(text).replace(/\n/g, '<br>')}</div>`;
        messages.appendChild(div);
        messages.scrollTop = messages.scrollHeight;
    }
    async function send(text) {
        if (!text.trim()) return;
        addMsg(text, 'user');
        input.value = '';
        const typing = document.createElement('div');
        typing.className = 'ai-message bot typing';
        typing.innerHTML = `<div class="ai-message-bubble"><span class="dot"></span><span class="dot"></span><span class="dot"></span></div>`;
        messages.appendChild(typing);
        messages.scrollTop = messages.scrollHeight;
        try {
            const res = await api.sendChatMessage(text, { reservations: appState.reservations.filter(r => !r.paid) });
            typing.remove();
            addMsg(res.reply, 'bot');
        } catch (err) { typing.remove(); addMsg('Lo siento, no pude procesar tu mensaje.', 'bot'); }
    }
    form.addEventListener('submit', e => { e.preventDefault(); send(input.value); });
    api.getChatSuggestions().then(data => {
        suggestions.innerHTML = (data.suggestions || []).map(s => `<button type="button" class="ai-suggestion">${s}</button>`).join('');
        suggestions.querySelectorAll('.ai-suggestion').forEach(btn => btn.addEventListener('click', () => send(btn.textContent)));
    }).catch(() => {});
    addMsg('¡Hola! Soy tu asistente TouristChain. Puedo ayudarte con destinos, hoteles, carros 3D, reservas, pagos y más. ¿En qué puedo ayudarte?', 'bot');
}
function escapeHtml(text) { const div = document.createElement('div'); div.textContent = text; return div.innerHTML; }

// ============================
// FORMULARIOS
// ============================
function initForms() {
    document.getElementById('login-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const data = await api.login(document.getElementById('login-email').value.trim(), document.getElementById('login-password').value);
            appState.user = data.user;
            updateAuthUI();
            e.target.reset();
            // Sin botón de Admin visible: el rol decide a dónde ir
            if (data.user.role === 'admin') { showNotification(`Bienvenido ${data.user.name} (Admin)`); setTimeout(() => window.location.href = 'admin.html', 800); }
            else if (data.user.role === 'socio') { showNotification(`Bienvenido ${data.user.name} (Socio)`); setTimeout(() => window.location.href = 'socio.html', 800); }
            else { showNotification(`¡Bienvenido ${data.user.name}!`); showView('dashboard'); }
        } catch (err) { showNotification(err.message, 'error'); }
    });

    document.getElementById('register-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!document.getElementById('register-terms').checked) return showNotification('Acepta los términos', 'error');
        if (document.getElementById('register-password').value !== document.getElementById('register-confirm').value) return showNotification('Las contraseñas no coinciden', 'error');
        try {
            const data = await api.register(document.getElementById('register-name').value.trim(), document.getElementById('register-email').value.trim(), document.getElementById('register-password').value, document.getElementById('register-phone').value.trim());
            appState.user = data.user;
            updateAuthUI();
            e.target.reset();
            showNotification('¡Cuenta creada! Bienvenido');
            showView('dashboard');
        } catch (err) { showNotification(err.message, 'error'); }
    });

    document.getElementById('forgot-password-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('forgot-email').value.trim();
        if (!email) return showNotification('Ingresa tu correo', 'error');
        try {
            const res = await api.forgotPassword(email);
            showNotification(res.message || 'Código enviado');
            if (res.resetCode) {
                document.getElementById('forgot-demo-code-value').textContent = res.resetCode;
                document.getElementById('forgot-demo-code')?.classList.remove('tc-hidden');
                document.getElementById('reset-password-form')?.classList.remove('tc-hidden');
            }
        } catch (err) { showNotification(err.message, 'error'); }
    });

    document.getElementById('reset-password-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('forgot-email').value.trim();
        const code = document.getElementById('reset-code').value.trim();
        const newPassword = document.getElementById('reset-password').value;
        try {
            const res = await api.resetPassword(email, code, newPassword);
            showNotification(res.message || 'Contraseña restablecida');
            e.target.reset();
            document.getElementById('forgot-password-form')?.reset();
            document.getElementById('forgot-demo-code')?.classList.add('tc-hidden');
            document.getElementById('reset-password-form')?.classList.add('tc-hidden');
            showView('login');
        } catch (err) { showNotification(err.message, 'error'); }
    });

    document.getElementById('contact-form')?.addEventListener('submit', (e) => {
        e.preventDefault();
        const fields = ['contact-name', 'contact-email', 'contact-subject', 'contact-message'];
        let valid = true;
        fields.forEach(id => {
            const el = document.getElementById(id);
            const error = document.getElementById(id.replace('contact-', '') + '-error');
            if (!el.value.trim()) { el.classList.add('error'); error.style.display = 'block'; valid = false; }
            else { el.classList.remove('error'); error.style.display = 'none'; }
        });
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(document.getElementById('contact-email').value)) { document.getElementById('contact-email').classList.add('error'); document.getElementById('email-error').style.display = 'block'; valid = false; }
        if (valid) { showNotification('¡Mensaje enviado!'); e.target.reset(); }
    });

    document.getElementById('settings-form')?.addEventListener('submit', saveProfile);
    document.getElementById('password-form')?.addEventListener('submit', changePassword);
    document.getElementById('itinerary-form')?.addEventListener('submit', createItinerary);
    document.getElementById('budget-form')?.addEventListener('submit', createBudget);
    document.querySelectorAll('.budget-input').forEach(input => input.addEventListener('input', updateBudgetTotal));
    document.getElementById('review-form')?.addEventListener('submit', submitReview);
    document.getElementById('add-card-form')?.addEventListener('submit', addPaymentMethod);

    document.getElementById('card-number')?.addEventListener('input', formatCard);
    document.getElementById('card-expiry')?.addEventListener('input', formatExpiry);
    document.getElementById('card-cvc')?.addEventListener('input', formatCvc);
    document.getElementById('new-card-number')?.addEventListener('input', formatCard);
    document.getElementById('new-card-expiry')?.addEventListener('input', formatExpiry);
    document.getElementById('new-card-cvc')?.addEventListener('input', formatCvc);

    document.querySelectorAll('.toggle-password').forEach(btn => {
        btn.addEventListener('click', () => {
            const input = document.getElementById(btn.dataset.target);
            input.type = input.type === 'password' ? 'text' : 'password';
            btn.querySelector('i').classList.toggle('fa-eye');
            btn.querySelector('i').classList.toggle('fa-eye-slash');
        });
    });

    document.querySelectorAll('#review-rating i').forEach(star => {
        star.addEventListener('click', () => {
            const val = parseInt(star.dataset.value);
            document.querySelectorAll('#review-rating i').forEach((s, i) => {
                s.classList.toggle('fas', i < val);
                s.classList.toggle('far', i >= val);
                s.classList.toggle('active', i < val);
            });
        });
    });

    document.getElementById('close-review-modal')?.addEventListener('click', () => document.getElementById('review-modal').classList.remove('is-active'));
    document.getElementById('close-add-card-modal')?.addEventListener('click', () => document.getElementById('add-card-modal').classList.remove('is-active'));
    document.getElementById('close-viewer-3d')?.addEventListener('click', () => {
        document.getElementById('viewer-3d-modal').classList.remove('is-active');
        if (appState.scene3d) { appState.scene3d.dispose(); appState.scene3d = null; }
    });
    document.getElementById('add-payment-method-btn')?.addEventListener('click', () => document.getElementById('add-card-modal').classList.add('is-active'));
}
function formatCard(e) { e.target.value = e.target.value.replace(/\D/g, '').replace(/(.{4})/g, '$1 ').trim(); }
function formatExpiry(e) { e.target.value = e.target.value.replace(/\D/g, '').replace(/^(\d{2})(\d)/, '$1/$2').substring(0, 5); }
function formatCvc(e) { e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4); }

// ============================
// PAQUETES
// ============================
function initPackages() {
    document.querySelectorAll('.button[data-package]').forEach(btn => {
        btn.addEventListener('click', async () => {
            if (!api.isAuthenticated()) { showNotification('Inicia sesión', 'warning'); showView('login'); return; }
            const pkg = btn.dataset.package;
            const packages = { basico: 799, medio: 1299, black: 2499 };
            const names = { basico: 'Paquete Básico', medio: 'Paquete Medio', black: 'Paquete Black' };
            try {
                const booking = await api.createBooking({ itemName: names[pkg], itemType: 'paquete', price: packages[pkg], quantity: 1 });
                appState.reservations.push({ id: booking.id, backendId: booking.id, nombre: names[pkg], precio: packages[pkg], tipo: 'paquete', fecha: new Date().toLocaleDateString(), paid: false });
                localStorage.setItem('touristchain-reservations', JSON.stringify(appState.reservations));
                showNotification(`${names[pkg]} agregado al carrito`);
            } catch (err) { showNotification(err.message, 'error'); }
        });
    });
}

function bindTripButtons() {
    document.querySelectorAll('.favorite-btn[data-type="viaje"]').forEach(btn => btn.addEventListener('click', () => toggleFavorite(parseInt(btn.dataset.id), 'viaje')));
    document.querySelectorAll('.viaje-card .button.is-cta[data-type="viaje"]').forEach(btn => btn.addEventListener('click', () => { const trip = appState.trips.find(t => t.id === parseInt(btn.dataset.id)); if (trip) addReservation(trip, 'viaje'); }));
    document.querySelectorAll('.review-open-btn[data-type="viaje"]').forEach(btn => btn.addEventListener('click', () => openReviewModal('viaje', parseInt(btn.dataset.id), btn.dataset.name)));
}

async function initViajes() {
    if (appState.trips.length === 0) await loadTrips();
    if (appState.hotels.length === 0) await loadHotels();
    const destacados = appState.trips.filter(t => t.status === 'disponible').slice(0, 5);
    initCarousel('viajes', destacados, renderCarouselSlide, 'viajes-carousel-track', 'viajes-carousel-nav');
    renderTripsGrid();
    initMap();
}

// ============================
// INICIALIZACIÓN
// ============================
document.addEventListener('DOMContentLoaded', async () => {
    applyTheme();

    document.getElementById('theme-toggle')?.addEventListener('click', toggleTheme);
    document.getElementById('accept-cookies')?.addEventListener('click', acceptCookies);
    document.getElementById('reject-cookies')?.addEventListener('click', rejectCookies);

    document.querySelectorAll('.tc-nav-link[href^="#"], .dropdown-item[href^="#"], .register-link[href^="#"], footer a[href^="#"]').forEach(link => {
        link.addEventListener('click', (e) => { e.preventDefault(); showView(link.getAttribute('href').substring(1)); document.getElementById('main-nav')?.classList.remove('open'); });
    });
    document.querySelectorAll('.dashboard-menu a').forEach(link => {
        link.addEventListener('click', (e) => { e.preventDefault(); showDashboardSection(link.getAttribute('href').substring(1)); });
    });

    document.getElementById('mobile-menu-toggle')?.addEventListener('click', () => document.getElementById('main-nav').classList.toggle('open'));
    // Dropdown Mi Cuenta también con clic (táctil/móvil), no solo hover
    document.querySelector('.cuenta-dropdown > .cuenta-btn')?.addEventListener('click', (e) => {
        if (window.innerWidth <= 768) { e.preventDefault(); document.querySelector('.cuenta-dropdown')?.classList.toggle('open'); }
    });

    document.getElementById('change-avatar-btn')?.addEventListener('click', changeAvatar);
    document.getElementById('random-avatar-btn')?.addEventListener('click', randomAvatar);
    document.getElementById('avatar-file-input')?.addEventListener('change', (e) => handleAvatarFile(e.target.files[0]));

    document.getElementById('notification-bell')?.addEventListener('click', (e) => { e.stopPropagation(); document.getElementById('notification-panel').classList.toggle('open'); });
    document.getElementById('mark-all-read')?.addEventListener('click', async () => { await api.markAllNotificationsRead(); loadNotifications(); });
    document.addEventListener('click', (e) => {
        const panel = document.getElementById('notification-panel');
        const bell = document.getElementById('notification-bell');
        if (panel && !panel.contains(e.target) && e.target !== bell && !bell.contains(e.target)) panel.classList.remove('open');
    });

    document.getElementById('start-journey-btn')?.addEventListener('click', () => showView('viajes'));
    document.getElementById('calc-budget-hero-btn')?.addEventListener('click', () => {
        if (!api.isAuthenticated()) { showNotification('Inicia sesión para calcular tu presupuesto', 'warning'); showView('login'); return; }
        showView('dashboard');
        showDashboardSection('dashboard-budgets');
    });
    document.getElementById('trip-search-btn')?.addEventListener('click', () => { showView('viajes'); renderTripsGrid(); });
    document.getElementById('hotel-search-btn')?.addEventListener('click', () => { showView('hoteles'); initHotels(); });
    document.getElementById('explore-trips-btn')?.addEventListener('click', () => showView('viajes'));
    document.getElementById('pay-reservations-btn')?.addEventListener('click', () => window.openPaymentModal && window.openPaymentModal());
    document.getElementById('pay-from-payments-btn')?.addEventListener('click', () => window.openPaymentModal && window.openPaymentModal());
    document.getElementById('logout-btn')?.addEventListener('click', logout);
    document.getElementById('cuenta-logout-btn')?.addEventListener('click', (e) => { e.preventDefault(); logout(); });
    document.getElementById('cuenta-mis-reservas')?.addEventListener('click', () => setTimeout(() => showDashboardSection('dashboard-reservations'), 50));
    document.getElementById('nequi-phone')?.addEventListener('input', (e) => { e.target.value = e.target.value.replace(/\D/g, '').substring(0, 10).replace(/(\d{3})(\d)/, '$1 $2').replace(/(\d{3} \d{3})(\d)/, '$1 $2'); });

    document.getElementById('trip-search')?.addEventListener('input', renderTripsGrid);
    document.getElementById('trip-price-filter')?.addEventListener('change', renderTripsGrid);
    document.getElementById('trip-tag-filter')?.addEventListener('change', renderTripsGrid);
    document.getElementById('trip-clear-filters')?.addEventListener('click', () => { document.getElementById('trip-search').value = ''; document.getElementById('trip-price-filter').value = ''; document.getElementById('trip-tag-filter').value = ''; renderTripsGrid(); });

    document.getElementById('hotel-search')?.addEventListener('input', initHotels);
    document.getElementById('hotel-stars-filter')?.addEventListener('change', initHotels);
    document.getElementById('hotel-city-filter')?.addEventListener('change', initHotels);

    document.getElementById('car-category-filter')?.addEventListener('change', initRenta);
    document.getElementById('car-sort')?.addEventListener('change', initRenta);
    document.getElementById('car-clear-filters')?.addEventListener('click', () => { document.getElementById('car-category-filter').value = ''; document.getElementById('car-sort').value = ''; initRenta(); });

    await Promise.all([loadTrips(), loadCars(), loadHotels()]);
    initForms();
    initPaymentModal();
    initChatbot();
    initPackages();
    updateFavoritesDisplay();
    updateReservationsDisplay();

    document.getElementById('hero-destinations').textContent = appState.trips.length;
    document.getElementById('hero-hotels').textContent = appState.hotels.length;
    document.getElementById('hero-cars').textContent = appState.cars.length;

    if (api.isAuthenticated()) {
        try {
            const stored = api.getUser();
            if (stored) appState.user = stored;
            appState.user = await api.getMe();
            localStorage.setItem('touristchain-user', JSON.stringify(appState.user));
        } catch (e) {
            // Token inválido/expirado -> limpiar sin romper la app
            api.logout();
            appState.user = { name: 'Invitado', email: '', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guest', loyaltyPoints: 0 };
        }
        loadNotifications();
    }
    updateAuthUI();

    const initialHash = (window.location.hash || '').substring(1);
    if (initialHash && document.getElementById(initialHash)) showView(initialHash);
    else if (initialHash.startsWith('dashboard-')) showView('dashboard'), showDashboardSection(initialHash);
    else showView(api.isAuthenticated() && initialHash === 'dashboard' ? 'dashboard' : 'inicio');

    console.log('✅ TouristChain renovado cargado');
});
