import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productoService, movimientoService } from '../services/api';

export default function RegistrarMovimiento() {
  const navigate = useNavigate();
  const nombre = localStorage.getItem('nombre') || 'Operador';

  const [productos, setProductos] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [tipo, setTipo] = useState(null);
  const [cantidad, setCantidad] = useState(1);
  const [descripcion, setDescripcion] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    productoService.listarActivos({ params: { size: 1000 } })
      .then((res) => setProductos(res.data.content || res.data))
      .catch(() => setProductos([]));
  }, []);

  const filtered = productos.filter((p) =>
    p.nombre.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSelectProduct = (prod) => {
    setSelectedProduct(prod);
    setSearchTerm('');
  };

  const handleTipo = (val) => {
    setTipo(val);
    setError('');
  };

  const adjustCantidad = (delta) => {
    setCantidad((c) => Math.max(1, c + delta));
  };

  const newStock = selectedProduct
    ? tipo === 'ENTRADA'
      ? selectedProduct.stock + cantidad
      : tipo === 'SALIDA'
        ? Math.max(0, selectedProduct.stock - cantidad)
        : selectedProduct.stock
    : null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedProduct) { setError('Selecciona un producto'); return; }
    if (!tipo) { setError('Selecciona ENTRADA o SALIDA'); return; }
    if (tipo === 'SALIDA' && cantidad > selectedProduct.stock) {
      setError('No hay suficiente stock para esta salida');
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      await movimientoService.registrar({
        productoId: selectedProduct.id,
        tipo,
        cantidad,
        descripcion,
      });
      setSuccess(true);
      setTimeout(() => navigate('/operador'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar el movimiento');
    } finally {
      setSubmitting(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: '#0f172a' }}>
        <div className="text-center">
          <div className="w-20 h-20 bg-emerald-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg className="w-10 h-10 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-white mb-2">Movimiento Registrado</h2>
          <p className="text-gray-400">Redirigiendo al panel...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen" style={{ backgroundColor: '#0f172a' }}>
      {/* Header */}
      <header className="bg-gradient-to-r from-blue-900 via-blue-800 to-purple-900 px-6 py-5 shadow-2xl">
        <div className="max-w-3xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('/operador')}
              className="w-10 h-10 bg-white/10 rounded-xl flex items-center justify-center hover:bg-white/20 transition-colors"
            >
              <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <div>
              <h1 className="text-xl font-bold text-white">Registrar Movimiento</h1>
              <p className="text-xs text-blue-200">Entrada o salida de inventario</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-sm text-blue-200">{nombre}</span>
            <div className="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
              <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-3xl mx-auto px-6 py-8">
        <form onSubmit={handleSubmit} className="rounded-2xl p-8 space-y-7 border border-white/5" style={{ backgroundColor: '#1e293b' }}>
          {/* Selector de Producto */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Producto</label>
            <div className="relative">
              <svg className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                type="text"
                value={selectedProduct ? selectedProduct.nombre : searchTerm}
                onChange={(e) => { setSelectedProduct(null); setSearchTerm(e.target.value); }}
                placeholder="Buscar producto..."
                className="w-full pl-12 pr-4 py-3 rounded-xl text-white placeholder-gray-500 border focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
              />
              {searchTerm && !selectedProduct && (
                <div
                  className="absolute top-full left-0 right-0 mt-1 rounded-xl border z-20 max-h-48 overflow-y-auto shadow-2xl"
                  style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
                >
                  {filtered.length === 0 ? (
                    <p className="px-4 py-3 text-sm text-gray-500">Sin resultados</p>
                  ) : (
                    filtered.map((p) => (
                      <button
                        key={p.id}
                        type="button"
                        onClick={() => handleSelectProduct(p)}
                        className="w-full text-left px-4 py-3 text-sm text-gray-300 hover:bg-blue-600/20 hover:text-white transition-colors border-b border-white/5 last:border-0"
                      >
                        <span className="font-medium">{p.nombre}</span>
                        <span className="text-gray-500 ml-2">Stock: {p.stock}</span>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
            {selectedProduct && (
              <div className="mt-3 flex items-center gap-3 text-sm" style={{ color: '#94a3b8' }}>
                <span className="px-2.5 py-1 rounded-lg bg-blue-500/10 text-blue-400 border border-blue-500/20">
                  Stock actual: {selectedProduct.stock}
                </span>
                {selectedProduct.categoria && (
                  <span className="px-2.5 py-1 rounded-lg bg-purple-500/10 text-purple-400 border border-purple-500/20">
                    {selectedProduct.categoria.nombre}
                  </span>
                )}
              </div>
            )}
          </div>

          {/* Tipo de Movimiento */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Tipo de Movimiento</label>
            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                onClick={() => handleTipo('ENTRADA')}
                className={`relative py-4 rounded-xl font-bold text-lg transition-all duration-300 border-2 ${
                  tipo === 'ENTRADA'
                    ? 'bg-emerald-500 text-white border-emerald-400 shadow-lg shadow-emerald-500/30 scale-[1.02]'
                    : 'bg-transparent text-gray-400 border-gray-700 hover:border-emerald-500/50 hover:text-emerald-300'
                }`}
              >
                <svg className="w-6 h-6 mx-auto mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                </svg>
                ENTRADA
              </button>
              <button
                type="button"
                onClick={() => handleTipo('SALIDA')}
                className={`relative py-4 rounded-xl font-bold text-lg transition-all duration-300 border-2 ${
                  tipo === 'SALIDA'
                    ? 'bg-red-500 text-white border-red-400 shadow-lg shadow-red-500/30 scale-[1.02]'
                    : 'bg-transparent text-gray-400 border-gray-700 hover:border-red-500/50 hover:text-red-300'
                }`}
              >
                <svg className="w-6 h-6 mx-auto mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M20 12H4" />
                </svg>
                SALIDA
              </button>
            </div>
          </div>

          {/* Cantidad */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Cantidad</label>
            <div className="flex items-center gap-4">
              <button
                type="button"
                onClick={() => adjustCantidad(-1)}
                className="w-12 h-12 rounded-xl text-white text-xl font-bold transition-all hover:scale-105"
                style={{ backgroundColor: '#334155' }}
              >
                −
              </button>
              <input
                type="number"
                value={cantidad}
                onChange={(e) => setCantidad(Math.max(1, parseInt(e.target.value) || 1))}
                className="w-24 text-center py-3 rounded-xl text-white text-xl font-bold focus:outline-none focus:ring-2 focus:ring-blue-500"
                style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
                min="1"
              />
              <button
                type="button"
                onClick={() => adjustCantidad(1)}
                className="w-12 h-12 rounded-xl text-white text-xl font-bold transition-all hover:scale-105"
                style={{ backgroundColor: '#334155' }}
              >
                +
              </button>
            </div>
          </div>

          {/* Descripción */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Descripción (opcional)</label>
            <textarea
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              rows={3}
              placeholder="Motivo del movimiento..."
              className="w-full px-4 py-3 rounded-xl text-white placeholder-gray-500 border focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all resize-none"
              style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
            />
          </div>

          {/* Preview Stock */}
          {selectedProduct && tipo && (
            <div className="rounded-xl p-5 border" style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}>
              <p className="text-sm font-medium text-gray-400 mb-3">Vista previa del stock</p>
              <div className="flex items-center justify-between">
                <div className="text-center">
                  <p className="text-xs text-gray-500 mb-1">Actual</p>
                  <p className="text-2xl font-bold text-white">{selectedProduct.stock}</p>
                </div>
                <svg className="w-6 h-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
                <div className="text-center">
                  <p className="text-xs text-gray-500 mb-1">Nuevo</p>
                  <p className={`text-2xl font-bold ${newStock > 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                    {newStock}
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Error */}
          {error && (
            <div className="bg-red-500/10 border border-red-500/30 text-red-400 px-4 py-3 rounded-xl text-sm">
              {error}
            </div>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={submitting}
            className="w-full py-4 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 disabled:opacity-50 text-white font-bold text-lg rounded-xl transition-all duration-300 shadow-lg shadow-blue-900/30"
          >
            {submitting ? (
              <span className="flex items-center justify-center gap-2">
                <svg className="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Registrando...
              </span>
            ) : (
              'Confirmar Movimiento'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
