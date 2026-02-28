<template>
	<div>
		<div class="title m-t-10px m-b-24px">任务转移信息</div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { projectUserList } from "@/api/project/list";
	import { taskTransfer } from "@/api/task/kanban";
	import { getUserList } from "@/api/common/login";
	import { useUserStore } from "@/store/user.ts";
	import {getTypeUserList} from "@/api/test/issues";
	import { ref } from "vue";

	const userStore = useUserStore();

	const props = defineProps({
		row: {
			type: Object,
			default: {}
		},
		index: {
			type:String,
			default: ''
		}
	});

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

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "任务转移至",
				value: '',
				type: "ElRadioGroup",
				prop: "assigned_to_user_type",
				span: 12,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "前端",
							value: "1",
							disabled: [1].includes(Number(props.row.assigned_to_user_type))
						},
						{
							label: "后端",
							value: "2",
							disabled: [2].includes(Number(props.row.assigned_to_user_type))
						},
						{
							label: "产品",
							value: "4",
							disabled: props.row.assigned_to_user_type == 4
						},
						{
							label: "UI",
							value: "3",
							disabled: props.row.assigned_to_user_type == 3
						},
					]
				},
				attrs: {
					filterable: true,
					onChange: (e: number[]) => {
						form.value.transfer_to_user_id = ''
						if(e == 3 || e ==4) {
							form.formItems[1].show = true
							if (e== 3) {
								form.formItems[1].select.list = uiList.value
							} else {
								form.formItems[1].select.list = productList.value
							}
						} else {
							form.formItems[1].show = false
						}
					}
				}
			},
			{
				label: "任务转移至",
				value: '',
				type: "ElSelect",
				prop: "transfer_to_user_id",
				span: 12,
				attrs: {
					filterable: true,
					clearable: true
				},
				show: false,
				select: {
					type: "ElOption",
					label: "userName",
					value: "id",
					list: []
				}
			},
			{
				label: "转移原因",
				value: "",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
		],
		rules: {
			assigned_to_user_type: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			transfer_to_user_id: [
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
		},
	});
	
	registerFormDone(async() => {
		
		
		await taskTransfer(
			Object.assign({}, form.value, {
				task_id: props.row.id,
				type: props.index,
			})
		)
	});
</script>
<style lang="scss" scoped>
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(
			90deg,
			rgba(27, 134, 255, 0.1) 0%,
			rgba(27, 134, 255, 0) 100%
		);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}
</style>
