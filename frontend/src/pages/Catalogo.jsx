import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { publicoService } from '../services/api';
import ProductThumb from '../components/common/ProductThumb';
import { useCart } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';

function ProductCard({ producto }) {
  const { addItem, count } = useCart();
  const navigate = useNavigate();
  const tieneOferta = producto.precioOriginal != null;
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow duration-200 flex flex-col">
      <div className={`h-48 relative ${tieneOferta ? 'bg-gradient-to-br from-red-100 to-red-200' : 'bg-gradient-to-br from-primary-100 to-primary-200'} flex items-center justify-center`}>
        {producto.imagenUrl ? (
          <ProductThumb url={producto.imagenUrl} className="w-full h-full !rounded-none" />
        ) : (
          <svg className="w-16 h-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        )}
        {tieneOferta && (
          <span className="absolute top-3 right-3 bg-danger text-white text-xs font-bold px-2.5 py-1 rounded-full">
            -{producto.porcentajeDescuento}% OFERTA
          </span>
        )}
      </div>
      <div className="p-5 flex flex-col flex-1">
        <div className="flex items-center justify-between mb-2">
          <span className="text-xs font-medium text-primary-600 bg-primary-50 px-2.5 py-1 rounded-full">
            {producto.categoriaNombre}
          </span>
          {producto.disponible && (
            <span className="text-xs font-semibold text-success bg-green-50 px-2.5 py-1 rounded-full flex items-center gap-1">
              <span className="w-1.5 h-1.5 bg-success rounded-full inline-block" />
              Disponible
            </span>
          )}
        </div>
        <h3 className="text-lg font-semibold text-gray-800 mb-1">{producto.nombre}</h3>
        {producto.descripcion && (
          <p className="text-sm text-gray-500 mb-3 line-clamp-2 flex-1">{producto.descripcion}</p>
        )}
          <div className="flex items-end justify-between mt-auto pt-3 border-t border-gray-100">
            <div>
              {tieneOferta ? (
                <div className="flex flex-col">
                  <span className="text-sm line-through text-gray-400">
                    ${producto.precioOriginal?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                  </span>
                  <span className="text-2xl font-bold text-danger">
                    ${producto.precioConDescuento?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                  </span>
                </div>
              ) : (
                <span className="text-2xl font-bold text-gray-900">
                  ${producto.precio?.toLocaleString('es-MX', { minimumFractionDigits: 2 })}
                </span>
              )}
            </div>
            <button
              onClick={(e) => { e.stopPropagation(); addItem(producto, 1); }}
              className="flex-shrink-0 w-9 h-9 bg-primary-600 hover:bg-primary-700 text-white rounded-lg flex items-center justify-center transition-colors"
              title="Agregar al carrito"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    );
  }

export default function Catalogo() {
  const { count } = useCart();
  const navigate = useNavigate();
  const [productos, setProductos] = useState({ content: [], totalPages: 0, totalElements: 0, number: 0 });
  const [categorias, setCategorias] = useState([]);
  const [categoriaId, setCategoriaId] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const size = 8;

  useEffect(() => {
    publicoService.listarCategorias().then((res) => setCategorias(res.data)).catch(() => {});
  }, []);

  useEffect(() => {
    const params = { page, size };
    if (categoriaId) params.categoriaId = categoriaId;
    publicoService.listarProductos(params)
      .then((res) => setProductos(res.data))
      .catch(() => setProductos({ content: [], totalPages: 0, totalElements: 0, number: 0 }))
      .finally(() => setLoading(false));
  }, [page, categoriaId]);

  const handleCategoriaChange = (e) => {
    setCategoriaId(e.target.value);
    setPage(0);
  };

  return (
    <div className="min-h-screen bg-surface font-inter">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="h-16 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
                <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <span className="text-xl font-bold text-gray-900 tracking-tight">InvenCore</span>
              <span className="hidden sm:inline text-sm text-gray-400 ml-2">Catálogo de Productos</span>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={() => navigate('/checkout')}
                className="relative p-2 text-gray-500 hover:text-primary-600 transition-colors"
                title="Carrito"
              >
                <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
                </svg>
                {count > 0 && (
                  <span className="absolute -top-1 -right-1 w-5 h-5 bg-danger text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                    {count > 99 ? '99+' : count}
                  </span>
                )}
              </button>
              <Link
                to="/login"
                className="text-sm font-medium text-primary-600 hover:text-primary-800 transition-colors"
              >
                Iniciar Sesión
              </Link>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold text-gray-900">Catálogo de Productos</h1>
            <p className="text-gray-500 mt-1">Explora nuestros productos disponibles</p>
          </div>
          <div className="w-full sm:w-64">
            <select
              value={categoriaId}
              onChange={handleCategoriaChange}
              className="input-field"
            >
              <option value="">Todas las categorías</option>
              {categorias.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.nombre}</option>
              ))}
            </select>
          </div>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {Array.from({ length: size }).map((_, i) => (
              <div key={i} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden animate-pulse">
                <div className="h-48 bg-gray-200" />
                <div className="p-5 space-y-3">
                  <div className="h-4 bg-gray-200 rounded w-1/3" />
                  <div className="h-5 bg-gray-200 rounded w-3/4" />
                  <div className="h-4 bg-gray-200 rounded w-full" />
                  <div className="h-6 bg-gray-200 rounded w-1/4" />
                </div>
              </div>
            ))}
          </div>
        ) : productos.content.length === 0 ? (
          <div className="text-center py-20">
            <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
            <h3 className="text-lg font-medium text-gray-500">No hay productos disponibles</h3>
            <p className="text-gray-400 mt-1">Intenta con otra categoría</p>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {productos.content.map((p) => (
                <ProductCard key={p.id} producto={p} />
              ))}
            </div>

            {productos.totalPages > 1 && (
              <div className="flex items-center justify-center gap-2 mt-10">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="btn-primary disabled:opacity-30"
                >
                  Anterior
                </button>
                {Array.from({ length: productos.totalPages }, (_, i) => (
                  <button
                    key={i}
                    onClick={() => setPage(i)}
                    className={`w-10 h-10 rounded-lg text-sm font-medium transition-colors ${
                      i === page
                        ? 'bg-primary-800 text-white'
                        : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}
                <button
                  onClick={() => setPage((p) => Math.min(productos.totalPages - 1, p + 1))}
                  disabled={page >= productos.totalPages - 1}
                  className="btn-primary disabled:opacity-30"
                >
                  Siguiente
                </button>
              </div>
            )}
          </>
        )}
      </main>

      <footer className="border-t border-gray-200 bg-white mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 text-center text-sm text-gray-400">
          InvenCore v1.0 — Sistema de Inventario Empresarial
        </div>
      </footer>
    </div>
  );
}
