<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { getEntityById, saveOrUpd } from "@/api/project/milestone";
	import { getDeveloper, PlanType, selectPlanDetail, selectPlanTree } from "@/api/project/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import { ref } from "vue";
	import dayjs from "dayjs";

	import { number, oneOfType, string } from "vue-types";

	const props = defineProps({
		id: number().def(),
		iterationId: oneOfType([number(), string()]).def(),
		groupId: number().def(),
		limitStartTime: string().def(),
		limitEneTime: string().def()
	});

	const limitStartTime = ref(props.limitStartTime);
	const limitEneTime = ref(props.limitEneTime);

	const list = ref([]);

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "标题",
				prop: "milestoneName",
				value: "",
				type: "ElInput",
				span: 12,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "负责人",
				value: [],
				type: "ElSelect",
				prop: "personCharges",
				span: 12,
				attrs: {
					filterable: true,
					multiple: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getDeveloper();
						return data;
					},
					list: []
				}
			},
			// {
			// 	label: "所属阶段",
			// 	value: "",
			// 	type: "ElCascader",
			// 	prop: "planId",
			// 	span: 12,
			// 	select: {
			// 		type: "ElOption",
			// 		listApi: async () => {
			// 			const data = await selectPlanTree({
			// 				project_id: appInstance.projectItem.id
			// 			});
			// 			list.value = data.reduce((list, item) => {
			// 				list.push(item);
			// 				if (item.children && item.children.length > 0) {
			// 					for (let i = 0; i < item.children.length; i++) {
			// 						const element = item.children[i];
			// 						list.push(element);
			// 					}
			// 				}
			// 				return list;
			// 			}, []);
			// 			// console.log(list.value);

			// 			return data;
			// 		}
			// 	},
			// 	attrs: {
			// 		options: [],
			// 		props: {
			// 			label: "plan_title",
			// 			value: "id",
			// 			checkStrictly: true
			// 		}
			// 		// onChange: (e) => {
			// 		// 	console.log(e);
			// 		// }
			// 	}
			// },
			{
				label: "所属阶段",
				value: props.groupId,
				type: "ElSelect",
				prop: "groupId",
				span: 12,
				select: {
					type: "ElOption",
					label: "plan_title",
					value: "id",
					listApi: async () => {
						const data = await selectPlanTree({
							project_id: appInstance.projectItem.id,
							iteration_id: props.iterationId
						});
						return data.filter((item: any) => item.plan_type == PlanType.PLAN_GROUP);
					},
					list: []
				},
				attrs: {
					onChange: (val: any) => {
						selectPlanDetail({ id: val }).then((res) => {
							const { plan_start_date, plan_end_date } = res;
							limitEneTime.value = plan_end_date;
							limitStartTime.value = plan_start_date;
							form.value.appointFinishTime = "";
						});
					}
				}
			},
			{
				label: "约定结束日期",
				value: "",
				type: "ElDatePicker",
				prop: "appointFinishTime",
				span: 12,
				attrs: {
					placeholder: "约定结束日期",
					valueFormat: "YYYY-MM-DD",
					disabledDate: (time: Date) => {
						const timeT = new Date(dayjs(time).format("YYYY-MM-DD")).getTime();
						// const timeT = time.getTime();
						if (limitStartTime.value && limitEneTime.value) {
							const lStart = new Date(limitStartTime.value).getTime();
							const lEnd = new Date(limitEneTime.value).getTime();
							return timeT < lStart || timeT > lEnd;
						} else if (limitStartTime.value) {
							const lStart = new Date(limitStartTime.value).getTime();
							return timeT < lStart;
						} else if (limitEneTime.value) {
							const lEnd = new Date(limitEneTime.value).getTime();
							return timeT > lEnd;
						}
						return false;
					}
				}
			},

			{
				label: "评审阶段",
				value: 1,
				type: "ElRadioGroup",
				prop: "groupType",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "需求阶段",
							value: 1
						},
						{
							label: "设计阶段",
							value: 2
						},
						{
							label: "开发阶段",
							value: 3
						},
						// {
						// 	label: "测试阶段",
						// 	value: 4
						// },
						// {
						// 	label: "上线阶段",
						// 	value: 5
						// },
						{
							label: "内部评审阶段",
							value: 4
						}
					]
				}
			},
			{
				label: "描述",
				value: "<p><br></p>",
				type: "Editor",
				prop: "description",
				span: 24,

				attrs: {
					style: { height: "300px", width: "100%" },
					contentType: "html"
				}
			}
		],
		rules: {
			groupId: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			personCharges: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			milestoneName: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			appointFinishTime: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			groupType: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});
	registerFormDone(async () => {
		const planId = form.value.planId instanceof Array ? form.value.planId : [form.value.planId];
		const fList = list.value.filter((item) => item.id == planId.slice(-1)[0]);
		await saveOrUpd(
			Object.assign(
				{},
				{
					...form.value,
					iteration_id: props.iterationId,
					// groupId: planId.length == 0 ? null : planId.length == 1 ? null : planId[0],
					planId: planId.length == 0 ? null : planId.length == 1 ? planId[0] : planId[1],
					planType: fList.length > 0 ? fList[0].plan_type : null,
					projectId: appInstance.projectItem.id,
					personCharges: form.value.personCharges.join(",")
				},
				props.id ? { id: props.id } : {}
			)
		);

		return form.value;
	});

	if (props.id) {
		getEntityById({ id: props.id }).then((res) => {
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});

			form.value.personCharges = res.personCharges.split(",").map(Number);
		});
	}
</script>
