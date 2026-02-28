import { request } from "../instance";

// 会议任务列表
export const getPageList = (data: any) =>
	request.post("/taskMeet/getPageList", data);

// 编辑/创建任务
export const saveOrUpdate = (data: any) =>
	request.post("/taskMeet/saveOrUpdate", data, { operationMessage: "保存成功" });

// 删除会议任务
export const delEntityById = (id: any) =>
	request.post(
		`/taskMeet/delEntityById?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);

// 详情 
export const info = (id: any) =>
	request.post(`/taskMeet/info?id=${id}`, {});