import { request } from "../instance";

// 考核组列表查询
export const selectPage = (data: any) => request.post("/sysTeamUser/selectPage", data);
// 考核组成员
export const getList = (data: any) => request.post("/sysTeamUser/getList", data);
// 选择成员列表
export const getTeamUserList = (data: any) => request.post("/sysTeamUser/getTeamUserList", data);
// 新增
export const add = (data: any) => request.post("/sysTeamUser/add", data);
// 删除
// export const del = (data: any) => request.post("/sysTeamUser/del", { params: data });

export const del = (id: any) =>
	request.post(
		`/sysTeamUser/del?id=${id}`,
		{},
		{ operationMessage: "删除成功" }
	);
