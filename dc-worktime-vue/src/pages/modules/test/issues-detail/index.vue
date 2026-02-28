<template>
	<BasePage>
		<template #pageSearch>
			<ElCard class="custom-card">
				<template #header>
					<div class="card-header">
						<div class="flex flex-items-center gap-6px cursor-pointer">
							<div @click="() => appInstance.router.back()">
								<ElIcon size="20px">
									<Back />
								</ElIcon>
							</div>

							<span>任务标题</span>
							<ElTag type="success">bug任务标签</ElTag>
						</div>
					</div>
				</template>
				<div class="flex flex-items-center flex-justify-around p-10px">
					<div class="flex relative">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-circle-bg.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<span class="surname">王</span>
						<div class="flex flex-col gap-6px">
							<span style="font-weight: bold"> 王高攀</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 负责人 </span>
						</div>
					</div>
					<div class="flex">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-end.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold"> {{ pageData.dataSource.end_time }}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 截止日期 </span>
						</div>
					</div>
					<div class="flex">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-start.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold"> 2025-12-01 ～ 2025-12-31</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 实际起始时间 </span>
						</div>
					</div>
					<div class="flex">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-success.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold"> {{ pageData.dataSource.statusName }}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 当前状态 </span>
						</div>
					</div>
					<div class="flex">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-naozhong.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold">
								{{
									pageData.dataSource.working_hours !== null &&
									pageData.dataSource.working_hours !== undefined
										? pageData.dataSource.working_hours + "h"
										: "--"
								}}</span
							>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 所用工时 </span>
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
							<el-tab-pane label="未解决信息" name="second"></el-tab-pane>
							<el-tab-pane label="工时" name="third"></el-tab-pane>
							<el-tab-pane label="挂起信息" name="fourth"></el-tab-pane>
							<el-tab-pane label="完成信息" name="fifth"></el-tab-pane>
						</el-tabs>
					</div>
				</template>
			</TableHeader>
		</template>
		<template #pageBody>
			<div v-if="isLoading">加载中...</div>
			<Info v-if="pageData.activeName == 'first'" :dataSource="pageData.dataSource"> </Info>
			<Not v-else-if="pageData.activeName == 'second'" :dataSource="pageData.dataSource.testBugHistoryList">
			</Not>
			<Page v-else-if="pageData.activeName == 'third'"> </Page>
			<Pending v-else-if="pageData.activeName == 'fourth'"> </Pending>
			<History v-else></History>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { Back } from "@element-plus/icons-vue";
	import { reactive, onMounted, ref } from "vue";
	import { appInstance } from "@/main";
	import { useRoute } from "vue-router";
	import Page from "./page.vue";
	import Info from "./info.vue";
	import Not from "./not.vue";
	import Pending from "./pending.vue";
	import History from "./history.vue";
	import { createModelAsync } from "@/core/dialog";
	import { detailInfo } from "@/api/test/issues";
	const route = useRoute();
	const id = route.query.id as string;
	const pageData = reactive({
		dataSource: {},
		activeName: "first"
	});
	const isLoading = ref(true);

	const fetchData = async () => {
		if (id) {
			const res = await detailInfo(id);
			if (res.code === 200) {
				pageData.dataSource = res.data;
			}
			isLoading.value = false;
		}
	};

	onMounted(() => {
		fetchData();
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

<style lang="scss">
	.custom-card {
		.el-card__header {
			padding: 10px;
		}
	}
	.demo-tabs {
		position: absolute;
		bottom: -11px;
		:deep(.el-tabs__header) {
			margin-bottom: 0;
		}
		.el-tabs__nav-wrap:after {
			height: 0;
		}
	}
	.el-tabs__header {
		margin-bottom: 1px;
	}

	.surname {
		position: absolute;
		top: 0;
		left: 0;
		width: 48px;
		height: 48px;
		line-height: 48px;
		text-align: center;
		font-size: 20px;
		color: #fff;
	}
</style>
