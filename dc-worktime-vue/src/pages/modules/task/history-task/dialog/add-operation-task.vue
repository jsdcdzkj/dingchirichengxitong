<template>
	<div>
		<BaseForm v-bind="form" />
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="title m-t-10px m-b-10px">详情</div>
		<BaseForm v-bind="form2" />
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="title m-t-10px m-b-10px">工时</div>
		<BaseForm v-bind="form3" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import { appInstance } from "@/main";
	import { saveOrUpdate, getEntityOperation } from "@/api/task/operation-maintenance";
	import { number } from "vue-types";
	import { projectUserList } from "@/api/project/list";
	import { ref, watch } from 'vue';

	const props = defineProps({
		id: number().def(),
		projectId: {
			type: Number,
			default: 0
		},
	});

	const { form } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "标题",
				prop: "name",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "所属项目",
				value: props.projectId,
				type: "ElSelect",
				prop: "project_id",
				span: 12,
				// attrs: {
				// 	disabled: true
				// },
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: appInstance.projectList,
				}
			},
			{
				label: "工作项类型",
				value: 1,
				type: "ElSelect",
				prop: "work_item_type",
				span: 12,
				attrs: {
					disabled: true
				},
				select: {
					type: "ElOption",
					list: appInstance.mapDict.work_item_type,
				}
			}
		],
		rules: {
			name: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			project_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			work_item_type: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		},
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
				value: "",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html",
				}
			},
			{
				label: "指派至",
				value: "",
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 12,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						console.log('22222222222props.projectId', props.projectId)
						const data = await projectUserList({
							projectId: props.projectId
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
					list: appInstance.mapDict.priority,
				}
			},
			{
			type: "ElDatePicker",
			value: "",
			prop: "end_time",
			label: "截止时间",
			span: 12,
			attrs: {
				valueFormat: "YYYY-MM-DD"
			}
		}
		],
		rules: {
			end_time: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			// assigned_user_id: [
			// 	{
			// 		required: true,
			// 		message: "请选择",
			// 		trigger: "change"
			// 	}
			// ],
			priority: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
		}
	});

	// 运维任务人员
	const assignedList = ref([])
	const getAssignedList = async(val) => {
		const data = await projectUserList({
			projectId: val
		});
		assignedList.value = data
		form2.formItems[1].select.list = assignedList.value
		console.log('222222222222222data', assignedList.value)
	}
	getAssignedList(props.projectId)

	// 监听项目的变化
	watch(
		() => form.value.project_id,
		(value: any) => {
			getAssignedList(value)
		}
	);
	

	const { form: form3, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "预估工时（分钟）",
				value: "30",
				type: "ElInputNumber",
				prop: "plan_hours",
				span: 12,
				attrs: {
					min: 0,
				},
			},
			{
				label: "难度等级",
				value: 1,
				type: "ElSelect",
				prop: "degree_of_difficulty",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.degree_of_difficulty,
				}
			},
		],
		rules: {
			plan_hours: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			degree_of_difficulty: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
		}
	});
	registerFormDone(async() => {
		console.log('66666666', form.value, form2.value, form3.value)
		const res = await saveOrUpdate({ ...form.value, ...form2.value,end_time: form2.value.end_time + ' 23:59:59', ...form3.value, id: props.id });
		return res;
	});
	
	// 编辑
	if (props.id) {
		getEntityOperation(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			Object.keys(form2.value).forEach((key) => {
				form2.value[key] = res[key];
				form2.value.assigned_to_user_id = res.assigned_to_user_id

			});
			Object.keys(form3.value).forEach((key) => {
				form3.value[key] = res[key];
			});
			console.log(form.value, form2.value, form3.value)
		});
	}
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
