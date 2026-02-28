import { request } from "../instance";

export const getDeptStatistics = (data: any) => request.get("/deptStatistics/getDeptStatistics", { params: data });
export const getDeptStatisticsTask = (data: any) =>
	request.get("/deptStatistics/getDeptStatisticsTask", { params: data });

export const getDeptStatisticsCost = (data: any) =>
	request.get("/deptStatistics/getDeptStatisticsCost", { params: data });

export const getDeptStatisticsCostList = (data: any) =>
	request.get("/deptStatistics/getDeptStatisticsCostList", { params: data });

export const getDeptUserAnalysis = (data: any) => request.get("/deptStatistics/getDeptUserAnalysis", { params: data });

export const getAbilityValueByUser = (data: any) =>
	request.get("/deptStatistics/getAbilityValueByUser", { params: data });

export const workHoursListData = (data: any) => request.post("/project/workHoursListData", data);

export const groupListTree = (data: any) => request.get("/project/groupListTree", { params: data });
