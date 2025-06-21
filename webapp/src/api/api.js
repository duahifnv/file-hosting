const apiUrl = 'http://localhost:8080/api';

export const authAPI = {
    login: async (credentials) => {
        return await fetch(apiUrl + '/auth', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(credentials)
        });
    },

    register: async (userData) => {
        return await fetch(apiUrl + '/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userData)
        });
    }
};