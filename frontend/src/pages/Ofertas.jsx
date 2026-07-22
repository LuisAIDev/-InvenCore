import { useState, useEffect } from 'react';
import API from '../services/api';

export default function Ofertas() {
  const [ofertas, setOfertas] = useState([]);
  const [productos, setProductos] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({
    nombre: '',
    porcentajeDescuento: '',
    fechaInicio: '',
    fechaFin: '',
    activa: true,
    productoIds: [],
  });

  const fetchOfertas = () => {
    API.get('/ofertas')
      .then((res) => setOfertas(res.data || []))
      .catch(() => setOfertas([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchOfertas();
  }, []);

  useEffect(() => {
    API.get('/productos/todos', { params: { size: 999 } })
      .then((res) => setProductos(res.data.content || res.data || []))
      .catch(() => {});
  }, []);

  const openCreate = () => {
    setEditing(null);
    setForm({ nombre: '', porcentajeDescuento: '', fechaInicio: '', fechaFin: '', activa: true, productoIds: [] });
    setShowModal(true);
  };

  const openEdit = (oferta) => {
    setEditing(oferta);
    setForm({
      nombre: oferta.nombre,
      porcentajeDescuento: oferta.porcentajeDescuento,
      fechaInicio: oferta.fechaInicio?.slice(0, 16),
      fechaFin: oferta.fechaFin?.slice(0, 16),
      activa: oferta.activa,
      productoIds: oferta.productoIds || [],
    });
    setShowModal(true);
  };

  const toggleProducto = (id) => {
    setForm((prev) => ({
      ...prev,
      productoIds: prev.productoIds.includes(id)
        ? prev.productoIds.filter((pid) => pid !== id)
        : [...prev.productoIds, id],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const body = {
      ...form,
      porcentajeDescuento: parseFloat(form.porcentajeDescuento),
      fechaInicio: form.fechaInicio ? `${form.fechaInicio}:00` : null,
      fechaFin: form.fechaFin ? `${form.fechaFin}:00` : null,
    };
    try {
      if (editing) {
        await API.put(`/ofertas/${editing.id}`, body);
      } else {
        await API.post('/ofertas', body);
      }
      setShowModal(false);
      fetchOfertas();
    } catch {
      alert('Error al guardar la oferta');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar esta oferta?')) return;
    try {
      await API.delete(`/ofertas/${id}`);
      fetchOfertas();
    } catch {
      alert('Error al eliminar la oferta');
    }
  };

  const formatDate = (d) => {
    if (!d) return '';
    const dt = new Date(d);
    return dt.toLocaleDateString('es-MX', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 sm:mb-8">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Ofertas</h1>
          <p className="text-sm text-gray-500 mt-1">{ofertas.length} oferta{ofertas.length !== 1 ? 's' : ''}</p>
        </div>
        <button onClick={openCreate} className="btn-primary flex items-center gap-2 self-start sm:self-auto">
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Nueva Oferta
        </button>
      </div>

      {loading ? (
        <div className="card animate-pulse space-y-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-12 bg-gray-200 rounded" />
          ))}
        </div>
      ) : ofertas.length === 0 ? (
        <div className="card text-center py-12">
          <svg className="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p className="text-gray-500">No hay ofertas registradas</p>
          <button onClick={openCreate} className="text-primary-600 hover:text-primary-800 font-medium mt-2">Crear primera oferta</button>
        </div>
      ) : (
        <div className="card overflow-hidden !p-0">
          <div className="hidden sm:block overflow-x-auto">
            <table className="w-full text-sm table-fixed">
              <colgroup>
                <col />
                <col className="w-[120px]" />
                <col className="w-[170px]" />
                <col className="w-[170px]" />
                <col className="w-[100px]" />
                <col className="w-[120px]" />
                <col className="w-[130px]" />
              </colgroup>
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr>
                  <th className="px-4 py-3 text-left font-semibold text-gray-600 text-xs uppercase tracking-wider">Nombre</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">% Descuento</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Inicio</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Fin</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Estado</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Productos</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600 text-xs uppercase tracking-wider">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {ofertas.map((o) => (
                  <tr key={o.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-medium text-gray-900 truncate min-w-0" title={o.nombre}>{o.nombre}</td>
                    <td className="px-4 py-3 text-center"><span className="text-danger font-bold">-{o.porcentajeDescuento}%</span></td>
                    <td className="px-4 py-3 text-center text-gray-600 text-xs whitespace-nowrap">{formatDate(o.fechaInicio)}</td>
                    <td className="px-4 py-3 text-center text-gray-600 text-xs whitespace-nowrap">{formatDate(o.fechaFin)}</td>
                    <td className="px-4 py-3 text-center">
                      {o.activa ? (
                        <span className="text-xs font-semibold text-success bg-green-50 px-2.5 py-1 rounded-full">Activa</span>
                      ) : (
                        <span className="text-xs font-semibold text-gray-400 bg-gray-100 px-2.5 py-1 rounded-full">Inactiva</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center text-gray-600 text-sm">{(o.productoIds || []).length} producto(s)</td>
                    <td className="px-4 py-3 text-center">
                      <div className="inline-flex items-center gap-2">
                        <button onClick={() => openEdit(o)} className="text-primary-600 hover:text-primary-800 text-sm font-medium">Editar</button>
                        <button onClick={() => handleDelete(o.id)} className="text-danger hover:text-red-700 text-sm font-medium">Eliminar</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="sm:hidden divide-y divide-gray-100">
            {ofertas.map((o) => (
              <div key={o.id} className="p-4 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-gray-900">{o.nombre}</span>
                  <span className="text-danger font-bold">-{o.porcentajeDescuento}%</span>
                </div>
                <div className="flex items-center justify-between text-xs text-gray-500">
                  <span>{formatDate(o.fechaInicio)} → {formatDate(o.fechaFin)}</span>
                  {o.activa ? (
                    <span className="text-success font-semibold">Activa</span>
                  ) : (
                    <span className="text-gray-400 font-semibold">Inactiva</span>
                  )}
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-xs text-gray-500">{(o.productoIds || []).length} producto(s)</span>
                  <div className="flex gap-3">
                    <button onClick={() => openEdit(o)} className="text-primary-600 text-xs font-medium">Editar</button>
                    <button onClick={() => handleDelete(o.id)} className="text-danger text-xs font-medium">Eliminar</button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-xl shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <div className="p-6 border-b border-gray-100">
              <h2 className="text-lg font-bold text-gray-900">{editing ? 'Editar Oferta' : 'Nueva Oferta'}</h2>
            </div>
            <form onSubmit={handleSubmit} className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                <input type="text" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} className="input-field" required />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">% Descuento</label>
                <input type="number" step="0.01" min="0" max="100" value={form.porcentajeDescuento} onChange={(e) => setForm({ ...form, porcentajeDescuento: e.target.value })} className="input-field" required />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Inicio</label>
                  <input type="datetime-local" value={form.fechaInicio} onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })} className="input-field" required />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Fin</label>
                  <input type="datetime-local" value={form.fechaFin} onChange={(e) => setForm({ ...form, fechaFin: e.target.value })} className="input-field" required />
                </div>
              </div>

              <div className="flex items-center gap-3">
                <input type="checkbox" id="activa" checked={form.activa} onChange={(e) => setForm({ ...form, activa: e.target.checked })} className="w-4 h-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500" />
                <label htmlFor="activa" className="text-sm font-medium text-gray-700">Oferta activa</label>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Productos en oferta</label>
                <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-lg divide-y divide-gray-100">
                  {productos
                    .filter((p) => p.activo !== false)
                    .map((p) => (
                      <label key={p.id} className="flex items-center gap-3 px-4 py-2.5 hover:bg-gray-50 cursor-pointer text-sm">
                        <input
                          type="checkbox"
                          checked={form.productoIds.includes(p.id)}
                          onChange={() => toggleProducto(p.id)}
                          className="w-4 h-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                        />
                        <span className="text-gray-700">{p.nombre}</span>
                        <span className="text-gray-400 ml-auto">${p.precio?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}</span>
                      </label>
                    ))}
                </div>
                {productos.length === 0 && <p className="text-sm text-gray-400">No hay productos disponibles</p>}
              </div>

              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setShowModal(false)} className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">Cancelar</button>
                <button type="submit" className="btn-primary">
                  {editing ? 'Guardar Cambios' : 'Crear Oferta'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
