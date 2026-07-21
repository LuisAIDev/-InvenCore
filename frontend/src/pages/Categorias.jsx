import { useState, useEffect } from 'react';
import API from '../services/api';

export default function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCategorias();
  }, []);

  const fetchCategorias = async () => {
    try {
      const res = await API.get('/categorias');
      setCategorias(res.data.content || res.data);
    } catch {
      setCategorias([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Categorías</h1>
          <p className="text-sm text-gray-500 mt-1">Administración de categorías de productos</p>
        </div>
      </div>

      <div className="card overflow-hidden !p-0">
        <div className="hidden sm:block overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">ID</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Descripción</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr><td colSpan={3} className="px-4 py-12 text-center text-gray-500">Cargando...</td></tr>
              ) : categorias.length === 0 ? (
                <tr><td colSpan={3} className="px-4 py-12 text-center text-gray-500">No hay categorías registradas.</td></tr>
              ) : (
                categorias.map((c) => (
                  <tr key={c.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">#{c.id}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">{c.nombre}</td>
                    <td className="px-4 py-3 text-gray-600">{c.descripcion || '—'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="sm:hidden divide-y divide-gray-100">
          {loading ? (
            <div className="p-4 text-center text-gray-500">Cargando...</div>
          ) : categorias.length === 0 ? (
            <div className="p-4 text-center text-gray-500">No hay categorías registradas.</div>
          ) : (
            categorias.map((c) => (
              <div key={c.id} className="p-4 space-y-1">
                <div className="flex items-center justify-between">
                  <p className="font-medium text-gray-900">{c.nombre}</p>
                  <span className="text-xs text-gray-400 font-mono">#{c.id}</span>
                </div>
                <p className="text-sm text-gray-500">{c.descripcion || 'Sin descripción'}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
