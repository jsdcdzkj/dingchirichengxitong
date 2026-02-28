import { request } from "../instance";

export const selectList = (data: any) => request.post("/milestone/selectList", data);

export const saveOrUpd = (data: any) =>
	request.post("/milestone/saveOrUpd", data, {
		operationMessage: data.id ? "修改成功" : "保存成功"
	});

export const getEntityById = (params: any) => request.post("/milestone/getEntityById", {}, { params });

export const countMilestone = (data) => request.get("/milestone/countMilestone", { params: { ...data } });
export const selectMilestoneByProjectId = (params: any) =>
	request.post("/milestone/selectMilestoneByProjectId", {}, { params });

export const deleteById = (params: any) =>
	request.post("/milestone/deleteById", {}, { params, operationMessage: "删除成功" });

export const reviewExport = (data: any) => request.post("/review/reviewExport", data, { responseType: "blob" });
