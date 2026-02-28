<template>
	<div class="detail-content">
		<div class="content-header">
			<div class="tree-oper flex flex-justify-between items-center p-16px">
				<ElButton
					:icon="Finished"
					plain
					style="width: 116px"
					@click="handleAllCheckList(!taskTreeProps.isAllCheck)"
				>
					{{ taskTreeProps.isAllCheck ? "全不选" : "全选" }}
				</ElButton>
				<ElButton
					:icon="taskTreeProps.isExpanded ? Upload : Download"
					style="width: 116px"
					@click="toggleExpand"
				>
					{{ taskTreeProps.isExpanded ? "折叠" : "展开" }}
				</ElButton>
			</div>
			<div class="flex flex-justify-between items-center p-16px w-full">
				<ElRadioGroup
					v-model:model-value="pageData.select"
					@change="handleSelectChange"
					class="flex w-full custom-radio-group"
				>
					<ElRadioButton value="1" style="flex: 1">
						<div class="flex-inline items-center">
							全部<span class="tag"> {{ listData.allData.length }} </span>
						</div>
					</ElRadioButton>
					<ElRadioButton value="2" style="flex: 1"
						><div class="flex-inline items-center">
							已分配<span class="tag"> {{ listData.assigned.length }} </span>
						</div></ElRadioButton
					>
					<ElRadioButton value="3" style="flex: 1"
						><div class="flex-inline items-center">
							未分配<span class="tag"> {{ listData.unassigned.length }} </span>
						</div></ElRadioButton
					>
				</ElRadioGroup>
			</div>
		</div>
		<div class="tree-content">
			<div class="tree-module">
				<!-- show-checkbox -->
				<ElTree
					:data="taskTreeProps.treeData"
					default-expand-all
					ref="treeInstance"
					:props="{
						children: 'children',
						label: 'moduleName'
					}"
					node-key="id"
					show-checkbox
					@check="handleCheckChange"
				>
					<template #default="{ node, data }">
						<span class="flex flex-items-center gap-10px">
							<Icon
								:name="data.type == 1 ? `icon-wenjian.png` : `icon-plan.png`"
								:size="{ width: '16px', height: '16px' }"
							></Icon>
							<span class="tree-label" :style="{ maxWidth: `${190 - node.level * 18}px` }">{{
								node.label
							}}</span>
						</span>
					</template>
				</ElTree>
			</div>
			<div class="tree-table">
				<ElTable border height="100%" :data="tableData">
					<ElTableColumn label="序号" type="index" width="80" align="center"></ElTableColumn>
					<ElTableColumn label="来源项目计划" prop="plan_title" align="center"></ElTableColumn>
					<ElTableColumn label="是否分配" prop="name" align="center">
						<template #default="{ row }">
							<ElTag v-if="row.is_assign == 1" type="success">已分配</ElTag>
							<ElTag v-else type="danger">未分配</ElTag>
						</template>
					</ElTableColumn>
					<ElTableColumn label="功能菜单" prop="module_name" align="center"></ElTableColumn>
					<ElTableColumn label="功能名称" prop="feature_name" align="center"></ElTableColumn>
					<ElTableColumn label="截止日期" prop="plan_end_date" align="center"></ElTableColumn>
					<!-- <ElTableColumn label="难易度" prop="name"></ElTableColumn> -->
				</ElTable>
			</div>
		</div>
	</div>
</template>

<script lang="tsx" setup>
	import { getTaskIdsByFeatureIds, selectModuleTree } from "@/api/project/plan";
	import { appInstance } from "@/main";
	import { reactive, ref, watchEffect } from "vue";
	import { Icon } from "@/components/icon/icon";
	import { ElTree, ElButton, ElRadioGroup, ElRadioButton, ElTable, ElTableColumn, ElTag } from "element-plus";

	import { array, number } from "vue-types";
	import { Download, Finished, Upload } from "@element-plus/icons-vue";
	import { useTaskTree } from "./tree";
	const data = ref();
	const props = defineProps({
		modules: array<any>().def([]),
		planId: number().def(0)
	});

	const pageData = reactive({
		select: "1"
	});
	const listData = reactive({
		allData: [],
		assigned: [],
		unassigned: []
	});

	const tableData = ref([]);

	const handleCheckChange = () => {
		const list = treeInstance.value?.getCheckedKeys(true) || [];
		if (list!!.length > 0) {
			getTaskIdsByFeatureIds(props.planId, treeInstance.value?.getCheckedKeys(true)).then((res) => {
				listData.allData = res.data.allData;
				listData.assigned = res.data.assigned;
				listData.unassigned = res.data.unassigned;
				tableData.value = res.data.allData;
			});
		} else {
			listData.allData = [];
			listData.assigned = [];
			listData.unassigned = [];
			tableData.value = [];
		}
	};

	const handleSelectChange = (val: string) => {
		switch (val) {
			case "1":
				tableData.value = listData.allData;
				break;
			case "2":
				tableData.value = listData.assigned;
				break;
			case "3":
				tableData.value = listData.unassigned;
				break;
		}
	};

	const refreshPlanTree = () => {
		// 数据重新组装
		function coverTree(list: any[]) {
			for (const item of list) {
				Reflect.set(item, "type", 1);
				Reflect.set(item, "id", Math.random().toString(16).slice(0, 12));
				if (item.children && item.children.length == 0) {
					if (item.functions.length > 0) {
						item.children = item.functions.map((item: any) => {
							return {
								moduleName: item.feature_name,
								type: 2,
								id: item.id
							};
						});
					} else {
						item.disabled = true;
					}
				} else {
					coverTree(item.children);
				}
			}
		}
		if (props.modules && props.modules.length > 0) {
			coverTree(props.modules);
			data.value = props.modules;
		}
	};

	const { taskTreeProps, handleAllCheck, toggleExpand, treeInstance } = useTaskTree();

	const handleAllCheckList = (isAllCheck: boolean) => {
		handleAllCheck(isAllCheck);
		if (taskTreeProps.isAllCheck) {
			getTaskIdsByFeatureIds(props.planId, treeInstance.value?.getCheckedKeys(true)).then((res) => {
				listData.allData = res.data.allData;
				listData.assigned = res.data.assigned;
				listData.unassigned = res.data.unassigned;
				tableData.value = res.data.allData;
			});
		} else {
			listData.allData = [];
			listData.assigned = [];
			listData.unassigned = [];
			tableData.value = [];
		}
	};
	taskTreeProps.treeData = props.modules;

	// watchEffect(() => {
	// 	refreshPlanTree();
	// });
</script>

<style lang="scss" scoped>
	.ppp-wu {
		height: 82px;
		background: #f4f6fd;
		border-radius: 8px 8px 8px 8px;
		display: flex;
		align-items: center;
		padding: 0 24px;
		justify-content: space-between;
		margin-bottom: 10px;
		.title {
			font-family:
				Alibaba PuHuiTi 3,
				Alibaba PuHuiTi 30;
			font-weight: normal;
			font-size: 16px;
			color: rgba(0, 0, 0, 0.85);
			text-align: center;
			font-style: normal;
			text-transform: none;
		}
		.desc {
			font-family:
				Alibaba PuHuiTi 3,
				Alibaba PuHuiTi 30;
			font-weight: normal;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			text-align: center;
			font-style: normal;
			text-transform: none;
		}
	}

	.detail-content {
		height: 100%;
		overflow: hidden;
		border: 1px solid rgba(0, 0, 0, 0.06);
		display: flex;
		flex-direction: column;
		.content-header {
			height: 56px;
			border-radius: 0px 0px 0px 0px;
			border-bottom: 1px solid rgba(0, 0, 0, 0.06);
			display: flex;
			.tree-oper {
				width: 270px;
				height: 100%;
				border-right: 1px solid rgba(0, 0, 0, 0.06);
			}
		}
		.tree-content {
			flex: 1;
			overflow: hidden;
			display: flex;
			.tree-module {
				width: 270px;
				height: 100%;
				padding-right: 10px;
				overflow-x: hidden;
				overflow-y: auto;
				border-right: 1px solid rgba(0, 0, 0, 0.06);
				flex-shrink: 0;
			}
			.tree-table {
				flex: 1;
				overflow: hidden;
				padding: 16px;
			}
		}
	}
	.el-radio-button__inner {
		width: 100%;
	}

	.tree-label {
		display: inline-block;
		max-width: 190px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	.tag {
		padding: 0 5px;
		height: 20px;
		display: flex;
		align-items: center;
		text-align: center;
		font-size: 14px;
		color: #fff;
		background: #f56c6c;
		border-radius: 27px 27px 27px 27px;
		border: 1px solid #ffffff;
		margin-left: 4px;
	}
</style>
