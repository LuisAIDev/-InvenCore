import { useState, useEffect } from 'react';
import { pedidoService } from '../services/api';

const estadoBadge = {
  PENDIENTE: 'bg-yellow-100 text-yellow-800',
  PAGADO: 'bg-green-100 text-green-800',
  CANCELADO: 'bg-red-100 text-red-800',
};

export default function AdminPedidos() {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    pedidoService.listarTodos()
      .then((res) => setPedidos(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleCancelar = async (id) => {
    if (!confirm('¿Cancelar este pedido?')) return;
    try {
      const res = await pedidoService.cancelar(id);
      setPedidos((prev) => prev.map((p) => (p.id === id ? res.data : p)));
    } catch {
      alert('Error al cancelar el pedido');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full" />
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Pedidos</h1>
        <span className="text-sm text-gray-500">{pedidos.length} pedido(s)</span>
      </div>

      {pedidos.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-lg font-medium">No hay pedidos registrados</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full table-fixed border-collapse">
            <colgroup>
              <col className="w-[80px]" />
              <col className="w-[160px]" />
              <col className="w-[200px]" />
              <col className="w-[100px]" />
              <col className="w-[120px]" />
              <col className="w-[100px]" />
            </colgroup>
            <thead>
              <tr className="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider border-b border-gray-200">
                <th className="pb-3">#</th>
                <th className="pb-3">Cliente</th>
                <th className="pb-3">Email</th>
                <th className="pb-3 text-right">Total</th>
                <th className="pb-3 text-center">Estado</th>
                <th className="pb-3 text-right">Acción</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map((p) => (
                <>
                  <tr
                    key={p.id}
                    onClick={() => setExpanded(expanded === p.id ? null : p.id)}
                    className="border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors"
                  >
                    <td className="py-3 text-sm font-mono text-gray-500">{p.id}</td>
                    <td className="py-3 text-sm font-medium text-gray-800 truncate">{p.clienteNombre}</td>
                    <td className="py-3 text-sm text-gray-500 truncate">{p.clienteEmail}</td>
                    <td className="py-3 text-sm font-semibold text-gray-800 tabular-nums text-right">
                      ${p.total?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="py-3 text-center">
                      <span className={`inline-block px-2.5 py-1 rounded-full text-xs font-semibold ${estadoBadge[p.estado] || 'bg-gray-100 text-gray-600'}`}>
                        {p.estado}
                      </span>
                    </td>
                    <td className="py-3 text-right">
                      {p.estado === 'PENDIENTE' && (
                        <button
                          onClick={(e) => { e.stopPropagation(); handleCancelar(p.id); }}
                          className="text-xs text-danger hover:text-danger/80 font-medium"
                        >
                          Cancelar
                        </button>
                      )}
                    </td>
                  </tr>
                  {expanded === p.id && (
                    <tr key={`${p.id}-detail`} className="bg-gray-50">
                      <td colSpan={6} className="px-6 py-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                          <div>
                            <h4 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Detalles del pedido</h4>
                            <div className="space-y-1 text-sm">
                              <p><span className="text-gray-500">Fecha:</span> {new Date(p.fechaCreacion).toLocaleString('es-MX')}</p>
                              <p><span className="text-gray-500">Estado:</span> {p.estado}</p>
                              {p.pago && (
                                <>
                                  <p><span className="text-gray-500">Pago ID:</span> <span className="font-mono text-xs">{p.pago.paymentIntentId}</span></p>
                                  <p><span className="text-gray-500">Pago estado:</span> {p.pago.estado}</p>
                                </>
                              )}
                            </div>
                          </div>
                          <div>
                            <h4 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Productos</h4>
                            <div className="space-y-2">
                              {p.items?.map((item) => (
                                <div key={item.id} className="flex items-center justify-between text-sm">
                                  <span className="text-gray-700 truncate">{item.productoNombre} x{item.cantidad}</span>
                                  <span className="font-semibold text-gray-800 tabular-nums ml-2">
                                    ${item.subtotal?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                                  </span>
                                </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
