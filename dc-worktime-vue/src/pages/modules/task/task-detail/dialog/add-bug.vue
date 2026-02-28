<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { PlanType, selectPlanTree } from "@/api/project/plan";
	import { appInstance } from "@/main";
	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		className: "flex-1",
		formItems: [
			{
				label: "所属模块",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "功能名称",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "是否是阻塞性BUG",
				prop: "plan_title",
				value: "",
				type: "ElRadioGroup",
				span: 12,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "是",
							value: "1"
						},
						{
							label: "否",
							value: "0"
						}
					]
				}
			},
			{
				label: "bug类型",
				value: 1,
				type: "ElSelect",
				prop: "person_in_charge",
				span: 12
			},
			{
				label: "指派至",
				prop: "plan_title",
				value: "",
				type: "ElRadioGroup",
				span: 12,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "是",
							value: "1"
						},
						{
							label: "否",
							value: "0"
						}
					]
				}
			},
			{
				label: "截止日期",
				value: "",
				type: "ElDatePicker",
				prop: "plan_start_date",
				span: 12,
				attrs: {
					type: "date"
				}
			},
			{
				label: "BUG标题",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 24
			},
			{
				label: "描述",
				value: "",
				type: "Editor",
				prop: "plan_desc",
				span: 24,
				attrs: {
					style: { height: "300px", width: "100%" },
					contentType: "html"
				}
			}
		],
		rules: {
			plan_title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			plan_desc: [
				{
					required: true,
					message: "请输入",
					trigger: "change",
					validator: (rule, value, callback) => {
						if (value === "<p><br></p>") {
							return callback(new Error("请输入"));
						}
						callback();
					}
				}
			],
			person_in_charge: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			plan_start_date: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	registerFormDone(async () => {
		return form.value;
	});
</script>
