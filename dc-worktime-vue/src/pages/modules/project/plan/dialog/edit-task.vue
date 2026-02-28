<template>
	<div class="flex">
		<div>
			<BaseForm v-bind="form" />
			<ElDivider class="m-t-10px m-b-10px" />
			<div class="title m-t-10px m-b-10px">详情</div>
			<BaseForm v-bind="form2" />
			<ElDivider class="m-t-10px m-b-10px" />
			<div class="title m-t-10px m-b-10px">关联内容</div>
			<BaseForm v-bind="form4" />
			<ElDivider class="m-t-10px m-b-10px" />
			<div class="title m-t-10px m-b-10px">工时</div>
			<BaseForm v-bind="form3" />
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { getEntity, taskSaveOrUpdate } from "@/api/project/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import { ElDivider } from "element-plus";
	import { number, oneOfType, string } from "vue-types";
	import { selectPlanDetail, selectPlanTree } from "@/api/project/plan";
	import { ref } from "vue";
	import { projectUserList } from "@/api/project/list";
	const props = defineProps({
		id: number().def(),
		planId: number().def(),
		iterationId: oneOfType([number(), string()]).def(),
		groupId: number().def(),
		limitStartTime: string().def(),
		limitEneTime: string().def()
	});

	const limitStartTime = ref(props.limitStartTime);
	const limitEneTime = ref(props.limitEneTime);
	const showWorkHour = ref(false);

	const disabledDate = (time: Date) => {
		return time.getTime() > new Date(limitEneTime.value).getTime();
	};

	const { form } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "标题",
				prop: "title",
				value: "",
				type: "ElInput",
				span: 16,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "任务类型",
				value: "",
				type: "ElSelect",
				prop: "task_attribute",
				span: 8,
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					// 任务类型: 1.售前 2.前端 3.后端 4 UI 5 测试 6 产品"
					list: appInstance.mapDict.task_attribute
				},
				attrs: {
					// onChange: (val: any) => {
					// 	showWorkHour.value = val == 5 || val == 3;
					// },
					disabled: true
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

			task_attribute: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	const { form: form2 } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "描述",
				value: "<p><br></p>",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
			{
				label: "负责人",
				value: "",
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 8,
				select: {
					type: "ElOption",
					label: "username",

					value: "id",
					listApi: async () => {
						const data = await projectUserList({ projectId: appInstance.projectItem.id });
						return data;
					},
					list: []
				},
				attrs: {
					filterable: true
				}
			},
			{
				label: "优先级",
				value: 1,
				type: "ElSelect",
				prop: "priority",
				span: 8,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.priority
				}
			},
			{
				label: "难易程度",
				value: 1,
				type: "ElSelect",
				prop: "degree_of_difficulty",
				span: 8,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.degree_of_difficulty
				}
			},
			{
				label: "截止时间",
				value: "",
				type: "ElDatePicker",
				prop: "end_time",
				span: 8,
				attrs: {
					valueFormat: "YYYY-MM-DD",
					placeholder: "请选择"
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
			],
			assigned_to_user_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			priority: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			plan_end_date: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			task_attribute: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	const { form: form3, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "预估工时（分钟）",
				value: 30,
				type: "ElInputNumber",
				prop: "planing_hours",
				span: 12,
				attrs: {
					min: 0
				}
			}
		],
		rules: {
			planing_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	const { form: form4 } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "关联计划",
				value: props.planId,
				type: "ElCascader",
				prop: "planId",
				span: 12,
				select: {
					type: "ElOption",
					listApi: async () => {
						const data = await selectPlanTree({
							project_id: appInstance.projectItem.id,
							iteration_id: props.iterationId
						});
						return data;
					}
				},
				attrs: {
					options: [],
					props: {
						label: "plan_title",
						value: "id",
						checkStrictly: true
					},
					disabled: true,
					onChange: (val: any) => {
						selectPlanDetail({ id: val.slice(-1)[0] }).then((res) => {
							const { plan_start_date, plan_end_date } = res;
							limitEneTime.value = plan_end_date;
							limitStartTime.value = plan_start_date;
							form2.value.plan_end_date = "";
						});
					}
				}
			}
		],
		rules: {
			planing_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		const data = Object.assign(
			{},
			{
				// ...form.value,
				// ...form2.value,
				// ...form3.value,
				// parent_id:
				// 	form4.value.parent_id instanceof Array ? form4.value.parent_id.slice(-1)[0] : form4.value.parent_id,
				// // parent_id: appInstance.projectItem.id,
				// project_id: appInstance.projectItem.id,
				// functionIds: treeRef.value?.getCheckedKeys(true),
				// iteration_id: props.iterationId,
				// work_hours: showWorkHour.value ? form3.value.work_hours : 0
				...form.value,
				...form2.value,
				...form3.value,
				...form4.value,
				end_time: form2.value.end_time + " 23:59:59",
				planId: form4.value.planId instanceof Array ? form4.value.planId.slice(-1)[0] : form4.value.planId,
				project_id: appInstance.projectItem.id
			},
			props.id ? { id: props.id } : {}
		);
		await taskSaveOrUpdate(data);
		return form.value;
	});
	if (props.id) {
		getEntity({ id: props.id }).then((res) => {
			// 赋值
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			Object.keys(form2.value).forEach((key) => {
				form2.value[key] = res[key];
			});
			Object.keys(form4.value).forEach((key) => {
				form4.value[key] = res[key];
			});
			Object.keys(form3.value).forEach((key) => {
				form3.value[key] = res[key];
			});
			// 权重
			// form2.value.priority = res.priority + "";
			// 难度
			// form2.value.degree_of_difficulty = res.degree_of_difficulty + "";
			// form3.value.working_hours = Number(res.working_hours);
			// form.value.task_attribute = res.task_attribute + "";
			// showWorkHour.value = res.task_attribute == 5 || res.task_attribute == 3;
			// 时间
			// const { plan_start_date, plan_end_date } = res;
			// limitEneTime.value = plan_end_date;
			// limitStartTime.value = plan_start_date;

			form2.value.degree_of_difficulty = Number(res.degree_of_difficulty);
			form2.value.priority = Number(res.priority);
			form.value.task_attribute = Number(res.task_attribute);
		});
	}
</script>
<style lang="scss" scoped>
	.tree-label {
		display: inline-block;
		max-width: 190px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(90deg, rgba(27, 134, 255, 0.1) 0%, rgba(27, 134, 255, 0) 100%);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}

	.select-item {
		height: 40px;
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 0 10px;
		background: #f8f8f8;
		border-radius: 4px 4px 4px 4px;
	}
	.drawer-container-main {
		overflow-y: hidden;
	}
</style>
