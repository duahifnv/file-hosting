import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { profileAPI } from '../api/api';

const ProfilePage = () => {
    const [form, setForm] = useState({ username: '', email: '', password: '', firstname: '', lastname: '' });
    const [showPassword, setShowPassword] = useState(false);
    const [editingHeader, setEditingHeader] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleAuthError = (e) => {
        if (!localStorage.getItem('jwtToken') || (e && e.status === 403)) {
            localStorage.removeItem('jwtToken');
            navigate('/auth');
        }
    };

    useEffect(() => {
        const fetchProfile = async () => {
            setLoading(true);
            setError('');
            try {
                const data = await profileAPI.getProfile();
                setForm({
                    username: data.username,
                    email: data.email,
                    password: '',
                    firstname: data.firstname || '',
                    lastname: data.lastname || ''
                });
            } catch (e) {
                handleAuthError(e);
                setError(e.message);
            }
            setLoading(false);
        };
        if (!localStorage.getItem('jwtToken')) {
            navigate('/auth');
            return;
        }
        fetchProfile();
        // eslint-disable-next-line
    }, []);

    const handleSave = async () => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            const updateData = {
                email: form.email,
                firstname: form.firstname,
                lastname: form.lastname
            };

            if (form.password) {
                updateData.password = form.password;
            }

            await profileAPI.updateProfile(updateData);
            setSuccess('Сохранено!');
            setForm(e => ({ ...e, password: '' }));
        } catch (e) {
            handleAuthError(e);
            setError(e.message);
        }
        setLoading(false);
    };

    const renderHeader = () => {
        if (!form.firstname && !form.lastname) {
            return (
                <div className="mb-4 flex flex-col items-center">
                    <button
                        className="px-4 py-2 bg-accent-light dark:bg-accent-dark text-white rounded-lg hover:opacity-90 transition-opacity"
                        onClick={() => setEditingHeader(true)}
                    >
                        Добавить имя
                    </button>
                </div>
            );
        }

        if (editingHeader) {
            return (
                <div className="flex flex-col items-center mb-4">
                    <input
                        type="text"
                        value={form.firstname}
                        onChange={e => setForm({ ...form, firstname: e.target.value })}
                        placeholder="Имя"
                        className="text-3xl font-bold text-accent-light dark:text-accent-dark bg-transparent border-b-2 border-accent-light dark:border-accent-dark focus:outline-none mb-2 text-center"
                    />
                    <input
                        type="text"
                        value={form.lastname}
                        onChange={e => setForm({ ...form, lastname: e.target.value })}
                        placeholder="Фамилия"
                        className="text-2xl font-semibold text-accent-light dark:text-accent-dark bg-transparent border-b-2 border-accent-light dark:border-accent-dark focus:outline-none text-center"
                    />
                    <button className="mt-2 text-sm text-blue-500 hover:underline" onClick={() => setEditingHeader(false)}>Сохранить</button>
                </div>
            );
        }

        return (
            <div className="mb-4 cursor-pointer flex flex-col items-center" onClick={() => setEditingHeader(true)}>
                {form.firstname && <span className="text-3xl font-bold text-accent-light dark:text-accent-dark">{form.firstname}</span>}
                {form.lastname && <span className="text-2xl font-semibold text-accent-light dark:text-accent-dark">{form.lastname}</span>}
                <span className="text-xs text-gray-400 mt-1">(нажмите для редактирования)</span>
            </div>
        );
    };

    return (
        <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex flex-col items-center justify-center p-4 transition-colors duration-300">
            {/* Navigation Button */}
            <button
                onClick={() => navigate('/main')}
                className="absolute top-4 left-4 p-2 rounded-full neumorphic-light dark:neumorphic-dark text-gray-700 dark:text-gray-300 hover:opacity-80 transition-opacity"
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
            </button>

            <div className="w-full max-w-md neumorphic-light dark:neumorphic-dark rounded-2xl p-8 flex flex-col gap-6">
                <h1 className="text-3xl font-bold text-accent-light dark:text-accent-dark mb-4">Профиль</h1>
                {renderHeader()}
                {loading && <div className="text-center animate-pulse text-lg">Загрузка...</div>}
                {error && <div className="text-center text-red-500">{error}</div>}
                <div className="flex flex-col gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Имя пользователя</label>
                        <input
                            type="text"
                            value={form.username}
                            readOnly
                            className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-gray-200 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Email</label>
                        <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Пароль</label>
                        <div className="relative flex items-center">
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={form.password}
                                onChange={e => setForm({ ...form, password: e.target.value })}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 pr-12"
                                placeholder="Введите новый пароль"
                            />
                            <button
                                type="button"
                                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-300"
                                onClick={() => setShowPassword(v => !v)}
                                tabIndex={-1}
                            >
                                {showPassword ? (
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                                ) : (
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.956 9.956 0 012.223-3.592m3.31-2.687A9.956 9.956 0 0112 5c4.478 0 8.268 2.943 9.542 7a9.956 9.956 0 01-4.043 5.306M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                                )}
                            </button>
                        </div>
                    </div>
                </div>
                <button className="w-full bg-accent-light dark:bg-accent-dark text-white py-3 px-4 rounded-lg font-medium hover:opacity-90 transition-opacity shadow-md" onClick={handleSave} disabled={loading}>Сохранить</button>
                {success && <div className="text-center text-green-500">{success}</div>}
            </div>
        </div>
    );
};

export default ProfilePage;