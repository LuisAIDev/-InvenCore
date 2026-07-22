import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Catalogo from './pages/Catalogo';
import Checkout from './pages/Checkout';
import Confirmacion from './pages/Confirmacion';
import Dashboard from './pages/Dashboard';
import Productos from './pages/Productos';
import Categorias from './pages/Categorias';
import Movimientos from './pages/Movimientos';
import Usuarios from './pages/Usuarios';
import Ofertas from './pages/Ofertas';
import AdminPedidos from './pages/AdminPedidos';
import Layout from './components/layout/Layout';
import OperadorDashboard from './pages/OperadorDashboard';
import RegistrarMovimiento from './pages/RegistrarMovimiento';
import { CartProvider } from './context/CartContext';

const PrivateRoute = ({ children, roles }) => {
  const token = localStorage.getItem('token');
  const rol = localStorage.getItem('rol');
  if (!token) return <Navigate to="/login" />;
  if (roles && !roles.includes(rol)) return <Navigate to="/" />;
  return children;
};

const ProtectedPage = ({ Page, roles }) => (
  <PrivateRoute roles={roles}>
    <Layout>
      <Page />
    </Layout>
  </PrivateRoute>
);

function App() {
  return (
    <BrowserRouter>
      <CartProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/catalogo" element={<Catalogo />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/confirmacion" element={<Confirmacion />} />
        <Route path="/" element={<ProtectedPage Page={Dashboard} roles={['ADMIN']} />} />
        <Route path="/productos" element={<ProtectedPage Page={Productos} roles={['ADMIN']} />} />
        <Route path="/categorias" element={<ProtectedPage Page={Categorias} roles={['ADMIN']} />} />
        <Route path="/movimientos" element={<ProtectedPage Page={Movimientos} roles={['ADMIN']} />} />
        <Route path="/usuarios" element={<ProtectedPage Page={Usuarios} roles={['ADMIN']} />} />
        <Route path="/ofertas" element={<ProtectedPage Page={Ofertas} roles={['ADMIN']} />} />
        <Route path="/operador" element={
          <PrivateRoute roles={['OPERADOR', 'ADMIN']}>
            <OperadorDashboard />
          </PrivateRoute>
        } />
        <Route path="/operador/movimiento" element={
          <PrivateRoute roles={['OPERADOR', 'ADMIN']}>
            <RegistrarMovimiento />
          </PrivateRoute>
        } />
        <Route path="/pedidos" element={<ProtectedPage Page={AdminPedidos} roles={['ADMIN']} />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
      </CartProvider>
    </BrowserRouter>
  );
}

export default App;
