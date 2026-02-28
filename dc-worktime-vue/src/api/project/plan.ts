import { request } from "../instance";

export enum PlanType {
	PLAN_GROUP = 1,
	PLAN = 2,
	TASK = 3,
	MILTONE = 4
}

// 新建计划-快速创建
export const createQuiklyRapid = (data: any) =>
	request.post("/proPlan/create_rapid", data, {
		operationMessage: "添加成功"
	});

// 新建计划
export const createPlan = (data: any) =>
	request.post("/proPlan/create_plan", data, {
		operationMessage: "添加成功"
	});

// 新建计划
export const selectPlanTree = (data: any) => request.post("/proPlan/select_plan_tree", data);

export const deletePlan = (data: any) =>
	request.post("/proPlan/delete_plan", data, {
		operationMessage: "删除成功"
	});
export const editPlan = (data: any) =>
	request.post("/proPlan/edit_plan", data, {
		operationMessage: "编辑成功"
	});

export const selectPlanDetail = (data: any) => request.post("/proPlan/select_plan_detail", data);

export const taskSaveOrUpdate = (data: any) =>
	request.post("/taskInfo/saveOrUpdate", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

export const createTask = (data: any) =>
	request.post("/proPlan/create_task", data, {
		operationMessage: "添加成功"
	});

export const editTask = (data: any) =>
	request.post("/proPlan/edit_task", data, {
		operationMessage: "编辑成功"
	});
// /proPlan/create_task

export const selectPlanById = (data: any) => {
	if (data.type == 1) {
		return request.post("/proPlan/select_plan_by_id", data);
	} else if (data.type == 2) {
		return request.get("/taskPlan/getQuestByPlan", { params: { ...data, plan_id: data.id } });
	} else {
		return request
			.get("/testBug/selectBugList", {
				params: {
					assignedToUserId: data.assignedToUserId,
					milestoneId: data.milestoneId,
					title: data.plan_title
				}
			})
			.then((res: any) => {
				return { records: res, total: res.length };
			});
	}
};

export const getEntity = (data: any) => request.post("/taskInfo/getEntity", {}, { params: data });

export const delEntityById = (data: any) =>
	request.post("/taskInfo/delEntityById", {}, { params: data, operationMessage: "删除成功" });

export const selectModuleTree = (data: any) => request.post("/proModule/select_module_tree", data);

export const getDeveloper = () => request.get("/proPlan/get_developer");

// 任务列表
export const getIterationList = (data: any) => request.post("/proPlanIteration/getIterationList", data);

//

export const addOrUpdateIteration = (data: any) =>
	request.post("/proPlanIteration/addOrUpdateIteration", data, {
		operationMessage: data.id ? "编辑成功" : "添加成功"
	});

// /proPlanIteration/deleteIteration

export const deleteIteration = (data: any) =>
	request.post("/proPlanIteration/deleteIteration", data, {
		operationMessage: "删除成功"
	});

export const getTaskIdsByFeatureIds = (planId: number, data: any) =>
	request.post(`/proPlan/${planId}/getTaskIdsByFeatureIds`, data);
