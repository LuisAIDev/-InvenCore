import { useState, useEffect } from 'react';
import API from '../services/api';
import { Cpu, Hammer, SprayCan, Armchair, Building2, FileText, HardHat, Server, Package } from 'lucide-react';

const normalizeName = (name) =>
  name.toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9\s]/g, '');

const categoryIcons = [
  { keys: ['electronica'], Icon: Cpu, color: 'bg-purple-100 text-purple-700' },
  { keys: ['herramientas'], Icon: Hammer, color: 'bg-amber-100 text-amber-700' },
  { keys: ['limpieza'], Icon: SprayCan, color: 'bg-cyan-100 text-cyan-700' },
  { keys: ['mobiliario'], Icon: Armchair, color: 'bg-orange-100 text-orange-700' },
  { keys: ['oficina'], Icon: Building2, color: 'bg-green-100 text-green-700' },
  { keys: ['papeleria'], Icon: FileText, color: 'bg-blue-100 text-blue-600' },
  { keys: ['seguridad'], Icon: HardHat, color: 'bg-red-100 text-red-700' },
  { keys: ['tecnologia'], Icon: Server, color: 'bg-slate-100 text-slate-700' },
];

const defaultIcon = { Icon: Package, color: 'bg-blue-100 text-blue-700' };

const getCategoryInfo = (nombre) => {
  const normalized = normalizeName(nombre);
  for (const entry of categoryIcons) {
    if (entry.keys.some((k) => normalized.includes(k))) return entry;
  }
  return defaultIcon;
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
                    <icon.Icon className="w-5 h-5" strokeWidth={1.5} />
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
