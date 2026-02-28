<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="86px" label-position="top" class="p-r-10px">
		<ElRow :gutter="10">
			<ElCol :span="24">
				<ElFormItem label="标题" prop="bug_title">
					<ElInput v-model="form.bug_title" placeholder="请输入标题" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="所属模块" prop="moduel_name">
					<ElInput v-model="form.moduel_name" placeholder="请选择所属模块" disabled />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="功能名称" prop="featureName">
					<ElInput v-model="form.featureName" placeholder="请选择功能名称" disabled />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="指派至" prop="assigned_to_user_type">
					<ElRadioGroup v-model="form.assigned_to_user_type" @change="changeUserType">
						<ElRadio :value="1">前端</ElRadio>
						<ElRadio :value="2">后端</ElRadio>
						<ElRadio :value="3">UI</ElRadio>
						<ElRadio :value="4">产品</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8" v-if="form.assigned_to_user_type == 4">
				<ElFormItem label="负责人" prop="assigned_to_user_id">
					<ElSelect v-model="form.assigned_to_user_id" key="product" placeholder="请选择" clearable filterable>
						<ElOption
							v-for="item in productList"
							:key="item.id"
							:label="item.userName"
							:value="item.id"
						></ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8" v-if="form.assigned_to_user_type == 3">
				<ElFormItem label="负责人" prop="assigned_to_user_id">
					<ElSelect v-model="form.assigned_to_user_id" key="ui" placeholder="请选择" clearable filterable>
						<ElOption
							v-for="item in uiList"
							:key="item.id"
							:label="item.userName"
							:value="item.id"
						></ElOption>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="截止时间" prop="end_time">
					<ElDatePicker
						v-model="form.end_time"
						type="date"
						format="YYYY-MM-DD"
						value-format="YYYY-MM-DD"
						:disabled-date="disabledDate"
						placeholder="选择日期"
						style="width: 100%"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="严重程度" prop="severity">
					<ElSelect v-model="form.severity" placeholder="请选择">
						<template v-for="option in severityList" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="来源" prop="source">
					<ElSelect v-model="form.source" placeholder="请选择">
						<template v-for="option in sourceOption" :key="option.value">
							<ElOption :label="option.label" :value="option.value"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="问题描述" prop="description">
					<Editor
						v-model="form.description"
						contentType="html"
						style="width: 100%; height: calc(100vh - 540px)"
					/>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw, computed, nextTick } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElCheckbox,
		ElCheckboxGroup,
		ElRadioGroup,
		ElRadio,
		ElDatePicker,
		ElSelect,
		ElOption,
		ElRow,
		ElCol,
		ElMessage
	} from "element-plus";
	import { addUpdateInfo } from "@/api/test/issues";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import {getTypeUserList} from "@/api/test/issues";
	import { useUserStore } from "@/store/user.ts";

	const props = defineProps({
		moudleList: {
			type: [Array, Object],
			default: () => []
		},
		projectId: {
			type: Number,
			default: 0
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		refreshSearch: {
			type: Function,
			default: () => () => {}
		},
	});
	const form = reactive({
		bug_title: "",
		module_id: "",
		pro_feature_id: "",
		featureName: "",
		module_name: "",
		// assigned_to_user_types: [],
		assigned_to_user_type: "",
		assigned_to_user_id: "",
		end_time: "",
		severity: "",
		source: 3,
		description: "",
		status: 1
	});
	const rules = reactive({
		bug_title: [
			{
				required: true,
				message: "请输入标题",
				trigger: "blur"
			}
		],
		module_id: [
			{
				required: true,
				message: "请选择所属模块",
				trigger: "blur"
			}
		],
		pro_feature_id: [
			{
				required: true,
				message: "请选择功能名称",
				trigger: "blur"
			}
		],
		assigned_to_user_type: [
			{
				required: true,
				message: "请选择指派至",
				trigger: "change"
			}
		],
		assigned_to_user_id: [
			{
				required: true,
				message: "请选择负责人",
				trigger: "change"
			}
		],
		end_time: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		severity: [
			{
				required: true,
				message: "请选择严重程度",
				trigger: "change"
			}
		],
		source: [
			{
				required: true,
				message: "请选择来源",
				trigger: "change"
			}
		],
		description: [
			{
				required: true,
				message: "请输入问题描述",
				trigger: "blur"
			}
		],
	});

	const userStore = useUserStore();
	const productList = ref([])
	const uiList = ref([])
	const getUerList = async () => {
		const productUser = await getTypeUserList({
			pageNo: 1,
			pageSize: 100,
			type: 3,
		});
		productList.value = productUser.filter(val => val.id != userStore.userInfo.sysUser.id);
		const data = await getTypeUserList({
			pageNo: 1,
			pageSize: 100,
			type: 4,
		});
		uiList.value = data.filter(val => val.id != userStore.userInfo.sysUser.id);
	}
	getUerList()
	// 改变指派人类型
	const changeUserType = (val) => {
		form.assigned_to_user_id = ''
	}
	// severityList 1:轻微bug 2：一般bug 3：重大bug
	const severityList = ref<any[]>([
		{
			label: "轻微",
			value: 1
		},
		{
			label: "一般",
			value: 2
		},
		{
			label: "严重",
			value: 3
		}
	]);

	// bug来源 1.任务BUG 2.评审BUG sourceOption
	const sourceOption = ref<any[]>([
		{
			label: "任务",
			value: 1
		},
		{
			label: "联调",
			value: 3
		}
	]);
	// 禁用当前日期之前的日期
	const disabledDate = (date: Date) => {
		return date < new Date(new Date().setHours(0, 0, 0, 0));
	};

	const { registerFormDone, formInstance } = useDialogForm();

	const defaultTime = computed(() => {
		const time = new Date();
		time.setHours(23, 59, 59, 0); // 设置时分秒为 23:59:59
		return [time];
	});
	// 初始化组员列表
	onMounted(async () => {
		if (props.rowInfo && props.rowInfo.id) {
			const { moduel_id, feature_id, end_time, featureName, moduel_name} = props.rowInfo || {}
			form.bug_title = ''
		    form.module_id = moduel_id
		    form.pro_feature_id = feature_id
			form.featureName = featureName
			form.moduel_name = moduel_name
		}
	});

	registerFormDone(async () => {
		const response = await addUpdateInfo(
			Object.assign({}, toRaw(form), {
				pro_project_id: props.projectId
			})
		);
		
		if (response && response.code === 500) {
			// 显示错误信息
			ElMessage.error(response.msg || "请求发生错误");
			return false;
		} else {
			ElMessage.success("提交成功");
			props.refreshSearch()
			return true;
		}
	});
</script>
