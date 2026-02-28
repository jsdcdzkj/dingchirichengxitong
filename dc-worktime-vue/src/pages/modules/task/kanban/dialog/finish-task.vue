<template>
	<div>
		<div class="title">工作成果</div>
		<ElForm>
			<div v-for="(item,index) in resultList" :key="index" class="flex flex-items-center m-b-12px">
				<ElInput class="flex-1" v-model="item.url" placeholder="请输入工作成果链接，如前端URL地址、UI设计地址、素材图地址等"></ElInput>
				<ElIcon class="m-l-10px" size="20px" @click="handleDelete(index)"><RemoveFilled color="#F56C6C" /></ElIcon>
			</div>
			<div class="plus-wrap" @click="handleAdd">
				<ElIcon><Plus /></ElIcon>
				新增成果
			</div>
		</ElForm>
		<ElDivider class="m-t-10px m-b-10px" />
		<BaseForm v-bind="form"></BaseForm>
	</div>
</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { getEntityById, saveOrUpd } from "@/api/project/list";
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
	import {taskWorkResultSave} from "@/api/task/kanban"
	import { appInstance } from "@/main";

	const props = defineProps({
		id: number().def(),
		row: {
			type: Object,
			default: {}
		}
	});

	const resultList = ref([{url:''}])

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
			{
				label: "成果附件",
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
			input: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			]
		}
	});

	// 新增
	const handleAdd = () => {
		resultList.value.push({url: ''})
	}

	// 删除
	const handleDelete = (index:any) => {
		resultList.value.splice(index, 1)
	}

	registerFormDone(async () => {
		const result = resultList.value.filter(val => val.url).map((val:any) => {
			val.task_id = props.row.id
			return val
		})
		await taskWorkResultSave(
			Object.assign({}, {
				id: props.row.id,
				fileList: form.value.fileList.map((item: any) => item.response.data),
				taskWorkResultList: result,
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
			form.value.fileList = processFilesStruct(form.value.fileList);
		});
	}
</script>
<style lang="scss" scoped>
.plus-wrap {
	width: 100%;
	height: 32px;
	background: #FFFFFF;
	border-radius: 4px 4px 4px 4px;
	border: 1px dashed rgba(0,0,0,0.25);
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 14px;
	color: rgba(0,0,0,0.65);
}

.title {
	line-height: 22px;
	margin-bottom: 8px;
	margin-top: 16px;
}
</style>