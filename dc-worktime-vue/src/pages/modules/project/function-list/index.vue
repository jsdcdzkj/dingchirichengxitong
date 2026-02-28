<template>
	<BasePage v-bind="page" :asideLeftWidth="280">
		<template #pageLeft>
			<div class="flex-1 flex flex-col w-100% p-10px">
				<div class="flex p-b-10px flex-items-center flex-justify-between m-t-8px p-l-10px p-r-10px">
					<div class="font-size-18px font-b">功能清单</div>
					<div>共 {{ totalAll }} 条</div>
				</div>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<div>
				<!-- 两个按钮 一个全部展开 一个全部折叠-->
				<div class="status-btn-box">
					<div class="status-btn" @click="expandAll">
						<ElIcon color="rgba(0, 0, 0, 0.65)" size="16">
							<Download />
						</ElIcon>
						<span class="m-l-4px">展开</span>
					</div>
					<div class="status-btn" @click="collapseAll">
						<ElIcon color="rgba(0, 0, 0, 0.65)" size="16">
							<Upload />
						</ElIcon>
						<span class="m-l-4px">折叠</span>
					</div>
				</div>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<div class="tree-list-con">
				<ElTree
					:data="treeData"
					:props="treeProps"
					:render-content="renderTreeNode"
					@node-click="handleNodeClick"
					ref="tree"
					node-key="id"
					:default-expanded-keys="expandedKeys"
				></ElTree>
			</div>
			<div class="tree-edit-box">
				<!-- 新增 -->
				<div class="flex flex-justify-center flex-items-center" @click="addFun">
					<ElIcon color="#1B86FF" size="16">
						<CirclePlus />
					</ElIcon>
					<span class="m-l-4px">新增</span>
				</div>
				<!-- <div class="flex flex-justify-center flex-items-center">
					<ElIcon color="rgba(0,0,0,0.4)" size="16">
						<EditPen />
					</ElIcon>
					<span class="m-l-4px">编辑</span>
				</div>
				<div class="flex flex-justify-center flex-items-center">
					<ElIcon color="rgba(0,0,0,0.4)" size="16">
						<Delete />
					</ElIcon>
					<span class="m-l-4px">删除</span>
				</div> -->
			</div>
		</template>
		<template #pageHeader></template>
		<template #pageSearch></template>
		<template #pageTitle>功能列表</template>
		<template #pageBody>
			<div
				class="flex flex-justify-between flex-items-center m-t-0px m-b-24px p-b-10px"
				style="border-bottom: 1px solid rgba(0, 0, 0, 0.06)"
			>
				<div class="title font-size-18px font-b font-bold">功能列表</div>
				<div>
					<ElButton type="danger" :icon="Delete" @click="batchDelete"> 删除 </ElButton>
					<ElButton type="success" :icon="CirclePlus" @click="add" :disabled="addShow"> 新增 </ElButton>
				</div>
			</div>
			<div class="statistics-box">
				<div class="statistics-complete">
					<div class="complete-item">
						<div class="complete-title">未开发</div>
						<div class="complete-chart">
							<div class="chart-back">
								<ElTooltip
									:content="`未开发任务占比: ${getPercentage(statisticsNum.unTaskSum, statisticsNum.taskSum)}%`"
									placement="top"
								>
									<div
										:style="{
											width: `${getPercentage(statisticsNum.unTaskSum, statisticsNum.taskSum)}%`,
											height: '100%',
											backgroundColor: '#F56C6C'
										}"
										class="anima"
									></div>
								</ElTooltip>
							</div>
						</div>
						<div class="complete-num">{{ statisticsNum.unTaskSum }}</div>
					</div>
					<div class="complete-item">
						<div class="complete-title">进行中</div>
						<div class="chart-back">
							<ElTooltip
								:content="`进行中任务占比: ${getPercentage(statisticsNum.doingTaskSum, statisticsNum.taskSum)}%`"
								placement="top"
							>
								<div
									:style="{
										width: `${getPercentage(statisticsNum.doingTaskSum, statisticsNum.taskSum)}%`,
										height: '100%',
										backgroundColor: '#409EFF'
									}"
									class="anima"
								></div>
							</ElTooltip>
						</div>
						<div class="complete-num">{{ statisticsNum.doingTaskSum }}</div>
					</div>
					<div class="complete-item">
						<div class="complete-title">已完成</div>
						<div class="complete-chart">
							<div class="chart-back">
								<ElTooltip
									:content="`已完成任务占比: ${getPercentage(statisticsNum.doneTaskSum, statisticsNum.taskSum)}%`"
									placement="top"
								>
									<div
										:style="{
											width: `${getPercentage(statisticsNum.doneTaskSum, statisticsNum.taskSum)}%`,
											height: '100%',
											backgroundColor: '#67C23A'
										}"
										class="anima"
									></div>
								</ElTooltip>
							</div>
						</div>
						<div class="complete-num">{{ statisticsNum.doneTaskSum }}</div>
					</div>
				</div>

				<div class="statistics-facility">
					<div class="facility-item">
						<div class="facility-num">{{ statisticsNum.num1 }}</div>
						<div class="facility-title">挑战级</div>
						<div class="facility-back"></div>
					</div>
					<div class="facility-item">
						<div class="facility-num">{{ statisticsNum.num2 }}</div>
						<div class="facility-title">困难级</div>
						<div class="facility-back back-1"></div>
					</div>
					<div class="facility-item">
						<div class="facility-num">{{ statisticsNum.num3 }}</div>
						<div class="facility-title">正常级</div>
						<div class="facility-back back-2"></div>
					</div>
					<div class="facility-item">
						<div class="facility-num">{{ statisticsNum.num4 }}</div>
						<div class="facility-title">简单级</div>
						<div class="facility-back back-3"></div>
					</div>
					<div class="facility-item">
						<div class="facility-num">{{ statisticsNum.num5 }}</div>
						<div class="facility-title">轻松级</div>
						<div class="facility-back back-4"></div>
					</div>
				</div>
			</div>

			<!-- 表格 -->
			<ElTable
				:data="tablelist"
				border
				v-loading="loading"
				:row-key="(row) => row.id"
				stripe
				@selection-change="handleSelectionChange"
			>
				<ElTableColumn type="selection" align="center"></ElTableColumn>
				<ElTableColumn label="序号" width="80" align="center" type="index"> </ElTableColumn>
				<ElTableColumn
					prop="feature_name"
					min-width="180"
					label="功能名称"
					show-overflow-tooltip
					align="center"
				>
					<template #default="{ row }">
						<!-- 可以点击查看详情 link-->
						<ElLink type="primary" @click="handleDetail(row)">
							{{ row.feature_name }}
						</ElLink>
					</template>
				</ElTableColumn>
				<!-- 历史开发者 -->
				<ElTableColumn
					prop="devNames"
					min-width="160"
					label="历史开发者"
					show-overflow-tooltip
					align="center"
				></ElTableColumn>
				<ElTableColumn
					prop="module_name"
					min-width="260"
					label="功能菜单"
					show-overflow-tooltip
					align="center"
				></ElTableColumn>
				<!-- <ElTableColumn
					prop="feature_desc"
					label="功能描述"
					align="center"
					min-width="220"
					show-overflow-tooltip
				></ElTableColumn> -->
				<ElTableColumn prop="degree_of_difficulty_name" width="88" label="难易度" align="center">
					<template #default="{ row }">
						<span class="degree" v-if="row.degree_of_difficulty == 1"><i class="degree-1"></i>轻松</span>
						<span class="degree" v-if="row.degree_of_difficulty == 2"><i class="degree-2"></i>简单</span>
						<span class="degree" v-if="row.degree_of_difficulty == 3"><i class="degree-3"></i>正常</span>
						<span class="degree" v-if="row.degree_of_difficulty == 4"><i class="degree-4"></i>困难</span>
						<span class="degree" v-if="row.degree_of_difficulty == 5"><i class="degree-5"></i>挑战</span>
					</template>
				</ElTableColumn>
				<!-- <ElTableColumn
					prop="degree_of_difficulty_desc"
					label="难易度描述"
					align="center"
					show-overflow-tooltip
				></ElTableColumn> -->
				<ElTableColumn prop="createTime" width="180" label="创建时间" align="center"></ElTableColumn>
				<ElTableColumn prop="actions" label="操作" width="140px" align="center">
					<template #default="{ row }">
						<ElButton size="small" type="primary" plain @click="handleEdit(row)"> 编辑 </ElButton>
						<ElButton size="small" type="danger" plain @click="handleDelete(row.id)"> 删除 </ElButton>
					</template>
				</ElTableColumn>
			</ElTable>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { ref, onMounted, computed, nextTick, onUpdated } from "vue";
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import DetailPage from "./dialog/detailPage.vue";
	import Add from "./dialog/add.vue";
	import { markRaw } from "vue";
	import { Plus, EditPen, Delete, CirclePlus, DocumentAdd, Download, Upload } from "@element-plus/icons-vue";
	import { selectModuleTree, selectProjectList, selectPage, delDataById, batchDeletion } from "@/api/test/issues";
	import { selectFeaturePage, deleteFeature, deleteModule } from "@/api/project/functionList";
	import { appInstance } from "@/main";
	import { ElIcon, ElMessageBox, type Action, ElTree, ElMessage, ElLoading, ElTooltip } from "element-plus";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";
	import { object } from "vue-types";
	// 定义下拉选的数据
	const options = ref([]);
	const projectName = ref("");
	const tablelist = ref([]);
	const multipleSelection = ref([]);
	const query_module_id = ref("");
	const totalAll = ref(0);
	const total = ref(0);
	const loading = ref(false);
	const moudleId = ref(0);
	const addShow = ref(true);
	const statusText = {
		1: "待指派",
		2: "待开始/未解决",
		3: "进行中",
		4: "待复测/已完成",
		5: "已解决",
		6: "已挂起",
		7: "已暂停"
	};
	const handlePaginationChange = async (pageNo: number, pageSize: number) => {
		loading.value = true;
		try {
			if (page.pagination) {
				page.pagination.currentPage = pageNo;
				page.pagination.pageSize = pageSize;
				await page.form.onSearch(page.form.value);
			}
		} finally {
			loading.value = false;
		}
	};
	const formConfig: FormConfig = {
		span: 8,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: false,
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "功能名称",
				prop: "feature_name",
				attrs: {
					placeholder: "请输入功能名称",
					clearable: true,
					onkeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							refreshList();
						}
					}
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "创建时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			}
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "",
		listApi: async (data) => {}
		// pagination: {
		// 	currentPage: 1,
		// 	pageSize: 10,
		// 	total: total,
		// 	background: true,
		// 	layout: "prev, pager, next, jumper, ->, total",
		// 	onChange: handlePaginationChange
		// }
	});
	const checkSelectable = (row) => {
		return row.status == 1;
	};

	const handleSelectionChange = (val) => {
		multipleSelection.value = val;
	};
	// 批量删除
	const batchDelete = async () => {
		if (multipleSelection.value.length === 0) {
			// 没有选中任何行
			ElMessage.warning("请选择要删除的行");
			return;
		}
		const ids = multipleSelection.value.map((item) => item.id);
		try {
			await deleteFeature(ids);
			ElMessage.success("批量删除成功");
			refreshList();
		} catch (error) {
			console.error("批量删除失败:", error);
		}
	};
	const add = () => {
		createDrawerAsync(
			{ title: "新增", showNext: true, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail
				treeData={treeData.value}
				showNext={true}
				moudleIds={moudleId.value}
				projectId={appInstance.projectItem.id}
			/>
		);
	};
	const handleEdit = (row: any) => {
		createDrawerAsync(
			{ title: "编辑", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail treeData={treeData.value} projectId={appInstance.projectItem.id} rowInfo={row} />
		);
	};
	const addFun = () => {
		createModelAsync(
			{ title: "新增节点", showNext: true, width: "640px", closeOnClickModal: false, closeOnPressEscape: false },
			{ getTreeData },
			<Add treeData={treeData.value} showNext={true} projectId={appInstance.projectItem.id} />
		);
	};
	const addFun2 = (id: any) => {
		createModelAsync(
			{ title: "新增节点", showNext: true, width: "640px", closeOnClickModal: false, closeOnPressEscape: false },
			{ getTreeData },
			<Add treeData={treeData.value} showNext={true} projectId={appInstance.projectItem.id} selectId={id} />
		);
	};
	const handleDetail = (row: any) => {
		createDrawerAsync(
			{ title: "详情", showNext: false, width: "1056px", showConfirm: false },
			{ refreshList },
			<DetailPage rowId={row.id} moduleList={treeData.value} />
		);
	};
	const handleDelete = (id: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteFeature([id]).then(() => {
						ElMessage.success("删除成功");
						refreshList();
					});
				}
			}
		});
	};
	// const statisticsNum
	const statisticsNum = ref({
		num1: 0,
		num2: 0,
		num3: 0,
		num4: 0,
		num5: 0,
		unTaskSum: 0,
		doingTaskSum: 0,
		doneTaskSum: 0,
		taskSum: 0
	});

	const getPercentage = (numerator, denominator) => {
		if (denominator === 0) {
			return 0;
		}
		return ((numerator / denominator) * 100).toFixed(2);
	};

	page.form.onSearch = async (data) => {
		loading.value = true;
		try {
			const [start_date, end_date] = data.time || [];
			const params = {
				...data,
				// 添加 query_module_id 作为查询条件
				module_id: query_module_id.value
			};

			delete params.time;
			// const project_id = 1;
			const project_id = appInstance.projectItem.id;
			// const pageNo = page.pagination.currentPage;
			// const pageSize = page.pagination.pageSize;
			const res = await selectFeaturePage({
				page: 1,
				size: 10000,
				params: { ...params, start_date, end_date, project_id }
				// pageNo,
				// pageSize
			});

			tablelist.value = res.page.records;
			total.value = res.page.total;
			statisticsNum.value = res.statistics;
			return res;
		} finally {
			loading.value = false;
		}
	};
	page.form.onReset = async () => {
		await page.form.onSearch(page.form.value);
	};

	const refreshList = async () => {
		getTreeData2();
		await page.form.onSearch(page.form.value);
	};

	// 定义树的数据
	const treeData = ref([]);

	onMounted(async () => {
		getTreeData();
	});

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	const expandedKeys = ref<string[]>([]); // 用于存储展开的节点 key

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
			expandedKeys.value = newExpandedKeys;
		}

		const res = await selectModuleTree({
			project_id: appInstance.projectItem.id
		});

		const processedTreeData = markNodeLevel(res);
		treeData.value = processedTreeData;

		moudleId.value = res[0].id;

		await page.form.onSearch(page.form.value);
		getTotalAll();
	};

	const getTreeData2 = async () => {
		if (tree.value) {
			// 获取当前展开的节点
			const currentExpandedNodes = tree.value.store.nodesMap;
			const newExpandedKeys = [];
			for (const key in currentExpandedNodes) {
				if (currentExpandedNodes[key].expanded) {
					newExpandedKeys.push(currentExpandedNodes[key].data.id);
				}
			}
			expandedKeys.value = newExpandedKeys;
		}

		const res = await selectModuleTree({
			project_id: appInstance.projectItem.id
		});

		const processedTreeData = markNodeLevel(res);
		treeData.value = processedTreeData;
		getTotalAll();
	};

	// 获取总条数totalAll
	const getTotalAll = async () => {
		const res = await selectFeaturePage({
			page: 1,
			size: 10000,
			params: {
				project_id: appInstance.projectItem.id
			}
		});
		totalAll.value = res.page.total;
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

	// 配置项
	const treeProps = ref({
		label: "moduleName",
		children: "children"
	});
	// 节点点击事件
	const handleNodeClick = (data, node) => {
		moudleId.value = data.id;
		query_module_id.value = data.id;
		// 判断是否有子节点
		if (data.children.length > 0) {
			// 如果有子节点，展开或折叠节点
			addShow.value = true;
		} else {
			addShow.value = false;
		}

		page.form.onSearch(page.form.value);
	};

	// 添加节点操作方法
	const handleEditNode = (nodeData: any) => {
		createModelAsync(
			{ title: "新增节点", showNext: false, width: "640px", closeOnClickModal: false, closeOnPressEscape: false },
			{ getTreeData },
			<Add treeData={treeData.value} projectId={appInstance.projectItem.id} rowInfo={nodeData} />
		);
	};

	const handleDeleteNode = (nodeData: any) => {
		ElMessageBox.confirm("确认删除该节点？", "提示", {
			confirmButtonText: "确定",
			cancelButtonText: "取消"
		}).then(() => {
			// 这里添加实际删除逻辑
			deleteModule({ id: nodeData.id }).then(() => {
				getTreeData();
				getTreeData2();
			});
		});
	};

	// 定义响应式变量存储溢出状态
	const overflowMap = ref<Record<string, boolean>>({});

	// 检测溢出状态的方法
	const checkOverflow = (el: HTMLElement, id: string) => {
		nextTick(() => {
			const isOverflow = el.scrollWidth > el.clientWidth;
			overflowMap.value[id] = isOverflow;
		});
	};

	// 修改后的渲染函数
	const renderTreeNode = (h, { node, renderParams, store }) => {
		if (!node?.data) return null;
		const { data } = node;

		const isFolder = data.hasChildren; // 根据标记判断是否为文件夹

		const handleAddNode = (e) => {
			e.stopPropagation();
			if (data.functions && data.functions.length > 0) {
				ElMessage.warning("当前节点下有功能，无法创建子节点");
				return;
			}

			addFun2(data.id);
		};

		return (
			<div class="node-container">
				<div class="tree-node">
					<img
						src={isFolder ? fileIcon : pageIcon}
						style="width: 16px; height: 16px; margin-right: 8px;vertical-align: text-bottom;"
					/>
					<ElTooltip
						content={data.moduleName}
						placement="top"
						disabled={!overflowMap.value[data.id]} // 动态禁用状态
						popper-class="custom-tooltip"
					>
						<span class="node-text" ref={(el) => el && checkOverflow(el, data.id)}>
							{data.moduleName}
						</span>
					</ElTooltip>
				</div>
				<div class="node-actions">
					<div class="actions-btn" onClick={handleAddNode}>
						<ElIcon>
							<DocumentAdd color="#409eff" />
						</ElIcon>
					</div>
					<div
						class="actions-btn"
						onClick={(e) => {
							e.stopPropagation();
							handleEditNode(data);
						}}
					>
						<ElIcon>
							<EditPen color="#409eff" />
						</ElIcon>
					</div>
					<div
						class="actions-btn"
						onClick={(e) => {
							e.stopPropagation();
							handleDeleteNode(data);
						}}
					>
						<ElIcon>
							<Delete color="#f56c6c" />
						</ElIcon>
					</div>
				</div>
			</div>
		);
	};
	const expandAll = () => {
		if (tree.value && tree.value.store.root.childNodes) {
			tree.value.store.root.childNodes.forEach((node) => {
				node.expanded = true;
				// 递归展开所有子节点
				expandChildren(node);
			});
			// 更新展开的节点 key
			const allKeys = getAllNodeKeys(tree.value.store.root.childNodes);
			expandedKeys.value = allKeys;
		}
	};

	const collapseAll = () => {
		if (tree.value && tree.value.store.root.childNodes) {
			tree.value.store.root.childNodes.forEach((node) => {
				node.expanded = false;
				// 递归折叠所有子节点
				collapseChildren(node);
			});
			// 清空展开的节点 key
			expandedKeys.value = [];
		}
	};

	// 递归展开子节点
	const expandChildren = (node) => {
		if (node.childNodes) {
			node.childNodes.forEach((child) => {
				child.expanded = true;
				expandChildren(child);
			});
		}
	};

	// 递归折叠子节点
	const collapseChildren = (node) => {
		if (node.childNodes) {
			node.childNodes.forEach((child) => {
				child.expanded = false;
				collapseChildren(child);
			});
		}
	};

	// 递归获取所有节点的 key
	const getAllNodeKeys = (nodes) => {
		let keys = [];
		nodes.forEach((node) => {
			keys.push(node.data.id);
			if (node.childNodes) {
				keys = keys.concat(getAllNodeKeys(node.childNodes));
			}
		});
		return keys;
	};
</script>
<style scoped lang="scss">
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
	.tree-list-con {
		padding: 16px 12px;
		height: calc(100% - 210px);
		box-sizing: border-box;
		overflow-y: overlay;
		max-width: 280px;
	}
	.tree-edit-box {
		display: flex;
		padding: 16px;
		width: 100%;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
	}
	.tree-edit-box div {
		flex: 1;
		height: 48px;
		border-radius: 8px;
		cursor: pointer;
		background-color: #e8f3ff;
		color: #1b86ff;
	}

	:deep(.node-container) {
		display: flex;
		align-items: center;
		width: 100%;
		justify-content: space-between;
		min-width: 0; /* 关键属性：允许子元素收缩 */

		&:hover .node-actions {
			opacity: 1;
			visibility: visible;
		}
	}

	:deep(.tree-node) {
		display: flex;
		align-items: center;
		padding: 4px 0;
		flex: 1; /* 占据剩余空间 */
		min-width: 0; /* 关键属性：允许文本截断 */

		> span {
			display: inline-block;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
			vertical-align: middle;
			max-width: 100%;
		}

		img {
			flex-shrink: 0; /* 禁止图标收缩 */
		}
	}

	:deep(.node-actions) {
		flex-shrink: 0; /* 禁止操作按钮收缩 */
		opacity: 0;
		visibility: hidden;
		transition: all 0.2s;
		display: flex;
		gap: 4px;
		margin-left: 8px;
		padding-left: 8px;
		background: #f8f8f8; /* 防止文字穿透 */

		.actions-btn {
			display: flex;
			align-items: center;
			margin-right: 4px;
			cursor: pointer;
		}
	}
	.status-btn-box {
		margin: 12px;
		display: flex;
		align-items: center;
		justify-content: space-around;
		.status-btn {
			width: 116px;
			height: 36px;
			background: #ffffff;
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			cursor: pointer;
			display: flex;
			align-items: center;
			justify-content: center;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
		}
	}

	// :deep(.tree-node) {
	// 	display: flex;
	// 	align-items: center;
	// 	padding: 4px 0;
	// 	flex-grow: 1;
	// }

	// :deep(.node-container) {
	// 	display: flex;
	// 	align-items: center;
	// 	width: 100%;
	// 	justify-content: space-between;

	// 	&:hover .node-actions {
	// 		opacity: 1;
	// 		visibility: visible;
	// 	}
	// }

	// :deep(.node-actions) {
	// 	width: 30px;
	// 	opacity: 0;
	// 	visibility: hidden;
	// 	transition: all 0.2s;
	// 	display: flex;
	// 	gap: 4px;
	// 	margin-left: 8px;
	// 	align-items: center;
	// 	.actions-btn {
	// 		display: flex;
	// 		align-items: center;
	// 		margin-right: 4px;
	// 		cursor: pointer;
	// 	}
	// }
	.anima {
		transition: all 300ms linear;
		overflow: hidden;
		background: url("@/assets/project/progress.png") repeat-x;
		background-size: cover;
	}

	.statistics-box {
		margin-bottom: 24px;
		display: flex;
		align-items: center;
		.statistics-complete {
			height: 84px;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			padding-right: 30px;
			margin-right: 30px;
			border-right: 1px solid rgba(0, 0, 0, 0.06);
			.complete-item {
				display: flex;
				align-items: center;
				.complete-title {
					font-size: 14px;
					color: rgba(0, 0, 0, 0.85);
					line-height: 22px;
				}
				.chart-back {
					width: 230px;
					margin: 0 14px;
					height: 12px;
					background: #f8f8f8;
					border-radius: 8px;
					overflow: hidden;
					border: 1px solid rgba(0, 0, 0, 0.06);
				}
			}
		}
		.statistics-facility {
			flex: 1;
			display: flex;
			align-items: center;
			justify-content: space-between;
			.facility-item {
				width: calc(20% - 12.8px);
				position: relative;
				height: 84px;
				background: #ffffff;
				box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
				border-radius: 12px 12px 12px 12px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				border-bottom: none;
				overflow: hidden;
				.facility-num {
					margin: 16px 0 4px;
					font-weight: bold;
					font-size: 18px;
					color: rgba(0, 0, 0, 0.85);
					line-height: 22px;
					text-align: center;
				}
				.facility-title {
					font-size: 14px;
					color: rgba(0, 0, 0, 0.65);
					line-height: 22px;
					text-align: center;
				}
				.facility-back {
					position: absolute;
					bottom: 0;
					left: 0;
					width: 100%;
					height: 11px;
					background: linear-gradient(90deg, #f56c6c 0%, rgba(245, 108, 108, 0) 100%);
					border-radius: 0px 0px 0px 0px;
					opacity: 0.36;
					&.back-1 {
						background: linear-gradient(90deg, #ff9d00 0%, rgba(255, 157, 0, 0) 100%);
					}
					&.back-2 {
						background: linear-gradient(90deg, #67c23a 0%, rgba(103, 194, 58, 0) 100%);
					}
					&.back-3 {
						background: linear-gradient(90deg, #40bfa8 0%, rgba(64, 191, 168, 0) 100%);
					}
					&.back-4 {
						background: linear-gradient(90deg, #409eff 0%, rgba(64, 158, 255, 0) 100%);
					}
				}
			}
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
