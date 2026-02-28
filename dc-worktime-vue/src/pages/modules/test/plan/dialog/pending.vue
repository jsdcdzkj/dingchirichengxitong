<template>
	<div h="100%">
		<div class="flex flex-items-center gap-10px">
			<ElIcon size="24px" style="color: var(--el-color-error)">
				<Warning></Warning>
			</ElIcon>
			<span class="font-size-14px" style="font-weight: bold">
				测试计划暂停后，需要手动恢复，请谨慎操作！
			</span>
		</div>
		<ElDivider />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { pendingTask } from "@/api/test/plan";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref, toRaw } from "vue";
	import { Warning } from "@element-plus/icons-vue";
	import { ElDivider, ElIcon, ElMessage } from "element-plus";

	const props = defineProps({
		rowId: {
			type: Number,
			default: 0
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 80,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "挂起原因",
				value: "",
				type: "ElInput",
				prop: "suspendReason",
				span: 24,
				attrs: {
					placeholder: "请输入挂起原因"
				}
			}
		],
		rules: {
			suspendReason: [
				{
					required: true,
					message: "请输入挂起原因",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		await pendingTask(
			Object.assign({}, toRaw(form.value), {
				id: props.rowId
			})
		);
		ElMessage.success("挂起成功");
		props.refreshList();
		return true;
	});
</script>
