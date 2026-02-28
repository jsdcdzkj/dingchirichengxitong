import { request } from "../instance";

// 列表接口
export const selectPageList = (data: any) =>
	request.post("/publishRecord/selectPageList", data, {params: data});
// 保存
export const saveOrUpd = (data: any) =>
	request.post("/publishRecord/saveOrUpd", data, { operationMessage: "保存成功" });

// 详情
export const getEntityById = (id: any) =>
	request.post("/publishRecord/getEntityById?id=" + id, {});

export const deleteById = (id: any) =>
	request.post(
		`/publishRecord/deleteById?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);

// 审核
export const auditPublish = (data: any) =>
	request.post("/publishRecord/auditPublish", data);

// 发布确认
export const publishVerify = (data: any) =>
	request.post("/publishRecord/publishVerify", data);