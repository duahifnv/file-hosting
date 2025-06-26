import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fileAPI } from '../api/api';

const SharedFilePage = () => {
    const { sharedId } = useParams();
    const navigate = useNavigate();
    const [fileMeta, setFileMeta] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [downloading, setDownloading] = useState(false);

    useEffect(() => {
        fetchSharedFile();
    }, [sharedId]);

    const fetchSharedFile = async () => {
        try {
            setLoading(true);
            setError('');

            const data = await fileAPI.getSharedFileMeta(sharedId);
            setFileMeta(data);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleDownload = async () => {
        try {
            setDownloading(true);
            setError('');

            const response = await fileAPI.downloadSharedFile(sharedId);

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileMeta.originalName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            setError(error.message);
        } finally {
            setDownloading(false);
        }
    };

    const getIcon = (contentType) => {
        if (contentType?.startsWith('image/')) {
            return (
                <svg className="w-16 h-16 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2zm16 0l-8 8-4-4-4 4" />
                </svg>
            );
        }
        return (
            <svg className="w-16 h-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M7 10V6a5 5 0 0110 0v4" />
            </svg>
        );
    };

    const formatFileSize = (bytes) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    const formatDateTime = (dateTime) => {
        return new Date(dateTime).toLocaleString('ru-RU');
    };

    if (loading) {
        return (
            <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-accent-light dark:border-accent-dark mx-auto mb-4"></div>
                    <div className="text-gray-600 dark:text-gray-400">Загрузка файла...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex items-center justify-center p-4">
                <div className="bg-white dark:bg-gray-800 p-8 rounded-2xl max-w-md w-full text-center">
                    <div className="text-red-500 mb-4">
                        <svg className="w-16 h-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                        </svg>
                    </div>
                    <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-200 mb-4">Ошибка</h2>
                    <p className="text-gray-600 dark:text-gray-400 mb-6">{error}</p>
                    <button
                        onClick={() => navigate('/main')}
                        className="px-6 py-2 bg-accent-light dark:bg-accent-dark text-white rounded-lg hover:opacity-90 transition-opacity"
                    >
                        Вернуться на главную
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex items-center justify-center p-4">
            <div className="bg-white dark:bg-gray-800 p-8 rounded-2xl max-w-lg w-full">
                <div className="text-center mb-6">
                    {getIcon(fileMeta.contentType)}
                    <h1 className="text-2xl font-bold text-gray-800 dark:text-gray-200 mt-4">
                        {fileMeta.originalName}
                    </h1>
                </div>

                <div className="space-y-4 mb-6">
                    <div className="flex justify-between items-center">
                        <span className="text-gray-600 dark:text-gray-400">Тип файла:</span>
                        <span className="text-gray-800 dark:text-gray-200">{fileMeta.contentType || 'Неизвестно'}</span>
                    </div>

                    <div className="flex justify-between items-center">
                        <span className="text-gray-600 dark:text-gray-400">Размер:</span>
                        <span className="text-gray-800 dark:text-gray-200">{formatFileSize(fileMeta.originalSize)}</span>
                    </div>

                    <div className="flex justify-between items-center">
                        <span className="text-gray-600 dark:text-gray-400">Загружен:</span>
                        <span className="text-gray-800 dark:text-gray-200">{formatDateTime(fileMeta.createdAt)}</span>
                    </div>

                    {fileMeta.expiresAt && (
                        <div className="flex justify-between items-center">
                            <span className="text-gray-600 dark:text-gray-400">Истекает:</span>
                            <span className="text-gray-800 dark:text-gray-200">{formatDateTime(fileMeta.expiresAt)}</span>
                        </div>
                    )}
                </div>

                <div className="flex gap-3">
                    <button
                        onClick={handleDownload}
                        disabled={downloading}
                        className="flex-1 px-6 py-3 bg-accent-light dark:bg-accent-dark text-white rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 flex items-center justify-center gap-2"
                    >
                        {downloading ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                Загрузка...
                            </>
                        ) : (
                            <>
                                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                </svg>
                                Скачать файл
                            </>
                        )}
                    </button>

                    <button
                        onClick={() => navigate('/main')}
                        className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
                    >
                        На главную
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SharedFilePage;