<template>
	<div>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<ElCard header="项目详情" class="custom-header">
					<ElDescriptions :column="3" class="custom-descri2">
						<ElDescriptionsItem label="任务标题：">
							领导交办任务
						</ElDescriptionsItem>
						<ElDescriptionsItem label="截止时间：">
							2025-09-08
						</ElDescriptionsItem>
						<ElDescriptionsItem label="任务提出人：">
							张三
						</ElDescriptionsItem>
						<ElDescriptionsItem label="预估工时：">
							12
						</ElDescriptionsItem>
						<ElDescriptionsItem label="紧急程度：">
							紧急
						</ElDescriptionsItem>
						<ElDescriptionsItem label="任务附件：">
							xxx
						</ElDescriptionsItem>
						<ElDescriptionsItem label="任务描述：">
							xxx
						</ElDescriptionsItem>
					</ElDescriptions>
				</ElCard>
			</ElCol>
		</ElRow>
		<BaseForm v-bind="form"></BaseForm>
	</div>

</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { getEntityById, saveOrUpd } from "@/api/project/list";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { toRaw } from "vue";
	import { number } from "vue-types";

	const props = defineProps({
		id: number().def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "left",
		formItems: [
			{
				label: "指派至",
				value: 1,
				type: "ElSelect",
				prop: "businessManager",
				span: 24,
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			
		],
		rules: {
			input: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		const [projectStartTime, projectEndTime] =
			form.value.projectStartTime || [];

		await saveOrUpd(
			Object.assign({}, toRaw(form.value), {
				projectEndTime,
				projectStartTime,
				files: form.value.files.map((item: any) => item.response.data),
				id: props.id
			})
		);
		return true;
	});

	if (props.id) {
		getEntityById(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			// 时间区间
			form.value.projectStartTime = [
				res.projectStartTime,
				res.projectEndTime
			];
			// 文件结构重构
			form.value.files = processFilesStruct(form.value.files);
		});
	}
</script>
