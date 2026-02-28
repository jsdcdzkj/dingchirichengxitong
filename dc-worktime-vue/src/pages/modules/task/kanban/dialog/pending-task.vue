<template>
	<div>
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"
				><Warning color="#F56C6C"
			/></ElIcon>
			任务挂起时需要创建指派至自己的运维任务或选择任务！
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="title"><span style="color: red">*</span> 任务方式</div>
		<ElRadioGroup v-model="way" @change="handleWay">
			<ElRadio label="创建运维任务" value="1" />
			<ElRadio label="选择其他任务" value="2" />
		</ElRadioGroup>
		<BaseForm class="m-t-16px" v-if="way == 1" v-bind="form1"></BaseForm>
		<div v-if="way == 2"  v-bind="form2">
			<div class="title">任务类型</div>
			<ElRadioGroup v-model="pending_to_type" @change="handleType">
				<ElRadioButton label="计划任务" value="1" />
				<ElRadioButton label="bug任务" value="2" />
				<ElRadioButton label="领导交办任务" value="4" />
				<ElRadioButton label="运维任务" value="3" />
				<ElRadioButton label="会议任务" value="5" />
			</ElRadioGroup>
			<div class="title">选择任务</div>
			<ElTable border :data="list" highlight-current-row @current-change="handleCurrentChange">
				<ElTableColumn label="序号" width="80" align="center" type="index">
				</ElTableColumn>
				<ElTableColumn v-if="pending_to_type !=4" label="项目名称" prop="project_name" align="center">
				</ElTableColumn>
				<ElTableColumn  label="任务名称" prop="title" align="center">
				</ElTableColumn>
				<ElTableColumn label="状态" prop="statusName" align="center">
				</ElTableColumn>
			</ElTable>
			<BaseForm v-bind="form2"></BaseForm>
		</div>
		
	</div>
</template>

<script setup lang="ts">
import { processFilesStruct } from "@/api/uploader";
import BaseForm from "@/core/struct/form/base-form";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { useUserStore } from "@/store/user.ts";
import { ElMessage } from "element-plus";
import {
	ElDivider,
	ElRadioGroup,
	ElRadioButton,
	ElTable,
	ElTableColumn,
	ElRadio
} from "element-plus";
import { Warning } from "@element-plus/icons-vue";
import { toRaw, ref, onMounted } from "vue";
import { number } from "vue-types";
import {
	pendingTask,
	pendingTaskSave,
	leaderGetList,
	operationGetList,
	taskGetList,
	bugGetList,
	pendingTaskBug,
	pendingTaskOperation,
	pendingTaskLeader,
	pendingTaskMeeting,
	meetGetList
} from "@/api/task/kanban";
import { appInstance } from "@/main";

const userStore = useUserStore();

const props = defineProps({
	index: number().def(),
	row: {
		type: Object,
		default: {}
	}
});

const list = ref([]);
const pending_to_type = ref('1');

const { form } = useDialogStructForm({
	expand: false,
	labelWidth: 60,
	showMessage: true,
	inlineMessage: false,
	labelPosition: "top",
	formItems: [
		{
			label: "任务方式",
			value: "2",
			type: "ElRadioGroup",
			prop: "type",
			span: 24,
			select: {
				type: "ElRadio",
				list: [
					{
						label: "创建运维任务",
						value: "1"
					},
					{
						label: "选择其他任务",
						value: "2"
					}
				]
			}
		}
	]
});

const { form: form1, registerFormDone } = useDialogStructForm({
	expand: false,
	labelWidth: 60,
	showMessage: true,
	inlineMessage: false,
	labelPosition: "top",
	formItems: [
		{
				label: "项目名称",
				value: '',
				type: "ElSelect",
				prop: "project_id",
				span: 12,
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: appInstance.projectList,
				}
			},
			{
				label: "任务标题",
				prop: "name",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "截止日期",
				value: '',
				type: "ElDatePicker",
				prop: "end_time",
				span: 12,
				attrs: {
					valueFormat: "YYYY-MM-DD"
				}
			},
			
		{
			label: "指派至",
			value: "",
			type: "ElRadioGroup",
			prop: "assigned_to_user_id",
			span: 24,
			select: {
				type: "ElRadio",
				list: [
					{
						label: "自己",
						value: userStore.userInfo.sysUser.id
					}
				]
			}
		},
		{
			label: "任务内容",
			value: "",
			type: "Editor",
			prop: "description",
			span: 24,
			attrs: {
				style: { height: "150px", width: "100%" },
				contentType: "html"
			}
		},
		{
				label: "预估工时（分钟）",
				value: "30",
				type: "ElInputNumber",
				prop: "plan_hours",
				span: 12,
				attrs: {
					min: 0,
				},
			},
			{
				label: "难度等级",
				value: 1,
				type: "ElSelect",
				prop: "degree_of_difficulty",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.degree_of_difficulty,
				}
			},
	],
	rules: {
		project_id: [
			{
				required: true,
				message: "请选择",
				trigger: "change"
			}
		],
		name: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		end_time: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		plan_hours: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		description: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		degree_of_difficulty: [
			{
				required: true,
				message: "请选择",
				trigger: "change"
			}
		]
	}
});
const { form:form2, registerFormDone: registerFormDone1 } = useDialogStructForm({
	expand: false,
	labelWidth: 60,
	showMessage: true,
	inlineMessage: false,
	labelPosition: "top",
	formItems: []
});
const leaderList = ref([]);
const planList = ref([]);
const bugList = ref([]);
const operationList = ref([]);
const meetList = ref([])
onMounted(() => {
	leaderGetList({statusList: ["2", "5", "8", "9"], assigned_to_user_id: userStore.userInfo.sysUser.id}).then((res) => {
		leaderList.value = res
	});
	bugGetList({statusList: ["2", "6", "7"], assigned_to_user_id: userStore.userInfo.sysUser.id}).then(res => {
		bugList.value = res.map((item:any) => {
			item.title = item.bug_title
			item.project_name = item.pro_project_name
			return item
		})
	})
	operationGetList({statusList: ["2", "5", "6"], assigned_to_user_id: userStore.userInfo.sysUser.id}).then((res) => {
		operationList.value = res.map((item:any) => {
			item.title = item.name
			return item
		});
	});
	taskGetList({statusList: ["2", "5", "6"], assigned_to_user_id: userStore.userInfo.sysUser.id}).then((res) => {
		planList.value = res;
		list.value = res;
	});
	meetGetList({statusList: ["0", "3", "4"], userId: userStore.userInfo.sysUser.id}).then((res) => {
		meetList.value = res;
		list.value = res;
	});
});
const handleType = (val: any) => {
	if (val == 4) {
		list.value = leaderList.value || [];
	} else if (val == 1) {
		list.value = planList.value || [];
	} else if (val == 2) {
		list.value = bugList.value || []
	}else if(val == 3){
		list.value = operationList.value || [];
	} else {
		list.value = meetList.value || [];
	}
};
const way = ref('2')
const handleWay = (val:any) => {
	way.value == val
}
const chooseId = ref();
const handleCurrentChange = (val: any) => {
	chooseId.value = val?.id;
};
registerFormDone1(async () => {
	if (way.value == '1') {
		await pendingTaskSave(
			Object.assign({}, toRaw(form.value), {
				...form1.value,
				task_type: props.index,
				task_id:props.row.id,
				end_time: form1.value.end_time + ' 23:59:59'
			})
		);
	} else {
		if (!chooseId.value) {
			ElMessage.warning("请至少选择一个任务");
			throw "请至少选择一个任务"
		}
		if(props.index == 1) {
			await pendingTask(
				Object.assign({}, toRaw(form.value), {
					pending_to_type: pending_to_type.value,
					pending_to_task_id: chooseId.value,
					id: props.row.id
				})
			);
		} else if(props.index == 2) {
			await pendingTaskBug(
				Object.assign({}, toRaw(form.value), {
					pending_to_type: pending_to_type.value,
					pending_to_task_id: chooseId.value,
					id: props.row.id
				})
			);
		} else if(props.index == 3) {
			await pendingTaskOperation(
				Object.assign({}, toRaw(form.value), {
					pending_to_type: pending_to_type.value,
					pending_to_task_id: chooseId.value,
					id: props.row.id
				})
			);
		} else if(props.index == 4) {
			await pendingTaskLeader(
				Object.assign({}, toRaw(form.value), {
					pending_to_type: pending_to_type.value,
					pending_to_task_id: chooseId.value,
					id: props.row.id
				})
			);
		} else {
			await pendingTaskMeeting(
				Object.assign({}, toRaw(form.value), {
					pending_to_type: pending_to_type.value,
					pending_to_task_id: chooseId.value,
					id: props.row.id
				})
			);
		}
		
	}

	return true;
});
</script>
<style lang="scss" scoped>
.title {
	line-height: 22px;
	margin-bottom: 8px;
	margin-top: 16px;
}
</style>