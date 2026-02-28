<template>
	<ElForm :model="form" labelWidth="100px" ref="formSearch">
		<ElRow :gutter="16">
			<ElCol :span="5">
				<ElFormItem label="任务标题">
					<ElInput
						placeholder="请输入任务标题"
						v-model="form.name"
						clearable
						@keyup.enter="onSearch"
					></ElInput>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="项目名称">
					<ElSelect v-model="form.project_id" placeholder="请选择" clearable filterable>
						<ElOption v-for="item in projectList" :key="item.id" :label="item.projectName" :value="item.id">
						</ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="属性">
					<ElSelect v-model="form.task_attribute" placeholder="请选择属性" clearable filterable>
						<ElOption
							v-for="(item, index) in appInstance.mapDict.task_attribute"
							:key="index"
							:label="item.label"
							:value="item.value"
						>
						</ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="负责人">
					<ElSelect v-model="form.assigned_to_user_id" @change="changeUser" placeholder="请选择负责人" clearable filterable>
						<ElOption
							v-for="(item, index) in userList"
							:key="index"
							:label="item.realName"
							:value="item.id"
						>
						</ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="状态">
					<ElSelect v-model="form.status" placeholder="请选择状态" clearable filterable>
						<ElOption
							v-for="(item, index) in statusList"
							:key="index"
							:label="item.label"
							:value="item.value"
						>
						</ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="优先级">
					<ElSelect v-model="form.priority" placeholder="请选择优先级" clearable filterable>
						<ElOption
							v-for="(item, index) in appInstance.mapDict.priority"
							:key="index"
							:label="item.label"
							:value="item.value"
						>
						</ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="5">
				<ElFormItem label="截止时间">
					<ElDatePicker
						v-model="form.value1"
						type="daterange"
						value-format="YYYY-MM-DD"
						range-separator="To"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="4">
				<ElButton :icon="Search" type="primary" @click="onSearch"> 搜索 </ElButton>
				<ElButton :icon="Refresh" @click="onReset"> 重置 </ElButton>
			</ElCol>
		</ElRow>
	</ElForm>
</template>
<script setup lang="tsx">
import { Search, Refresh } from "@element-plus/icons-vue";
import { ref, watch } from "vue";
import { ElCol, ElButton, ElRow, ElForm, ElFormItem, ElDatePicker, ElSelect, ElOption, ElInput } from "element-plus";
import { appInstance } from "@/main";

const props = defineProps({
	projectList: {
		type: Array,
		default: []
	},
	userList: {
		type: Array,
		default: []
	},
	userId: {
		type: String,
		default: ''
	}
});
//1待指派/2待开始/3进行中/4已完成/5已挂起
const statusList = [
	{
		value: 1,
		label: "待指派"
	},
	{
		value: 2,
		label: "待开始"
	},
	{
		value: 3,
		label: "进行中"
	},
	{
		value: 4,
		label: "已完成"
	},
	{
		value: 5,
		label: "已挂起"
	},
	{
		value: 6,
		label: "已暂停"
	}
];

let form = ref({
	assigned_to_user_id: props.userId
});

watch(() => props.userId, () => {
	form.value.assigned_to_user_id = props.userId
})

const emit = defineEmits(["onSearch", "onReset", "changeUserId"]);
const changeUser = (val) => {
	emit('changeUserId', val)
}
const onSearch = () => {
	form.value.completionTimeStart = form.value.value1?.[0]
	form.value.completionTimeEnd = form.value.value1?.[1]
	emit("onSearch", form.value);
};
const onReset = () => {
	form.value = {assigned_to_user_id: props.userId}
	emit("onReset", form.value);
};
defineExpose({
	onSearch,
	
})
</script>