<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="86px" label-position="top" class="p-r-10px">
		<ElRow :gutter="10">
			<ElCol :span="12">
				<ElFormItem label="项目名称" prop="pro_project_id">
					<ElSelect
						v-model="form.pro_project_id"
						placeholder="请选择项目名称"
						clearable
						@change="handleChange"
					>
						<template v-for="option in moudleList" :key="option.id">
							<ElOption :label="option.projectName" :value="option.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="测试计划名称" prop="plan_name">
					<ElInput v-model="form.plan_name" placeholder="请输入测试计划名称" />
				</ElFormItem>
			</ElCol>

			<ElCol :span="12">
				<ElFormItem label="计划测试时间" prop="planTime">
					<ElDatePicker
						v-model="form.planTime"
						type="daterange"
						format="YYYY-MM-DD"
						value-format="YYYY-MM-DD"
						range-separator="至"
						start-placeholder="开始时间"
						end-placeholder="结束时间"
						style="width: 100%"
					/>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="测试人" prop="plan_member">
					<ElSelect v-model="form.plan_member" placeholder="请选择测试人" clearable>
						<template v-for="option in userList" :key="option.id">
							<ElOption :label="option.realName" :value="option.id"></ElOption>
						</template>
					</ElSelect>
				</ElFormItem>
			</ElCol>
			<ElCol :span="12">
				<ElFormItem label="计划测试内容" prop="testPlanProModelIds">
					<!-- 带复选框的树结构 -->
					<div style="width: 100%; max-height: calc(100vh - 400px); min-height: 260px; overflow-y: auto">
						<ElTree
							:data="treeData"
							:props="treeProps"
							default-expand-all
							:render-content="renderTreeNode"
							show-checkbox
							node-key="id"
							:checked-keys="form.testPlanProModelIds"
							@check="handleTreeCheck"
							ref="tree"
						></ElTree>
					</div>
				</ElFormItem>
			</ElCol>
		</ElRow>
	</ElForm>
</template>

<script setup lang="tsx">
	import { ref, onMounted, reactive, toRaw, nextTick } from "vue";
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
	import { selectModuleTree } from "@/api/test/issues";
	import { addUpdateInfo, selectById } from "@/api/test/plan";
	import { selectPageList } from "@/api/project/list";
	import { getListAll } from "@/api/setting/user";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";

	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		},
		refreshList: {
			type: Function,
			default: () => () => {}
		}
	});

	const form = reactive({
		pro_project_id: "",
		plan_name: "",
		plan_member: "",
		planTime: "",
		testPlanProModelIds: []
	});
	const rules = reactive({
		pro_project_id: [
			{
				required: true,
				message: "请选择项目名称",
				trigger: "blur"
			}
		],
		plan_name: [
			{
				required: true,
				message: "请输入测试计划名称",
				trigger: "blur"
			}
		],
		plan_member: [
			{
				required: true,
				message: "请选择测试人",
				trigger: "blur"
			}
		],
		planTime: [
			{
				required: true,
				message: "请选择计划测试时间",
				trigger: "blur"
			}
		],
		testPlanProModelIds: [
			{
				required: true,
				message: "请选择计划测试内容",
				trigger: "change", // 改为change触发
				type: "array" // 明确验证数组类型
			}
		]
	});

	const moudleList = ref([]);
	const userList = ref([]);

	const { registerFormDone, formInstance } = useDialogForm();

	// 初始化组员列表
	onMounted(async () => {
		const moudleData = await selectPageList({
			pageNo: 1,
			pageSize: 999
		});
		moudleList.value = moudleData.records;

		const data = await getListAll({});
		userList.value = data;
		// 处理 planTime 字段 rowInfo 返回的是开始时间和结束时间  start_time end_time
		// if (props.rowInfo.start_time && props.rowInfo.off_time) {
		// 	form.planTime = [props.rowInfo.start_time, props.rowInfo.off_time];
		// 	console.log("form.planTime", form.planTime);
		// }
		// // 处理 testPlanProModelIds 字段
		// if (props.rowInfo.testPlanProModelIds) {
		// 	form.testPlanProModelIds = props.rowInfo.testPlanProModelIds;
		// }
		// // 处理其他字段
		// Object.keys(props.rowInfo).forEach((key) => {
		// 	if (key !== "testPlanProModelIds") {
		// 		form[key] = props.rowInfo[key];
		// 	}
		// });

		if (props.rowInfo.id) {
			// 渲染树形结构

			const res = await selectById({ id: props.rowInfo.id });
			form.pro_project_id = res.pro_project_id;
			await handleChange();
			form.plan_name = res.plan_name;
			form.plan_member = Number(res.plan_member);
			form.planTime = [res.start_time, res.off_time];
			// 从 testPlanProModels 中提取 pro_model_id
			if (res.testPlanProModels && Array.isArray(res.testPlanProModels)) {
				const ids = res.testPlanProModels.map((item) => item.pro_model_id);
				form.testPlanProModelIds = ids;
			}

			// 更新树组件的选中状态
			nextTick(() => {
				if (tree.value) {
					tree.value.setCheckedKeys(form.testPlanProModelIds);
					formInstance.value?.validateField("testPlanProModelIds");
				}
			});
		}
	});

	// 定义树的数据
	const treeData = ref([]);

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	const handleChange = async () => {
		const selectedNodeId = form.pro_project_id;

		try {
			await getTreeData(selectedNodeId);
			console.log("树数据获取并设置完成");
		} catch (error) {
			console.error("获取树数据时出错:", error);
		}
	};
	const handleTreeCheck = (checkedNodes, { checkedKeys }) => {
		nextTick(() => {
			if (tree.value) {
				const halfCheckedKeys = tree.value.getHalfCheckedKeys();
				const allSelectedIds = [...checkedKeys, ...halfCheckedKeys];
				form.testPlanProModelIds = allSelectedIds;
			}
		});
		formInstance.value?.validateField("testPlanProModelIds");
	};

	const getTreeData = async (id) => {
		try {
			const res = await selectModuleTree({ project_id: id });

			treeData.value = res;
			markNodeLevel(treeData.value);
			const getAllNodeIds = (nodes: any[]): number[] => {
				if (!nodes || !Array.isArray(nodes)) {
					return [];
				}
				return nodes.reduce((acc, node) => {
					acc.push(node.id);
					if (node.children) {
						acc.push(...getAllNodeIds(node.children));
					}
					return acc;
				}, []);
			};
			nextTick(() => {
				if (tree.value) {
					// 直接设置checkedKeys并更新form
					const allIds = getAllNodeIds(treeData.value);
					tree.value.setCheckedKeys(allIds);
					// 获取全选和半选的节点 ID
					const checkedKeys = tree.value.getCheckedKeys();
					const halfCheckedKeys = tree.value.getHalfCheckedKeys();
					const allSelectedIds = [...checkedKeys, ...halfCheckedKeys];
					form.testPlanProModelIds = allSelectedIds;
					formInstance.value?.validateField("testPlanProModelIds");
				}
			});
		} catch (error) {
			console.error("获取树数据时出错:", error);
		}
	};

	// 自动标记层级关系
	const markNodeLevel = (nodes, level = 1) => {
		// Add null check
		if (!nodes || !Array.isArray(nodes)) {
			return;
		}
		nodes.forEach((node) => {
			node.isRoot = level === 1;
			if (node.children) {
				markNodeLevel(node.children, level + 1);
			}
		});
	};

	// 配置项
	const treeProps = ref({
		label: "moduleName",
		children: "children"
	});

	// 渲染函数
	const renderTreeNode = (h, { node, renderParams, store }) => {
		// 防御性检查
		if (!node?.data) return null;

		// 正确解构数据
		const { data } = node;

		// 检查是否为根节点
		const isRoot = data?.isRoot ?? false;

		return (
			<span class="tree-node">
				<img
					src={isRoot ? fileIcon : pageIcon}
					style="width: 16px; height: 16px; margin-right: 8px;vertical-align: text-bottom;"
				/>
				{data.moduleName}
			</span>
		);
	};

	registerFormDone(async () => {
		// 确保保存前获取最新的全选和半选状态
		if (tree.value) {
			const checkedKeys = tree.value.getCheckedKeys();
			const halfCheckedKeys = tree.value.getHalfCheckedKeys();
			const allSelectedIds = [...checkedKeys, ...halfCheckedKeys];
			form.testPlanProModelIds = allSelectedIds;
		}
		// 处理 planTime 字段
		if (form.planTime && Array.isArray(form.planTime)) {
			form.start_time = form.planTime[0];
			form.off_time = form.planTime[1];
		}

		await addUpdateInfo(
			Object.assign({}, toRaw(form), props.rowInfo ? { id: props.rowInfo.id } : {}, {
				testPlanProModelIds: form.testPlanProModelIds
			})
		);

		treeData.value = [];
		props.refreshList();
		return true;
	});
</script>
