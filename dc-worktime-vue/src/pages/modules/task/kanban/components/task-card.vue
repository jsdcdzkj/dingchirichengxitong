<template>
	<ElCard>
		<template #header>
			<div
				class="card-header flex flex-items-center flex-justify-between"
			>
				<div class="task-name flex flex-items-center">
					<div v-if="taskType == 1" class="type">
						<img v-if="row.task_attribute == 3" class="type-icon" src="@/assets/task/product-icon.png" alt="">
						<img v-else-if="row.task_attribute == 2" class="type-icon" src="@/assets/task/backend-icon.png" alt="">
						<img v-else-if="row.task_attribute == 1" class="type-icon" src="@/assets/task/front-icon.png" alt="">
						<img v-else-if="row.task_attribute == 4" src="@/assets/task/ui-icon.png" class="type-icon" alt="">
						{{ item.taskAttributeName }}
					</div>
					<div class="type" v-else-if="taskType == 2">
						<img  src="@/assets/task/bug-icon.png" class="type-icon" alt="">
						{{item.sourceName}}
					</div>
					<div class="type" v-else-if="taskType == 3">
						<img src="@/assets/task/operate-icon.png" class="type-icon" alt="">
						运维任务
					</div>
					<div class="type" v-else-if="taskType == 4">
						<img src="@/assets/task/leader-icon.png" class="type-icon" alt="">
						领导交办任务
					</div>
					<div class="type" v-else-if="taskType == 5">
						<img src="@/assets/task/meeting-icon.png" class="type-icon" alt="">
						会议
					</div>
					<div class="project-name" v-if="row.project_name || row.pro_project_name">{{row.project_name || row.pro_project_name}}</div>
                    <span class="status">{{row.statusName}}</span>
				</div>
				<span class="man">{{ row.assigned_to_user_name?.substr(-1) || row.userName?.substr(-1) }}</span>
			</div>
		</template>
		<div class="content">
			<div class="flex flex-items-center card-tip-title">
				<Icon
                    name="icon-jikou.png"
                    :size="{ width: '16px', height: '16px' }"
                />
				<div style="margin-left:6px;max-width:88px" v-if="taskType == 1">【{{item.taskAttributeName}}】</div>
				<div style="margin-left:6px;max-width:88px" v-if="taskType == 2">【{{taskTypeArr[item.assigned_to_user_type]}}】</div>
				<div style="margin-left:6px;max-width:88px" v-if="taskType == 3">【运维任务】</div>
				<div style="margin-left:6px;max-width:118px" v-if="taskType == 4">【领导交办任务】</div>
				<div style="margin-left:6px;overflow: hidden;text-align: left; flex:1;text-overflow: ellipsis; white-space: nowrap;">{{ row.title || row.bug_title || row.name }}</div>
			</div>
			<div class="flex flex-items-center flex-wrap" :class="{'flex-justify-between': taskType == 5}">
				<span class="flex flex-items-center">
					<div class="name m-t-12px">{{ row.assigned_to_user_name || row.userName }}</div>
					<div v-if="taskType == 5 && (row.plan_name || row.milestoneName)" class="name textOver m-t-12px m-l-6px">{{ row.plan_name || row.milestoneName}}</div>
				</span>
				
				<div class="m-t-12px" v-if="taskType == 1 || taskType == 3">
					<ElTag size="small" style="margin: 0 8px">{{row.priorityName}}</ElTag>
				</div>
				<div class="m-t-12px" v-if="taskType == 2">
					<ElTag size="small" style="margin: 0 8px" type="danger">{{row.severityName}}</ElTag>
				</div>
				<div class="m-t-12px" v-if="taskType == 4">
					<ElTag size="small" style="margin: 0 8px" effect="dark" type="danger">{{row.urgencyName}}</ElTag>
				</div>
				<div v-if="taskType == 1 || (taskType == 2 && (item.assigned_to_user_type == 1 || item.assigned_to_user_type == 2))" class="name textOver m-t-12px">{{ row.featureName || row.plan_title}}</div>
				<template v-if="taskType == 5">
					<div class="name  m-l-8px textOver m-t-12px" style="float:right">{{ row.meet_address + ' ' + row.meet_time?.substring(11,19)}}</div>
				</template>
				<div v-if="(taskType != 2 && taskType != 5) && (taskStatus == 1 || taskStatus == 2)" class="name m-l-8px m-t-12px">{{'剩余预估工时：' + row.remaining_work}}</div>
			</div>
			<div class="flex flex-items-center flex-justify-between flex-wrap">
				<div class="flex flex-items-center m-t-12px" style="line-height: 24px;">
					<span v-if="taskType != 2" class="m-r-8px flex flex-items-center">
						<ElIcon><Clock /></ElIcon>
						<span class="m-l-4px">{{ row.planHours  }}</span>
					</span>
					<span class="flex flex-items-center">
						<ElIcon><Calendar /></ElIcon>
						{{ row.end_time?.substring(0,10) || row.meet_time?.substring(0,10) }}
					</span>
				</div>
				<div class="m-t-12px"><slot name="operation"></slot></div>
			</div>
		</div>
	</ElCard>
</template>

<script setup lang="tsx">
import {
	Edit,
	Clock,
	Stopwatch,
	Handbag,
	Calendar
} from "@element-plus/icons-vue";
import { Icon } from "@/components/icon/icon";
import { computed, ref } from "vue";
import { string } from "vue-types";

const props = defineProps({
	taskType: {
		type: String,
		defaulte: '1'
	},
	taskStatus: {
		type: String,
		defaulte: ''
	},
	item: {
		type: Object,
		default: () => ({})
	},
});
const row = computed(() => {
	return props.item
})

const taskTypeArr = ref(['', '前端', '后端','UI','产品'])

</script>
<style lang="scss" scoped>
.card-header {
	.task-name {
		font-size: 12px;
		color: rgba(0, 0, 0, 0.65);
		line-height: 20px;

		.type {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 22px;
			height: 30px;
			padding: 0 8px;
			display: flex;
			align-items: center;
			background: #e8f3ff;
			border-radius: 16px 16px 16px 16px;
		}

		.type-icon {
			width: 17px;
			height: 17px;
			margin-right: 8px;
		}

		.status {
			padding: 4px 8px;
			background: #f8f8f8;
			border-radius: 4px 4px 4px 4px;
			margin-left: 8px;
		}
	}
	.man {
		width: 24px;
		height: 24px;
        line-height: 24px;
        font-size: 14px;
		text-align: center;
		color: #ffffff;
		background: #1b86ff;
		border-radius: 32px 32px 32px 32px;
	}
}
.content {
    font-size: 12px;
	color: rgba(0, 0, 0, 0.65);

    .bag-icon {
        height: 20px;
        width: 20px;
        background: #1b86ff;
        margin-right: 8px;
        line-height: 20px;
        text-align: center;
        color: #fff;
        font-size: 14px;
    }
    .name {
        background: #F8F8F8;
        padding: 0 8px;
        height: 20px;
        line-height: 20px;
		min-width: 56px;
    }

	.textOver {
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
	}

	.card-tip-title {
		font-size: 14px;
		font-weight: 500;
	}
}

.project-name {
	text-align: left;
    font-size: 14px;
    margin-left: 8px;
	padding: 4px 8px;
	background: rgb(235.9, 245.3, 255,0.7);
	text-overflow: ellipsis;
	white-space: nowrap;
	overflow: hidden;
}

::v-deep {
	.el-card__header {
		padding: 12px;
	}
}
::-webkit-scrollbar {
  display: none;
}
</style>