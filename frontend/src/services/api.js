import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  registro: (data) => api.post('/auth/registro', data),
};

export const productoService = {
  listarActivos: (params) => api.get('/productos', { params }),
  listarTodos: (params) => api.get('/productos/todos', { params }),
  stockBajo: (params) => api.get('/productos/stock-bajo', { params }),
  buscarPorId: (id) => api.get(`/productos/${id}`),
  crear: (data) => api.post('/productos', data),
  actualizar: (id, data) => api.put(`/productos/${id}`, data),
  eliminar: (id) => api.delete(`/productos/${id}`),
};

export const movimientoService = {
  listarTodos: (params) => api.get('/movimientos', { params }),
  listarPorProducto: (id, params) => api.get(`/movimientos/producto/${id}`, { params }),
  registrar: (data) => api.post('/movimientos', data),
};

export default api;
