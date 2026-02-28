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
				type: "ElSelect",
				prop: "transfer_to_user_id",
				span: 12,
				attrs: {
					filterable: true,
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						let data = []
						if(props.index == 4) {
							const res = await getUserList({
								pageNo: 1,
								pageSize: 100,
							});
							data = res.records
						} else {
							data = await projectUserList({
								projectId: props.row.project_id || props.row.pro_project_id
							});
						}
						console.log('2222222222222val', data)
						return data.filter(val => val.id != userStore.userInfo.sysUser.id);
					},
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
