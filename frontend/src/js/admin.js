// ============================================
        // SISTEMA DE ADMINISTRACIÓN TOURISTCHAIN
        // CRUD COMPLETO Y FUNCIONAL
        // ============================================

        import { api } from './api.js';
        import '../scss/admin.scss';

        // Estado de la aplicación
        const AdminApp = {
            // Estado de autenticación
            isLoggedIn: false,
            currentUser: null,
            currentTheme: localStorage.getItem('adminTheme') || 'default',
            
            // Panel actual
            currentPanel: 'dashboard',
            
            // Datos del sistema
            users: [],
            companies: [],
            workers: [],
            payments: [],
            travels: [],
            activity: [],
            
            // Configuración de paginación
            pagination: {
                users: { page: 1, total: 0, perPage: 10 },
                companies: { page: 1, total: 0, perPage: 10 },
                workers: { page: 1, total: 0, perPage: 10 },
                payments: { page: 1, total: 0, perPage: 10 },
                travels: { page: 1, total: 0, perPage: 10 }
            },
            
            // Estadísticas
            stats: {
                users: { total: 0, active: 0, change: 0 },
                income: { today: 0, monthly: 0, change: 0 },
                trips: { active: 0, completed: 0, change: 0 },
                pending: { total: 0, change: 0 }
            },
            
            // Configuración
            settings: {
                systemName: 'TouristChain',
                contactEmail: 'contacto@touristchain.com',
                sessionTime: 30,
                language: 'es',
                paymentMethods: ['tarjeta', 'paypal', 'transferencia', 'efectivo'],
                notifications: {
                    email: true,
                    push: true,
                    sms: false
                }
            },
            
            // Credenciales de administrador
            credentials: {
                username: 'admin',
                password: 'admin123',
                email: 'admin@touristchain.com',
                name: 'Administrador Principal'
            }
        };

        // ============ INICIALIZACIÓN ============
        document.addEventListener('DOMContentLoaded', async function() {
            await initializeAdminSystem();
        });

        async function initializeAdminSystem() {
            // Cargar datos guardados
            await loadSavedData();

            // Aplicar tema
            applyTheme(AdminApp.currentTheme);

            // Configurar eventos
            setupEventListeners();

            // Inicializar gráficos
            initializeCharts();

            // Verificar sesión
            checkSession();

            console.log('✅ Sistema de Administración TouristChain inicializado');
        }

        async function loadSavedData() {
            try {
                // Intentar cargar datos desde el backend
                const [users, companies, payments, travels] = await Promise.all([
                    api.getUsers().catch(() => []),
                    api.getCompanies().catch(() => []),
                    api.getPayments().catch(() => []),
                    api.getTrips().catch(() => [])
                ]);

                AdminApp.users = users;
                AdminApp.companies = companies;
                AdminApp.payments = payments;
                AdminApp.travels = travels;

                // Los trabajadores y actividad se mantienen en localStorage por ahora
                const savedData = localStorage.getItem('adminData');
                if (savedData) {
                    const data = JSON.parse(savedData);
                    AdminApp.workers = data.workers || [];
                    AdminApp.activity = data.activity || [];
                } else {
                    loadDemoWorkersAndActivity();
                }
            } catch (error) {
                console.error('Error al cargar datos del backend:', error);
                loadDemoData();
            }

            // Cargar configuración
            const savedSettings = localStorage.getItem('adminSettings');
            if (savedSettings) {
                try {
                    AdminApp.settings = JSON.parse(savedSettings);
                } catch (e) {
                    console.error('Error al cargar configuración:', e);
                }
            }
        }

        function loadDemoWorkersAndActivity() {
            AdminApp.workers = [
                {
                    id: 1,
                    name: 'Pedro Sánchez',
                    companyId: 1,
                    company: 'Viajes del Sol S.A.',
                    position: 'Guía Turístico',
                    salary: 2500,
                    contractDate: '2024-01-15',
                    status: 'activo',
                    email: 'pedro@viajessol.com',
                    phone: '+34 600 123 456'
                },
                {
                    id: 2,
                    name: 'Isabel Ruiz',
                    companyId: 2,
                    company: 'Aventuras Extremas S.L.',
                    position: 'Coordinadora',
                    salary: 3200,
                    contractDate: '2024-02-01',
                    status: 'vacaciones',
                    email: 'isabel@aventuras.com',
                    phone: '+34 600 654 321'
                }
            ];

            AdminApp.activity = [
                {
                    id: 1,
                    date: new Date().toLocaleString('es-ES'),
                    user: 'Sistema',
                    action: 'Inicialización',
                    details: 'Datos cargados desde backend',
                    ip: '127.0.0.1'
                }
            ];
        }

        function loadDemoData() {
            // Usuarios de demostración
            AdminApp.users = [
                {
                    id: 1,
                    name: 'Juan Pérez',
                    email: 'juan@example.com',
                    password: 'hashed123',
                    role: 'user',
                    status: 'activo',
                    phone: '+34 123 456 789',
                    address: 'Calle Principal 123, Madrid',
                    createdAt: '2024-01-15',
                    lastLogin: '2024-12-01'
                },
                {
                    id: 2,
                    name: 'María García',
                    email: 'maria@example.com',
                    password: 'hashed456',
                    role: 'admin',
                    status: 'activo',
                    phone: '+34 987 654 321',
                    address: 'Av. Libertad 45, Barcelona',
                    createdAt: '2024-02-20',
                    lastLogin: '2024-12-01'
                },
                {
                    id: 3,
                    name: 'Carlos López',
                    email: 'carlos@example.com',
                    password: 'hashed789',
                    role: 'empresa',
                    status: 'pendiente',
                    phone: '+34 555 123 456',
                    address: 'Plaza Mayor 10, Sevilla',
                    createdAt: '2024-03-10',
                    lastLogin: '2024-11-30'
                },
                {
                    id: 4,
                    name: 'Ana Martínez',
                    email: 'ana@example.com',
                    password: 'hashed012',
                    role: 'trabajador',
                    status: 'activo',
                    phone: '+34 666 789 012',
                    address: 'Calle Nueva 8, Valencia',
                    createdAt: '2024-04-05',
                    lastLogin: '2024-11-29'
                }
            ];
            
            // Empresas de demostración
            AdminApp.companies = [
                {
                    id: 1,
                    ruc: '12345678901',
                    name: 'Viajes del Sol S.A.',
                    contact: 'Roberto Gómez',
                    email: 'info@viajessol.com',
                    phone: '+34 900 123 456',
                    address: 'Av. del Turismo 123, Barcelona',
                    status: 'activa',
                    workers: 5,
                    createdAt: '2024-01-10'
                },
                {
                    id: 2,
                    ruc: '98765432109',
                    name: 'Aventuras Extremas S.L.',
                    contact: 'Laura Méndez',
                    email: 'contacto@aventuras.com',
                    phone: '+34 900 654 321',
                    address: 'Calle Aventura 45, Madrid',
                    status: 'activa',
                    workers: 8,
                    createdAt: '2024-02-15'
                },
                {
                    id: 3,
                    ruc: '45678912304',
                    name: 'Turismo Premium S.A.',
                    contact: 'David Ruiz',
                    email: 'info@turismopremium.com',
                    phone: '+34 900 789 123',
                    address: 'Paseo Marítimo 78, Málaga',
                    status: 'pendiente',
                    workers: 3,
                    createdAt: '2024-03-22'
                }
            ];
            
            // Trabajadores de demostración
            AdminApp.workers = [
                {
                    id: 1,
                    name: 'Pedro Sánchez',
                    companyId: 1,
                    company: 'Viajes del Sol S.A.',
                    position: 'Guía Turístico',
                    salary: 2500,
                    contractDate: '2024-01-15',
                    status: 'activo',
                    email: 'pedro@viajessol.com',
                    phone: '+34 600 123 456'
                },
                {
                    id: 2,
                    name: 'Isabel Ruiz',
                    companyId: 2,
                    company: 'Aventuras Extremas S.L.',
                    position: 'Coordinadora',
                    salary: 3200,
                    contractDate: '2024-02-01',
                    status: 'vacaciones',
                    email: 'isabel@aventuras.com',
                    phone: '+34 600 654 321'
                },
                {
                    id: 3,
                    name: 'Miguel Ángel Torres',
                    companyId: 1,
                    company: 'Viajes del Sol S.A.',
                    position: 'Recepcionista',
                    salary: 1800,
                    contractDate: '2024-03-10',
                    status: 'activo',
                    email: 'miguel@viajessol.com',
                    phone: '+34 600 789 012'
                }
            ];
            
            // Pagos de demostración
            AdminApp.payments = [
                {
                    id: 1,
                    userId: 1,
                    userName: 'Juan Pérez',
                    amount: 1200,
                    method: 'tarjeta',
                    date: '2024-12-01',
                    status: 'completado',
                    reference: 'PAY-001',
                    description: 'Reserva París 2024'
                },
                {
                    id: 2,
                    userId: 3,
                    userName: 'Carlos López',
                    amount: 800,
                    method: 'paypal',
                    date: '2024-12-01',
                    status: 'pendiente',
                    reference: 'PAY-002',
                    description: 'Reserva Roma Diciembre'
                },
                {
                    id: 3,
                    userId: 1,
                    userName: 'Juan Pérez',
                    amount: 1500,
                    method: 'transferencia',
                    date: '2024-11-30',
                    status: 'completado',
                    reference: 'PAY-003',
                    description: 'Tour Nueva York'
                },
                {
                    id: 4,
                    userId: 4,
                    userName: 'Ana Martínez',
                    amount: 950,
                    method: 'efectivo',
                    date: '2024-11-29',
                    status: 'rechazado',
                    reference: 'PAY-004',
                    description: 'Excursión Montaña'
                }
            ];
            
            // Viajes de demostración
            AdminApp.travels = [
                {
                    id: 1,
                    destination: 'París, Francia',
                    description: 'Tour por la ciudad del amor con visita a la Torre Eiffel, Louvre y paseo en barco por el Sena',
                    startDate: '2024-12-15',
                    endDate: '2024-12-22',
                    price: 1200,
                    capacity: 20,
                    available: 15,
                    status: 'disponible',
                    image: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34',
                    createdAt: '2024-10-01'
                },
                {
                    id: 2,
                    destination: 'Roma, Italia',
                    description: 'Historia y cultura en la Ciudad Eterna con visitas al Coliseo, Vaticano y Fontana di Trevi',
                    startDate: '2024-12-20',
                    endDate: '2024-12-27',
                    price: 1100,
                    capacity: 15,
                    available: 8,
                    status: 'disponible',
                    image: 'https://images.unsplash.com/photo-1552832230-c0197dd311b5',
                    createdAt: '2024-10-05'
                },
                {
                    id: 3,
                    destination: 'Tokio, Japón',
                    description: 'Aventura tecnológica y cultural en la capital japonesa con visita a Akihabara, Shinjuku y templos tradicionales',
                    startDate: '2025-01-10',
                    endDate: '2025-01-20',
                    price: 2200,
                    capacity: 12,
                    available: 4,
                    status: 'reservado',
                    image: 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf',
                    createdAt: '2024-10-10'
                }
            ];
            
            // Actividad de demostración
            AdminApp.activity = [
                {
                    id: 1,
                    date: '2024-12-01 10:30:00',
                    user: 'Juan Pérez',
                    action: 'Nueva reserva',
                    details: 'París - $1,200',
                    ip: '192.168.1.100'
                },
                {
                    id: 2,
                    date: '2024-12-01 09:15:00',
                    user: 'María García',
                    action: 'Actualización de perfil',
                    details: 'Cambio de contraseña',
                    ip: '192.168.1.101'
                },
                {
                    id: 3,
                    date: '2024-11-30 16:45:00',
                    user: 'Sistema',
                    action: 'Backup automático',
                    details: 'Base de datos completa',
                    ip: '127.0.0.1'
                },
                {
                    id: 4,
                    date: '2024-11-30 14:20:00',
                    user: 'Carlos López',
                    action: 'Registro de empresa',
                    details: 'Viajes del Sol S.A.',
                    ip: '192.168.1.102'
                },
                {
                    id: 5,
                    date: '2024-11-30 11:05:00',
                    user: 'Ana Martínez',
                    action: 'Pago completado',
                    details: 'PAY-001 - $800',
                    ip: '192.168.1.103'
                }
            ];
            
            saveDataToStorage();
        }

        function saveDataToStorage() {
            const data = {
                users: AdminApp.users,
                companies: AdminApp.companies,
                workers: AdminApp.workers,
                payments: AdminApp.payments,
                travels: AdminApp.travels,
                activity: AdminApp.activity,
                settings: AdminApp.settings
            };
            localStorage.setItem('adminData', JSON.stringify(data));
        }

        // ============ SISTEMA DE AUTENTICACIÓN ============
        function checkSession() {
            const savedSession = localStorage.getItem('adminSession');
            if (savedSession) {
                try {
                    const session = JSON.parse(savedSession);
                    if (session.expires > Date.now()) {
                        AdminApp.isLoggedIn = true;
                        AdminApp.currentUser = session.user;
                        showAdminPanel();
                    } else {
                        showLoginScreen();
                    }
                } catch (e) {
                    console.error('Error al cargar sesión:', e);
                    showLoginScreen();
                }
            } else {
                showLoginScreen();
            }
        }

        function showLoginScreen() {
            document.getElementById('login-screen').style.display = 'flex';
            document.getElementById('admin-panel').classList.remove('active');
        }

        function showAdminPanel() {
            document.getElementById('login-screen').style.display = 'none';
            document.getElementById('admin-panel').classList.add('active');
            
            updateUserInfo();
            loadCurrentPanel();
            updateStats();
            
            showNotification('Bienvenido al panel de administración', 'success');
        }

        // Evento de login
        document.getElementById('login-form').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            
            try {
                const data = await api.login(email, password);
                
                if (data.user.role !== 'admin') {
                    showNotification('Acceso denegado. Solo administradores pueden ingresar.', 'error');
                    api.logout();
                    return;
                }
                
                AdminApp.isLoggedIn = true;
                AdminApp.currentUser = {
                    name: data.user.name,
                    email: data.user.email,
                    role: data.user.role
                };
                
                // Crear sesión
                const session = {
                    user: AdminApp.currentUser,
                    expires: Date.now() + (AdminApp.settings.sessionTime * 60 * 1000)
                };
                localStorage.setItem('adminSession', JSON.stringify(session));
                
                showAdminPanel();
            } catch (error) {
                showNotification(error.message || 'Credenciales incorrectas', 'error');
            }
        });

        function logout() {
            if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
                AdminApp.isLoggedIn = false;
                AdminApp.currentUser = null;
                localStorage.removeItem('adminSession');
                showLoginScreen();
                showNotification('Sesión cerrada exitosamente', 'info');
            }
        }

        // ============ SISTEMA DE TEMAS ============
        function setupEventListeners() {
            // Toggle de tema
            document.getElementById('theme-toggle').addEventListener('click', toggleTheme);
            
            // Formularios
            document.getElementById('user-form').addEventListener('submit', saveUser);
            document.getElementById('socio-form').addEventListener('submit', saveSocio);
            document.getElementById('company-form').addEventListener('submit', saveCompany);
            document.getElementById('worker-form').addEventListener('submit', saveWorker);
            document.getElementById('travel-form').addEventListener('submit', saveTravel);
            document.getElementById('general-settings').addEventListener('submit', saveSettings);
            
            // Filtros
            const searchUsers = document.getElementById('search-users');
            if (searchUsers) {
                searchUsers.addEventListener('input', function() {
                    debounce(filterUsers, 300)();
                });
            }
            
            // Teclas de acceso rápido
            document.addEventListener('keydown', function(e) {
                if (e.ctrlKey && e.key === 'k') {
                    e.preventDefault();
                    const search = document.getElementById('search-users');
                    if (search) search.focus();
                }
                if (e.key === 'Escape') {
                    closeAllModals();
                }
            });
        }

        function toggleTheme() {
            const themes = ['default', 'dark', 'cool'];
            const currentIndex = themes.indexOf(AdminApp.currentTheme);
            const nextIndex = (currentIndex + 1) % themes.length;
            
            AdminApp.currentTheme = themes[nextIndex];
            applyTheme(AdminApp.currentTheme);
            localStorage.setItem('adminTheme', AdminApp.currentTheme);
            
            const themeNames = {
                default: 'Clásico',
                dark: 'Oscuro',
                cool: 'Moderno'
            };
            
            showNotification(`Tema cambiado a: ${themeNames[AdminApp.currentTheme]}`, 'info');
        }

        function applyTheme(theme) {
            document.body.className = '';
            if (theme !== 'default') {
                document.body.classList.add(`theme-${theme}`);
            }
            
            // Actualizar toggle
            const toggle = document.getElementById('theme-toggle');
            if (toggle) {
                toggle.classList.toggle('active', theme !== 'default');
            }
        }

        // ============ NAVEGACIÓN ============
        function showPanel(panelId) {
            AdminApp.currentPanel = panelId;
            
            // Ocultar todos los paneles
            document.querySelectorAll('.panel').forEach(panel => {
                panel.classList.remove('active');
            });
            
            // Mostrar panel seleccionado
            const targetPanel = document.getElementById(panelId + '-panel');
            if (targetPanel) {
                targetPanel.classList.add('active');
            }
            
            // Actualizar navegación activa
            document.querySelectorAll('.sidebar-link').forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('onclick') && link.getAttribute('onclick').includes(panelId)) {
                    link.classList.add('active');
                }
            });
            
            document.querySelectorAll('.admin-nav-link').forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('onclick') && link.getAttribute('onclick').includes(panelId)) {
                    link.classList.add('active');
                }
            });
            
            // Cargar datos del panel
            loadPanelData(panelId);
            
            // Scroll al inicio
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }

        function loadPanelData(panelId) {
            switch(panelId) {
                case 'dashboard':
                    loadDashboard();
                    break;
                case 'usuarios':
                    loadUsers();
                    break;
                case 'empresas':
                    loadCompanies();
                    break;
                case 'socios':
                    loadSocios();
                    break;
                case 'trabajadores':
                    loadWorkers();
                    break;
                case 'pagos':
                    loadPayments();
                    break;
                case 'viajes':
                    loadTravels();
                    break;
                case 'reportes':
                    loadReports();
                    break;
                case 'configuracion':
                    loadSettingsPanel();
                    break;
            }
        }

        // ============ DASHBOARD ============
        function loadDashboard() {
            updateStats();
            loadRecentActivity();
            updateCharts();
        }

        function updateStats() {
            // Calcular estadísticas
            const totalUsers = AdminApp.users.length;
            const activeUsers = AdminApp.users.filter(u => u.status === 'activo').length;
            const usersChange = totalUsers > 0 ? ((activeUsers / totalUsers) * 100).toFixed(1) : 0;
            
            const today = new Date().toISOString().split('T')[0];
            const todayIncome = AdminApp.payments
                .filter(p => p.date === today && p.status === 'completado')
                .reduce((sum, p) => sum + p.amount, 0);
            
            const activeTravels = AdminApp.travels.filter(t => t.status === 'disponible' || t.status === 'reservado').length;
            const pendingItems = AdminApp.payments.filter(p => p.status === 'pendiente').length +
                               AdminApp.users.filter(u => u.status === 'pendiente').length;

            const activeSocios = AdminApp.users.filter(u => u.role === 'socio' && u.status === 'activo').length;
            const totalSocios = AdminApp.users.filter(u => u.role === 'socio').length;

            // Actualizar UI
            document.getElementById('stat-users').textContent = totalUsers;
            document.getElementById('users-change').textContent = `+${usersChange}%`;
            document.getElementById('stat-income').textContent = `$${todayIncome.toLocaleString()}`;
            document.getElementById('income-change').textContent = `+${(Math.random() * 15).toFixed(1)}%`;
            document.getElementById('stat-trips').textContent = activeTravels;
            document.getElementById('trips-change').textContent = `+${(Math.random() * 10).toFixed(1)}%`;
            document.getElementById('stat-pending').textContent = pendingItems;
            document.getElementById('pending-change').textContent = `+${(Math.random() * 5).toFixed(1)}%`;
            const sociosEl = document.getElementById('stat-socios');
            if (sociosEl) sociosEl.textContent = activeSocios;
            const sociosChange = document.getElementById('socios-change');
            if (sociosChange) sociosChange.textContent = totalSocios > 0 ? `+${((activeSocios / totalSocios) * 100).toFixed(1)}%` : '+0%';
            
            // Actualizar estado
            AdminApp.stats = {
                users: { total: totalUsers, active: activeUsers, change: parseFloat(usersChange) },
                income: { today: todayIncome, monthly: todayIncome * 30, change: parseFloat((Math.random() * 15).toFixed(1)) },
                trips: { active: activeTravels, completed: AdminApp.travels.filter(t => t.status === 'completado').length, change: parseFloat((Math.random() * 10).toFixed(1)) },
                pending: { total: pendingItems, change: parseFloat((Math.random() * 5).toFixed(1)) }
            };
        }

        function loadRecentActivity() {
            const container = document.getElementById('recent-activity');
            const recentActivity = AdminApp.activity.slice(0, 10);
            
            container.innerHTML = recentActivity.map(item => `
                <tr>
                    <td>${item.date}</td>
                    <td>${item.user}</td>
                    <td>${item.action}</td>
                    <td>${item.details}</td>
                    <td>${item.ip}</td>
                </tr>
            `).join('');
        }

        function refreshActivity() {
            // Simular nueva actividad
            const newActivity = {
                id: AdminApp.activity.length + 1,
                date: new Date().toLocaleString('es-ES'),
                user: AdminApp.currentUser.name,
                action: 'Actualización',
                details: 'Recarga de actividad reciente',
                ip: '127.0.0.1'
            };
            
            AdminApp.activity.unshift(newActivity);
            saveDataToStorage();
            loadRecentActivity();
            showNotification('Actividad actualizada', 'success');
        }

        // ============ GESTIÓN DE USUARIOS (CRUD COMPLETO) ============
        function loadUsers(page = 1) {
            const searchTerm = document.getElementById('search-users')?.value.toLowerCase() || '';
            const roleFilter = document.getElementById('filter-role')?.value || '';
            const statusFilter = document.getElementById('filter-status')?.value || '';
            
            // Filtrar usuarios
            let filteredUsers = AdminApp.users.filter(user => {
                const matchesSearch = user.name.toLowerCase().includes(searchTerm) || 
                                    user.email.toLowerCase().includes(searchTerm);
                const matchesRole = !roleFilter || user.role === roleFilter;
                const matchesStatus = !statusFilter || user.status === statusFilter;
                
                return matchesSearch && matchesRole && matchesStatus;
            });
            
            // Paginación
            const perPage = AdminApp.pagination.users.perPage;
            const startIndex = (page - 1) * perPage;
            const endIndex = startIndex + perPage;
            const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
            const totalPages = Math.ceil(filteredUsers.length / perPage);
            
            AdminApp.pagination.users = { page, total: filteredUsers.length, perPage };
            
            // Renderizar tabla
            const container = document.getElementById('users-table');
            container.innerHTML = paginatedUsers.map(user => `
                <tr>
                    <td>${user.id}</td>
                    <td>
                        <strong>${user.name}</strong><br>
                        <small class="text-muted">${user.email}</small>
                    </td>
                    <td>${user.email}</td>
                    <td><span class="badge badge-${user.role}">${getRoleName(user.role)}</span></td>
                    <td><span class="badge badge-${user.status}">${getStatusName(user.status)}</span></td>
                    <td>${user.createdAt}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editUser(${user.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="deleteUser(${user.id})" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="action-btn view" onclick="viewUser(${user.id})" title="Ver detalles">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
            
            // Renderizar paginación
            renderPagination('users-pagination', page, totalPages, 'loadUsers');
        }

        function filterUsers() {
            loadUsers(1);
        }

        function openUserForm(userId = null) {
            const modal = document.getElementById('user-modal');
            const title = document.getElementById('user-modal-title');
            
            if (userId) {
                // Editar usuario existente
                const user = AdminApp.users.find(u => u.id === userId);
                if (user) {
                    title.textContent = 'Editar Usuario';
                    document.getElementById('user-id').value = user.id;
                    document.getElementById('user-name').value = user.name;
                    document.getElementById('user-email').value = user.email;
                    document.getElementById('user-password').value = '';
                    document.getElementById('user-password').required = false;
                    document.getElementById('user-role').value = user.role;
                    document.getElementById('user-status').value = user.status;
                    document.getElementById('user-phone').value = user.phone || '';
                    document.getElementById('user-address').value = user.address || '';
                }
            } else {
                // Nuevo usuario
                title.textContent = 'Nuevo Usuario';
                document.getElementById('user-form').reset();
                document.getElementById('user-id').value = '';
                document.getElementById('user-password').required = true;
            }
            
            modal.classList.add('active');
        }

        async function saveUser(e) {
            e.preventDefault();

            const userId = parseInt(document.getElementById('user-id').value);
            const userData = {
                name: document.getElementById('user-name').value,
                email: document.getElementById('user-email').value,
                role: document.getElementById('user-role').value,
                status: document.getElementById('user-status').value,
                phone: document.getElementById('user-phone').value,
                address: document.getElementById('user-address').value,
                lastLogin: new Date().toISOString().split('T')[0]
            };

            const password = document.getElementById('user-password').value;
            if (password) {
                userData.password = password;
            }

            try {
                if (userId) {
                    await api.updateUser(userId, userData);
                    showNotification('✅ Usuario actualizado correctamente', 'success');
                } else {
                    if (!userData.password) {
                        userData.password = 'password123';
                    }
                    await api.createUser(userData);
                    showNotification('✅ Usuario creado correctamente', 'success');
                }

                closeModal('user-modal');
                AdminApp.users = await api.getUsers();
                loadUsers();
                updateStats();
            } catch (error) {
                showNotification(error.message || 'Error al guardar usuario', 'error');
            }
        }

        function editUser(userId) {
            openUserForm(userId);
        }

        async function deleteUser(userId) {
            if (confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
                try {
                    await api.deleteUser(userId);
                    AdminApp.users = await api.getUsers();
                    showNotification('✅ Usuario eliminado correctamente', 'success');
                    loadUsers();
                    updateStats();
                } catch (error) {
                    showNotification(error.message || 'Error al eliminar usuario', 'error');
                }
            }
        }

        function viewUser(userId) {
            const user = AdminApp.users.find(u => u.id === userId);
            if (user) {
                alert(`👤 DETALLES DEL USUARIO\n\n` +
                      `ID: ${user.id}\n` +
                      `Nombre: ${user.name}\n` +
                      `Email: ${user.email}\n` +
                      `Rol: ${getRoleName(user.role)}\n` +
                      `Estado: ${getStatusName(user.status)}\n` +
                      `Teléfono: ${user.phone || 'No especificado'}\n` +
                      `Dirección: ${user.address || 'No especificada'}\n` +
                      `Registro: ${user.createdAt}\n` +
                      `Último acceso: ${user.lastLogin}`);
            }
        }

        // ============ GESTIÓN DE SOCIOS (cuentas de empresas aliadas con NIT) ============
        function getSocios() {
            return (AdminApp.users || []).filter(u => u.role === 'socio');
        }

        function loadSocios() {
            const term = document.getElementById('search-socios')?.value.toLowerCase() || '';
            const container = document.getElementById('socios-table');
            if (!container) return;
            const socios = getSocios().filter(s => {
                const p = s.partnerProfile || {};
                return !term ||
                    (s.name || '').toLowerCase().includes(term) ||
                    (s.email || '').toLowerCase().includes(term) ||
                    (p.nit || '').toLowerCase().includes(term) ||
                    (p.companyName || '').toLowerCase().includes(term);
            });
            container.innerHTML = socios.length ? socios.map(s => {
                const p = s.partnerProfile || {};
                return `
                <tr>
                    <td><strong>${p.nit || '-'}</strong></td>
                    <td>
                        <strong>${p.companyName || s.name}</strong><br>
                        <small class="text-muted">${p.type || 'hotel'} · ${p.city || ''}</small>
                    </td>
                    <td>${p.contactName || '-'}</td>
                    <td>${s.email}</td>
                    <td>${p.phone || s.phone || '-'}</td>
                    <td><span class="badge badge-${s.status}">${getStatusName(s.status)}</span></td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editSocio(${s.id})" title="Editar NIT y datos">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn view" onclick="toggleSocioStatus(${s.id})" title="Activar/Desactivar">
                                <i class="fas fa-power-off"></i>
                            </button>
                            <button class="action-btn delete" onclick="deleteSocio(${s.id})" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>`;
            }).join('') : '<tr><td colspan="7" class="has-text-centered">No hay socios registrados. Crea el primero con “Nuevo Socio”.</td></tr>';
        }

        function openSocioForm(socioId = null) {
            const modal = document.getElementById('socio-modal');
            const title = document.getElementById('socio-modal-title');
            document.getElementById('socio-form').reset();
            document.getElementById('socio-id').value = '';
            document.getElementById('socio-password').required = true;
            document.getElementById('socio-email').disabled = false;
            document.getElementById('socio-password-group').style.display = 'block';

            if (socioId) {
                const socio = getSocios().find(s => s.id === socioId);
                if (socio) {
                    const p = socio.partnerProfile || {};
                    title.textContent = 'Editar Socio (NIT y datos)';
                    document.getElementById('socio-id').value = socio.id;
                    document.getElementById('socio-company').value = p.companyName || socio.name;
                    document.getElementById('socio-nit').value = p.nit || '';
                    document.getElementById('socio-contact').value = p.contactName || '';
                    document.getElementById('socio-email').value = socio.email;
                    document.getElementById('socio-email').disabled = true;
                    document.getElementById('socio-password').required = false;
                    document.getElementById('socio-password-group').style.display = 'none';
                    document.getElementById('socio-phone').value = p.phone || socio.phone || '';
                    document.getElementById('socio-address').value = p.address || '';
                    document.getElementById('socio-city').value = p.city || '';
                    document.getElementById('socio-type').value = p.type || 'hotel';
                    document.getElementById('socio-status').value = socio.status;
                }
            } else {
                title.textContent = 'Nuevo Socio';
                document.getElementById('socio-status').value = 'activo';
                document.getElementById('socio-type').value = 'hotel';
            }

            modal.classList.add('active');
        }

        function editSocio(socioId) {
            openSocioForm(socioId);
        }

        async function saveSocio(e) {
            e.preventDefault();
            const socioId = document.getElementById('socio-id').value
                ? parseInt(document.getElementById('socio-id').value)
                : null;
            try {
                if (socioId) {
                    await api.updatePartnerAccount(socioId, {
                        companyName: document.getElementById('socio-company').value.trim(),
                        nit: document.getElementById('socio-nit').value.trim(),
                        contactName: document.getElementById('socio-contact').value.trim(),
                        phone: document.getElementById('socio-phone').value.trim(),
                        address: document.getElementById('socio-address').value.trim(),
                        city: document.getElementById('socio-city').value.trim(),
                        type: document.getElementById('socio-type').value,
                        status: document.getElementById('socio-status').value
                    });
                    showNotification('✅ Socio actualizado correctamente', 'success');
                } else {
                    await api.createPartnerAccount({
                        companyName: document.getElementById('socio-company').value.trim(),
                        nit: document.getElementById('socio-nit').value.trim(),
                        contactName: document.getElementById('socio-contact').value.trim(),
                        email: document.getElementById('socio-email').value.trim(),
                        password: document.getElementById('socio-password').value,
                        phone: document.getElementById('socio-phone').value.trim(),
                        address: document.getElementById('socio-address').value.trim(),
                        city: document.getElementById('socio-city').value.trim(),
                        type: document.getElementById('socio-type').value
                    });
                    showNotification('✅ Socio creado: ya puede ingresar por su panel con su email y contraseña', 'success');
                }
                closeModal('socio-modal');
                AdminApp.users = await api.getUsers().catch(() => AdminApp.users);
                loadSocios();
                updateStats();
            } catch (error) {
                showNotification(error.message || 'Error al guardar socio', 'error');
            }
        }

        async function toggleSocioStatus(socioId) {
            const socio = getSocios().find(s => s.id === socioId);
            if (!socio) return;
            const next = socio.status === 'activo' ? 'inactivo' : 'activo';
            if (!confirm(`¿${next === 'activo' ? 'Activar' : 'Desactivar'} al socio ${socio.name}?`)) return;
            try {
                await api.updatePartnerAccount(socioId, { status: next });
                AdminApp.users = await api.getUsers().catch(() => AdminApp.users);
                loadSocios();
                updateStats();
                showNotification(`✅ Socio ${next === 'activo' ? 'activado' : 'desactivado'}`, 'success');
            } catch (error) {
                showNotification(error.message || 'Error al cambiar estado', 'error');
            }
        }

        async function deleteSocio(socioId) {
            if (!confirm('¿Eliminar este socio? Su usuario de ingreso también se eliminará.')) return;
            try {
                await api.deleteUser(socioId);
                AdminApp.users = await api.getUsers().catch(() => AdminApp.users);
                loadSocios();
                updateStats();
                showNotification('✅ Socio eliminado correctamente', 'success');
            } catch (error) {
                showNotification(error.message || 'Error al eliminar socio', 'error');
            }
        }

        // ============ GESTIÓN DE EMPRESAS (CRUD COMPLETO) ============
        function loadCompanies() {
            const container = document.getElementById('companies-table');
            container.innerHTML = AdminApp.companies.map(company => `
                <tr>
                    <td>${company.ruc}</td>
                    <td>
                        <strong>${company.name}</strong><br>
                        <small class="text-muted">${company.contact}</small>
                    </td>
                    <td>${company.contact}</td>
                    <td>${company.email}</td>
                    <td>${company.phone}</td>
                    <td><span class="badge badge-${company.status}">${getStatusName(company.status)}</span></td>
                    <td>${company.workers}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editCompany(${company.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="deleteCompany(${company.id})" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function openCompanyForm(companyId = null) {
            const modal = document.getElementById('company-modal');
            const title = document.getElementById('company-modal-title');
            
            if (companyId) {
                const company = AdminApp.companies.find(c => c.id === companyId);
                if (company) {
                    title.textContent = 'Editar Empresa';
                    document.getElementById('company-id').value = company.id;
                    document.getElementById('company-ruc').value = company.ruc;
                    document.getElementById('company-name').value = company.name;
                    document.getElementById('company-contact').value = company.contact;
                    document.getElementById('company-email').value = company.email;
                    document.getElementById('company-phone').value = company.phone;
                    document.getElementById('company-address').value = company.address || '';
                    document.getElementById('company-status').value = company.status;
                }
            } else {
                title.textContent = 'Nueva Empresa';
                document.getElementById('company-form').reset();
                document.getElementById('company-id').value = '';
            }
            
            modal.classList.add('active');
        }

        async function saveCompany(e) {
            e.preventDefault();

            const companyId = parseInt(document.getElementById('company-id').value);
            const companyData = {
                ruc: document.getElementById('company-ruc').value,
                name: document.getElementById('company-name').value,
                contact: document.getElementById('company-contact').value,
                email: document.getElementById('company-email').value,
                phone: document.getElementById('company-phone').value,
                address: document.getElementById('company-address').value,
                status: document.getElementById('company-status').value
            };

            try {
                if (companyId) {
                    const existing = AdminApp.companies.find(c => c.id === companyId);
                    if (existing) {
                        companyData.workers = existing.workers;
                    }
                    await api.updateCompany(companyId, companyData);
                    showNotification('✅ Empresa actualizada correctamente', 'success');
                } else {
                    companyData.workers = 0;
                    await api.createCompany(companyData);
                    showNotification('✅ Empresa creada correctamente', 'success');
                }

                closeModal('company-modal');
                AdminApp.companies = await api.getCompanies();
                loadCompanies();
            } catch (error) {
                showNotification(error.message || 'Error al guardar empresa', 'error');
            }
        }

        function editCompany(companyId) {
            openCompanyForm(companyId);
        }

        async function deleteCompany(companyId) {
            if (confirm('¿Estás seguro de que quieres eliminar esta empresa?')) {
                const hasWorkers = AdminApp.workers.some(w => w.companyId === companyId);

                if (hasWorkers) {
                    if (!confirm('Esta empresa tiene trabajadores asociados. ¿Desea eliminarla de todos modos? Los trabajadores quedarán sin empresa asignada.')) {
                        return;
                    }
                }

                try {
                    await api.deleteCompany(companyId);
                    AdminApp.companies = await api.getCompanies();
                    showNotification('✅ Empresa eliminada correctamente', 'success');
                    loadCompanies();
                } catch (error) {
                    showNotification(error.message || 'Error al eliminar empresa', 'error');
                }
            }
        }

        // ============ GESTIÓN DE TRABAJADORES (CRUD COMPLETO) ============
        function loadWorkers() {
            const container = document.getElementById('workers-table');
            container.innerHTML = AdminApp.workers.map(worker => `
                <tr>
                    <td>${worker.id}</td>
                    <td>
                        <strong>${worker.name}</strong><br>
                        <small class="text-muted">${worker.email || ''}</small>
                    </td>
                    <td>${worker.company}</td>
                    <td>${worker.position}</td>
                    <td>$${worker.salary.toLocaleString()}</td>
                    <td>${worker.contractDate}</td>
                    <td><span class="badge badge-${worker.status}">${getStatusName(worker.status)}</span></td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editWorker(${worker.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="deleteWorker(${worker.id})" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function openWorkerForm(workerId = null) {
            const modal = document.getElementById('worker-modal');
            const title = document.getElementById('worker-modal-title');
            const companySelect = document.getElementById('worker-company');
            
            // Cargar empresas en el select
            companySelect.innerHTML = '<option value="">Seleccionar empresa...</option>' +
                AdminApp.companies.map(c => 
                    `<option value="${c.id}">${c.name} (${c.ruc})</option>`
                ).join('');
            
            if (workerId) {
                const worker = AdminApp.workers.find(w => w.id === workerId);
                if (worker) {
                    title.textContent = 'Editar Trabajador';
                    document.getElementById('worker-id').value = worker.id;
                    document.getElementById('worker-name').value = worker.name;
                    document.getElementById('worker-company').value = worker.companyId;
                    document.getElementById('worker-position').value = worker.position;
                    document.getElementById('worker-salary').value = worker.salary;
                    document.getElementById('worker-contract-date').value = worker.contractDate;
                    document.getElementById('worker-status').value = worker.status;
                    document.getElementById('worker-email').value = worker.email || '';
                    document.getElementById('worker-phone').value = worker.phone || '';
                }
            } else {
                title.textContent = 'Nuevo Trabajador';
                document.getElementById('worker-form').reset();
                document.getElementById('worker-id').value = '';
            }
            
            modal.classList.add('active');
        }

        function saveWorker(e) {
            e.preventDefault();
            
            const workerId = parseInt(document.getElementById('worker-id').value);
            const companyId = parseInt(document.getElementById('worker-company').value);
            const company = AdminApp.companies.find(c => c.id === companyId);
            
            const workerData = {
                name: document.getElementById('worker-name').value,
                companyId: companyId,
                company: company ? company.name : '',
                position: document.getElementById('worker-position').value,
                salary: parseFloat(document.getElementById('worker-salary').value),
                contractDate: document.getElementById('worker-contract-date').value,
                status: document.getElementById('worker-status').value,
                email: document.getElementById('worker-email').value,
                phone: document.getElementById('worker-phone').value
            };
            
            if (workerId) {
                const index = AdminApp.workers.findIndex(w => w.id === workerId);
                if (index !== -1) {
                    AdminApp.workers[index] = { ...AdminApp.workers[index], ...workerData };
                    showNotification('✅ Trabajador actualizado correctamente', 'success');
                }
            } else {
                const newId = AdminApp.workers.length > 0 ? Math.max(...AdminApp.workers.map(w => w.id)) + 1 : 1;
                workerData.id = newId;
                AdminApp.workers.push(workerData);
                
                // Actualizar contador de trabajadores en la empresa
                if (company) {
                    const companyIndex = AdminApp.companies.findIndex(c => c.id === companyId);
                    if (companyIndex !== -1) {
                        AdminApp.companies[companyIndex].workers += 1;
                    }
                }
                
                showNotification('✅ Trabajador creado correctamente', 'success');
            }
            
            closeModal('worker-modal');
            saveDataToStorage();
            loadWorkers();
            loadCompanies(); // Actualizar contador en empresas
        }

        function editWorker(workerId) {
            openWorkerForm(workerId);
        }

        function deleteWorker(workerId) {
            if (confirm('¿Estás seguro de que quieres eliminar este trabajador?')) {
                const worker = AdminApp.workers.find(w => w.id === workerId);
                
                AdminApp.workers = AdminApp.workers.filter(w => w.id !== workerId);
                
                // Actualizar contador de trabajadores en la empresa
                if (worker && worker.companyId) {
                    const companyIndex = AdminApp.companies.findIndex(c => c.id === worker.companyId);
                    if (companyIndex !== -1 && AdminApp.companies[companyIndex].workers > 0) {
                        AdminApp.companies[companyIndex].workers -= 1;
                    }
                }
                
                saveDataToStorage();
                showNotification('✅ Trabajador eliminado correctamente', 'success');
                loadWorkers();
                loadCompanies();
            }
        }

        // ============ GESTIÓN DE PAGOS ============
        function loadPayments() {
            const container = document.getElementById('payments-table');
            
            // Calcular estadísticas
            const today = new Date().toISOString().split('T')[0];
            const todayIncome = AdminApp.payments
                .filter(p => p.date === today && p.status === 'completado')
                .reduce((sum, p) => sum + p.amount, 0);
            
            const pendingPayments = AdminApp.payments.filter(p => p.status === 'pendiente').length;
            const failedTransactions = AdminApp.payments.filter(p => p.status === 'rechazado').length;
            
            // Actualizar estadísticas
            document.getElementById('today-income').textContent = todayIncome.toLocaleString();
            document.getElementById('total-transactions').textContent = AdminApp.payments.length;
            document.getElementById('pending-payments').textContent = pendingPayments;
            document.getElementById('failed-transactions').textContent = failedTransactions;
            
            // Renderizar tabla
            container.innerHTML = AdminApp.payments.map(payment => `
                <tr>
                    <td>${payment.id}</td>
                    <td>${payment.userName}</td>
                    <td>$${payment.amount.toLocaleString()}</td>
                    <td><span class="badge">${getPaymentMethodName(payment.method)}</span></td>
                    <td>${payment.date}</td>
                    <td><span class="badge badge-${payment.status}">${getPaymentStatusName(payment.status)}</span></td>
                    <td>${payment.reference}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editPayment('${payment.id}')" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="deletePayment('${payment.id}')" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="action-btn view" onclick="viewPayment('${payment.id}')" title="Ver detalles">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function editPayment(paymentId) {
            const payment = AdminApp.payments.find(p => p.id == paymentId);
            if (payment) {
                const newStatus = prompt('Nuevo estado para el pago:', payment.status);
                if (newStatus && ['pendiente', 'completado', 'rechazado'].includes(newStatus)) {
                    payment.status = newStatus;
                    saveDataToStorage();
                    loadPayments();
                    updateStats();
                    showNotification('✅ Estado del pago actualizado', 'success');
                }
            }
        }

        function deletePayment(paymentId) {
            if (confirm('¿Estás seguro de que quieres eliminar este pago?')) {
                AdminApp.payments = AdminApp.payments.filter(p => p.id != paymentId);
                saveDataToStorage();
                loadPayments();
                updateStats();
                showNotification('✅ Pago eliminado correctamente', 'success');
            }
        }

        function viewPayment(paymentId) {
            const payment = AdminApp.payments.find(p => p.id == paymentId);
            if (payment) {
                alert(`💳 DETALLES DEL PAGO\n\n` +
                      `ID: ${payment.id}\n` +
                      `Usuario: ${payment.userName}\n` +
                      `Monto: $${payment.amount}\n` +
                      `Método: ${getPaymentMethodName(payment.method)}\n` +
                      `Fecha: ${payment.date}\n` +
                      `Estado: ${getPaymentStatusName(payment.status)}\n` +
                      `Referencia: ${payment.reference}\n` +
                      `Descripción: ${payment.description || 'N/A'}`);
            }
        }

        // ============ GESTIÓN DE VIAJES (CRUD COMPLETO) ============
        function loadTravels() {
            const container = document.getElementById('travels-table');
            container.innerHTML = AdminApp.travels.map(travel => `
                <tr>
                    <td>${travel.id}</td>
                    <td>
                        <strong>${travel.destination}</strong><br>
                        <small class="text-muted">${travel.description.substring(0, 50)}...</small>
                    </td>
                    <td>${travel.description.substring(0, 80)}...</td>
                    <td>${travel.startDate} al ${travel.endDate}</td>
                    <td>$${travel.price.toLocaleString()}</td>
                    <td>${travel.capacity}</td>
                    <td>${travel.available}</td>
                    <td><span class="badge badge-${travel.status}">${getTravelStatusName(travel.status)}</span></td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" onclick="editTravel(${travel.id})" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete" onclick="deleteTravel(${travel.id})" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="action-btn view" onclick="viewTravel(${travel.id})" title="Ver detalles">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function openTravelForm(travelId = null) {
            const modal = document.getElementById('travel-modal');
            const title = document.getElementById('travel-modal-title');
            
            if (travelId) {
                const travel = AdminApp.travels.find(t => t.id === travelId);
                if (travel) {
                    title.textContent = 'Editar Viaje';
                    document.getElementById('travel-id').value = travel.id;
                    document.getElementById('travel-destination').value = travel.destination;
                    document.getElementById('travel-description').value = travel.description;
                    document.getElementById('travel-start-date').value = travel.startDate;
                    document.getElementById('travel-end-date').value = travel.endDate;
                    document.getElementById('travel-price').value = travel.price;
                    document.getElementById('travel-capacity').value = travel.capacity;
                    document.getElementById('travel-available').value = travel.available;
                    document.getElementById('travel-status').value = travel.status;
                    document.getElementById('travel-image').value = travel.image || '';
                }
            } else {
                title.textContent = 'Nuevo Viaje';
                document.getElementById('travel-form').reset();
                document.getElementById('travel-id').value = '';
                document.getElementById('travel-start-date').valueAsDate = new Date();
                document.getElementById('travel-end-date').valueAsDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
            }
            
            modal.classList.add('active');
        }

        async function saveTravel(e) {
            e.preventDefault();

            const travelId = parseInt(document.getElementById('travel-id').value);
            const travelData = {
                destination: document.getElementById('travel-destination').value,
                description: document.getElementById('travel-description').value,
                startDate: document.getElementById('travel-start-date').value,
                endDate: document.getElementById('travel-end-date').value,
                price: parseFloat(document.getElementById('travel-price').value),
                capacity: parseInt(document.getElementById('travel-capacity').value),
                available: parseInt(document.getElementById('travel-available').value),
                status: document.getElementById('travel-status').value,
                image: document.getElementById('travel-image').value
            };

            try {
                if (travelId) {
                    await api.updateTrip(travelId, travelData);
                    showNotification('✅ Viaje actualizado correctamente', 'success');
                } else {
                    await api.createTrip(travelData);
                    showNotification('✅ Viaje creado correctamente', 'success');
                }

                closeModal('travel-modal');
                AdminApp.travels = await api.getTrips();
                loadTravels();
                updateStats();
            } catch (error) {
                showNotification(error.message || 'Error al guardar viaje', 'error');
            }
        }

        function editTravel(travelId) {
            openTravelForm(travelId);
        }

        async function deleteTravel(travelId) {
            if (confirm('¿Estás seguro de que quieres eliminar este viaje?')) {
                try {
                    await api.deleteTrip(travelId);
                    AdminApp.travels = await api.getTrips();
                    showNotification('✅ Viaje eliminado correctamente', 'success');
                    loadTravels();
                    updateStats();
                } catch (error) {
                    showNotification(error.message || 'Error al eliminar viaje', 'error');
                }
            }
        }

        function viewTravel(travelId) {
            const travel = AdminApp.travels.find(t => t.id === travelId);
            if (travel) {
                alert(`✈️ DETALLES DEL VIAJE\n\n` +
                      `ID: ${travel.id}\n` +
                      `Destino: ${travel.destination}\n` +
                      `Descripción: ${travel.description}\n` +
                      `Fechas: ${travel.startDate} al ${travel.endDate}\n` +
                      `Precio: $${travel.price}\n` +
                      `Capacidad: ${travel.capacity} personas\n` +
                      `Disponible: ${travel.available} lugares\n` +
                      `Estado: ${getTravelStatusName(travel.status)}\n` +
                      `Creado: ${travel.createdAt}`);
            }
        }

        // ============ REPORTES ============
        function loadReports() {
            // Inicializar fechas por defecto
            const now = new Date();
            const startDate = new Date(now.getFullYear(), now.getMonth() - 1, 1);
            const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
            
            document.getElementById('report-start').valueAsDate = startDate;
            document.getElementById('report-end').valueAsDate = endDate;
            
            // Generar reporte inicial
            generateReportData();
        }

        function generateReportData() {
            const startDate = document.getElementById('report-start').value;
            const endDate = document.getElementById('report-end').value;
            const reportType = document.getElementById('report-type').value;
            
            let reportHTML = '<div class="glass-card"><h3 class="title is-4" style="color: white;">';
            
            switch(reportType) {
                case 'ventas':
                    const ventas = AdminApp.payments.filter(p => 
                        p.date >= startDate && p.date <= endDate && p.status === 'completado'
                    );
                    const totalVentas = ventas.reduce((sum, p) => sum + p.amount, 0);
                    
                    reportHTML += `📊 Reporte de Ventas (${startDate} a ${endDate})</h3>`;
                    reportHTML += `<p class="mt-3"><strong>Total de ventas:</strong> $${totalVentas.toLocaleString()}</p>`;
                    reportHTML += `<p><strong>Número de transacciones:</strong> ${ventas.length}</p>`;
                    
                    if (ventas.length > 0) {
                        reportHTML += `<div class="table-wrapper mt-4"><table class="data-table"><thead>
                            <tr><th>Fecha</th><th>Usuario</th><th>Monto</th><th>Referencia</th></tr></thead><tbody>`;
                        
                        ventas.forEach(p => {
                            reportHTML += `<tr>
                                <td>${p.date}</td>
                                <td>${p.userName}</td>
                                <td>$${p.amount}</td>
                                <td>${p.reference}</td>
                            </tr>`;
                        });
                        
                        reportHTML += '</tbody></table></div>';
                    }
                    break;
                    
                case 'usuarios':
                    const nuevosUsuarios = AdminApp.users.filter(u => 
                        u.createdAt >= startDate && u.createdAt <= endDate
                    );
                    
                    reportHTML += `👥 Nuevos Usuarios (${startDate} a ${endDate})</h3>`;
                    reportHTML += `<p class="mt-3"><strong>Total de nuevos usuarios:</strong> ${nuevosUsuarios.length}</p>`;
                    
                    const usersByRole = {};
                    nuevosUsuarios.forEach(u => {
                        usersByRole[u.role] = (usersByRole[u.role] || 0) + 1;
                    });
                    
                    reportHTML += '<p><strong>Distribución por rol:</strong></p><ul>';
                    for (const [role, count] of Object.entries(usersByRole)) {
                        reportHTML += `<li>${getRoleName(role)}: ${count}</li>`;
                    }
                    reportHTML += '</ul>';
                    break;
                    
                case 'viajes':
                    const viajesVendidos = AdminApp.travels.filter(t => t.status !== 'disponible');
                    
                    reportHTML += `✈️ Viajes más Vendidos</h3>`;
                    reportHTML += `<p class="mt-3"><strong>Total de viajes reservados/completados:</strong> ${viajesVendidos.length}</p>`;
                    
                    viajesVendidos.sort((a, b) => (b.capacity - b.available) - (a.capacity - a.available));
                    
                    if (viajesVendidos.length > 0) {
                        reportHTML += `<div class="table-wrapper mt-4"><table class="data-table"><thead>
                            <tr><th>Destino</th><th>Reservas</th><th>Ocupación</th><th>Estado</th></tr></thead><tbody>`;
                        
                        viajesVendidos.forEach(t => {
                            const reservas = t.capacity - t.available;
                            const ocupacion = ((reservas / t.capacity) * 100).toFixed(1);
                            reportHTML += `<tr>
                                <td>${t.destination}</td>
                                <td>${reservas}/${t.capacity}</td>
                                <td>${ocupacion}%</td>
                                <td><span class="badge badge-${t.status}">${getTravelStatusName(t.status)}</span></td>
                            </tr>`;
                        });
                        
                        reportHTML += '</tbody></table></div>';
                    }
                    break;
                    
                case 'empresas':
                    const empresasActivas = AdminApp.companies.filter(c => c.status === 'activa');
                    
                    reportHTML += `🏢 Empresas Activas</h3>`;
                    reportHTML += `<p class="mt-3"><strong>Total de empresas activas:</strong> ${empresasActivas.length}</p>`;
                    reportHTML += `<p><strong>Trabajadores totales:</strong> ${
                        empresasActivas.reduce((sum, e) => sum + e.workers, 0)
                    }</p>`;
                    
                    if (empresasActivas.length > 0) {
                        reportHTML += `<div class="table-wrapper mt-4"><table class="data-table"><thead>
                            <tr><th>Empresa</th><th>Contacto</th><th>Trabajadores</th><th>Estado</th></tr></thead><tbody>`;
                        
                        empresasActivas.forEach(c => {
                            reportHTML += `<tr>
                                <td>${c.name}</td>
                                <td>${c.contact}</td>
                                <td>${c.workers}</td>
                                <td><span class="badge badge-activo">Activa</span></td>
                            </tr>`;
                        });
                        
                        reportHTML += '</tbody></table></div>';
                    }
                    break;
            }
            
            reportHTML += '<div class="mt-4">';
            reportHTML += '<button class="btn btn-primary" onclick="exportReport()">';
            reportHTML += '<i class="fas fa-download"></i> Exportar Reporte</button>';
            reportHTML += '<button class="btn btn-warning ml-2" onclick="printReport()">';
            reportHTML += '<i class="fas fa-print"></i> Imprimir</button>';
            reportHTML += '</div></div>';
            
            document.getElementById('report-results').innerHTML = reportHTML;
        }

        function exportReport() {
            showNotification('📊 Reporte exportado correctamente', 'success');
        }

        function printReport() {
            window.print();
        }

        // ============ CONFIGURACIÓN ============
        function loadSettingsPanel() {
            // Cargar valores actuales
            document.getElementById('system-name').value = AdminApp.settings.systemName;
            document.getElementById('contact-email').value = AdminApp.settings.contactEmail;
            document.getElementById('session-time').value = AdminApp.settings.sessionTime;
            document.getElementById('system-language').value = AdminApp.settings.language;
            
            // Configurar tabs
            document.querySelectorAll('[data-tab]').forEach(tab => {
                tab.addEventListener('click', function() {
                    const tabId = this.getAttribute('data-tab');
                    
                    document.querySelectorAll('[data-tab]').forEach(t => {
                        t.classList.remove('is-active');
                    });
                    this.classList.add('is-active');
                    
                    document.querySelectorAll('.tab-content').forEach(content => {
                        content.style.display = 'none';
                    });
                    const tabContent = document.getElementById(tabId + '-tab');
                    if (tabContent) {
                        tabContent.style.display = 'block';
                    }
                });
            });
        }

        function saveSettings(e) {
            e.preventDefault();
            
            AdminApp.settings = {
                systemName: document.getElementById('system-name').value,
                contactEmail: document.getElementById('contact-email').value,
                sessionTime: parseInt(document.getElementById('session-time').value),
                language: document.getElementById('system-language').value,
                paymentMethods: AdminApp.settings.paymentMethods,
                notifications: AdminApp.settings.notifications
            };
            
            localStorage.setItem('adminSettings', JSON.stringify(AdminApp.settings));
            saveDataToStorage();
            
            showNotification('✅ Configuración guardada correctamente', 'success');
            
            // Actualizar título si cambió
            document.title = `${AdminApp.settings.systemName} - Panel de Administración`;
        }

        // ============ FUNCIONES UTILITARIAS ============
        function getRoleName(role) {
            const roles = {
                'admin': 'Administrador',
                'user': 'Usuario',
                'socio': 'Socio',
                'empresa': 'Empresa',
                'trabajador': 'Trabajador'
            };
            return roles[role] || role;
        }

        function getStatusName(status) {
            const statuses = {
                'activo': 'Activo',
                'inactivo': 'Inactivo',
                'pendiente': 'Pendiente',
                'bloqueado': 'Bloqueado',
                'activa': 'Activa',
                'inactiva': 'Inactiva',
                'vacaciones': 'Vacaciones',
                'suspendido': 'Suspendido',
                'disponible': 'Disponible',
                'reservado': 'Reservado',
                'completado': 'Completado',
                'cancelado': 'Cancelado'
            };
            return statuses[status] || status;
        }

        function getPaymentMethodName(method) {
            const methods = {
                'tarjeta': 'Tarjeta',
                'card': 'Tarjeta',
                'pse': 'PSE',
                'qr': 'Código QR',
                'paypal': 'PayPal',
                'transferencia': 'Transferencia',
                'efectivo': 'Efectivo'
            };
            return methods[method] || method;
        }

        function getPaymentStatusName(status) {
            const statuses = {
                'pendiente': 'Pendiente',
                'completado': 'Completado',
                'rechazado': 'Rechazado'
            };
            return statuses[status] || status;
        }

        function getTravelStatusName(status) {
            const statuses = {
                'disponible': 'Disponible',
                'reservado': 'Reservado',
                'completado': 'Completado',
                'cancelado': 'Cancelado'
            };
            return statuses[status] || status;
        }

        function updateUserInfo() {
            if (AdminApp.currentUser) {
                document.getElementById('user-display-name').textContent = AdminApp.currentUser.name;
                document.getElementById('user-display-email').textContent = AdminApp.currentUser.email;

                // Crear avatar con iniciales
                const avatar = document.getElementById('user-avatar');
                const initials = AdminApp.currentUser.name.split(' ')
                    .map(n => n[0])
                    .join('')
                    .toUpperCase()
                    .substring(0, 2);

                avatar.innerHTML = `<span style="font-weight: bold;">${initials}</span>`;
            }
        }

        function closeModal(modalId) {
            document.getElementById(modalId).classList.remove('active');
        }

        function closeAllModals() {
            document.querySelectorAll('.modal-overlay').forEach(modal => {
                modal.classList.remove('active');
            });
        }

        function showNotification(message, type = 'info') {
            const container = document.getElementById('notification-container');
            const notification = document.createElement('div');
            notification.className = `notification ${type}`;
            notification.innerHTML = `
                <div class="notification-icon">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : 
                                      type === 'error' ? 'exclamation-circle' : 
                                      type === 'warning' ? 'exclamation-triangle' : 'info-circle'}"></i>
                </div>
                <div class="notification-content">
                    <p>${message}</p>
                </div>
                <button class="notification-close" onclick="this.parentElement.remove()">
                    <i class="fas fa-times"></i>
                </button>
            `;
            
            container.appendChild(notification);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                if (notification.parentElement) {
                    notification.remove();
                }
            }, 5000);
        }

        function debounce(func, wait) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        }

        function renderPagination(containerId, currentPage, totalPages, callback) {
            const container = document.getElementById(containerId);
            if (!container || totalPages <= 1) {
                container.innerHTML = '';
                return;
            }
            
            let html = '';
            
            // Botón anterior
            if (currentPage > 1) {
                html += `<a class="pagination-link" onclick="${callback}(${currentPage - 1})">‹</a>`;
            }
            
            // Páginas
            const maxPages = 5;
            let startPage = Math.max(1, currentPage - Math.floor(maxPages / 2));
            let endPage = Math.min(totalPages, startPage + maxPages - 1);
            
            if (endPage - startPage + 1 < maxPages) {
                startPage = Math.max(1, endPage - maxPages + 1);
            }
            
            for (let i = startPage; i <= endPage; i++) {
                if (i === currentPage) {
                    html += `<a class="pagination-link active">${i}</a>`;
                } else {
                    html += `<a class="pagination-link" onclick="${callback}(${i})">${i}</a>`;
                }
            }
            
            // Botón siguiente
            if (currentPage < totalPages) {
                html += `<a class="pagination-link" onclick="${callback}(${currentPage + 1})">›</a>`;
            }
            
            container.innerHTML = html;
        }

        // ============ GRÁFICOS ============
        let usersChart = null;
        let incomeChart = null;

        function initializeCharts() {
            // Destruir gráficos existentes
            if (usersChart) usersChart.destroy();
            if (incomeChart) incomeChart.destroy();
            
            // Crear gráfico de usuarios
            const usersCtx = document.getElementById('users-chart');
            if (usersCtx) {
                usersChart = new Chart(usersCtx.getContext('2d'), {
                    type: 'line',
                    data: {
                        labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                        datasets: [{
                            label: 'Usuarios Registrados',
                            data: [120, 190, 300, 500, 200, 300, 450, 600, 750, 900, 1100, 1300],
                            borderColor: 'rgb(255, 214, 10)',
                            backgroundColor: 'rgba(255, 214, 10, 0.1)',
                            tension: 0.4,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                labels: {
                                    color: 'white'
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: {
                                    color: 'rgba(255, 255, 255, 0.1)'
                                },
                                ticks: {
                                    color: 'white'
                                }
                            },
                            x: {
                                grid: {
                                    color: 'rgba(255, 255, 255, 0.1)'
                                },
                                ticks: {
                                    color: 'white'
                                }
                            }
                        }
                    }
                });
            }
            
            // Crear gráfico de ingresos
            const incomeCtx = document.getElementById('income-chart');
            if (incomeCtx) {
                incomeChart = new Chart(incomeCtx.getContext('2d'), {
                    type: 'bar',
                    data: {
                        labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                        datasets: [{
                            label: 'Ingresos ($)',
                            data: [5000, 8000, 12000, 15000, 10000, 18000, 22000, 25000, 30000, 35000, 40000, 45000],
                            backgroundColor: 'rgba(74, 111, 255, 0.7)',
                            borderColor: 'rgb(74, 111, 255)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                labels: {
                                    color: 'white'
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: {
                                    color: 'rgba(255, 255, 255, 0.1)'
                                },
                                ticks: {
                                    color: 'white'
                                }
                            },
                            x: {
                                grid: {
                                    color: 'rgba(255, 255, 255, 0.1)'
                                },
                                ticks: {
                                    color: 'white'
                                }
                            }
                        }
                    }
                });
            }
        }

        function updateCharts() {
            if (usersChart && incomeChart) {
                usersChart.update();
                incomeChart.update();
            } else {
                initializeCharts();
            }
        }

        // ============ EXPORTACIÓN DE DATOS ============
        function exportAllData() {
            const data = {
                fecha: new Date().toLocaleString(),
                sistema: AdminApp.settings.systemName,
                usuarios: AdminApp.users,
                empresas: AdminApp.companies,
                trabajadores: AdminApp.workers,
                pagos: AdminApp.payments,
                viajes: AdminApp.travels
            };
            
            const dataStr = JSON.stringify(data, null, 2);
            const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
            
            const exportFileDefaultName = `touristchain-backup-${new Date().toISOString().split('T')[0]}.json`;
            
            const linkElement = document.createElement('a');
            linkElement.setAttribute('href', dataUri);
            linkElement.setAttribute('download', exportFileDefaultName);
            linkElement.click();
            
            showNotification('📁 Todos los datos exportados correctamente', 'success');
        }

        function exportUsers(format) {
            let data, mimeType, extension;
            
            switch(format) {
                case 'excel':
                    // Convertir a formato Excel
                    const ws = XLSX.utils.json_to_sheet(AdminApp.users.map(u => ({
                        ID: u.id,
                        Nombre: u.name,
                        Email: u.email,
                        Rol: getRoleName(u.role),
                        Estado: getStatusName(u.status),
                        Teléfono: u.phone || '',
                        Dirección: u.address || '',
                        Registro: u.createdAt,
                        'Último Login': u.lastLogin || ''
                    })));
                    const wb = XLSX.utils.book_new();
                    XLSX.utils.book_append_sheet(wb, ws, "Usuarios");
                    XLSX.writeFile(wb, `usuarios-touristchain-${new Date().toISOString().split('T')[0]}.xlsx`);
                    break;
                    
                case 'pdf':
                    showNotification('📄 Generando PDF de usuarios...', 'info');
                    // Aquí se implementaría la generación de PDF
                    break;
                    
                case 'csv':
                    const csvContent = "data:text/csv;charset=utf-8," 
                        + "ID,Nombre,Email,Rol,Estado,Teléfono,Dirección,Registro,Último Login\n"
                        + AdminApp.users.map(u => 
                            `${u.id},"${u.name}","${u.email}","${getRoleName(u.role)}","${getStatusName(u.status)}","${u.phone || ''}","${u.address || ''}","${u.createdAt}","${u.lastLogin || ''}"`
                        ).join("\n");
                    
                    const encodedUri = encodeURI(csvContent);
                    const link = document.createElement("a");
                    link.setAttribute("href", encodedUri);
                    link.setAttribute("download", `usuarios-touristchain-${new Date().toISOString().split('T')[0]}.csv`);
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                    break;
            }
            
            showNotification(`✅ Usuarios exportados en formato ${format.toUpperCase()}`, 'success');
        }

        // ============ FUNCIONES ADICIONALES ============
        function loadCurrentPanel() {
            showPanel(AdminApp.currentPanel);
        }

        // Auto-guardado periódico
        setInterval(() => {
            saveDataToStorage();
            console.log('💾 Datos guardados automáticamente');
        }, 30000); // Cada 30 segundos

        // Inicialización completa
        console.log('🚀 Panel de Administración TouristChain cargado exitosamente!');
        console.log('📊 Usuarios:', AdminApp.users.length);
        console.log('🏢 Empresas:', AdminApp.companies.length);
        console.log('👷 Trabajadores:', AdminApp.workers.length);
        console.log('💳 Pagos:', AdminApp.payments.length);
        console.log('✈️ Viajes:', AdminApp.travels.length);

        // Asegurarse de que los charts se inicialicen después de que el DOM esté listo
        setTimeout(() => {
            if (AdminApp.isLoggedIn) {
                initializeCharts();
            }
        }, 1000);

        // Exponer funciones usadas en atributos inline onclick
        Object.assign(window, {
            closeModal,
            deleteCompany,
            deletePayment,
            deleteSocio,
            deleteTravel,
            deleteUser,
            deleteWorker,
            editCompany,
            editPayment,
            editSocio,
            editTravel,
            editUser,
            editWorker,
            exportAllData,
            exportReport,
            exportUsers,
            filterUsers,
            generateReportData,
            loadSocios,
            logout,
            openCompanyForm,
            openSocioForm,
            openTravelForm,
            openUserForm,
            openWorkerForm,
            printReport,
            refreshActivity,
            showPanel,
            toggleSocioStatus,
            viewPayment,
            viewTravel,
            viewUser,
        });
