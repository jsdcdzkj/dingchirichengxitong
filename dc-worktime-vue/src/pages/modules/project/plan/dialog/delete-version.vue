<template>
	<div>
		<div class="flex gap-10px items-center">
			<ElIcon style="color: red" :size="30">
				<WarningFilled />
			</ElIcon>
			<div style="line-height: 30px">
				迭代版本删除后，其相关联的计划、阶段和任务等数据无法正确挂接，影响十分严重！<br />
				如果您确定要删除本迭代版本，请在文本输入框内正确输入下方标红加粗加大的删除确认文本！<br />
				<span style="color: red; font-weight: bold; font-size: 18px"
					>我已了解迭代版本删除的后果，本次删除是我真实意愿表达，我确定删除本迭代版本</span
				>
			</div>
		</div>
		<div style="margin-top: 20px">
			<BaseForm v-bind="form" />
		</div>
	</div>
</template>

<script setup lang="tsx">
	import { WarningFilled } from "@element-plus/icons-vue";
	import { ElIcon } from "element-plus";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { number } from "vue-types";
	import { deleteIteration } from "@/api/project/plan";
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
				label: "删除确认文本",
				prop: "plan_title",
				value: "",
				type: "ElInput",
				span: 24,
				attrs: {
					placeholder: "请输入删除确认文本"
				}
			}
		],
		rules: {
			plan_title: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				},
				{
					required: true,
					validator: (rule, value, callback) => {
						if (value !== "我已了解迭代版本删除的后果，本次删除是我真实意愿表达，我确定删除本迭代版本") {
							callback(new Error("输入不正确"));
						} else {
							callback();
						}
					}
				}
			]
		}
	});
	registerFormDone(async () => {
		await deleteIteration({
			project_id: appInstance.projectItem.id,
			id: props.id as number
		});
		return form.value;
	});
</script>

<style lang="scss" scoped></style>
