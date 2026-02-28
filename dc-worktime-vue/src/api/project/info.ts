import { request } from "../instance";

export const highRiskTaskList = (id: number) => {
	return request.get(`/project/highRiskTaskList`, {
		params: {
			projectId: id
		}
	});
};
export const dayTaskList = (params: any) => {
	return request.post(`/project/dayTaskList`, params);
};

export const countDayTask = (params: any) => {
	return request.get(`/project/countDayTask`, { params });
};

export const degreeOfDifficulty = (params: any) => {
	return request.get(`/progressTest/degreeOfDifficulty`, { params });
};

export const sourceSeverity = (params: any) => {
	return request.get(`/progressTest/sourceSeverity`, { params });
};

export const bugModelTop = (params: any) => {
	return request.get(`/progressTest/bugModelTop`, { params });
};

export const severityPercentage = (params: any) => {
	return request.get(`/progressTest/severityPercentage`, { params });
};
export const completionProgress = (params: any) => {
	return request.get(`/progressTest/completionProgress`, { params });
};

export const repairComplete = (params: any) => {
	return request.get(`/progressTest/repairComplete`, { params });
};

export const workHoursContrastData = (params: any) => {
	return request.get(`/workHoursReport/workHoursContrastData`, { params });
};

export const workHoursDaysData = (params: any) => {
	return request.get(`/workHoursReport/workHoursDaysData`, { params });
};

export const workHoursListData = (params: any) => {
	return request.get(`/workHoursReport/workHoursListData`, { params });
};
export const contributionDegree = (params: any) => {
	return request.get(`/taskInfo/contributionDegree`, { params });
};

export const bugIndex = (params: any) => {
	return request.get(`/taskInfo/bugIndex`, { params });
};

export const bugCount = (params: any) => {
	return request.get(`/contribution/bugCount`, { params });
};

export const groupList = () => {
	return request.post(`/sysGroup/groupList`, {});
};
export const overtimeWork = (params: any) => {
	return request.get(`/taskInfo/overtimeWork`, { params });
};

export const degreeOfDifficultyBug = (params: any) => {
	return request.get(`/taskInfo/degreeOfDifficulty`, { params });
};

// /workHoursReport/workHoursListData

// /taskInfo/degreeOfDifficulty?groupId=2&projectId=1

// /taskInfo/overtimeWork?projectId=1

// /sysGroup/groupList
///contribution/bugCount?groupId=2&pro_project_id=1
// /taskInfo/bugIndex?type=2&projectId=1
// /taskInfo/contributionDegree?type=1&projectId=1
// /workHoursReport/workHoursListData
// /workHoursReport/workHoursDaysData
// /workHoursReport/workHoursContrastData

// /progressTest/repairComplete?pro_project_id=1
// /progressTest/completionProgress?pro_project_id=1
///progressTest/severityPercentage?pro_project_id=1
// /progressTest/bugModelTop?pro_project_id=1
// /progressTest/degreeOfDifficulty?pro_project_id=10000
// /project/countDayTask?projectId=1
