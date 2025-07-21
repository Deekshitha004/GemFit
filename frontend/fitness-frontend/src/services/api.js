import axios from "axios";

const API_URL = 'http://localhost:9999/api';

const api = axios.create({
  baseURL: API_URL
});

// Attach Authorization and UserId headers to every request
api.interceptors.request.use((config) => {
  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  if (userId) {
    config.headers['X-User-Id'] = userId;  // ✅ use correct header case
  }

  return config;
});

export const getActivities = () => api.get('/activities');
export const addActivity = async (payload) => {
  const res = await api.post('/activities', payload);
  return res.data;          // ☑ send object back to caller
};
export const getActivityDetail = (id) => api.get(`/recommendations/activity/${id}`);

export const deleteActivity = async (id) => {
  // Most back-ends return 204 with no body; we just want the status.
  await api.delete(`/activities/${id}`);
  return id;
};
export default api;
