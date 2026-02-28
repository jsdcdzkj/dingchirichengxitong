<template>
	<!-- 第三部分 -->
	<ElCard class="box-card">
		<div class="position-relative">
			<div class="member-search ml-22px">
				<div>
					<el-radio-group v-model="radio3">
						<el-radio-button label="按月" :value="1" />
						<el-radio-button label="按季" :value="2" />
						<el-radio-button label="自定义" :value="3" />
					</el-radio-group>
				</div>
				<div>
					<template v-if="radio3 === 1">
						<ElDatePicker
							v-model="sumDate3"
							type="month"
							format="YYYY-MM"
							value-format="YYYY-MM"
							placeholder="选择月份"
							style="width: 140px; margin-left: 8px"
							@change="handleMonthChange"
						/>
					</template>
					<template v-else-if="radio3 === 2">
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
					</template>
					<template v-else-if="radio3 === 3">
						<ElDatePicker
							v-model="sumDate5"
							type="daterange"
							format="YYYY-MM-DD"
							value-format="YYYY-MM-DD"
							placeholder="选择日期范围"
							style="width: 280px; margin-left: 8px"
							@change="handleDateRangeChange"
						/>
					</template>
				</div>

				<!-- 请选择日期 -->
			</div>
			<el-tabs v-model="activeName" @tab-click="handleClick">
				<el-tab-pane :name="1">
					<template #label>
						<ElIcon>
							<Avatar />
						</ElIcon>
						<span class="ml-10px">前端开发</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="2">
					<template #label>
						<ElIcon>
							<Avatar />
						</ElIcon>
						<span class="ml-10px">后端开发</span>
					</template>
				</el-tab-pane>
				<el-tab-pane :name="3">
					<template #label>
						<ElIcon>
							<Avatar />
						</ElIcon>
						<span class="ml-10px">产品/项目经理</span>
					</template>
				</el-tab-pane>
				<!-- <el-tab-pane name="4">
					<template #label>
						<ElIcon>
							<Avatar />
						</ElIcon>
						<span class="ml-10px">设计</span>
					</template>
				</el-tab-pane> -->
				<el-tab-pane :name="4">
					<template #label>
						<ElIcon>
							<Avatar />
						</ElIcon>
						<span class="ml-10px">测试</span>
					</template>
				</el-tab-pane>
				<div class="overflow-hidden" v-loading="loading">
					<div class="common-box" style="width: 250px; float: left">
						<div class="common-header">
							<div class="common-title flex flex-items-center">
								<ProjectInfoIcon name="icon-title.png" />
								<span class="mr-6px">人员能力值分析</span>
								<ElTooltip class="box-item" effect="dark" placement="top">
									<template #default>
										<ElIcon>
											<QuestionFilled style="color: rgba(0, 0, 0, 0.45); cursor: pointer" />
										</ElIcon>
									</template>
									<template #content>
										<div>
											<span style="font-weight: bold">开发⼈员</span> <br />
											难度系数(轻松：0.5 简单：1 ⼀般：1.5 困难：2 挑战：3) <br />
											权重占⽐（效率20% 难度40% bug率40%） <br />
											平均任务效率(e) = Σ(任务计划⼯时/实际⼯时）/任务数
											（⽐值⼤于异常指数1.5排除该任务）<br />
											功能点平均复杂度(c） =Σ( 功能点*难度系数）/功能点个数 <br />
											bug率(b) = 功能点*难度系数/bug数 （bug率⼩于10%不扣分）<br />
											max代表所有⼈中的最⼤值 <br />
											个⼈得分 = （e/e(max) * 20% + c/c(max) * 40% +b/b(max)*40%）*100<br />
											<span style="font-weight: bold">产品、测试⼈员(暂定)</span> <br />
											难度系数(轻松：0.5 简单：1 ⼀般：1.5 困难：2 挑战：3)<br />
											权重占⽐（效率50% 难度50%） <br />
											平均任务效率(e) = Σ(任务计划⼯时/实际⼯时）/任务数
											（⽐值⼤于异常指数1.5排除该任务）<br />
											功能点平均复杂度(c） =Σ( 功能点*难度系数）/功能点个数 <br />
											max代表所有⼈中的最⼤值 <br />
											个⼈得分 = （e/e(max) * 50% + c/c(max) * 50%）*100<br />
										</div>
									</template>
								</ElTooltip>
								<div class="sort">
									<div class="sort-icon-box">
										<ElIcon
											:style="{ color: isAscending ? '#1B86FF' : 'rgba(0, 0, 0, 0.25)' }"
											@click="toggleSort"
										>
											<CaretTop />
										</ElIcon>
										<ElIcon
											:style="{ color: !isAscending ? '#1B86FF' : 'rgba(0, 0, 0, 0.25)' }"
											@click="toggleSort"
										>
											<CaretBottom />
										</ElIcon>
									</div>
									<span>{{ isAscending ? "正序" : "倒序" }}</span>
								</div>
							</div>
						</div>
						<div class="common-body flex">
							<div class="member-list">
								<div
									class="member-item"
									v-for="(item, index) in memberList"
									:key="index"
									@click="selectMember(item.userId)"
								>
									<ElIcon v-if="selectedMember === item.userId">
										<CircleCheckFilled />
									</ElIcon>
									<!-- v-else -->
									<ElIcon v-else>
										<CircleCheck style="color: rgba(0, 0, 0, 0.15)" />
									</ElIcon>

									<ProjectInfoIcon name="header.png" :width="36" :height="36" />
									<div class="member-info">
										<p>{{ item.name }}</p>
										<span>{{ item.groupName }}</span>
									</div>
									<div class="member-score">
										<ElTooltip class="box-item" effect="dark" placement="top">
											<template #default>
												<span>{{
													item.score !== undefined && item.score !== null
														? (Math.round(item.score * 100) / 100).toString()
														: "0"
												}}</span>
											</template>
											<template #content>
												<div>
													平均任务效率：{{
														item.efficiency !== undefined && item.efficiency !== null
															? (Math.round(item.efficiency * 100) / 100).toString()
															: "0"
													}}
													<br />
													功能点平均复杂度：
													{{
														item.complexity !== undefined && item.complexity !== null
															? (Math.round(item.complexity * 100) / 100).toString()
															: "0"
													}}
													<br />
													<span v-if="activeName == 1 || activeName == 2"
														>bug率：
														{{
															item.bugRate !== undefined && item.bugRate !== null
																? (Math.round(item.bugRate * 100) / 100).toString()
																: "0"
														}}
													</span>
												</div>
											</template>
										</ElTooltip>
									</div>
								</div>
							</div>
						</div>
					</div>

					<!-- 右边-->
					<div class="common-box ml-32px" style="width: calc(100% - 250px - 32px); float: left">
						<div class="common-header">
							<div class="common-title flex flex-items-center">
								<ProjectInfoIcon name="icon-title.png" />
								<span class="mr-6px" style="line-height: 32px">工作数据</span>
								<ElTooltip
									class="box-item"
									effect="dark"
									content="工作数据仅展示此处时间筛选时，有工作的项目数据"
									placement="top"
								>
									<ElIcon
										><QuestionFilled style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
									/></ElIcon>
								</ElTooltip>
							</div>
						</div>
						<div class="common-body">
							<div class="work-list">
								<div class="work-left">
									<ProjectInfoIcon name="dashboard.png" :height="33" :width="33" />
									<span class="mt-12px">工作时长分析</span>
								</div>
								<div class="work-right flex-1 flex-shrink-0">
									<!-- 字段：预估工作时长 任务工作时长 BUG修复工作时长 的表格 -->
									<el-table :data="analysisTable" width="100%" height="100%" stripe>
										<el-table-column prop="planHours" align="center">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">预估工作时长</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内的所有任务的预估工作时长"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<el-table-column
											prop="taskHours"
											align="center"
											v-if="activeName == 1 || activeName == 2 || activeName == 3"
										>
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">任务工作时长</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内的所有任务的实际工作时长"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<el-table-column
											prop="bugHours"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										>
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">BUG修复工作时长</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内，用于修复BUG的实际工作时长"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<!-- 评审准时率 -->
										<el-table-column prop="milestonePassRate" align="center" v-if="activeName == 3">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">评审准时率</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内，所有评审的准时完成率"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<!-- 实际工作时长 -->
										<el-table-column prop="taskHours" align="center" v-if="activeName == 4">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">实际工作时长</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内的所有任务的实际工作时长"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
									</el-table>
								</div>
							</div>
							<!-- 2 -->
							<div class="work-list">
								<div class="work-left">
									<ProjectInfoIcon name="pie-chart-fill.png" :height="33" :width="33" />
									<span class="mt-12px">工作内容分析</span>
								</div>
								<div class="work-right flex-1">
									<!-- 字段：预估工作时长 任务工作时长 BUG修复工作时长 的表格 -->
									<el-table :data="analysisTable" style="width: 100%" height="100%" stripe>
										<el-table-column prop="challengeCount" label="挑战任务数" align="center" />
										<el-table-column prop="difficultCount" label="困难任务数" align="center" />
										<el-table-column prop="normalCount" label="正常任务数" align="center" />
										<el-table-column prop="easyCount" label="简单任务数" align="center" />
										<el-table-column prop="lightCount" label="轻松任务数" align="center" />
										<el-table-column
											prop="challengeFun"
											label="挑战功能数"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<el-table-column
											prop="difficultFun"
											label="困难功能数"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<el-table-column
											prop="normalFun"
											label="正常及以下功能数"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<el-table-column
											prop="activeProjectCount"
											label="活跃项目数"
											align="center"
											v-if="activeName == 3"
										/>
										<el-table-column
											prop="completeFunCount"
											label="完成功能数"
											align="center"
											v-if="activeName == 3"
										/>
										<el-table-column
											prop="activeProjectCount"
											label="测试项目数"
											align="center"
											v-if="activeName == 4"
										/>
										<el-table-column
											prop="completeFunCount"
											label="测试功能数"
											align="center"
											v-if="activeName == 4"
										/>
									</el-table>
								</div>
							</div>
							<!-- 3 -->
							<div class="work-list">
								<div class="work-left">
									<ProjectInfoIcon name="computer-fill.png" :height="33" :width="33" />
									<span class="mt-12px">工作质量分析</span>
								</div>
								<div class="work-right flex-1">
									<!-- 字段：预估工作时长 任务工作时长 BUG修复工作时长 的表格 -->
									<el-table :data="analysisTable" style="width: 100%" height="100%" stripe>
										<el-table-column
											prop="bugRate"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										>
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">BUG率占比</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内,个人BUG总数/个人任务功能点总数"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<el-table-column
											prop="passRate"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										>
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">验收一次通过率</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内,开发和内部验收阶段，内部评审总得分/内部评审总分值"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<el-table-column
											prop="challengeBugRate"
											label="挑战功能BUG率"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<el-table-column
											prop="difficultBugRate"
											label="困难功能BUG率"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<el-table-column
											prop="normalBugRate"
											label="正常及以下功能BUG率"
											align="center"
											v-if="activeName == 1 || activeName == 2"
										/>
										<!-- 产品的 -->
										<el-table-column prop="passRate" align="center" v-if="activeName == 3">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">需求阶段评审一次通过率</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内,需求阶段，内部评审总得分/内部评审总分值"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<!-- <el-table-column
											prop="challengeCount"
											label="需求阶段评审平均分"
											v-if="activeName == 3"
											align="center"
										/> -->
										<el-table-column
											prop="leadBackRate"
											label="领导交办任务打回率"
											v-if="activeName == 3"
											align="center"
										/>
										<el-table-column
											prop="taskScoreAvg"
											label="领导交办任务平均分"
											v-if="activeName == 3"
											align="center"
										/>
										<!-- 测试的 -->
										<el-table-column prop="testBugRate" align="center" v-if="activeName == 4">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">测试报告bug覆盖率</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="测试报告发现的bug数量/（内部评审时发现的bug数量+测试报告发现的bug数量）"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<el-table-column prop="passRate" align="center" v-if="activeName == 4">
											<template #header>
												<div class="flex flex-items-center flex-justify-center">
													<span class="mr-10px">项目验收一次通过率</span>
													<ElTooltip
														class="box-item"
														effect="dark"
														content="选定时间内，内部验收阶段，评审总得分/内部评审总分值"
														placement="top"
													>
														<ElIcon
															><QuestionFilled
																style="
																	color: #7191bd;
																	font-size: 15px;
																	cursor: pointer;
																"
														/></ElIcon>
													</ElTooltip>
												</div>
											</template>
										</el-table-column>
										<!-- <el-table-column
											prop="challengeCount"
											label="内部验收阶段评审平均分"
											v-if="activeName == 4"
											align="center"
										/> -->
										<el-table-column
											prop="testTaskRate"
											label="测试任务按期完成率"
											v-if="activeName == 4"
											align="center"
										/>
									</el-table>
								</div>
							</div>
							<!-- 4 -->
							<div class="work-list">
								<div class="work-left">
									<ProjectInfoIcon name="calendar-fill.png" :height="33" :width="33" />

									<span class="mt-12px" v-if="activeName == 3">管理效能分析</span>
									<span class="mt-12px" v-else>工作时限分析</span>
								</div>
								<div class="work-right flex-1">
									<!-- 字段：预估工作时长 任务工作时长 BUG修复工作时长 的表格 -->
									<el-table :data="analysisTable" style="width: 100%" height="100%" stripe>
										<el-table-column prop="earlyCount" label="提前完成任务数" align="center" />
										<el-table-column prop="onTimeCount" label="准时完成任务数" align="center" />
										<el-table-column prop="delayCount" label="延期完成任务数" align="center" />
										<!-- <el-table-column
											prop="delayCount"
											label="项目按期完成率"
											align="center"
											v-if="activeName == 3"
										/> -->
										<el-table-column
											prop="projectReviewRate"
											label="按实开展项目评审率"
											align="center"
											v-if="activeName == 3"
										/>
									</el-table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</el-tabs>
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
	import ElQuarter from "@/components/quarter/index.vue";

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
	const loading = ref(false);
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
			initGetList();
			sumDate3.value = "";
			sumDate5.value = "";
		}
	};

	// 处理自定义日期范围选择
	const handleDateRangeChange = (dates) => {
		if (dates && dates.length === 2) {
			startDate.value = dates[0];
			endDate.value = dates[1];
			initGetList();
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

	// 初始mounted
	onMounted(() => {
		// const currentMonth = getCurrentMonth();
		// sumDate3.value = currentMonth;
		// handleMonthChange(currentMonth);
		initGetList();
	});

	// 首次加载时的初始化函数
	const initGetList = () => {
		loading.value = true;
		getDeptUserAnalysis({
			type: activeName.value,
			// projectIds: props.projectList.map((item: any) => item.id).join(","),
			startDate: startDate.value,
			endDate: endDate.value
		}).then((res) => {
			console.log(res, "大幅度发的辅导费");
			memberList.value = res;
			selectMember(memberList.value[0].userId);
			loading.value = false;
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

	const initWorkHours = () => {
		// 检查 props.projectList 是否存在且不为空
		// if (!props.projectList || props.projectList.length === 0) {
		// 	console.log("projectIds 为空，不调用 workHoursListData 接口");
		// 	tableData6.value = [];
		// 	return Promise.resolve([]);
		// }

		const projectIds = props.projectList.map((item: any) => item.id);
		return workHoursListData({
			projectIds
		})
			.then((res) => {
				tableData6.value = res;
			})
			.catch((error) => {
				console.error("获取 workHoursListData 数据失败:", error);
			});
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
		height: calc(100vh - 236px);
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
</style>
