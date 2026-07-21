import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import API, { productoService } from '../services/api';

export default function Dashboard() {
  const [metrics, setMetrics] = useState({
    totalActivos: 0,
    stockBajo: 0,
    movimientosHoy: 0,
    ultimoMovimiento: null,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const [prodRes, movRes] = await Promise.allSettled([
          productoService.listarActivos({ page: 0, size: 10000 }),
          API.get('/movimientos', { params: { page: 0, size: 10000 } }),
        ]);

        const prodData = prodRes.status === 'fulfilled' ? (prodRes.value.data.content || prodRes.value.data) : [];
        const movData = movRes.status === 'fulfilled' ? (movRes.value.data.content || movRes.value.data) : [];
        const productos = Array.isArray(prodData) ? prodData : [];
        const movimientos = Array.isArray(movData) ? movData : [];

        const activos = productos.filter((p) => p.activo !== false).length;
        const bajo = productos.filter(
          (p) => p.stock <= p.stockMinimo
        ).length;
        const hoy = new Date().toISOString().split('T')[0];
        const movHoy = movimientos.filter((m) => {
          const fecha = m.fecha ? m.fecha.split('T')[0] : '';
          return fecha === hoy;
        }).length;
        const ultimo = movimientos.length > 0 ? movimientos[movimientos.length - 1] : null;

        setMetrics({ totalActivos: activos, stockBajo: bajo, movimientosHoy: movHoy, ultimoMovimiento: ultimo });
      } catch {
        // fallback a datos de ejemplo si falla la API
        setMetrics({
          totalActivos: 0,
          stockBajo: 0,
          movimientosHoy: 0,
          ultimoMovimiento: null,
        });
      } finally {
        setLoading(false);
      }
    };
    fetchMetrics();
  }, []);

  const cards = [
    {
      title: 'Productos Activos',
      value: metrics.totalActivos,
      color: 'text-primary-800',
      bg: 'bg-blue-50',
      icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
      border: 'border-l-4 border-primary-800',
    },
    {
      title: 'Stock Bajo',
      value: metrics.stockBajo,
      color: 'text-danger',
      bg: 'bg-red-50',
      icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.77-.833-2.694-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z',
      border: 'border-l-4 border-danger',
    },
    {
      title: 'Movimientos Hoy',
      value: metrics.movimientosHoy,
      color: 'text-success',
      bg: 'bg-green-50',
      icon: 'M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4',
      border: 'border-l-4 border-success',
    },
    {
      title: 'Último Movimiento',
      value: metrics.ultimoMovimiento
        ? new Date(metrics.ultimoMovimiento.fecha).toLocaleString('es-MX', {
            day: '2-digit',
            month: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
          })
        : '—',
      color: 'text-warning',
      bg: 'bg-amber-50',
      icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
      border: 'border-l-4 border-warning',
    },
  ];

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-1">Resumen general del inventario</p>
        </div>
        <Link to="/productos" className="btn-primary flex items-center gap-2 self-start sm:self-auto">
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Nuevo Producto
        </Link>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {cards.map((card) => (
          <div key={card.title} className={`card ${card.border}`}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-500">{card.title}</p>
                <p className={`text-3xl font-bold mt-1 ${card.color}`}>
                  {loading ? (
                    <span className="inline-block w-12 h-8 bg-gray-200 rounded animate-pulse" />
                  ) : (
                    card.value
                  )}
                </p>
              </div>
              <div className={`w-12 h-12 ${card.bg} rounded-lg flex items-center justify-center`}>
                <svg className={`w-6 h-6 ${card.color}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d={card.icon} />
                </svg>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Acceso Rápido</h3>
          <div className="grid grid-cols-2 gap-4">
            {[
              { to: '/productos', label: 'Gestionar Productos', icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4' },
              { to: '/movimientos', label: 'Ver Movimientos', icon: 'M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4' },
              { to: '/categorias', label: 'Categorías', icon: 'M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10' },
              { to: '/usuarios', label: 'Usuarios', icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z' },
            ].map((item) => (
              <Link
                key={item.to}
                to={item.to}
                className="flex items-center gap-3 p-4 rounded-lg border border-gray-200 hover:border-primary-300 hover:bg-primary-50 transition-colors duration-200 group"
              >
                <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center group-hover:bg-primary-100 transition-colors">
                  <svg className="w-5 h-5 text-gray-600 group-hover:text-primary-800" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d={item.icon} />
                  </svg>
                </div>
                <span className="text-sm font-medium text-gray-700 group-hover:text-primary-800">{item.label}</span>
              </Link>
            ))}
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Información del Sistema</h3>
          <div className="space-y-3">
            {[
              { label: 'Versión', value: '1.0.0' },
              { label: 'Entorno', value: import.meta.env.VITE_ENV || (import.meta.env.MODE === 'production' ? 'Producción' : 'Desarrollo') },
              { label: 'Base de Datos', value: 'PostgreSQL 17' },
              { label: 'API Status', value: 'Online' },
            ].map((info) => (
              <div key={info.label} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
                <span className="text-sm text-gray-500">{info.label}</span>
                <span className="text-sm font-medium text-gray-800">{info.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
