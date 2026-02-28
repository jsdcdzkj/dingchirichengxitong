<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="86px">
		<ElRow>
			<ElCol :span="8">
				<ElFormItem label="BUG来源：" prop="source">
					<!-- bug来源 1.任务BUG 2.评审BUG -->
					<span v-if="form.source == '1'">任务BUG</span>
					<span v-if="form.source == '2'">评审BUG</span>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="项目名称" prop="source">
					<span>项目名称</span>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="截止日期：" prop="end_time">
					<span>{{ form.end_time }}</span>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="关联功能：" prop="end_time">
					<span>项目名称</span>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="BUG标题：" prop="bug_title">
					<span>{{ form.bug_title }}</span>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="严重程度：" prop="severity">
					<!-- 1:轻微bug 2：一般bug 3：重大bug -->
					<span v-if="form.severity == '1'">轻微bug</span>
					<span v-if="form.severity == '2'">一般bug</span>
					<span v-if="form.severity == '3'">重大bug</span>
				</ElFormItem>
			</ElCol>

			<ElCol :span="24">
				<ElFormItem label="问题描述：" prop="description">
					<Editor v-model="form.description" style="width: 100%" />
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="指派至：" prop="assigned_to_user_id">
					<ElRadioGroup v-model="form.assigned_to_user_id">
						<ElRadio :label="1">前端</ElRadio>
						<ElRadio :label="2">后端</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw } from "vue";
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
		ElCol
	} from "element-plus";
	import { audit } from "@/api/test/issues";

	const props = defineProps({
		moudleList: {
			type: Array,
			default: () => []
		},
		projectId: {
			type: String,
			default: ""
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});
	const form = reactive({
		bug_title: "",
		module_id: "",
		pro_feature_id: [],
		assigned_to_user_id: "",
		end_time: "",
		severity: "",
		source: "",
		description: ""
	});
	const rules = reactive({});

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化组员列表
	onMounted(async () => {
		console.log("props.rowInfo", props.rowInfo);
		for (const key in props.rowInfo) {
			if (Object.prototype.hasOwnProperty.call(props.rowInfo, key)) {
				// 确保 form 对象有对应的属性
				if (Object.prototype.hasOwnProperty.call(form, key)) {
					form[key] = props.rowInfo[key];
				}
			}
		}
	});

	registerFormDone(async () => {
		console.log("form", form);
		await audit(
			Object.assign({}, toRaw(form), {
				pro_project_id: 1
			})
		);
		return true;
	});
</script>
