import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const clientVersionApi = {
  page: (param) => {
    return postRequest('/client-version/page', param);
  },

  add: (param) => {
    return postRequest('/client-version/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/client-version/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/client-version/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/client-version/update', param);
  },

  get: (id) => {
    return getRequest(`/client-version/${id}`);
  }
};
