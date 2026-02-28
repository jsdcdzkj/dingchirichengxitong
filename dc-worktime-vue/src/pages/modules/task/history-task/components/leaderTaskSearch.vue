<template>
	<ElForm :model="form" labelWidth="100px" ref="formSearch">
		<ElRow :gutter="16">
			<ElCol :span="5">
				<ElFormItem label="标题">
					<ElInput placeholder="请输入标题" v-model="form.name" clearable @keyup.enter="onSearch"></ElInput>
				</ElFormItem>
			</ElCol>

			<ElCol :span="5">
				<ElFormItem label="提出人">
					<ElSelect v-model="form.proposer_user_id" placeholder="请选择提出人" clearable filterable>
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
				<ElFormItem label="创建人">
					<ElSelect v-model="form.createUser" placeholder="请选择创建人" clearable filterable>
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
				<ElFormItem label="紧急程度">
					<ElSelect v-model="form.priority" placeholder="请选择紧急程度" clearable filterable>
						<ElOption
							v-for="(item, index) in appInstance.mapDict.urgency"
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
                        value-format="YYYY-MM-DD"
						type="daterange"
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
<script setup lang="ts">
import { Search, Refresh } from "@element-plus/icons-vue";
import { watch, ref } from "vue";
import { ElCol, ElButton, ElRow, ElForm, ElFormItem, ElDatePicker, ElSelect, ElOption, ElInput } from "element-plus";
import { appInstance } from "@/main";
import { useUserStore } from "@/store/user.ts";

const userStore = useUserStore();

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

// 1待指派/2待开始/3进行中/4已完成/5已挂起
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
		label: "待评分"
	},
	{
		value: 5,
		label: "被打回"
	},
	{
		value: 6,
		label: "已终止"
	},
	{
		value: 7,
		label: "已评分"
	},
	{
		value: 8,
		label: "已挂起"
	},
	{
		value: 9,
		label: "已暂停"
	}
];

watch(() => props.userId, () => {
	form.value.assigned_to_user_id = props.userId
})

let form = ref({assigned_to_user_id: props.userId});
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
	form.value = {assigned_to_user_id: props.userId};
	emit("onReset", form.value);
};
defineExpose({
	onSearch
})
</script>