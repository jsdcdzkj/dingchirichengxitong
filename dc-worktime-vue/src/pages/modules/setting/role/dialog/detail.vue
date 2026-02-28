<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="90px">
		<ElFormItem label="角色名称" prop="role_name">
			<ElInput v-model="form.role_name" placeholder="请输入角色名称" />
		</ElFormItem>
		<ElFormItem label="菜单权限" prop="menuIds">
			<div class="tree-con" style="width: 100%">
				<div class="tree-edit">
					<ElCheckbox :indeterminate="isIndeterminate" v-model="checkOne" @change="handleCheckOne"
						>全选</ElCheckbox
					>
					<ElCheckbox v-model="checkTwo" @change="handleCheckTwo">全部展开</ElCheckbox>
					<!-- <ElCheckbox v-model="checkThree">父子联动</ElCheckbox> -->
				</div>
				<div class="tree-box">
					<ElTree
						ref="tree"
						:data="treeData"
						show-checkbox
						node-key="id"
						:default-expanded-keys="defaultExpandedKeys"
						:props="defaultProps"
						:check-strictly="false"
						@check-change="handleTreeCheckChange"
					>
					</ElTree>
				</div>
			</div>
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="js">
	import { ref, onMounted, reactive, watch, nextTick } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { ElForm, ElFormItem, ElInput, ElTree, ElCheckbox } from "element-plus";
	import { number } from "vue-types";
	import { toRaw } from "vue";
	import { saveRole, getRoleList } from "@/api/setting/role";
	import { getMenuTree } from "@/api/setting/menu";

	const form = reactive({
		role_name: "",
		menuIds: []
	});

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const rules = reactive({
		role_name: [
			{
				required: true,
				message: "请输入角色名称",
				trigger: "blur"
			}
		]
	});
	const defaultProps = {
		children: "children",
		label: "title"
	};

	// 新增功能相关状态
	const checkOne = ref(false);
	const checkTwo = ref(false);
	// const checkThree = ref(false);
	const isIndeterminate = ref(false);
	const defaultExpandedKeys = ref([]);
	const tree = ref();
	const treeData = ref([]);

	// 获取所有节点ID（用于全选/展开）
	const getAllNodeKeys = (data) => {
		let keys = [];
		data.forEach((item) => {
			keys.push(item.id);
			if (item.children) {
				keys = keys.concat(getAllNodeKeys(item.children));
			}
		});
		return keys;
	};

	// 全选/反选
	const handleCheckOne = (val) => {
		if (val) {
			tree.value?.setCheckedKeys(getAllNodeKeys(treeData.value));
		} else {
			tree.value?.setCheckedKeys([]);
		}
	};

	// 展开/折叠
	const handleCheckTwo = (val) => {
		if (val) {
			defaultExpandedKeys.value = getAllNodeKeys(treeData.value);
		} else {
			const nodes = tree.value?.store.nodesMap;
			if (nodes) {
				Object.values(nodes).forEach((node) => (node.expanded = false));
			}
		}
	};

	// 更新全选状态
	const updateCheckState = () => {
		const checkedKeys = tree.value?.getCheckedKeys() || [];
		const allKeys = getAllNodeKeys(treeData.value);
		const isAllChecked = checkedKeys.length === allKeys.length;
		const isPartialChecked = checkedKeys.length > 0 && !isAllChecked;

		isIndeterminate.value = isPartialChecked;
		checkOne.value = isAllChecked ? true : isPartialChecked ? null : false;
	};

	// 树节点选中变化监听
	const handleTreeCheckChange = () => {
		updateCheckState();
	};

	// 监听父子联动模式变化
	// watch(checkThree, (val) => {
	// 	tree.value?.setCheckedKeys(tree.value?.getCheckedKeys() || []);
	// });

	// 初始化加载菜单树
	onMounted(async () => {
		const res = await getMenuTree();
		// 过滤掉隐藏的菜单
		// const filterTree = (nodes) => {
		// 	return nodes.filter((node) => {
		// 		if (node.isShow === 1 && node.menuType === 1) {
		// 			if (node.children && node.children.length > 0) {
		// 				// 递归过滤子节点
		// 				node.children = filterTree(node.children);
		// 			}
		// 			return true;
		// 		}
		// 		return false;
		// 	});
		// };

		// // 过滤符合条件的数据并赋值给 treeData
		// treeData.value = filterTree(res);
		treeData.value = res;

		await nextTick(updateCheckState);

		// 调用异步函数获取角色菜单ID
		await fetchRoleMenuIds();
	});

	// 如果是编辑
	const fetchRoleMenuIds = async () => {
		if (props.rowInfo && props.rowInfo.id) {
			try {
				// 检查 props.rowInfo 中是否存在 role_name 属性
				if ("role_name" in props.rowInfo) {
					form.role_name = props.rowInfo.role_name;
				} else {
					console.warn("props.rowInfo 中不存在 role_name 属性");
				}
				// 获取当前角色的菜单 ID
				const res = await getRoleList(props.rowInfo.id);
				// 检查返回数据是否符合预期
				if (res && Array.isArray(res)) {
					form.menuIds = res;
					await nextTick(() => {
						// 过滤出所有叶子节点的 ID
						const leafKeys = [];
						const getLeafKeys = (nodes) => {
							nodes.forEach((node) => {
								if (!node.children || node.children.length === 0) {
									leafKeys.push(node.id);
								} else {
									getLeafKeys(node.children);
								}
							});
						};
						getLeafKeys(treeData.value);

						// 只设置选中的叶子节点
						const checkedLeafKeys = form.menuIds.filter((id) => leafKeys.includes(id));
						tree.value?.setCheckedKeys(checkedLeafKeys);

						// 再次使用 nextTick 确保树的选中状态已经更新
						nextTick(() => {
							// 更新全选状态
							updateCheckState();
						});
					});
				} else {
					console.error("返回的数据格式不符合预期:", res);
				}
			} catch (error) {
				console.error("获取角色菜单 ID 时发生错误:", error);
			}
		}
	};
	// 表单提交逻辑
	const { registerFormDone, formInstance } = useDialogForm();
	registerFormDone(async () => {
		// 获取选中节点的 ID
		const checkedKeys = tree.value.getCheckedKeys();
		// 获取半选节点的 ID
		const halfCheckedKeys = tree.value.getHalfCheckedKeys();
		// 合并选中和半选节点的 ID
		form.menuIds = [...checkedKeys, ...halfCheckedKeys];
		await saveRole(Object.assign({}, toRaw(form), props.rowInfo ? { id: props.rowInfo.id } : {}));
		return true;
	});
</script>

<style scoped>
	.tree-edit {
		display: flex;
		align-items: center;
		margin-bottom: 10px;
	}
	.tree-box {
		height: 300px;
		overflow: auto;
		border: 1px solid #ebeef5;
		border-radius: 4px;
		padding: 10px;
		margin-top: 10px;
	}
</style>
@/api/setting/role@/api/setting/menu
