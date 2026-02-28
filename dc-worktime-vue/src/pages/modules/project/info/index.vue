<template>
	<div>
		<!--  项目里程碑 -->
		<ElRow :gutter="16">
			<ElCol :span="24">
				<ElCard header="项目里程碑" class="custom-header">
					<div style="min-height: 180px; width: 100%; overflow: hidden" class="bg-rp">
						<ProjectProgress
							time="appointFinishTime"
							label="milestoneName"
							:progressList="projectMilestoneList"
							:progress="pageData.progress"
						/>
					</div>
				</ElCard>
			</ElCol>
		</ElRow>

		<ElRow :gutter="16" class="m-t-16px">
			<!-- 项目总览与预警  -->
			<ElCol :span="12">
				<OverallWarning />
			</ElCol>
			<!-- 项目工时总览 -->
			<ElCol :span="12">
				<OverallWorkingHours />
			</ElCol>
		</ElRow>
		<!-- 当日任务总览 -->
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<OverallTodayTask />
			</ElCol>
		</ElRow>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="12">
				<OverallProjectAnly />
			</ElCol>
			<ElCol :span="12">
				<OverallDeveloperAnly />
			</ElCol>
		</ElRow>
		<!-- 项目详情 -->
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<ElCard header="项目详情" class="custom-header">
					<ElRow :gutter="12">
						<ElCol :span="8">
							<div class="flex flex-col gap-10px">
								<Title title="基本" titleIcon="icon-wenjian.png"></Title>
								<CellItem label="项目用户" :value="appInstance.projectItem.projectUser"> </CellItem>
								<CellItem label="业务负责人" :value="appInstance.projectItem.businessManagerName">
								</CellItem>
							</div>
						</ElCol>
						<ElCol :span="8">
							<div class="flex flex-col gap-10px">
								<Title title="回款" titleIcon="icon-wenjian.png"></Title>
								<CellItem
									label="是否回款"
									:value="Number(appInstance.projectItem.amountReceived) > 0 ? '是' : '否'"
								>
								</CellItem>
								<CellItem label="已回款金额" :value="appInstance.projectItem.amountReceived">
								</CellItem>
								<CellItem
									label="未回款金额"
									:value="
										Number(appInstance.projectItem.contractAmount) -
										Number(appInstance.projectItem.amountReceived)
									"
								>
								</CellItem>
							</div>
						</ElCol>
						<ElCol :span="8">
							<div class="flex flex-col gap-10px">
								<Title title="合同" titleIcon="icon-wenjian.png"></Title>
								<CellItem
									label="是否已签订合同"
									:value="appInstance.projectItem.isContract == 1 ? '是' : '否'"
								>
								</CellItem>
								<CellItem
									label="是否签订内部协议"
									:value="appInstance.projectItem.isProtocol == 1 ? '是' : '否'"
								>
								</CellItem>
								<CellItem label="合同/协议签订日期" :value="appInstance.projectItem.contractTime">
								</CellItem>
							</div>
						</ElCol>
					</ElRow>
					<ElRow :gutter="12" class="m-t-10px">
						<ElCol :span="24">
							<div class="flex flex-col gap-10px">
								<Title title="附件" titleIcon="icon-wenjian.png"></Title>
								<CellItem label="备注" :value="appInstance.projectItem.remark"> </CellItem>
								<CellItem label="项目合同/内部协议">
									<template #default>
										<div
											class="inline-flex gap-10px"
											style="flex-wrap: wrap; overflow-y: auto; overflow-x: hidden"
										>
											<ElLink
												@click="downFile(item.fileUrl, item.fileName)"
												type="primary"
												v-for="item in appInstance.projectItem.files"
											>
												{{ item.fileName }}
											</ElLink>
										</div>
									</template>
								</CellItem>
							</div>
						</ElCol>
					</ElRow>
				</ElCard>
			</ElCol>
		</ElRow>
	</div>
</template>

<script setup lang="ts">
	import ProjectProgress from "@/components/module/project/project-progress";
	import { onMounted, reactive } from "vue";
	import { appInstance } from "@/main";

	import { underwayPlan } from "@/api/project/list";
	import { useProgress } from "../milestone/progress";

	import OverallWarning from "@/components/project-info/modules/overall-warning/index.vue";
	import { downFile } from "@/core/utils";
	import { useAppStore } from "@/store/app";
	const { projectMilestoneList, refreshProjectStatus } = useProgress("bg-rp");
	const pageData = reactive({
		projectMilestoneList: [],
		progress: 0,
		deliverableCount: { delivery: 0, nonDelivery: 0 },
		planIngList: []
	});
	const app = useAppStore();
	const handleChangeCollapse = () => {
		if (!app.collapse) {
			app.changeCollapse();
			appInstance.mitt.emit("changeCollapse", app.collapse);
		}
	};

	// onMounted(() => {
	handleChangeCollapse();
	// });
	onMounted(() => {
		refreshProjectStatus();
	});

	underwayPlan({ projectId: appInstance.projectItem.id }).then((res) => {
		pageData.planIngList = res;
	});
</script>

<style lang="scss" scoped>
	.border-0px {
		.el-descriptions__body .el-descriptions__table.is-bordered .el-descriptions__cell {
			border: none;
		}
		.el-descriptions__label.el-descriptions__cell.is-bordered-label {
			background: transparent;
		}
	}
</style>

<style lang="css">
	.custom-header {
		.el-card__header {
			text-align: left;
			font-weight: bold;
		}
	}
</style>
