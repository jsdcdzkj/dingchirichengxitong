<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { projectUserList } from "@/api/project/list";
	import { createQuiklyRapid, editPlan, PlanType, selectPlanDetail } from "@/api/project/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";

	import { number, oneOfType, string } from "vue-types";

	const props = defineProps({
		id: number().def(),
		iterationId: oneOfType([number(), string()]).def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "阶段标题",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "负责人",
				value: "",
				type: "ElSelect",
				prop: "person_in_charge",
				span: 12,
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
				label: "计划时间",
				value: [],
				type: "ElDatePicker",
				prop: "plan_start_date",
				span: 12,
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "时间",
					valueFormat: "YYYY-MM-DD"
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
			person_in_charge: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			projectStartTime: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});
	registerFormDone(async () => {
		const [plan_start_date, plan_end_date] = form.value.plan_start_date;
		const data = {
			plan_type: PlanType.PLAN_GROUP,
			plan_end_date,
			plan_start_date,
			plan_title: form.value.plan_title,
			project_id: appInstance.projectItem.id,
			person_in_charge: form.value.person_in_charge,
			iteration_id: props.iterationId
		};
		if (!props.id) {
			await createQuiklyRapid(data);
		} else {
			await editPlan(Object.assign(data, { id: props.id }));
		}
		return form.value;
	});

	if (props.id) {
		selectPlanDetail({ id: props.id }).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			// 时间区间
			form.value.plan_start_date = [res.plan_start_date, res.plan_end_date];
		});
	}
</script>
