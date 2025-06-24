import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { authAPI } from "../api/api";

const AuthPage = () => {
    const [activeTab, setActiveTab] = useState('login');
    const [authData, setAuthData] = useState({ username: '', password: '' });
    const [registerData, setRegisterData] = useState({ username: '', password: '', email: '', firstname: '', lastname: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleAuthSubmit = async (e) => {
        e.preventDefault();
        try {
            const responseData = activeTab === 'login'
                ? await authAPI.login(authData)
                : await authAPI.register(registerData);

            localStorage.setItem('jwtToken', responseData.token);
            navigate('/main');
        } catch (err) {
            setError(err.message || 'Ошибка аутентификации');
        }
    };

    return (
        <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex flex-col items-center justify-center p-4 transition-colors duration-300">
            {/* Theme Toggle */}
            <button
                id="themeToggle"
                className="absolute top-4 right-4 p-2 rounded-full neumorphic-light dark:neumorphic-dark text-gray-700 dark:text-gray-300"
                onClick={() => document.documentElement.classList.toggle('dark')}
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
            </button>

            {/* Main Title */}
            <h1 className="text-5xl font-bold mb-12 text-accent-light dark:text-accent-dark">FileIO</h1>

            {/* Auth Container */}
            <div className="flex flex-col md:flex-row gap-8 w-full max-w-4xl">
                {/* Login Form */}
                <div className={`flex-1 neumorphic-light dark:neumorphic-dark rounded-2xl p-8 relative z-10 transform transition-all duration-300 ${activeTab === 'login' ? 'scale-105 shadow-2xl' : 'scale-95 opacity-80'}`}>
                    <h2 className="text-2xl font-bold mb-6 text-gray-800 dark:text-gray-200">Вход</h2>

                    <form onSubmit={handleAuthSubmit} onClick={() => setActiveTab('login')}>
                        <div className="mb-4">
                            <label htmlFor="auth-username" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Имя пользователя
                            </label>
                            <input
                                type="text"
                                id="auth-username"
                                value={authData.username}
                                onChange={(e) => setAuthData({...authData, username: e.target.value})}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>

                        <div className="mb-6">
                            <label htmlFor="auth-password" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Пароль
                            </label>
                            <input
                                type="password"
                                id="auth-password"
                                value={authData.password}
                                onChange={(e) => setAuthData({...authData, password: e.target.value})}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>

                        {error && activeTab === 'login' && (
                            <div className="mb-4 p-3 bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-200 rounded-lg text-sm">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full bg-accent-light dark:bg-accent-dark text-white py-3 px-4 rounded-lg font-medium hover:opacity-90 transition-opacity shadow-md"
                            onClick={() => setActiveTab('login')}
                        >
                            Войти
                        </button>
                    </form>
                </div>

                {/* Registration Form */}
                <div className={`flex-1 neumorphic-light dark:neumorphic-dark rounded-2xl p-8 relative -ml-6 md:-ml-12 mt-6 md:mt-12 transform transition-all duration-300 ${activeTab === 'register' ? 'scale-105 shadow-2xl' : 'scale-95 opacity-80'}`}>
                    <h2 className="text-2xl font-bold mb-6 text-gray-800 dark:text-gray-200">Регистрация</h2>

                    <form onSubmit={handleAuthSubmit} onClick={() => setActiveTab('register')}>
                        <div className="mb-4">
                            <label htmlFor="reg-email" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Email</label>
                            <input
                                type="email"
                                id="reg-email"
                                value={registerData.email}
                                onChange={e => setRegisterData({ ...registerData, email: e.target.value })}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label htmlFor="reg-firstname" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Имя</label>
                            <input
                                type="text"
                                id="reg-firstname"
                                value={registerData.firstname}
                                onChange={e => setRegisterData({ ...registerData, firstname: e.target.value })}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label htmlFor="reg-lastname" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Фамилия</label>
                            <input
                                type="text"
                                id="reg-lastname"
                                value={registerData.lastname}
                                onChange={e => setRegisterData({ ...registerData, lastname: e.target.value })}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label htmlFor="reg-username" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Имя пользователя
                            </label>
                            <input
                                type="text"
                                id="reg-username"
                                value={registerData.username}
                                onChange={(e) => setRegisterData({...registerData, username: e.target.value})}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>

                        <div className="mb-6">
                            <label htmlFor="reg-password" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Пароль
                            </label>
                            <input
                                type="password"
                                id="reg-password"
                                value={registerData.password}
                                onChange={(e) => setRegisterData({...registerData, password: e.target.value})}
                                className="w-full px-4 py-3 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-accent-light dark:focus:ring-accent-dark transition-all"
                                required
                            />
                        </div>

                        {error && activeTab === 'register' && (
                            <div className="mb-4 p-3 bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-200 rounded-lg text-sm">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full bg-accent-light dark:bg-accent-dark text-white py-3 px-4 rounded-lg font-medium hover:opacity-90 transition-opacity shadow-md"
                            onClick={() => setActiveTab('register')}
                        >
                            Зарегистрироваться
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AuthPage;