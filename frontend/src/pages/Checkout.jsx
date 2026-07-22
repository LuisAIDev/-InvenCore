import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Elements, PaymentElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';
import { publicoService } from '../services/api';
import { useCart } from '../context/CartContext';

const stripePk = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY || 'pk_test_placeholder';

const stripePromise = loadStripe(stripePk);

function CheckoutForm({ pedidoId, token, onSuccess }) {
  const stripe = useStripe();
  const elements = useElements();
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    setProcessing(true);
    setError(null);

    const { error: submitError } = await elements.submit();
    if (submitError) {
      setError(submitError.message);
      setProcessing(false);
      return;
    }

    const { error: confirmError } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${window.location.origin}/confirmacion?pedidoId=${pedidoId}`,
      },
      redirect: 'if_required',
    });

    if (confirmError) {
      setError(confirmError.message);
      setProcessing(false);
      return;
    }

    try {
      await publicoService.confirmarPago(pedidoId, token);
      onSuccess(pedidoId);
    } catch {
      setError('Error al confirmar el pago. Contacta a soporte.');
      setProcessing(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <PaymentElement />
      {error && <p className="text-sm text-danger bg-red-50 px-4 py-2 rounded-lg">{error}</p>}
      <button
        type="submit"
        disabled={!stripe || processing}
        className="btn-primary w-full disabled:opacity-50"
      >
        {processing ? 'Procesando...' : 'Pagar ahora'}
      </button>
    </form>
  );
}

export default function Checkout() {
  const navigate = useNavigate();
  const { items, total, clearCart } = useCart();
  const [clientSecret, setClientSecret] = useState(null);
  const [pedidoId, setPedidoId] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({ nombre: '', email: '' });

  const crearPedido = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        clienteNombre: form.nombre,
        clienteEmail: form.email,
        items: items.map((i) => ({ productoId: i.id, cantidad: i.cantidad })),
      };
      const res = await publicoService.crearPedido(payload);
      setPedidoId(res.data.id);
      setClientSecret(res.data.clientSecret);
      setToken(res.data.tokenConfirmacion);
    } catch (err) {
      setError(err.response?.data?.message || 'Error al crear el pedido');
      setLoading(false);
    }
  };

  const handleSuccess = (id) => {
    clearCart();
    navigate(`/confirmacion?pedidoId=${id}`);
  };

  if (items.length === 0 && !pedidoId) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-2">Carrito vacío</h2>
          <p className="text-gray-500 mb-4">Agrega productos desde el catálogo</p>
          <Link to="/catalogo" className="btn-primary">Ir al catálogo</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-surface">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <Link to="/catalogo" className="text-sm font-medium text-primary-600 hover:text-primary-800">
            &larr; Seguir comprando
          </Link>
          <span className="text-lg font-bold text-gray-900">Checkout</span>
          <span className="text-sm text-gray-500">{items.length} producto(s)</span>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 py-8">
        {!clientSecret ? (
          <div className="grid grid-cols-1 md:grid-cols-5 gap-8">
            <div className="md:col-span-3">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">Tus datos</h2>
                <form onSubmit={crearPedido} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Nombre completo</label>
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
                    <label className="block text-sm font-medium text-gray-700 mb-1">Correo electrónico</label>
                    <input
                      type="email"
                      required
                      value={form.email}
                      onChange={(e) => setForm({ ...form, email: e.target.value })}
                      className="input-field"
                      placeholder="juan@ejemplo.com"
                    />
                  </div>
                  {error && <p className="text-sm text-danger">{error}</p>}
                  <button type="submit" disabled={loading} className="btn-primary w-full disabled:opacity-50">
                    {loading ? 'Creando pedido...' : 'Continuar al pago'}
                  </button>
                </form>
              </div>
            </div>

            <div className="md:col-span-2">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">Resumen</h2>
                <div className="space-y-3 mb-4">
                  {items.map((item) => (
                    <div key={item.id} className="flex items-center justify-between">
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-800 truncate">{item.nombre}</p>
                        <p className="text-xs text-gray-500">x{item.cantidad}</p>
                      </div>
                      <span className="text-sm font-semibold text-gray-800 tabular-nums">
                        ${(item.precio * item.cantidad).toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="border-t border-gray-100 pt-3 flex items-center justify-between">
                  <span className="text-base font-bold text-gray-900">Total</span>
                  <span className="text-xl font-bold text-gray-900 tabular-nums">
                    ${total.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-5 gap-8">
            <div className="md:col-span-3">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">Pago con tarjeta</h2>
                <Elements stripe={stripePromise} options={{ clientSecret, appearance: { theme: 'stripe' } }}>
                  <CheckoutForm pedidoId={pedidoId} token={token} onSuccess={handleSuccess} />
                </Elements>
              </div>
            </div>

            <div className="md:col-span-2">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">Pedido #{pedidoId}</h2>
                <p className="text-sm text-gray-500 mb-4">Revisa tu pedido antes de pagar</p>
                <div className="space-y-3">
                  {items.map((item) => (
                    <div key={item.id} className="flex items-center justify-between">
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-800 truncate">{item.nombre}</p>
                        <p className="text-xs text-gray-500">x{item.cantidad}</p>
                      </div>
                      <span className="text-sm font-semibold text-gray-800 tabular-nums">
                        ${(item.precio * item.cantidad).toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="border-t border-gray-100 pt-3 mt-3 flex items-center justify-between">
                  <span className="text-base font-bold text-gray-900">Total</span>
                  <span className="text-xl font-bold text-gray-900 tabular-nums">
                    ${total.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                  </span>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
