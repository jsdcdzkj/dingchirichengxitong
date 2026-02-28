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
		labelPosition: "top",
		formItems: [
			{
				label: "工作组类型名称",
				value: "",
				type: "ElInput",
				prop: "dictLabel",
				span: 24,
				attrs: {
					placeholder: "请输入"
				}
			}
		],
		rules: {
			dictLabel: [
				{
					required: true,
					message: "工作组类型名称",
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
			return await saveOrUpdate(
				Object.assign({}, toRaw(form.value), { dictSign: "sysGroup" })
			);
		}
	});

	if (props.rowInfo.id) {
		form.value.dictLabel = props.rowInfo.dictLabel;
	}
</script>
