// ============================================
// PANEL DEL SOCIO - TOURISTCHAIN
// Empres aliada creada por el admin (NIT + datos)
// ============================================

import { api } from './api.js';
import '../scss/admin.scss';

const SocioApp = {
    user: null,
    showcase: null,
    bookings: [],
    trips: [],
    notifications: []
};

function esc(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('login-form')?.addEventListener('submit', handleLogin);
    document.getElementById('empresa-form')?.addEventListener('submit', saveEmpresa);
    document.getElementById('vitrina-form')?.addEventListener('submit', saveVitrina);
    document.getElementById('password-form')?.addEventListener('submit', changePassword);
    checkSession();
});

function showNotification(message, type = 'success') {
    const container = document.getElementById('notification-container');
    if (!container) return;
    const div = document.createElement('div');
    div.className = `notification-toast ${type} show`;
    div.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i> ${message}`;
    container.appendChild(div);
    setTimeout(() => div.remove(), 3500);
}

function checkSession() {
    if (api.isAuthenticated()) {
        const stored = api.getUser();
        if (stored && stored.role === 'socio') {
            SocioApp.user = stored;
            showPanelApp();
            loadData();
            return;
        }
        if (stored && stored.role === 'admin') {
            showNotification('Eres administrador: usa admin.html', 'error');
        }
        api.logout();
    }
    document.getElementById('login-screen').style.display = 'flex';
    document.getElementById('socio-panel').classList.remove('active');
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    try {
        const data = await api.login(email, password);
        if (data.user.role !== 'socio') {
            api.logout();
            showNotification(data.user.role === 'admin'
                ? 'Eres administrador: ingresa por admin.html'
                : 'Esta cuenta no es de socio. Pide tu acceso al administrador.', 'error');
            return;
        }
        SocioApp.user = data.user;
        e.target.reset();
        showPanelApp();
        await loadData();
        showNotification(`Bienvenido ${data.user.name}`);
    } catch (err) {
        showNotification(err.message || 'Credenciales incorrectas', 'error');
    }
}

function logout() {
    if (!confirm('¿Cerrar sesión?')) return;
    api.logout();
    SocioApp.user = null;
    SocioApp.showcase = null;
    document.getElementById('login-screen').style.display = 'flex';
    document.getElementById('socio-panel').classList.remove('active');
}

function showPanelApp() {
    document.getElementById('login-screen').style.display = 'none';
    document.getElementById('socio-panel').classList.add('active');
    updateUserInfo();
}

function updateUserInfo() {
    const u = SocioApp.user;
    if (!u) return;
    document.getElementById('user-display-name').textContent = u.partnerProfile?.companyName || u.name;
    document.getElementById('user-display-email').textContent = u.email;
    document.getElementById('user-display-nit').textContent = 'NIT ' + (u.partnerProfile?.nit || '-');
    document.getElementById('user-avatar').textContent = (u.partnerProfile?.companyName || u.name || 'S').charAt(0).toUpperCase();
    document.getElementById('resumen-sub').textContent = `Bienvenido, ${u.partnerProfile?.companyName || u.name}`;
}

async function loadData() {
    try {
        const me = await api.getPartnerMe();
        SocioApp.user = me.user;
        SocioApp.showcase = me.showcase;
        localStorage.setItem('touristchain-user', JSON.stringify(me.user));
        updateUserInfo();
        fillForms();
    } catch (err) {
        showNotification('No se pudo cargar tu perfil: ' + err.message, 'error');
    }
    try {
        const [bookings, trips, notifications] = await Promise.all([
            api.getBookings().catch(() => []),
            api.getTrips().catch(() => []),
            api.getNotifications().catch(() => [])
        ]);
        SocioApp.bookings = bookings;
        SocioApp.trips = trips;
        SocioApp.notifications = notifications;
    } catch (err) { /* datos opcionales */ }
    renderResumen();
    renderReservas();
    renderNotificaciones();
}

function renderResumen() {
    const u = SocioApp.user || {};
    const sc = SocioApp.showcase;
    document.getElementById('stat-estado').textContent = u.status === 'activo' ? 'Activa' : (u.status || '-');
    document.getElementById('stat-vitrina').textContent = sc ? 'Sí' : 'No';
    document.getElementById('stat-destinos').textContent = SocioApp.trips.length;
    document.getElementById('stat-reservas').textContent = SocioApp.bookings.length;

    const p = u.partnerProfile || {};
    const rows = [
        ['Empresa', p.companyName || u.name || '-'],
        ['NIT', p.nit || '-'],
        ['Contacto', p.contactName || '-'],
        ['Email', u.email || '-'],
        ['Teléfono', p.phone || u.phone || '-'],
        ['Dirección', p.address || '-'],
        ['Ciudad', p.city || u.location || '-'],
        ['Tipo', p.type || '-']
    ];
    document.getElementById('resumen-empresa').innerHTML = rows
        .map(([k, v]) => `<tr><td><strong>${esc(k)}</strong></td><td>${esc(v)}</td></tr>`).join('');
}

function fillForms() {
    const u = SocioApp.user || {};
    const p = u.partnerProfile || {};
    const sc = SocioApp.showcase || {};
    document.getElementById('emp-nit').value = p.nit || '';
    document.getElementById('emp-company').value = p.companyName || u.name || '';
    document.getElementById('emp-contact').value = p.contactName || '';
    document.getElementById('emp-phone').value = p.phone || u.phone || '';
    document.getElementById('emp-address').value = p.address || '';
    document.getElementById('emp-city').value = p.city || u.location || '';
    document.getElementById('emp-type').value = p.type || 'hotel';
    document.getElementById('vit-descripcion').value = sc.description || u.bio || '';
    document.getElementById('vit-website').value = sc.website && sc.website !== '#' ? sc.website : '';
    document.getElementById('vit-logo').value = sc.logo && !sc.logo.includes('placeholder') ? sc.logo : '';
    document.getElementById('vit-beneficios').value = (sc.benefits || []).join('\n');
}

async function saveEmpresa(e) {
    e.preventDefault();
    const data = {
        companyName: document.getElementById('emp-company').value.trim(),
        contactName: document.getElementById('emp-contact').value.trim(),
        phone: document.getElementById('emp-phone').value.trim(),
        address: document.getElementById('emp-address').value.trim(),
        city: document.getElementById('emp-city').value.trim(),
        type: document.getElementById('emp-type').value
    };
    try {
        const res = await api.updatePartnerMe(data);
        SocioApp.user = res.user;
        SocioApp.showcase = res.showcase;
        localStorage.setItem('touristchain-user', JSON.stringify(res.user));
        updateUserInfo();
        renderResumen();
        showNotification('Datos de la empresa actualizados');
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

async function saveVitrina(e) {
    e.preventDefault();
    const data = {
        description: document.getElementById('vit-descripcion').value.trim(),
        website: document.getElementById('vit-website').value.trim() || '#',
        logo: document.getElementById('vit-logo').value.trim() || undefined,
        benefits: document.getElementById('vit-beneficios').value.split('\n').map(b => b.trim()).filter(Boolean)
    };
    try {
        const res = await api.updatePartnerMe(data);
        SocioApp.showcase = res.showcase;
        renderResumen();
        showNotification('Vitrina publicada correctamente');
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

function renderReservas() {
    const tbody = document.getElementById('reservas-table');
    if (!tbody) return;
    tbody.innerHTML = SocioApp.bookings.length ? SocioApp.bookings.map(b => `
        <tr>
            <td>${b.id}</td>
            <td><strong>${esc(b.itemName || '-')}</strong><br><small class="text-muted">${esc(b.itemType || '')}</small></td>
            <td>$${Number(b.total || 0).toLocaleString()}</td>
            <td><span class="badge badge-${b.status}">${esc(b.status || '-')}</span></td>
        </tr>
    `).join('') : '<tr><td colspan="4" class="has-text-centered">No tienes reservas</td></tr>';
}

function renderNotificaciones() {
    const tbody = document.getElementById('notificaciones-table');
    if (!tbody) return;
    tbody.innerHTML = SocioApp.notifications.length ? SocioApp.notifications.map(n => `
        <tr>
            <td><strong>${esc(n.title)}</strong></td>
            <td>${esc(n.message)}</td>
            <td>${n.createdAt ? new Date(n.createdAt).toLocaleString() : '-'}</td>
            <td><span class="badge badge-${n.read ? 'activo' : 'pendiente'}">${n.read ? 'Leída' : 'Nueva'}</span></td>
        </tr>
    `).join('') : '<tr><td colspan="4" class="has-text-centered">Sin notificaciones</td></tr>';
}

async function markAllRead() {
    try {
        await api.markAllNotificationsRead();
        SocioApp.notifications = await api.getNotifications().catch(() => []);
        renderNotificaciones();
        showNotification('Notificaciones marcadas como leídas');
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

async function changePassword(e) {
    e.preventDefault();
    const current = document.getElementById('current-password').value;
    const next = document.getElementById('new-password').value;
    if (next.length < 8) { showNotification('La nueva contraseña debe tener al menos 8 caracteres', 'error'); return; }
    try {
        await api.changePassword(current, next);
        e.target.reset();
        showNotification('Contraseña actualizada');
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

function showPanel(panelId) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.getElementById(panelId + '-panel')?.classList.add('active');
    document.querySelectorAll('.sidebar-link, .admin-nav-link').forEach(l => {
        l.classList.remove('active');
        if (l.getAttribute('onclick')?.includes(`'${panelId}'`)) l.classList.add('active');
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Funciones globales para los onclick del HTML
window.showPanel = showPanel;
window.logout = logout;
window.markAllRead = markAllRead;
