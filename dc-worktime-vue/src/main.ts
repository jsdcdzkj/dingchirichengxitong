import App from "./App.vue";
// import { ViteSSG } from "vite-ssg";
import "element-plus/dist/index.css"; //引入element-plus样式
import "@/styles/index.scss";
import "nprogress/nprogress.css";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import "uno.css";

import "element-plus/theme-chalk/src/message.scss";
import "element-plus/theme-chalk/src/message-box.scss";
import { createPinia } from "pinia";
// import { setupRouter } from "./core/router";

import ElementPlus from "element-plus";
import mitt from "mitt";
// import { router } from "@/core/router";
import { createApp } from "vue";
import { ProjectManger } from "./core";
import { Quill, QuillEditor } from "@vueup/vue-quill";
export const bus = mitt();

// @ts-ignore
window.Quill = Quill;

// const renderApp = async () => {
// 	const app = createApp(App);
// 	const pinia = createPinia();
// 	app.use(pinia);
// 	app.use(ElementPlus, { locale: zhCn });
// 	await setupRouter(router);
// 	app.use(router);
// 	bus.on("logout", () => router.replace("/login"));
// 	app.mount("#app");
// };

// renderApp();

export const appInstance = new ProjectManger({
	app: App,
	routes: [
		{
			path: "/login",
			name: "Login",
			component: () => import("@/pages/common/login.vue")
		},
		{
			path: "/",
			name: "Root",
			children: [],
			component: () => import("@/composables/index.vue")
		}
	]
});

window.appInstance = appInstance;
