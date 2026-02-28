import { request } from "../instance";

// /sysmenu/getMenuTree.do
export const getMenuTree = (data: any) => {
	return request.post("/sysmenu/getMenuTree.do", {}, { params: data });
};

export const userMenuTree = (id: any) => {
	return request.get("/userMenuTree", { params: { id } });
};
// /sysmenu/saveMenu.do
export const saveMenu = (data: any) => {
	return request.post("/sysmenu/saveMenu.do", data);
};

// /sysmenu/delMenu.do
export const delMenu = (data: any) => {
	return request.post(
		"/sysmenu/delMenu.do",
		{},
		{
			params: data,
			headers: {
				"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
			}
		}
	);
};
