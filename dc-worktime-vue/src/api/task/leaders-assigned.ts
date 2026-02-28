import { request } from "../instance";

// 领导交办任务列表
export const getPageList = (data: any) =>
	request.post("/taskLeadersInfo/getPageList", data);

// 创建运维任务
export const creatTask = (data: any) =>
	request.post("/taskLeadersInfo/creatTask", data, { operationMessage: "保存成功" });

// 删除领导交办任务
export const delEntityById = (id: any) =>
	request.post(
		`/taskLeadersInfo/delEntityById?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);

// 详情 
export const getEntityLeader = (id: any) =>
	request.post(`/taskLeadersInfo/getEntity?id=${id}`, {});

// 终止任务
export const abortTask = (id: any) =>
	request.post(`/taskLeadersInfo/abortTask?id=${id}`, {});

// 任务打回
export const taskRollback = (data: any) =>
	request.post("/taskLeadersInfo/taskRollback", data);

// 指派
export const saveOrUpdate = (data: any) =>
	request.post("/taskLeadersInfo/saveOrUpdate", data);

// 打分
export const mark = (data: any) =>
	request.post("/taskLeadersInfo/mark", data);