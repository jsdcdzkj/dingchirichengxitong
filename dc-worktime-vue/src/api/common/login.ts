import type { DictMap } from "@/core";
import { request } from "../instance";

export const login = (data: any) => request.post("/login", data, { operationMessage: "登录成功" });

export const getUserInfo = () =>
	request.get("/userInfo", {
		headers: {}
	});

export const logout = () => request.get("/logout");

export const uploadFile = (files: any) =>
	request.upload("/project/uploadFile", { files }, { uploadFileName: "multipartFile" });

export const getUserList = (data: any) =>
	request.post("/sysUser/getList", data, {
		params: data
	});

export const getDictSignList = () => request.post("/sysDict/getDictSignList", {});

export const sysDictPage = () => request.post<DictMap>("/sysDict/getMapList", {});

export const getUserMenu = (id: number) => request.get("/userMenuTree", { params: { id } });
// userMenu

// 项目迭代/计划/版本
export const iterationTree = (data: any) => request.post("/proPlan/iterationTree", data);

// 里程碑
export const getPlanAndMilestone = (data: any) => request.post(`/proPlan/getPlanAndMilestone`, data, {params: data});