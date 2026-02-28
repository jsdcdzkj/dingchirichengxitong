import { request } from "../instance";

export const saveDeliverable = (data: any) =>
	request.post("/deliverable/saveDeliverable", data, {
		operationMessage: "保存成功"
	});

export const editLinkOrFile = (data: any) =>
	request.post("/deliverable/editLinkOrFile", data, {
		operationMessage: "操作成功"
	});

export const getEntityById = (data: any) => request.post("/deliverable/getEntityById", data);

export const selectList = (data: any) => request.post("/deliverable/selectList", data);

export const deleteById = (data: any) => request.post("/deliverable/deleteById", {}, { params: data });

export const countDeliverable = (data: any) => request.post("/deliverable/countDeliverable", data);

export const saveReview = (data: any) =>
	request.post("/review/saveReview", data, {
		operationMessage: "操作成功"
	});
