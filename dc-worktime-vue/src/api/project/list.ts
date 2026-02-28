import { request } from "../instance";

export const selectPageList = (data: any) => request.post("/project/selectPageList", data);
// 项目列表
// export const selectPageList = (data: any) => request.post("/project/selectPageListQuick", data);

export const selectList = () => request.post("/project/selectList", {});

export const saveOrUpd = (data: any) =>
	request.post("/project/saveOrUpd", data, { operationMessage: data.id ? "编辑成功" : "添加成功" });

export const getEntityById = (id: any) => request.post("/project/getEntityById?id=" + id, {});

export const deleteById = (id: any) =>
	request.post(`/project/deleteById?id=${id}`, {}, { operationMessage: "删除成功" });

export const saveProjectUser = (data: any) =>
	request.post("/project/saveProjectUser", data, {
		operationMessage: "人员维护成功"
	});

export const countWorkItemType = (data: any) => request.get("/project/countWorkItemType", { params: data });

export const underwayPlan = (data: any) => request.post("/proPlan/underwayPlan", data);

export const projectUserList = (data: any) => request.post("/project/projectUserList", {}, { params: data });

export const projectProgress = (data: any) => request.post("/proPlan/projectProgress", data);
