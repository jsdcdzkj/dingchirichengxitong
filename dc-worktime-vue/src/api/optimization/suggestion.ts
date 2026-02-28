import { request } from "../instance";

export const selectPage = (params: any) => request.get("/optimizeSuggestion/selectPage", { params });

export const delData = (data: any) => request.get("/optimizeSuggestion/delData", { params: data });

export const addUpdateInfo = (data: any) => request.post("/optimizeSuggestion/addUpdateInfo", data);

export const markResultcount = (data: any) => request.post("/optimizeSuggestion/markResultCount", data);

export const selectById = (params: any) => request.get("/optimizeSuggestion/selectById", { params });
