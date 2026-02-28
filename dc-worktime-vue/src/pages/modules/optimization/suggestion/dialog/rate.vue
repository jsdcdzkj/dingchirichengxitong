<template>
	<div h="100%">
		<div class="flex flex-items-center gap-10px">
			<ElIcon size="24px" style="color: var(--el-color-primary)">
				<Warning></Warning>
			</ElIcon>
			<span class="font-size-14px" style="font-weight: bold"> 请为优化建议打分 </span>
		</div>
		<ElDivider />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { addUpdateInfo } from "@/api/optimization/suggestion";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref, toRaw } from "vue";
	import { Warning } from "@element-plus/icons-vue";
	import { ElDivider, ElIcon } from "element-plus";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "right",
		formItems: [
			{
				label: "评分",
				value: "",
				type: "ElRate",
				prop: "mark",
				span: 24,
				attrs: {
					allowHalf: true
				}
			},
			{
				label: "结果",
				value: "1",
				type: "ElRadioGroup",
				prop: "markResult",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "采纳",
							value: "1"
						},
						{
							label: "不采纳",
							value: "0"
						}
					]
				}
			}
		],
		rules: {
			mark: [
				{
					required: true,
					message: "请设置评分",
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
				id: props.rowInfo.id
			})
		);
		props.refreshList();
		return true;
	});
</script>
