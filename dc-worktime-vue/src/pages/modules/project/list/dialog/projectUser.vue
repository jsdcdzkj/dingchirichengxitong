<template>
	<div h="100%">
		<div class="flex flex-items-center gap-10px">
			<ElIcon size="24px" style="color: var(--el-color-primary)">
				<Warning></Warning>
			</ElIcon>
			<span class="font-size-14px" style="font-weight: bold"> 根据项目实际进展，请维护项目组成员信息 </span>
		</div>
		<ElDivider />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { saveProjectUser } from "@/api/project/list";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { Warning } from "@element-plus/icons-vue";
	import { ElDivider, ElIcon } from "element-plus";

	import { array, number } from "vue-types";

	const props = defineProps({
		id: number().def(),
		userIds: array().def([])
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		span: 24,
		formItems: [
			{
				label: "参与成员",
				prop: "userIds",
				value: props.userIds,
				type: "ElSelect",
				attrs: {
					multiple: true,
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getListAll({});
						return data;
					},
					list: []
				}
			}
		]
	});

	registerFormDone(async () => {
		await saveProjectUser({ userIds: form.value.userIds, id: props.id });
		return form.value;
	});
</script>
