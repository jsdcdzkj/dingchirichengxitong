<script lang="jsx" setup>
	import { useAppStore } from "@/store/app";
	import { DArrowLeft, DArrowRight, Moon, Sunny, Monitor } from "@element-plus/icons-vue";
	import { ElMenuItem, ElSubMenu } from "element-plus";
	import { ref, h, watch } from "vue";
	import { useRoute, useRouter } from "vue-router";
	import BaseChildMenus from "./BaseChildMenus";
	import BaseParentMenus from "./BaseParentMenus";
	import { toggleDark, isDark } from "@/composables";
	import Logo from "./logo.vue";
	import { useTheme } from "@/composables/dark";
	import { useUserStore } from "@/store/user";
	import { appInstance } from "@/main";
	import { downFile } from "@/core/utils";
	const { handeToggleDark } = useTheme();
	const app = useAppStore();
	const user = useUserStore();
	const route = useRoute();
	const router = useRouter();

	const cRoutes = ref([]);
	const defaultActive = ref("");
	const defaultOpend = ref("");

	const handleSelect = (e) => {
		router.push(e);
	};

	const handleChangeCollapse = () => {
		app.changeCollapse();
		appInstance.mitt.emit("changeCollapse", app.collapse);
	};

	watch(
		() => route.fullPath,
		(r) => {
			if (route.matched.length <= 1) return;

			cRoutes.value = appInstance.menus.find((item) => item.fullPath == route.matched[1].path).children;
			defaultActive.value = route.matched[1].path;
			defaultOpend.value = route.matched.slice(2).map((item) => item.path);
		},
		{ immediate: true, deep: true }
	);
</script>

<template>
	<div
		:class="`h-full ep-r-1px flex flex-col z-999 am ${app.collapse ? 'w-96px' : 'w-326px'}`"
		style="box-shadow: 1px 0px 3px 0px rgba(0, 0, 0, 0.12)"
	>
		<div class="collose" @click="handleChangeCollapse">
			<el-icon class="transition-none" v-if="!app.collapse"><DArrowLeft class="transition-none" /></el-icon>
			<el-icon class="transition-none" v-else><DArrowRight class="transition-none" /></el-icon>
		</div>
		<Logo :collapse="app.collapse" />
		<div class="flex-1 overflow-hidden flex flex-shrink-0">
			<div
				:class="[
					'w-96px h-full p-inline-16px p-t-24px p-b-24px flex flex-col flex-justify-between',
					app.collapse ? '' : 'ep-r-1px'
				]"
				style="transition: 0ms"
			>
				<BaseParentMenus
					:raw-list="appInstance.menus"
					:default-active="defaultActive"
					:collapse="app.collapse"
					:ch-list="appInstance.menus"
					:ch-active="route.fullPath"
					:defaultOpend="defaultOpend"
				/>
				<div>
					<button
						class="w-full cursor-pointer border-none bg-transparent btn-theme m-b-8px"
						style="height: var(--el-menu-item-height)"
						@click="downFile('/file/鼎驰工时系统.exe', '鼎驰工时系统.exe')"
					>
						<el-icon size="20px">
							<Monitor />
						</el-icon>
					</button>
					<button
						class="w-full cursor-pointer border-none bg-transparent btn-theme m-b-8px"
						style="height: var(--el-menu-item-height)"
						@click="handeToggleDark"
					>
						<el-icon size="20px">
							<Moon v-if="isDark"></Moon>
							<Sunny v-else></Sunny>
						</el-icon>
					</button>
					<UserItem />
				</div>
			</div>
			<BaseChildMenus :raw-list="cRoutes" :default-active="route.fullPath" :defaultOpend="defaultOpend" />
		</div>
	</div>
</template>

<style lang="scss" scoped>
	.ep-r-1px {
		border-right: solid 1px var(--el-menu-border-color);
	}

	.ep-b-1px {
		border-bottom: solid 1px var(--el-menu-border-color);
	}

	.h-full {
		position: relative;

		.collose {
			width: var(--el-slide-size);
			height: var(--el-slide-size);
			background: var(--el-color-white);
			box-shadow: 0px 1px 3px 0px rgba(64, 49, 49, 0.12);
			border-radius: var(--el-slide-size);
			border: 1px solid rgba(0, 0, 0, 0.06);
			box-sizing: border-box;
			position: absolute;
			top: calc(var(--el-menu-horizontal-height) - var(--el-slide-size) / 2);
			right: calc(var(--el-slide-size) / 2 - var(--el-slide-size));
			z-index: 888;
			cursor: pointer;
			text-align: center;
			line-height: var(--el-slide-size);
		}
	}
	.dark .h-full .collose {
		background: var(--el-menu-border-color);
	}
</style>
