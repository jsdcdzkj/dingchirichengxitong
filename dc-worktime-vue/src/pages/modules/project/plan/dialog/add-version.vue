<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import { projectUserList } from "@/api/project/list";
	import { addOrUpdateIteration, getDeveloper } from "@/api/project/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";

	import { object } from "vue-types";

	const props = defineProps({
		item: object().def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "迭代版本名称",
				prop: "iteration_name",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "请选择负责人",
				value: "",
				type: "ElSelect",
				prop: "person_in_charge",
				span: 24,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await projectUserList({
							projectId: appInstance.projectItem.id
						});
						return data;
					},
					list: []
				}
			},
			{
				label: "是否使用项目计划标准管理模版",
				value: 1,
				type: "ElRadioGroup",
				prop: "is_template",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "是",
							value: 1
						},
						{
							label: "否",
							value: 0
						}
					]
				}
			}
		],
		rules: {
			iteration_name: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			person_in_charge: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			is_template: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});
	registerFormDone(async () => {
		// if (!props.id) {
		await addOrUpdateIteration(
			Object.assign(
				{},
				{ ...form.value, project_id: appInstance.projectItem.id },
				props.item ? { id: props.item.id } : {}
			)
		);
		// } else {
		// 	await editPlan(Object.assign(data, { id: props.id }));
		// }
		return form.value;
	});

	// if (props.id) {
	// 	selectPlanDetail({ id: props.id }).then((res) => {
	// 		// form.value = mergeProps(form.value, res);
	// 		Object.keys(form.value).forEach((key) => {
	// 			form.value[key] = res[key];
	// 		});
	// 		// 时间区间
	// 		form.value.plan_start_date = [res.plan_start_date, res.plan_end_date];
	// 	});
	// }
	if (props.item) {
		Object.keys(form.value).forEach((key) => {
			form.value[key] = props.item[key];
		});
	}
</script>
