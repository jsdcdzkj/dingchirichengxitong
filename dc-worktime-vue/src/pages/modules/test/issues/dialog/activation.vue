<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="110px">
		<ElRow>
			<ElCol :span="24">
				<ElFormItem label="指派至">
					<!-- <ElCheckboxGroup v-model="assigned_to_user_type" disabled>
						<ElCheckbox :value="option.value" v-for="option in userOptions" :key="option.value">
							{{ option.label }}
						</ElCheckbox>
					</ElCheckboxGroup> -->
					<ElRadioGroup v-model="form.assigned_to_user_type">
						<ElRadio :value="1">前端</ElRadio>
						<ElRadio :value="2">后端</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>
			<ElCol :span="8">
				<ElFormItem label="截止日期" prop="end_time">
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
			<ElCol :span="24">
				<ElFormItem label="激活原因描述" prop="descriptionHistory">
					<Editor v-model="form.descriptionHistory" contentType="html" style="width: 100%; height: 300px" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw, computed } from "vue";
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
	import { activateTask } from "@/api/test/issues";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		}
	});
	// const assigned_to_user_type = ref<any[]>([]);
	const form = reactive({
		assigned_to_user_type: "",
		end_time: "",
		descriptionHistory: ""
	});
	const rules = reactive({
		end_time: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		descriptionHistory: [
			{
				required: true,
				message: "请输入激活原因描述",
				trigger: "blur"
			}
		]
	});
	// 禁用当前日期之前的日期
	const disabledDate = (date: Date) => {
		return date < new Date(new Date().setHours(0, 0, 0, 0));
	};

	// 1：前端 2：后端 3：部署 4：测试
	const userOptions = ref<any[]>([
		{
			label: "前端",
			value: 1
		},
		{
			label: "后端",
			value: 2
		}
	]);

	const { registerFormDone, formInstance } = useDialogForm();

	const defaultTime = computed(() => {
		const time = new Date();
		time.setHours(23, 59, 59, 0); // 设置时分秒为 23:59:59
		return [time];
	});
	// 初始化组员列表
	onMounted(async () => {
		form.assigned_to_user_type = props.rowInfo.assigned_to_user_type;
		// form.end_time = props.rowInfo.end_time;
	});

	registerFormDone(async () => {
		console.log("form", form);
		await activateTask(
			Object.assign({}, toRaw(form), {
				id: props.rowInfo.id,
				module_id: props.rowInfo.module_id,
				pro_feature_id: props.rowInfo.pro_feature_id
			})
		);
		props.refreshList();
		return true;
	});
</script>
