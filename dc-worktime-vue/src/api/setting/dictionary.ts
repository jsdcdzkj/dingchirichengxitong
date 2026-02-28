import { request } from "../instance";

export const getPage = (data: any) =>
	request.post("/sysDict/getPage", {}, { params: data });

export const saveOrUpdate = (data: any) =>
	request.post("/sysDict/saveOrUpdate", data, {
		operationMessage: "添加成功"
	});
export const update = (data: any) =>
	request.post("/sysDict/update", data, {
		operationMessage: "修改成功"
	});

export const deleteDict = (data: any) => {
	return request.post(
		`/sysDict/delete?id=${data}`,
		{},
		{ operationMessage: "删除成功" }
	);
};

export const selectDictList = (data: any) =>
	request.post("/sysDict/selectDictList", {}, { params: data });
