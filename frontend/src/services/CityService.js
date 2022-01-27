import http from "../http-common";
import authHeader from './auth-header';

const getAll = () => {
  return http.get('cities', { headers: authHeader() });
};

const get = (id) => {
  return http.get(`cities/${id}`, { headers: authHeader() });
};

const update = (id, data) => {
  return http.put(`cities/${id}`, data, { headers: authHeader() });
};

const findByTitle = (title) => {
  return http.get(`cities?title=${title}`, { headers: authHeader() });
};


const CityService = {
  getAll,
  get,
  update,
  findByTitle,
};

export default CityService;
