<template>
	<BasePage v-bind="page" :asideLeftWidth="280" :isSpecialLayout="true">
		<template #pageLeft>
			<div class="flex-1 flex flex-col w-100% p-10px">
				<div class="flex p-b-6px flex-items-center flex-justify-between m-t-4px p-l-10px p-r-10px">
					<div class="font-size-18px font-b font-bold">请选择项目</div>
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
				></ElTree>
			</div>
		</template>
		<template #pageHeader></template>
		<template #pageSearch></template>
		<template #pageBody>
			<div
				class="flex flex-justify-between flex-items-center m-t-0px m-b-24px p-b-10px"
				style="border-bottom: 1px solid rgba(0, 0, 0, 0.06)"
			>
				<div class="title font-size-18px font-b font-bold">测试计划列表</div>
				<div>
					<!-- <ElButton type="danger" :icon="Delete" @click="batchDelete">
						删除
					</ElButton> -->
					<ElButton type="success" :icon="CirclePlus" @click="add"> 创建测试计划 </ElButton>
				</div>
			</div>

			<!-- 表格 -->
			<ElTable :data="tablelist" border v-loading="loading" stripe :row-key="(row) => row.id">
				<ElTableColumn label="序号" width="80" align="center">
					<template #default="{ $index }">
						<!-- 计算当前页的起始序号 -->
						{{ (page.pagination.currentPage - 1) * page.pagination.pageSize + $index + 1 }}
					</template>
				</ElTableColumn>
				<ElTableColumn prop="plan_name" label="测试计划名称" align="center" min-width="180">
					<template #default="{ row }">
						<!-- 可以点击查看详情 link-->
						<ElLink type="primary" @click="handleDetail(row.id)">
							{{ row.plan_name }}
						</ElLink>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="plan_member_name" label="测试人" align="center" width="100"> </ElTableColumn>
				<ElTableColumn prop="start_time" label="计划开始日期" align="center" width="120"> </ElTableColumn>

				<ElTableColumn prop="off_time" label="计划截止日期" align="center" width="120"></ElTableColumn>
				<ElTableColumn prop="issue_time" label="下发时间" align="center" width="180"></ElTableColumn>
				<ElTableColumn prop="completion_time" label="完成时间" align="center" width="180"></ElTableColumn>
				<ElTableColumn prop="create_user_name" label="创建人" align="center" width="100"></ElTableColumn>
				<ElTableColumn prop="status" label="状态" align="center" width="90">
					<template #default="{ row }">
						{{ statusText[row.status] }}
					</template>
				</ElTableColumn>
				<ElTableColumn prop="actions" label="操作" width="210px" align="center" fixed="right">
					<template #default="{ row }">
						<!-- 状态 状态 0.待下发 1.已下发 2.已挂起 3.已完成 -->
						<template v-if="row.status == 0">
							<ElButton size="small" type="primary" plain @click="handleEdit(row)"> 编辑 </ElButton>
							<ElButton size="small" type="success" plain @click="handleIssued(row)"> 下发 </ElButton>
							<ElButton size="small" type="danger" plain @click="handleDelete(row.id)"> 删除 </ElButton>
						</template>
						<template v-else-if="row.status == 1">
							<ElButton size="small" type="success" plain @click="handleComplete(row)"> 完成 </ElButton>
							<ElButton size="small" type="warning" plain @click="handlePending(row)"> 挂起 </ElButton>
						</template>
						<template v-else-if="row.status == 2">
							<ElButton size="small" type="primary" plain @click="handleRestore(row)"> 恢复 </ElButton>
						</template>

						<template v-else><span> -- </span></template>
					</template>
				</ElTableColumn>
			</ElTable>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { ref, onMounted, computed, nextTick } from "vue";
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import Pending from "./dialog/pending.vue";
	import Restore from "./dialog/restore.vue";
	import Assign from "./dialog/assign.vue";
	import { markRaw } from "vue";
	import { Plus, EditPen, Delete, CirclePlus } from "@element-plus/icons-vue";

	import { selectProject, selectPage, issueTask, completeTask, delData } from "@/api/test/plan";
	import { ElIcon, ElMessageBox, type Action, ElTree, ElMessage } from "element-plus";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";
	import { appInstance } from "@/main";
	import { Row } from "element-plus/es/components/table-v2/src/components/index.mjs";
	import DetailPage from "../plan-detail/index.vue";
	// 定义下拉选的数据
	const options = ref([]);
	const projectName = ref("");
	const tablelist = ref([]);
	const multipleSelection = ref([]);
	const query_module_id = ref<number[]>([]);
	const total = ref(0);
	const loading = ref(false);
	const statusText = {
		0: "待下发",
		1: "已下发",
		2: "已挂起",
		3: "已完成"
	};
	const selectedNode = ref(null); // 记录选中的节点
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
		span: 6,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: false,
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "计划名称",
				prop: "plan_name",
				attrs: {
					placeholder: "请输入计划名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "测试人",
				prop: "plan_member_name",
				attrs: {
					placeholder: "请输入测试人",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "计划开始日期",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time2",
				label: "计划截止日期",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time3",
				label: "下发时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time4",
				label: "完成时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "status",
				label: "状态",
				attrs: {
					placeholder: "请选择状态",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						// 0.待下发 1.已下发 2.已挂起 3.已完成
						{
							label: "待下发",
							id: "0"
						},
						{
							label: "已下发",
							id: "1"
						},
						{
							label: "已挂起",
							id: "2"
						},
						{
							label: "已完成",
							id: "3"
						}
					]
				}
			}
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "",
		listApi: async (data) => {},
		pagination: {
			currentPage: 1,
			pageSize: 20,
			total: total,
			background: true,
			layout: "prev, pager, next, jumper, ->, total",
			onChange: handlePaginationChange
		}
	});

	const add = () => {
		// console.log("treeData", treeData.value);
		createDrawerAsync(
			{ title: "新增", showNext: true, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail />
		);
	};
	const handleEdit = (row: any) => {
		createDrawerAsync(
			{ title: "编辑", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail rowInfo={row} />
		);
	};
	const handleIssued = (row: any) => {
		//
		ElMessageBox.confirm("测试计划下发后将通知测试责任人，下发后不可删除和编辑！", "下发", {
			confirmButtonText: "确认",
			cancelButtonText: "取消",
			type: "warning",
			customClass: "custom-message-box",
			callback: (action: Action) => {
				if (action == "confirm") {
					// issueTask
					issueTask({
						id: row.id
					}).then(() => {
						ElMessage.success("下发成功");
						refreshList();
					});
					// refreshList();
				}
			}
		});
	};
	const handleComplete = (row: any) => {
		// 测试计划完成后，记录完成时间，请谨慎操作！
		ElMessageBox.confirm("测试计划完成后，记录完成时间，请谨慎操作！", "完成", {
			confirmButtonText: "确认",
			cancelButtonText: "取消",
			type: "warning",
			callback: (action: Action) => {
				if (action == "confirm") {
					completeTask({
						id: row.id
					}).then(() => {
						ElMessage.success("操作成功");
						refreshList();
					});
				}
			}
		});
	};
	const handlePending = (row: any) => {
		createModelAsync(
			{ title: "挂起", showNext: false, width: "480px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Pending rowId={row.id} />
		);
	};
	const handleRestore = (row: any) => {
		createModelAsync(
			{ title: "恢复", showNext: false, width: "520px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Restore rowInfo={row} />
		);
	};
	// const handleDetail = (row: any) => {
	// 	appInstance.router.push({
	// 		path: "/test/plan-detail",
	// 		query: {
	// 			id: row.id
	// 		}
	// 	});
	// };
	const handleDetail = (id: any) => {
		createDrawerAsync(
			{ title: "详情", width: "80%", showNext: false, showConfirm: false },
			{},
			<DetailPage rowId={id} query_detail_status={1} />
		);
	};
	const handleDelete = (id: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					delData({ id: id }).then(() => {
						ElMessage.success("删除成功");
						page.form.onSearch(page.form.value);
					});
				}
			}
		});
	};
	const isSearching = ref(false);
	page.form.onSearch = async (data) => {
		if (isSearching.value) return; // 如果正在搜索，直接返回
		isSearching.value = true;
		loading.value = true;
		try {
			const [query_start_time_start, query_start_time_end] = data.time || [];
			const [query_off_time_start, query_off_time_end] = data.time2 || [];
			const [query_issue_time_start, query_issue_time_end] = data.time3 || [];
			const [query_completion_time_start, query_completion_time_end] = data.time4 || [];
			const params = {
				...data
			};
			delete params.time;
			delete params.time2;
			delete params.time3;
			delete params.time4;
			const pro_project_id = query_module_id.value;
			const pageNo = page.pagination.currentPage;
			const pageSize = page.pagination.pageSize;
			const res = await selectPage({
				...params,
				query_off_time_start,
				query_off_time_end,
				query_issue_time_start,
				query_issue_time_end,
				query_start_time_start,
				query_start_time_end,
				query_completion_time_start,
				query_completion_time_end,
				pro_project_id,
				pageNo,
				pageSize
			});

			tablelist.value = res.records;
			total.value = res.total;
			await getTreeData();
			return res;
		} finally {
			loading.value = false;
			isSearching.value = false;
		}
	};
	page.form.onReset = async () => {
		selectedNode.value = null;
		query_module_id.value = "";
		await page.form.onSearch(page.form.value);
	};
	const refreshList = async () => {
		await page.form.onSearch(page.form.value);
	};

	// 定义树的数据
	const treeData = ref([]);

	onMounted(async () => {
		getTreeData(page.form.value);
	});

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	const getTreeData = async () => {
		const formData = page.form.value;
		const [query_start_time_start, query_start_time_end] = formData.time || [];
		const [query_off_time_start, query_off_time_end] = formData.time2 || [];
		const [query_issue_time_start, query_issue_time_end] = formData.time3 || [];
		const [query_completion_time_start, query_completion_time_end] = formData.time4 || [];

		const params = {
			...formData
		};
		delete params.time;
		delete params.time2;
		delete params.time3;
		delete params.time4;

		Object.assign(params, {
			query_off_time_start,
			query_off_time_end,
			query_issue_time_start,
			query_issue_time_end,
			query_start_time_start,
			query_start_time_end,
			query_completion_time_start,
			query_completion_time_end
		});
		if (isSearching.value) {
			// 如果正在搜索，不执行 page.form.onSearch
			const res = await selectProject(params);
			treeData.value = res;
		} else {
			const res = await selectProject(params);
			treeData.value = res;
			await page.form.onSearch(page.form.value);
		}
	};
	const handleNodeClick = async (data: any, node: any) => {
		query_module_id.value = data.id;
		selectedNode.value = data.id;
		await page.form.onSearch(page.form.value);
	};

	// 配置项
	const treeProps = ref({
		label: "moduleName",
		children: "children"
	});

	// 定义响应式变量存储溢出状态
	const overflowMap = ref<Record<string, boolean>>({});

	// 检测溢出状态的方法
	const checkOverflow = (el: HTMLElement, id: string) => {
		nextTick(() => {
			const isOverflow = el.scrollWidth > el.clientWidth;
			overflowMap.value[id] = isOverflow;
		});
	};

	// 渲染函数
	const renderTreeNode = (h, { node, renderParams, store }) => {
		// 防御性检查
		if (!node?.data) return null;

		// 正确解构数据
		const { data } = node;

		return (
			<span class="tree-node">
				<img src={pageIcon} style="width: 16px; height: 16px; margin-right: 8px;vertical-align: text-bottom;" />
				<ElTooltip
					content={data.projectName}
					placement="top"
					disabled={!overflowMap.value[data.id]} // 动态禁用状态
					popper-class="custom-tooltip"
				>
					<span class="node-text" ref={(el) => el && checkOverflow(el, data.id)}>
						{data.projectName}
					</span>
				</ElTooltip>
				{data.taskPlanCount > 0 && <span class="task-plan-count">{data.taskPlanCount}</span>}
			</span>
		);
	};
</script>
<style scoped lang="scss">
	.tree-list-con {
		padding: 16px 12px;
		height: calc(100% - 88px);
		box-sizing: border-box;
		overflow-y: auto;
		max-width: 280px;
	}
	:deep(.node-container) {
		display: flex;
		align-items: center;
		width: 100%;
		justify-content: space-between;
		min-width: 0; /* 关键属性：允许子元素收缩 */
	}

	:deep(.tree-node) {
		display: flex;
		align-items: center;
		padding: 4px 0;
		width: 100%;
		> span {
			display: inline-block;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
			vertical-align: middle;
			max-width: 170px;
		}

		img {
			flex-shrink: 0; /* 禁止图标收缩 */
		}
	}
	:deep(.custom-message-box) {
		width: 480px !important; // 设置宽度，可根据需要调整
	}
	:deep(.el-tree-node__content) {
		height: 56px;
	}
	:deep(.task-plan-count) {
		display: inline-block;
		margin-left: 12px;
		padding: 1px 5px;
		background: #1b86ff;
		border-radius: 50%;
		color: #fff;
		font-size: 12px;
		transform: scale(0.833);
		transform-origin: center;
		vertical-align: bottom;
	}
	/* 自定义选中样式 */
	:deep(.el-tree-node.is-current > .el-tree-node__content) {
		background-color: var(--el-menu-c-active);
		border-radius: 4px;
		.tree-node {
			color: var(--el-menu-active-color);
		}
	}

	/* 确保自定义选中样式生效 */
	:deep(.el-tree-node__content) {
		transition: background-color 0.3s;
	}
</style>
