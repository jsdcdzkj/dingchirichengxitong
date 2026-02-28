import { getUserInfo, login, logout } from "@/api/common/login";
import { defineStore } from "pinia";

type UserInfo = {
	avatar: string;
	loginName: string;
	phone: string;
	usename: string;
};

type UserState = {
	token: string | null;
	userInfo: UserInfo | null;
};

export const useUserStore = defineStore("user", {
	state: () => {
		return {
			token: localStorage.getItem(import.meta.env.VITE_TOKEN) || "",
			userInfo: null,
			loading: false,
			menus: [] as any[]
		};
	},
	actions: {
		async changePageLoading(loading: boolean) {
			this.loading = loading;
		},
		async getUserInfo() {
			const userInfo = await getUserInfo();
			this.userInfo = userInfo;
		},
		async setMenus(menus: any[]) {
			this.menus = menus;
		},
		async login(data: any) {
			const { jsdcToken } = await login(data);
			localStorage.setItem(import.meta.env.VITE_TOKEN, jsdcToken);
			this.token = jsdcToken;
			return jsdcToken;
		},
		async logout() {
			localStorage.removeItem(import.meta.env.VITE_TOKEN);
			this.token = "";
			this.userInfo = null;
			return true;
		}
	},
	getters: {
		isLogin(): boolean {
			return !!this.token;
		}
	}
});
