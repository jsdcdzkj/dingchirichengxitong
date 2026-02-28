<template>
	<div class="flex">
		<div class="tree-wrap">
			<div class="tree-btn">
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
							:name="data.children.length > 0 ? `icon-wenjian.png` : `icon-plan.png`"
							:size="{ width: '16px', height: '16px' }"
						></Icon>
						<span class="tree-label" :style="{ maxWidth: `${190 - node.level * 18}px` }">{{
							node.label
						}}</span>
					</span>
				</template>
			</ElTree>
		</div>
		<ElTable border :data="tableData" class="flex-1">
			<ElTableColumn label="序号" width="60" align="center" type="index"> </ElTableColumn>
			<ElTableColumn show-overflow-tooltip label="来源项目计划" width="180" align="center" prop="planTaskTitle">
				<template #default="scope">
					<ElLink type="primary" @click="handleView(scope.row)">{{ scope.row.planTaskTitle }}</ElLink>
				</template>
			</ElTableColumn>
			<ElTableColumn show-overflow-tooltip label="功能菜单" width="160" align="center" prop="tree_name"></ElTableColumn>
			<ElTableColumn show-overflow-tooltip label="功能名称" width="140" align="center" prop="featureName">
				<template #default="scope">
					<ElLink type="primary" @click="handleDetail(scope.row)">{{ scope.row.featureName }}</ElLink>
				</template>
			</ElTableColumn>
			<ElTableColumn show-overflow-tooltip label="截止日期" align="center" prop="end_time">
			</ElTableColumn>
			<ElTableColumn show-overflow-tooltip label="难易度" align="center" prop="degree">
				<template #default="{ row }">
					<span class="degree" v-if="row.degree == 1"
						><i class="degree-1"></i>轻松</span
					>
					<span class="degree" v-if="row.degree == 2"
						><i class="degree-2"></i>简单</span
					>
					<span class="degree" v-if="row.degree == 3"
						><i class="degree-3"></i>正常</span
					>
					<span class="degree" v-if="row.degree == 4"
						><i class="degree-4"></i>困难</span
					>
					<span class="degree" v-if="row.degree == 5"
						><i class="degree-5"></i>挑战</span
					>
				</template>
			</ElTableColumn>
			<ElTableColumn v-if="detail.iskanban" show-overflow-tooltip label="操作" align="center">
				<template #default="{ row }">
					<ElButton type="danger" @click="handleQuestion(row)" size="small">提问题</ElButton>
				</template>
			</ElTableColumn>
		</ElTable>
	</div>
</template>
<script lang="tsx" setup>
import { reactive, ref } from "vue";
import { ElTable, ElTableColumn, ElLink, ElTree, ElButton } from "element-plus";
import { createDrawerAsync } from "@/core/dialog";
import PlanDetailPage from "@/pages/modules/project/plan-detail/index.vue";
import DetailPage from "./dialog/detailPage.vue";
import DrawBug from "./drawBug.vue"
import { getTree, selectModuleTreeByMo } from "@/api/task/kanban";
import { useTaskTree } from "./tree";
import { Icon } from "@/components/icon/icon";
import { Download, Finished, Upload } from "@element-plus/icons-vue";

const props = defineProps({
	detail: {
		type: Object,
		default: {}
	},
	refreshSearch: {
		type: Function,
		default: () => () => {}
	},
});

const { taskTreeProps, handleAllCheck, toggleExpand, treeInstance } = useTaskTree();
const handleCheckChange = () => {
		const list = treeInstance.value?.getCheckedKeys(true) || [];
		if (list!!.length > 0) {
			selectModuleTreeByMo({ task_id: props.detail.id, moduls: list.join(',')}).then((res) => {
				tableData.value = res
			});
		} else {
			tableData.value = [];
		}
	};

	const tableData = ref([])
	const handleAllCheckList = (isAllCheck: boolean) => {
		handleAllCheck(isAllCheck);
		const list = treeInstance.value?.getCheckedKeys(true) || [];
		console.log('3333333333333list', list)
		if (taskTreeProps.isAllCheck) {
			selectModuleTreeByMo({ task_id: props.detail.id, moduls: list.join(',')}).then((res) => {
				tableData.value = res
			});
		} else {
			tableData.value = [];
		}
	};

const getTreeData = () => {
	getTree({task_id: props.detail.id, projectId: props.detail.project_id}).then(res => {
		console.log('000000000000res', res)
		taskTreeProps.treeData = res;
		console.log('11111111111111res', taskTreeProps.treeData)
		setTimeout(() => {
			handleAllCheckList(true)
		}, 100)
		
	})
}
getTreeData()

// 跳转计划详情
const handleView = (row: Object) => {
	createDrawerAsync(
		{ title: row.planTaskTitle, width: "1100px", showNext: false, showConfirm: false },
		{},
		<PlanDetailPage query_detail_status={1} id={row.plan_task_id} />
	);
};

// 跳转功能点清单
const handleDetail = (row: any) => {
		createDrawerAsync(
			{ title: row.featureName, showNext: false, width: "1100px", showConfirm: false },
			{},
			<DetailPage rowInfo={row} />
		);
	};

const handleQuestion = (row) => {
	createDrawerAsync(
			{ title: "提问题", showNext: false, width: "1200px", closeOnClickModal: false, closeOnPressEscape: false },
			{refreshSearch: props.refreshSearch},
			<DrawBug moudleList={taskTreeProps.treeData} detailInfo={props.detail} rowInfo={row} projectId={props.detail.project_id} />
		);
}
</script>
<style scoped lang="scss">
:deep(.el-popper) {
	max-width: 500px !important; /* 设置最大宽度 */
	line-height: 1.5 !important; /* 可选：调整行高 */
}
.textOverflow {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;

	p {
		margin: 0 !important;
	}
}

.tree-wrap {
	height: calc(100vh - 380px);
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 16px;

	.tree-btn {
		border-bottom: 1px solid rgba(0,0,0,0.06);
		padding: 0 16px 16px 16px;
	}
}

.degree {
	display: inline-block;
	width: 62px;
	height: 26px;
	font-size: 14px !important;
	color: rgba(0, 0, 0, 0.65);
	line-height: 26px;
	border-radius: 4px 4px 4px 4px;
	border: 1px solid rgba(0, 0, 0, 0.06);
	box-sizing: border-box;
	text-align: center;
	i {
		display: inline-block;
		margin-right: 6px;
		width: 10px;
		height: 10px;
		border-radius: 50%;
		&.degree-1 {
			background: #409eff;
		}
		&.degree-2 {
			background: #40bfa8;
		}
		&.degree-3 {
			background: #67c23a;
		}
		&.degree-4 {
			background: #ff9d00;
		}
		&.degree-5 {
			background: #f56c6c;
		}
	}
}
</style>