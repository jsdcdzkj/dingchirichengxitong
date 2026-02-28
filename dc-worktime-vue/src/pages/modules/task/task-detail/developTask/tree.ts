import type { TreeInstance } from "element-plus";
import { onMounted, reactive, ref } from "vue";

export const useTaskTree = () => {
	const taskTreeProps = reactive({
		props: {
			label: "moduleName",
			children: "children"
		},
		treeData: [] as any[],
		isAllCheck: true,
		isExpanded: true
	});

	const treeInstance = ref<TreeInstance>();

	// 获取所有节点ID并全选
	const getAllNodeIds = (nodes: any[]): number[] => {
		return nodes?.reduce((acc, node) => {
			acc.push(node.id);
			if (node.children) {
				acc.push(...getAllNodeIds(node.children));
			}
			return acc;
		}, []);
	};

	// 全选或全不选
	const handleAllCheck = (isAllCheck: boolean) => {
		if (isAllCheck == true) {
			taskTreeProps.isAllCheck = true;
			const allIds = getAllNodeIds(taskTreeProps.treeData);

			treeInstance.value?.setCheckedKeys(allIds); // 设置全选
		} else {
			taskTreeProps.isAllCheck = false;
			treeInstance.value?.setCheckedKeys([]); // 设置全选
		}
	};

	// 折叠或展开
	const toggleExpand = () => {
		if (treeInstance.value) {
			for (let i = 0; i < taskTreeProps.treeData.length; i++) {
				treeInstance.value.store.nodesMap[taskTreeProps.treeData[i].id].expanded = !taskTreeProps.isExpanded;
			}
			taskTreeProps.isExpanded = !taskTreeProps.isExpanded;
		}
	};

	onMounted(() => {
		console.log(treeInstance.value);
	});

	return {
		taskTreeProps,
		handleAllCheck,
		toggleExpand,
		treeInstance
	};
};
