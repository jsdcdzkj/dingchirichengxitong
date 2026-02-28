<template>
	<ElForm :model="tableData" :rules="rules" class="notMargn" inline-message ref="formInstance">
		<ElTable border :data="tableData.deliverables">
			<ElTableColumn label="序号" align="center">
				<template #default="{ $index }">
					{{ $index + 1 }}
				</template>
			</ElTableColumn>
			<ElTableColumn label="目标名称" align="center">
				<template #default="scope">
					<ElFormItem :prop="`deliverables.${scope.$index}.goalName`" :rules="rules.goalName">
						<ElInput v-model:model-value="scope.row.goalName"></ElInput>
					</ElFormItem>
				</template>
			</ElTableColumn>
			<ElTableColumn label="类型" align="center">
				<template #default="scope">
					<ElFormItem :prop="`deliverables.${scope.$index}.type`" :rules="rules.type">
						<ElSelect :disabled="isWu" v-model:model-value="scope.row.type">
							<ElOption label="链接" :value="1"></ElOption>
							<ElOption label="文件" :value="2"></ElOption>
						</ElSelect>
					</ElFormItem>
				</template>
			</ElTableColumn>
			<ElTableColumn label="操作" align="center" v-if="!isEdit">
				<template #default="scope">
					<ElButton type="primary" plain size="small" @click="handleDelete(scope.$index)">删除</ElButton>
				</template>
			</ElTableColumn>
		</ElTable>
		<div w="100%" class="add-table-item" v-if="!isEdit">
			<ElButton text link :icon="Plus" @click="handleAdd" type="primary">新增</ElButton>
		</div>
	</ElForm>
</template>

<script setup lang="tsx">
	import { saveDeliverable } from "@/api/project/deliverables";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { appInstance } from "@/main";
	import { Plus } from "@element-plus/icons-vue";
	import { ElTableColumn, ElTable, ElInput, ElSelect, ElOption, ElButton, ElForm, ElFormItem } from "element-plus";
	import { reactive, computed } from "vue";
	import { number, array, bool } from "vue-types";

	const props = defineProps({
		milestoneId: number().def(),
		id: number().def(),
		deliverables: array().def([]),
		isWu: bool().def()
	});

	const isEdit = computed(() => props.id !== undefined);

	const { registerFormDone, formInstance } = useDialogForm();

	const tableData = reactive({
		deliverables:
			props.deliverables.length > 0
				? props.deliverables
				: [
						{
							goalName: "",
							type: 1
						}
					]
	});

	const rules = reactive({
		goalName: [{ required: true, message: "请输入", trigger: "blur" }],
		type: [{ required: true, message: "请选择", trigger: "change" }]
	});
	const handleAdd = () => {
		tableData.deliverables.push({
			goalName: "",
			type: 1
		});
	};
	const handleDelete = (index: number) => {
		tableData.deliverables.splice(index, 1);
	};

	registerFormDone(async () => {
		await saveDeliverable(
			isEdit.value
				? tableData.deliverables.map((item) => {
						return {
							...item,
							projectId: appInstance.projectItem.id,
							milestoneId: props.milestoneId,
							id: props.id
						};
					})
				: tableData.deliverables.map((item) => {
						return {
							...item,
							projectId: appInstance.projectItem.id,
							milestoneId: props.milestoneId
						};
					})
		);
		return true;
	});
</script>

<style lang="scss" scoped>
	.add-table-item {
		border-left: 1px solid var(--el-border-color);
		border-right: 1px solid var(--el-border-color);
		border-bottom: 1px solid var(--el-border-color);
		padding: 10px;
		text-align: center;
	}
</style>
