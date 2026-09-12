// Capa de comunicación con el backend TouristChain
const API_URL = 'http://localhost:3001/api';

function getToken() {
    return localStorage.getItem('touristchain-token');
}

function getHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

async function handleResponse(response) {
    if (!response.ok) {
        const error = await response.json().catch(() => ({ error: 'Error desconocido' }));
        throw new Error(error.error || `Error ${response.status}`);
    }
    return response.json();
}

export const api = {
    baseUrl: API_URL,
    // Auth
    async login(email, password) {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await handleResponse(response);
        if (data.token) {
            localStorage.setItem('touristchain-token', data.token);
            localStorage.setItem('touristchain-user', JSON.stringify(data.user));
        }
        return data;
    },

    async register(name, email, password, phone = '') {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone })
        });
        const data = await handleResponse(response);
        if (data.token) {
            localStorage.setItem('touristchain-token', data.token);
            localStorage.setItem('touristchain-user', JSON.stringify(data.user));
        }
        return data;
    },

    logout() {
        localStorage.removeItem('touristchain-token');
        localStorage.removeItem('touristchain-user');
    },

    getUser() {
        const user = localStorage.getItem('touristchain-user');
        return user ? JSON.parse(user) : null;
    },

    isAuthenticated() {
        return !!getToken();
    },

    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'admin';
    },

    async getMe() {
        const response = await fetch(`${API_URL}/auth/me`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async updateProfile(profile) {
        const response = await fetch(`${API_URL}/auth/profile`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(profile)
        });
        const data = await handleResponse(response);
        if (data) localStorage.setItem('touristchain-user', JSON.stringify(data));
        return data;
    },

    async changePassword(currentPassword, newPassword) {
        const response = await fetch(`${API_URL}/auth/password`, {
            method: 'PATCH',
            headers: getHeaders(),
            body: JSON.stringify({ currentPassword, newPassword })
        });
        return handleResponse(response);
    },

    async forgotPassword(email) {
        const response = await fetch(`${API_URL}/auth/forgot`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });
        return handleResponse(response);
    },

    async resetPassword(email, code, newPassword) {
        const response = await fetch(`${API_URL}/auth/reset`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, code, newPassword })
        });
        return handleResponse(response);
    },

    // Partners (público)
    async getPartners() {
        const response = await fetch(`${API_URL}/partners`);
        return handleResponse(response);
    },

    // Socios: cuenta creada por el admin (NIT + datos básicos)
    async createPartnerAccount(account) {
        const response = await fetch(`${API_URL}/partners/accounts`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(account)
        });
        return handleResponse(response);
    },

    async updatePartnerAccount(id, account) {
        const response = await fetch(`${API_URL}/partners/accounts/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(account)
        });
        return handleResponse(response);
    },

    async getPartnerMe() {
        const response = await fetch(`${API_URL}/partners/me`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async updatePartnerMe(profile) {
        const response = await fetch(`${API_URL}/partners/me`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(profile)
        });
        return handleResponse(response);
    },

    // Trips
    async getTrips(params = {}) {
        const query = new URLSearchParams(params).toString();
        const response = await fetch(`${API_URL}/trips?${query}`);
        return handleResponse(response);
    },

    async getTrip(id) {
        const response = await fetch(`${API_URL}/trips/${id}`);
        return handleResponse(response);
    },

    async createTrip(trip) {
        const response = await fetch(`${API_URL}/trips`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(trip)
        });
        return handleResponse(response);
    },

    async updateTrip(id, trip) {
        const response = await fetch(`${API_URL}/trips/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(trip)
        });
        return handleResponse(response);
    },

    async deleteTrip(id) {
        const response = await fetch(`${API_URL}/trips/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Cars
    async getCars() {
        const response = await fetch(`${API_URL}/cars`);
        return handleResponse(response);
    },

    // Hotels
    async getHotels(params = {}) {
        const query = new URLSearchParams(params).toString();
        const response = await fetch(`${API_URL}/hotels?${query}`);
        return handleResponse(response);
    },

    async getHotel(id) {
        const response = await fetch(`${API_URL}/hotels/${id}`);
        return handleResponse(response);
    },

    // Bookings
    async getBookings() {
        const response = await fetch(`${API_URL}/bookings`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createBooking(booking) {
        const response = await fetch(`${API_URL}/bookings`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(booking)
        });
        return handleResponse(response);
    },

    async updateBooking(id, booking) {
        const response = await fetch(`${API_URL}/bookings/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(booking)
        });
        return handleResponse(response);
    },

    async deleteBooking(id) {
        const response = await fetch(`${API_URL}/bookings/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Payments
    async getPayments() {
        const response = await fetch(`${API_URL}/payments`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createPayment(payment) {
        const response = await fetch(`${API_URL}/payments`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(payment)
        });
        return handleResponse(response);
    },

    async createQrPayment({ amount, description, reservationIds }) {
        const response = await fetch(`${API_URL}/payments/qr`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({ amount, description, reservationIds })
        });
        return handleResponse(response);
    },

    async checkPaymentStatus(paymentId) {
        const response = await fetch(`${API_URL}/payments/${paymentId}/status`, { headers: getHeaders() });
        return handleResponse(response);
    },

    // Reviews
    async getReviews(filters = {}) {
        const query = new URLSearchParams(filters).toString();
        const response = await fetch(`${API_URL}/reviews?${query}`);
        return handleResponse(response);
    },

    async getReviewStats(filters = {}) {
        const query = new URLSearchParams(filters).toString();
        const response = await fetch(`${API_URL}/reviews/stats?${query}`);
        return handleResponse(response);
    },

    async createReview(review) {
        const response = await fetch(`${API_URL}/reviews`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(review)
        });
        return handleResponse(response);
    },

    async deleteReview(id) {
        const response = await fetch(`${API_URL}/reviews/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Itineraries
    async getItineraries() {
        const response = await fetch(`${API_URL}/itineraries`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createItinerary(itinerary) {
        const response = await fetch(`${API_URL}/itineraries`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(itinerary)
        });
        return handleResponse(response);
    },

    async updateItinerary(id, itinerary) {
        const response = await fetch(`${API_URL}/itineraries/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(itinerary)
        });
        return handleResponse(response);
    },

    async deleteItinerary(id) {
        const response = await fetch(`${API_URL}/itineraries/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Budgets
    async getBudgets() {
        const response = await fetch(`${API_URL}/budgets`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createBudget(budget) {
        const response = await fetch(`${API_URL}/budgets`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(budget)
        });
        return handleResponse(response);
    },

    async deleteBudget(id) {
        const response = await fetch(`${API_URL}/budgets/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Notifications
    async getNotifications() {
        const response = await fetch(`${API_URL}/notifications`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createNotification({ title, message, type = 'info' }) {
        const response = await fetch(`${API_URL}/notifications`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({ title, message, type })
        });
        return handleResponse(response);
    },

    async markNotificationRead(id) {
        const response = await fetch(`${API_URL}/notifications/${id}/read`, {
            method: 'PATCH',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    async markAllNotificationsRead() {
        const response = await fetch(`${API_URL}/notifications/read-all`, {
            method: 'PATCH',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    async deleteNotification(id) {
        const response = await fetch(`${API_URL}/notifications/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Chat IA
    async sendChatMessage(message, context = {}) {
        const response = await fetch(`${API_URL}/chat/ask`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message, context })
        });
        return handleResponse(response);
    },

    async getChatSuggestions() {
        const response = await fetch(`${API_URL}/chat/suggestions`);
        return handleResponse(response);
    },

    // Admin: Users
    async getUsers() {
        const response = await fetch(`${API_URL}/users`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createUser(user) {
        const response = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(user)
        });
        return handleResponse(response);
    },

    async updateUser(id, user) {
        const response = await fetch(`${API_URL}/users/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(user)
        });
        return handleResponse(response);
    },

    async deleteUser(id) {
        const response = await fetch(`${API_URL}/users/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    },

    // Admin: Companies
    async getCompanies() {
        const response = await fetch(`${API_URL}/companies`, { headers: getHeaders() });
        return handleResponse(response);
    },

    async createCompany(company) {
        const response = await fetch(`${API_URL}/companies`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(company)
        });
        return handleResponse(response);
    },

    async updateCompany(id, company) {
        const response = await fetch(`${API_URL}/companies/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(company)
        });
        return handleResponse(response);
    },

    async deleteCompany(id) {
        const response = await fetch(`${API_URL}/companies/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        return handleResponse(response);
    }
};
