<template>
	<ElForm
		:model="tableData"
		:rules="rules"
		class="notMargn"
		inline-message
		ref="formInstance"
	>
		<ElTable border :data="tableData.items">
			<ElTableColumn label="序号" align="center">
				<template #default="{ $index }">
					{{ $index + 1 }}
				</template>
			</ElTableColumn>
			<ElTableColumn label="目标名称" align="center">
				<template #default="scope">
					<ElFormItem
						:prop="`items.${scope.$index}.name`"
						:rules="rules.name"
					>
						<ElInput v-model:model-value="scope.row.name"></ElInput>
					</ElFormItem>
				</template>
			</ElTableColumn>
			<ElTableColumn label="类型" align="center">
				<template #default="scope">
					<ElFormItem
						:prop="`items.${scope.$index}.type`"
						:rules="rules.type"
					>
						<ElSelect v-model:model-value="scope.row.type">
							<ElOption label="链接" :value="1"></ElOption>
							<ElOption label="文件" :value="2"></ElOption>
						</ElSelect>
					</ElFormItem>
				</template>
			</ElTableColumn>
			<ElTableColumn label="操作" align="center">
				<template #default="scope">
					<ElButton
						type="primary"
						plain
						size="small"
						@click="handleDelete(scope.$index)"
						>删除</ElButton
					>
				</template>
			</ElTableColumn>
		</ElTable>
		<div w="100%" class="add-table-item">
			<ElButton text link :icon="Plus" @click="handleAdd" type="primary"
				>新增</ElButton
			>
		</div>
	</ElForm>
</template>

<script setup lang="tsx">
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { Plus } from "@element-plus/icons-vue";
	import {
		ElTableColumn,
		ElTable,
		ElInput,
		ElSelect,
		ElOption,
		ElButton,
		ElForm,
		ElFormItem
	} from "element-plus";
	import { reactive } from "vue";

	const { registerFormDone, formInstance } = useDialogForm();

	const tableData = reactive({
		items: [
			{
				name: "",
				type: 1
			}
		]
	});

	const rules = reactive({
		name: [{ required: true, message: "请输入", trigger: "blur" }],
		type: [{ required: true, message: "请选择", trigger: "change" }]
	});
	const handleAdd = () => {
		tableData.items.push({
			name: "",
			type: 1
		});
	};
	const handleDelete = (index: number) => {
		tableData.items.splice(index, 1);
	};

	registerFormDone(() => {
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
