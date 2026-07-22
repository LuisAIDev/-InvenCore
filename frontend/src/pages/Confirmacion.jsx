import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { publicoService } from '../services/api';

export default function Confirmacion() {
  const [searchParams] = useSearchParams();
  const pedidoId = searchParams.get('pedidoId');
  const [pedido, setPedido] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!pedidoId) { setLoading(false); return; }
    publicoService.obtenerPedido(pedidoId)
      .then((res) => setPedido(res.data))
      .catch(() => setPedido(null))
      .finally(() => setLoading(false));
  }, [pedidoId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <div className="animate-spin w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full" />
      </div>
    );
  }

  if (!pedido) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Pedido no encontrado</h2>
          <Link to="/catalogo" className="btn-primary mt-4">Ir al catálogo</Link>
        </div>
      </div>
    );
  }

  const esExitoso = pedido.estado === 'PAGADO';

  return (
    <div className="min-h-screen bg-surface">
      <header className="bg-white border-b border-gray-200">
        <div className="max-w-2xl mx-auto px-4 sm:px-6 h-16 flex items-center">
          <span className="text-lg font-bold text-gray-900">InvenCore</span>
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-4 sm:px-6 py-12">
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center">
          {esExitoso ? (
            <div className="w-16 h-16 bg-success/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>
          ) : (
            <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.77-.833-2.694-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
            </div>
          )}

          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {esExitoso ? '¡Pago confirmado!' : 'Pago pendiente'}
          </h1>
          <p className="text-gray-500 mb-6">
            {esExitoso
              ? 'Tu pedido ha sido procesado exitosamente. Recibirás un correo con los detalles.'
              : 'Tu pedido está siendo procesado. Te notificaremos cuando se confirme el pago.'}
          </p>

          <div className="bg-gray-50 rounded-lg p-4 mb-6 text-left space-y-2">
            <p className="text-sm text-gray-600">
              <span className="font-medium">Pedido:</span> #{pedido.id}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">Cliente:</span> {pedido.clienteNombre}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">Email:</span> {pedido.clienteEmail}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">Estado:</span>{' '}
              <span className={`font-semibold ${esExitoso ? 'text-success' : 'text-yellow-600'}`}>
                {pedido.estado}
              </span>
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-medium">Total:</span>{' '}
              <span className="font-semibold tabular-nums">
                ${pedido.total?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
              </span>
            </p>
          </div>

          <div className="space-y-3">
            {pedido.items?.map((item) => (
              <div key={item.id} className="flex items-center justify-between text-sm">
                <span className="text-gray-700">{item.productoNombre} x{item.cantidad}</span>
                <span className="font-semibold text-gray-800 tabular-nums">
                  ${item.subtotal?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                </span>
              </div>
            ))}
          </div>

          <Link to="/catalogo" className="btn-primary mt-8 inline-block">
            Seguir comprando
          </Link>
        </div>
      </main>
    </div>
  );
}
