import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { fileAPI } from '../api/api';
import ShareModal from '../components/ShareModal';

const sortOptions = [
    { value: 'originalName', label: 'Имя файла' },
    { value: 'contentType', label: 'Тип' },
    { value: 'originalSize', label: 'Размер' },
    { value: 'createdAt', label: 'Создан' },
    { value: 'expiresAt', label: 'Истекает' },
];

const icons = {
    'image': (
        <svg className="w-8 h-8 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2zm16 0l-8 8-4-4-4 4" /></svg>
    ),
    'svg': (
        <svg className="w-8 h-8 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" /></svg>
    ),
    'default': (
        <svg className="w-8 h-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M7 10V6a5 5 0 0110 0v4" /></svg>
    )
};

function getIcon(contentType) {
    if (contentType?.startsWith('image/')) return icons.image;
    if (contentType === 'image/svg+xml') return icons.svg;
    return icons.default;
}

const MainPage = () => {
    const [fileMetas, setFileMetas] = useState([]);
    const [page, setPage] = useState(0);
    const [size] = useState(5);
    const [sort, setSort] = useState('createdAt');
    const [order, setOrder] = useState('desc');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [modalImg, setModalImg] = useState(null);
    const [contentType, setContentType] = useState('');
    const [showDropdown, setShowDropdown] = useState(false);
    const [activeTab, setActiveTab] = useState('my-files');
    const [showShareModal, setShowShareModal] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const navigate = useNavigate();

    const handleAuthError = (e) => {
        if (!localStorage.getItem('jwtToken') || (e && e.status === 403)) {
            localStorage.removeItem('jwtToken');
            navigate('/auth');
        }
    };

    const fetchMetas = async () => {
        setLoading(true);
        setError('');
        try {
            const data = await fileAPI.getAllFileMetas({
                page,
                size,
                sort: `${sort},${order}`,
                contentType,
                shared: activeTab === 'shared-files'
            });
            setFileMetas(data.fileMetas || []);
        } catch (e) {
            handleAuthError(e);
            setError(e.message);
        }
        setLoading(false);
    };

    useEffect(() => {
        if (!localStorage.getItem('jwtToken')) {
            navigate('/auth');
            return;
        }
        fetchMetas();
    }, [page, sort, order, contentType, activeTab]);

    const handleFilePick = async (e) => {
        if (!e.target.files[0]) return;
        setLoading(true);
        setError('');
        try {
            await fileAPI.uploadFile(e.target.files[0]);
            fetchMetas();
        } catch (e) {
            handleAuthError(e);
            setError(e.message);
        }
        setLoading(false);
    };

    const handleDelete = async (metaId) => {
        setLoading(true);
        setError('');
        try {
            if (activeTab === 'shared-files') {
                await fileAPI.removeShare(metaId);
            } else {
                await fileAPI.removeFile(metaId);
            }
            fetchMetas();
        } catch (e) {
            handleAuthError(e);
            setError(e.message);
        }
        setLoading(false);
    };

    const handleShare = (file) => {
        setSelectedFile(file);
        setShowShareModal(true);
    };

    const handleShareCreated = () => {
        setShowShareModal(false);
        setSelectedFile(null);
        if (activeTab === 'shared-files') {
            fetchMetas();
        }
    };

    const handleDoubleClick = async (meta) => {
        if (meta.contentType?.startsWith('image/')) {
            try {
                const res = await fileAPI.getFileById(meta.id, activeTab === 'shared-files');
                const blob = await res.blob();
                setModalImg(URL.createObjectURL(blob));
                setShowModal(true);
            } catch (e) {
                handleAuthError(e);
                setError(e.message);
            }
        } else {
            try {
                const res = await fileAPI.getFileById(meta.id, activeTab === 'shared-files');
                const blob = await res.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = meta.originalName;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            } catch (e) {
                handleAuthError(e);
                setError(e.message);
            }
        }
    };

    return (
        <div className="bg-primary-light dark:bg-primary-dark min-h-screen flex flex-col items-center justify-center p-4 transition-colors duration-300">
            <header className="w-full max-w-4xl flex items-center justify-between mb-8">
                <div></div>
                <div className="text-4xl font-bold text-accent-light dark:text-accent-dark select-none">FileIO</div>
                <div className="relative" onMouseEnter={() => setShowDropdown(true)} onMouseLeave={() => setShowDropdown(false)}>
                    <button className="p-2 rounded-full neumorphic-light dark:neumorphic-dark">
                        <svg className="w-8 h-8 text-gray-700 dark:text-gray-200" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5.121 17.804A13.937 13.937 0 0112 15c2.5 0 4.847.655 6.879 1.804M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                    </button>
                    {showDropdown && (
                        <div className="absolute right-0 mb-2 w-40 bg-white dark:bg-gray-800 rounded-xl shadow-lg z-10 flex flex-col animate-fadeIn">
                            <button className="px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700" onClick={() => { setShowDropdown(false); navigate('/profile'); }}>Профиль</button>
                            <button className="px-4 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700" onClick={() => { localStorage.removeItem('jwtToken'); setShowDropdown(false); navigate('/auth'); }}>Выйти</button>
                        </div>
                    )}
                </div>
            </header>
            <div className="w-full max-w-4xl flex flex-col gap-6">
                <div className="flex flex-wrap gap-4 items-center justify-between mb-2">
                    <div className="flex gap-2 items-center">
                        <div className="flex bg-gray-200 dark:bg-gray-700 rounded-lg p-1">
                            <button
                                onClick={() => setActiveTab('my-files')}
                                className={`px-4 py-2 rounded-md transition-colors ${
                                    activeTab === 'my-files'
                                        ? 'bg-white dark:bg-gray-600 text-gray-800 dark:text-gray-200 shadow-sm'
                                        : 'text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200'
                                }`}
                            >
                                Мои файлы
                            </button>
                            <button
                                onClick={() => setActiveTab('shared-files')}
                                className={`px-4 py-2 rounded-md transition-colors ${
                                    activeTab === 'shared-files'
                                        ? 'bg-white dark:bg-gray-600 text-gray-800 dark:text-gray-200 shadow-sm'
                                        : 'text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200'
                                }`}
                            >
                                Общие файлы
                            </button>
                        </div>
                        <select className="px-3 py-2 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200" value={sort} onChange={e => setSort(e.target.value)}>
                            {sortOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
                        </select>
                        <select className="px-3 py-2 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200" value={order} onChange={e => setOrder(e.target.value)}>
                            <option value="asc">По возрастанию</option>
                            <option value="desc">По убыванию</option>
                        </select>
                        <input type="text" placeholder="contentType..." className="px-3 py-2 rounded-lg neumorphic-input-light dark:neumorphic-input-dark bg-secondary-light dark:bg-secondary-dark text-gray-800 dark:text-gray-200" value={contentType} onChange={e => setContentType(e.target.value)} />
                    </div>
                    {activeTab === 'my-files' && (
                        <label className="bg-accent-light dark:bg-accent-dark text-white py-2 px-4 rounded-lg font-medium hover:opacity-90 transition-opacity shadow-md cursor-pointer">
                            Добавить файл
                            <input type="file" className="hidden" onChange={handleFilePick} />
                        </label>
                    )}
                </div>
                <div className="neumorphic-light dark:neumorphic-dark rounded-2xl p-6 min-h-[300px] flex flex-col gap-4 transition-all">
                    {loading && <div className="text-center animate-pulse text-lg">Загрузка...</div>}
                    {error && <div className="text-center text-red-500">{error}</div>}
                    {!loading && !error && fileMetas.length === 0 && <div className="text-center text-gray-400">Нет файлов</div>}
                    {fileMetas.map(meta => (
                        <div
                            key={meta.id}
                            className="flex items-center gap-4 p-4 rounded-xl hover:scale-[1.01] transition-transform cursor-pointer neumorphic-light dark:neumorphic-dark relative group"
                            onDoubleClick={() => handleDoubleClick(meta)}
                        >
                            <div>{getIcon(meta.contentType)}</div>
                            <div className="flex-1 flex flex-col gap-1">
                                <div className="font-semibold text-lg text-gray-800 dark:text-gray-200">{meta.originalName}</div>
                                <div className="text-sm text-gray-500 dark:text-gray-400 flex gap-4">
                                    <span>{meta.contentType}</span>
                                    <span>{(meta.originalSize / 1024).toFixed(1)} KB</span>
                                    <span>Создан: {new Date(meta.createdAt).toLocaleString()}</span>
                                    <span>Истекает: {meta.expiresAt ? new Date(meta.expiresAt).toLocaleString() : '-'}</span>
                                </div>
                            </div>
                            <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                {activeTab === 'my-files' && (
                                    <button
                                        className="px-3 py-1 bg-blue-500 text-white rounded-lg"
                                        onClick={e => { e.stopPropagation(); handleShare(meta); }}
                                    >
                                        Поделиться
                                    </button>
                                )}
                                <button
                                    className="px-3 py-1 bg-red-500 text-white rounded-lg"
                                    onClick={e => { e.stopPropagation(); handleDelete(meta.id); }}
                                >
                                    {activeTab === 'shared-files' ? 'Удалить ссылку' : 'Удалить'}
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
                <div className="flex justify-between mt-2">
                    <button
                        className="px-4 py-2 rounded-lg neumorphic-light dark:neumorphic-dark disabled:opacity-50"
                        onClick={() => setPage(p => Math.max(0, p - 1))}
                        disabled={page === 0}
                    >Назад</button>
                    <span className="text-gray-700 dark:text-gray-300">Страница {page + 1}</span>
                    <button
                        className="px-4 py-2 rounded-lg neumorphic-light dark:neumorphic-dark"
                        onClick={() => setPage(p => p + 1)}
                        disabled={fileMetas.length < size}
                    >Вперёд</button>
                </div>
            </div>
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 animate-fadeIn" onClick={() => setShowModal(false)}>
                    <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl max-w-2xl max-h-[80vh] flex flex-col items-center">
                        <img src={modalImg} alt="preview" className="max-w-full max-h-[60vh] rounded-xl shadow-lg" />
                        <button className="mt-4 px-4 py-2 bg-accent-light dark:bg-accent-dark text-white rounded-lg" onClick={() => setShowModal(false)}>Закрыть</button>
                    </div>
                </div>
            )}
            {showShareModal && selectedFile && (
                <ShareModal
                    file={selectedFile}
                    onClose={() => {
                        setShowShareModal(false);
                        setSelectedFile(null);
                    }}
                    onShareCreated={handleShareCreated}
                />
            )}
        </div>
    );
};

export default MainPage;
