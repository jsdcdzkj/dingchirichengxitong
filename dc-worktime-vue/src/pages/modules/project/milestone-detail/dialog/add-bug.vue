<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="tsx">
	import { selectFeaturePage } from "@/api/project/functionList";
	import { selectModuleTree } from "@/api/project/plan";
	import { selectListByGroupId } from "@/api/test/issues";
	import { createModelAsync } from "@/core/dialog";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";
	import { ElMessage } from "element-plus";
	import BugList from "./bug-list.vue";
	import { object } from "vue-types";
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
				label: "所属模块",
				value: "",
				type: "ElCascader",
				prop: "module_id",
				span: 12,
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
							if (form.formItems[1].select && form.formItems[1].select.list) {
								form.formItems[1].select.list = res.page.records;
								if (form.formItems[1].attrs)
									form.formItems[1].attrs.disabled = res.page.records.length == 0;
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
				span: 9,
				select: {
					type: "ElOption",
					label: "feature_name",
					value: "id",
					list: []
				},
				attrs: {
					onChange: (e: number) => {
						if (form.formItems[2].attrs) form.formItems[2].attrs.disabled = e < 0;
					},
					disabled: true
				}
			},
			{
				label: "",
				prop: "",
				value: "",
				type: "ElButton",
				span: 3,
				attrs: {
					disabled: true,
					type: "primary",
					button: "检测相同BUG",
					style: "margin-top:30px;width:100%",
					onClick: () => {
						selectListByGroupId({
							pro_project_id: appInstance.projectItem.id,
							pro_feature_id: form.value.pro_feature_id
						}).then((data) => {
							if (data.length > 0) {
								// ElMessage.warning("检测到相同BUG");
								createModelAsync(
									{ title: "相同BUG列表", width: "1080px" },
									{},
									<BugList tableList={data} />
								);
							} else {
								ElMessage.success("未检测到相同BUG");
							}
						});
					}
				}
			},
			{
				label: "是否是阻塞性BUG",
				prop: "isChoke",
				value: "0",
				type: "ElRadioGroup",
				span: 12,
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
			},
			{
				label: "bug类型",
				value: "",
				type: "ElSelect",
				prop: "severity",
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
				label: "指派至",
				prop: "assigned_to_user_type",
				value: [],
				type: "ElCheckboxGroup",
				span: 12,
				select: {
					type: "ElCheckbox",
					list: [
						{
							label: "前端",
							value: 1
						},
						{
							label: "后端",
							value: 2
						}
					]
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
				label: "BUG标题",
				prop: "bug_title",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入"
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
			person_in_charge: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			isChoke: [
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
			assigned_to_user_type: [
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
		let module_id = 0;
		if (typeof props.data.module_id == "number") {
			module_id = props.data.module_id;
		} else {
			module_id = props.data.module_id[props.data.module_id.length - 1];
		}
		if (props.data.module_id) {
			selectFeaturePage({
				page: 1,
				size: 200,
				params: {
					feature_name: "",
					module_id: module_id,
					project_id: appInstance.projectItem.id
				}
			}).then((res) => {
				if (form.formItems[1].select && form.formItems[1].select.list) {
					form.formItems[1].select.list = res.page.records;
					if (form.formItems[1].attrs) form.formItems[1].attrs.disabled = res.page.records.length == 0;
				}
			});
		}
	}

	registerFormDone(async () => {
		return form.value;
	});
</script>
