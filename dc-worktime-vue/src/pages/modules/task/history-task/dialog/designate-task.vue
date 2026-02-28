<template>
	<div>
		<div class="title m-b-24px">任务分配信息</div>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="任务名称：">
						{{ detailInfo.name }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="项目名称：">
						{{ detailInfo.project_name}}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="截止日期：">
						{{ detailInfo.end_time }}
					</ElDescriptionsItem>
					<!-- <ElDescriptionsItem label="关联功能：">
					</ElDescriptionsItem> -->
					<ElDescriptionsItem label="任务描述：">
						<div style="display:flex">
							<div class="desc-img" v-html="detailInfo.description"></div>
						</div>
					</ElDescriptionsItem>
				</ElDescriptions>
			</ElCol>
		</ElRow>
		<BaseForm v-bind="form" class="m-t-12px"></BaseForm>
	</div>

</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { designate, getEntityOperation } from "@/api/task/operation-maintenance";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDescriptions, ElDescriptionsItem, ElRate } from "element-plus";
	import { toRaw, ref } from "vue";
	import { number } from "vue-types";
	import { projectUserList } from "@/api/project/list";

	const props = defineProps({
		id: number().def(),
		projectId: {
			type: Number,
			default: 0
		},
		row: {
			type: Object,
			default: ({})
		},
	});
	const detailInfo = ref({})
	if (props.id) {
		getEntityOperation(props.id).then((res) => {
			detailInfo.value = res
		});
	}

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 88,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "left",
		formItems: [
			{
				label: "指派至",
				value: props.row.assigned_user_id,
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 24,
				attrs: {
					filterable: true,
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await projectUserList({
							projectId: props.projectId
						});
						return data;
					},
					list: []
				}
			},
		],
		rules: {
			assigned_to_user_id: [
				{
					required: true,
					message: "请输入",
					trigger: "change"
				}
			]
		}
	});

	registerFormDone(async () => {
		await designate({
				assigned_to_user_id: form.value.assigned_to_user_id,
				id: props.id,
				status: 2,
			})
		return true;
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
