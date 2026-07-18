import { useState, useEffect } from 'react';
import API from '../services/api';

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState({
    nombre: '',
    categoriaId: '',
    precio: '',
    stock: '',
    stockMinimo: '',
  });

  useEffect(() => {
    fetchProductos();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/api/categorias', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(r => r.json())
    .then(data => setCategorias(data))
    .catch(() => {});
  }, []);

  const fetchProductos = async () => {
    try {
      const res = await API.get('/productos');
      setProductos(res.data);
    } catch {
      setProductos([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar este producto?')) return;
    try {
      await API.delete(`/productos/${id}`);
      fetchProductos();
    } catch {
      alert('Error al eliminar el producto');
    }
  };

  const filtered = productos.filter((p) =>
    p.nombre?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Productos</h1>
          <p className="text-sm text-gray-500 mt-1">
            {productos.length} producto{productos.length !== 1 ? 's' : ''} registrado{productos.length !== 1 ? 's' : ''}
          </p>
        </div>
        <button
          onClick={() => {
            setEditing(null);
            setForm({ nombre: '', categoriaId: '', precio: '', stock: '', stockMinimo: '' });
            setShowModal(true);
          }}
          className="btn-primary flex items-center gap-2"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Nuevo Producto
        </button>
      </div>

      <div className="card mb-6">
        <div className="relative">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Buscar por nombre de producto..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input-field pl-10"
          />
        </div>
      </div>

      <div className="card overflow-hidden !p-0">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">ID</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Categoría</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Precio</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Stock</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Stock Mín.</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Estado</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <tr key={i}>
                    {Array.from({ length: 8 }).map((__, j) => (
                      <td key={j} className="px-4 py-3">
                        <div className="h-4 bg-gray-200 rounded animate-pulse" />
                      </td>
                    ))}
                  </tr>
                ))
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-4 py-12 text-center text-gray-500">
                    <svg className="w-12 h-12 mx-auto mb-3 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                    </svg>
                    {search ? 'No se encontraron productos con ese nombre.' : 'No hay productos registrados.'}
                  </td>
                </tr>
              ) : (
                filtered.map((p) => {
                  const bajoStock = p.stock <= p.stockMinimo;
                  return (
                    <tr key={p.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 font-mono text-xs text-gray-500">#{p.id}</td>
                      <td className="px-4 py-3 font-medium text-gray-800">{p.nombre}</td>
                      <td className="px-4 py-3 text-gray-600">{p.categoriaNombre || 'Sin categoría'}</td>
                      <td className="px-4 py-3 text-gray-600">${parseFloat(p.precio).toFixed(2)}</td>
                      <td className="px-4 py-3">
                        <span className={`font-semibold ${bajoStock ? 'text-danger' : 'text-gray-800'}`}>
                          {p.stock}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-gray-600">{p.stockMinimo}</td>
                      <td className="px-4 py-3">
                        {bajoStock ? (
                          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-danger">
                            <span className="w-1.5 h-1.5 rounded-full bg-danger animate-pulse" />
                            Stock Bajo
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-success">
                            <span className="w-1.5 h-1.5 rounded-full bg-success" />
                            Disponible
                          </span>
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => {
                              setEditing(p);
                              setForm({
                                nombre: p.nombre,
                                categoriaId: p.categoriaId?.toString() || '',
                                precio: p.precio.toString(),
                                stock: p.stock.toString(),
                                stockMinimo: p.stockMinimo.toString(),
                              });
                              setShowModal(true);
                            }}
                            className="p-1.5 text-gray-400 hover:text-primary-800 hover:bg-blue-50 rounded-lg transition-colors"
                            title="Editar"
                          >
                            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                              <path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                          </button>
                          <button
                            onClick={() => handleDelete(p.id)}
                            className="p-1.5 text-gray-400 hover:text-danger hover:bg-red-50 rounded-lg transition-colors"
                            title="Eliminar"
                          >
                            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                              <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-lg p-6 mx-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-6">
              {editing ? 'Editar Producto' : 'Nuevo Producto'}
            </h3>
            <form onSubmit={async (e) => {
              e.preventDefault();
              try {
                const payload = {
                  ...form,
                  categoriaId: parseInt(form.categoriaId),
                  precio: parseFloat(form.precio),
                  stock: parseInt(form.stock),
                  stockMinimo: parseInt(form.stockMinimo),
                };
                if (editing) {
                  await API.put(`/productos/${editing.id}`, payload);
                } else {
                  await API.post('/productos', payload);
                }
                setShowModal(false);
                fetchProductos();
              } catch {
                alert('Error al guardar el producto');
              }
            }}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                  <input
                    type="text"
                    value={form.nombre}
                    onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                    className="input-field"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Categoría</label>
                  <select
                    value={form.categoriaId || ''}
                    onChange={e => setForm({...form, categoriaId: parseInt(e.target.value)})}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="">Selecciona una categoría</option>
                    {categorias.map(cat => (
                      <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                    ))}
                  </select>
                </div>
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Precio</label>
                    <input
                      type="number"
                      step="0.01"
                      value={form.precio}
                      onChange={(e) => setForm({ ...form, precio: e.target.value })}
                      className="input-field"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Stock</label>
                    <input
                      type="number"
                      value={form.stock}
                      onChange={(e) => setForm({ ...form, stock: e.target.value })}
                      className="input-field"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Stock Mín.</label>
                    <input
                      type="number"
                      value={form.stockMinimo}
                      onChange={(e) => setForm({ ...form, stockMinimo: e.target.value })}
                      className="input-field"
                      required
                    />
                  </div>
                </div>
              </div>
              <div className="flex justify-end gap-3 mt-8">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                >
                  Cancelar
                </button>
                <button type="submit" className="btn-primary">
                  {editing ? 'Guardar Cambios' : 'Crear Producto'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
