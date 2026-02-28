<template>
	<BaseForm v-bind="form" />
</template>

<script setup lang="ts">
	import { editLinkOrFile } from "@/api/project/deliverables";
	import App from "@/App.vue";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";

	import { number, string } from "vue-types";

	const props = defineProps({
		id: number().def(),
		type: number().def(),
		deliverableSituation: string().def("")
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			props.type == 1
				? {
						label: "链接",
						prop: "deliverableSituation",
						value: props.deliverableSituation,
						type: "ElInput",
						span: 24
					}
				: {
						label: "文件",
						value: [],
						type: "ElUpload",
						prop: "file",
						span: 24,
						attrs: {
							action: `/api/project/uploadFile`,
							multiple: false,
							name: "multipartFile",
							headers: {
								JsdcToken: appInstance.loginToken
							},
							limit: 1
						}
					}
		],
		rules: {
			deliverableSituation: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
			file: [
				{
					required: true,
					message: "请上传",
					trigger: "change"
				}
			]
		}
	});
	registerFormDone(async () => {
		console.log(form.value);

		let data = {};
		if (props.type == 1) {
			data = {
				type: props.type,
				deliverableSituation: form.value.deliverableSituation
			};
		} else {
			data = {
				type: props.type,
				file: form.value.file[0].response.data
			};
		}
		await editLinkOrFile(Object.assign({}, data, props.id ? { id: props.id } : {}));
		return form.value;
	});
</script>
