<template>
	<div class="main-box">
		<groupOne :projectList="props.groupInfo" />

		<!-- 第二 -->
		<div class="flex">
			<ElCard class="box-card mt-16px flex-1">
				<template #header>
					<div class="card-header flex flex-justify-between">
						<span>高风险项目任务预警</span>
						<!-- 右对齐的下拉选 -->
						<div class="" style="width: 260px">
							<!-- <ElSelect v-model="value" placeholder="请选择" style="width: 100px">
								<ElOption label="选项1" value="1" />
								<ElOption label="选项2" value="2" />
								<ElOption label="选项3" value="3" />
							</ElSelect> -->
							<ElSelect
								v-model="projectName"
								placeholder="请选择"
								clearable
								filterable
								@change="handleChange"
								style="width: 260px"
							>
								<ElOption :key="'all'" :label="'全部'" :value="0"></ElOption>
								<ElOption
									v-for="item in options"
									:key="item.id"
									:label="item.projectName"
									:value="item.id"
								></ElOption>
							</ElSelect>
						</div>
					</div>
				</template>
				<Carousel ref="carouselRef" :list="list" class="m-t-10px" h="342px" />
			</ElCard>
			<div style="width: 16px"></div>
			<ElCard class="box-card mt-16px flex-1">
				<template #header>
					<div class="card-header flex flex-justify-between">
						<span>7天内待评审里程碑</span>
					</div>
				</template>
				<div class="table-box-separate2 flex-1">
					<el-table :data="tableData3" style="width: 100%" height="370" stripe>
						<el-table-column prop="milestoneName" label="里程碑名称" min-width="120" align="center" />
						<el-table-column prop="projectName" label="项目名称" min-width="120" align="center" />
						<el-table-column prop="appointFinishTime" label="约定评审时间" width="120" align="center" />
						<el-table-column prop="groupMembers" label="评审组员" min-width="140" align="center" />
					</el-table>
				</div>
			</ElCard>
		</div>
		<!-- 第三 -->
		<div class="flex">
			<ElCard class="box-card mt-16px flex-1">
				<template #header>
					<div class="card-header flex flex-justify-between">
						<span>人员贡献统计分析</span>
						<!-- 右对齐的下拉选 -->
						<div class="" style="width: 260px">
							<ElSelect
								v-model="projectName2"
								placeholder="请选择"
								clearable
								filterable
								@change="handleChange2"
								style="width: 260px"
							>
								<ElOption :key="'all'" :label="'全部'" :value="0"></ElOption>
								<ElOption
									v-for="item in options"
									:key="item.id"
									:label="item.projectName"
									:value="item.id"
								></ElOption>
							</ElSelect>
						</div>
					</div>
				</template>
				<div>
					<!-- 上 -->
					<div class="flex">
						<!-- 1 -->
						<div class="common-box flex-1">
							<div class="common-header">
								<div class="common-title flex flex-items-center">
									<ProjectInfoIcon name="icon-title.png" />
									<span>贡献排名</span>
								</div>
							</div>
							<div class="common-body pr-24px">
								<div class="statistics-list">
									<div class="statistics-item" v-for="(item, index) in contributeList">
										<p>【{{ item.name }}】{{ item.score }}分</p>
										<span class="lengend" :style="{ backgroundColor: colors[index] }"></span>
									</div>
								</div>
							</div>
						</div>
						<!-- 2 -->
						<div class="common-box flex-1">
							<div class="common-header">
								<div class="common-title flex flex-items-center">
									<ProjectInfoIcon name="icon-title.png" />
									<span>综合BUG指数排名</span>
								</div>
							</div>
							<div class="common-body pr-24px">
								<div class="statistics-list">
									<div class="statistics-item" v-for="(item, index) in bugList">
										<p>【{{ item.name }}】{{ item.score }}分</p>
										<span class="lengend" :style="{ backgroundColor: colors[index] }"></span>
									</div>
								</div>
							</div>
						</div>
						<!-- 3 -->
						<div class="common-box flex-1">
							<div class="common-header">
								<div class="common-title flex flex-items-center">
									<ProjectInfoIcon name="icon-title.png" />
									<span>成员BUG数量占比</span>
								</div>
							</div>
							<div class="common-body">
								<div ref="chartRef" style="width: 100%; height: 280px; margin-top: 20px"></div>
							</div>
						</div>
					</div>
					<!-- 下 -->
					<div class="common-box mt-16px">
						<TeamChartAnly :groupId="props.groupInfo.id" :projectId="selectProjectId" />
					</div>
				</div>
			</ElCard>
			<div style="width: 16px"></div>
			<TeamWorkTime :groupId="props.groupInfo.id" :projectId="selectProjectId" />
		</div>
	</div>
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
	import Carousel from "@/components/module/project/carousel/index.vue";
	import { highRiskTaskList } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { groupList } from "@/api/project/info";
	import groupOne from "./groupOne.vue";
	import { selectProjectList } from "@/api/test/issues";
	import {
		getUserTaskInfoList,
		getTaskMilestone,
		getContributionDegree,
		getBugIndex,
		getBugCount,
		getOvertimeWork
	} from "@/api/task/group";

	// const props = defineProps({
	// 	projectList: {
	// 		type: Array,
	// 		default: []
	// 	}
	// });
	const props = defineProps({
		groupInfo: {
			type: Object,
			default: () => {
				return {};
			}
		}
	});

	// 第二部分开始

	const projectName = ref(0); // 项目名称
	const projectName2 = ref(0); // 项目名称
	const options = ref([]); // 项目名称选项
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

	const list = ref([]);

	function calculateTimeRemaining(totalSeconds: number) {
		if (totalSeconds < 60) {
			return { h: 0, m: totalSeconds };
		}
		const h = Math.floor(totalSeconds / 60);
		const m = Math.floor(totalSeconds % 60);
		return { h, m };
	}

	const getList = async () => {
		const params = {
			groupId: props.groupInfo.id,
			pageNo: 1,
			pageSize: 99,
			...(projectName.value !== 0 && { projectId: projectName.value })
		};

		const res = await getUserTaskInfoList(params);
		if (Array.isArray(res)) {
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
					isPostpone: item.isPostpone,
					title: item.title,
					task_attribute,
					postPoneTemp: h > 0 ? `${h}小时 ${m}分钟` : `${m}分钟`
				};
			});
			for (let i = 0; i < listMap.length; i += 3) {
				list.value.push(listMap.slice(i, i + 3));
			}
		} else {
			console.error("API 返回数据格式不正确", res);
			ElMessage.error("获取数据格式错误，请稍后重试");
		}
	};

	const handleChange = async (value) => {
		try {
			projectName.value = value;
			list.value = [];
			await getList();
		} catch (error) {
			console.error("处理选择变化时出错:", error);
			ElMessage.error("获取数据失败，请稍后重试");
		}
	};
	const contributeList = ref([]);
	const bugList = ref([]);
	const selectProjectId = ref(0);

	const getContributionDegreeData = async () => {
		const res = await getContributionDegree({
			groupId: props.groupInfo.id,
			projectId: projectName2.value !== 0 ? projectName2.value : undefined
		});
		contributeList.value = res;

		const res2 = await getBugIndex({
			groupId: props.groupInfo.id,
			projectId: projectName2.value !== 0 ? projectName2.value : undefined
		});
		bugList.value = res2;

		await getEchartData();
	};

	const echartDataList = ref([]);
	const getEchartData = async () => {
		const res = await getBugCount({
			groupId: props.groupInfo.id,
			pro_project_id: projectName2.value !== 0 ? projectName2.value : undefined
		});
		echartDataList.value = res.map((item) => ({
			...item,
			count: Number(item.count)
		}));
		console.log("echartDataList", echartDataList.value);
	};
	const chartRef = ref(null);
	let chartInstance = null;

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
				}
				// 自定义 tooltip 内容
				// formatter: (params) => {
				// 	const item = echartDataList.value.find((data) => data.realName === params.name);
				// 	if (item) {
				// 		return `
				//            <div style="padding: 10px;">
				//                <div style="font-weight: bold; color: #1890ff; margin-bottom: 8px;">${item.realName}</div>
				//                <div style="margin-bottom: 4px;">数量: <span style="color: #666;">${item.count}</span></div>
				//                <div>占比: <span style="color: #f5222d;">${params.percent}%</span></div>
				//            </div>
				//        `;
				// 	}
				// 	return params.name;
				// }
			},
			series: [
				{
					type: "pie",
					radius: ["20%", "100%"],
					center: ["50%", "50%"],
					roseType: "radius",
					color: colors.value,
					label: { show: false },
					data: echartDataList.value.map((item) => ({
						value: item.count,
						name: item.realName
					}))
					// color: ["#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de", "#3ba272"]
				}
			]
		};
		chartInstance.setOption(option);
	};
	// 监听 echartDataList 变化，更新图表
	watch(
		echartDataList,
		() => {
			if (chartInstance) {
				const option = {
					series: [
						{
							data: echartDataList.value.map((item) => ({
								value: item.count,
								name: item.realName
							}))
						}
					]
				};
				chartInstance.setOption(option);
			}
		},
		{ deep: true }
	);
	const handleChange2 = async (value) => {
		projectName2.value = value;
		selectProjectId.value = value;
		console.log("selectProjectId.value", selectProjectId.value);
		contributeList.value = [];
		await getContributionDegreeData();
	};
	onMounted(async () => {
		const res = await selectProjectList({});
		options.value = res;

		await getTableData3();
		await getContributionDegreeData();
		initChart();
		getList();
	});

	const tableData3 = ref([]);
	const getTableData3 = async () => {
		const res = await getTaskMilestone({
			groupId: props.groupInfo.id
		});
		tableData3.value = res;
	};

	// 定义 ref 引用 Carousel 组件
	const carouselRef = ref(null);

	// 示例：在某个方法里调用子组件的方法
	const callCarouselMethod = () => {
		if (carouselRef.value) {
			carouselRef.value.handleChange2;
		}
	};

	watch(
		() => props.groupInfo,
		async (newGroupInfo) => {
			await getTableData3();
			list.value = [];
			await getList();
			await getContributionDegreeData();
			callCarouselMethod();
		},
		{ deep: true }
	);

	// 第三部分

	const groupListRef = ref([]);

	groupList().then((res) => {
		groupListRef.value = res;
	});
	// 模拟更新 selectProjectId 的函数
	const updateProjectId = (newId) => {
		selectProjectId.value = newId;
		console.log("父组件更新 selectProjectId 为", newId);
	};

	// 监听 groupInfo.id 的变化
	watch(
		() => props.groupInfo.id,
		(newId) => {
			console.log("父组件监听到 groupInfo.id 变化为", newId);
		}
	);

	// 生命周期
	onMounted(() => {
		initChart();
	});
</script>
<style scoped lang="scss">
	.main-box {
		height: 100%;
		overflow-y: auto;
	}
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
			.point-line {
				margin: 0 24px;
				position: relative;
				width: 4px;
				height: 444px;
				background: linear-gradient(
					180deg,
					rgba(27, 134, 255, 0.12) 0%,
					#1b86ff 50%,
					rgba(27, 134, 255, 0.12) 100%
				);
				&::after {
					// 右边指向的三角形 处于线的中间位置
					content: "";
					position: absolute;
					top: 50%;
					right: -10px;
					width: 0;
					height: 0;
					border-top: 10px solid transparent;
					border-bottom: 10px solid transparent;
					border-left: 10px solid #1b86ff;
					transform: translateY(-50%);
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
				margin-right: 32px;
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
	.table-box-separate2 {
		border-radius: 8px;
		border: 1px solid #d7dae2;
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
					border-left: 2px solid #f9f9f9;
				}
				&:first-child .cell {
					border-left: none;
				}
			}
			td.el-table__cell {
				border-bottom: 1px solid #f9f9f9;
				border-left: 2px solid #f9f9f9;
				&:first-child {
					border-left: none;
				}
			}
		}
		:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
			background-color: #f4f7ff;
		}
	}
	.statistics-list {
		width: 100%;
		height: 287px;
		background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);
		border-radius: 12px 12px 12px 12px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		overflow: hidden;
		overflow-y: auto;
		.statistics-item {
			padding: 16px;
			display: flex;
			align-items: center;
			border-bottom: 1px dashed rgba(0, 0, 0, 0.25);
			&:last-child {
				border-bottom: none;
			}
			p {
				flex: 1;
				margin: 0 16px 0 0;
				font-size: 14px;
				color: #344e6b;
				line-height: 22px;
				text-align: left;
			}
			.lengend {
				width: 12px;
				height: 12px;
				background: #5a6fc0;
				border-radius: 32px;
			}
		}
	}
</style>
