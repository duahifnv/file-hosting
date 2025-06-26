const apiUrl = 'http://localhost:8080/api';

export const authAPI = {
    login: async (credentials) => {
        const response = await fetch(apiUrl + '/auth', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(credentials)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);

            if (errorData?.violations) {
                const errorMessages = errorData.violations.map(
                    v => `${v.fieldName}: ${v.message}`
                );
                throw new Error(errorMessages.join('\n'));
            }

            if (errorData?.message) {
                throw new Error(errorData.message);
            }

            throw new Error(`Ошибка сервера, повторите попытку позже`);
        }
        return response.json();
    },

    register: async (registerData) => {
        const response = await fetch(apiUrl + '/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(registerData)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);

            if (errorData?.violations) {
                const errorMessages = errorData.violations.map(
                    v => `${v.fieldName}: ${v.message}`
                );
                throw new Error(errorMessages.join('\n'));
            }

            if (errorData?.message) {
                throw new Error(errorData.message);
            }

            throw new Error(`Ошибка сервера, повторите попытку позже`);
        }
        return response.json();
    }
};

export const userAPI = {
    getAllUsers: async ({ page = 0, size = 10 } = {}) => {
        const token = localStorage.getItem('jwtToken');
        const params = new URLSearchParams({ page, size });
        const response = await fetch(`${apiUrl}/users?${params.toString()}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка получения пользователей');
        return response.json();
    },
    getUserByEmail: async (email) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/user?email=${encodeURIComponent(email)}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.status === 404) throw new Error('Пользователь не найден');
        if (!response.ok) throw new Error('Ошибка получения пользователя');
        return response.json();
    },
    getCurrentUser: async () => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/user/me`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.status === 403) throw { status: 403 };
        if (!response.ok) throw new Error('Ошибка получения профиля');
        return response.json();
    },
    updateCurrentUser: async (body) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/user/me`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            if (response.status === 403) throw { status: 403 };
            const errorData = await response.json().catch(() => null);

            if (errorData?.violations) {
                const errorMessages = errorData.violations.map(
                    v => `${v.fieldName}: ${v.message}`
                );
                throw new Error(errorMessages.join('\n'));
            }

            if (errorData?.message) {
                throw new Error(errorData.message);
            }

            throw new Error(`Ошибка сервера, повторите попытку позже`);
        }

        return response;
    }
};

export const profileAPI = {
    getProfile: userAPI.getCurrentUser,
    updateProfile: userAPI.updateCurrentUser
};

export const fileAPI = {
    getAllFileMetas: async ({ page = 0, size = 5, sort = 'createdAt,desc', contentType = '', shared = false } = {}) => {
        const token = localStorage.getItem('jwtToken');
        const params = new URLSearchParams({ page, size, sort });
        if (contentType) params.append('contentType', contentType);
        if (shared) params.append('shared', 'true');
        const response = await fetch(`${apiUrl}/file-metas?${params.toString()}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка получения списка файлов');
        return response.json();
    },
    uploadFile: async (file) => {
        const token = localStorage.getItem('jwtToken');
        const formData = new FormData();
        formData.append('file', file);
        const response = await fetch(`${apiUrl}/files`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` },
            body: formData
        });
        if (!response.ok) throw new Error('Ошибка загрузки файла');

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return null;
    },
    getFileById: async (metaId, shared = false) => {
        const token = localStorage.getItem('jwtToken');
        const params = new URLSearchParams();
        if (shared) params.append('shared', 'true');
        const response = await fetch(`${apiUrl}/files/${metaId}?${params.toString()}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка получения файла');
        return response;
    },
    removeFile: async (metaId) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/files/${metaId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка удаления файла');
    },
    getFileMeta: async (metaId) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/file-metas/${metaId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка получения метаданных файла');
        return response.json();
    },
    createShare: async (fileId, shareData) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/files/sharing/${fileId}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify(shareData)
        });
        if (!response.ok) throw new Error('Ошибка создания ссылки для общего доступа');
        return response.json();
    },
    removeShare: async (fileId) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/files/sharing/${fileId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Ошибка удаления ссылки для общего доступа');
    },
    getSharedFileMeta: async (sharedId) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/file-metas/${sharedId}?shared=true`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.status === 410) {
            throw new Error('Ссылка на файл истекла');
        }
        if (response.status === 404) {
            throw new Error('Файл не найден или у вас нет доступа к нему');
        }
        if (!response.ok) {
            throw new Error('Ошибка получения файла');
        }
        return response.json();
    },
    downloadSharedFile: async (sharedId) => {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${apiUrl}/files/${sharedId}?shared=true`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            throw new Error('Ошибка загрузки файла');
        }
        return response;
    }
};
