import { useState, useEffect } from 'react';
import API from '../services/api';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsuarios();
  }, []);

  const fetchUsuarios = async () => {
    try {
      const res = await API.get('/usuarios');
      setUsuarios(res.data.content || res.data);
    } catch {
      setUsuarios([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Usuarios</h1>
          <p className="text-sm text-gray-500 mt-1">Gestión de usuarios del sistema</p>
        </div>
      </div>

      <div className="card overflow-hidden !p-0">
        <div className="hidden sm:block overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">ID</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Email</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Rol</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Estado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr><td colSpan={5} className="px-4 py-12 text-center text-gray-500">Cargando...</td></tr>
              ) : usuarios.length === 0 ? (
                <tr><td colSpan={5} className="px-4 py-12 text-center text-gray-500">No hay usuarios registrados.</td></tr>
              ) : (
                usuarios.map((u) => (
                  <tr key={u.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">#{u.id}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">{u.nombre}</td>
                    <td className="px-4 py-3 text-gray-600">{u.email}</td>
                    <td className="px-4 py-3">
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                        {u.rol}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        u.activo !== false ? 'bg-green-100 text-success' : 'bg-red-100 text-danger'
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${u.activo !== false ? 'bg-success' : 'bg-danger'}`} />
                        {u.activo !== false ? 'Activo' : 'Inactivo'}
                      </span>
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
          ) : usuarios.length === 0 ? (
            <div className="p-4 text-center text-gray-500">No hay usuarios registrados.</div>
          ) : (
            usuarios.map((u) => (
              <div key={u.id} className="p-4 space-y-2">
                <div className="flex items-center justify-between">
                  <div className="min-w-0 flex-1">
                    <p className="font-medium text-gray-900 truncate">{u.nombre}</p>
                    <p className="text-sm text-gray-500 truncate">{u.email}</p>
                  </div>
                  <span className="text-xs text-gray-400 font-mono flex-shrink-0 ml-2">#{u.id}</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                    {u.rol}
                  </span>
                  <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${
                    u.activo !== false ? 'bg-green-100 text-success' : 'bg-red-100 text-danger'
                  }`}>
                    <span className={`w-1.5 h-1.5 rounded-full ${u.activo !== false ? 'bg-success' : 'bg-danger'}`} />
                    {u.activo !== false ? 'Activo' : 'Inactivo'}
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
