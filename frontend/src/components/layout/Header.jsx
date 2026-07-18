import { useNavigate } from 'react-router-dom';

export default function Header() {
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
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8 sticky top-0 z-40">
      <div>
        <h2 className="text-lg font-semibold text-gray-800">Panel de Administración</h2>
      </div>

      <div className="flex items-center gap-6">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-primary-800 rounded-full flex items-center justify-center text-white text-sm font-semibold">
            {nombre.charAt(0).toUpperCase()}
          </div>
          <div className="text-right">
            <p className="text-sm font-medium text-gray-800 leading-tight">{nombre}</p>
            <p className="text-xs text-gray-500 uppercase tracking-wide">{rol}</p>
          </div>
        </div>

        <button
          onClick={handleLogout}
          className="flex items-center gap-2 px-3 py-2 text-sm text-gray-500 hover:text-danger hover:bg-red-50 rounded-lg transition-colors duration-200"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Salir
        </button>
      </div>
    </header>
  );
}
