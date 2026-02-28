<template>
	<div>
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider, ElInput, ElIcon } from "element-plus";
	import { number } from "vue-types";
	import {completeTask} from "@/api/task/kanban"

	const props = defineProps({
		id: number().def(),
		row: {
			type: Object,
			default: {}
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "工作成果",
				value: "",
				type: "Editor",
				prop: "work_product",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
		],
	});

	registerFormDone(async () => {
		await completeTask(
			Object.assign({}, {
				id: props.row.id,
				work_product: form.value.work_product
			})
		);
		return true;
	});
</script>
<style lang="scss" scoped>
.plus-wrap {
	width: 100%;
	height: 32px;
	background: #FFFFFF;
	border-radius: 4px 4px 4px 4px;
	border: 1px dashed rgba(0,0,0,0.25);
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 14px;
	color: rgba(0,0,0,0.65);
}

.title {
	line-height: 22px;
	margin-bottom: 8px;
	margin-top: 16px;
}
</style>