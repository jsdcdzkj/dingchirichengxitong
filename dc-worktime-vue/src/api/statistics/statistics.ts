import { request } from "../instance";

// 分析维度接口
export const analysisDimension = (data: any) =>
	request.post(`/project/analysisDimension?startDate=${data.startDate}&endDate=${data.endDate}`, data);
// 项目进度分析
export const selectAnalysisPageList = (data: any) =>
	request.post("/project/selectAnalysisPageList", data);

// 各项目工时总览
export const projectWorkingHour = (data: any) =>
	request.post(`/project/projectWorkingHour`, data);

// 项目工时对比表
export const workHoursContrastData = (data: any) =>
	request.post("/project/workHoursContrastData", data);

// 一周工时曲线
export const workHoursDaysData = (data: any) =>
	request.post("/project/workHoursDaysData", data);

// 高风险项目任务预警
export const highRiskTaskListByIds = (data: any) =>
	request.post(`/project/highRiskTaskListByIds`, data);

// 项目质量情况统计分析
export const projectQualityList = (data: any) =>
	request.post("/project/projectQualityList", data);
//产品与项目
export const projectProductList = (data: any) =>
	request.get("/hourAccounting/project", {params:data});
//产品与项目导出
export const projectProductExport = (data: any) =>
	request.get("/hourAccounting/projectExport", {params:data,responseType: "blob"});
//开发与测试
export const projectDevelopList = (data: any) =>
	request.get("/hourAccounting/test", {params:data});
//开发与测试导出
export const projectDevelopExport = (data: any) =>
	request.get("/hourAccounting/testExport", {params:data,responseType: "blob"});
// 运维与实施
export const projectOperationList = (data: any) =>
	request.get("/hourAccounting/implement", {params:data});
// 运维与实施导出
export const projectOperationExport = (data: any) =>
	request.get("/hourAccounting/implementExport", {params:data,responseType: "blob"});
// bug修复
export const projectBugList = (data: any) =>
	request.get("/hourAccounting/bug", {params:data});
// bug修复导出
export const projectBugExport = (data: any) =>
	request.get("/hourAccounting/bugExport", {params:data,responseType: "blob"});
// 领导交代任务
export const projectLeaderList = (data: any) =>
	request.get("/hourAccounting/taskLeadersPageList", {params:data})
// 领导交代任务导出;
export const projectLeaderExport = (data: any) =>
	request.get("/hourAccounting/taskLeadersExport", {params:data,responseType: "blob"});