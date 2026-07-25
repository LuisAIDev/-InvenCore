import { useState, useEffect } from 'react';
import API, { authService, userService } from '../services/api';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ nombre: '', email: '', password: '', rol: 'OPERADOR' });
  const [formError, setFormError] = useState('');
  const [formLoading, setFormLoading] = useState(false);

  const currentEmail = localStorage.getItem('email');

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

  const handleToggleEstado = async (usuario) => {
    const accion = usuario.activo !== false ? 'desactivar' : 'activar';
    if (!confirm(`¿${accion.charAt(0).toUpperCase() + accion.slice(1)} al usuario "${usuario.nombre}"?`)) return;
    try {
      await userService.toggleEstado(usuario.id);
      fetchUsuarios();
    } catch (err) {
      alert(err.response?.data?.message || `Error al ${accion} usuario`);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormLoading(true);
    try {
      await authService.registro(form);
      setShowModal(false);
      setForm({ nombre: '', email: '', password: '', rol: 'OPERADOR' });
      fetchUsuarios();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Error al crear usuario');
    } finally {
      setFormLoading(false);
    }
  };

  const openModal = () => {
    setForm({ nombre: '', email: '', password: '', rol: 'OPERADOR' });
    setFormError('');
    setShowModal(true);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Usuarios</h1>
          <p className="text-sm text-gray-500 mt-1">Gestión de usuarios del sistema</p>
        </div>
        <button onClick={openModal} className="btn-primary text-sm">
          + Crear usuario
        </button>
      </div>

      <div className="card overflow-hidden !p-0">
        <div className="hidden sm:block overflow-x-auto">
          <table className="w-full text-sm table-fixed">
            <colgroup>
              <col className="w-[60px]" />
              <col className="w-[160px]" />
              <col />
              <col className="w-[120px]" />
              <col className="w-[110px]" />
              <col className="w-[100px]" />
            </colgroup>
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">ID</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Nombre</th>
                <th className="px-4 py-3 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Email</th>
                <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Rol</th>
                <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Estado</th>
                <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr><td colSpan={6} className="px-4 py-12 text-center text-gray-500">Cargando...</td></tr>
              ) : usuarios.length === 0 ? (
                <tr><td colSpan={6} className="px-4 py-12 text-center text-gray-500">No hay usuarios registrados.</td></tr>
              ) : (
                usuarios.map((u) => (
                  <tr key={u.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 text-center font-mono text-xs text-gray-500">#{u.id}</td>
                    <td className="px-4 py-3 font-medium text-gray-800 truncate min-w-0" title={u.nombre}>{u.nombre}</td>
                    <td className="px-4 py-3 text-gray-600 truncate min-w-0" title={u.email}>{u.email}</td>
                    <td className="px-4 py-3 text-center">
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                        {u.rol}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-center">
                      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        u.activo !== false ? 'bg-green-100 text-success' : 'bg-red-100 text-danger'
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${u.activo !== false ? 'bg-success' : 'bg-danger'}`} />
                        {u.activo !== false ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-center">
                      {u.email === currentEmail ? (
                        <span className="text-xs text-gray-400" title="Tu cuenta">—</span>
                      ) : (
                        <button
                          onClick={() => handleToggleEstado(u)}
                          className={`text-xs font-medium px-2.5 py-1 rounded-lg transition-colors ${
                            u.activo !== false
                              ? 'text-red-700 bg-red-50 hover:bg-red-100'
                              : 'text-green-700 bg-green-50 hover:bg-green-100'
                          }`}
                        >
                          {u.activo !== false ? 'Desactivar' : 'Activar'}
                        </button>
                      )}
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
                  {u.email !== currentEmail && (
                    <button
                      onClick={() => handleToggleEstado(u)}
                      className={`text-xs font-medium px-2 py-0.5 rounded-lg transition-colors ${
                        u.activo !== false
                          ? 'text-red-700 bg-red-50 hover:bg-red-100'
                          : 'text-green-700 bg-green-50 hover:bg-green-100'
                      }`}
                    >
                      {u.activo !== false ? 'Desactivar' : 'Activar'}
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md mx-4 p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Crear usuario</h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                <input
                  type="text"
                  required
                  value={form.nombre}
                  onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                  className="input-field"
                  placeholder="Juan Pérez"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input
                  type="email"
                  required
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  className="input-field"
                  placeholder="usuario@invencore.com"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
                <input
                  type="password"
                  required
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                  className="input-field"
                  placeholder="Mínimo 6 caracteres"
                  minLength={6}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Rol</label>
                <select
                  value={form.rol}
                  onChange={(e) => setForm({ ...form, rol: e.target.value })}
                  className="input-field"
                >
                  <option value="OPERADOR">OPERADOR</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </div>
              {formError && <p className="text-sm text-danger">{formError}</p>}
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="btn-secondary flex-1">
                  Cancelar
                </button>
                <button type="submit" disabled={formLoading} className="btn-primary flex-1 disabled:opacity-50">
                  {formLoading ? 'Creando...' : 'Crear usuario'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
