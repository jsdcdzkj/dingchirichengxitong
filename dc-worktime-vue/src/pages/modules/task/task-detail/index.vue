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
							<span class="m-l-10px">{{
								pageData.detailInfo.title || pageData.detailInfo.name || pageData.detailInfo.bug_title
							}}</span>
							<ElTag class="m-l-10px" type="success">{{
								type == 1
									? "计划任务标签"
									: type == 2
										? "bug任务标签"
										: type == 3
											? "运维任务标签"
										: type == 4
											? "领导交办的任务标签"
											: "会议任务标签"
							}}</ElTag>
						</div>
					</div>
				</template>
				<div class="flex flex-items-center p-10px">
					<div class="flex flex-1">
						<div style="position: relative">
							<img
								w="48px"
								h="48px"
								src="../../../../assets/milestone/icon-circle-bg.png"
								alt=""
								srcset=""
								class="m-r-10px"
							/>
							<span class="name">{{ pageData.detailInfo.assigned_to_user_name?.substring(0, 1) || pageData.detailInfo.userName?.substring(0, 1)}}</span>
						</div>

						<div class="flex flex-col gap-6px">
							<span style="font-weight: bold">{{
								pageData.detailInfo.assigned_to_user_name || pageData.detailInfo.userName || "--"
							}}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 负责人 </span>
						</div>
					</div>
					<div class="flex flex-1">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-end.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold">{{
								pageData.detailInfo.end_time?.substring(0, 10) || pageData.detailInfo.meet_end_time?.substring(0, 10) || "--"
							}}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 截止日期 </span>
						</div>
					</div>
					<div class="flex flex-1">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-start.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold; display: flex">
								<img
									w="22px"
									h="22px"
									class="m-r-8px"
									src="../../../../assets/milestone/start-time.png"
								/>
								{{ pageData.detailInfo.start_time?.substring(0, 10) || "--" }}
							</span>
							<span style="font-weight: bold; display: flex">
								<img
									w="22px"
									h="22px"
									class="m-r-8px"
									src="../../../../assets/milestone/end-time.png"
								/>
								{{ pageData.detailInfo.completion_time?.substring(0, 10) || "--" }}
							</span>
						</div>
					</div>
					<div class="flex flex-1">
						<img
							w="48px"
							h="48px"
							src="../../../../assets/milestone/icon-success.png"
							alt=""
							srcset=""
							class="m-r-10px"
						/>
						<div class="flex flex-col gap-6px flex-items-start">
							<span style="font-weight: bold"> {{ pageData.detailInfo.statusName || "--" }}</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 当前状态 </span>
						</div>
					</div>
					<div class="flex flex-1">
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
								{{ pageData.detailInfo.workHours ? pageData.detailInfo.workHours : "--" }}
							</span>
							<span style="color: rgba(0, 0, 0, 0.65); font-size: 14px"> 所用工时 </span>
						</div>
					</div>
				</div>
			</ElCard>
		</template>
		<template #pageBody>
			<div :class="{ 'p-l-24px': isRouteData, 'p-r-24px': isRouteData }" style="height: 100%; overflow-y: auto">
				<DevelopTask :detailInfo="pageData.detailInfo" :refreshSearch="refreshSearch" v-if="type == 1"></DevelopTask>
				<BugDetail
					:detailInfo="pageData.detailInfo"
					@updateDetailInfo="updateDetailInfo"
					@updateShowBtn="updateShowBtn"
					:showBtn="pageData.showBtn"
					:refreshParentParentPage="props.refreshParentParentPage"
					:testType="pageData.detailInfo.assigned_to_user_type"
					v-if="type == 2"
				></BugDetail>
				<OperationTask :detailInfo="pageData.detailInfo" v-if="type == 3"></OperationTask>
				<LeaderTask :detailInfo="pageData.detailInfo" v-if="type == 4"></LeaderTask>
				<MeetTask :detailInfo="pageData.detailInfo" v-if="type == 5"></MeetTask>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { Back } from "@element-plus/icons-vue";
	import { onMounted, reactive } from "vue";
	import { appInstance } from "@/main";
	import BugDetail from "./bugDetail/index.vue"; // bug详情
	import DevelopTask from "./developTask/index.vue"; // 前端和后端开发详情
	import LeaderTask from "./leaderTask/index.vue"; // 领导交办任务详情
	import OperationTask from "./operationTask/index.vue"; // 运维任务详情
	import MeetTask from "./meetTask/index.vue"; // 运维任务详情
	import ProductTask from "./productTask/index.vue"; // 产品和ui任务详情

	import { useRoute } from "vue-router";
	import { createModelAsync } from "@/core/dialog";
	import MilestoneRate from "./dialog/milestone-rate.vue";
	import { getEntity } from "@/api/task/await-assigned";
	import { getEntityLeader } from "@/api/task/leaders-assigned";
	import { getEntityOperation } from "@/api/task/operation-maintenance";
	import { detailBug, getMeetEntity } from "@/api/task/kanban";

	const pageData = reactive({
		dataSource: [{}, {}, {}, {}, {}],
		activeName: "first",
		detailInfo: {},
		showBtn: false
	});
	const props = defineProps({
		id: {
			type: Number,
			default: ""
		},
		type: {
			type: Number,
			default: ""
		},
		showBtn: {
			type: Boolean,
			default: false
		},
		iskanban: {
			type: Boolean,
			default: false
		},
		refreshParentParentPage: {
			type: Function,
			default: () => () => {}
		},
		refreshSearch: {
			type: Function,
			default: () => () => {}
		},
		// testType: {
		// 	type: String,
		// 	default: ""
		// }
	});

	const route = useRoute();
	const isRouteData = !!route?.query.id;
	const id = Number(route?.query.id) || props.id;
	const type = route?.query.type || props.type;
	const showBtn = route?.query.showBtn || props.showBtn;
	const refreshParentParentPage = route?.query.refreshParentParentPage || props.refreshParentParentPage;
	if (type == "4") {
		getEntityLeader(id).then((res) => {
			pageData.detailInfo = res;
		});
	} else if (type == "3") {
		getEntityOperation(id).then((res) => {
			pageData.detailInfo = res;
		});
	} else if (type == "2") {
		detailBug(id).then((res) => {
			pageData.detailInfo = res;
			pageData.showBtn = showBtn;
			if (typeof props.refreshParentParentPage === "function") {
				props.refreshParentParentPage();
			}
		});
	} else if(type == "1"){
		console.log('11111111111refreshSearch', props.refreshSearch)
		getEntity(id).then((res) => {
			pageData.detailInfo = {...res, iskanban: props.iskanban};
		});
	} else {
		getMeetEntity(id).then((res) => {
			pageData.detailInfo = res;
			pageData.detailInfo.start_time = res.meet_start_time
			pageData.detailInfo.completion_time = res.meet_completion_time
		});
	}
	const updateDetailInfo = (newShowBtn: any) => {
		pageData.detailInfo = newShowBtn;
	};
	const updateShowBtn = (newShowBtn: any) => {
		pageData.showBtn = newShowBtn;
	};
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
	.name {
		position: absolute;
		top: 21%;
		left: 25%;
		font-size: 18px;
		color: #fff;
	}
</style>

<style>
	.custom-card {
		background: var(--el-page-color);
		.el-card__header {
			padding: 16px;
		}
	}
	.demo-tabs {
		position: absolute;
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
