<template><BaseForm v-bind="form"></BaseForm></template>

<script setup lang="ts">
	import { saveOrUpdate, update } from "@/api/setting/dictionary";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { toRaw, nextTick } from "vue";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 90,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "right",
		formItems: [
			{
				label: "字典名称",
				value: "",
				type: "ElInput",
				prop: "dictLabel",
				span: 24,
				attrs: {
					placeholder: "请输入字典名称"
				}
			},
			{
				label: "字典值",
				value: "",
				type: "ElInput",
				prop: "dictValue",
				span: 24,
				attrs: {
					placeholder: "请输入字典值"
				}
			},
			{
				label: "字典标识",
				value: "",
				type: "ElInput",
				prop: "dictSign",
				span: 24,
				attrs: {
					placeholder: "请输入字典标识"
				}
			},
			{
				label: "描述",
				value: "",
				type: "ElInput",
				prop: "memo",
				span: 24,
				attrs: {
					placeholder: "请输入描述"
				}
			},
			{
				label: "排序",
				value: "",
				type: "ElInput",
				prop: "sort",
				span: 24,
				attrs: {
					placeholder: "请输入排序"
				}
			}
		],
		rules: {
			dictLabel: [
				{
					required: true,
					message: "请输入字典名称",
					trigger: "blur"
				}
			],
			dictValue: [
				{
					required: true,
					message: "请输入字典值",
					trigger: "blur"
				}
			],
			dictSign: [
				{
					required: true,
					message: "请输入字典标识",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		if (props.rowInfo.id) {
			return await update(
				Object.assign({}, toRaw(form.value), {
					id: props.rowInfo.id
				})
			);
		} else {
			return await saveOrUpdate(Object.assign({}, toRaw(form.value)));
		}
	});

	if (props.rowInfo.id) {
		Object.keys(form.value).forEach((key) => {
			form.value[key] = props.rowInfo[key];
		});
	}
</script>
