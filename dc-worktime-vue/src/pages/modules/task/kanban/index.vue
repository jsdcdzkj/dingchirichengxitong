<template>
	<BasePage>
		<template #pageSearch>
			<div style="
					background: var(--el-page-color);
					border-radius: 8px 8px 8px 8px;
					border: 1px solid rgba(0, 0, 0, 0.06);
					padding: 20px;
				">
				<ElForm :model="form" labelWidth="100px" ref="formSearch">
					<ElRow :gutter="10">
						<ElCol :span="7">
							<ElFormItem label="项目名称">
								<ElSelect v-model="form.project_id" placeholder="请选择" clearable filterable>
									<ElOption v-for="item in options" :key="item.id" :label="item.projectName"
										:value="item.id"><span style="float: left">{{ item.projectName }}</span>
										<span style="
												float: right;
												background: #f56c6c;
												color: #fff;
												border-radius: 50%;
												font-size: 13px;
												display: inline-block;
												line-height: 20px;
												padding: 0 6px;
												margin-top: 8px;
											">
											{{ item.taskCount }}
										</span>
									</ElOption>
								</ElSelect>
							</ElFormItem>
						</ElCol>
						<ElCol span="4">
							<ElButton :icon="Search" type="primary" @click="onSearch"> 搜索 </ElButton>
							<ElButton :icon="Refresh" @click="onReset"> 重置 </ElButton>
						</ElCol>
					</ElRow>
				</ElForm>
			</div>
		</template>
		<template #pageBody>
			<div style="position: relative">
				<span class="tip">共 {{ totalNum }} 个工作项</span>
				<ElTabs v-model="activeName" @tab-click="handleClick">
					<ElTabPane name="taskInfo">
						<template #label>
							<span class="custom-tabs-label">
								计划任务
								<span class="tag">{{
									taskCount.taskInfoDbCount - 99 > 0 ? "99+" : taskCount.taskInfoDbCount
								}}</span>
							</span>
						</template>
						<div class="flex task-card" v-loading="loading">
							<div class="flex-1 card-item">
								<div class="title">未开始（{{ taskAllList.notStartedTaskList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.notStartedTaskList"
										:key="index" :item="item" taskType="1" taskStatus="1"
										@click="handleView(1, item.id, item)">
										<template #operation>
											<ElButton type="primary" size="small" @click.stop="handleMove(1, item)">
												<img src="@/assets/task/icon-zhuanyi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												转移
											</ElButton>
											<ElButton v-if="item.status == 2 || item.status == 6" type="warning"
												size="small" @click.stop="handleStart(1, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												开始
											</ElButton>
											<ElButton type="success" v-if="item.status == 5" size="small"
												@click.stop="handleRestore(1, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												恢复
											</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">进行中（{{ taskAllList.inProgressTaskList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.inProgressTaskList"
										:key="index" :item="item" taskType="1" taskStatus="2"
										@click="handleView(1, item.id, item)">
										<template #tip>
											<span class="status">进行中</span>
										</template>
										<template #operation>
											<ElButton :icon="ShoppingBag" size="small"
												@click.stop="handlePending(1, item)">挂起</ElButton>
											<ElButton type="warning" size="small" @click.stop="handleStop(1, item)">
												<img src="@/assets/task/icon-zanting.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												暂停
											</ElButton>
											<ElButton type="success" size="small" :icon="CircleCheckFilled"
												@click.stop="handleFinish(1, item)">完成</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									（近一个月）已完成（{{ taskAllList?.completedTaskList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.completedTaskList"
										:key="index" :item="item" taskType="1" @click="handleView(1, item.id, item)">
										<template #tip>
											<span class="status">已完成</span>
										</template>
									</TaskCard>
								</div>
							</div>
						</div>
					</ElTabPane>
					<ElTabPane name="testBug">
						<template #label>
							<span class="custom-tabs-label">
								问题修复任务
								<span class="tag">{{
									taskCount.testBugDbCount - 99 > 0 ? "99+" : taskCount.testBugDbCount
								}}</span>
							</span>
						</template>
						<div class="flex task-card">
							<div class="flex-1 card-item">
								<div class="title">未开始（{{ taskAllList.notStartedTestBugList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.notStartedTestBugList"
										:key="index" :item="item" taskType="2" taskStatus="1"
										@click="handleView(2, item.id, item)">
										<template #tip>
											<span class="status">待开始</span>
										</template>
										<template #operation>
											<ElButton type="primary" size="small" @click.stop="handleMove(2, item)">
												<img src="@/assets/task/icon-zhuanyi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												转移
											</ElButton>
											<ElButton v-if="item.status == 2 || item.status == 7" type="warning"
												size="small" @click.stop="handleStart(2, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												开始
											</ElButton>
											<ElButton type="success" v-if="item.status == 6" size="small"
												@click.stop="handleRestore(2, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												恢复
											</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">进行中（{{ taskAllList.inProgressTestBugList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.inProgressTestBugList"
										:key="index" :item="item" taskType="2" taskStatus="2"
										@click="handleView(2, item.id, item)">
										<template #tip>
											<span class="status">进行中</span>
										</template>
										<template #operation>
											<ElButton :icon="ShoppingBag" size="small"
												@click.stop="handlePending(2, item)">挂起</ElButton>
											<ElButton type="warning" size="small" @click.stop="handleStop(2, item)">
												<img src="@/assets/task/icon-zanting.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												暂停
											</ElButton>
											<ElButton type="success" size="small" :icon="CircleCheckFilled"
												@click.stop="handleFinish(2, item)">完成</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									（近一个月）已完成（{{ taskAllList.completedTestBugList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.completedTestBugList"
										:key="index" :item="item" taskType="2" @click="handleView(2, item.id, item)">
										<template #tip>
											<span class="status">已完成</span>
										</template>
									</TaskCard>
								</div>
							</div>
						</div>
					</ElTabPane>
					<ElTabPane name="taskOperationInfo">
						<template #label>
							<span class="custom-tabs-label">
								运维任务
								<span class="tag">{{
									taskCount.taskOperationInfoDbCount - 99 > 0
										? "99+"
										: taskCount.taskOperationInfoDbCount
								}}</span>
							</span>
						</template>
						<div class="flex task-card">
							<div class="flex-1 card-item">
								<div class="title">
									未开始（{{ taskAllList.notStartedOperationList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el"
										v-for="(item, index) in taskAllList.notStartedOperationList" :key="index"
										:item="item" taskType="3" taskStatus="1" @click="handleView(3, item.id, item)">
										<template #tip>
											<span class="status">待开始</span>
										</template>
										<template #operation>
											<ElButton type="primary" size="small" @click.stop="handleMove(3, item)">
												<img src="@/assets/task/icon-zhuanyi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												转移
											</ElButton>
											<ElButton v-if="item.status == 2 || item.status == 6" type="warning"
												size="small" @click.stop="handleStart(3, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												开始
											</ElButton>
											<ElButton type="success" v-if="item.status == 5" size="small"
												@click.stop="handleRestore(3, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												恢复
											</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									进行中（{{ taskAllList.inProgressOperationList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el"
										v-for="(item, index) in taskAllList.inProgressOperationList" :key="index"
										:item="item" taskType="3" taskStatus="2" @click="handleView(3, item.id, item)">
										<template #tip>
											<span class="status">进行中</span>
										</template>
										<template #operation>
											<ElButton :icon="ShoppingBag" size="small"
												@click.stop="handlePending(3, item)">挂起</ElButton>
											<ElButton type="warning" size="small" @click.stop="handleStop(3, item)">
												<img src="@/assets/task/icon-zanting.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												暂停
											</ElButton>
											<ElButton type="success" size="small" :icon="CircleCheckFilled"
												@click.stop="handleFinish(3, item)">完成</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									（近一个月）已完成（{{ taskAllList.completedOperationList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el"
										v-for="(item, index) in taskAllList.completedOperationList" :key="index"
										:item="item" taskType="3" @click="handleView(3, item.id, item)">
										<template #tip>
											<span class="status">已完成</span>
										</template>
									</TaskCard>
								</div>
							</div>
						</div>
					</ElTabPane>
					<ElTabPane name="taskLeadersInfo">
						<template #label>
							<span class="custom-tabs-label">
								领导交办任务
								<span class="tag">{{
									taskCount.taskLeadersInfoDbCount - 99 > 0 ? "99+" : taskCount.taskLeadersInfoDbCount
								}}</span>
							</span>
						</template>
						<div class="flex task-card">
							<div class="flex-1 card-item">
								<div class="title">未开始（{{ taskAllList.notStartedLeadersList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.notStartedLeadersList"
										:key="index" :item="item" taskType="4" taskStatus="1"
										@click="handleView(4, item.id, item)">
										<template #tip>
											<span class="status">待开始</span>
										</template>
										<template #operation>
											<ElButton type="primary" size="small" @click.stop="handleMove(4, item)">
												<img src="@/assets/task/icon-zhuanyi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												转移
											</ElButton>
											<ElButton v-if="item.status == 2 || item.status == 9 || item.status == 5"
												type="warning" size="small" @click.stop="handleStart(4, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												开始
											</ElButton>
											<ElButton type="success" v-if="item.status == 8" :icon="Stopwatch"
												size="small" @click.stop="handleRestore(4, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												恢复
											</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">进行中（{{ taskAllList.inProgressLeadersList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.inProgressLeadersList"
										:key="index" :item="item" taskType="4" taskStatus="2"
										@click="handleView(4, item.id, item)">
										<template #tip>
											<span class="status">进行中</span>
										</template>
										<template #operation>
											<ElButton :icon="ShoppingBag" size="small"
												@click.stop="handlePending(4, item)">挂起</ElButton>
											<ElButton :icon="Upload" size="small" type="primary"
												@click.stop="handleResult(4, item)">提交阶段成果</ElButton>
											<ElButton type="warning" size="small" @click.stop="handleStop(4, item)">
												<img src="@/assets/task/icon-zanting.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												暂停
											</ElButton>
											<ElButton type="success" size="small" :icon="CircleCheckFilled"
												@click.stop="handleFinish(4, item)">完成</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									（近一个月）已完成（{{ taskAllList.completedLeadersList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.completedLeadersList"
										:key="index" :item="item" taskType="4" @click="handleView(4, item.id, item)">
										<template #tip>
											<span class="status">已完成</span>
										</template>
									</TaskCard>
								</div>
							</div>
						</div>
					</ElTabPane>
					<ElTabPane name="taskMeet">
						<template #label>
							<span class="custom-tabs-label">
								会议任务
								<span class="tag">{{
									taskCount.taskMeetDbCount - 99 > 0 ? "99+" : taskCount.taskMeetDbCount
								}}</span>
							</span>
						</template>
						<div class="flex task-card">
							<div class="flex-1 card-item">
								<div class="title">未开始（{{ taskAllList.notStartedMeetList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.notStartedMeetList"
										:key="index" :item="item" taskType="5" taskStatus="1"
										@click="handleView(5, item.id, item)">
										<template #tip>
											<span class="status">待开始</span>
										</template>
										<template #operation>
											<ElButton type="danger" size="small" v-if="item.status == 0"
												@click.stop="handleNoJoin(5, item)">
												<img src="@/assets/task/no-join.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												不参加
											</ElButton>
											<ElButton v-if="item.status == 0 || item.status == 4" type="warning"
												size="small" @click.stop="handleStart(5, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												开始
											</ElButton>
											<ElButton type="success" v-if="item.status == 3" size="small"
												@click.stop="handleRestore(5, item)">
												<img src="@/assets/task/icon-kaishi.png"
													style="width: 14px; height: 14px; margin-right: 4px" />
												恢复
											</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">进行中（{{ taskAllList.inProgressMeetList?.length || 0 }}）</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.inProgressMeetList"
										:key="index" :item="item" taskType="5" taskStatus="2"
										@click="handleView(5, item.id, item)">
										<template #tip>
											<span class="status">进行中</span>
										</template>
										<template #operation>
											<ElButton :icon="ShoppingBag" size="small"
												@click.stop="handlePending(5, item)">挂起</ElButton>
											<ElButton type="success" size="small" :icon="CircleCheckFilled"
												@click.stop="handleFinish(5, item)">完成</ElButton>
										</template>
									</TaskCard>
								</div>
							</div>
							<div class="flex-1 card-item">
								<div class="title">
									（近一个月）已完成（{{ taskAllList.completedMeetList?.length || 0 }}）
								</div>
								<div class="card-content">
									<TaskCard class="card-el" v-for="(item, index) in taskAllList.completedMeetList"
										:key="index" :item="item" taskType="5" @click="handleView(5, item.id, item)">
										<template #tip>
											<span class="status">已完成</span>
										</template>
									</TaskCard>
								</div>
							</div>
						</div>
					</ElTabPane>
				</ElTabs>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang='tsx'>
import { createDrawerAsync, createModelAsync } from "@/core/dialog";
import { Stopwatch, CircleCheckFilled, ShoppingBag, Upload, Search, Refresh } from "@element-plus/icons-vue";
import { type FormConfig } from "@/core/struct/form/use-base-form";
import { usePage } from "@/core/struct/page";
import BasePage from "@/core/struct/page/base-page";
import Detail from "./dialog/detail.vue";
import PendingTask from "./dialog/pending-task.vue";
import FinishBug from "./dialog/finish-bug.vue";
import MoveTask from "./dialog/move-task.vue";
import MoveBugTask from "./dialog/move-bug-task.vue";
import FinishTask from "./dialog/finish-task.vue";
import FinishTest from "./dialog/Finish-test.vue";
import FinishInterfaceTask from "./dialog/finish-interface-task.vue"; // 后台任务完成
import CommitAchievements from "./dialog/commit-achievements.vue"; // 提交阶段性成果
import { markRaw, onMounted, ref, nextTick } from "vue";
import { ElMessageBox, type Action } from "element-plus";
import {
	getList,
	getGroupUserList,
	taskDashboard,
	taskDashboardList,
	completeTaskLeader,
	completeTaskMeeting,
	delEntityById,
	completeTaskOperation,
	startTask,
	restoreTask,
	startTaskBug,
	startTaskLeader,
	startTaskMeeting,
	startTaskOperation,
	restoreTaskBug,
	restoreTaskLeader,
	restoreTaskMeet,
	restoreTaskOperation,
	completeTask,
	computeWorkingMinByNow,
	getUnfinishedTaskNumList,
	stopTaskOperation,
	stopTaskLeader,
	stopTaskBug,
	stopTask
} from "@/api/task/kanban";
import TaskCard from "./components/task-card.vue";
import { appInstance } from "@/main";
import DetailPage from "../task-detail/index.vue";
import { useUserStore } from "@/store/user.ts";

const userStore = useUserStore();

const loading = ref(false);
const taskCount = ref({
	taskInfoDbCount: 0,
	taskLeadersInfoDbCount: 0,
	taskOperationInfoDbCount: 0,
	testBugDbCount: 0,
	taskMeetDbCount: 0,
});
const taskAllList = ref({
	completedLeadersList: [],
	completedOperationList: [],
	completedTaskList: [],
	completedTestBugList: [],
	completedMeetList: [],
	inProgressLeadersList: [],
	inProgressOperationList: [],
	inProgressTaskList: [],
	inProgressTestBugList: [],
	inProgressMeetList: [],
	notStartedLeadersList: [],
	notStartedOperationList: [],
	notStartedTaskList: [],
	notStartedTestBugList: [],
	notStartedMeetList: []
});

const form = ref({});
// 切换tab
const totalNum = ref(0);
// 内容区域
const activeName = ref("taskInfo");
const onSearch = async () => {
	const pList = await getUnfinishedTaskNumList({
		pageNo: 1,
		pageSize: 100
	});
	options.value = pList.filter(el => el.taskCount > 0);
	loading.value = true;
	const activeIndex =
		activeName.value == "taskInfo"
			? "1"
			: activeName.value == "testBug"
				? "2"
				: activeName.value == "taskOperationInfo"
					? "3"
					: activeName.value == "taskLeadersInfo"
						? "4"
						: "5";
	try {
		taskCount.value = await taskDashboard({ ...form.value, assigned_to_user_id: userStore.userInfo.sysUser.id });
		taskAllList.value = await taskDashboardList({
			...form.value,
			assigned_to_user_id: userStore.userInfo.sysUser.id,
			taskType: activeIndex
		});
		loading.value = false;
		totalNum.value = taskCount.value[`${activeName.value}Count`];
		return;
	} catch {
		loading.value = false;
	}
};
const onReset = async () => {
	form.value = {};
	await onSearch(form.value);
};

const handleClick = (tab: any) => {
	onSearch();
};

const options = ref([]);
const grounpList = ref([]);
const assignedList = ref([]);
onMounted(async () => {
	grounpList.value = await getList({
		pageNo: 1,
		pageSize: 20
	});
	onSearch();
});

// 改变开发组
// const handleGrounp = async (val) => {
// 	console.log("11111111111val", val);
// 	form.value.assigned_to_user_id = "";
// 	assignedList.value = await getGroupUserList({
// 		groupId: val
// 	});
// };

//开始计算工时
const handleStart = (index: any, row: any) => {
	ElMessageBox.alert("任务开始后将进行计算和统计工时，是否确认操作？", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				if (index == 1) {
					startTask(row.id).then(() => {
						onSearch();
					});
				} else if (index == 2) {
					startTaskBug(row.id).then(() => {
						onSearch();
					});
				} else if (index == 3) {
					startTaskOperation(row.id).then(() => {
						onSearch();
					});
				} else if (index == 4) {
					startTaskLeader(row.id).then(() => {
						onSearch();
					});
				} else {
					startTaskMeeting(row.id).then(() => {
						onSearch();
					});
				}
			}
		}
	});
};
// 恢复计算工时
const handleRestore = (index: any, row: any) => {
	ElMessageBox.alert("任务恢复后将继续计算和统计工时，是否确认操作？", "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				if (index == 1) {
					restoreTask(row.id).then(() => {
						onSearch();
					});
				} else if (index == 2) {
					restoreTaskBug(row.id).then(() => {
						onSearch();
					});
				} else if (index == 3) {
					restoreTaskOperation(row.id).then(() => {
						onSearch();
					});
				} else if (index == 4) {
					restoreTaskLeader(row.id).then(() => {
						onSearch();
					});
				} else {
					restoreTaskMeet(row.id).then(() => {
						onSearch();
					});
				}
			}
		}
	});
};

// 提交阶段性成果
const handleResult = (index: any, row: any) => {
	createModelAsync(
		{ title: "提交阶段成果", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
		{},
		<CommitAchievements index={index} row={row} />
	).then(() => {
		onSearch();
	});
};

// 挂起任务
const handlePending = (index: any, row: any) => {
	createDrawerAsync(
		{ title: "挂起", width: "800px", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
		{},
		<PendingTask index={index} row={row} />
	).then(() => {
		onSearch();
	});
};

// 任务暂停
const handleStop = (index: any, row: any) => {
	ElMessageBox.alert(`是否暂停该任务？`, "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				if (index == 1) {
					stopTask(row.id).then(() => {
						onSearch();
					});
				} else if (index == 2) {
					stopTaskBug(row.id).then(() => {
						onSearch();
					});
				} else if (index == 3) {
					stopTaskOperation(row.id).then(() => {
						onSearch();
					});
				} else {
					stopTaskLeader(row.id).then(() => {
						onSearch();
					});
				}
			}
		}
	});
};

//计划完成
const handleFinish = async (index: any, row: any) => {
	const time = await computeWorkingMinByNow({ type: index, task_id: row.id });
	// 根据前端，后端任务来展示组件
	const templateDialog =
		row.task_attribute == 2 ? <FinishInterfaceTask types={index} row={row} /> : <FinishTask row={row} />;
	if (index == 1) {
		if (row.task_attribute == 2) {
			ElMessageBox.confirm("是否需要验证接口?", "提示信息", {
				distinguishCancelAndClose: true,
				confirmButtonText: "需要",
				cancelButtonText: "直接提交"
			})
				.then(() => {
					createModelAsync(
						{
							title: "完成",
							width: "800px",
							showNext: false,
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						templateDialog
					).then(() => {
						onSearch();
					});
				})
				.catch((action: Action) => {
					if (action === "cancel") {
						// 直接提交验证结果
						completeTask({ id: row.id }).then(() => {
							onSearch();
						});
					}
				});
		} else if (row.task_attribute == 4) {
			createModelAsync(
				{ title: "完成", width: "800px", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
				{},
				<FinishTest row={row} />
			).then(() => {
				onSearch();
			});
		} else {
			createModelAsync(
				{ title: "完成", width: "800px", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
				{},
				templateDialog
			).then(() => {
				onSearch();
			});
		}
	} else if (index == 2) {
		createModelAsync(
			{ title: "完成", width: "800px", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
			{},
			<FinishBug id={row.id} time={time} />
		).then(() => {
			onSearch();
		});
	} else if (index == 3) {
		ElMessageBox.alert(`任务完成后将封存本任务工时统计，本任务工时${time}。是否确认操作？`, "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					completeTaskOperation({ id: row.id }).then(() => {
						onSearch();
					});
				}
			}
		});
	} else if (index == 4) {
		ElMessageBox.alert(`任务完成后将封存本任务工时统计，本任务工时${time}。是否确认操作？`, "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					completeTaskLeader({ id: row.id }).then(() => {
						onSearch();
					});
				}
			}
		});
	} else {
		ElMessageBox.alert(`任务完成后将封存本任务工时统计，本任务工时${time}。是否确认操作？`, "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					completeTaskMeeting({ id: row.id }).then(() => {
						onSearch();
					});
				}
			}
		});
	}
};

//转移任务
const handleMove = (index, row) => {
	const template = index == 2 ? <MoveBugTask row={row} index={index} /> : <MoveTask row={row} index={index} />;
	createModelAsync(
		{ title: "任务转移", width: "800px", showNext: false, closeOnClickModal: false, closeOnPressEscape: false },
		{},
		template
	).then(() => {
		onSearch();
	});
};

// 不参加任务
const handleNoJoin = (index, row) => {
	ElMessageBox.alert(`将从列表中删除该条任务，是否确认操作？`, "提示", {
		confirmButtonText: "确认",
		callback: (action: Action) => {
			if (action == "confirm") {
				delEntityById(row.id).then(() => {
					onSearch();
				});
			}
		}
	});
}

const handleView = (index: any, id: any, row) => {
	createDrawerAsync(
		{ title: row.title || row.bug_title || row.name, width: "1200px", showNext: false, showConfirm: false },
		{refreshSearch: onSearch},
		<DetailPage type={index} id={id} iskanban={true} />
	);
};
</script>
<style lang="scss" scoped>
.tip {
	position: absolute;
	right: 10px;
	top: 4px;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	line-height: 22px;
}

.task-card {
	border: 1px solid rgba(0, 0, 0, 0.06);
	border-radius: 8px;
	margin: 24px;
	flex-grow: 1;

	.card-item {
		height: calc(100vh - 298px);
		max-width: 33.4%;
		background: #ffffff;
		border-radius: 0px 0px 0px 0px;
		border-right: 1px solid rgba(0, 0, 0, 0.06);
		flex-grow: 1;
	}

	.title {
		height: 48px;
		line-height: 48px;
		background: #f8f8f8;
		color: rgba(0, 0, 0, 0.85);
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
		border-right: 1px solid rgba(0, 0, 0, 0.06);
	}

	.card-content {
		padding: 16px;
		height: calc(100% - 48px);
		overflow: auto;

		.card-el {
			margin-bottom: 8px;
		}
	}
}

.custom-tabs-label {
	display: flex;
	align-items: center;
	padding: 0 24px;

	.tag {
		padding: 0 5px;
		height: 20px;
		display: flex;
		align-items: center;
		text-align: center;
		font-size: 14px;
		color: #fff;
		background: #f56c6c;
		border-radius: 27px 27px 27px 27px;
		border: 1px solid #ffffff;
		margin-left: 4px;
	}
}

::v-deep {
	.el-tag {
		padding: 0 8px;
	}

	.el-tabs__item {
		padding: 0;
	}
}
</style>
