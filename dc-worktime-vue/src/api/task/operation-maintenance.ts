import { request } from "../instance";

// 运维任务列表
export const getPageList = (data: any) =>
	request.post("/taskOperationInfo/getPageList", data);

// 运维任务指派
export const designate = (data: any) =>
	request.post("/taskOperationInfo/designate", data);

// 删除运维任务
export const delEntityById = (id: any) =>
	request.post(
		`/taskOperationInfo/delEntityById?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);
// 运维任务详情
export const getEntityOperation = (id: any) =>
	request.post("/taskOperationInfo/getEntity?id=" + id, {});

// 保存分配数据
export const saveOrUpdate = (data: any) =>
	request.post("/taskOperationInfo/saveOrUpdate", data, { operationMessage: "保存成功" });

// 项目列表查询
export const selectPageList = (data: any) => request.post("/project/selectPageListQuick", data);
