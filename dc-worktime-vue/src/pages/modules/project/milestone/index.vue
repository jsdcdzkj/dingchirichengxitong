<template>
	<BasePage>
		<template #pageSearch>
			<div class="page-search">
				<div class="flex gap-16px">
					<MoilestionItem icon="icon-1" :value="pageData.count.notReview" description="待评审里程碑" />
					<MoilestionItem icon="icon-2" :value="pageData.count.alreadyReview" description="已评审里程碑" />
					<MoilestionItem icon="icon-3" :value="pageData.count.postpone" description="延期里程碑" />
					<MoilestionItem icon="icon-4" :value="pageData.count.notPass" description="不通过里程碑" />
					<MoilestionItem icon="icon-5" :value="pageData.count.greatBug" description="严重问题里程碑" />
					<MoilestionItem icon="icon-6" :value="pageData.count.reviewAvg" description="评审平均分" />
				</div>
				<div class="p-bg">
					<ElSelect
						@change="handleChangeIteration"
						style="width: 350px"
						placeholder="请选择迭代"
						v-model:model-value="iteration.currentId"
					>
						<ElOption
							v-for="item in iteration.list"
							:key="item.id"
							:label="item.iteration_name"
							:value="item.id"
						>
						</ElOption>
					</ElSelect>
					<div class="p-progress">
						<ProjectProgress
							:progressList="projectMilestoneList"
							:progress="pageData.progress"
							time="appointFinishTime"
							label="milestoneName"
						/>
					</div>
				</div>
			</div>
		</template>
		<template #pageHeader>
			<TableHeader :title="`全部里程碑(共${pageData.dataSource.length}条)`">
				<ElSelect
					@change="handleChangeIteration"
					style="width: 350px"
					placeholder="请选择迭代"
					v-model:model-value="iteration.currentId"
				>
					<ElOption
						v-for="item in iteration.list"
						:key="item.id"
						:label="item.iteration_name"
						:value="item.id"
					>
					</ElOption>
				</ElSelect>
				<ElInput
					placeholder="搜索里程碑名称"
					:prefix-icon="Search"
					v-model:model-value="milestoneName"
					@keydown="handleKeyDown"
				></ElInput>
				<!-- <ElButton :icon="Plus" type="success" @click="handleAddMilestone">添加里程碑</ElButton> -->
			</TableHeader>
		</template>
		<template #pageBody>
			<ElTable
				border
				:row-class-name="rowClassMethod"
				:data="pageData.dataSource"
				@sort-change="handleSortMethod"
			>
				<!-- <ElTableColumn type="selection" width="55" align="center">
				</ElTableColumn> -->
				<ElTableColumn label="迭代版本" prop="iterationName" align="center" min-width="300px">
					<!-- <template #default="scope"> -->
					<!-- <el-link link type="primary" @click="handleJumpDetail(scope.row)">{{ -->
					<!-- scope.row.iterationName -->
					<!-- }}</el-link> -->
					<!-- </template> -->
				</ElTableColumn>
				<ElTableColumn label="里程碑名称" align="center" min-width="300px">
					<template #default="scope">
						<el-link link type="primary" @click="handleJumpDetail(scope.row)">{{
							scope.row.milestoneName
						}}</el-link>
					</template>
				</ElTableColumn>
				<ElTableColumn label="评审阶段" align="center" min-width="300px">
					<template #default="scope">
						<el-link v-if="scope.row.groupType == 1" link>需求阶段</el-link>
						<el-link v-else-if="scope.row.groupType == 2" link>设计阶段</el-link>
						<el-link v-else-if="scope.row.groupType == 3" link>开发阶段</el-link>
						<el-link v-else-if="scope.row.groupType == 4" link>内部评审阶段</el-link>
						<el-link v-else link>--</el-link>
					</template>
				</ElTableColumn>
				<ElTableColumn sortable label="状态" width="100px" align="center" prop="state">
					<template #default="scope">
						<ElTag disable-transitions type="success" v-if="scope.row.state == 1">已完成</ElTag>
						<ElTag disable-transitions type="warning" v-else>未完成</ElTag>
					</template>
				</ElTableColumn>
				<ElTableColumn sortable label="约定结束日期" width="140px" prop="appointFinishTime" align="center">
				</ElTableColumn>
				<ElTableColumn sortable label="实际结束日期" width="180px" prop="actualFinishTime" align="center">
				</ElTableColumn>
				<ElTableColumn label="交付物" align="center" width="100px">
					<template #default="scope">
						<ElLink>{{ scope.row.deliverableState }}</ElLink>
					</template>
				</ElTableColumn>
				<ElTableColumn label="结果" width="100px" align="center">
					<template #default="scope">
						<template v-if="scope.row.proReview">
							<ElTag
								disable-transitions
								type="success"
								v-if="Number(scope.row.proReview.reviewTotalPoints) >= 60"
								>通过</ElTag
							>
							<ElTag disable-transitions type="warning" v-else>未通过</ElTag>
						</template>
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<ElTableColumn label="延期情况" width="100px" align="center">
					<template #default="scope">
						<template v-if="scope.row.proReview">
							<span v-if="Number(scope.row.proReview.milestonePostponeDay) > 0"> 延期完成 </span>
							<span v-else-if="Number(scope.row.proReview.milestonePostponeDay) < 0"> 提前完成 </span>
							<span v-else>正常完成</span>
						</template>
						<span v-else>--</span>
					</template>

					<!-- Number(item.proReview.milestonePostponeDay) > 0 -->
				</ElTableColumn>
				<!-- <ElTableColumn label="问题数量" width="100px" align="center">
					<template #default="scope">
						<ElTag type="primary" v-if="scope.row.state == 1">已完成</ElTag>
						<ElTag type="danger" v-else>未完成</ElTag>
					</template>
				</ElTableColumn> -->
				<ElTableColumn label="分数" width="100px" align="center">
					<template #default="scope">
						<template v-if="scope.row.proReview">
							<span> {{ scope.row.proReview.reviewTotalPoints }} </span>
						</template>
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<ElTableColumn label="关联计划或阶段" width="200px" align="center">
					<template #default="scope">
						<template v-if="scope.row.planId">
							<div
								@click="handlePlanDetail(scope.row)"
								class="flex items-center gap-4px flex-justify-center gap-4px cursor-pointer"
							>
								<Icon name="icon-plan.png" :size="{ width: '16px', height: '16px' }" />
								<span
									style="
										white-space: nowrap;
										display: inline-block;
										max-width: 200px;
										overflow: hidden;
										text-overflow: ellipsis;
									"
									class="text-ellipsis-1"
								>
									{{ scope.row.planName }}
								</span>
							</div>
						</template>
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<ElTableColumn label="问题数量" width="300px" align="center">
					<template #default="scope">
						<template v-if="scope.row.proReview">
							<span>
								轻微数量: {{ scope.row.proReview.testBugs.filter((item) => item.severity == 1).length }}
							</span>
							<span>
								一般数量: {{ scope.row.proReview.testBugs.filter((item) => item.severity == 2).length }}
							</span>
							<span>
								重大数量: {{ scope.row.proReview.testBugs.filter((item) => item.severity == 3).length }}
							</span>
						</template>
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<!-- <ElTableColumn label="操作" align="center" width="200px" fixed="right">
					<template #default="scope">
						<ElButton plain size="small" @click="handleEditM(scope.row)" type="primary">编辑</ElButton>
						<ElButton
							v-if="scope.row.state != 1"
							type="danger"
							plain
							size="small"
							@click="handleDelete(scope.row)"
							>删除</ElButton
						>
					</template>
				</ElTableColumn> -->
			</ElTable>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import BasePage from "@/core/struct/page/base-page";
	import ProjectProgress from "@/components/module/project/project-progress";
	import TableHeader from "@/core/struct/table/table-header";
	import { Search } from "@element-plus/icons-vue";
	import { reactive, ref, onMounted } from "vue";
	import { createDrawerAsync } from "@/core/dialog";

	import { appInstance } from "@/main";
	import { selectList, countMilestone } from "@/api/project/milestone";
	import { ElLink } from "element-plus";
	import MoilestionItem from "./milestone-item.vue";
	import { Icon } from "@/components/icon/icon";
	import PlanDetail from "../plan-detail/index.vue";
	import { useVersion } from "@/pages/modules/project/plan/dialog/version.tsx";
	import { useProgress } from "./progress";
	const milestoneName = ref("");
	const { iteration, getVersionList, setSKey, getIterationId } = useVersion(true);

	const { projectMilestoneList, refreshProjectStatus } = useProgress("p-progress");

	const pageData = reactive({
		dataSource: [],
		projectMilestoneList: [],
		progress: 0,
		count: {
			alreadyReview: 0,
			greatBug: 0,
			reviewAvg: 0,
			notPass: 0,
			notReview: 0,
			postpone: 0
		},
		sort: {} as { sortActualFinishTime?: number; sortState?: number; sortAppointFinishTime?: number }
	});

	// 切换迭代
	const handleChangeIteration = (e: number) => {
		setSKey(e);
		refreshTable();
		refreshProjectStatus(getIterationId().id);
		countMilestone({ projectId: appInstance.projectItem.id, iterationId: getIterationId().id }).then(({ data }) => {
			pageData.count = data;
		});
	};

	const rowClassMethod = ({ rowIndex }: any) => {
		if (rowIndex % 2 != 0) {
			return "warning-row";
		} else {
			return "success-row";
		}
	};

	// 回车查询
	const handleKeyDown = (e: KeyboardEvent) => {
		if (e.key == "Enter") {
			refreshTable();
		}
	};

	const handleSortMethod = ({ prop, order }: any) => {
		if (prop == "state") {
			pageData.sort = order == "ascending" ? { sortState: 1 } : { sortState: 2 };
		} else if (prop == "appointFinishTime") {
			pageData.sort = order == "ascending" ? { sortAppointFinishTime: 1 } : { sortAppointFinishTime: 2 };
		} else if (prop == "actualFinishTime") {
			pageData.sort = order == "ascending" ? { sortActualFinishTime: 1 } : { sortActualFinishTime: 2 };
		}
		refreshTable();
	};

	const handlePlanDetail = (row: any) => {
		createDrawerAsync(
			{
				title: "计划详情",
				width: "1280px"
			},
			{},
			<PlanDetail id={row.planId} query_detail_status={1} />
		);
	};

	const refreshTable = () => {
		selectList({
			milestoneName: milestoneName.value,
			projectId: appInstance.projectItem.id,
			iteration_id: getIterationId().id,
			...pageData.sort
		}).then((res) => {
			pageData.dataSource = res;
		});
	};

	const handleJumpDetail = (row: any) => {
		appInstance.router.push({
			path: "/project/milestone-detail",
			query: {
				id: row.id
			}
		});
	};

	refreshTable();
	getVersionList();
	onMounted(() => {
		refreshProjectStatus(getIterationId().id);
		countMilestone({ projectId: appInstance.projectItem.id, iterationId: getIterationId().id }).then(({ data }) => {
			pageData.count = data;
		});
	});
</script>

<style lang="scss" scoped>
	.page-search {
		width: 100%;
		border-radius: 6px;

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
	.p-bg {
		margin-top: 12px;
		background: var(--el-page-color);
		box-shadow: 0px 0px 7px 0px rgba(0, 0, 0, 0.05);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		padding: 20px 20px;
		height: 296px;
		display: flex;

		flex-direction: column;
		overflow: hidden;
		width: 100%;
		gap: 30px;
		.p-progress {
			overflow: hidden;
			width: 100%;
		}
	}
	.text-ellipsis-1 {
		font-size: 14px;
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
		color: var(--el-color-primary);
		text-decoration: underline;
	}
</style>
