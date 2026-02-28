<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="110px">
		<ElRow>
			<ElCol :span="8">
				<ElFormItem label="责任人" prop="assigned_to_user_id">
					<ElSelect v-model="form.assigned_to_user_id" placeholder="请选择" clearable>
						<template v-for="option in props.userList" :key="option.id">
							<ElOption :label="option.username" :value="option.id"></ElOption>
						</template>
					</ElSelect>
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
				<ElFormItem label="问题补充描述" prop="descriptionHistory">
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
	import { noStartTask, getTypeUserList } from "@/api/test/issues";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		},
		userList: {
			type: Array,
			default: () => []
		}
	});
	// const assigned_to_user_type = ref<any[]>([]);
	const form = reactive({
		assigned_to_user_type: "",
		end_time: "",
		descriptionHistory: "",
		assigned_to_user_id: ""
	});
	const rules = reactive({
		end_time: [
			{
				required: true,
				message: "请选择截止时间",
				trigger: "blur"
			}
		],
		assigned_to_user_id: [
			{
				required: true,
				message: "请选择责任人",
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
	const userList = ref<any[]>([]);
	// 初始化组员列表
	onMounted(async () => {
		const data2 = await getTypeUserList({ type: "3" });
		userList.value = data2;

		form.assigned_to_user_type = props.rowInfo.assigned_to_user_type;
		form.assigned_to_user_id = props.rowInfo.assigned_to_user_id;

		// form.end_time = props.rowInfo.end_time;
	});

	registerFormDone(async () => {
		console.log("form", form);
		await noStartTask(
			Object.assign({}, toRaw(form), {
				id: props.rowInfo.id
			})
		);
		props.refreshList();
		return true;
	});
</script>
