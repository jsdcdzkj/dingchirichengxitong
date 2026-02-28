<template>
	<div>
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"><Warning color="#F56C6C"/></ElIcon>
			请随时上传处理过程的阶段性成果！
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import {
		Warning
	} from "@element-plus/icons-vue";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import {
		RemoveFilled,
		Plus
	} from "@element-plus/icons-vue";
	import { ElDivider, ElInput, ElIcon } from "element-plus";
	import { toRaw, ref } from "vue";
	import { number } from "vue-types";
	import {saveOrUpdateLeader} from "@/api/task/kanban"
	import { appInstance } from "@/main";

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
				label: "处理内容",
				value: "",
				type: "Editor",
				prop: "content",
				span: 24,
				attrs: {
					style: { height: "150px", width: "100%" },
					contentType: "html"
				}
			},
			{
				label: "处理附件",
				value: [],
				type: "ElUpload",
				prop: "fileList",
				span: 24,
				attrs: {
					action: `/api/project/uploadFile`,
					placeholder: "",
					multiple: true,
					name: "multipartFile",
					headers: {
						JsdcToken: appInstance.loginToken
					}
				}
			}
		],
		rules: {
			content: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		await saveOrUpdateLeader(
			Object.assign({}, {
				task_leaders_id: props.row.id,
				title: props.row.title,
				fileList: form.value.fileList.map((item: any) => item.response.data),
				content: form.value.content,
			})
		);
		return true;
	});
</script>
<style lang="scss" scoped>
</style>