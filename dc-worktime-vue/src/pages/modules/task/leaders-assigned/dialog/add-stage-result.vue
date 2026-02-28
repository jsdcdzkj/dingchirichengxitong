<template>
	<div class="score-wrap">
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"><Warning color="#F56C6C"/></ElIcon>
			请随时上传处理过程的阶段性成果!
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { getEntityById, saveOrUpd } from "@/api/project/list";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import {
		Warning
	} from "@element-plus/icons-vue";
	import { ElDivider } from "element-plus";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { toRaw } from "vue";
	import { number } from "vue-types";
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
				label: "处理内容",
				value: "",
				type: "Editor",
				prop: "businessManager22",
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
				prop: "files",
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
			input: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	registerFormDone(async () => {
		const [projectStartTime, projectEndTime] =
			form.value.projectStartTime || [];

		await saveOrUpd(
			Object.assign({}, toRaw(form.value), {
				projectEndTime,
				projectStartTime,
				files: form.value.files.map((item: any) => item.response.data),
				id: props.id
			})
		);
		return true;
	});

	if (props.id) {
		getEntityById(props.id).then((res) => {
			// form.value = mergeProps(form.value, res);
			Object.keys(form.value).forEach((key) => {
				form.value[key] = res[key];
			});
			// 时间区间
			form.value.projectStartTime = [
				res.projectStartTime,
				res.projectEndTime
			];
			// 文件结构重构
			form.value.files = processFilesStruct(form.value.files);
		});
	}
</script>
<style lang="scss" scoped>
.score-wrap {
	font-size: 14px;
	color: rgba(0,0,0,0.85);
	line-height: 22px;
}
</style>