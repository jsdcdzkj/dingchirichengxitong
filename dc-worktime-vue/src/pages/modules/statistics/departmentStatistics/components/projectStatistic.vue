<template>
	<div>
		<ElCard header="各项目进度分析" class="custom-header table-box-separate">
			<!-- <TodayTaskTable ref="projectProgress" :columns="columns" :api="analysisPageList" :params="selectProject" /> -->
			<ElTable :data="tableData.data" style="width: 100%" height="370" stripe ref="tableRef">
				<ElTableColumn
					prop="projectName"
					show-overflow-tooltip
					label="项目名称"
					min-width="220"
					align="center"
				/>
				<ElTableColumn prop="projectStatus" label="项目状态" width="120" align="center">
					<template #default="scope">
						<ElTag v-if="scope.row.projectStatus == 1" type="warning">未开始</ElTag>
						<ElTag v-else-if="scope.row.projectStatus == 2" type="primary">进行中</ElTag>
						<ElTag v-else type="success">已完成</ElTag>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="projectSchedule" label="项目进度" width="160" align="center">
					<template #default="scope">
						<ElProgress v-if="scope.row.projectSchedule" :percentage="Number(scope.row.projectSchedule)" />
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="projectManagerName" label="项目经理" width="140" align="center">
					<template #default="scope"
						><span>{{ scope.row.projectManagerName ? scope.row.projectManagerName : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="projectEndTime" label="计划完成时间" width="140" align="center">
					<template #default="scope"
						><span>{{ scope.row.projectEndTime ? scope.row.projectEndTime : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="expectFinish" label="预测完成时间" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.expectFinish ? scope.row.expectFinish : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="risk" label="延期风险" width="120" align="center">
					<template #default="scope">
						<span class="flex flex-items-center flex-justify-center" v-if="scope.row.risk == '延期完成'">
							<ElIcon size="14px" color="#F56C6C" class="m-r-4px"><WarningFilled /></ElIcon>
							延期完成
						</span>
						<span
							class="flex flex-items-center flex-justify-center"
							v-else-if="scope.row.risk == '提前完成'"
						>
							<ElIcon size="14px" color="#409eff" class="m-r-4px"><CircleCheckFilled /></ElIcon>
							提前完成
						</span>
						<span
							class="flex flex-items-center flex-justify-center"
							v-else-if="scope.row.risk == '准时完成'"
						>
							<ElIcon size="14px" color="#67c23a" class="m-r-4px"><CircleCheckFilled /></ElIcon>
							准时完成
						</span>
						<span v-else>--</span>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="taskOverdueRate" label="任务超期率" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.taskOverdueRate ? scope.row.taskOverdueRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="milestoneProgress" label="里程碑进度" width="120" align="center">
					<template #default="scope"
						><span>{{
							scope.row.milestoneProgress ? scope.row.milestoneProgress + "%" : "--"
						}}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="milestonePassRate" label="里程碑通过率" width="120" align="center">
					<template #default="scope"
						><span>{{
							scope.row.milestonePassRate ? scope.row.milestonePassRate + "%" : "--"
						}}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="milestoneAvg" label="里程碑评审平均分" width="160" align="center">
					<template #default="scope"
						><span>{{ scope.row.milestoneAvg ? scope.row.milestoneAvg : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="milestoneReviewNum" label="里程碑评审次数" width="140" align="center">
					<template #default="scope"
						><span>{{ scope.row.milestoneReviewNum ? scope.row.milestoneReviewNum : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="milestoneGreatBugNum" label="严重问题里程碑数量" width="180" align="center">
					<template #default="scope"
						><span>{{
							scope.row.milestoneGreatBugNum ? scope.row.milestoneGreatBugNum : "--"
						}}</span></template
					>
				</ElTableColumn>
			</ElTable>
			<div class="m-t-16px flex flex-justify-end">
				<ElPagination
					background
					layout="total, jumper,prev, pager, next"
					:total="tableData.total"
					@current-change="handleCurrentChange"
					@size-change="handleSizeChange"
					:currentPage="progressList.pageNo"
					:pageSize="progressList.pageSize"
				></ElPagination>
			</div>
		</ElCard>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="12">
				<ElCard header="项目总览与预警" class="custom-header">
					<div
						class="flex flex-items-center flex-col flex-justify-center no-data"
						style="height: 586px"
						v-if="workList.length == 0"
					>
						<img src="@/assets/statistics/no-data.png" alt="" />
						<div>暂无数据</div>
					</div>
					<div class="flex" v-else>
						<div class="flex-1 flex">
							<el-checkbox-group v-model="checked1" @change="chooseProject" class="flex-1">
								<ElTable :data="workList" class="work-time-table" height="568px" border>
									<ElTableColumn
										prop="projectName"
										label="项目名称"
										align="center"
										show-overflow-tooltip
									>
										<template #default="scope">
											<span style="text-align: center; display: inline-block">
												<ElCheckbox :label="scope.row.projectName" :value="scope.row.id" />
											</span>
										</template>
									</ElTableColumn>
									<ElTableColumn
										prop="devCost"
										label="总开发成本(元)"
										align="center"
										show-overflow-tooltip
									>
										<template #default="scope">
											<div class="flex flex-items-center" style="justify-content: center">
												<img
													src="@/assets/statistics/icon-money.png"
													style="width: 16px; height: 16px; margin-right: 4px"
													alt=""
												/>
												{{ scope.row.devCost }}
											</div>
										</template>
									</ElTableColumn>
								</ElTable>
							</el-checkbox-group>
							<div class="lines">
								<ElIcon class="left-arrow" color="rgba(27, 134, 255, .9)">
									<CaretRight />
								</ElIcon>
							</div>
						</div>
						<div class="work-time-right">
							<div>
								<HoursAbs :list="workCompareList" />
							</div>
							<div class="m-t-24px">
								<Title title="一周工时偏差曲线">
									<template #right>
										<el-date-picker
											v-model="weekValue"
											@change="handleChange"
											id="exportDate"
											type="week"
											:teleported="false"
											format="YYYY 第 ww 周"
											placeholder="请选择一周"
										/>
									</template>
								</Title>
								<LineSevenDay v-if="showRef" :data="linedata" />
							</div>
						</div>
					</div>
				</ElCard>
			</ElCol>
			<ElCol :span="12">
				<ElCard class="custom-header">
					<template #header>
						<div class="card-header">
							<div class="flex flex-items-center flex-justify-between">
								<div>高风险项目任务预警</div>
								<div style="float: right">
									<ElDropdown @command="handleCommand">
										<ElButton>
											{{ activeProject ? activeProject : "全部"
											}}<ElIcon color="rgba(0,0,0,0.85)" class="el-icon--right"
												><ArrowDown
											/></ElIcon>
										</ElButton>
										<template #dropdown>
											<ElDropdownMenu>
												<ElDropdownItem command="-1">全部</ElDropdownItem>
												<ElDropdownItem
													v-for="(item, index) in selectProject"
													:command="item.id"
													:key="index"
													>{{ item.projectName }}</ElDropdownItem
												>
											</ElDropdownMenu>
										</template>
									</ElDropdown>
								</div>
							</div>
						</div>
					</template>
					<div class="task-list">
						<div
							class="flex flex-items-center flex-col flex-justify-center no-data"
							style="height: 568px"
							v-if="list.length == 0"
						>
							<img src="@/assets/statistics/no-data.png" alt="" />
							<div>暂无数据</div>
						</div>
						<Carousel v-else :list="list" h="568px"></Carousel>
					</div>
				</ElCard>
			</ElCol>
		</ElRow>
		<ElCard header="项目质量情况统计分析" class="custom-header m-t-16px table-box-separate">
			<!-- <TodayTaskTable ref="projectQuality" :columns="columnsProgress" :api="qualityList" /> -->
			<ElTable :data="qualityData.data" style="width: 100%" height="370" stripe ref="tableRef">
				<ElTableColumn
					prop="projectName"
					show-overflow-tooltip
					label="项目名称"
					min-width="220"
					align="center"
				/>
				<ElTableColumn prop="qualityScore" label="质量评分" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.qualityScore || "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="projectManagerName" label="项目经理" width="160" align="center">
					<template #default="scope"
						><span>{{ scope.row.projectManagerName || "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="bugRate" label="项目Bug率" width="140" align="center">
					<template #default="scope"
						><span>{{ scope.row.bugRate ? scope.row.bugRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="repairBugRate" label="项目BUG修复完成度" width="180" align="center">
					<template #default="scope"
						><span>{{ scope.row.repairBugRate ? scope.row.repairBugRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="seriousBugRate" label="严重BUG占比" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.seriousBugRate ? scope.row.seriousBugRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="javaGroup" label="后端开发组" show-overflow-tooltip width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.javaGroup || "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="javaBugRate" label="后端BUG占比" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.javaBugRate ? scope.row.javaBugRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="javaSeriousBugRate" label="后端严重BUG占比" width="160" align="center">
					<template #default="scope"
						><span>{{
							scope.row.javaSeriousBugRate ? scope.row.javaSeriousBugRate + "%" : "--"
						}}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="vueGroup" label="前端开发组" show-overflow-tooltip width="160" align="center">
					<template #default="scope"
						><span>{{ scope.row.vueGroup || "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="vueBugRate" label="前端BUG占比" width="120" align="center">
					<template #default="scope"
						><span>{{ scope.row.vueBugRate ? scope.row.vueBugRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="vueSeriousBugRate" label="前端严重BUG占比" width="160" align="center">
					<template #default="scope"
						><span>{{
							scope.row.vueSeriousBugRate ? scope.row.vueSeriousBugRate + "%" : "--"
						}}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="testGroup" label="测试组" width="140" align="center">
					<template #default="scope"
						><span>{{ scope.row.testGroup || "--" }}</span></template
					>
				</ElTableColumn>
				<ElTableColumn prop="testCoverRate" label="测试报告BUG覆盖率" width="180" align="center">
					<template #default="scope"
						><span>{{ scope.row.testCoverRate ? scope.row.testCoverRate + "%" : "--" }}</span></template
					>
				</ElTableColumn>
			</ElTable>
			<div class="m-t-16px flex flex-justify-end">
				<ElPagination
					background
					layout="total, jumper,prev, pager, next"
					:total="qualityData.total"
					@current-change="handleQualityCurrentChange"
					@size-change="handleQualitySizeChange"
					:currentPage="qualityList.pageNo"
					:pageSize="qualityList.pageSize"
				></ElPagination>
			</div>
		</ElCard>
	</div>
</template>

<script setup lang="tsx">
	import { ref, reactive, computed } from "vue";
	import LineSevenDay from "@/components/project-info/e-charts/line-seven-day";
	import { ElPagination } from "element-plus";
	import HoursAbs from "./hours-abs/index.vue";
	import { CaretRight, ArrowDown, WarningFilled, CircleCheckFilled } from "@element-plus/icons-vue";
	import {
		selectAnalysisPageList,
		projectQualityList,
		highRiskTaskListByIds,
		projectWorkingHour,
		workHoursDaysData,
		workHoursContrastData
	} from "@/api/statistics/statistics";
	import Carousel from "./carousel/index.vue";
	import { appInstance } from "@/main";
	import { ElMessage } from "element-plus";

	// 各项目进度分析
	const selectIds = ref([]);
	const progressList = reactive({
		projectIds: selectIds.value,
		pageNo: 1,
		pageSize: 10
	});
	const tableData = reactive({
		data: [],
		total: 0
	});

	// 各项目质量分析
	const qualityList = reactive({
		projectIds: selectIds.value,
		pageNo: 1,
		pageSize: 10
	});
	const qualityData = reactive({
		data: [],
		total: 0
	});

	// 重置选择的项目
	const handleReset = () => {
		workList.value = [];
		activeProject.value = "全部";
		list.value = [];
		// 工时对比
		workCompareList.value = [];

		// 各项目进度分析重置
		tableData.data = [];
		tableData.total = 0;
		progressList.projectIds = [];
		progressList.pageNo = 1;
		progressList.pageSize = 10;

		// 个项目质量分析重置
		// 各项目进度分析重置
		qualityData.data = [];
		qualityData.total = 0;
		qualityList.projectIds = [];
		qualityList.pageNo = 1;
		qualityList.pageSize = 10;
	};

	const getList = () => {
		selectAnalysisPageList(progressList).then((res) => {
			tableData.data = res.records.map((val) => {
				val.risk = val.expectFinish?.split(",")[1];
				val.expectFinish = val.expectFinish?.split(",")[0];
				return val;
			});
			tableData.total = res.total;
		});
	};

	const handleCurrentChange = (val: number) => {
		progressList.pageNo = val;
		getList();
	};

	// 每页显示数量改变事件处理函数
	const handleSizeChange = (size: number) => {
		progressList.pageSize = size;
		progressList.pageNo = 1; // 重置页码为第一页
		getList();
	};

	// 各项目质量分析
	const getQualityList = () => {
		projectQualityList(qualityList).then((res) => {
			qualityData.data = res.records;
			qualityData.total = res.total;
		});
	};

	const handleQualityCurrentChange = (val: number) => {
		qualityList.pageNo = val;
		getQualityList();
	};

	// 每页显示数量改变事件处理函数
	const handleQualitySizeChange = (size: number) => {
		qualityList.pageSize = size;
		qualityList.pageNo = 1; // 重置页码为第一页
		getQualityList();
	};

	// 各项目工时列表
	const workList = ref([]);
	// 选中的项目数据
	const selectProject = ref([]);

	// 工时总览的选择数据
	const chooseIds = ref();
	// 选择的数据
	const checked1 = ref([]);
	const init = (data) => {
		selectProject.value = data;
		selectIds.value = [];
		data.map((val) => {
			selectIds.value.push(val.id);
		});
		progressList.projectIds = selectIds.value;
		progressList.pageNo = 1;
		qualityList.projectIds = selectIds.value;
		qualityList.pageNo = 1;
		// 项目进度
		getList();
		// 项目质量统计
		getQualityList();

		// 高风险项目任务预警
		getHighWarning(selectIds.value);
		activeProject.value = "全部";

		// 各项目工时总览
		projectWorkingHour(selectIds.value).then((res) => {
			workList.value = res;

			// 获取一周工时
			chooseIds.value = [workList.value[0].id];
			checked1.value = [workList.value[0].id];
			getDays(new Date());
			getDaysData(chooseIds.value);
			getContrastData(chooseIds.value);
		});
	};

	const list = ref([]);
	const activeProject = ref("");
	const handleCommand = (val) => {
		const temp = selectProject.value.filter((item) => item.id == val);
		activeProject.value = temp[0]?.projectName || "全部";
		const ids = temp[0]?.id ? [temp[0]?.id] : selectIds.value;
		if (temp.length == 0) return ElMessage.error("至少选择一个项目");
		getHighWarning(ids);
	};

	function calculateTimeRemaining(totalSeconds: number) {
		if (totalSeconds < 60) {
			return { h: 0, m: totalSeconds };
		}
		const h = Math.floor(totalSeconds / 60);
		const m = Math.floor(totalSeconds % 60);
		return { h, m };
	}
	const getHighWarning = (ids) => {
		highRiskTaskListByIds(ids).then((res) => {
			const listMap: any[] = res.map((item: any) => {
				let { h, m } = calculateTimeRemaining(item.postponeTime || 0);
				const task_attribute = appInstance.mapDict.task_attribute?.find(
					(it) => it.value == item.task_attribute
				)?.label;

				return {
					personChargeName: item.personChargeName,
					startTime: item.startTime,
					endTime: item.endTime,
					postponeTime: item.postponeTime,
					title: item.title,
					task_attribute,
					postPoneTemp: h > 0 ? `${h}小时 ${m}分钟` : `${m}分钟`,
					isPostpone: item.isPostpone
				};
			});
			list.value = [];
			for (let i = 0; i < listMap.length; i += 5) {
				list.value.push(listMap.slice(i, i + 5));
			}
		});
	};

	// 获取项目一周工时偏差曲线
	const showRef = ref(false);
	const getDaysData = (ids) => {
		workHoursDaysData({ projectIds: ids, search_time: startWeek.value }).then((res) => {
			Object.keys(linedata).forEach((key) => {
				Reflect.set(linedata, key, res[key]);
			});
			showRef.value = true;
		});
	};

	// 选择时间
	const startWeek = ref("");
	const weekValue = ref(new Date().getTime());

	// 获取一周的第一天
	const getDays = (value) => {
		var date = new Date(value);
		// 本周一的日期
		date.setDate(date.getDate() - date.getDay() + 1);
		const month1 = date.getMonth() - 9 >= 0 ? date.getMonth() + 1 : "0" + (date.getMonth() + 1);
		const date1 = date.getDate() - 9 > 0 ? date.getDate() : "0" + date.getDate();
		startWeek.value = date.getFullYear() + "-" + month1 + "-" + date1;

		getDaysData(chooseIds.value);
	};

	const handleChange = (value) => {
		getDays(value);
	};

	// 工时对比表
	const workCompareList = ref([]);
	const getContrastData = (ids) => {
		workHoursContrastData({ projectIds: ids }).then((res) => {
			workCompareList.value = [];
			Object.keys(res).forEach((key) => {
				workCompareList.value.push(res[key]);
			});
		});
	};

	// 工时折线图
	const linedata = reactive({
		planValueList: [],
		nameList: [],
		workValueList: []
	});

	const chooseProject = (val) => {
		chooseIds.value = val;
		if (val.length == 0) return ElMessage.error("至少选择一个项目");

		getDaysData(val);
		getContrastData(val);
	};

	defineExpose({
		init,
		handleReset
	});
</script>
<style lang="scss" scoped>
	.no-data {
		height: 251px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.45);
		line-height: 22px;
		img {
			width: 165px;
			height: 165px;
			margin-bottom: 16px;
		}
	}
	.card-header {
		width: 100%;
	}

	.task-list {
		.task-item {
			height: 90px;
			background: #ffffff;
			box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
			border-radius: 12px 12px 12px 12px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			display: flex;
			align-items: center;
			padding: 0 16px;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 32px;
			margin-bottom: 16px;
			position: relative;

			img {
				width: 64px;
				height: 64px;
				margin-right: 16px;
			}

			.list-tag {
				position: absolute;
				width: 105px;
				height: 90px;
				right: -17px;
				top: -2px;
			}

			.type-tag {
				position: absolute;
				z-index: 2;
				font-size: 14px;
				color: #ffffff;
				line-height: 22px;
				right: 14px;
				top: 8px;
			}

			.remaining {
				height: 32px;
				display: inline-flex;
				align-items: center;
				padding: 0 10px;
				background: #fef0f0;
				border-radius: 8px 8px 8px 8px;
				color: #f56c6c;

				img {
					width: 20px;
					height: 20px;
					margin-right: 8px;
				}
			}

			.orange {
				color: #f5a250;
				background: #fef6ed;
			}
		}
	}
	.lines {
		width: 4px;
		height: 568px;
		background: linear-gradient(180deg, rgba(27, 134, 255, 0.12) 0%, #1b86ff 50%, rgba(27, 134, 255, 0.12) 100%);
		border-radius: 0px 0px 0px 0px;
		position: relative;
		margin: 0 16px;

		.left-arrow {
			position: absolute;
			top: 50%;
			left: -3px;
		}
	}
	.work-time-right {
		width: 410px;
	}
	::v-deep {
		.el-card__header {
			font-weight: bold;
		}

		// 工时对比表的表格
		.work-time-table {
			border-radius: 12px;
			overflow: hidden;
			background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);

			.cell {
				line-height: 32px;
			}

			.el-table__header {
			}

			th.el-table__cell {
				background: linear-gradient(180deg, #e8f4fe 0%, #d7edff 100%) !important;
			}
		}
	}

	// 表格样式
	.table-box-separate {
		overflow: hidden;
		:deep(.el-table) {
			border: none;
			background-color: transparent;
			tr {
				background-color: transparent;
			}
			th.el-table__cell {
				padding: 0;
				background: #e6f1ff !important;
				font-size: 14px;
				color: #275798;
				.cell {
					line-height: 44px;
					border-left: 2px solid #fff;
				}
				&:first-child .cell {
					border-left: none;
				}
				&:first-child {
					border-top-left-radius: 20px;
					border-bottom-left-radius: 20px;
				}
				&:last-child {
					border-top-right-radius: 20px;
					border-bottom-right-radius: 20px;
				}
			}
			td.el-table__cell {
				border-bottom: 1px solid #fff;
				border-left: 2px solid #fff;
				&:first-child {
					border-left: none;
				}
			}
		}
		:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
			background-color: #f4f7ff;
			&:first-child {
				border-top-left-radius: 20px;
				border-bottom-left-radius: 20px;
			}
			&:last-child {
				border-top-right-radius: 20px;
				border-bottom-right-radius: 20px;
			}
		}
		:deep(.el-table__column-filter-trigger i) {
			color: #275798;
		}
	}
</style>
