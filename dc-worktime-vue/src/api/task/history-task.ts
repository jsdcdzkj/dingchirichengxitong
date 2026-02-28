import { request } from "../instance";

// 领导交办任务列表
export const getHistoryPageList = (data: any) =>
	request.post("/taskOperationInfo/getHistoryPageList", data);