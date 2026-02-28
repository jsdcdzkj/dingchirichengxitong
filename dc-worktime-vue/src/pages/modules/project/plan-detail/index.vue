<template>
	<BasePage>
		<template #pageSearch>
			<ElCard class="custom-card">
				<template #header v-if="!id">
					<div class="card-header">
						<div class="flex flex-items-center gap-6px cursor-pointer">
							<div @click="() => appInstance.router.go(-1)">
								<ElIcon size="20px">
									<Back />
								</ElIcon>
							</div>
							<span>{{ pageData.detail?.plan_title }}</span>
							<ElTag type="success">计划</ElTag>
						</div>
					</div>
				</template>
				<div class="flex flex-items-center flex-justify-around p-10px">
					<div class="flex">
						<div class="label-circle primary">{{ pageData.detail?.person_in_charge_name.slice(0, 1) }}</div>
						<div class="flex flex-col gap-6px">
							<span style="ffont-weight: bold"> {{ pageData.detail?.person_in_charge_name }}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 负责人 </span>
						</div>
					</div>
					<div class="flex">
						<div class="label-circle warning">{{ pageData.detail?.progress || "0%" }}</div>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="ffont-weight: bold">{{ pageData.detail?.progress || "0%" }}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 当前进度 </span>
						</div>
					</div>
					<div class="flex">
						<!-- <div class="label-circle danger"></div> -->
						<img src="@/assets/project/plan-time.png" class="label-circle" alt="" />
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="ffont-weight: bold">
								{{ pageData.detail?.plan_start_date }} ～
								{{ pageData.detail?.parent_plan_end_date }}</span
							>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 计划日期 </span>
						</div>
					</div>
					<div class="flex">
						<!-- <div class="label-circle info"></div> -->
						<img src="@/assets/project/real-time.png" class="label-circle" alt="" />
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="ffont-weight: bold">
								{{ pageData.detail?.actual_start_date }} ～ {{ pageData.detail?.actual_end_date }}</span
							>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 实际时间 </span>
						</div>
					</div>
				</div>
			</ElCard>
		</template>
		<template #pageHeader>
			<TableHeader>
				<template #title>
					<div h="25px" style="position: relative">
						<el-tabs v-model="pageData.activeName" class="demo-tabs">
							<el-tab-pane label="详情" name="first"></el-tab-pane>
							<el-tab-pane label="关联内容" name="second"></el-tab-pane>
							<el-tab-pane label="完成信息" name="third"></el-tab-pane>
						</el-tabs>
					</div>
				</template>
			</TableHeader>
		</template>
		<template #pageBody>
			<Info
				v-if="pageData.activeName == 'first'"
				v-bind="pageData.detail"
				@update="handleUpdate"
				:id="task_info_id"
			>
			</Info>
			<Page
				:planId="task_info_id"
				v-else-if="pageData.activeName == 'second'"
				:modules="pageData.detail?.modules"
			>
			</Page>
			<Success
				v-else
				:actual_start_date="pageData.detail?.actual_start_date"
				:actual_end_date="pageData.detail?.actual_end_date"
			/>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { Back } from "@element-plus/icons-vue";
	import { reactive } from "vue";
	import { appInstance } from "@/main";
	import Page from "./page.vue";
	import Info from "./info.vue";
	import Success from "./success.vue";
	import { useRoute } from "vue-router";
	import { ElCard, ElTabs, ElTabPane, ElTag, ElIcon } from "element-plus";
	import { editPlan, getEntity, selectPlanDetail } from "@/api/project/plan";
	import { number } from "vue-types";

	const route = useRoute();

	const props = defineProps({
		id: {
			type: String,
			default: ""
		},
		query_detail_status: number().def()
	});

	const task_info_id = props.id || Number(route.query.id);

	const pageData = reactive({
		dataSource: [{}, {}, {}, {}, {}],
		activeName: "first",
		detail: null as any
	});
	const handleUpdate = (value: any) => {
		editPlan({
			id: task_info_id,
			description: value,
			project_id: appInstance.projectItem.id,
			plan_title: pageData.detail.plan_title,
			person_in_charge: pageData.detail.person_in_charge,
			plan_start_date: pageData.detail.plan_start_date,
			plan_end_date: pageData.detail.plan_end_date,
			plan_type: pageData.detail.plan_type,
			parent_id: pageData.detail.parent_id
		}).then(() => {
			selectPlanDetail({ id: task_info_id }).then((res) => {
				pageData.detail = res;
			});
		});
	};

	const refreshPlanTree = (res: any) => {
		// 数据重新组装
		function coverTree(list: any[]) {
			for (const item of list) {
				Reflect.set(item, "type", 1);
				Reflect.set(item, "id", Math.random().toString(16).slice(0, 12));
				if (item.children && item.children.length == 0) {
					if (item.functions.length > 0) {
						item.children = item.functions.map((item: any) => {
							return {
								moduleName: item.feature_name,
								type: 2,
								id: item.id
							};
						});
					} else {
						item.disabled = true;
					}
				} else {
					coverTree(item.children);
				}
			}
		}
		if (res.modules && res.modules.length > 0) {
			coverTree(res.modules);
		}
	};

	selectPlanDetail({ id: task_info_id, query_detail_status: props.query_detail_status }).then((res) => {
		refreshPlanTree(res);
		pageData.detail = res;
	});
</script>

<style lang="scss" scoped>
	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.page-search {
		height: 328px;
		width: 100%;
		background-color: var(--el-page-color);
		border-radius: 6px;
		padding: 32px;
		.title {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 36px;
			margin: auto;
			width: 200px;
			height: 20px;
			color: #fff;
		}
	}
</style>

<style>
	.custom-card {
		background: #fff;
		.el-card__header {
			padding: 16px;
		}
	}
	.demo-tabs {
		position: absolute;
		bottom: -9px;
		.el-tabs__header {
			margin-bottom: 0;
		}
		.el-tabs__nav-wrap:after {
			height: 0;
		}
	}
	.el-tabs__header {
		margin-bottom: 0;
	}
</style>

<style lang="scss" scoped>
	.label-circle {
		width: 48px;
		height: 48px;
		border-radius: 24px;
		text-align: center;
		line-height: 48px;
		color: #fff;
		font-weight: bold;
		font-size: 18px;
		margin-right: 10px;
	}

	.primary {
		background: #409eff;
	}
	.warning {
		background: #f5a250;
	}

	.danger {
		background: #f56c6c;
	}

	.info {
		background: #40bfa8;
	}
</style>
