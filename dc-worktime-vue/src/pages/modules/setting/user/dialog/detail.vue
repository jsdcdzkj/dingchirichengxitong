<template><BaseForm v-bind="form"></BaseForm></template>

<script setup lang="ts">
	import { getAllRoles } from "@/api/setting/role";
	import { saveUser, getRolesByUserId } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref } from "vue";
	import { toRaw, nextTick } from "vue";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const editDisabled = ref(false);

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 90,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "right",
		formItems: [
			// 				"username": "", //登录账号

			// "realName": "", //真实姓名
			// "dept_id": "", //部门
			// "workId": "", //工号
			// "phone": "", //手机号
			// "status": "", //状态 0启用1禁用
			// roleIds: [],所属角色
			// {
			// 	label: "登录账号",
			// 	value: "",
			// 	type: "ElInput",
			// 	prop: "username",
			// 	span: 24,
			// 	attrs: {
			// 		placeholder: "请输入"
			// 	}
			// },
			{
				label: "用户名称",
				value: "",
				type: "ElInput",
				prop: "username",
				span: 24,
				attrs: {
					placeholder: "请输入用户名称",
					disabled: editDisabled
				}
			},
			{
				label: "真实姓名",
				value: "",
				type: "ElInput",
				prop: "realName",
				span: 24,
				attrs: {
					placeholder: "请输入真实姓名"
				}
			},
			{
				label: "所属角色",
				value: null,
				type: "ElSelect",
				prop: "roleIds",
				span: 24,
				select: {
					type: "ElOption",
					label: "role_name",
					value: "id",

					listApi: async () => {
						const data = await getAllRoles();
						return data;
					},
					list: []
				},
				attrs: {
					multiple: true,
					clearable: true,
					filterable: true,
					placeholder: "请选择所属角色"
				}
			},
			{
				label: "工号",
				value: "",
				type: "ElInput",
				prop: "workId",
				span: 24,
				attrs: {
					placeholder: "请输入工号"
				}
			},
			{
				label: "手机号",
				value: "",
				type: "ElInput",
				prop: "phone",
				span: 24,
				attrs: {
					placeholder: "请输入手机号",
					disabled: editDisabled
				}
			},

			{
				label: "状态",
				value: "0",
				type: "ElRadioGroup",
				prop: "status",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "启用",
							value: "0"
						},
						{
							label: "禁用",
							value: "1"
						}
					]
				}
			}
		],
		rules: {
			username: [
				{
					required: true,
					message: "请输入登录账号",
					trigger: "blur"
				}
			],
			realName: [
				{
					required: true,
					message: "请输入真实姓名",
					trigger: "blur"
				}
			],
			roleIds: [
				{
					required: true,
					message: "请选择所属角色",
					trigger: "change"
				}
			],
			workId: [
				{
					required: true,
					message: "请输入工号",
					trigger: "blur"
				}
			],
			phone: [
				{
					required: true,
					message: "请输入手机号",
					trigger: "blur"
				},
				{
					pattern: /^1[3456789]\d{9}$/,
					message: "请输入正确的手机号",
					trigger: "blur"
				}
			]
		}
	});
	onMounted(async () => {
		if (props.rowInfo.id) {
			const data = await getRolesByUserId(props.rowInfo.id);
			form.value.roleIds = data;
		}
	});

	registerFormDone(async () => {
		await saveUser(
			Object.assign({}, toRaw(form.value), {
				id: props.rowInfo.id
			})
		);
		return true;
	});

	if (props.rowInfo.id) {
		Object.keys(form.value).forEach((key) => {
			form.value[key] = props.rowInfo[key];
		});
		editDisabled.value = true;
	}
</script>
