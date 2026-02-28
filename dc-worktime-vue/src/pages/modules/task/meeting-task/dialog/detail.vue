<template>
	<div class="meeting-wrap">
		<div class="meeting-info dirct flex flex-items-start m-b-24px m-t-24px">
			<div class="flex task-info-wrap">
				<div class="task-info flex flex-items-center m-r-12px">
					<img src="@/assets/task/icon-name.png" class="icons" alt="" />
					<div class="info">
						<div>姓名</div>
						<div class="val">{{ detailInfo.createUserName || "--" }}</div>
					</div>
				</div>
				<div class="task-info flex flex-items-center m-r-12px">
					<img src="@/assets/task/icon-date.png" class="icons" alt="" />
					<div class="info">
						<div>日期</div>
						<div class="val">
							{{ (detailInfo.meet_time && detailInfo.meet_time.substring(0, 10)) || "--" }}
						</div>
					</div>
				</div>
				<div class="task-info flex flex-items-center m-r-12px">
					<img src="@/assets/task/icon-time.png" class="icons" alt="" />
					<div class="info">
						<div>时间</div>
						<div class="val">
							{{ (detailInfo.meet_time && detailInfo.meet_time.substring(11, 19)) || "--" }}
						</div>
					</div>
				</div>
				<div class="task-info flex flex-items-center m-r-12px">
					<img src="@/assets/task/icon-state.png" class="icons" alt="" />
					<div class="info">
						<div>状态信息</div>
						<div class="val">{{ detailInfo.statusName || "--" }}</div>
					</div>
				</div>
				<div class="task-info flex flex-items-center">
					<img src="@/assets/task/icon-location.png" class="icons" alt="" />
					<div class="info">
						<div>地点名称</div>
						<div class="val">{{ detailInfo.meet_address || "--" }}</div>
					</div>
				</div>
			</div>
			<ElTabs v-model="activeName" @tab-click="handleClick">
				<ElTabPane label="详情" name="first"></ElTabPane>
				<ElTabPane label="参会人员" name="second"></ElTabPane>
				<ElTabPane label="完成信息" name="third"></ElTabPane>
			</ElTabs>
		</div>
		<div class="meeting-info detail-info" v-if="activeName == 'first'">
			<div class="title">属性</div>
			<div class="content-info">
				<ElDescriptions :column="3">
					<ElDescriptionsItem label="类型：">会议任务</ElDescriptionsItem>
					<ElDescriptionsItem label="关联项目：">{{ detailInfo.project_name }}</ElDescriptionsItem>
					<ElDescriptionsItem label="关联计划或者里程碑：">{{
						detailInfo.plan_name || detailInfo.milestoneName
					}}</ElDescriptionsItem>
					<ElDescriptionsItem label="会议时间：">{{ detailInfo.meet_time }}</ElDescriptionsItem>
					<ElDescriptionsItem label="会议时长：">{{ detailInfo.planing_hours ? detailInfo.planing_hours + "分钟" : "--" }}</ElDescriptionsItem>
					<ElDescriptionsItem label="会议地点：">{{ detailInfo.meet_address }}</ElDescriptionsItem>
					<ElDescriptionsItem label="创建时间：">{{ detailInfo.createTime }}</ElDescriptionsItem>
					<ElDescriptionsItem label="创建人：">{{ detailInfo.createUserName }}</ElDescriptionsItem>
					<ElDescriptionsItem label="更新时间：">{{ detailInfo.updateTime }}</ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</div>
		<div v-if="activeName == 'second'">
			<div class="flex flex-wrap m-b-16px">
				<div class="meet-user" :class="{active: index == activeIndex}" @click="handleUserClick(index)" v-for="(item,index) in detailInfo.taskMeetUsers" :key="item.id">
					<el-icon size="14px" color="rgba(0,0,0.65)"><User /></el-icon>
					<span class="m-l-8px">{{ item.userName }}</span>
				</div>
			</div>
			<div class="meeting-info detail-info">
				<div class="title">会议执行信息</div>
				<div class="content-info">
					<ElDescriptions :column="3">
						<ElDescriptionsItem label="类型：">会议任务</ElDescriptionsItem>
						<ElDescriptionsItem label="开始时间:">{{ userMeetInfo.meet_start_time || "--" }}</ElDescriptionsItem>
						<ElDescriptionsItem label="完成时间:">{{ userMeetInfo.completion_time || "--" }}</ElDescriptionsItem>
						<ElDescriptionsItem label="预计会议时长:">{{
							userMeetInfo.planing_hours ? userMeetInfo.planing_hours + "分钟" : "--"
						}}</ElDescriptionsItem>
						<ElDescriptionsItem label="我的会议时长:">{{
							(userMeetInfo.working_hours === 0 || userMeetInfo.working_hours) ? userMeetInfo.working_hours + "分钟" : "--"
						}}</ElDescriptionsItem>
					</ElDescriptions>
				</div>
			</div>
		</div>
		<div class="meeting-info detail-info" v-if="activeName == 'third'">
			<div class="title">会议完成信息</div>
			<div class="content-info">
				<ElDescriptions :column="3">
					<ElDescriptionsItem label="类型：">会议任务</ElDescriptionsItem>
					<ElDescriptionsItem label="开始时间:">{{ detailInfo.meet_time || "--" }}</ElDescriptionsItem>
					<ElDescriptionsItem label="完成时间">{{ detailInfo.completion_time || "--" }}</ElDescriptionsItem>
					<ElDescriptionsItem label="会议时长">{{
						detailInfo.planing_hours ? detailInfo.planing_hours + "分钟" : "--"
					}}</ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { info } from "@/api/task/meeting-task";
import { Right, MoreFilled, User } from "@element-plus/icons-vue";
import {
	ElTabPane,
	ElTabs,
	ElDescriptions,
	ElTable,
	ElTableColumn,
	ElDescriptionsItem,
	ElCard,
	ElLink
} from "element-plus";
import { number } from "vue-types";
import { downFile } from "@/core/utils";
import { ref } from "vue";

const props = defineProps({
	id: number().def()
});

const activeName = ref("first");
const detailInfo = ref({});
const userMeetInfo = ref({})
if (props.id) {
	info(props.id).then((res) => {
		detailInfo.value = res;
		userMeetInfo.value = detailInfo.value.taskMeetUsers?.[0]
		
	});
}

const activeIndex = ref('0');

const handleUserClick = (index: any) => {
	activeIndex.value = index;
	userMeetInfo.value = detailInfo.value.taskMeetUsers?.[index] || {}

};
</script>
<style lang="scss" scoped>
.title {
	font-weight: bold;
	font-size: 16px;
	color: var(--el-text-color-regular);
	line-height: 24px;
}
.meeting-wrap {
	.meeting-info {
		background:var(--el-page-color);
		box-shadow: 0px 0px 7px 0px rgba(0, 0, 0, 0.05);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
	}

	.dirct {
		flex-direction: column;
	}

	.icons {
		width: 48px;
		height: 48px;
		border-radius: 12px;
		margin-right: 16px;
	}
	.task-info-wrap {
		padding: 16px;
		width: 100%;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
	}

	.task-info {
		flex: 1;
		height: 82px;
		background:var(--el-page-color);
		// box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
		// border-radius: 8px 8px 8px 8px;
		// border: 1px solid rgba(0, 0, 0, 0.06);

		.info {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			.val {
				height: 24px;
				line-height: 24px;
				font-size: 16px;
				font-weight: bold;
				margin-bottom: 4px;
				margin-top: 6px;
				color: var(--el-text-color-regular);
			}
		}
	}

	.meet-user {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-right: 12px;
		margin-bottom: 12px;
		width: 98px;
		height: 44px;
		color: rgba(0,0,0,0.65);
		background: #FFFFFF;
		box-shadow: 0px 0px 7px 0px rgba(0,0,0,0.05);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0,0,0,0.06);
	}
	.active {
		color:#fff;
		background: #1B86FF;
		box-shadow: 0px 0px 7px 0px rgba(0,0,0,0.05);
	}
}

.detail-info {
	.title {
		padding: 48px;
		font-weight: bold;
		font-size: 16px;
		color: var(--el-text-color-regular);
		line-height: 48px;
		padding: 0 24px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);
	}
}
.content-info {
	padding: 24px;
	
}

:deep(.el-dialog__headerbtn) {
	height: 56px;
}

::v-deep {
	.el-tabs__header {
		margin: 0;
	}

	.el-tabs--top > .el-tabs__header .el-tabs__item:nth-child(2) {
		padding-left: 24px;
	}
	.el-tabs__item {
		height: 54px;
		padding: 0 24px;
	}
}
</style>