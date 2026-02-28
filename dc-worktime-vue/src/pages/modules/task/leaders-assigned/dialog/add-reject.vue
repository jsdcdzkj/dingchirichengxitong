<template>
	<div class="score-wrap">
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"><Warning color="#F56C6C"/></ElIcon>
			请领导为被指派人的工作质量结果进行评价!
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { ElDivider } from "element-plus";
	import {
		Warning
	} from "@element-plus/icons-vue";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { toRaw } from "vue";
	import { number } from "vue-types";
	import {taskRollback} from "@/api/task/leaders-assigned"
	import { appInstance } from "@/main";

	const props = defineProps({
		id: number().def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "打回意见",
				value: "",
				type: "ElInput",
				prop: "call_back_opinion",
				span: 24,
			},
			{
				label: "补充描述",
				value: "",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
			{
				label: "补充附件",
				value: [],
				type: "ElUpload",
				prop: "fileList",
				span: 24,
				attrs: {
					action: `/api/project/uploadFile`,
					placeholder: "",
					multiple: true,
					name: "multipartFile",
					headers: {
						JsdcToken: appInstance.loginToken
					}
				}
			}
		],
		rules: {
			description: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		await taskRollback(
			Object.assign({}, toRaw(form.value), {
				fileList: form.value.fileList.map((item: any) => item.response.data),
				task_leaders_id: props.id
			})
		);
		return true;
	});
</script>
<style lang="scss" scoped>
.score-wrap {
	font-size: 14px;
	color: rgba(0,0,0,0.85);
	line-height: 22px;
}
</style>