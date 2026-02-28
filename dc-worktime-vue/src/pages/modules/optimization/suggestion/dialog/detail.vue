<template><BaseForm v-bind="form"></BaseForm></template>

<script setup lang="ts">
	import { addUpdateInfo } from "@/api/optimization/suggestion";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { processFilesStruct } from "@/api/uploader";
	import { onMounted, ref, toRaw } from "vue";
	import { appInstance } from "@/main";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		moduleList: {
			type: Array,
			default: () => []
		},
		refreshList: {
			type: Function,
			default: () => {}
		},
		projectId: {
			type: Number,
			default: ""
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 80,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "项目名称",
				value: null,
				type: "ElSelect",
				prop: "pro_project_id",
				span: 12,
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					list: props.moduleList
				},
				attrs: {
					clearable: true,
					filterable: true,
					placeholder: "请选择项目"
				}
			},
			{
				label: "标题",
				value: "",
				type: "ElInput",
				prop: "title",
				span: 12,
				attrs: {
					placeholder: "请输入标题"
				}
			},
			{
				label: "附件",
				value: [],
				type: "ElUpload",
				prop: "files",
				span: 12,
				attrs: {
					action: `/api/project/uploadFile`,
					placeholder: "",
					multiple: true,
					name: "multipartFile",
					headers: {
						JsdcToken: appInstance.loginToken
					}
				}
			},
			{
				label: "内容",
				value: "<p><br></p>",
				type: "Editor",
				prop: "content",
				span: 24,
				attrs: {
					style: { height: "calc(100vh - 460px)", width: "100%" },
					contentType: "html"
				}
			}
		],
		rules: {
			pro_project_id: [
				{
					required: true,
					message: "请选择项目",
					trigger: "change"
				}
			],
			title: [
				{
					required: true,
					message: "请输入标题",
					trigger: "blur"
				}
			],
			content: [
				{
					required: true,
					message: "请输入内容",
					trigger: "blur"
				}
			]
		}
	});
	// 初始化组员列表
	onMounted(async () => {});

	registerFormDone(async () => {
		await addUpdateInfo(
			Object.assign({}, toRaw(form.value), {
				files: form.value.files.map((item: any) => item.response.data),
				id: props.rowInfo.id
			})
		);
		props.refreshList();
		return true;
	});

	if (props.rowInfo.id) {
		Object.keys(form.value).forEach((key) => {
			form.value[key] = props.rowInfo[key];
		});
		form.value.files = processFilesStruct(props.rowInfo.files);
	}
	if (props.projectId) {
		form.value.pro_project_id = props.projectId;
	}
</script>
