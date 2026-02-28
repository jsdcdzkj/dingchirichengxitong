<template>
	<div class="tabPage flex flex-col">
		<div class="title">工作安排与跟踪日历</div>
		<div class="flex-1 flex overflow-hidden w-100%">
			<div class="left flex flex-col overflow-hidden">
				<div class="label flex">
					<img src="@/assets/icon_label.png" alt="" />
					<span>日历</span>
				</div>
				<div class="always-visible-picker">
					<!-- 隐藏输入框，仅显示日期面板 -->
					<el-date-picker v-model="chooseDate" type="week" value-format="x" placeholder="选择日期"
						:teleported="false" readonly :disabled-date="disabledDate"></el-date-picker>
				</div>
				<div class="label flex" style="margin-top: 310px">
					<img src="@/assets/icon_label.png" alt="" />
					<span>开发组成员筛选</span>
				</div>
				<div class="flex-1 item_box">
					<div class="item flex" @click="setUserId('')">
						<img src="@/assets/header.png" alt="" />
						<div class="flex-1 overflow-hidden">
							<div class="name">全部</div>
							<div class="position">全部开发组成员</div>
						</div>
						<div class="select" :class="{ active: !groupUserId }">
							<el-icon size="16" color="#1B86FF" v-if="!groupUserId">
								<CircleCheckFilled />
							</el-icon>
						</div>
					</div>
					<div class="item flex" :class="`people${index}`" v-for="(el, index) in groupInfo.sysGroupUser"
						:key="index" @click="setUserId(el.userId)">
						<img src="@/assets/header.png" alt="" />
						<div class="flex-1 overflow-hidden">
							<div class="name">{{ el.userName }}</div>
							<div class="position">{{ groupInfo.name }}</div>
						</div>
						<div class="select" :class="{ active: groupUserId == el.userId }">
							<el-icon size="16" color="#1B86FF" v-if="groupUserId == el.userId">
								<CircleCheckFilled />
							</el-icon>
						</div>
					</div>
				</div>
			</div>
			<div class="right flex-1 flex overflow-hidden flex-col">
				<div class="dateInfo flex">
					<el-icon :color="isNowWeek ? '#ddd' : 'rgba(0, 0, 0, 0.45)'" @click="subWeek">
						<CaretLeft />
					</el-icon>
					<span>{{ getWeekInfo(chooseDate) }}</span>
					<el-icon color="rgba(0, 0, 0, 0.45)" @click="addWeek">
						<CaretRight />
					</el-icon>
				</div>
				<div class="flex-1 overflow-hidden w-100%">
					<el-table :data="list" height="100%" border :span-method="arraySpanMethod">
						<el-table-column prop="projectName" label="项目" align="center" />
						<el-table-column :label="el.label" v-for="el in tableDateList" :key="el.date" align="center"
							min-width="160">
							<template #default="{ row }">
								<div style="padding-bottom: 4px" v-if="row.start_time == el.date">
									<div class="people_box" :class="'people' + findPeopleIndex(row.user_id)"
										@click="showTaskList(row.user_id)">
										<div class="header flex">
											<div class="flex">
												<img src="@/assets/header.png" alt="" />
												<span>{{ row.realName }}</span>
											</div>
											<span v-if="row.completion_start_time == row.completion_end_time">{{
												row.completion_start_time.substr(-5)
											}}</span>
											<span v-else>{{ row.completion_start_time.substr(-5) }} 至
												{{ row.completion_end_time.substr(-5) }}</span>
										</div>
										<div class="tags flex">
											<div class="tag" v-if="row.countLeaders && row.countLeaders > 0">
												领导交办*{{ row.countLeaders }}
											</div>
											<div class="tag" v-if="row.countTask && row.countTask > 0">
												计划任务*{{ row.countTask }}
											</div>
											<div class="tag" v-if="row.countOperation && row.countOperation > 0">
												运维*{{ row.countOperation }}
											</div>
											<div class="tag" v-if="row.countTestBug && row.countTestBug > 0">
												问题修复任务*{{ row.countTestBug }}
											</div>
											<div class="tag" v-if="row.countMeet && row.countMeet > 0">
												会议任务*{{ row.countMeet }}
											</div>
										</div>
										<div class="footer flex">
											<div class="flex">
												<el-icon color="rgba(0, 0, 0, 0.65)" :size="12"
													style="margin-right: 4px">
													<Clock />
												</el-icon>
												<span>预估工时：{{ row.planDays }}</span>
											</div>
											<div class="info_tag">
												<!-- <el-button size="small" :icon="WarnTriangleFilled" plain type="danger"
												>超期*2</el-button
											>
											<el-button size="small" :icon="WarnTriangleFilled" plain type="warning"
												>超期预警*2</el-button
											> -->
												<el-button size="small" :icon="Promotion" plain type="primary"
													v-if="row.countPriority && row.countPriority > 0">优先*{{
														row.countPriority }}</el-button>
											</div>
										</div>
									</div>
								</div>
							</template>
						</el-table-column>
					</el-table>
				</div>
			</div>
		</div>
		<el-dialog v-model="showDialog" title="任务列表" width="80%">
			<div style="padding: 15px">
				<el-table :data="taskList" border max-height="65vh">
					<el-table-column type="index" label="序号" align="center" width="80"></el-table-column>
					<el-table-column property="priorityName" align="center" label="优先级" width="100"></el-table-column>
					<el-table-column property="project_name" align="center" label="项目名称"></el-table-column>
					<el-table-column property="title" align="center" label="任务标题" show-overflow-tooltip>
						<template #default="{ row }">
							<el-link type="primary" @click="handleView(row.pending_to_type, row)">{{
								row.title
							}}</el-link>
						</template>
					</el-table-column>
					<el-table-column property="taskType" align="center" label="类型" width="120"></el-table-column>
					<el-table-column property="taskAttributeName" align="center" label="任务属性"
						width="100"></el-table-column>
					<el-table-column property="statusName" align="center" label="状态" width="100"></el-table-column>
					<el-table-column property="end_time" align="center" label="任务截止日期" width="120">
						<template #default="{ row }">
							{{ row.end_time ? dayjs(row.end_time).format("YYYY-MM-DD") : "-" }}
						</template>
					</el-table-column>
					<el-table-column property="start_time" align="center" label="开始时间" width="180"></el-table-column>
				</el-table>
			</div>
		</el-dialog>
	</div>
</template>
<script setup>
import dayjs from "dayjs";
import "dayjs/locale/zh-cn"; // 中文
import en from "dayjs/locale/en"; // 英文
import isoWeek from 'dayjs/plugin/isoWeek'; // 用于 ISO 周处理
import { ref, defineProps, watch, computed, h } from "vue";
import { createDrawerAsync } from "@/core/dialog";
import { ElDatePicker } from "element-plus";
import DetailPage from "@/pages/modules/task/task-detail/index.vue";

import { getTaskList, getUserTaskList } from "@/api/task/group";
import {
	CircleCheckFilled,
	CaretLeft,
	CaretRight,
	Clock,
	WarnTriangleFilled,
	Promotion
} from "@element-plus/icons-vue";
// 设置 Day.js 的周起始为周一
dayjs.locale("zh-cn", { weekStart: 1 });
dayjs.extend(isoWeek);
const modifiedEn = {
	...en,
	weekStart: 1
};
//@ts-ignore
dayjs.locale(modifiedEn, null, true);
const WEEKDAYS = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
const props = defineProps({
	groupInfo: {
		type: Object,
		default: () => {
			return {};
		}
	}
});
watch(
	() => props.groupInfo,
	() => {
		if (!groupUserId.value) {
			getTaskData();
		} else {
			groupUserId.value = "";
		}
	}
);
const chooseDate = ref(new Date().getTime());
const list = ref([{}, {}, {}, {}, {}]);
const groupUserId = ref("");
const taskList = ref([]);
const showDialog = ref(false);
watch(groupUserId, () => {
	getTaskData();
});
watch(chooseDate, () => {
	getTaskData();
});
const setUserId = (id) => {
	if (id == groupUserId.value) {
		return;
	}
	groupUserId.value = id;
};
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }) => {
	if (columnIndex === 0) {
		if (rowIndex !== 0 && row[column.property] === list.value[rowIndex - 1][column.property]) {
			return [0, 1];
		}
		let count = 0;
		list.value.forEach((item, i) => {
			if (i >= rowIndex && item[column.property] === row[column.property]) {
				count++;
			}
		});
		return [count, 1];
	}
	if (!isDateInRange(tableDateList.value[columnIndex - 1].date, row.start_time, row.end_time)) {
		return [1, 1];
	}
	if (tableDateList.value[columnIndex - 1].date == row.start_time) {
		let num = 0;
		tableDateList.value.forEach((el, index) => {
			if (isDateInRange(el.date, row.start_time, row.end_time)) {
				num++;
			}
		});
		return [1, num];
	}
	return [1, 0];
};
// 切换日期
const addWeek = () => {
	chooseDate.value = dayjs(chooseDate.value).add(1, "week").toDate().getTime();
};
const subWeek = () => {
	if (isNowWeek.value) return;
	chooseDate.value = dayjs(chooseDate.value).subtract(1, "week").toDate().getTime();
};
// 计算周数
const getWeekInfo = (date = new Date()) => {
	const d = dayjs(date);
	const year = d.year();
	const month = d.month() + 1; // 0-based 转 1-based
	const dayOfMonth = d.date(); // 1-31
	const startOfMonth = dayjs(`${year}-${month}-01`).day() === 0 ? 7 : dayjs(`${year}-${month}-01`).day();
	const weekNumber = Math.ceil((dayOfMonth - (8 - startOfMonth)) / 7) + 1;
	return `${year}年${month}月第${weekNumber}周`;
};
// 计算周一到周日的日期
const getWeekDays = (date = new Date()) => {
	const d = dayjs(date);
	const weekStart = d.day() === 0 ? d.subtract(6, "day") : d.subtract(d.day() - 1, "day");
	return Array.from({ length: 7 }, (_, index) => {
		const currentDay = weekStart.add(index, "day");
		const dateStr = currentDay.format("MM-DD");
		const weekdayStr = WEEKDAYS[currentDay.day()];
		return {
			label: `${weekdayStr}（${dateStr}）`,
			date: currentDay.format("YYYY-MM-DD")
		};
	});
};
// 获取起止日期
const getWeekRange = computed(() => {
	const inputDate = dayjs(chooseDate.value);
	const clonedDate = inputDate.clone();
	const dayOfWeek = clonedDate.day();
	const weekMonday = clonedDate.subtract(dayOfWeek === 0 ? 6 : dayOfWeek - 1, "day");
	const monday = weekMonday.format("YYYY-MM-DD");
	const sunday = weekMonday.add(6, "day").format("YYYY-MM-DD");
	return [monday, sunday];
});
const isNowWeek = computed(() => {
	const now = dayjs();
	const nowWeek = getWeekRange.value;
	return isDateInRange(now, nowWeek[0], nowWeek[1]);
})
// 获取table日期列表
const tableDateList = computed(() => {
	return getWeekDays(chooseDate.value);
});
// 判断日期是否在时间段内的方法
const isDateInRange = (targetDate, startDate, endDate, format = "YYYY-MM-DD") => {
	// 使用 dayjs 解析日期
	const target = dayjs(targetDate, format);
	const start = dayjs(startDate, format);
	const end = dayjs(endDate, format);
	// 检查日期是否有效
	if (!target.isValid() || !start.isValid() || !end.isValid()) {
		return false;
	}

	// 判断目标日期是否在范围内（包含边界）
	return (target.isAfter(start) || target.isSame(start)) && (target.isBefore(end) || target.isSame(end));
};
// 查看详情
const handleView = (type, row) => {
	createDrawerAsync(
		{ title: row.title, width: "1100px", showNext: false, showConfirm: false },
		{},
		h(DetailPage, { type, id: row.id })
	);
};
// 获取当前用户在组内顺序
const findPeopleIndex = (id) => {
	if (!props.groupInfo.sysGroupUser) {
		return "";
	}
	return props.groupInfo.sysGroupUser.findIndex((item) => item.userId == id);
};
// 禁用本周之前的日期
const disabledDate = (date) => {
	// 获取本周一的日期
	const thisWeekMonday = dayjs().startOf('isoWeek');
	// 如果日期早于本周一，则禁用
	return date < thisWeekMonday;
};
// 获取数据
const getTaskData = () => {
	getTaskList({
		groupId: props.groupInfo.id,
		start_time: getWeekRange.value[0],
		end_time: getWeekRange.value[1],
		user_id: groupUserId.value
	}).then((res) => {
		list.value = res;
	});
};
const showTaskList = (id) => {
	getUserTaskList({
		user_id: id,
		start_time: getWeekRange.value[0],
		end_time: getWeekRange.value[1]
	}).then((res) => {
		taskList.value = res;
		showDialog.value = true;
	});
};
</script>
<style lang="scss" scoped>
.tabPage {
	width: 100%;
	height: 100%;
	overflow: hidden;
	align-items: flex-start;

	.title {
		height: 56px;
		line-height: 56px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
		color: rgba(0, 0, 0, 0.85);
		font-size: 18px;
		font-weight: bold;
		padding-left: 10px;
		width: 100%;
		text-align: left;
		box-sizing: border-box;
		margin-top: -10px;
	}
}

.left {
	width: 285px;
	flex-shrink: 0;
	height: 100%;
	margin-right: 32px;

	.label {
		align-items: center;
		padding-left: 10px;
		padding-top: 10px;
		height: 32px;

		img {
			width: 24px;
			height: auto;
			margin-right: 8px;
		}

		color: rgba(0, 0, 0, 0.85);
		font-size: 16px;
	}

	.item_box {
		overflow: auto;
		margin-top: 20px;
		padding-left: 10px;
		padding-bottom: 4px;

		.item {
			width: 100%;
			height: 80px;
			margin-bottom: 12px;
			box-shadow: inset 0px -2px 12px 0px rgba(54, 1, 245, 0.32);
			border-top: 4px solid #3601f5;
			border-radius: 8px;
			overflow: hidden;
			background: #ffffff;
			align-items: center;
			box-sizing: border-box;
			padding: 0 16px;
			text-align: left;
			cursor: pointer;

			&:nth-last-child(1) {
				margin-bottom: 0;
			}

			img {
				width: 36px;
				height: 36px;
				margin-right: 16px;
			}

			.name {
				color: rgba(0, 0, 0, 0.85);
				font-weight: bold;
				line-height: 24px;
				font-size: 16px;
			}

			.position {
				color: rgba(0, 0, 0, 0.65);
				font-size: 14px;
				line-height: 22px;
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
				width: 100%;
			}

			.select {
				width: 16px;
				height: 16px;
				border: 1px solid rgba(0, 0, 0, 0.15);
				border-radius: 50%;

				&.active {
					border: none;
				}
			}
		}
	}
}

.right {
	padding-top: 14px;

	.dateInfo {
		width: 100%;
		justify-content: center;
		align-items: center;
		height: 32px;
		margin-bottom: 10px;

		.el-icon {
			cursor: pointer;
		}

		span {
			color: rgba(0, 0, 0, 0.85);
			font-size: 16px;
			font-weight: bold;
			margin: 0 8px;
		}
	}
}

.people_box {
	background: #ffffff;
	border-radius: 8px;
	overflow: hidden;
	padding: 0 16px;
	box-shadow: inset 0px -2px 12px 0px rgba(54, 1, 245, 0.32);
	border-top: 4px solid #3601f5;
	cursor: pointer;

	img {
		width: 24px;
		height: 24px;
		margin-right: 8px;
		flex-shrink: 0;
	}

	.header {
		flex-shrink: 0;
		justify-content: space-between;
		align-items: center;
		padding: 8px 0;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
		color: rgba(0, 0, 0, 0.85);
		font-size: 12px;

		.flex span {
			flex-shrink: 0;
		}
	}

	.tags {
		flex-wrap: wrap;
		padding-top: 10px;

		.tag {
			border-radius: 4px;
			padding: 2px 8px;
			margin: 4px 4px 0 0;
			background: #1b86ff;
			color: #fff;
			font-size: 12px;
		}
	}

	.footer {
		color: rgba(0, 0, 0, 0.65);
		font-size: 12px;
		justify-content: space-between;
		flex-wrap: wrap;
		padding: 10px 0;

		.flex {
			align-items: center;
			flex-shrink: 0;
		}
	}
}

.people0 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(40, 114, 250, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #1b86ff !important;
}

.people1 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(55, 184, 35, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #67c23a !important;

	.tags .tag {
		background: #67c23a;
	}
}

.people2 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(2, 163, 134, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #40bfa8 !important;

	.tags .tag {
		background: #40bfa8;
	}
}

.people3 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(250, 176, 40, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #f5a250 !important;

	.tags .tag {
		background: #f5a250;
	}
}

.people4 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(250, 40, 141, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #d96ba7 !important;

	.tags .tag {
		background: #d96ba7;
	}
}

.people5 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(250, 40, 40, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #f56c6c !important;

	.tags .tag {
		background: #f56c6c;
	}
}

.people6 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(119, 38, 169, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #a25ac4 !important;

	.tags .tag {
		background: #a25ac4;
	}
}

.people7 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(100, 100, 100, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #4a4a4a !important;

	.tags .tag {
		background: #4a4a4a;
	}
}

.people8 {
	box-shadow:
		inset 0px -2px 12px 0px rgba(169, 121, 38, 0.32),
		0px 2px 4px 0px rgba(0, 0, 0, 0.24) !important;
	border-top: 4px solid #ba7135 !important;

	.tags .tag {
		background: #ba7135;
	}
}

.always-visible-picker {
	position: relative;
	z-index: 100;
}

:deep(.always-visible-picker) {
	.el-input {
		/* 隐藏输入框 */
		display: none !important;
	}

	.el-date-picker {
		width: 260px;
	}

	.el-date-picker .el-picker-panel__content {
		width: 240px;
	}

	.el-date-table td,
	.el-date-table th {
		width: 30px;
		padding: 0;
	}

	.el-picker__popper {
		/* 强制面板显示 */
		display: block !important;
		opacity: 1 !important;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
		/* 添加阴影美化 */
		z-index: 9999;
		/* 提高层级 */
		top: 10px !important;
		left: 10px !important;
	}

	.el-popper__arrow {
		display: none !important;
	}

	.el-date-picker__header {
		background-color: #f5f7fa;
		/* 基础背景色 */
	}
}
</style>
