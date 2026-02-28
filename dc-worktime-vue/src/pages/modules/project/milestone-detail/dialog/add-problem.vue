<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="tsx">
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import BaseForm from "@/core/struct/form/base-form";
	import { appInstance } from "@/main";
	import { projectUserList } from "@/api/project/list";
	import { object } from "vue-types";
	import { selectModuleTree } from "@/api/project/plan";
	import { selectFeaturePage } from "@/api/project/functionList";
	const props = defineProps({
		groupType: {
			type: Number,
			default: 1
		},
		data: object().def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		className: "flex-1",
		formItems: [
			{
				label: "问题类型",
				prop: "question_type",
				type: "ElSelect",
				value: "",
				span: 12,
				select: {
					type: "ElOption",
					list:
						props.groupType == 2
							? appInstance.mapDict.question_type?.slice(0, 8)
							: appInstance.mapDict.question_type?.slice(8)
				}
			},
			{
				label: "严重程度",
				prop: "severity",
				type: "ElSelect",
				value: "",
				span: 12,
				select: {
					type: "ElOption",
					list: [
						{
							label: "轻微",
							value: "1"
						},
						{
							label: "一般",
							value: "2"
						},
						{
							label: "严重",
							value: "3"
						}
					]
				}
			},
			{
				label: "问题标题",
				prop: "bug_title",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				label: "责任人",
				prop: "assigned_to_user_id",
				type: "ElSelect",
				value: "",
				span: 12,
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
				label: "截止日期",
				value: "",
				type: "ElDatePicker",
				prop: "end_time",
				span: 12,
				attrs: {
					type: "date",
					valueFormat: "YYYY-MM-DD",
					placeholder: "请选择"
				}
			},

			{
				label: "所属模块",
				value: "",
				type: "ElCascader",
				prop: "module_id",
				span: 12,
				show: props.groupType == 2,
				select: {
					type: "ElOption",
					listApi: async () => {
						const data = await selectModuleTree({
							project_id: appInstance.projectItem.id
						});
						return data;
					}
				},
				attrs: {
					options: [],
					props: {
						label: "moduleName",
						value: "id"
					},
					onChange: (e: number[]) => {
						selectFeaturePage({
							page: 1,
							size: 200,
							params: {
								feature_name: "",
								module_id: e[e.length - 1],
								project_id: appInstance.projectItem.id
							}
						}).then((res) => {
							const len = form.formItems.length - 2;
							if (form.formItems[len].select && form.formItems[len].select.list) {
								form.formItems[len].select.list = res.page.records;
								if (form.formItems[len].attrs)
									form.formItems[len].attrs.disabled = res.page.records.length == 0;
							}
							form.value.pro_feature_id = "";
						});
					}
				}
			},
			{
				label: "功能名称",
				prop: "pro_feature_id",
				value: "",
				type: "ElSelect",
				span: 12,
				show: props.groupType == 2,
				select: {
					type: "ElOption",
					label: "feature_name",
					value: "id",
					list: []
				},
				attrs: {
					disabled: true
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
			question_type: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],

			bug_title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			end_time: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			assigned_to_user_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],

			pro_feature_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			module_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],

			severity: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			]
		}
	});

	if (props.data) {
		Reflect.ownKeys(props.data).forEach((key) => {
			Reflect.set(form.value, key, Reflect.get(props.data, key));
		});
		if (props.data.module_id) {
			selectFeaturePage({
				page: 1,
				size: 200,
				params: {
					feature_name: "",
					module_id: props.data.module_id,
					project_id: appInstance.projectItem.id
				}
			}).then((res) => {
				const len = form.formItems.length - 2;
				if (form.formItems[len].select && form.formItems[len].select.list) {
					form.formItems[len].select.list = res.page.records;
					if (form.formItems[len].attrs) form.formItems[len].attrs.disabled = res.page.records.length == 0;
				}
			});
		}
	}

	registerFormDone(() => {
		let module_id = 0;
		if (typeof form.value.module_id == "number") {
			module_id = form.value.module_id;
		} else {
			module_id = form.value.module_id[form.value.module_id.length - 1];
		}
		return {
			...form.value,
			question_type_name: appInstance.mapDict.question_type?.find(
				(item) => item.value == form.value.question_type
			)?.label,
			assigned_to_user_name: form.formItems[3].select.list?.find(
				(item) => item.id == form.value.assigned_to_user_id
			)?.username,
			assigned_to_user_type: props.groupType == 1 ? 4 : 3,
			module_id
		};
	});
</script>
