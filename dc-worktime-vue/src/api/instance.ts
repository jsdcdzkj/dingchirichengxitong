import { RequestClient, doAuthResponseInterceptor, doResponseInterceptor } from "@/core/request";
import { appInstance } from "@/main";

import { ElMessage } from "element-plus";

export type ResponseData = {
	code: number;
	data: any;
	msg: string;
};

export const request = new RequestClient({
	baseURL: import.meta.env.VITE_BASE_API,
	timeout: 10000
});

doAuthResponseInterceptor({
	client: request,
	doAuth: () => {
		return Promise.resolve();
	},
	doRefreshToken: () => {
		return Promise.resolve();
	},
	enableRefreshToken: false
});

const responseIn = doResponseInterceptor((messageStr) => {
	ElMessage.error(messageStr);
});

// 请求拦截器
request.addRequestInterceptor((config) => {
	// 设置token
	config.headers[import.meta.env.VITE_TOKEN] = appInstance.loginToken;
	if (config.responseType == "stream") {
		config.timeout = 360000;
	}
	return config;
});
// 响应拦截器
request.addResponseInterceptor<ResponseData>((response) => {
	const { data, config } = response;
	// 文件流
	if (config.responseType == "blob") return data;
	const { code, data: backData, msg } = data;

	if (code === 200) {
		if (config.operationMessage) {
			ElMessage.success(config.operationMessage);
		}
		return backData;
	} else if (code == 401) {
		// 登录失效
		// useUserStore().logout();

		appInstance.logout();
		request.cancelTokenManager?.cancelRequest();
		return Promise.reject(msg);
	} else if (code == 500) {
		//服务器错误
		ElMessage.error(msg);
		return Promise.reject(msg);
	}
	return data;
}, responseIn.onRejected);
