<template>
	<div>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<ElCard header="分析维度" class="custom-header">
					<template #header>
						<div class="card-header">
							<div class="flex flex-items-center flex-justify-between">
								<div>分析维度</div>
								<ElForm :model="form" labelWidth="100px" ref="formSearch" :inline="true">
									<ElRow :gutter="10">
										<ElCol :span="24">
											<ElFormItem label="时间粒度：">
												<ElRadioGroup v-model="form.timeType" class="m-r-8px">
													<ElRadioButton label="按月" value="1" />
													<ElRadioButton label="按季" value="2" />
													<ElRadioButton label="按年" value="3" />
												</ElRadioGroup>
												<ElDatePicker
													class="time-type"
													style="width: 160px; margin-left: 10px"
													v-if="form.timeType == 1"
													key="month"
													v-model="form.time"
													format="YYYY-MM"
													value-format="YYYY-MM"
													type="month"
													@change="changeMonth"
													placeholder="请选择月"
												/>
												<!-- <ElSelect
													style="width: 220px"
													v-if="form.timeType == 2"
													v-model="form.quarter"
													placeholder="请选择季度"
													@change="changeQuarter"
												>
													<ElOption label="第一季度" value="1"></ElOption>
													<ElOption label="第二季度" value="2"></ElOption>
													<ElOption label="第三季度" value="3"></ElOption>
													<ElOption label="第四季度" value="4"></ElOption>
												</ElSelect> -->
												<!-- <ElDatePicker
													v-if="form.timeType == 2"
													v-model="form.quarter"
													class="time-type"
													type="quarter"
													key="quarter"
													format="YYYY [Q]Q"
													value-format="YYYY-Q"
													placeholder="选择季度"
													@change="handleQuarterChange"
												/> -->
												<ElQuarter
													v-if="form.timeType == 2"
													class="time-type"
													style="width: 160px; margin-left: 10px"
													v-model="form.quarter"
													@change="handleQuarterChange"
													placeholder="选择季度"
												/>
												<ElDatePicker
													v-if="form.timeType == 3"
													class="time-type"
													style="width: 160px; margin-left: 10px"
													format="YYYY"
													value-format="YYYY"
													key="year"
													v-model="form.year"
													type="year"
													@change="changeYear"
													placeholder="请选择年"
												/>
											</ElFormItem>
											<ElButton type="primary" @click="searchProject">查询项目</ElButton>
										</ElCol>
									</ElRow>
								</ElForm>
							</div>
						</div>
					</template>

					<div>
						<div
							class="flex flex-items-center flex-col flex-justify-center no-data"
							v-if="projectProgressList.length == 0 && projectList.length == 0"
						>
							<img src="@/assets/statistics/no-data.png" alt="" />
							<div>暂无数据，请在右上角选择时间和项目后挖掘数据</div>
						</div>
						<div class="flex flex-items-start project-wrap" v-if="projectProgressList.length > 0">
							<div class="progress">进行中：</div>
							<ElRow class="flex-1">
								<ElCol :span="3">
									<el-checkbox
										v-model="checkProgressAll"
										:indeterminate="isProgressIndeterminate"
										@change="handleCheckProgressAllChange"
									>
										全部
									</el-checkbox>
								</ElCol>
								<ElCol :span="3" v-for="(item, index) in projectProgressList" :key="index">
									<el-checkbox
										v-model="item.checked"
										:label="item.projectName"
										:value="item.id"
										@change="changeProgressCheck"
									>
										<div class="textOverflow">{{ item.projectName }}</div>
									</el-checkbox>
								</ElCol>
							</ElRow>
						</div>
						<div class="flex flex-items-start project-wrap m-t-16px" v-if="projectList.length > 0">
							<div class="progress">未进行：</div>
							<ElRow class="flex-1">
								<ElCol :span="3">
									<el-checkbox
										v-model="checkAll"
										:indeterminate="isIndeterminate"
										@change="handleCheckAllChange"
									>
										全部
									</el-checkbox>
								</ElCol>
								<ElCol :span="3" v-for="(item, index) in projectList" :key="index">
									<el-checkbox
										v-model="item.checked"
										:label="item.projectName"
										:value="item.id"
										@change="changeCheck"
									>
										<div class="textOverflow">{{ item.projectName }}</div>
									</el-checkbox>
								</ElCol>
							</ElRow>
						</div>
					</div>
					<template #footer>
						<div style="float: right; margin-bottom: 16px">
							<ElButton :icon="Refresh" @click="onReset"> 重置 </ElButton>
							<ElButton :icon="Search" type="primary" @click="onSearch"> 挖掘数据 </ElButton>
						</div>
					</template>
				</ElCard>
			</ElCol>
			<ElCol :span="8"></ElCol>
		</ElRow>
		<ElTabs v-model="activeName" type="card" class="statistic-tabs" @tab-click="handleClick">
			<el-tab-pane label="按项目统计分析" name="first">
				<div class="content-statistic">
					<ProjectStatistic ref="projectStatistic" />
				</div>
			</el-tab-pane>
			<el-tab-pane label="按人员统计分析" name="second">
				<staffStatistic :projectList="queryParams.selectProject" :selectCondition="selectCondition" />
			</el-tab-pane>
			<el-tab-pane label="项目工时核算表" name="third">
				<div class="content-statistic">
					<workTimeStatistic
						:projectList="queryParams.selectProject"
						:startDate="queryParams.startDate"
						:endDate="queryParams.endDate"
					/>
				</div>
			</el-tab-pane>
		</ElTabs>
	</div>
</template>
<script setup lang="tsx">
	import { ref, reactive } from "vue";
	import { Search, Refresh, ArrowDown } from "@element-plus/icons-vue";
	import ProjectStatistic from "./components/projectStatistic.vue";
	import workTimeStatistic from "./components/workTimeStatistic.vue";
	import { analysisDimension } from "@/api/statistics/statistics";
	import staffStatistic from "./components/staffStatistic.vue";
	import { ElMessage } from "element-plus";
	import ElQuarter from "@/components/quarter/index.vue";
	import dayjs from "dayjs";
	const date = new Date();
	const year = date.getFullYear();
	const month = date.getMonth() - 9 >= 0 ? `${date.getMonth() + 1}` : "0" + (date.getMonth() + 1);

	// 查询条件
	const queryParams = reactive({
		startDate: year + "-" + month + "-01 00:00:00",
		endDate:
			dayjs(year + "-" + month)
				.endOf("month")
				.format("YYYY-MM-DD") + " 23:59:59",
		selectProject: []
	});

	// 重置
	const onReset = () => {
		form.value.timeType = "1";
		form.value.time = year + "-" + month;
		projectProgressList.value = [];
		projectList.value = [];
		queryParams.startDate = year + "-" + month + "-01 00:00:00";
		queryParams.endDate =
			dayjs(year + "-" + month)
				.endOf("month")
				.format("YYYY-MM-DD") + " 23:59:59";
		queryParams.selectProject = [];
		checkProgressAll.value = false;
		checkAll.value = false;
		isIndeterminate.value = false;
		isProgressIndeterminate.value = false;
		searchProject();
		projectStatistic.value.handleReset();
	};

	const form = ref({
		timeType: "1",
		time: year + "-" + month,
		quarter: ""
	});
	// 日期格式化函数
	const formatDate = (date) => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	};

	// 改变月
	const changeMonth = (val) => {
		queryParams.startDate = val + "-01 00:00:00";
		queryParams.endDate = dayjs(val).endOf("month").format("YYYY-MM-DD") + " 23:59:59";
	};

	// 改变季度
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

			queryParams.startDate = formatDate(startOfQuarter) + " 00:00:00";
			queryParams.endDate = formatDate(endOfQuarter) + " 23:59:59";
		}
	};

	// 改变年
	const changeYear = (val) => {
		queryParams.startDate = val + "-01-01 00:00:00";
		queryParams.endDate = val + "-12-31 23:59:59";
	};

	const checkProgressAll = ref(false);
	const checkAll = ref(false);
	const projectProgressList = ref([]);
	const projectList = ref([]);

	// 查询项目
	const isIndeterminate = ref(false);
	const isProgressIndeterminate = ref(false);
	const searchProject = () => {
		checkProgressAll.value = false;
		checkAll.value = false;
		isIndeterminate.value = false;
		isProgressIndeterminate.value = false;
		analysisDimension(queryParams).then((res) => {
			projectList.value = res.notStart.map((val) => {
				val.checked = false;
				if (val.projectName == "领导交办任务") {
					val.id = "-1";
				}
				return val;
			});
			projectProgressList.value = res.underWay.map((val) => {
				val.checked = false;
				if (val.projectName == "领导交办任务") {
					val.id = "-1";
				}
				return val;
			});
		});
	};

	searchProject();

	const projectStatistic = ref();
	const selectCondition = ref({});
	// 挖掘数据
	const onSearch = () => {
		// 拿到选中的数据
		const selectProgressArr = projectProgressList.value.filter((val) => val.checked);
		const selectArr = projectList.value.filter((val) => val.checked);
		queryParams.selectProject = selectProgressArr.concat(selectArr);
		if (queryParams.selectProject.length == 0) {
			return ElMessage.error("至少选择一个项目");
		}
		if (queryParams.selectProject.length == 1 && queryParams.selectProject[0].projectName == "领导交办任务") {
			return ElMessage.error("不可以只选择领导交办，至少再选择一个项目");
		}
		if ((activeName.value = "first")) {
			projectStatistic.value.init(queryParams.selectProject);
		}
		selectCondition.value = JSON.parse(JSON.stringify(form.value));
	};
	// 进行中全选
	const handleCheckProgressAllChange = (val) => {
		projectProgressList.value = projectProgressList.value.map((item) => {
			item.checked = val;
			return item;
		});
	};
	// 全选
	const handleCheckAllChange = (val) => {
		projectList.value = projectList.value.map((item) => {
			item.checked = val;
			return item;
		});
	};

	// 进行中数据选择
	const changeProgressCheck = () => {
		const selectProgressArr = projectProgressList.value.filter((val) => val.checked);
		isProgressIndeterminate.value =
			selectProgressArr.length > 0 && selectProgressArr.length < projectProgressList.value.length;
		if (selectProgressArr.length === projectProgressList.value.length) {
			checkProgressAll.value = true;
		} else if (selectProgressArr.length == 0) {
			checkProgressAll.value = false;
		}
	};
	// 未开始数据选择
	const changeCheck = () => {
		const selectArr = projectList.value.filter((val) => val.checked);
		isIndeterminate.value = selectArr.length > 0 && selectArr.length < projectList.value.length;
		if (selectArr.length === projectList.value.length) {
			checkAll.value = true;
		} else if (selectArr.length == 0) {
			checkAll.value = false;
		}
	};

	const activeName = ref("first");
	const handleClick = () => {};
</script>

<style scoped lang="scss">
	.time-type {
		width: 160px;
		margin-left: 10px;
	}
	.card-header {
		width: 100%;
		font-weight: bold;
	}

	.project-wrap {
		text-align: left;
		.progress {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 38px;
		}
	}
	.box {
		width: 44px;
		height: 20px;
		border-radius: 4px 4px 4px 4px;
		border: 1px dotted rgba(30, 80, 137, 0.51);
		margin: 0 8px;
	}
	.statistic-tabs {
		margin-top: 16px;
	}
	.content-statistic {
		background: #fff;
		text-align: left;
	}

	.no-data {
		height: 256px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.45);
		line-height: 22px;
		img {
			width: 165px;
			height: 165px;
			margin-bottom: 16px;
		}
	}

	.textOverflow {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	::v-deep {
		.el-card__header {
			height: 56px;
			display: flex;
			align-items: center;
			padding: 0 32px;
		}

		// checkbox
		.el-checkbox {
			overflow: hidden;
			width: 100%;
			height: 38px;

			.el-checkbox__label {
				overflow: hidden;
			}
		}

		.el-form-item {
			margin-right: 8px;
			margin-bottom: 0;
		}
		.statistic-tabs > .el-tabs__header {
			margin: 0;
			.el-tabs__nav {
				border: 0 !important;
			}
			.el-tabs__item {
				background: #fff;
				border: 1px solid rgba(0, 0, 0, 0.06);
				border-radius: 8px 8px 0 0;
				margin-right: 8px;

				&.is-active {
					background: linear-gradient(180deg, #ffffff 0%, #d0e6ff 100%);
					box-shadow: inset 0px 0px 4px 0px #1a81f4;
					border-radius: 8px 8px 0px 0px;
					border-bottom: 2px solid #1b86ff;
					border-bottom-color: #1a81f4 !important;
				}
			}
		}
	}
</style>
