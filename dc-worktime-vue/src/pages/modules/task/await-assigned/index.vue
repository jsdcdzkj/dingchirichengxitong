<template>
	<BasePage v-bind="page" :aside-left-width="280">
		<template #pageLeft>
			<div class="flex-1 flex flex-col w-100% p-10px">
				<div class="flex p-b-10px flex-items-center flex-justify-between m-t-8px p-l-10px p-r-10px">
					<!-- 项目名称下拉选 -->
					<ElSelect v-model="projectName" placeholder="请选择" clearable filterable @change="handleChange">
						<ElOption v-for="item in options" :key="item.id" :label="item.projectName" :value="item.id">
							<span class="project-name">{{ item.projectName }}</span>
							<ElTag :type="item.count > 0 ? 'primary' :'info'" effect="light" round>
								<img
									src="@/assets/task/task-num.png"
									style="width: 12px; height: 12px; margin-right: 8px"
									alt=""
								/>
								<span>待分配 &nbsp;{{ item.count }}</span>
							</ElTag>
						</ElOption>
					</ElSelect>
				</div>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<div class="flex flex-justify-between p-18px">
				<ElButton :icon="Finished" plain style="width: 116px" @click="handleAllCheck(!isAllCheck)">
					{{ isAllCheck ? "全不选" : "全选" }}
				</ElButton>
				<!-- <ElButton :icon="Upload" style="width: 70px" @click="handleHide">折叠</ElButton>
				<ElButton :icon="Download" style="width: 70px" @click="handleShow">展开</ElButton> -->
				<ElButton :icon="isExpanded ? Upload : Download" style="width: 116px" @click="toggleExpand">
					{{ isExpanded ? "折叠" : "展开" }}
				</ElButton>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<div class="tree-list-con">
				<ElTree
					:data="treeData"
					:props="treeProps"
					:render-content="renderTreeNode"
					show-checkbox
					@check-change="handleCheckChange"
					ref="tree"
					:default-expanded-keys="expandNode"
					node-key="id"
				></ElTree>
			</div>
		</template>
		<template #pageBody>
			<div
				class="flex flex-justify-between flex-items-center m-t-0px m-b-24px p-b-10px"
				style="border-bottom: 1px solid rgba(0, 0, 0, 0.06)"
			>
				<div class="title font-size-18px font-b font-bold">待分配功能任务</div>
				<div>
					<ElButton type="success" @click="handleAssign('all')"> 合并分配 </ElButton>
				</div>
			</div>
			<ElTabs v-model="activeName" @tab-click="handleClick">
				<ElTabPane :name="item.id" :key="item.id" v-for="item in deegressList">
					<template #label>
						<span class="custom-tabs-label">
							{{ item.name }}
							<span class="tag">
								{{ item.count - 99 > 0 ? "99+" : item.count }}
							</span>
						</span>
					</template>
					<!-- 表格 -->
					<ElTable
						ref="multipleTableRef"
						:data="tablelist"
						border
						class="m-t-24px"
						v-loading="loading"
						:row-key="(row) => row.id"
						stripe
						height="calc(100vh - 340px)"
						@selection-change="handleSelectionChange"
						@select-all="handleSelectAll"
					>
						<ElTableColumn type="selection" align="center"></ElTableColumn>
						<ElTableColumn label="序号" width="80" align="center" type="index"> </ElTableColumn>
						<ElTableColumn
							prop="historical_developers"
							label="开发人员"
							align="center"
							min-width="160px"
							show-overflow-tooltip
						></ElTableColumn>
						<ElTableColumn
							prop="plan_title"
							label="来源开发组任务"
							align="center"
							min-width="200px"
							show-overflow-tooltip
						>
							<template #default="scope">
								<ElLink type="primary" @click="handleView(scope.row)">{{
									scope.row.plan_title
								}}</ElLink>
							</template>
						</ElTableColumn>
						<ElTableColumn
							prop="tree_name"
							label="功能菜单"
							align="center"
							min-width="160px"
							show-overflow-tooltip
						></ElTableColumn>
						<ElTableColumn
							prop="feature_name"
							label="功能名称"
							align="center"
							min-width="160px"
							show-overflow-tooltip
						>
							<template #default="{ row }">
								<!-- 可以点击查看详情 link-->
								<ElLink type="primary" @click="handleDetail(row)">
									{{ row.feature_name }}
								</ElLink>
							</template>
						</ElTableColumn>
						<!-- <ElTableColumn
					prop="feature_desc"
					label="功能描述"
					align="center"
					min-width="200px"
					show-overflow-tooltip
				></ElTableColumn> -->
						<!-- <ElTableColumn
							prop="realName"
							label="任务分配责任人"
							align="center"
							width="140px"
							show-overflow-tooltip
						></ElTableColumn> -->
						<ElTableColumn label="截止日期" width="220px" align="center" show-overflow-tooltip
							><template #default="scope">
								{{ scope.row.plan_end_date }}
							</template></ElTableColumn
						>
						<ElTableColumn
							prop="degree_of_difficulty_name"
							label="难易度"
							width="100px"
							align="center"
							show-overflow-tooltip
						>
							<template #default="{ row }">
								<span class="degree" v-if="row.degree_of_difficulty == 1"
									><i class="degree-1"></i>轻松</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 2"
									><i class="degree-2"></i>简单</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 3"
									><i class="degree-3"></i>正常</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 4"
									><i class="degree-4"></i>困难</span
								>
								<span class="degree" v-if="row.degree_of_difficulty == 5"
									><i class="degree-5"></i>挑战</span
								>
							</template>
						</ElTableColumn>
						<!-- <ElTableColumn
							prop="degree_of_difficulty_desc"
							label="难易度描述"
							show-overflow-tooltip
							min-width="160px"
							align="center"
						></ElTableColumn> -->
						<ElTableColumn fixed="right" prop="actions" label="操作" width="140px" align="center">
							<template #default="{ row }">
								<ElButton size="small" plain type="primary" @click="handleAssign(row)"> 分配 </ElButton>
							</template>
						</ElTableColumn>
					</ElTable>
				</ElTabPane>
			</ElTabs>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { ref, onMounted, computed, nextTick } from "vue";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import { deleteById, toTask, taskCount, toTaskCount } from "@/api/task/await-assigned";
	import { ElMessage } from "element-plus";
	import AssignTask from "./dialog/assign-task.vue";
	import { selectModuleTree } from "@/api/test/issues";
	import { appInstance } from "@/main";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";
	import { Plus, EditPen, Delete, CirclePlus, Download, Upload, Finished } from "@element-plus/icons-vue";
	import { createDrawerAsync } from "@/core/dialog";
	import PlanDetailPage from "@/pages/modules/project/plan-detail/index.vue";
	import DetailPage from "./dialog/detailPage.vue";

	const formConfig: FormConfig = {
		span: 6,
		expandSpan: 4,
		labelWidth: 110,
		labelPosition: "right",
		notMargn: true,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "feature_name",
				label: "功能名称",
				attrs: {
					placeholder: "请输入功能名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			// {
			// 	type: "ElInput",
			// 	value: "",
			// 	prop: "feature_desc",
			// 	label: "功能描述名称",
			// 	attrs: {
			// 		placeholder: "请输入功能描述",
			// 		clearable: true,
			// 		onKeyup: (e: KeyboardEvent) => {
			// 			if (e.key === "Enter") {
			// 				page.form.onSearch(page.form.value);
			// 			}
			// 		}
			// 	}
			// },
			{
				type: "ElInput",
				value: "",
				prop: "realName",
				label: "任务分配负责人",
				attrs: {
					placeholder: "请输入负责人",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			}
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		listApi: async () => {
			// return await toTask({ ...data });
		}
	});
	// 表格数据
	const multipleSelection = ref([]);
	const loading = ref(false);
	const tablelist = ref([]);

	page.form.onSearch = async (data) => {
		loading.value = true;
		getProjectCount()
		getTaskType()
		try {
			const res = await toTask({
				...data,
				project_id: projectName.value,
				module_ids: query_module_id.value.join(","),
				degree_of_difficulty: activeName.value
			});

			tablelist.value = res;
			
			return res;
		} finally {
			loading.value = false;
		}
	};
	page.form.onReset = async () => {
		await page.form.onSearch(page.form.value);
	};

	const multipleTableRef = ref();

	// 点击全选
	const handleSelectAll = (val) => {
		const fg = tablelist.value.some(item => item.plan_end_date != tablelist.value[0].plan_end_date)
		if (fg) {
			val.length > 0 && ElMessage.warning("请选择截止时间一致的功能点！");
			multipleTableRef.value[activeName.value? activeName.value : 0].clearSelection()
			multipleSelection.value = []
		} else {
			multipleSelection.value = val;
		}
	}
	const handleSelectionChange = (val: any) => {
		if(val.length > 1 && (val[0].plan_end_date != val[val.length - 1].plan_end_date)) {
			val.length > 0 && ElMessage.warning("请选择截止时间一致的功能点！");
			const temp = val[val.length - 1]
			const index = tablelist.value.findIndex(item => item.id == temp.id)
			nextTick(()=>{
				multipleTableRef.value[activeName.value? activeName.value : 0].toggleRowSelection(tablelist.value[index], false)
			})
		} else {
			multipleSelection.value = val;
		}
		
	};
	const handleAssign = (row: any) => {
		let chooseData = multipleSelection.value;
		if (row == 'all' && !(chooseData && chooseData.length > 0)) {
			return ElMessage.warning("请至少选择一个任务");
		}
		if (row != "all") {
			chooseData = [row];
		}
		createDrawerAsync(
			{
				title: `${row == "all" ? "合并" : ""}分配任务`,
				width: "1000px",
				showNext: false, closeOnClickModal: false,closeOnPressEscape: false
			},
			{},
			<AssignTask row={chooseData} projectId={projectName.value} />
		).then(() => {
			page.form?.onSearch();
		});
	};

	// 跳转计划详情
	const handleView = (row: Object) => {
		createDrawerAsync({ title: row.plan_title, width: '1100px', showNext: false, showConfirm: false }, {}, <PlanDetailPage query_detail_status={1} id={row.plan_task_id} />);
	};

	const handleDetail = (row: any) => {
		createDrawerAsync(
			{ title: row.title, showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage rowInfo={row} />
		);
	};

	// 左侧菜单树
	// 定义树的数据
	const options = ref([]);
	const projectName = ref("");
	const query_module_id = ref<number[]>([]);
	const treeData = ref([]);

	const handleChange = async (value) => {
		projectName.value = value;
		query_module_id.value = [];
		isAllCheck.value = false
		isExpanded.value = false
		await getTreeData();
	};
	const getProjectCount =async() => {
		options.value = await taskCount({}) || []
	}

	onMounted(async () => {
		options.value = await taskCount({}) || []
		// 默认选中第一个选项
		if (options.value.length > 0) {
			projectName.value = options.value[0].id;
		}
		getTreeData();
	});

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

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

	const getTreeData = async () => {
		if (!projectName.value) return;

		const res = await selectModuleTree({ project_id: projectName.value }); // 使用动态project_id
		// const res = await selectModuleTree({ project_id: 1 }); // 使用动态project_id
		treeData.value = res;
		markNodeLevel(treeData.value);

		nextTick(() => {
			if (tree.value) {
				console.log('222222222222tree.value', treeData.value[0])
				const allIds = getAllNodeIds(treeData.value[0]?.children);
				allIds.push(treeData.value[0].id)
				console.log('1111111111111111allIds', allIds)
				tree.value.setCheckedKeys(allIds); // 设置全选
				query_module_id.value = allIds; // 存入query_module_id
			}
		});

		// await page.form.onSearch(page.form.value);
	};

	// 自动标记层级关系
	const markNodeLevel = (nodes, level = 1) => {
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

	// 复选框变化处理
	const handleCheckChange = (data, checked) => {
		const checkedKeys = tree.value?.getCheckedKeys() || [];
		query_module_id.value = checkedKeys as number[];
		// console.log("选中的节点ID：", query_module_id.value);
		page.form.onSearch(page.form.value);
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
			<el-tooltip
				class="box-item"
				effect="dark"
				content={data.moduleName}
				placement="top"
			>
				<span class="tree-node">
					<img
						src={isRoot ? fileIcon : pageIcon}
						style="width: 16px; height: 16px; margin-right: 8px;vertical-align: text-bottom;"
					/>
					<span class="node-name">{data.moduleName}</span>
				</span>
			</el-tooltip>
		);
	};

	
	const expandNode = ref()
	// 全选
	const isAllCheck = ref(false)
	const handleAllCheck = (val:Boolean) => {
		if (val == true) {
			isAllCheck.value = true
			const allIds = getAllNodeIds(treeData.value);
			tree.value.setCheckedKeys(allIds); // 设置全选
		} else {
			isAllCheck.value = false
			tree.value.setCheckedKeys([]); // 设置全选
		}
		
	}
	const isExpanded = ref(false);

	// 新增一个函数来处理按钮点击事件
	const toggleExpand = () => {
		for (let i = 0; i < treeData.value.length; i++) {
			tree.value.store.nodesMap[treeData.value[i].id].expanded = !isExpanded.value;
        }
		// 切换展开状态
		isExpanded.value = !isExpanded.value;
	};
	// // 折叠
	// const handleHide = () => {
	// 	for (let i = 0; i < treeData.value.length; i++) {
	// 		tree.value.store.nodesMap[treeData.value[i].id].expanded = false;
    //     }
	// }
	// // 展开
	// const handleShow = () => {
	// 	for (let i = 0; i < treeData.value.length; i++) {
	// 		tree.value.store.nodesMap[treeData.value[i].id].expanded = true;
    //     }
	// }

	const deegressList = ref([])
	const getTaskType = () => {

		toTaskCount({project_id: projectName.value,module_ids:query_module_id.value.join(",")}).then(res=> {
			deegressList.value = res
		})
	}
	

	// 根据难易程度展示待分配列表
	console.log('33333333333333333appInstance', appInstance)
	const activeName = ref('')
	const handleClick = (val) => {
		page.form.onSearch(page.form.value)
		multipleSelection.value = []
	}
</script>
<style scoped lang="scss">
.tree-list-con {
	padding: 24px;
	height: calc(100% - 145px);
	box-sizing: border-box;
	overflow-y: auto;
	max-width: 340px;
}
.tree-edit-box {
	display: flex;
	width: 100%;
	border-top: 1px solid #e8e8e8;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	line-height: 22px;
}
.tree-edit-box div {
	flex: 1;
	height: 56px;
	border-left: 1px solid #e8e8e8;
	cursor: pointer;
	&:first-child {
		border-left: none;
	}
}

:deep(.el-tree-node__content) {
	height: 48px;
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

.custom-tabs-label {
	display: flex;
	align-items: center;
	padding: 0 24px;
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
.el-select-dropdown__item {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

:ddep(.el-tag__content) {
	display: flex;
	align-items: center;
}

:deep(.el-popper) {
	max-width: 500px !important; /* 设置最大宽度 */
	line-height: 1.5 !important; /* 可选：调整行高 */
}

::v-deep {
	.el-tag {
		padding: 0 8px;
	}
	.el-tabs__item {
		padding: 0;
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
