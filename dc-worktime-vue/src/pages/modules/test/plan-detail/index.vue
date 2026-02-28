<template>
	<BasePage>
		<template #pageSearch>
			<div class="p-2px">
				<ElCard class="custom-card" shadow="none">
					<!-- <template #header>
					<div class="card-header">
						<div class="flex flex-items-center gap-6px cursor-pointer">
							<div @click="() => appInstance.router.back()">
								<ElIcon size="20px">
									<Back />
								</ElIcon>
							</div>

							<span>测试计划详情</span>
						</div>
					</div>
				</template> -->
					<div class="p-10px">
						<ElDescriptions :column="4">
							<ElDescriptionsItem label="项目名称：">{{
								pageData.dataSource.pro_project_name
							}}</ElDescriptionsItem>
							<ElDescriptionsItem label="测试计划名称：">{{
								pageData.dataSource.plan_name
							}}</ElDescriptionsItem>
							<ElDescriptionsItem label="测试人：">{{
								pageData.dataSource.plan_member_name
							}}</ElDescriptionsItem>
							<ElDescriptionsItem label="计划测试时间："
								>{{ pageData.dataSource.start_time }} 至
								{{ pageData.dataSource.off_time }}</ElDescriptionsItem
							>
							<ElDescriptionsItem label="下发时间：">{{
								pageData.dataSource.issue_time || "--"
							}}</ElDescriptionsItem>

							<ElDescriptionsItem label="完成时间：">{{
								pageData.dataSource.completion_time || "--"
							}}</ElDescriptionsItem>
							<ElDescriptionsItem label="状态：">{{
								statusText[pageData.dataSource.status]
							}}</ElDescriptionsItem>
						</ElDescriptions>
					</div>
				</ElCard>
			</div>
		</template>

		<template #pageBody>
			<div class="h-100% flex gap-12px p-t-10px p-r-10px p-l-10px p-b-6px">
				<div style="width: 300px; height: 100%; overflow: auto; margin-right: 16px">
					<ElTree
						:data="treeData"
						:props="treeProps"
						default-expand-all
						:render-content="renderTreeNode"
						@node-click="handleNodeClick"
						node-key="id"
						ref="tree"
					></ElTree>
				</div>
				<div class="flex-1 custom-table h-100% flex flex-col">
					<ElTable :data="tablelist" border v-loading="loading" stripe>
						<ElTableColumn label="序号" width="60" align="center" type="index"> </ElTableColumn>
						<ElTableColumn
							prop="module_name"
							width="220"
							label="功能菜单"
							show-overflow-tooltip
							align="center"
						></ElTableColumn>
						<ElTableColumn
							prop="feature_name"
							width="220"
							label="功能名称"
							show-overflow-tooltip
							align="center"
						></ElTableColumn>
						<ElTableColumn
							prop="feature_desc"
							label="功能描述"
							align="center"
							show-overflow-tooltip
						></ElTableColumn>
						<!-- <ElTableColumn prop="degree_of_difficulty_name" width="80" label="难易度" align="center">
						</ElTableColumn> -->
						<!-- <ElTableColumn
					prop="degree_of_difficulty_desc"
					label="难易度描述"
					align="center"
					show-overflow-tooltip
				></ElTableColumn> -->
					</ElTable>
				</div>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { selectModuleTree } from "@/api/test/issues";
	import BasePage from "@/core/struct/page/base-page";
	import { Back } from "@element-plus/icons-vue";
	import { reactive, onMounted, ref, nextTick } from "vue";
	import { appInstance } from "@/main";
	import { useRoute } from "vue-router";
	import { selectById } from "@/api/test/plan";
	import { selectFeatureByMIds } from "@/api/project/functionList";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";
	import { ElCard, ElDescriptions, ElDescriptionsItem, ElIcon, ElTable, ElTableColumn, ElTree } from "element-plus";
	// const route = useRoute();
	// const id = route.query.id as string;
	// 定义一个数组来存储选中的节点的 id
	const props = defineProps({
		rowId: {
			type: Number,
			default: 0
		}
	});

	const testPlanProModelIds = ref([]);
	const isLoading = ref(true);
	const statusText = {
		0: "待下发",
		1: "已下发",
		2: "已挂起",
		3: "已完成"
	};
	const tablelist = ref([]);
	const loading = ref(false);
	const query_module_id = ref("");
	const query_module_ids = ref([]);
	const query_project_id = ref("");

	const pageData = reactive({
		dataSource: {}
	});
	const fetchData = async () => {
		if (props.rowId) {
			const res = await selectById({ id: props.rowId });
			pageData.dataSource = res;
			// query_module_id.value 为res中的testPlanProModels数组中pro_model_id字段的值
			query_module_ids.value = res.testPlanProModels.map((item) => item.pro_model_id);

			isLoading.value = false;
			if (res.testPlanProModels && Array.isArray(res.testPlanProModels)) {
				const ids = res.testPlanProModels.map((item) => item.pro_model_id);

				// 调用过滤树数据的方法
				await getTreeData(res.pro_project_id, ids);
				query_project_id.value = res.pro_project_id;

				fetchTableList();
			} else {
				await getTreeData(res.pro_project_id);
			}
		}
	};
	// tablelist
	const fetchTableList = async () => {
		loading.value = true;
		const params = {
			module_ids: query_module_ids.value,
			module_id: query_module_id.value,
			project_id: query_project_id.value
		};
		const res = await selectFeatureByMIds({
			page: 1,
			size: 10000,
			params: { ...params }
		});
		tablelist.value = res.records;
		loading.value = false;
		return res;
	};

	onMounted(async () => {
		fetchData();
	});

	// 定义树的数据
	const treeData = ref([]);

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	const getTreeData = async (id, visibleIds = []) => {
		try {
			const res = await selectModuleTree({ project_id: id });
			// 过滤树数据，只保留 visibleIds 中的节点
			const filterTreeData = (nodes) => {
				return nodes.filter((node) => {
					if (visibleIds.includes(node.id)) {
						if (node.children) {
							node.children = filterTreeData(node.children);
						}
						return true;
					}
					return false;
				});
			};

			treeData.value = visibleIds.length ? filterTreeData(res) : res;
			console.log("333333333333333treeData.value", treeData.value);
			markNodeLevel(treeData.value);
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
			node.disabled = true;
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
	// 节点点击事件
	const handleNodeClick = (data, node) => {
		query_module_id.value = data.id;
		// // 判断是否有子节点
		// if (data.children.length > 0) {
		// 	// 如果有子节点，展开或折叠节点
		// 	addShow.value = true;
		// } else {
		// 	addShow.value = false;
		// }

		// 刷新表格数据
		fetchTableList();
	};

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
</script>

<style lang="scss" scoped>
	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.page-search {
		height: 328px;
		width: 100%;
		background-color: var(--el-page-color);
		border-radius: 6px;
		padding: 32px;
		.title {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 36px;
			margin: auto;
			width: 200px;
			height: 20px;
			color: #fff;
		}
	}
</style>

<style lang="scss">
	// .custom-card {
	// 	.el-card__header {
	// 		padding: 10px;
	// 	}
	// }

	// .surname {
	// 	position: absolute;
	// 	top: 0;
	// 	left: 0;
	// 	width: 48px;
	// 	height: 48px;
	// 	line-height: 48px;
	// 	text-align: center;
	// 	font-size: 20px;
	// 	color: #fff;
	// }
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
</style>
