<template>
	<ElCard class="box-card">
		<template #header>
			<div class="card-header">
				<span>组员工作总结与明细</span>
			</div>
		</template>
		<div class="flex">
			<div class="common-box">
				<div class="common-header">
					<div class="common-title flex flex-items-center">
						<ProjectInfoIcon name="icon-title.png" />
						<span>各组总结与明细</span>
						<div class="common-search ml-22px">
							<div>
								<el-radio-group v-model="radio1">
									<el-radio-button label="按天" :value="1" />
									<el-radio-button label="按周" :value="2" />
									<el-radio-button label="按月" :value="3" />
									<el-radio-button label="按季" :value="4" />
									<el-radio-button label="自定义" :value="5" />
								</el-radio-group>
							</div>
							<div v-if="radio1 === 1">
								<ElDatePicker
									v-model="sumDate1"
									type="date"
									format="YYYY-MM-DD"
									value-format="YYYY-MM-DD"
									placeholder="选择日期"
									style="width: 140px; margin-left: 8px"
									@change="handleDateChange"
								/>
							</div>
							<div v-else-if="radio1 === 2">
								<ElDatePicker
									v-model="sumDate2"
									type="week"
									format="YYYY 第 ww 周"
									value-format="YYYY-ww"
									placeholder="选择周"
									style="width: 140px; margin-left: 8px"
									@change="handleWeekChange"
								/>
							</div>
							<div v-else-if="radio1 === 3">
								<ElDatePicker
									v-model="sumDate3"
									type="month"
									format="YYYY-MM"
									value-format="YYYY-MM"
									placeholder="选择月"
									style="width: 140px; margin-left: 8px"
									@change="handleMonthChange"
								/>
							</div>
							<div v-else-if="radio1 === 4">
								<!-- <ElDatePicker
									v-model="sumDate4"
									type="quarter"
									format="YYYY [Q]Q"
									value-format="YYYY-Q"
									placeholder="选择季度"
									style="width: 140px; margin-left: 8px"
									@change="handleQuarterChange"
								/> -->
								<ElQuarter
									style="width: 140px; margin-left: 8px"
									v-model="sumDate4"
									@change="handleQuarterChange"
									placeholder="选择季度"
								/>
							</div>
							<div v-else-if="radio1 === 5">
								<ElDatePicker
									v-model="sumDate5"
									type="daterange"
									format="YYYY-MM-DD"
									value-format="YYYY-MM-DD"
									placeholder="选择日期范围"
									style="width: 280px; margin-left: 8px"
									@change="handleDateRangeChange"
								/>
							</div>
						</div>
					</div>
					<div class="common-search">
						<el-radio-group v-model="radio2">
							<el-radio-button label="全部" :value="0" />
							<el-radio-button :label="`运维(${operationCount})`" :value="1" />
							<el-radio-button :label="`BUG修复(${bugFixCount})`" :value="2" />
							<el-radio-button :label="`计划任务(${planTaskCount})`" :value="3" />
						</el-radio-group>
					</div>
				</div>
				<div class="common-body flex flex-items-center">
					<div class="team-list">
						<div
							class="team-item"
							v-for="(item, index) in teamList"
							:key="index"
							:class="{ active: isSelected(item.userId) }"
							@click="toggleSelection(item.userId)"
						>
							<ProjectInfoIcon name="header.png" :width="36" :height="36" />
							<p>{{ item.userName }}</p>
							<div class="select-check">
								<ElIcon>
									<Select />
								</ElIcon>
							</div>
						</div>
					</div>
					<div class="point-line"></div>
					<!-- 字段： 人员名称 任务数量 的带有复选框的数据表格 -->
					<div class="table-box">
						<el-table :data="tableData" style="width: 100%" height="444">
							<el-table-column width="40" align="center">
								<template #default="scope">
									<el-radio
										v-model="selectedRowId"
										:label="scope.row.id"
										@change="handleRowSelect(scope.row)"
									/>
								</template>
							</el-table-column>
							<el-table-column prop="projectName" label="项目名称" width="210" align="center" />
							<el-table-column prop="count" label="任务数量" width="90" align="center" />
						</el-table>
					</div>
					<!-- 字段:来源 任务标题 负责人 任务属性 任务截止日期 状态 开始日期 完成日期 优先级 的数据表格 -->
					<div class="table-box-separate flex-1">
						<el-table :data="tableData2" style="width: 100%" height="440" stripe>
							<el-table-column prop="source" label="来源" width="160" align="center" />
							<el-table-column prop="title" label="任务标题" min-width="160" align="center" />
							<!-- <el-table-column
								prop="task_attribute"
								label="任务属性"
								width="100"
								align="center"
								:filters="getFilters('task_attribute')"
								filter-multiple="false"
								:filter-method="filterByRealName"
							/> -->
							<el-table-column prop="task_attribute" label="任务属性" width="100" align="center" />
							<el-table-column prop="end_time" label="任务截止日期" width="140" sortable align="center" />
							<!-- <el-table-column
								prop="statusName"
								label="状态"
								width="100"
								align="center"
								:filters="getFilters('statusName')"
								filter-multiple="false"
								:filter-method="filterByRealName2"
							>
								<template #default="scope">
									<el-tag :type="getStatusTagType(scope.row.statusName)">
										{{ scope.row.statusName }}
									</el-tag>
								</template>
							</el-table-column> -->
							<el-table-column prop="statusName" label="状态" width="100" align="center">
								<!-- 根据状态不同，显示不同颜色的标签 -->
								<template #default="scope">
									<span class="degree"
										><i :style="getStatusTagType(scope.row.statusName)"></i
										>{{ scope.row.statusName }}</span
									>
								</template>
							</el-table-column>
							<el-table-column prop="start_time" label="开始日期" width="120" align="center" />
							<el-table-column prop="completion_time" label="完成日期" width="120" align="center" />
							<!-- <el-table-column
								prop="priority"
								label="优先级"
								width="100"
								align="center"
								:filters="getFilters('priority')"
								filter-multiple="false"
								:filter-method="filterByRealName3"
							/> -->
							<el-table-column prop="priority" label="优先级" width="100" align="center" />
						</el-table>
						<!-- <div class="flex flex-row-reverse mt-10px">
							<el-pagination
								@size-change="handleSizeChange"
								@current-change="handleCurrentChange"
								:current-page="currentPage"
								:page-size="pageSize"
								:total="tableData2.length"
								small
								background
								layout="prev, pager, next"
							/>
						</div> -->
					</div>
				</div>
			</div>
		</div>
	</ElCard>
</template>

<script setup lang="tsx">
	import { ref, onMounted, computed, nextTick, watch } from "vue";
	import {
		ElIcon,
		ElMessageBox,
		type Action,
		ElCard,
		ElButton,
		ElMessage,
		ElDatePicker,
		ElTooltip
	} from "element-plus";
	import {
		Select,
		Avatar,
		Timer,
		QuestionFilled,
		CaretTop,
		CaretBottom,
		CircleCheckFilled,
		CircleCheck
	} from "@element-plus/icons-vue";
	import { getTaskSummary, getTaskDetail } from "@/api/task/group";
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import ElQuarter from "@/components/quarter/index.vue";
	// import { getDeptStatistics, getTaskDetail } from "@/api/statistics/staff";

	// const props = defineProps({
	// 	projectList: {
	// 		type: Array,
	// 		default: []
	// 	}
	// });
	const props = defineProps({
		projectList: {
			type: Object,
			default: () => {
				return {};
			}
		}
	});

	const radio1 = ref(2);
	const radio2 = ref(0);

	const sumDate1 = ref("");
	const sumDate2 = ref("");
	const sumDate3 = ref("");
	const sumDate4 = ref("");
	const sumDate5 = ref("");

	const startDate = ref("");
	const endDate = ref("");

	// 处理单选框变化
	const handleRadioChange = () => {
		// 切换选择器时重置日期
		startDate.value = "";
		endDate.value = "";
	};

	// 处理按天选择
	const handleDateChange = (date) => {
		startDate.value = date;
		endDate.value = date;
		normalGetList();
		sumDate2.value = "";
		sumDate3.value = "";
		sumDate4.value = "";
		sumDate5.value = "";
	};

	// 处理按周选择
	const handleWeekChange = (week) => {
		if (week) {
			const [year, weekNumber] = week.split("-").map(Number);
			// 计算本周第一天（周一）
			const d = new Date(year, 0, 1 + (weekNumber - 1) * 7);
			const day = d.getDay();
			const diff = d.getDate() - day + (day === 0 ? -6 : 1);
			const startOfWeek = new Date(d.setDate(diff));
			// 计算本周最后一天（周日）
			const endOfWeek = new Date(startOfWeek);
			endOfWeek.setDate(endOfWeek.getDate() + 6);

			startDate.value = formatDate(startOfWeek);
			endDate.value = formatDate(endOfWeek);
			normalGetList();
			sumDate1.value = "";
			sumDate3.value = "";
			sumDate4.value = "";
			sumDate5.value = "";
		}
	};

	// 处理按月选择
	const handleMonthChange = (month) => {
		if (month) {
			const [year, monthNumber] = month.split("-").map(Number);
			// 计算本月第一天
			const startOfMonth = new Date(year, monthNumber - 1, 1);
			// 计算本月最后一天
			const endOfMonth = new Date(year, monthNumber, 0);

			startDate.value = formatDate(startOfMonth);
			endDate.value = formatDate(endOfMonth);
			normalGetList();
			sumDate1.value = "";
			sumDate2.value = "";
			sumDate4.value = "";
			sumDate5.value = "";
		}
	};

	// 处理按季选择
	const handleQuarterChange = (quarter) => {
		if (quarter) {
			const [year, quarterNumber] = quarter.split("-").map(Number);
			// 计算本季度第一天
			const startMonth = (quarterNumber - 1) * 3;
			const startOfQuarter = new Date(year, startMonth, 1);
			// 计算本季度最后一天
			const endMonth = startMonth + 2;
			const endOfQuarter = new Date(year, endMonth + 1, 0);

			startDate.value = formatDate(startOfQuarter);
			endDate.value = formatDate(endOfQuarter);
			normalGetList();
			sumDate1.value = "";
			sumDate2.value = "";
			sumDate3.value = "";
			sumDate5.value = "";
		}
	};

	// 处理自定义日期范围选择
	const handleDateRangeChange = (dates) => {
		if (dates && dates.length === 2) {
			startDate.value = dates[0];
			endDate.value = dates[1];
			normalGetList();
			sumDate1.value = "";
			sumDate2.value = "";
			sumDate3.value = "";
			sumDate4.value = "";
		}
	};

	// 日期格式化函数
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	};

	const teamList = ref([]);

	// 初始化 teamList
	teamList.value = props.projectList.sysGroupUser || [];

	watch(
		() => props.projectList,
		(newProjectList) => {
			teamList.value = newProjectList.sysGroupUser || [];
			if (teamList.value.length > 0) {
				// 更新选中团队
				selectedTeams.value = teamList.value[0].userId;
				// 重新获取表格数据
				normalGetList();
				// 监听 tableData 变化，默认选中第一条
				watch(
					tableData,
					(newTableData) => {
						if (newTableData.length > 0 && newTableData[0].id !== undefined) {
							selectedRowId.value = newTableData[0].id;
							handleRowSelect(newTableData[0]);
						}
					},
					{ immediate: true, once: true }
				);
			} else {
				// 若 teamList 为空，重置选中状态
				selectedTeams.value = null;
				selectedRowId.value = null;
				selectedUserIds.value = null;
				tableData.value = [];
			}
		},
		{ deep: true }
	);
	const getTeamIcon = (item: any) => {
		if (item.type == "1") {
			// 前端
			return "terminal-window-fill.png";
		} else if (item.type == "2") {
			// 后端
			return "code-box-fill.png";
		} else if (item.type == "3") {
			// 测试
			return "bug-fill.png";
		} else if (item.type == "4") {
			// 产品
			return "brain-fill.png";
		} else if (item.type == "5") {
			// 运维
			return "server-fill.png";
		} else if (item.type == "6") {
			// UI 设计
			return "brush-ai-fill.png";
		} else {
			return "";
		}
	};
	const tableData = ref([]);
	// 存储选中项的 id
	// 存储选中行的 id
	const selectedRowId = ref<number | null>(null);
	const selectedUserIds = ref<number | null>(null);

	const selectedTeams = ref<number[]>([]);

	const operationCount = ref(0);
	const bugFixCount = ref(0);
	const planTaskCount = ref(0);

	// 处理行选择事件
	const handleRowSelect = (row) => {
		selectedUserIds.value = row.id;

		operationCount.value = row.operationCount;
		bugFixCount.value = row.bugCount;
		planTaskCount.value = row.planCount;
		// 调用接口
		fetchDeptStatisticsTask();
	};

	// 后续点击团队项时的正常渲染函数
	const normalGetList = () => {
		// 调用接口获取数据
		getTaskSummary({
			userId: selectedTeams.value,
			startTime: startDate.value,
			endTime: endDate.value
		})
			.then((res) => {
				console.log("normalGetList res", res);
				tableData.value = res;
				if (res.length == 0) {
					selectedUserIds.value = null;
					operationCount.value = 0;
					bugFixCount.value = 0;
					planTaskCount.value = 0;
					tableData2.value = [];
				}
			})
			.catch((error) => {
				console.error("获取任务总结数据出错:", error);
			});
	};

	// 监听 tableData 变化
	watch(
		tableData,
		(newTableData) => {
			if (newTableData.length > 0 && newTableData[0].id !== undefined) {
				selectedRowId.value = newTableData[0].id;
				handleRowSelect(newTableData[0]);
			}
		},
		{ immediate: true }
	);
	const pageNum = ref(1);
	const pageSize = ref(10);
	const total = ref(0);

	// 获取当前周的信息
	const getCurrentWeek = () => {
		const now = new Date();
		const year = now.getFullYear();
		const startOfYear = new Date(year, 0, 1);
		const daysOffset = startOfYear.getDay();
		const diff = now.getTime() - startOfYear.getTime() + daysOffset * (1000 * 60 * 60 * 24);
		const days = Math.floor(diff / (1000 * 60 * 60 * 24));
		const weekNumber = Math.ceil((days + 1) / 7);
		return `${year}-${String(weekNumber).padStart(2, "0")}`;
	};

	// 获取上一周的信息
	const getPreviousWeek = () => {
		const now = new Date();
		// 将日期往前推 7 天
		now.setDate(now.getDate() - 7);
		const year = now.getFullYear();
		const startOfYear = new Date(year, 0, 1);
		const daysOffset = startOfYear.getDay();
		const diff = now.getTime() - startOfYear.getTime() + daysOffset * (1000 * 60 * 60 * 24);
		const days = Math.floor(diff / (1000 * 60 * 60 * 24));
		const weekNumber = Math.ceil((days + 1) / 7);
		return `${year}-${String(weekNumber).padStart(2, "0")}`;
	};

	onMounted(() => {
		const previousWeek = getCurrentWeek();
		sumDate2.value = previousWeek;
		handleWeekChange(previousWeek);

		if (teamList.value.length > 0) {
			selectedTeams.value = teamList.value[0].userId;
			normalGetList();
		}
	});

	// 检查某项是否被选中
	const isSelected = (id: number) => {
		return selectedTeams.value === id;
	};

	// 切换选中状态，改为单选效果
	const toggleSelection = (id: number) => {
		// 直接赋值为 id
		selectedTeams.value = id;
		normalGetList();
	};

	const originalTableData2 = ref([]);
	const tableData2 = ref([]);
	const currentPage = ref(1);
	// const pageSize = ref(10);
	// // 计算分页后的数据
	// const paginatedTableData2 = computed(() => {
	// 	const start = (currentPage.value - 1) * pageSize.value;
	// 	const end = start + pageSize.value;
	// 	return tableData2.value.slice(start, end);
	// });

	// 调用 getTaskDetail 接口
	const fetchDeptStatisticsTask = () => {
		getTaskDetail({
			type: radio2.value,
			projectId: selectedUserIds.value,
			// pageNum: pageNum.value,
			// pageSize: pageSize.value,
			userId: selectedTeams.value,
			startTime: startDate.value,
			endTime: endDate.value
		})
			.then((res) => {
				tableData2.value = res;
			})
			.catch((error) => {
				console.error("getTaskDetail error", error);
			});
	};

	// 监听 radio2 的变化
	watch(radio2, (newValue) => {
		fetchDeptStatisticsTask();
	});
	// 动态生成筛选器选项
	const getFilters = (prop) => {
		const uniqueValues = [...new Set(tableData2.value.map((item) => item[prop]))].filter(
			(value) => value !== undefined && value !== null
		);
		return uniqueValues.map((value) => ({ text: String(value), value }));
	};

	// 自定义筛选方法
	const filterByRealName = (value, row) => {
		return row.task_attribute === value;
	};

	const filterByRealName2 = (value, row) => {
		return row.statusName === value;
	};

	const filterByRealName3 = (value, row) => {
		return row.priority === value;
	};

	// 处理筛选变化事件
	const handleFilterChange = (filters) => {
		const realNameFilter = filters.realName;
		if (realNameFilter && realNameFilter.length > 0) {
			// 使用原始数据进行筛选
			tableData2.value = originalTableData2.value.filter((item) => realNameFilter.includes(item.realName));
		} else {
			// 没有筛选条件时，显示全部数据
			tableData2.value = originalTableData2.value;
		}
	};

	// 处理每页数量变化
	const handleSizeChange = (newSize: number) => {
		pageSize.value = newSize;
		// 重置当前页码为第一页
		pageNum.value = 1;
		// 重新获取数据
		fetchDeptStatisticsTask();
	};

	// 处理当前页码变化
	const handleCurrentChange = (newPage: number) => {
		pageNum.value = newPage;
		// 重新获取数据
		fetchDeptStatisticsTask();
	};

	// 后续如果状态值描述被改了 要回来修改这儿
	const getStatusTagType = (status) => {
		if (status === "待指派") {
			return "background: #989898";
		} else if (status === "待开始") {
			return "background: #40BFA8";
		} else if (status === "进行中") {
			return "background: #1B86FF";
		} else if (status === "已完成") {
			return "background: #67C23A";
		} else if (status === "已结束") {
			return "background: #67C23A";
		} else if (status === "已挂起") {
			return "background: #F5A250";
		} else if (status === "已暂停") {
			return "background: #F56C6C";
		} else if (status === "已解决") {
			return "background: #449AD7";
		} else if (status === "已评分") {
			return "background: #C4AF42";
		} else if (status === "已终止") {
			return "background: #D36BD9";
		} else if (status === "被打回") {
			return "background: #8A59DE";
		} else {
			return "background: #989898";
		}
	};
</script>
<style scoped lang="scss">
	:deep(.el-card__header) {
		padding-top: 16px;
		padding-bottom: 16px;
		.card-header {
			font-weight: bold;
			font-size: 18px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 24px;
			text-align: left;
		}
	}
	.common-box {
		width: 100%;
		.common-header {
			width: 100%;
			display: flex;
			justify-content: space-between;
			align-items: center;
			.common-title {
				font-family:
					Alibaba PuHuiTi 3,
					Alibaba PuHuiTi 30;
				font-weight: bold;
				font-size: 16px;
				color: rgba(0, 0, 0, 0.85);
				span {
					margin-left: 8px;
				}
			}
			.common-search {
				display: flex;
				justify-content: flex-end;
				align-items: center;
			}
		}
		.common-body {
			width: 100%;
			margin-top: 16px;
			.team-list {
				height: 444px;
				overflow-y: auto;
				.team-item {
					padding: 16px 6px 16px 16px;
					position: relative;
					display: flex;
					align-items: center;
					margin-bottom: 8px;
					width: 180px;
					height: 56px;
					background: #ffffff;
					border-radius: 8px 8px 8px 8px;
					border: 1px solid rgba(0, 0, 0, 0.06);
					box-sizing: border-box;
					cursor: pointer;
					p {
						margin: 0 0 0 10px;
						font-size: 14px;
						color: rgba(0, 0, 0, 0.85);
						line-height: 22px;
					}
					&.active {
						border-top: 3px solid #1b86ff;
						.select-check {
							display: block;
						}
					}
					.select-check {
						display: none;
						position: absolute;
						right: 0;
						top: -1px;
						width: 21px;
						height: 16px;
						line-height: 18px;
						color: #fff;
						background: #1b86ff;
						border-radius: 0px 4px 0px 8px;
						text-align: center;
						font-size: 12px;
					}
				}
			}

			.table-box {
				margin-right: 32px;
				background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);
				border-radius: 12px 12px 12px 12px;
				border: 1px solid rgba(0, 0, 0, 0.15);
				overflow: hidden;
				:deep(.el-table) {
					border: none;
					background-color: transparent;
					tr {
						background-color: transparent;
					}
					th.el-table__cell {
						padding: 0;
						background: linear-gradient(180deg, #e8f4fe 0%, #d7edff 100%);
						font-size: 14px;
						color: #275798;
						.cell {
							line-height: 44px;
							border-left: 1px solid #fff;
						}
						&:first-child .cell {
							border-left: none;
						}
					}
					td.el-table__cell {
						border-bottom: 1px dashed rgba(0, 0, 0, 0.15);
						border-left: 1px dashed rgba(0, 0, 0, 0.15);
						&:first-child {
							border-left: none;
						}
					}
				}
			}
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
			.echart-table {
				width: 372px;
				.echart-table-header {
					display: flex;
					height: 44px;
					font-size: 14px;
					line-height: 44px;
					color: #275798;
					background: #e6f1ff;
					border-radius: 4px 4px 0px 0px;
					.echart-table-header-item {
						flex: 1;
						text-align: center;
					}
					.legend-item {
						width: 16px;
					}
				}
				.echart-table-body {
					margin-top: 10px;
					height: 274px;
					overflow-y: auto;
					.echart-table-list {
						margin-bottom: 10px;
						display: flex;
						justify-content: space-between;
						.echart-table-body-item {
							flex: 1;
							margin-right: 4px;
							height: 44px;
							line-height: 44px;
							background: #f4f7ff;
							border-radius: 4px 4px 4px 4px;
							font-size: 14px;
							color: rgba(0, 0, 0, 0.65);
							text-align: center;
						}
						.legend-item {
							width: 16px;
							height: 44px;
							background: #5a6fc0;
							border-radius: 4px 4px 4px 4px;
						}
					}
				}
				.echart-table-bot {
					display: flex;
					height: 44px;
					font-size: 14px;
					line-height: 44px;
					color: #275798;
					background: #e6f1ff;
					border-radius: 0px 0px 4px 4px;
					.echart-table-bot-item {
						flex: 1;
						text-align: center;
					}
					.legend-item {
						width: 16px;
					}
				}
			}
		}
	}
	.member-search {
		display: flex;
		width: 330px;
		position: absolute;
		top: 0px;
		right: 10px;
	}
	.sort {
		margin-left: 20px;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 64px;
		height: 32px;
		border-radius: 4px 4px 4px 4px;
		border: 1px solid rgba(0, 0, 0, 0.15);
		font-size: 14px;
		font-weight: normal;
		color: rgba(0, 0, 0, 0.85);
		.sort-icon-box {
			margin-right: 4px;
			height: 22px;
			display: flex;
			flex-direction: column;
			font-size: 12px;
			color: rgba(0, 0, 0, 0.25);
			:deep(.el-icon) {
				cursor: pointer;
				&:last-child {
					margin-top: -4px;
				}
			}
		}
	}
	.member-list {
		width: 250px;
		height: 444px;
		overflow-y: auto;
		.member-item {
			display: flex;
			align-items: center;
			padding: 14px 16px;
			margin-bottom: 12px;
			width: 100%;
			height: 79px;
			background: #ffffff;
			box-shadow: inset 0px -2px 12px 0px rgba(40, 114, 250, 0.32);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			cursor: pointer;
			.el-icon {
				margin-right: 10px;
				color: #1b86ff;
			}
			.member-info {
				text-align: left;
				margin-left: 16px;
				p {
					margin: 0 0 4px;
					font-size: 16px;
					color: rgba(0, 0, 0, 0.85);
					line-height: 24px;
				}
				span {
					font-size: 14px;
					color: rgba(0, 0, 0, 0.65);
					line-height: 22px;
				}
			}
			.member-score {
				flex: 1;
				text-align: right;
				font-weight: bold;
				font-size: 20px;
				color: rgba(0, 0, 0, 0.85);
				line-height: 22px;
			}
		}
	}
	.work-list {
		margin-bottom: 16px;
		display: flex;
		align-items: center;
		&:last-child {
			margin-bottom: 0;
		}
		.work-left {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			width: 116px;
			height: 99px;
			border-radius: 8px 0px 0px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 22px;
		}
		.work-right {
			border: 1px solid rgba(0, 0, 0, 0.15);
			border-radius: 0px 8px 8px 0px;
			border-left: none;
			border-bottom: none;
			overflow: hidden;
			:deep(.el-table) {
				border: none;
				background-color: transparent;
				tr {
					background-color: transparent;
				}
				th.el-table__cell {
					padding: 0;
					background: #f4f7ff !important;
					.cell {
						line-height: 50px;
						border-left: 1px solid #fff;
						font-size: 14px;
						color: #275798;
					}
					&:first-child .cell {
						border-left: none;
					}
				}
				td.el-table__cell {
					padding: 12px 0;
					// border-bottom: 1px solid rgba(0,0,0,0.06);
					border-left: 1px solid rgba(0, 0, 0, 0.06);
					&:first-child {
						border-left: none;
					}
				}
			}
		}
	}
	.distribution-table {
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
	.distribution-chart {
		width: 500px;
	}
	.point-line {
		margin: 0 24px;
		position: relative;
		width: 4px;
		height: 444px;
		background: linear-gradient(180deg, rgba(27, 134, 255, 0.12) 0%, #1b86ff 50%, rgba(27, 134, 255, 0.12) 100%);
		&::after {
			// 右边指向的三角形 处于线的中间位置
			content: "";
			position: absolute;
			top: 50%;
			right: -10px;
			width: 0;
			height: 0;
			border-top: 8px solid transparent;
			border-bottom: 8px solid transparent;
			border-left: 10px solid #1b86ff;
			transform: translateY(-50%);
		}
	}
	:deep(.el-radio__label) {
		display: none;
	}
	.degree {
		display: inline-block;
		font-size: 14px !important;
		color: rgba(0, 0, 0, 0.65);
		line-height: 26px;
		i {
			display: inline-block;
			margin-right: 6px;
			width: 10px;
			height: 10px;
			border-radius: 50%;
		}
	}
</style>
