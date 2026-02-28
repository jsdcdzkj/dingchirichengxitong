import { createRouter, createWebHistory, type Router, type RouteRecordRaw, type RouterHistory } from "vue-router";
import { createApp, reactive, watch, type App, type Component, type Reactive } from "vue";
import defu from "defu";
import { getMenuTree, userMenuTree } from "@/api/setting/menu";
import { coverPathRouter, reconstructionRouter } from "./router";
import { createPinia, type Pinia } from "pinia";
import { selectPageList, selectList } from "@/api/project/list";
// @ts-expect-error "类型推断问题"
import NProgress from "nprogress";
import ElementPlus from "element-plus";
import { getUserInfo, login, sysDictPage } from "@/api/common/login";
import mitt, { type Emitter } from "mitt";
import { textEllipsis } from "./directive/text-ellipsis";
interface ProjectMangerOptions {
	app: Component;
	title?: string;
	routes: Readonly<RouteRecordRaw[]>;
	routerHistory?: RouterHistory;
	callback?: (app: App, router: Router) => void;
}

type DictItemType = {
	dictLabel: string;
	dictValue: string | number;
};

export type DictMap = {
	degree_of_difficulty?: DictItemType[];
	priority?: DictItemType[];
	quality_evaluation?: DictItemType[];
	task_attribute?: DictItemType[];
	urgency?: DictItemType[];
	work_item_type?: DictItemType[];
	task_attribute_plan?: DictItemType[];
};

type DictItemSelfType = {
	label: string;
	value: string | number;
};

export type DictSelfMap = {
	degree_of_difficulty?: DictItemSelfType[];
	priority?: DictItemSelfType[];
	quality_evaluation?: DictItemSelfType[];
	task_attribute?: DictItemSelfType[];
	urgency?: DictItemSelfType[];
	work_item_type?: DictItemSelfType[];
	task_attribute_plan?: DictItemSelfType[];
	question_type?: DictItemSelfType[];
};

export interface ProvideContext {
	projectList: any[];
	selectProjectIndex: number;
	selectProjectTestIndex: number;
	showSelectProject: boolean;
	token: string;
	menus: any[];
	refreshView: boolean;
	dictMap: DictSelfMap;
	breadcrumb: string;
	breadName: Object;
	workId: string;
	userInfo:any;
}

interface IProjectManger {
	setSelectProjectIndex: (index: number) => void;
	setSelectProjectTestIndex: (index: number) => void;
	actionsLogin: (model: any) => Promise<void>;
	logout: () => void;
	refreshView: () => Promise<void>;
	refreshProjectList: () => Promise<void>;
}

const _ProjectConfig = "project-config";

class BaseProjectManger {
	_options: ProjectMangerOptions;
	_app: App;
	_router: Router;
	_store: Pinia;
	_mitt: Emitter<any>;

	constructor(options: ProjectMangerOptions) {
		this._options = defu(
			{
				title: "管理体统",
				routerHistory: createWebHistory()
			},
			options
		);
		this._mitt = mitt();
		this._app = this._initApp(options.app);
		this._store = createPinia();
		this._router = this._initRouter();
		this._bindDirective(this._app);
	}

	private _bindDirective(app: App) {
		app.directive("textEllipsis", textEllipsis);
	}

	private _initApp(app: Component) {
		return createApp(app);
	}

	private _initRouter() {
		return createRouter({
			history: this._options.routerHistory!!,
			routes: this._options.routes
		});
	}

	get router() {
		return this._router;
	}

	get app() {
		return this._app;
	}

	get store() {
		return this._store;
	}
}

export class ProjectManger extends BaseProjectManger implements IProjectManger {
	_config: Reactive<ProvideContext>;
	constructor(options: ProjectMangerOptions) {
		super(options);
		this._config = this._initConfig();
		this._watchConfig();
		this._mount();
	}

	private _watchConfig() {
		watch(
			() => this._config,
			(newVal) => {
				localStorage.setItem(_ProjectConfig, JSON.stringify(newVal));
			},
			{ deep: true, immediate: true }
		);
	}

	private _appUse() {
		this._app.use(ElementPlus);
		this._app.use(this._store);
		this._interceptorAuthRouter();
		this._app.use(this._router);
	}

	private _mount() {
		if (this.isLogin) {
			getUserInfo().then(({ sysUser }) => {
				console.log('222222222222sysUser', sysUser)
				this._config.userInfo = sysUser
				Promise.all([this._dynamicRouter(sysUser.id), this.refreshProjectList(), this.getDictMap()]).then(
					() => {
						this._appUse();
						if (import.meta.env.DEV) {
							// 在开发环境中会出现，防止重复挂载app问题，在这里解决
							if (!window.appIsMounted) {
								this._app.mount("#app", true);
								window.appIsMounted = true;
							}
						} else {
							this._app.mount("#app", true);
						}
					}
				);
			});
		} else {
			this._appUse();
			this._app.mount("#app", true);
			// 记录app已经挂载
			window.appIsMounted = true;
		}
	}

	private _initConfig() {
		const projectConfig = localStorage.getItem(_ProjectConfig);
		const defaultConfig = {
			projectList: [],
			selectProjectIndex: Number(localStorage.getItem("projectIndex")) || 0,
			showSelectProject: false,
			token: localStorage.getItem(import.meta.env.VITE_TOKEN) || "",
			refreshView: false,
			menus: [],
			dictMap: {},
			breadcrumb: "",
			breadName: {},
			workId: "",
			selectProjectTestIndex: -1,
			userInfo:{}
		};
		if (projectConfig) {
			return reactive<ProvideContext>(defu(JSON.parse(projectConfig), defaultConfig));
		} else {
			return reactive<ProvideContext>({
				...defaultConfig
			});
		}
	}

	private async _dynamicRouter(userId: number) {
		// 获取跟路由
		const rootPath = this._router.getRoutes().find((item) => item.path == "/");

		// this._router.clearRoutes()
		// if (this._router.getRoutes().length == 2) {
		// 获取动态路由数据
		const data = await userMenuTree(userId);
		// 添加路由并绑定页面
		let menus = reconstructionRouter(data);
		menus = coverPathRouter(menus, "/", this._router, "Root", []);
		this._config.menus = menus;

		// 跟节点重定向到第一个菜单
		if (rootPath) {
			rootPath.redirect = menus[0].fullPath;
		}
		// }

		return this._config.menus;
	}

	// 获取全局项目列表
	async refreshProjectList() {
		const data = await selectList();
		this._config.projectList = data;
		if (data.length == 0) {
			this._config.selectProjectIndex = -1;
		}
	}

	private _interceptorAuthRouter() {
		this._router.beforeEach(async (to, _, next) => {
			NProgress.start();
			const breadcrumb = to.matched.reduce((str, item) => {
				if (item.meta && item.meta.title) {
					str += `${item.meta.title} > `;
				}

				return str;
			}, "");

			this._config.breadName = to.matched.slice(-1)[0]?.meta;
			this._config.breadcrumb = breadcrumb.slice(0, breadcrumb.length - 3);

			this._config.showSelectProject =
				to.fullPath.includes("/project") && !to.fullPath.includes("/list") && to.meta.isShow == true;

			if (this.isLogin) {
				next();
			} else {
				if (to.path !== "/login") {
					next({ path: "/login" });
				} else {
					next();
				}
			}
		});

		this._router.afterEach(() => {
			NProgress.done();
		});
	}

	setSelectProjectIndex(index: number) {
		this._config.selectProjectIndex = index;
		localStorage.setItem("projectIndex", index + "");
	}

	setSelectProjectTestIndex(index: number) {
		this._config.selectProjectTestIndex = index;
	}

	async actionsLogin(model: any) {
		const { jsdcToken, user } = await login(model);
		this._config.token = jsdcToken;
		this._config.workId = user.workId;

		getUserInfo().then(({ sysUser }) => {
			this._config.userInfo = sysUser
		})
		await Promise.all([this._dynamicRouter(user.id), this.refreshProjectList(), this.getDictMap()]);
		this._router.replace({ name: "Root" });
	}

	async getDictMap() {
		const dictMap = await sysDictPage();
		const dictSelfMap = Object.keys(dictMap).reduce((obj: DictSelfMap, key: string) => {
			const list = (Reflect.get(dictMap, key) as DictItemType[]).map((item) => {
				return {
					label: item.dictLabel,
					value: Number(item.dictValue)
				};
			});
			Reflect.set(obj, key, list);
			return obj;
		}, {} as DictSelfMap);
		this._config.dictMap = dictSelfMap;
	}

	logout() {
		this._config.token = "";
		localStorage.removeItem(_ProjectConfig);
		this._router.replace("/login");
	}

	async refreshView() {
		this._config.refreshView = true;
		await new Promise((resolve) => setTimeout(resolve, 200));
		this._config.refreshView = false;
	}

	destroy() {
		// console.log("销毁");
		// this._app = undefined;
		// this._app.unmount();
		// this._app = null;
		// this._router = null;
		// this._store = null;
	}

	get projectList() {
		return this._config.projectList;
	}

	get selectProjectIndex() {
		return this._config.selectProjectIndex;
	}

	get selectProjectTestIndex() {
		return this._config.selectProjectTestIndex;
	}

	// 定义一个getter方法，用于获取当前选中的项目项
	get projectItem() {
		// 在控制台输出当前选中的项目索引
		if (this._config.selectProjectIndex == -1) {
			return {
				id: 0
			};
		}

		// 返回项目列表中，当前选中索引对应的项目项
		return this._config.projectList[this._config.selectProjectIndex];
	}

	get showSelectList() {
		return this._config.showSelectProject;
	}
	get isLogin() {
		return !!this._config.token;
	}

	get breadcrumb() {
		return this._config.breadcrumb;
	}

	get userInfo() {
		return this._config.userInfo;
	}

	get breadName() {
		return this._config.breadName;
	}

	get menus() {
		return this._config.menus;
	}

	get isRefreshView() {
		return this._config.refreshView;
	}

	get loginToken() {
		return this._config.token;
	}

	get mapDict() {
		return this._config.dictMap;
	}

	get mitt() {
		return this._mitt;
	}
}
