<template>
	<div>
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"><Warning color="#F56C6C"/></ElIcon>
			当前时间，任务工时{{ time }}。提交后测试人员讲进行复测，是否确定提交任务解决？
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { completeTaskBug } from "@/api/task/kanban";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import {
		Warning
	} from "@element-plus/icons-vue";
	import { toRaw } from "vue";
	import { number } from "vue-types";

	const props = defineProps({
		id: number().def(),
		time: {
			type: String,
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
				label: "解决类型",
				value: "1",
				type: "ElRadioGroup",
				prop: "states",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "已解决",
							value: "1"
						},
						{
							label: "未复现",
							value: "2"
						},
						{
							label: "不是我的",
							value: "3"
						},
					]
				}
			},
			{
				label: "解决说明",
				value: "",
				type: "Editor",
				prop: "content",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
		],
		rules: {
			states: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			content: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {

		await completeTaskBug(
			Object.assign({}, toRaw(form.value), {
				id: props.id
			})
		);
		return true;
	});
</script>
