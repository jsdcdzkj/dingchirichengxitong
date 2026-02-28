import { login } from "@/api/common/login";
import { defineStore } from "pinia";

export const useAppStore = defineStore("app", {
	state: () => {
		return {
			menusList: [],
			collapse: false
		};
	},
	actions: {
		changeCollapse() {
			this.collapse = !this.collapse;
		}
	},
	getters: {}
});
