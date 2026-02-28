import { request } from "../instance";

// 分配任务列表
export const toTask = (data: any) =>
	request.post("/proPlan/toTask", data);

// 保存分配数据
export const saveOrUpdate = (data: any) =>
	request.post("/taskInfo/saveOrUpdate", data, { operationMessage: "分配成功" });

// 计划任务详情
export const getEntity = (id: any) =>
	request.post(`/taskInfo/getEntity?id=${id}`, {});

// 项目下拉框数据
export const taskCount = (data: any) =>
	request.post("/proPlan/taskCount", data);

// 获取难易度列表
export const toTaskCount = (data: any) =>
request.post("/proPlan/toTaskCount", data);

// 待分配任务负责人下拉框
export const getGroupUser = (data: any) => request.post("/sysGroup/getGroupUser", {}, { params: data });
