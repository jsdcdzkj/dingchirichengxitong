<template>
	<div h="100%">
		<div class="flex flex-items-center gap-10px">
			<ElIcon size="24px" style="color: var(--el-color-error)">
				<Warning></Warning>
			</ElIcon>
			<span class="font-size-14px" style="font-weight: bold">
				项目计划已挂起{{
					props.rowInfo.suspend_day
				}}天，请设定恢复后计划及任务的顺延时长！
			</span>
		</div>
		<ElDivider />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { recoverTask } from "@/api/test/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref, toRaw } from "vue";
	import { Warning } from "@element-plus/icons-vue";
	import { ElDivider, ElIcon, ElMessage } from "element-plus";

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
		labelWidth: 90,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "right",
		formItems: [
			{
				label: "顺延时长：",
				value: "0",
				type: "ElRadioGroup",
				prop: "status",
				span: 24,
				select: {
					type: "ElRadio",
					list: [
						{
							label: "按暂停天数顺延",
							value: "0"
						}
					]
				}
			}
		],
		rules: {}
	});
	// 初始化组员列表

	registerFormDone(async () => {
		console.log("form", props.rowInfo.id);
		await recoverTask(
			Object.assign({}, toRaw(form.value), {
				id: props.rowInfo.id
			})
		);
		ElMessage.success("恢复成功");
		props.refreshList();
		return true;
	});
</script>
