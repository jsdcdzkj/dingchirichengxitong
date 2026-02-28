<template>
	<!-- 第二部分 -->
	<ElCard class="box-card mt-16px">
		<template #header>
			<div class="card-header">
				<span>人员成本总览</span>
			</div>
		</template>
		<div class="flex flex-items-center flex-col flex-justify-center no-data" v-if="props.projectList.length == 0">
			<img src="@/assets/statistics/no-data.png" alt="" />
			<div>暂无数据</div>
		</div>
		<div v-else>
			<div class="flex">
				<div class="common-box" style="width: calc(100% - 810px)">
					<div class="common-header">
						<div class="common-title flex flex-items-center">
							<ProjectInfoIcon name="icon-title.png" />
							<span>各组成本分析</span>
						</div>
					</div>
					<div class="common-body flex flex-items-center" style="margin-top: 20px">
						<div class="table-box-separate flex-1">
							<el-table
								:data="tableData3"
								style="width: 100%"
								height="400"
								stripe
								show-summary
								@selection-change="handleTable3SelectionChange"
								ref="tableRef"
								:row-key="(row) => row.groupId"
							>
								<el-table-column type="selection" width="55" align="center" fixed="left" />
								<el-table-column
									prop="groupName"
									label="组名称"
									min-width="170"
									fixed="left"
									align="center"
								/>
								<el-table-column prop="taskPrice" label="任务成本(元)" width="120" align="center" />
								<el-table-column
									prop="operationPrice"
									label="运维成本(元)"
									width="120"
									align="center"
								/>
								<el-table-column prop="bugPrice" label="BUG修复成本(元)" width="140" align="center" />
								<el-table-column
									prop="leadersPrice"
									label="领导交办成本(元)"
									width="140"
									align="center"
								/>
								<el-table-column prop="planPrice" label="计划成本(元)" width="120" align="center" />
								<el-table-column prop="total" label="实际成本(元)" width="120" align="center" />
							</el-table>
						</div>
					</div>
				</div>
				<div class="point-line"></div>
				<div class="common-box" style="width: 756px">
					<div class="common-header">
						<div class="common-title flex flex-items-center">
							<ProjectInfoIcon name="icon-title.png" />
							<span>选定组各项目成本统计</span>
						</div>
					</div>
					<div class="common-body flex flex-items-center" style="margin-top: 20px">
						<div class="echart-table">
							<div class="echart-table-header">
								<div class="echart-table-header-item">
									<span>项目名称</span>
								</div>
								<div class="echart-table-header-item">
									<span>实际工时(人天)</span>
								</div>
								<div class="echart-table-header-item">
									<span>开发成本(元)</span>
								</div>
								<div class="legend-item">
									<span></span>
								</div>
							</div>
							<div class="echart-table-body">
								<div class="echart-table-list" v-for="(item, index) in echartDataList" :key="index">
									<div class="echart-table-body-item">
										<!-- 加上tooptip -->

										<ElTooltip
											class="box-item"
											effect="dark"
											:content="item.projectName"
											placement="top"
										>
											<span>{{ item.projectName }}</span>
										</ElTooltip>
									</div>
									<div class="echart-table-body-item">
										<span>{{ item.sumHours }}</span>
									</div>
									<div class="echart-table-body-item">
										<span>{{ item.taskPrice }}</span>
									</div>
									<div class="legend-item" :style="{ backgroundColor: colors[index] }"></div>
								</div>
							</div>
							<div class="echart-table-bot">
								<div class="echart-table-bot-item">
									<span>合计</span>
								</div>
								<div class="echart-table-bot-item">
									<span>{{ sumHoursAll }}</span>
								</div>
								<div class="echart-table-bot-item">
									<span>{{ taskPriceAll }}</span>
								</div>
								<div class="legend-item">
									<span></span>
								</div>
							</div>
						</div>
						<div ref="chartRef" style="width: 370px; height: 400px"></div>
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
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import * as echarts from "echarts";
	import { getDeptStatisticsCost, getDeptStatisticsCostList } from "@/api/statistics/staff";
	import staffOne from "./staffOne.vue";

	const props = defineProps({
		projectList: {
			type: Array,
			default: []
		}
	});

	// 第二部分开始
	const tableData3 = ref([]);
	const tableRef = ref(null);

	const fetchTableData3 = () => {
		getDeptStatisticsCost({
			projectIds: props.projectList.map((item: any) => item.id).join(",")
			// startDate: startDate.value,
			// endDate: endDate.value
		}).then((res) => {
			// 复制数组并移除最后一项 因为最后一项是后端给生成的一条合计数据
			// 把res中的 total为0的项去掉
			const newRes = [...res.filter((item) => item.total !== 0)];
			newRes.pop();
			tableData3.value = newRes;
		});
	};

	onMounted(() => {
		fetchTableData3(); // 组件挂载时获取数据
		// 监听 tableData3 变化，数据加载完成后选中第一条
		// watch(
		// 	tableData3,
		// 	async () => {
		// 		if (tableData3.value.length > 0 && tableRef.value) {
		// 			await nextTick(); // 等待 DOM 更新完成
		// 			tableRef.value.toggleRowSelection(tableData3.value[0], true);
		// 		}
		// 	},
		// 	{ immediate: true }
		// );
	});
	// 监听 projectList 变化
	watch(
		() => props.projectList,
		() => {
			fetchTableData3(); // 重新获取表格数据
		},
		{ deep: true } // 由于 projectList 是数组，需要深度监听
	);

	// 监听 tableData3 变化，在数据更新且表格渲染完成后选中第一项
	watch(
		tableData3,
		async () => {
			await nextTick();
			await nextTick();
			if (tableData3.value.length > 0 && tableRef.value) {
				console.log("准备选中第一项，数据：", tableData3.value[0]);
				try {
					tableRef.value.toggleRowSelection(tableData3.value[0], true);
					console.log("已尝试选中第一项");
				} catch (error) {
					console.error("选中第一项失败：", error);
				}
			}
		},
		{ deep: true }
	);

	// 自定义合计方法
	const getSummaries = ({ columns, data }) => {
		const sums = [];
		columns.forEach((column, index) => {
			if (index === 0) {
				sums[index] = "合计";
				return;
			}
			const values = data.map((item) => Number(item[column.property]));
			if (!values.every((value) => isNaN(value))) {
				sums[index] = values.reduce((prev, curr) => {
					const value = Number(curr);
					if (!isNaN(value)) {
						return prev + curr;
					} else {
						return prev;
					}
				}, 0);
				sums[index] += " 元";
			} else {
				sums[index] = "";
			}
		});
		return sums;
	};
	const tableData4 = ref([
		// 示例数据，实际使用时替换为真实数据
		{ project: "项目1", hours: 10, cost: "1000" },
		{ project: "项目2", hours: 20, cost: "2000" }
	]);

	const chartRef = ref(null);
	let chartInstance = null;

	const echartDataList = ref([]);

	// 处理表格选中项变化
	const handleTable3SelectionChange = async (selection) => {
		const groupIds = selection.map((item) => item.groupId);
		console.log("选中的 groupIds:", props.projectList.map((item: any) => item.id).join(","));
		if (groupIds.length > 0) {
			try {
				const response = await getDeptStatisticsCostList({
					groupIds: groupIds.join(","),
					projectIds: props.projectList.map((item: any) => item.id).join(",")
				});
				echartDataList.value = response || [];
				initChart();
			} catch (error) {
				console.error("获取 echartDataList 数据失败:", error);
			}
		} else {
			echartDataList.value = []; // 没有选中项时清空数据
			if (chartInstance) {
				chartInstance.clear(); // 清空图表
			}
		}
	};

	// 计算 sumHoursAll
	const sumHoursAll = computed(() => {
		const total = echartDataList.value.reduce((acc, item) => {
			const sumHours = Number(item.sumHours) || 0;
			return acc + sumHours;
		}, 0);
		// 保留两位小数并转换为数字
		return Number(total.toFixed(2));
	});

	// 计算 taskPriceAll
	const taskPriceAll = computed(() => {
		const total = echartDataList.value.reduce((acc, item) => {
			const taskPrice = Number(item.taskPrice) || 0;
			return acc + taskPrice;
		}, 0);
		// 保留两位小数并转换为数字
		return Number(total.toFixed(2));
	});

	const colors = ref([
		"#91CC75",
		"#FAC858",
		"#EE6666",
		"#73C0DE",
		"#3BA272",
		"#FC8452",
		"#9A60B4",
		"#EA7CCC",
		"#5470C6",
		"#A5D87A",
		"#FFD166",
		"#F57A7A",
		"#8ACDE6",
		"#4CBA8A",
		"#FF9A5E",
		"#B075C9",
		"#F28FD6",
		"#6A80D1",
		"#B8E38D",
		"#FFDB7D",
		"#FF8E8E",
		"#A1D9ED",
		"#5DC292",
		"#FFB07A",
		"#C68ADF",
		"#FAA2E0",
		"#8090DC",
		"#7BC95F",
		"#FFE08C",
		"#EE8888",
		"#67B5D5",
		"#2D9D6E",
		"#F77D4A",
		"#8E55A9",
		"#E56DB8",
		"#4A5EC1",
		"#C4E6A3",
		"#FFEA9E",
		"#F79999",
		"#7FC9E8",
		"#43B17D",
		"#FF8D5C",
		"#A86BBD",
		"#EF92D1",
		"#5D6EC8",
		"#9ED489",
		"#FFDE74",
		"#F26B6B",
		"#89D2E9",
		"#39AD78"
	]);

	// 初始化图表
	const initChart = () => {
		if (!chartRef.value) return;

		chartInstance = echarts.init(chartRef.value);
		const option = {
			tooltip: {
				trigger: "item",
				// 自定义 tooltip 样式
				backgroundColor: "rgba(255, 255, 255, 0.9)",
				borderColor: "#ddd",
				borderWidth: 1,
				textStyle: {
					color: "#333"
				},
				// 自定义 tooltip 内容
				formatter: (params) => {
					const item = echartDataList.value.find((data) => data.projectName === params.name);
					if (item) {
						return `
	                       <div style="padding: 10px;text-algin:left">
	                           <div style="font-weight: bold; color: #275798; margin-bottom: 8px;text-align: left;">${item.projectName}</div>
	                           <div style="margin-bottom: 4px;text-align: left;">实际工时(人天)：<span style="color: #666;">${item.sumHours}</span></div>
	                           <div style="margin-bottom: 4px;text-align: left;">开发成本(元)： <span style="color: #666;">${item.taskPrice}</span></div>
	                           <div style="text-align: left;">占比：<span style="color: #666;">${params.percent}%</span></div>
	                       </div>
	                   `;
					}
					return params.name;
				}
			},
			series: [
				{
					type: "pie",
					radius: ["20%", "94%"],
					center: ["50%", "50%"],
					roseType: "radius",
					label: { show: false },
					data: echartDataList.value.map((item) => ({
						value: item.taskPrice,
						name: item.projectName
					})),

					color: colors.value
				}
			]
		};
		chartInstance.setOption(option);
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
						background-color: #fff;
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
					height: 302px;
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
</style>
