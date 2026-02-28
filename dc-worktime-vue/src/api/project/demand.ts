import { request } from "../instance";

// 列表接口
export const selectPageList = (data: any) =>
	request.post("/requirement/selectPageList", data);
// 保存
export const saveOrUpd = (data: any) =>
	request.post("/requirement/saveOrUpd", data);

// 详情
export const getEntityById = (id: any) =>
	request.post("/requirement/getEntityById?id=" + id, {});

export const deleteById = (id: any) =>
	request.post(
		`/requirement/deleteById?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);

// 需求角标统计
export const countRequirement = (id: any, iterationId:any) =>
	request.post("/requirement/countRequirement?projectId=" + id + '&iteration_id=' + iterationId, {});
