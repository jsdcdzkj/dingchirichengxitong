<template>
	<staffOne :projectList="props.projectList" :selectCondition="props.selectCondition" />

	<!-- 第二部分 -->
	<staffTwo :projectList="props.projectList" />

	<!-- 第三部分 -->
	<ElCard class="box-card mt-16px">
		<template #header>
			<div class="card-header">
				<span>部门工时分布</span>
			</div>
		</template>
		<div class="flex flex-items-center flex-col flex-justify-center no-data" v-if="projectList.length == 0">
			<img src="@/assets/statistics/no-data.png" alt="" />
			<div>暂无数据</div>
		</div>
		<div class="position-relative" v-else>
			<!-- 部门工时分布 -->
			<div class="common-box">
				<!-- <div class="common-header">
					<div class="common-title flex flex-items-center">
						<ProjectInfoIcon name="icon-title.png" />
						<span class="mr-6px">部门工时分布</span>
					</div>
				</div> -->
				<div class="flex flex-items-center flex-col flex-justify-center no-data" v-if="!tabOne">
					<img src="@/assets/statistics/no-data.png" alt="" />
					<div>暂无数据</div>
				</div>
				<div class="common-body common-tabs-box" v-else>
					<el-tabs v-model="activeWork" @tab-click="handleClickWork">
						<el-tab-pane :name="1" v-if="tabOne">
							<template #label>
								<ElIcon>
									<Timer />
								</ElIcon>
								<span class="ml-10px">合计工时分布</span>
							</template>
						</el-tab-pane>
						<el-tab-pane :name="2" v-if="tabTwo">
							<template #label>
								<ElIcon>
									<Timer />
								</ElIcon>
								<span class="ml-10px">计划任务工时分布</span>
							</template>
						</el-tab-pane>
						<el-tab-pane :name="3" v-if="tabThree">
							<template #label>
								<ElIcon>
									<Timer />
								</ElIcon>
								<span class="ml-10px">运维工时分布</span>
							</template>
						</el-tab-pane>
						<el-tab-pane :name="4" v-if="tabFour">
							<template #label>
								<ElIcon>
									<Timer />
								</ElIcon>
								<span class="ml-10px">BUG修复工时分布</span>
							</template>
						</el-tab-pane>
						<el-tab-pane :name="5" v-if="leaderTab && tabFive">
							<template #label>
								<ElIcon>
									<Timer />
								</ElIcon>
								<span class="ml-10px">领导交办任务工时分布</span>
							</template>
						</el-tab-pane>
						<div class="flex"></div>
					</el-tabs>
					<div class="flex">
						<div class="distribution-table">
							<!-- 字段：姓名 合计工时 任务 运维 BUG修复 领导交办 -->
							<el-table :data="tableData6" style="width: 100%" height="780" stripe>
								<el-table-column prop="name" label="姓名" align="center" />
								<el-table-column
									prop="sumWorkHoursTotal"
									label="合计工时"
									sortable
									align="center"
									v-if="tabOne"
								/>
								<el-table-column
									prop="sumWorkHoursTask"
									label="计划任务"
									sortable
									align="center"
									v-if="tabTwo"
								/>
								<el-table-column
									prop="sumWorkHoursOperation"
									label="运维"
									sortable
									align="center"
									v-if="tabThree"
								/>
								<el-table-column
									prop="sumWorkHoursTestBug"
									label="BUG修复"
									sortable
									align="center"
									v-if="tabFour"
								/>
								<el-table-column
									prop="sumWorkHoursLeaders"
									label="领导交办"
									sortable
									align="center"
									v-if="leaderTab && tabFive"
								/>
							</el-table>
						</div>
						<div class="distribution-chart">
							<!-- 第一层是部门 第二层是人员 -->
							<div ref="sunburstChartRef" style="width: 780px; height: 780px"></div>
						</div>
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
		ElTooltip,
		type TabsPaneContext
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
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import * as echarts from "echarts";
	import {
		getDeptUserAnalysis,
		getAbilityValueByUser,
		workHoursListData,
		groupListTree
	} from "@/api/statistics/staff";
	import staffOne from "./staffOne.vue";
	import staffTwo from "./staffTwo.vue";

	const props = defineProps({
		projectList: {
			type: Array,
			default: []
		},
		selectCondition: {
			type: Object,
			default: {}
		}
	});

	// 第四部分
	const radio3 = ref(1);
	const activeName = ref(1);
	const handleClick = (tab: TabsPaneContext, event: Event) => {
		initGetList();
		isAscending.value = true;
	};

	const sumDate3 = ref("");
	const sumDate4 = ref("");
	const sumDate5 = ref("");
	const startDate = ref("");
	const endDate = ref("");

	const memberList = ref([]);
	const selectedMember = ref(null);

	const analysisTable = ref([]);

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
			initGetList();
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
			initGetList();
		}
	};

	// 处理自定义日期范围选择
	const handleDateRangeChange = (dates) => {
		if (dates && dates.length === 2) {
			startDate.value = dates[0];
			endDate.value = dates[1];
			initGetList();
		}
	};

	// 日期格式化函数
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	};

	// 首次加载时的初始化函数
	const initGetList = () => {
		getDeptUserAnalysis({
			type: activeName.value,
			projectIds: props.projectList.map((item: any) => item.id).join(","),
			startDate: startDate.value,
			endDate: endDate.value
		}).then((res) => {
			memberList.value = res;
			selectMember(memberList.value[0].userId);
		});
	};

	// 定义排序状态，默认正序
	const isAscending = ref(true);

	// 切换排序状态的方法
	const toggleSort = () => {
		isAscending.value = !isAscending.value;
		// 复制一份 memberList 的数据进行排序，避免直接修改响应式数据导致的一些潜在问题
		const sortedList = [...memberList.value];

		if (isAscending.value) {
			sortedList.sort((a, b) => b.score - a.score);
		} else {
			sortedList.sort((a, b) => a.score - b.score);
		}

		// 更新 memberList 的值
		memberList.value = sortedList;
	};

	// 获取当前月份，格式为 YYYY-MM
	const getCurrentMonth = () => {
		const currentDate = new Date();
		const year = currentDate.getFullYear();
		// 月份从 0 开始计数，所以要加 1，同时补零
		const month = String(currentDate.getMonth() + 1).padStart(2, "0");
		return `${year}-${month}`;
	};

	// 选择成员的方法
	const selectMember = (id) => {
		selectedMember.value = id;
		// 调用获取能力值的方法
		getAbilityValueByUser({
			userId: id,
			type: activeName.value,
			startDate: startDate.value,
			endDate: endDate.value
		}).then((res) => {
			if (res && typeof res === "object") {
				analysisTable.value = [res];
			} else {
				analysisTable.value = [];
			}
		});
	};

	const initOnProjectListChange = async () => {
		const currentMonth = getCurrentMonth();
		sumDate3.value = currentMonth;
		handleMonthChange(currentMonth);
		initGetList();

		// 等待数据加载完成
		await Promise.all([initWorkHours(), getGroupListTree()]);

		// 检查数据是否加载完成
		if (tableData6.value.length === 0 || groupListTreeData.value.length === 0) {
			return;
		}

		// 使用 nextTick 确保 DOM 渲染完成后再初始化图表
		await nextTick();

		// 初始化旭日图
		initSunburstChart();

		// 模拟点击第一个选项卡
		const firstTab = { props: { name: "1" } } as TabsPaneContext;
		await handleClickWork(firstTab, new Event("click"));
	};

	// onMounted(initOnProjectListChange);

	// 监听 projectList 变化
	watch(
		() => props.projectList,
		async () => {
			await initOnProjectListChange();
		},
		{ deep: true } // 由于 projectList 是数组，需要深度监听
	);
	const activeWork = ref(1);
	const tableData6 = ref([]);

	const groupListTreeData = ref([]);

	const getGroupListTree = () => {
		return groupListTree()
			.then((res) => {
				groupListTreeData.value = res;
			})
			.catch((error) => {
				console.error("获取 groupListTree 数据失败:", error);
			});
	};

	const leaderTab = ref(false);

	const tabOne = ref(true);
	const tabTwo = ref(true);
	const tabThree = ref(true);
	const tabFour = ref(true);
	const tabFive = ref(true);

	const initWorkHours = () => {
		// 检查 props.projectList 是否存在且不为空
		// if (!props.projectList || props.projectList.length === 0) {
		// 	console.log("projectIds 为空，不调用 workHoursListData 接口");
		// 	tableData6.value = [];
		// 	return Promise.resolve([]);
		// }

		const projectIds = props.projectList.map((item: any) => item.id);

		leaderTab.value = projectIds.includes("-1");
		return workHoursListData({
			projectIds
		})
			.then((res) => {
				tableData6.value = res;
				// 遍历res中的sumWorkHoursTotal、sumWorkHoursTask、sumWorkHoursOperation、sumWorkHoursTestBug、sumWorkHoursLeaders，如果其中和为0，则隐藏对应的tab
				// 定义字段和对应标签页的映射
				const fieldTabMap = {
					sumWorkHoursTotal: tabOne,
					sumWorkHoursTask: tabTwo,
					sumWorkHoursOperation: tabThree,
					sumWorkHoursTestBug: tabFour,
					sumWorkHoursLeaders: tabFive
				};

				// 遍历每个字段
				for (const [field, tabRef] of Object.entries(fieldTabMap)) {
					// 计算该字段的总和
					const sum = res.reduce((acc, item) => {
						const value = parseFloat(item[field]);
						return acc + (isNaN(value) ? 0 : value);
					}, 0);

					// 如果总和为 0，则隐藏对应的标签页
					tabRef.value = sum !== 0;
				}
			})
			.catch((error) => {
				console.error("获取 workHoursListData 数据失败:", error);
			});
	};

	// 定义旭日图数据
	const sunburstData = ref([]);

	// 定义旭日图 DOM 引用
	const sunburstChartRef = ref(null);
	let sunburstChartInstance = null;

	// 初始化旭日图
	const initSunburstChart = () => {
		if (!sunburstChartRef.value) {
			return;
		}

		sunburstChartInstance = echarts.init(sunburstChartRef.value);
		const option = {
			tooltip: {
				trigger: "item",
				formatter: (params) => {
					const value = Number(params.data.value).toFixed(1); // 保留一位小数
					const name = params.data.name || "合计";
					return `${name} : ${value}`;
				}
			},
			series: {
				type: "sunburst",
				data: sunburstData.value,
				radius: [30, "90%"],
				// minAngle: 1,
				label: {
					rotate: "radial"
				}
			}
		};
		sunburstChartInstance.setOption(option);
	};

	// 根据不同的 tab 生成旭日图数据
	const generateSunburstData = (tabName: string) => {
		if (tableData6.value.length === 0) {
			return [];
		}
		// 定义 tabName 到 tableData6 字段的映射
		const tabFieldMap: Record<string, string> = {
			"1": "sumWorkHoursTotal",
			"2": "sumWorkHoursTask",
			"3": "sumWorkHoursOperation",
			"4": "sumWorkHoursTestBug",
			"5": "sumWorkHoursLeaders"
		};

		const field = tabFieldMap[tabName];

		const data = groupListTreeData.value.map((department) => ({
			name: department.dictLabel,
			children: department.groups.map((group) => ({
				name: group.name,
				children: group.sysGroupUser.map((user) => {
					// 从 tableData6 中找到对应 userId 的数据
					const userData = tableData6.value.find((item) => item.user_id === user.userId);
					// 如果找到数据则取对应字段的值，否则默认为 0
					let value = 0;
					if (userData && userData[field]) {
						const parsedValue = parseFloat(userData[field]);
						value = Number.isInteger(parsedValue) ? Math.trunc(parsedValue) : parsedValue;
					}
					return {
						name: user.userName,
						value
					};
				})
			}))
		}));
		return data;
	};
	const handleClickWork = async (tab: TabsPaneContext, event: Event) => {
		const tabName = tab.props.name;
		// 生成对应 tab 的旭日图数据
		sunburstData.value = generateSunburstData(tabName);
		await nextTick();
		// 更新旭日图
		if (sunburstChartInstance) {
			sunburstChartInstance.setOption({
				series: {
					data: sunburstData.value
				}
			});
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
				margin-right: 14px;
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
							padding: 0 10px;
							height: 44px;
							line-height: 44px;
							background: #f4f7ff;
							border-radius: 4px 4px 4px 4px;
							font-size: 14px;
							color: rgba(0, 0, 0, 0.65);
							text-align: center;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
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
		z-index: 6;
		display: flex;
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
				.el-table__empty-block {
					height: 48px !important;
					min-height: 48px !important;
				}
			}
		}
	}
	.distribution-table {
		width: calc(100% - 700px);
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
		width: 780px;
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
	:deep(.el-tabs__header) {
		margin-bottom: 15px !important;
	}
	.common-tabs-box {
		margin-top: 0 !important;
		:deep(.el-tabs__nav) {
			float: right;
		}
		:deep(.el-tabs__nav-wrap:after) {
			display: none;
		}
		:deep(.el-tabs__header) {
			position: absolute;
			top: -60px;
			right: 20px;
		}
	}
</style>
