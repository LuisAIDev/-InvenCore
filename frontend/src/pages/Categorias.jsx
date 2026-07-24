import { useState, useEffect } from 'react';
import API from '../services/api';

const categoryIcons = {
  default: {
    svg: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    color: 'bg-blue-100 text-blue-700',
  },
  electronica: {
    svg: 'M9 3v2m6-2v2M21 12a9 9 0 11-18 0 9 9 0 0118 0zM9 12a2 2 0 114 0 2 2 0 01-4 0z',
    color: 'bg-purple-100 text-purple-700',
  },
  herramientas: {
    svg: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z',
    color: 'bg-amber-100 text-amber-700',
  },
  limpieza: {
    svg: 'M12 6v6m0 0v6m0-6h6m-6 0H6',
    color: 'bg-cyan-100 text-cyan-700',
  },
  mobiliario: {
    svg: 'M4 6h16M4 10h16m4-7h-4a2 2 0 00-2 2V4a2 2 0 012-2h4a2 2 0 012 2v7a2 2 0 00-2 2h4m-4 7v-7a2 2 0 00-2-2H6a2 2 0 00-2 2v7',
    color: 'bg-orange-100 text-orange-700',
  },
  oficina: {
    svg: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4',
    color: 'bg-green-100 text-green-700',
  },
  papeleria: {
    svg: 'M9 12h6m-6 6V6h12v6M9 16h6a2 2 0 002 2h2a2 2 0 00 2-2V6a2 2 0 00-2-2h-6a2 2 0 00-2 2v14a2 2 0 00 2 2zm0 0V10m3 4h-2',
    color: 'bg-blue-100 text-blue-600',
  },
  seguridad: {
    svg: 'M12 2L2 7v10c0 5.55 3.84 10.74 9 12 5.16 1.26 11 1 11-12V7l-9-5z',
    color: 'bg-red-100 text-red-700',
  },
  tecnologia: {
    svg: 'M4.5 12a.5.5 0 010 1h15a.5.5 0 010-1H4.5zm6.5 4a.5.5 0 010 1h15a.5.5 0 010-1H11zm-6-4a.5.5 0 010 1h15a.5.5 0 010-1H5z',
    color: 'bg-slate-100 text-slate-700',
  },
};

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

  const getCategoryInfo = (nombre) => {
    const key = nombre.toLowerCase();
    for (const [catKey, icon] of Object.entries(categoryIcons)) {
      if (key.includes(catKey)) return icon;
    }
    return categoryIcons.default;
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Categorías</h1>
          <p className="text-sm text-gray-500 mt-1">Administración de categorías de productos</p>
        </div>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="card p-5">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-gray-200 rounded-lg animate-pulse" />
                <div className="space-y-2 flex-1">
                  <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-1/2" />
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : categorias.length === 0 ? (
        <div className="card p-12 text-center text-gray-500">No hay categorías registradas.</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {categorias.map((c) => {
            const icon = getCategoryInfo(c.nombre);
            return (
              <div key={c.id} className="card p-5 hover:shadow-md transition-shadow duration-200">
                <div className="flex items-start gap-4">
                  <div className={`w-11 h-11 rounded-lg flex items-center justify-center flex-shrink-0 ${icon.color}`}>
                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d={icon.svg} />
                    </svg>
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-gray-900 text-base truncate" title={c.nombre}>{c.nombre}</h3>
                    <p className="text-sm text-gray-500 mt-0.5 truncate" title={c.descripcion || 'Sin descripción'}>
                      {c.descripcion || 'Sin descripción'}
                    </p>
                    <div className="flex items-center gap-3 mt-2">
                      <span className="text-xs font-mono text-gray-400">#{c.id}</span>
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-600">
                        <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                        </svg>
                        {c.productCount != null ? c.productCount : 0} producto{c.productCount !== 1 ? 's' : ''}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
