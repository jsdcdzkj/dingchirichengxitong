<template>
	<div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import { auditPublish } from "@/api/project/release-record"

	const props = defineProps({
		id: {
			type: Number,
			default: 0
		},
	})

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "审核结果",
				value: "",
				type: "ElRadioGroup",
				prop: "state",
				span: 12,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "通过",
							value: "4"
						},
						{
							label: "驳回",
							value: "2"
						}
					]
				}
			},
			{
				label: "审核意见",
				value: "",
				type: "Editor",
				prop: "auditOpinion",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
		],
		rules: {
			auditOpinion: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			state: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
		}
	});

	registerFormDone(async() => {
		await auditPublish({...form.value, id: props.id})
		return form.value;
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
