<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="110px">
		<ElRow>
			<ElCol :span="24">
				<ElFormItem label="复测解决类型" prop="retestsType">
					<ElRadioGroup v-model="form.retestsType">
						<ElRadio :value="1">已解决</ElRadio>
						<!-- <ElRadio :value="2">未复现</ElRadio> -->
						<ElRadio :value="2">这不是问题</ElRadio>
					</ElRadioGroup>
				</ElFormItem>
			</ElCol>

			<ElCol :span="24">
				<ElFormItem label="解决说明" prop="retestsContent">
					<Editor v-model="form.retestsContent" contentType="html" style="width: 100%; height: 300px" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElTree, ElRadioGroup, ElRadio, ElRow, ElCol } from "element-plus";
	import { solveTask } from "@/api/test/issues";

	const props = defineProps({
		moudleList: {
			type: Array,
			default: () => []
		},
		projectId: {
			type: Number,
			default: 0
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		}
	});

	const form = reactive({
		retestsType: "",
		retestsContent: ""
	});
	const rules = reactive({
		retestsType: [
			{
				required: true,
				message: "请选择复测解决类型",
				trigger: "blur"
			}
		]
	});

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化组员列表
	onMounted(async () => {});

	registerFormDone(async () => {
		console.log("form", form);
		await solveTask(
			Object.assign({}, toRaw(form), {
				id: props.projectId
			})
		);
		props.refreshList();
		return true;
	});
</script>
