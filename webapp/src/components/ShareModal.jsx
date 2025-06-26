import { useState, useEffect } from 'react';
import { userAPI, fileAPI } from '../api/api';

const ShareModal = ({ file, onClose, onShareCreated }) => {
    const [shareMode, setShareMode] = useState('link');
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [allUsers, setAllUsers] = useState([]);
    const [lifetime, setLifetime] = useState('PT30M');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [shareResult, setShareResult] = useState(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await userAPI.getAllUsers({ size: 100 });
            setAllUsers(response.users || []);
        } catch (error) {
            setError('Ошибка загрузки пользователей');
        }
    };

    const handleCreateShare = async () => {
        setLoading(true);
        setError('');

        try {
            const shareData = {
                sharingLifetime: lifetime,
                sharedUsersEmails: shareMode === 'users' ? selectedUsers : []
            };

            const result = await fileAPI.createShare(file.id, shareData);
            setShareResult(result);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCopyLink = () => {
        const link = `http://localhost:3000/shared/${shareResult.sharedId}`;
        navigator.clipboard.writeText(link);
    };

    const formatDateTime = (dateTime) => {
        return new Date(dateTime).toLocaleString('ru-RU');
    };

    if (shareResult) {
        return (
            <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 animate-fadeIn">
                <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl max-w-md w-full mx-4">
                    <h2 className="text-2xl font-bold mb-4 text-gray-800 dark:text-gray-200">Ссылка создана</h2>

                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                            Ссылка для общего доступа:
                        </label>
                        <div className="flex gap-2">
                            <input
                                type="text"
                                value={`http://localhost:3000/shared/${shareResult.sharedId}`}
                                readOnly
                                className="flex-1 px-3 py-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 text-sm"
                            />
                            <button
                                onClick={handleCopyLink}
                                className="px-3 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                            >
                                Копировать
                            </button>
                        </div>
                    </div>

                    <div className="mb-4 text-sm text-gray-600 dark:text-gray-400">
                        <div>Создано: {formatDateTime(shareResult.createdAt)}</div>
                        <div>Истекает: {formatDateTime(shareResult.expiresAt)}</div>
                    </div>

                    <div className="flex gap-2">
                        <button
                            onClick={() => {
                                setShareResult(null);
                                onShareCreated();
                            }}
                            className="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
                        >
                            Готово
                        </button>
                        <button
                            onClick={onClose}
                            className="flex-1 px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                        >
                            Закрыть
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 animate-fadeIn">
            <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl max-w-md w-full mx-4">
                <h2 className="text-2xl font-bold mb-4 text-gray-800 dark:text-gray-200">
                    Поделиться файлом
                </h2>

                <div className="mb-4">
                    <div className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                        Файл: {file.originalName}
                    </div>
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                        Режим доступа:
                    </label>
                    <div className="space-y-2">
                        <label className="flex items-center">
                            <input
                                type="radio"
                                value="link"
                                checked={shareMode === 'link'}
                                onChange={(e) => setShareMode(e.target.value)}
                                className="mr-2"
                            />
                            <span className="text-gray-800 dark:text-gray-200">Любой с ссылкой</span>
                        </label>
                        <label className="flex items-center">
                            <input
                                type="radio"
                                value="users"
                                checked={shareMode === 'users'}
                                onChange={(e) => setShareMode(e.target.value)}
                                className="mr-2"
                            />
                            <span className="text-gray-800 dark:text-gray-200">Только выбранные пользователи</span>
                        </label>
                    </div>
                </div>

                {shareMode === 'users' && (
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                            Выберите пользователей:
                        </label>
                        <select
                            multiple
                            value={selectedUsers}
                            onChange={(e) => setSelectedUsers(Array.from(e.target.selectedOptions, option => option.value))}
                            className="w-full px-3 py-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200"
                            size="4"
                        >
                            {allUsers.map(user => (
                                <option key={user.email} value={user.email}>
                                    {user.email}
                                </option>
                            ))}
                        </select>
                    </div>
                )}

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                        Время жизни ссылки:
                    </label>
                    <select
                        value={lifetime}
                        onChange={(e) => setLifetime(e.target.value)}
                        className="w-full px-3 py-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200"
                    >
                        <option value="PT1M">1 минута</option>
                        <option value="PT5M">5 минут</option>
                        <option value="PT15M">15 минут</option>
                        <option value="PT30M">30 минут</option>
                        <option value="PT1H">1 час</option>
                        <option value="PT6H">6 часов</option>
                        <option value="PT12H">12 часов</option>
                        <option value="P1D">1 день</option>
                        <option value="P7D">7 дней</option>
                        <option value="P30D">30 дней</option>
                    </select>
                </div>

                {error && (
                    <div className="mb-4 text-red-500 text-sm">{error}</div>
                )}

                <div className="flex gap-2">
                    <button
                        onClick={handleCreateShare}
                        disabled={loading}
                        className="flex-1 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors disabled:opacity-50"
                    >
                        {loading ? 'Создание...' : 'Создать ссылку'}
                    </button>
                    <button
                        onClick={onClose}
                        className="flex-1 px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                    >
                        Отмена
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ShareModal;