import { request } from "../instance";

export const selectPage = (params: any) => request.get("/testPlan/selectPage", { params });

// export const selectProject = () => request.post("/testPlan/selectProject");
export const selectProject = (params: any) => request.get("/testPlan/selectProject", { params });
// /testPlan/addUpdateInfo
export const addUpdateInfo = (data: any) => request.post("/testPlan/addUpdateInfo", data);

// /testPlan/issueTask
export const issueTask = (params: any) => request.get("/testPlan/issueTask", { params });

// /testPlan/completeTask
export const completeTask = (params: any) => request.get("/testPlan/completeTask", { params });

// /testPlan/pendingTask
export const pendingTask = (params: any) => request.get("/testPlan/pendingTask", { params });

// export const pendingTask = (data: any) =>
// request.post("/testPlan/pendingTask", data);

// testPlan/recoverTask
export const recoverTask = (params: any) => request.get("/testPlan/recoverTask", { params });

// /testPlan/delData
export const delData = (params: any) => request.get("/testPlan/delData", { params });

// /testPlan/selectById
export const selectById = (params: any) => request.get("/testPlan/selectById", { params });

// /testPlan/selectByProjectId
