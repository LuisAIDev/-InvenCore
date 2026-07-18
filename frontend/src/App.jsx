import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Productos from './pages/Productos';
import Categorias from './pages/Categorias';
import Movimientos from './pages/Movimientos';
import Usuarios from './pages/Usuarios';
import Layout from './components/layout/Layout';
import OperadorDashboard from './pages/OperadorDashboard';
import RegistrarMovimiento from './pages/RegistrarMovimiento';

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
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<ProtectedPage Page={Dashboard} roles={['ADMIN']} />} />
        <Route path="/productos" element={<ProtectedPage Page={Productos} roles={['ADMIN']} />} />
        <Route path="/categorias" element={<ProtectedPage Page={Categorias} roles={['ADMIN']} />} />
        <Route path="/movimientos" element={<ProtectedPage Page={Movimientos} roles={['ADMIN']} />} />
        <Route path="/usuarios" element={<ProtectedPage Page={Usuarios} roles={['ADMIN']} />} />
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
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
