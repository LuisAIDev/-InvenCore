import { createContext, useContext, useState, useCallback, useMemo } from 'react';

const CartContext = createContext();

export function CartProvider({ children }) {
  const [items, setItems] = useState(() => {
    try {
      const saved = localStorage.getItem('cart');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  const saveItems = (newItems) => {
    setItems(newItems);
    localStorage.setItem('cart', JSON.stringify(newItems));
  };

  const addItem = useCallback((producto, cantidad = 1) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.id === producto.id);
      let next;
      if (existing) {
        next = prev.map((i) =>
          i.id === producto.id ? { ...i, cantidad: i.cantidad + cantidad } : i
        );
      } else {
        const precio = producto.precioConDescuento ?? producto.precio;
        next = [...prev, { id: producto.id, nombre: producto.nombre, precio, imagenUrl: producto.imagenUrl, cantidad }];
      }
      localStorage.setItem('cart', JSON.stringify(next));
      return next;
    });
  }, []);

  const removeItem = useCallback((productoId) => {
    setItems((prev) => {
      const next = prev.filter((i) => i.id !== productoId);
      localStorage.setItem('cart', JSON.stringify(next));
      return next;
    });
  }, []);

  const updateCantidad = useCallback((productoId, cantidad) => {
    if (cantidad < 1) return;
    setItems((prev) => {
      const next = prev.map((i) => (i.id === productoId ? { ...i, cantidad } : i));
      localStorage.setItem('cart', JSON.stringify(next));
      return next;
    });
  }, []);

  const clearCart = useCallback(() => {
    setItems([]);
    localStorage.removeItem('cart');
  }, []);

  const total = useMemo(() => items.reduce((sum, i) => sum + i.precio * i.cantidad, 0), [items]);
  const count = useMemo(() => items.reduce((sum, i) => sum + i.cantidad, 0), [items]);

  return (
    <CartContext.Provider value={{ items, addItem, removeItem, updateCantidad, clearCart, total, count }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart must be used within CartProvider');
  return ctx;
}
