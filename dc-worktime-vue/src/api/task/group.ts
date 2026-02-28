import { request } from "../instance";

// 工作安排与日历数据
export const getTaskList = (data: any) =>
	request.post("/workHoursReport/workPlanCalendarData", data, { headers: { "Content-Type": "multipart/form-data" } });
// 人员任务列表
export const getUserTaskList = (data: any) =>
	request.post("/workHoursReport/workPlanCalendarList", data, { headers: { "Content-Type": "multipart/form-data" } });

// 组员工作总结与明细
export const getTaskSummary = (data: any) => request.get("/taskInfo/workSummary", { params: data });
export const getTaskDetail = (data: any) => request.get("/taskInfo/workDetail", { params: data });
export const getUserTaskInfoList = (data: any) => request.get("/taskInfo/taskInfoListByGroup", { params: data });

export const getTaskMilestone = (data: any) => request.get("/taskInfo/milestone", { params: data });

// 组员贡献度
export const getContributionDegree = (data: any) => request.get("/taskInfo/contributionDegreeGroup", { params: data });

export const getBugIndex = (data: any) => request.get("/taskInfo/bugIndexByGroup", { params: data });
export const getBugCount = (data: any) => request.get("/contribution/bugCount", { params: data });

export const getOvertimeWork = (data: any) => request.get("/taskInfo/overtimeWorkByGroup", { params: data });
export const getDifficulty = (data: any) => request.get("/taskInfo/degreeOfDifficulty", { params: data });
export const getWorkHoursContrast = (data: any) =>
	request.post("/workHoursReport/workHoursContrastGroupData", data, {
		headers: { "Content-Type": "multipart/form-data" }
	});

export const getWorkHoursDays = (data: any) =>
	request.post("/workHoursReport/workHoursDaysGroupData", data, {
		headers: { "Content-Type": "multipart/form-data" }
	});

export const getWorkHoursList = (data: any) =>
	request.post("/workHoursReport/workHoursListGroupData", data, {
		headers: { "Content-Type": "multipart/form-data" }
	});
