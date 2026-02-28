import { request } from "../instance";

export const selectFeaturePage = (data: any) => request.post("/proModule/select_feature_page", data);

export const selectFeatureByMIds = (data: any) => request.post("/proModule/selectFeatureByMIds", data);

export const createFeature = (data: any) => request.post("/proModule/create_feature", data);

export const editFeature = (data: any) => request.post("/proModule/edit_feature", data);

export const deleteFeature = (data: any) => request.post("/proModule/delete_feature", data);

export const createRapid = (data: any) => request.post("/proModule/create_rapid", data);

export const createPlan = (data: any) => request.post("/proModule/create_plan", data);

export const deleteModule = (data: any) => request.post("/proModule/delete_module", data);

export const selectPageList = (data: any) => request.post("/deliverable/selectPageList", data);

export const selectFileList = (data: any) => request.post("/file/selectPageList", data);

export const saveOrUpd = (data: any) => request.post("/file/saveOrUpd", data);

export const deleteById = (params: any) => {
	return request.get(`/file/deleteById`, { params });
};

export const deleteFileById = (params: any) => {
	return request.get(`/file/deleteFileById`, { params });
};

// /proModule/select_feature_detail
export const selectFeatureDetail = (data: any) => request.post("/proModule/select_feature_detail", data);

// export const selectPageList = (params: any) =>
// request.post("/deliverable/selectPageList", {}, { params });
