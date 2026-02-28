<template>
	<div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import { saveOrUpd, getEntityById } from "@/api/project/release-record"
	import { appInstance } from "@/main";
	import { projectUserList } from "@/api/project/list";

	const props = defineProps({
		id: {
			type: Number,
			default: ''
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
				value: 3,
				type: "ElSelect",
				prop: "workItemType",
				span: 12,
				attrs: {
					disabled: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: appInstance.mapDict.work_item_type
				}
			},
			{
				label: "指定发布人",
				value: "",
				type: "ElSelect",
				prop: "appointPublishUser",
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
				label: "预计发布时间",
				value: "",
				type: "ElDatePicker",
				prop: "predictPublishDate",
				span: 12,
				attrs: {
					valueFormat: "YYYY-MM-DD HH:mm",
					format: 'YYYY-MM-DD HH:mm',
					type: 'datetime',
				}
			},
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
			],
			description: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			appointPublishUser: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			predictPublishDate: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	registerFormDone(async() => {
		await saveOrUpd({...form.value, id: props.id, state: 3})
		return form.value;
	});

	if (props.id) {
		getEntityById(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			form.value.workItemType = Number(form.value.workItemType)
			form.value.appointPublishUser = Number(form.value.appointPublishUser)
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
