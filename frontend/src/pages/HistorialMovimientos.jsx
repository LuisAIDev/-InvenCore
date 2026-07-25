import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { movimientoService } from '../services/api';

export default function HistorialMovimientos() {
  const navigate = useNavigate();
  const nombre = localStorage.getItem('nombre') || 'Operador';
  const [movimientos, setMovimientos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const hoy = new Date();
    const inicio = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate()).toISOString();
    const fin = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate(), 23, 59, 59).toISOString();
    movimientoService.listarTodos({ page: 0, size: 1000 })
      .then((res) => {
        const todos = res.data.content || res.data;
        const hoyStr = new Date().toISOString().split('T')[0];
        const filtrados = todos.filter((m) => {
          const fecha = m.fecha ? m.fecha.split('T')[0] : '';
          return fecha === hoyStr;
        });
        setMovimientos(filtrados);
      })
      .catch(() => setMovimientos([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#0f172a' }}>
      <header className="bg-gradient-to-r from-blue-900 via-blue-800 to-purple-900 px-6 py-5 shadow-2xl">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('/operador')}
              className="w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center hover:bg-white/20 transition-colors"
            >
              <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <div>
              <h1 className="text-xl font-bold text-white">Movimientos del Día</h1>
              <p className="text-xs text-blue-200">{new Date().toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-sm text-blue-200">{nombre}</span>
            <div className="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
              <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-4xl mx-auto px-6 py-8">
        {loading ? (
          <div className="space-y-3">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="rounded-xl p-5 animate-pulse" style={{ backgroundColor: '#1e293b' }}>
                <div className="h-5 bg-gray-600 rounded w-2/3 mb-2" />
                <div className="h-4 bg-gray-600 rounded w-1/3" />
              </div>
            ))}
          </div>
        ) : movimientos.length === 0 ? (
          <div className="rounded-2xl p-12 text-center" style={{ backgroundColor: '#1e293b' }}>
            <svg className="w-16 h-16 mx-auto text-gray-600 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            </svg>
            <p className="text-gray-400 text-lg">Sin movimientos hoy</p>
            <p className="text-gray-500 text-sm mt-1">No se han registrado movimientos en el día de hoy.</p>
          </div>
        ) : (
          <div className="space-y-2">
            <p className="text-sm text-gray-400 mb-4">
              {movimientos.length} movimiento{movimientos.length !== 1 ? 's' : ''} registrado{movimientos.length !== 1 ? 's' : ''} hoy
            </p>
            {movimientos.map((m) => (
              <div
                key={m.id}
                className="flex items-center gap-4 rounded-xl p-4 border border-white/5 transition-all hover:border-white/10"
                style={{ backgroundColor: '#1e293b' }}
              >
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${
                  m.tipo === 'ENTRADA' ? 'bg-emerald-500/15' : 'bg-red-500/15'
                }`}>
                  <svg className={`w-5 h-5 ${m.tipo === 'ENTRADA' ? 'text-emerald-400' : 'text-red-400'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    {m.tipo === 'ENTRADA' ? (
                      <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                    ) : (
                      <path strokeLinecap="round" strokeLinejoin="round" d="M20 12H4" />
                    )}
                  </svg>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-white truncate">{m.productoNombre}</p>
                  <p className="text-xs text-gray-400 mt-0.5">
                    {m.descripcion || 'Sin descripción'}
                  </p>
                </div>
                <div className="text-right shrink-0">
                  <span className={`inline-block text-sm font-bold ${
                    m.tipo === 'ENTRADA' ? 'text-emerald-400' : 'text-red-400'
                  }`}>
                    {m.tipo === 'ENTRADA' ? '+' : '-'}{m.cantidad}
                  </span>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {m.fecha ? new Date(m.fecha).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' }) : ''}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}