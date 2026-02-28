import { request } from "../instance";
export const selectModuleTree = (data: any) => request.post("/proModule/select_module_tree", data);

export const selectProjectList = (data: any) => request.post("/project/selectList", data);

export const selectListCount = (data: any) => request.post("/project/selectListCount", data);

// export const delData = () =>
// 	request.get("/testBug/delData", {
// 		headers: {}
// 	});

export const selectPage = (data: any) => request.get("/testBug/selectPage", { params: data });

// /testBug/addUpdateInfo
export const addUpdateInfo = (data: any) => request.post("/testBug/addUpdateInfo", data);

export const audit = (data: any) => request.get("/testBug/audit", { params: data });

export const delDataById = (data: any) => request.get("/testBug/delData", { params: data });

// /testBug/solveTask
export const solveTask = (data: any) => request.get("/testBug/solveTask", { params: data });

// /testBug/noStartTask
export const noStartTask = (data: any) =>
	// request.post("/testBug/noStartTask", data);
	request.get("/testBug/noStartTask", { params: data });

export const batchDeletion = (data: any) => {
	return request.post(`/testBug/batchDeletion?ids=${data}`);
};

export const detailInfo = (data: any) => {
	return request.post(`/testBug/detail?id=${data}`);
};

export const selectListByGroupId = (data: any) => {
	return request.post<any>(`/testBug/selectListByGroupId`, data);
};

export const top = (data: any) => request.get("/testBug/top", { params: data });

export const topCancel = (data: any) => request.get("/testBug/topCancel", { params: data });

export const severityCount = (data: any) => request.get("/testBug/severityCount", { params: data });
// 问题统计
export const severityCountBean = (data: any) => request.get("/testBug/severityCountBean", { params: data });

export const activateTask = (data: any) => request.get("/testBug/activateTask", { params: data });

export const selectDesignPage = (data: any) => request.get("/testBug/selectDesignPage", { params: data });

export const getTypeUserList = (data: any) => request.get("/sysGroup/getTypeUserList", { params: data });

export const selectProductPage = (data: any) => request.get("/testBug/selectProductPage", { params: data });
