<template>
	<BasePage>
		<template #pageSearch>
			<ElCard class="custom-card">
				<template #header>
					<div class="card-header">
						<div class="flex flex-items-center gap-6px cursor-pointer">
							<div @click="() => appInstance.router.back()" v-if="!id">
								<ElIcon size="20px">
									<Back />
								</ElIcon>
							</div>

							<span>{{ pageData.detail?.milestoneName }}</span>
							<ElTag type="success">里程碑</ElTag>
						</div>
					</div>
				</template>
				<div class="flex flex-items-center flex-justify-around p-10px">
					<div class="flex">
						<div class="icon-circle m-r-10px">
							{{ pageData.detail?.personChargeName.slice(0, 1) }}
						</div>
						<div class="flex flex-col gap-6px items-start">
							<span style="font-weight: bold"> {{ pageData.detail?.personChargeName }} </span>
							<span style="font-size: 14px"> 负责人 </span>
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
							<span style="ffont-weight: bold">{{ pageData.detail?.appointFinishTime }}</span>
							<span style="font-size: 14px"> 约定完成时间 </span>
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
							<span style="ffont-weight: bold"> {{ pageData.detail?.actualFinishTime }}</span>
							<span style="font-size: 14px"> 实际完成时间 </span>
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
							<span style="ffont-weight: bold">
								{{ pageData.detail?.state == 1 ? "已完成" : "未完成" }}</span
							>
							<span style="font-size: 14px"> 当前状态 </span>
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
							<el-tab-pane label="交付物" name="first"></el-tab-pane>
							<el-tab-pane label="详情" name="second"></el-tab-pane>
							<el-tab-pane
								label="评审记录"
								name="third"
								v-if="pageData.detail?.proReview && pageData.detail?.proReview.isTemporarily == 0"
							></el-tab-pane>
						</el-tabs>
					</div>
				</template>
				<ElButton
					type="primary"
					plain
					@click="handleToWu(pageData.detail?.proReview)"
					v-if="
						!pageData.detail?.proReview ||
						(pageData.detail?.proReview && pageData.detail?.proReview.isTemporarily == 1)
					"
				>
					里程碑评审
				</ElButton>
				<div v-else>
					<ElButton type="primary" @click="handleDownloadFile"> 下载评审表 </ElButton>
				</div>
			</TableHeader>
		</template>
		<template #pageBody>
			<Page :milestoneId="milestoneId" dialog v-if="pageData.activeName == 'first'"> </Page>
			<Info
				v-bind="pageData.detail"
				@update="handleUpdate"
				:id="milestoneId"
				v-else-if="pageData.activeName == 'second'"
			>
			</Info>
			<History :detail="pageData.detail" v-else></History>
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
	import History from "./history.vue";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import MilestoneRate from "./dialog/milestone-rate.vue";
	import { useRoute } from "vue-router";
	import { getEntityById, reviewExport } from "@/api/project/milestone";
	import { downFile } from "@/core/utils";
	import { ElCard, ElTabs, ElTabPane, ElButton, ElTag, ElIcon } from "element-plus";
	const pageData = reactive({
		dataSource: [{}, {}, {}, {}, {}],
		activeName: "first",
		detail: null as any,
		proReview: null as any
	});
	import { bool } from "vue-types";

	const props = defineProps({
		id: {
			type: String,
			default: ""
		},
		dialog: bool().def(false)
	});
	const route = useRoute();
	const milestoneId = props.id || Number(route.query.id);
	const handleToWu = (proReview?: any) => {
		createDrawerAsync(
			{
				title: "里程碑评审",
				width: "1000px",
				showZanCun: true,
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<MilestoneRate id={milestoneId} groupType={pageData.detail?.groupType} />
		).then(() => {
			getEntityById({ id: milestoneId }).then((res) => {
				pageData.detail = res;
			});
		});
	};
	const handleDownloadFile = () => {
		if (pageData.detail?.proReview) {
			let name = "评审表.xlsx";
			if (pageData.detail?.groupType == 1) {
				name = "需求阶段验收单会议纪要.xlsx";
			} else if (pageData.detail?.groupType == 2) {
				name = "设计阶段验收单会议纪要.xlsx";
			} else if (pageData.detail?.groupType == 3) {
				name = "开发阶段验收单会议纪要.xlsx";
			} else if (pageData.detail?.groupType == 4) {
				name = "内部验收阶段验收单会议纪要.xlsx";
			}

			reviewExport({
				projectId: appInstance.projectItem.id,
				id: pageData.detail.proReview.id
			}).then((data) => {
				downFile(URL.createObjectURL(data), name);
			});
		}
	};
	getEntityById({ id: milestoneId }).then((res) => {
		pageData.detail = res;
	});
	const handleUpdate = () => {
		getEntityById({ id: milestoneId }).then((res) => {
			pageData.detail = res;
		});
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
	.icon-circle {
		width: 48px;
		height: 48px;
		border-radius: 24px;
		background-image: url(../../../../assets/milestone/icon-circle-bg.png);
		background-repeat: repeat;
		background-size: cover;
		color: #fff;
		line-height: 48px;
		text-align: center;
		font-size: 20px;
		font-weight: bold;
	}
</style>

<style>
	.custom-card {
		.el-card__header {
			padding: 10px;
		}
	}
	.demo-tabs {
		position: absolute;
		bottom: -11px;
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
