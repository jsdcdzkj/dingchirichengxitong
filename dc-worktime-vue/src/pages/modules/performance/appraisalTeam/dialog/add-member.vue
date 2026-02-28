<template>
	<div>
		<BaseForm v-bind="form" />
	</div>
</template>

<script setup lang="ts">
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDivider } from "element-plus";
	import { add, getTeamUserList } from "@/api/performance/appraisalTeam";
	import { number } from "vue-types";

	const props = defineProps({
		team_id: number().def(),
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "left",
		formItems: [
			{
				label: "成员",
				value: "",
				type: "ElSelect",
				prop: "userIdList",
				span: 12,
				attrs: {
					filterable: true,
					multiple:true,
				},
				select: {
					type: "ElOption",
					label: "realName",
					value: "id",
					listApi: async () => {
						const data = await getTeamUserList({team_id:props.team_id});
						return data;
					},
					list: []
				}
			},
		],
		rules: {
			userIdList: [
				{
					required: true,
					message: "请选择",
					trigger: "change"
				}
			],
		}
	});
	

	registerFormDone(async() => {
		const res = await add({team_id: props.team_id, userIdList: form.value.userIdList});
		return res;
	});

</script>
<style lang="scss" scoped>
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(
			90deg,
			rgba(27, 134, 255, 0.1) 0%,
			rgba(27, 134, 255, 0) 100%
		);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}
</style>
