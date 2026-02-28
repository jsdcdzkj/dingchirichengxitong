<template>
	<BaseForm v-bind="form"></BaseForm>
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { getEntityById, saveOrUpd } from "@/api/project/list";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { toRaw } from "vue";
	import { number } from "vue-types";
	import { appInstance } from "@/main";

	const props = defineProps({
		id: number().def()
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "项目名称",
				value: "",
				type: "ElInput",
				prop: "projectName",
				span: 8
			},
			{
				label: "项目用户",
				value: "",
				type: "ElInput",
				prop: "projectUser",
				span: 8
			},
			{
				label: "业务负责人",
				value: 1,
				type: "ElSelect",
				prop: "businessManager",
				span: 8,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			{
				label: "项目经理",
				value: 1,
				type: "ElSelect",
				prop: "projectManager",
				span: 8,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			{
				label: "计划时间",
				value: "",
				type: "ElDatePicker",
				prop: "projectStartTime",
				span: 8,
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "时间",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				label: "合同额/报价",
				value: "",
				type: "ElInput",
				prop: "contractAmount",
				span: 8
			},
			{
				label: "备注",
				value: "",
				type: "ElInput",
				prop: "remark",
				span: 24,
				attrs: {
					type: "textarea"
				}
			},
			{
				label: "是否已签订合同",
				value: "1",
				type: "ElRadioGroup",
				prop: "isContract",
				span: 8,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "是",
							value: "1"
						},
						{
							label: "否",
							value: "0"
						}
					]
				}
			},
			{
				label: "是否签订内部协议",
				value: "1",
				type: "ElRadioGroup",
				prop: "isProtocol",
				span: 8,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "是",
							value: "1"
						},
						{
							label: "否",
							value: "0"
						}
					]
				}
			},
			{
				label: "合同/协议签订日期",
				value: "",
				type: "ElDatePicker",
				prop: "contractTime",
				span: 8,
				attrs: {
					placeholder: "时间",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				label: "已回款金额",
				value: "",
				type: "ElInput",
				prop: "amountReceived",
				span: 8
			},
			{
				label: "项目合同/内部协议",
				value: [],
				type: "ElUpload",
				prop: "files",
				span: 24,
				attrs: {
					action: `/api/project/uploadFile`,
					placeholder: "",
					multiple: true,
					name: "multipartFile",
					headers: {
						JsdcToken: appInstance.loginToken
					}
				}
			}
		],
		rules: {
			input: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		const [projectStartTime, projectEndTime] =
			form.value.projectStartTime || [];

		await saveOrUpd(
			Object.assign({}, toRaw(form.value), {
				projectEndTime,
				projectStartTime,
				files: form.value.files.map((item: any) => item.response.data),
				id: props.id
			})
		);
		return true;
	});

	if (props.id) {
		getEntityById(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			// 时间区间
			form.value.projectStartTime = [
				res.projectStartTime,
				res.projectEndTime
			];
			// 文件结构重构
			form.value.files = processFilesStruct(form.value.files);
		});
	}
</script>
