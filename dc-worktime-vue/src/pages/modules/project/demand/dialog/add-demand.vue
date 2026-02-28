<template>
	<div>
		<BaseForm v-bind="form" />
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="title m-t-10px m-b-10px">详情</div>
		<BaseForm v-bind="form2" />
	</div>
</template>

<script setup lang="ts">
import {ref} from "vue"
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import { saveOrUpd } from "@/api/project/demand"
	import { appInstance } from "@/main";
	import { projectUserList } from "@/api/project/list";
	import {
		PlanType,
		selectPlanTree
	} from "@/api/project/plan";

	const props = defineProps({
		iterationId: {
			type: String,
			default: 0,
		}
	})

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
				span: 12
			},
			{
				label: "所属项目",
				value: appInstance.projectItem.id,
				type: "ElSelect",
				prop: "projectId",
				span: 12,
				attrs: {
					disabled: true,
				},
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: appInstance.projectList
				}
			},
			{
				label: "工作项类型",
				value: 2,
				type: "ElSelect",
				prop: "workItemType",
				span: 12,
				attrs: {
					disabled: true,
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: appInstance.mapDict.work_item_type
				}
			}
		],
		rules: {
			title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			projectId: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			workItemType: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	const list = ref([]);

	const { form: form2, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "描述",
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
				label: "需求来源",
				value: "",
				type: "ElInput",
				prop: "requireSource",
				span: 24,
				attrs: {
					type: 'textarea',
					maxlength: 50,
					rows: 1,
				}
			},
			{
				label: "负责人",
				value: "",
				type: "ElSelect",
				prop: "personCharge",
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
				label: "优先级",
				value: 1,
				type: "ElSelect",
				prop: "priority",
				span: 12,
				select: {
					type: "ElOption",

					list: appInstance.mapDict.priority
				}
			},
			{
				label: "所属计划",
				value: "",
				type: "ElCascader",
				prop: "planId",
				span: 12,
				select: {
					type: "ElOption",
					listApi: async () => {
						const data = await selectPlanTree({
							project_id: appInstance.projectItem.id,
							iteration_id: props.iterationId,
						});
						list.value = data.reduce((list, item) => {
							list.push(item);
							if (item.children && item.children.length > 0) {
								for (let i = 0; i < item.children.length; i++) {
									const element = item.children[i];
									list.push(element);
								}
							}
							return list;
						}, []);
						console.log('list.value1111111111111', list.value);

						return data;
					}
				},
				attrs: {
					options: [],
					props: {
						label: "plan_title",
						value: "id",
						checkStrictly: true
					}
				}
			},
		],
		rules: {
			description: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			personCharge: [
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
		}
	});

	registerFormDone(async() => {
		const planId = form2.value.planId instanceof Array ? form2.value.planId : [form2.value.planId];
		const fList = list.value.filter((item) => item.id == planId.slice(-1)[0]);
		console.log('99999999999fList', fList, planId)
		await saveOrUpd({
			...form.value,
			...form2.value,
			state: 1,
			iteration_id: props.iterationId,
			planId: planId.length == 0 ? null : planId.length == 1 ? planId[0] : planId[1],
			planType: fList.length > 0 ? fList[0].plan_type : null
		})
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

	::v-deep {
		.el-radio__inner {
			border: 1px solid rgba(0,0,0,0.15);
		}
	}
</style>
