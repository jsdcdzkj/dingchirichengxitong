import { request } from "../instance";

export const selectPage = (data: any) =>
	request.post("/sysGroup/selectPage", {}, { params: data });

export const delData = (data: any) => {
	return request.post(
		`/sysGroup/delData?id=${data}`,
		{},
		{ operationMessage: "删除成功" }
	);
};
export const addUpdateInfo = (data: any) => {
	return request.post(`/sysGroup/addUpdateInfo`, {}, { data });
};
