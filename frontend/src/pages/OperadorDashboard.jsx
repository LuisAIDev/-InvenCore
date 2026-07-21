import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productoService, movimientoService } from '../services/api';

const formatCOP = (amount) =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(amount);

const MetricCard = ({ title, value, icon, bgGradient, iconBg, pulse }) => (
  <div className={`relative overflow-hidden rounded-2xl p-6 ${bgGradient} shadow-lg transition-transform duration-300 hover:scale-[1.02]`}>
    <div className="flex items-start justify-between">
      <div className="z-10">
        <p className="text-sm font-medium text-white/70">{title}</p>
        <p className={`text-3xl font-bold text-white mt-2 ${pulse ? 'animate-pulse' : ''}`}>
          {value}
        </p>
      </div>
      <div className={`w-14 h-14 ${iconBg} rounded-xl flex items-center justify-center backdrop-blur-sm`}>
        <svg className="w-7 h-7 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
        </svg>
      </div>
    </div>
    <div className="absolute -bottom-4 -right-4 w-24 h-24 rounded-full bg-white/5" />
    <div className="absolute -top-4 -left-4 w-16 h-16 rounded-full bg-white/5" />
  </div>
);

export default function OperadorDashboard() {
  const navigate = useNavigate();
  const nombre = localStorage.getItem('nombre') || 'Operador';
  const [productos, setProductos] = useState([]);
  const [movimientos, setMovimientos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [prodRes, movRes] = await Promise.all([
          productoService.listarActivos({ page: 0, size: 10000 }),
          movimientoService.listarTodos({ page: 0, size: 10000 }),
        ]);
        setProductos(prodRes.data.content || prodRes.data);
        setMovimientos(movRes.data.content || movRes.data);
      } catch {
        setProductos([]);
        setMovimientos([]);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const activos = productos.filter((p) => p.activo !== false);
  const stockBajo = productos.filter((p) => p.stock <= p.stockMinimo);
  const hoy = new Date().toISOString().split('T')[0];
  const movimientosHoy = movimientos.filter((m) => {
    const fecha = m.fecha ? m.fecha.split('T')[0] : '';
    return fecha === hoy;
  });
  const ultimoMov = movimientos.length > 0 ? movimientos[movimientos.length - 1] : null;

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#0f172a' }}>
      {/* Header */}
      <header className="bg-gradient-to-r from-blue-900 via-blue-800 to-purple-900 px-6 py-5 shadow-2xl">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm">
              <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </div>
            <div>
              <h1 className="text-xl font-bold text-white">InvenCore</h1>
              <p className="text-xs text-blue-200">Panel del Operador</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="text-right">
              <p className="text-sm font-medium text-white">{nombre}</p>
              <p className="text-xs text-blue-200">OPERADOR</p>
            </div>
            <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
              <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
        </div>
      </header>

      {/* Metrics */}
      <div className="max-w-7xl mx-auto px-6 py-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <MetricCard
            title="Productos Disponibles"
            value={loading ? '...' : activos.length}
            icon="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
            bgGradient="bg-gradient-to-br from-blue-600 to-blue-800"
            iconBg="bg-blue-400/30"
          />
          <MetricCard
            title="Alertas de Stock Bajo"
            value={loading ? '...' : stockBajo.length}
            icon="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.77-.833-2.694-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"
            bgGradient="bg-gradient-to-br from-red-600 to-red-800"
            iconBg="bg-red-400/30"
            pulse
          />
          <MetricCard
            title="Movimientos del Día"
            value={loading ? '...' : movimientosHoy.length}
            icon="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"
            bgGradient="bg-gradient-to-br from-emerald-600 to-emerald-800"
            iconBg="bg-emerald-400/30"
          />
          <MetricCard
            title="Último Movimiento"
            value={ultimoMov
              ? new Date(ultimoMov.fecha).toLocaleString('es-CO', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
              : '—'}
            icon="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
            bgGradient="bg-gradient-to-br from-orange-600 to-orange-800"
            iconBg="bg-orange-400/30"
          />
        </div>

        {/* Productos Disponibles */}
        <div className="mt-8">
          <h2 className="text-lg font-semibold text-white mb-4">Productos Disponibles</h2>
          {loading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="rounded-2xl p-5 animate-pulse" style={{ backgroundColor: '#1e293b' }}>
                  <div className="h-5 bg-gray-600 rounded w-3/4 mb-3" />
                  <div className="h-4 bg-gray-600 rounded w-1/2 mb-3" />
                  <div className="h-6 bg-gray-600 rounded w-1/3 mb-3" />
                  <div className="h-2 bg-gray-600 rounded w-full mb-2" />
                  <div className="h-6 bg-gray-600 rounded w-1/4" />
                </div>
              ))}
            </div>
          ) : activos.length === 0 ? (
            <div className="rounded-2xl p-10 text-center" style={{ backgroundColor: '#1e293b' }}>
              <svg className="w-16 h-16 mx-auto text-gray-600 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
              <p className="text-gray-400 text-lg">No hay productos disponibles</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {activos.map((prod) => {
                const pct = prod.stockMinimo > 0 ? Math.min((prod.stock / prod.stockMinimo) * 100, 100) : 100;
                const isLow = prod.stock <= prod.stockMinimo;
                return (
                  <div
                    key={prod.id}
                    className="rounded-2xl p-5 transition-all duration-300 hover:shadow-xl hover:shadow-blue-900/30 hover:-translate-y-1 border border-white/5"
                    style={{ backgroundColor: '#1e293b' }}
                  >
                    <div className="flex items-start justify-between mb-3">
                      <h3 className="font-semibold text-white text-sm leading-tight flex-1 mr-2">
                        {prod.nombre}
                      </h3>
                      {isLow ? (
                        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-red-500/20 text-red-400 animate-pulse border border-red-500/30 shrink-0">
                          <span className="w-1.5 h-1.5 rounded-full bg-red-400" />
                          Stock Bajo
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 shrink-0">
                          <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
                          Disponible
                        </span>
                      )}
                    </div>
                    {prod.categoria && (
                      <p className="text-xs text-gray-400 mb-3">{prod.categoria.nombre}</p>
                    )}
                    <p className="text-lg font-bold text-white mb-4">
                      {formatCOP(prod.precio)}
                    </p>
                    <div className="mb-1">
                      <div className="flex justify-between text-xs mb-1">
                        <span className="text-gray-400">Stock</span>
                        <span className={isLow ? 'text-red-400 font-medium' : 'text-gray-300'}>
                          {prod.stock} / {prod.stockMinimo}
                        </span>
                      </div>
                      <div className="w-full h-2 rounded-full overflow-hidden" style={{ backgroundColor: '#0f172a' }}>
                        <div
                          className={`h-full rounded-full transition-all duration-500 ${
                            isLow ? 'bg-gradient-to-r from-red-500 to-orange-500' : 'bg-gradient-to-r from-blue-500 to-purple-500'
                          }`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* FAB */}
      <button
        onClick={() => navigate('/operador/movimiento')}
        className="fixed bottom-8 right-8 w-16 h-16 bg-gradient-to-br from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white rounded-2xl shadow-2xl shadow-blue-900/50 flex items-center justify-center transition-all duration-300 hover:scale-110 hover:rotate-90 z-50"
      >
        <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
      </button>
    </div>
  );
}
