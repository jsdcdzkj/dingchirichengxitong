<template><BaseForm v-bind="form"></BaseForm></template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { creatTask} from "@/api/task/leaders-assigned";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { getEntityLeader } from "@/api/task/leaders-assigned";
	import BaseForm from "@/core/struct/form/base-form";;
	import { appInstance } from "@/main";
	import { toRaw } from "vue";
	import { number } from "vue-types";
	import { useUserStore } from "@/store/user.ts";
	import { processFilesStruct } from "@/api/uploader";

	const props = defineProps({
		id: number().def()
	});

	const userStore = useUserStore();

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
		{
				label: "任务标题",
				prop: "title",
				value: "",
				type: "ElInput",
				span: 12
			},
			{
				label: "任务提出人",
				value: appInstance.userInfo?.id,
				type: "ElSelect",
				prop: "proposer_user_id",
				span: 12,
				attrs: {
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					list: [],
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
				}
			},
			{
				label: "紧急程度",
				value: "",
				type: "ElSelect",
				prop: "urgency",
				span: 12,
				select: {
					type: "ElOption",
					list: appInstance.mapDict.urgency,
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
			},
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
				label: "指派至",
				value: "",
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 12,
				attrs: {
					filterable: true,
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100,
						});
						return data.records;
					},
					list: []
				}
			},
			{
				label: "任务描述",
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
				label: "任务附件",
				value: [],
				type: "ElUpload",
				prop: "fileList",
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
			title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			proposer_user_id: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			urgency: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
			end_time: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			plan_hours: [
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
			]
		}
	});

	registerFormDone(async () => {
		const params = Object.assign({}, toRaw(form.value), {
				fileList: form.value.fileList.map((item: any) => item.response.data),
				id: props.id,
				end_time: form.value.end_time.length > 11 ? form.value.end_time : form.value.end_time + ' 23:59:59'
			})
		console.log('3333333333333333', form.value, params)

		await creatTask(params);
		return true;
	});

	if (props.id) {
		getEntityLeader(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			form.value.urgency = Number(form.value.urgency)

			// 文件结构重构
			form.value.fileList = processFilesStruct(form.value.fileList);
		});
	}
</script>
