import { useNavigate } from 'react-router-dom';

export default function Header({ onToggle }) {
  const navigate = useNavigate();
  const nombre = localStorage.getItem('nombre') || localStorage.getItem('email') || 'Usuario';
  const rol = localStorage.getItem('rol') || 'USER';

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('email');
    localStorage.removeItem('nombre');
    navigate('/login');
  };

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4 sm:px-8 sticky top-0 z-30">
      <div className="flex items-center gap-3">
        <button
          onClick={onToggle}
          className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg sm:hidden"
        >
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <h2 className="text-lg font-semibold text-gray-800 truncate">Panel de Administración</h2>
      </div>

      <div className="flex items-center gap-3 sm:gap-6">
        <div className="flex items-center gap-2 sm:gap-3">
          <div className="w-8 h-8 sm:w-9 sm:h-9 bg-primary-800 rounded-full flex items-center justify-center text-white text-xs sm:text-sm font-semibold flex-shrink-0">
            {nombre.charAt(0).toUpperCase()}
          </div>
          <div className="hidden sm:block text-right">
            <p className="text-sm font-medium text-gray-800 leading-tight truncate max-w-[160px]">{nombre}</p>
            <p className="text-xs text-gray-500 uppercase tracking-wide">{rol}</p>
          </div>
        </div>

        <button
          onClick={handleLogout}
          className="flex items-center gap-1.5 sm:gap-2 px-2.5 sm:px-3 py-2 text-sm text-gray-500 hover:text-danger hover:bg-red-50 rounded-lg transition-colors duration-200"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          <span className="hidden sm:inline">Salir</span>
        </button>
      </div>
    </header>
  );
}
