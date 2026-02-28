<template>
	<BasePage v-bind="page">
		<template #pageSearch>
			<div class="search-wrap">
				<ElTabs v-model="activeName" @tab-click="handleClick">
					<ElTabPane label="计划任务" name="1">
						<planTaskSearch
							ref="planTaskEl"
							:projectList="options"
							:userList="userList"
							class="m-t-20px"
							@onSearch="onSearch"
							:userId="userId"
							@onReset="onReset"
							@changeUserId="changeUserId"
						></planTaskSearch>
					</ElTabPane>
					<ElTabPane label="问题修复任务" name="2" class="m-t-20px">
						<bugTaskSearch
							ref="bugTaskEl"
							:projectList="options"
							:userList="userList"
							class="m-t-20px"
							:userId="userId"
							@onSearch="onSearch"
							@onReset="onReset"
							@changeUserId="changeUserId"
						></bugTaskSearch>
					</ElTabPane>
					<ElTabPane label="运维任务" name="3" class="m-t-20px">
						<operationTaskSearch
							ref="operationTaskEl"
							:projectList="options"
							:userList="userList"
							:userId="userId"
							class="m-t-20px"
							@onSearch="onSearch"
							@onReset="onReset"
							@changeUserId="changeUserId"
						></operationTaskSearch>
					</ElTabPane>
					<ElTabPane label="领导交办任务" name="4" class="m-t-20px">
						<leaderTaskSearch
							ref="leaderTaskEl"
							:projectList="options"
							:userList="userList"
							:userId="userId"
							class="m-t-20px"
							@onSearch="onSearch"
							@onReset="onReset"
							@changeUserId="changeUserId"
						></leaderTaskSearch>
					</ElTabPane>
					<ElTabPane label="会议任务" name="5" class="m-t-20px">
						<meetingTaskSearch
							ref="meetingTaskEl"
							:projectList="options"
							:userList="userList"
							:userId="userId"
							class="m-t-20px"
							@onSearch="onSearch"
							@onReset="onReset"
							@changeUserId="changeUserId"
						></meetingTaskSearch>
					</ElTabPane>
				</ElTabs>
			</div>
		</template>
		<template #pageBody>
			<ElTable
				ref="multipleTableRef"
				:data="tablelist"
				border
				class="m-t-24px"
				v-loading="loading"
				:row-key="(row) => row.id"
				stripe
				height="calc(100vh - 340px)"
			>
				<ElTableColumn label="序号" width="80" align="center" type="index">
					<template #default="scope">
						{{ (queryForm.pageNo - 1) * queryForm.pageSize + scope.$index + 1 }}
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 1 || activeName == 3 || activeName == 4 || activeName == 5"
					prop="title"
					label="任务标题"
					align="center"
					min-width="160px"
					show-overflow-tooltip
				>
					<template #default="scope">
						<ElLink type="primary" @click="handleJumpDetail(scope.row)">{{ scope.row.title }}</ElLink>
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 2"
					prop="title"
					label="问题标题"
					align="center"
					min-width="160px"
					show-overflow-tooltip
				>
					<template #default="scope">
						<ElLink type="primary" @click="handleJumpDetail(scope.row)">{{ scope.row.bug_title }}</ElLink>
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 5"
					prop="project_name"
					label="关联项目名称"
					align="center"
					min-width="160px"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 5"
					label="关联项目计划/里程碑"
					align="center"
					min-width="160px"
					show-overflow-tooltip
				>
					<template #default="scope">
						{{ scope.row.type == 1 ? scope.row.plan_name : scope.row.milestoneName }}
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 5"
					prop="meet_time"
					label="会议时间"
					width="180"
					align="center"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 5"
					width="100"
					prop="planing_hours"
					label="会议时长"
					align="center"
					show-overflow-tooltip
				>
					<template #default="scope">
						{{ scope.row.planing_hours ? scope.row.planing_hours + "分钟" : 0 }}
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 2"
					prop="featureName"
					label="关联功能点"
					align="center"
					min-width="160px"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					v-if="!(activeName == 4 || activeName == 5)"
					prop="project_name"
					label="所属项目"
					align="center"
					min-width="200px"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 1 || activeName == 3"
					prop="task_attribute_name"
					width="160"
					label="任务属性"
					align="center"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="activeName == 2"
					prop="assigned_to_user_type_name"
					label="问题属性"
					width="160"
					align="center"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="activeName == 4"
					prop="proposer_user_name"
					label="提出人"
					align="center"
					width="100"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="activeName == 4"
					prop="urgencyName"
					label="紧急程度"
					align="center"
					width="120"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="activeName != 5"
					prop="assigned_to_user_name"
					label="负责人"
					align="center"
					width="100"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					label="任务截止日期"
					v-if="activeName != 5"
					width="120px"
					align="center"
					show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.end_time && scope.row.end_time.substring(0, 10) }}
					</template></ElTableColumn
				>
				<ElTableColumn
					prop="workHours"
					label="累计工时"
					align="center"
					width="120"
					show-overflow-tooltip
				>
				<template #default="scope">
						{{ scope.row.workHours }}
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 1 || activeName == 3"
					prop="degreeName"
					label="难易度"
					align="center"
					width="100"
					show-overflow-tooltip
				>
				<template #default="{ row }">
								<span class="degree" v-if="row.degree_of_difficulty == 1"
									><i class="degree-1"></i>轻松</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 2"
									><i class="degree-2"></i>简单</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 3"
									><i class="degree-3"></i>正常</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 4"
									><i class="degree-4"></i>困难</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 5"
									><i class="degree-5"></i>挑战</span
								>
							</template>
				</ElTableColumn>
				<ElTableColumn prop="statusName" label="状态" width="100" show-overflow-tooltip align="center">
					<template #default="scope">
						<ElTag
							disable-transitions
							type="warning"
							v-if="['待指派', '待开始'].includes(scope.row.statusName)"
						>
							{{ scope.row.statusName }}
						</ElTag>
						<ElTag
							disable-transitions
							type="primary"
							v-if="['进行中', '待评分', '待复测'].includes(scope.row.statusName)"
						>
							{{ scope.row.statusName }}
						</ElTag>
						<ElTag
							disable-transitions
							type="success"
							v-if="['已完成', '已评分', '已解决'].includes(scope.row.statusName)"
						>
							{{ scope.row.statusName }}
						</ElTag>
						<ElTag
							disable-transitions
							type="danger"
							v-if="['被打回', '已挂起'].includes(scope.row.statusName)"
						>
							{{ scope.row.statusName }}
						</ElTag>
						<ElTag
							disable-transitions
							type="info"
							v-if="['已暂停', '已终止', '已结束'].includes(scope.row.statusName)"
						>
							{{ scope.row.statusName }}
						</ElTag>
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeName == 5"
					prop="userNames"
					label="参会人"
					align="center"
					min-width="200"
					show-overflow-tooltip
				>
				</ElTableColumn>
				<ElTableColumn
					label="开始日期"
					v-if="activeName != 5"
					width="180px"
					align="center"
					show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.start_time }}
					</template></ElTableColumn
				>
				<ElTableColumn
					label="完成日期"
					v-if="activeName != 5"
					width="180px"
					align="center"
					show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.completion_time }}
					</template></ElTableColumn
				>
				<ElTableColumn v-if="activeName == 4" label="评分" width="100" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.task_rating }}
					</template></ElTableColumn
				>
				<ElTableColumn
					v-if="activeName == 4 || activeName == 5"
					label="创建人"
					align="center"
					width="100"
					show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.createUserName }}
					</template></ElTableColumn
				>
				<ElTableColumn
					v-if="activeName == 1 || activeName == 3"
					label="优先级"
					width="100"
					align="center"
					show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.priorityName }}
					</template></ElTableColumn
				>
				<ElTableColumn v-if="activeName == 2" label="严重程度" width="96" align="center"
					><template #default="scope">
						<div v-if="scope.row.severity == 1" class="severity icon-bug"><span></span><i>轻微</i></div>
						<div v-if="scope.row.severity == 2" class="severity icon-bug1"><span></span><i>一般</i></div>
						<div v-if="scope.row.severity == 3" class="severity icon-bug2"><span></span><i>严重</i></div>
					</template></ElTableColumn
				>
			</ElTable>
			<ElPagination
				style="display: flex; justify-content: center; margin-top: 16px"
				background
				:current-page="queryForm.pageNo"
				:page-size="queryForm.pageSize"
				layout="total, sizes, prev, pager, next, jumper"
				@size-change="handleSizeChange"
				@current-change="pageCurrentChange"
				:total="total"
			></ElPagination>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
import { createDrawerAsync, createModelAsync } from "@/core/dialog";
import { type FormConfig } from "@/core/struct/form/use-base-form";
import { usePage } from "@/core/struct/page";
import BasePage from "@/core/struct/page/base-page";
import AddOperationTask from "./dialog/add-operation-task.vue";
import DesignateTask from "./dialog/designate-task.vue";
import { markRaw, onMounted, reactive } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { ElMessageBox, type Action } from "element-plus";
import { getHistoryPageList } from "@/api/task/history-task";
import { selectPageList } from "@/api/project/list";
import { getUserList } from "@/api/common/login";
import { appInstance } from "@/main";
import { ref } from "vue";
import DetailPage from "../task-detail/index.vue";
import planTaskSearch from "./components/planTaskSearch.vue";
import bugTaskSearch from "./components/bugTaskSearch.vue";
import leaderTaskSearch from "./components/leaderTaskSearch.vue";
import meetingTaskSearch from "./components/meetingTaskSearch.vue";
import operationTaskSearch from "./components/operationTaskSearch.vue";
import { useUserStore } from "@/store/user.ts";

const userStore = useUserStore();

// 选择任务类型
const activeName = ref("1");

// 获得项目列表
const options = ref();
selectPageList({
	pageNo: 1,
	pageSize: 100
}).then((res) => {
	options.value = res.records;
	console.log("222222222222options.value", options.value);
});

// 改变所选人
const userId = ref(appInstance.userInfo?.id);
const changeUserId = (val) => {
	userId.value = val;
};

// 查询项目所有人
const userList = ref();
getUserList({ pageNo: 1, pageSize: 100 }).then((res) => {
	userList.value = res.records;
});

const { page, pageApi } = usePage({
	title: "历史任务列表",
	listApi: async () => {}
});

// 查询条件
const queryForm = ref({
	pageNo: 1,
	pageSize: 20
});

const tablelist = ref([]);
const loading = ref(false);
const total = ref(0);
// 计划查询组件
const planTaskEl = ref();
const oldParams = ref();
const onSearch = async (data) => {
	console.log("77777777777777data", data);
	oldParams.value = data ? data : oldParams.value;
	const params = {
		...queryForm.value,
		...oldParams.value,
		task_type: activeName.value
	};
	loading.value = true;
	try {
		const res = await getHistoryPageList(params);

		tablelist.value = res.records;
		total.value = res.total;
	} finally {
		loading.value = false;
	}
};
onMounted(() => {
	planTaskEl.value.onSearch();
});

// onSearch();
const onReset = (val) => {
	userId.value = appInstance.userInfo?.id
	onSearch({...val, assigned_to_user_id: userId.value });
};
const bugTaskEl = ref();
const operationTaskEl = ref();
const leaderTaskEl = ref();
const meetingTaskEl = ref();
// 改变任务类型
const handleClick = ({ index }) => {
	console.log("99999999999type", index);
	queryForm.value.pageNo = 1;
	if (index == 0) {
		planTaskEl.value.onSearch();
	} else if (index == 1) {
		bugTaskEl.value.onSearch();
	} else if (index == 2) {
		operationTaskEl.value.onSearch();
	} else if (index == 3) {
		leaderTaskEl.value.onSearch();
	} else if (index == 4) {
		meetingTaskEl.value.onSearch();
	}
};

// 切换页面显示个数操作
const handleSizeChange = (val) => {
	queryForm.value.pageNo = 1;
	queryForm.value.pageSize = val;
	onSearch();
};
// 切换页面页码
const pageCurrentChange = (val) => {
	queryForm.value.pageNo = val;
	onSearch();
};

// 跳转到详情
const handleJumpDetail = (row: any) => {
	createDrawerAsync(
		{ title: row.bug_title || row.title, width: "1100px", showNext: false, showConfirm: false },
		{},
		<DetailPage type={activeName.value} id={row.id} />
	);
};
</script>
<style lang="scss" scoped>
:deep(.el-popper) {
	max-width: 500px !important; /* 设置最大宽度 */
	line-height: 1.5 !important; /* 可选：调整行高 */
}

::v-deep {
	.el-tabs__item {
		height: 56px;
		padding: 0 26px;
	}

	.el-tabs--top > .el-tabs__header .el-tabs__item:nth-child(2) {
		padding-left: 26px;
	}
}

.severity {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 64px;
	height: 24px;
	background: #f0f9eb;
	border-radius: 4px 4px 4px 4px;
	border: 1px solid #85ce61;
	font-size: 14px;
	color: #67c23a;
	line-height: 22px;
	span {
		margin-right: 4px;
		display: inline-block;
		width: 14px;
		height: 14px;
		background: url("@/assets/bug/bug-fill.png") no-repeat center;
		background-size: cover;
	}
	i {
		font-style: normal;
		font-size: 14px;
	}
	&.icon-bug1 {
		color: #f5a250;
		background: #fef6ed;
		border: 1px solid #f7b573;
		span {
			background: url("@/assets/bug/bug-fill-2.png") no-repeat center;
			background-size: cover;
		}
	}
	&.icon-bug2 {
		color: #f56c6c;
		background: #fef0f0;
		border: 1px solid #f78989;
		span {
			background: url("@/assets/bug/bug-fill-1.png") no-repeat center;
			background-size: cover;
		}
	}
}

.search-wrap {
	background: var(--el-page-color);
}
.degree {
	display: inline-block;
	width: 62px;
	height: 26px;
	font-size: 14px !important;
	color: rgba(0, 0, 0, 0.65);
	line-height: 26px;
	border-radius: 4px 4px 4px 4px;
	border: 1px solid rgba(0, 0, 0, 0.06);
	box-sizing: border-box;
	text-align: center;
	i {
		display: inline-block;
		margin-right: 6px;
		width: 10px;
		height: 10px;
		border-radius: 50%;
		&.degree-1 {
			background: #409eff;
		}
		&.degree-2 {
			background: #40bfa8;
		}
		&.degree-3 {
			background: #67c23a;
		}
		&.degree-4 {
			background: #ff9d00;
		}
		&.degree-5 {
			background: #f56c6c;
		}
	}
}
</style>
