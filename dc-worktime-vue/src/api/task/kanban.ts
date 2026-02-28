import { request } from "../instance";

// 开发组名称
export const getList = (data: any) =>
	request.post("/sysGroup/getList", data);

// 开发组人员
export const getGroupUserList = (data: any) =>
	request.post("/sysGroup/getGroupUserList", {}, {params: data});

// 完成任务--接口验证
export const interfaceCheck = (data: any) =>
	request.post("/taskInfo/interfaceCheck", data);

// 项目列表下拉框数据(未完成任务数量)
export const getUnfinishedTaskNumList = (data: any) =>
	request.post("/project/getUnfinishedTaskNumList", data);

// 任务看板列表
export const taskDashboard = (data: any) =>
	request.post("/taskInfo/taskDashboard", {}, {params: data});

// 看板的数据列表
export const taskDashboardList = (data: any) =>
	request.post("/taskInfo/taskDashboardList", {}, {params: data});

// 根据当前时间计算工时
export const computeWorkingMinByNow = (data: any) =>
	request.post("/taskInfo/computeWorkingMinByNow", data, {
		params: data
	});

// 挂起任务并且新建运维任务
export const pendingTaskSave = (data: any) =>
	request.post("/taskOperationInfo/pendingTaskSave", data);

// 任务转移
export const taskTransfer = (data: any) =>
	request.post("/taskTransfer/saveOrUpdate", data);

// bug详情
export const detailBug = (id: any) =>
	request.post(`/testBug/detail?id=${id}`, {});

// 计划任务列表
export const taskGetList = (data: any) =>
	request.post("/taskInfo/getList", data);

// 运维列表查询
export const operationGetList = (data: any) =>
	request.post("/taskOperationInfo/getList", data);

// bug任务列表
export const bugGetList = (data: any) =>
	request.post("/testBug/getList", data);

// 领导交办任务列表
export const leaderGetList = (data: any) =>
	request.post("/taskLeadersInfo/getList", data);

// 计划任务操作接口
// 开始
export const startTask = (id: any) =>
	request.post(`/taskInfo/startTask?id=${id}`, {}, {
		operationMessage: "任务开始"
	});

// 完成
export const completeTask = (data: any) =>
	request.post("/taskInfo/completeTask", data, {
		params: data
	});

// 转移
export const transferTask = (data: any) =>
	request.post("/taskInfo/transferTask", data, {
		params: data
	});

// 挂起
export const pendingTask = (data: any) =>
	request.post("/taskInfo/pendingTask", {}, {
		params: data
	});

// 恢复
export const restoreTask = (id: any) =>
	request.post(`/taskInfo/restoreTask?id=${id}`, {});

// 暂停
export const stopTask = (id: any) =>
	request.post(`/taskInfo/stopTask?id=${id}`, {});

// 工作成果提交
export const taskWorkResultSave = (data: any) =>
	request.post("/taskInfo/taskWorkResultSave", data);

// 工作成果提交11
export const saveOrUpdateLeader = (data: any) =>
	request.post("/taskLeadersResults/saveOrUpdate", data);

// 后端完成任务接口填写
export const taskInterfaceSave = (data: any) =>
	request.post("/taskInfo/taskInterfaceSave", data);

// 接口关联下拉表
export const getFeatureList = (data: any) =>
	request.post("/taskPlan/getFeatureList", data, {
		params: data
	});



// 运维任务
// 开始
export const startTaskOperation = (id: any) =>
	request.post(`/taskOperationInfo/startTask?id=${id}`, {});

// 完成
export const completeTaskOperation = (data: any) =>
	request.post("/taskOperationInfo/completeTask", data, {
		params: data
	});

// 转移
export const transferTaskOperation = (data: any) =>
	request.post("/taskOperationInfo/transferTask", data);

// 挂起
export const pendingTaskOperation = (data: any) =>
	request.post("/taskOperationInfo/pendingTask", {}, {
		params:data
	});

// 恢复
export const restoreTaskOperation = (id: any) =>
	request.post(`/taskOperationInfo/restoreTask?id=${id}`, {});

// 暂停
export const stopTaskOperation = (id: any) =>
	request.post(`/taskOperationInfo/stopTask?id=${id}`, {});


// 领导交办任务
// 开始
export const startTaskLeader = (id: any) =>
	request.post(`/taskLeadersInfo/startTask?id=${id}`, {});

// 完成
export const completeTaskLeader = (data: any) =>
	request.post("/taskLeadersInfo/completeTask", data, {
		params: data
	});

// 转移
export const transferTaskLeader = (data: any) =>
	request.post("/taskLeadersInfo/transferTask", data);

// 挂起
export const pendingTaskLeader = (data: any) =>
	request.post("/taskLeadersInfo/pendingTask", {}, {
		params: data
	});

// 恢复
export const restoreTaskLeader = (id: any) =>
	request.post(`/taskLeadersInfo/restoreTask?id=${id}`, {});

// 暂停
export const stopTaskLeader = (id: any) =>
	request.post(`/taskLeadersInfo/stopTask?id=${id}`, {});

// 阶段性成果提交
export const saveOrUpdate = (data: any) =>
	request.post("/taskLeadersResults/saveOrUpdate", data);

// bug任务
// 开始
export const startTaskBug = (id: any) =>
	request.post(`/testBug/startTask?id=${id}`, {});

// 完成
export const completeTaskBug = (data: any) =>
	request.post("/testBug/completeTask", data, {
		params: data
	});

// 转移
export const transferTaskBug = (data: any) =>
	request.post("/testBug/transferTask", data);

// 挂起
export const pendingTaskBug = (data: any) =>
	request.post("/testBug/pendingTask", {}, {
		params: data
	});

// 恢复
export const restoreTaskBug = (id: any) =>
	request.post(`/testBug/restoreTask?id=${id}`, {});

// 暂停
export const stopTaskBug = (id: any) =>
	request.post(`/testBug/stopTask?id=${id}`, {});

// 会议任务
// 会议列表
export const meetGetList = (data: any) =>
	request.post("/taskMeetUsers/getList", data);
// 开始
export const startTaskMeeting = (id: any) =>
	request.post(`/taskMeetUsers/startTask?id=${id}`, {});

// 完成
export const completeTaskMeeting = (data: any) =>
	request.post("/taskMeetUsers/completeTask", data, {
		params: data
	});

// 转移
// export const transferTaskBug = (data: any) =>
// 	request.post("/testBug/transferTask", data);

// 挂起
export const pendingTaskMeeting = (data: any) =>
	request.post("/taskMeetUsers/pendingTask", {}, {
		params: data
	});

// 恢复
export const restoreTaskMeet = (id: any) =>
	request.post(`/taskMeetUsers/restoreTask?id=${id}`, {});

// 暂停
export const stopTaskMeeting = (id: any) =>
	request.post(`/taskMeetUsers/stopTask?id=${id}`, {});

// 不参加该任务
export const delEntityById = (id: any) =>
	request.post(`/taskMeetUsers/delEntityById?id=${id}`, {});

// 会议任务详情
export const getMeetEntity = (id: any) =>
request.post(`/taskMeetUsers/getEntity?id=${id}`, {});


// 关联任务树形查看功能点
export const getTree = (data: any) => request.get("/taskInfo/getTree", { params: data });

// 关联任务功能点列表
export const selectModuleTreeByMo = (data: any) => request.get("/taskInfo/selectModuleTreeByMo", { params: data });


//季度绩效考核
export const reportKPI = (data: any,token:any) =>
	request.post("/sysTeamUser/reportKPI", data,{cancelToken:token});


//绩效考核字典表
export const getListSys = (data: any) =>
	request.post("/sysGroup/getList", data);