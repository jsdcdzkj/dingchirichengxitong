<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px">
		<ElRow>
			<ElCol :span="24">
				<ElFormItem label="父级菜单" prop="parentId">
					<ElTreeSelect
						v-model="form.parentId"
						placeholder="请选择父级菜单"
						:data="treeData"
						:props="treeProps"
						filterable
						:filter-node-method="filterNode"
						clearable
						:check-strictly="true"
						:render-after-expand="false"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="24">
				<ElFormItem label="节点名称" prop="moduleName">
					<ElInput v-model="form.moduleName" placeholder="请输入节点名称" />
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElCheckbox,
		ElCheckboxGroup,
		ElRadioGroup,
		ElRadio,
		ElDatePicker,
		ElSelect,
		ElOption,
		ElRow,
		ElCol
	} from "element-plus";
	import { createRapid, createPlan } from "@/api/project/functionList";
	import { selectDictList } from "@/api/setting/dictionary";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { appInstance } from "@/main";

	import { selectModuleTree } from "@/api/test/issues";
	const props = defineProps({
		projectId: {
			type: Number,
			default: 0
		},
		getTreeData: {
			type: Function,
			default: () => () => {}
		},
		treeData: {
			type: Array,
			default: () => []
		},
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		showNext: {
			type: Boolean,
			default: false
		},
		selectId: {
			type: Number,
			default: ""
		}
	});
	const form = reactive({
		moduleName: "",
		parentId: ""
	});
	const rules = reactive({
		moduleName: [{ required: true, message: "请输入节点名称", trigger: "blur" }]
		// parentId: [
		// 	{ required: true, message: "请选择上级节点名称", trigger: "change" }
		// ]
	});

	// 难易度
	const sourceOption = ref<any[]>([]);
	const { registerFormDone, formInstance } = useDialogForm();
	const tree = ref<InstanceType<typeof ElTree>>();

	const treeData = ref<any[]>([]);
	const treeProps = {
		children: "children",
		label: "moduleName",
		value: "id",
		disabled: (data: any) => data.functions?.length > 0 // 新增disabled判断
	};
	const filterNode = (value: string, data: any) => {
		if (!value) return true;
		// 检查当前节点的 moduleName 是否包含搜索值
		if (data.moduleName && data.moduleName.includes(value)) {
			return true;
		}
		// 递归检查子节点
		if (data.children && data.children.length > 0) {
			for (let i = 0; i < data.children.length; i++) {
				if (filterNode(value, data.children[i])) {
					return true;
				}
			}
		}
		return false;
	};
	// 自动标记层级关系
	const markNodeLevel = (nodes, level = 1) => {
		nodes.forEach((node) => {
			node.isRoot = level === 1;
			// 标记是否有子节点
			node.hasChildren = !!node.children && node.children.length > 0;
			if (node.children) {
				markNodeLevel(node.children, level + 1);
			}
		});
		return nodes; // 返回处理后的数据
	};
	const getTreeData = async () => {
		if (tree.value) {
			// 获取当前展开的节点
			const currentExpandedNodes = tree.value.store.nodesMap;
			const newExpandedKeys = [];
			for (const key in currentExpandedNodes) {
				if (currentExpandedNodes[key].expanded) {
					newExpandedKeys.push(currentExpandedNodes[key].data.id);
				}
			}
		}

		const res = await selectModuleTree({
			project_id: appInstance.projectItem.id
		});

		const processedTreeData = markNodeLevel(res);
		treeData.value = processedTreeData;
	};

	// 初始化组员列表
	onMounted(async () => {
		// treeData.value = props.treeData;
		await getTreeData();
		if (props.rowInfo.id) {
			// parentId 值为 "0"，则不赋值
			if (props.rowInfo.parentId == "0") {
				form.parentId = "";
			} else {
				form.parentId = props.rowInfo.parentId;
			}
			form.moduleName = props.rowInfo.moduleName;
		}
		if (props.selectId) {
			form.parentId = props.selectId;
		}
	});
	const cachedParentId = ref("");
	registerFormDone(async () => {
		// 提交前缓存 parentId
		cachedParentId.value = form.parentId;
		if (props.rowInfo.id) {
			if (!form.parentId) {
				await createPlan(
					Object.assign({}, toRaw(form), {
						project_id: props.projectId,
						id: props.rowInfo.id,
						parentId: "0"
					})
				);
			} else {
				await createPlan(
					Object.assign({}, toRaw(form), {
						project_id: props.projectId,
						id: props.rowInfo.id
					})
				);
			}
		} else {
			await createRapid(
				Object.assign({}, toRaw(form), {
					project_id: props.projectId
				})
			);
		}

		props.getTreeData();
		getTreeData();
		// 如果是连续添加模式
		if (props.showNext) {
			// 劫持表单的 resetFields 方法
			if (formInstance.value) {
				const originalReset = formInstance.value.resetFields;
				formInstance.value.resetFields = () => {
					originalReset.call(formInstance.value); // 执行原始重置
					form.parentId = cachedParentId.value; // 强制恢复 parentId
				};
			}
		}
		return true;
	});
</script>
