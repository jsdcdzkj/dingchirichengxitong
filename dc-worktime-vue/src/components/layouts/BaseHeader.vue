<script lang="ts" setup>
	import { selectPageList } from "@/api/project/list";
	import { appInstance, bus } from "@/main";
	import { Menu, CaretBottom } from "@element-plus/icons-vue";
	import { computed, ref, watch } from "vue";
	import { Icon } from "@/components/icon/icon";
	import { useRoute } from "vue-router";
	import { SKey } from "@/pages/modules/project/plan/dialog/version";
	const isProject = ref(false);
	const route = useRoute();

	const handleCommand = (tab: number) => {
		if (tab != appInstance.selectProjectIndex) {
			localStorage.setItem(SKey, "");
			appInstance.setSelectProjectIndex(tab);
		}

		appInstance.refreshView();
	};
</script>

<template>
	<div class="layout-header">
		<ElDropdown v-if="appInstance.showSelectList" trigger="click" @command="handleCommand" :tabindex="0">
			<div class="flex flex-items-center gap10px">
				<div class="tag-text">项</div>
				<span class="projectName" v-if="appInstance.projectItem">{{
					appInstance.projectItem.projectName
				}}</span>
				<ElIcon>
					<CaretBottom />
				</ElIcon>
			</div>
			<template #dropdown>
				<el-dropdown-menu style="max-height: 600px; overflow-y: auto">
					<el-dropdown-item
						:command="index"
						style="justify-content: center"
						v-for="(item, index) in appInstance.projectList"
						:key="item.id"
						>{{ item.projectName }}</el-dropdown-item
					>
				</el-dropdown-menu>
			</template>
		</ElDropdown>

		<div class="flex flex-items-center gap10px" v-else>
			<div class="header-tag">
				<Icon
					:name="appInstance.breadName.icon"
					:size="{
						width: '20px',
						height: '20px'
					}"
				/>
			</div>
			{{ appInstance.breadName.title }}
		</div>

		<ElTag type="primary" size="large">{{ appInstance.breadcrumb }}</ElTag>
	</div>
</template>

<style lang="scss">
	.layout-header {
		height: var(--el-menu-item-height);
		background-color: var(--el-body-bg);
		display: flex;
		justify-content: space-between;
		align-items: center;
		cursor: pointer;
		padding: 0 var(--body-padding-size);
		.header-tag {
			width: 40px;
			height: 40px;
			background: var(--el-page-color);
			box-shadow: 0px 0px 12px 0px var(--el-body-shandow);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			text-align: center;
			line-height: 44px;
		}
		.el-tag--info {
			background-color: var(--el-page-color);
		}
	}

	.tag-text {
		width: 38px;
		height: 38px;
		background-color: var(--el-color-success);
		font-size: 16px;
		font-weight: bold;
		border-radius: 8px;
		line-height: 38px;
		text-align: center;
		color: var(--el-header-project-tag);
	}
	.projectName {
		color: var(--el-logo-color);
		font-weight: bold;
		font-size: 20px;
	}
</style>
