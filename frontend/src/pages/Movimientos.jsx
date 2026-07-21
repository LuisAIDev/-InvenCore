import { useState, useEffect } from 'react';
import API from '../services/api';

export default function Movimientos() {
  const [movimientos, setMovimientos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMovimientos();
  }, []);

  const fetchMovimientos = async () => {
    try {
      const res = await API.get('/movimientos');
      setMovimientos(res.data);
    } catch {
      setMovimientos([]);
    } finally {
      setLoading(false);
    }
  };

  const getTipoBadge = (tipo) => {
    if (tipo === 'ENTRADA') return 'bg-green-100 text-success';
    if (tipo === 'SALIDA') return 'bg-red-100 text-danger';
    return 'bg-gray-100 text-gray-600';
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Movimientos</h1>
          <p className="text-sm text-gray-500 mt-1">Historial de entradas y salidas del inventario</p>
        </div>
      </div>

      <div className="card overflow-hidden !p-0">
        <div className="hidden sm:block overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">ID</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Producto</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Tipo</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Cantidad</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Fecha</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr><td colSpan={5} className="px-4 py-12 text-center text-gray-500">Cargando...</td></tr>
              ) : movimientos.length === 0 ? (
                <tr><td colSpan={5} className="px-4 py-12 text-center text-gray-500">No hay movimientos registrados.</td></tr>
              ) : (
                movimientos.map((m) => (
                  <tr key={m.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">#{m.id}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">{m.producto?.nombre || `ID ${m.productoId}`}</td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getTipoBadge(m.tipo)}`}>
                        {m.tipo}
                      </span>
                    </td>
                    <td className="px-4 py-3 font-semibold">{m.cantidad}</td>
                    <td className="px-4 py-3 text-gray-500">
                      {m.fecha ? new Date(m.fecha).toLocaleString('es-MX') : '—'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="sm:hidden divide-y divide-gray-100">
          {loading ? (
            <div className="p-4 text-center text-gray-500">Cargando...</div>
          ) : movimientos.length === 0 ? (
            <div className="p-4 text-center text-gray-500">No hay movimientos registrados.</div>
          ) : (
            movimientos.map((m) => (
              <div key={m.id} className="p-4 space-y-2">
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0 flex-1">
                    <p className="font-medium text-gray-900 truncate">{m.producto?.nombre || `ID ${m.productoId}`}</p>
                    <p className="text-xs text-gray-400 font-mono">#{m.id}</p>
                  </div>
                  <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium flex-shrink-0 ${getTipoBadge(m.tipo)}`}>
                    {m.tipo}
                  </span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="font-semibold text-gray-800">Cantidad: {m.cantidad}</span>
                  <span className="text-gray-500 text-xs">
                    {m.fecha ? new Date(m.fecha).toLocaleString('es-MX') : '—'}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
