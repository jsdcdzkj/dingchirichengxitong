<template>
	<BasePage v-bind="page" :asideLeftWidth="280">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey == 'actionsBtn'">
				<!-- <div v-if="row.status == 1"> -->
				<div>
					<ElButton type="primary" plain size="small" @click="handleEdit(row)"> 编辑 </ElButton>
					<ElButton type="primary" plain size="small" @click="handleDelete(row)"> 删除 </ElButton>
					<ElButton type="primary" plain size="small" @click="handleRate(row)"> 评分 </ElButton>
				</div>
				<!-- <span v-else> -- </span> -->
			</template>
		</template>
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
				<div class="title font-size-18px font-b font-bold">优化列表</div>
				<div>
					<!-- <ElButton type="danger" :icon="Delete" @click="batchDelete">
						删除
					</ElButton> -->
					<ElButton type="success" :icon="ChatDotSquare" @click="add"> 提优化建议 </ElButton>
				</div>
			</div>

			<!-- 表格 -->
			<ElTable :data="tablelist" border v-loading="loading" :row-key="(row) => row.id" stripe>
				<ElTableColumn label="序号" width="80" align="center">
					<template #default="{ $index }">
						<!-- 计算当前页的起始序号 -->
						{{ (page.pagination.currentPage - 1) * page.pagination.pageSize + $index + 1 }}
					</template>
				</ElTableColumn>
				<ElTableColumn prop="title" label="标题" align="center" min-width="200" show-overflow-tooltip>
					<template #default="{ row }">
						<!-- 可以点击查看详情 link-->
						<ElLink type="primary" @click="handleDetail(row)">
							{{ row.title }}
						</ElLink>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="create_user_name" label="提出人" width="90" align="center"> </ElTableColumn>
				<ElTableColumn prop="createTime" label="提出时间" width="180" align="center"> </ElTableColumn>

				<ElTableColumn prop="mark" label="评分" align="center" width="200">
					<template #default="{ row }">
						<!-- 如果有值则显示评分ElRate，否则显示 -- -->
						<template v-if="row.mark">
							<ElRate
								:allow-half="true"
								v-model="row.mark"
								disabled
								show-score
								:score-template="`${Number(row.mark) || 0} 分`"
							></ElRate>
						</template>
						<template v-else>--</template>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="markResult" label="结果" width="100" align="center">
					<template #default="{ row }">
						<span v-if="row.markResult == 0">不采纳</span>
						<span v-if="row.markResult == 1">采纳</span>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="projectManagerName" label="项目经理" width="100" align="center">
					<template #default="{ row }">
						{{ row.projectManagerName || "--" }}
					</template>
				</ElTableColumn>

				<ElTableColumn prop="actions" label="操作" width="200" align="center" fixed="right">
					<template #default="{ row }">
						<!-- 判断结果是否有值，有值则不显示按钮，无值则显示按钮 -->
						<template v-if="!row.markResult">
							<ElButton size="small" type="primary" plain @click="handleEdit(row)"> 编辑 </ElButton>
							<ElButton size="small" type="warning" plain @click="handleRate(row)"> 评分 </ElButton>
							<ElButton size="small" type="danger" plain @click="handleDelete(row)"> 删除 </ElButton>
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
	import DetailPage from "./dialog/detailPage.vue";
	import Rate from "./dialog/rate.vue";
	import { Plus, EditPen, Delete, ChatDotSquare } from "@element-plus/icons-vue";
	import { selectPage, delData, markResultcount } from "@/api/optimization/suggestion";
	import { selectPageList } from "@/api/project/list";
	import { ElIcon, ElMessageBox, type Action, ElTree, ElButton, ElMessage, ElRate } from "element-plus";
	import pageIcon from "@/assets/page.png";
	const tablelist = ref([]);
	const query_module_id = ref<number[]>([]);
	const total = ref(0);
	const loading = ref(false);

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
		span: 8,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: false,
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "标题名称",
				prop: "title",
				attrs: {
					placeholder: "请输入标题名称",
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
				label: "提出人",
				prop: "create_user_name",
				attrs: {
					placeholder: "请输入提出人",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "markResult",
				label: "结果",
				attrs: {
					placeholder: "请选择结果",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "不采纳",
							id: "0"
						},
						{
							label: "采纳",
							id: "1"
						}
					]
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "提出日期",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "项目经理",
				prop: "projectManagerName",
				attrs: {
					placeholder: "请输入项目经理",
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
	page.form.onSearch = async (data) => {
		loading.value = true;
		try {
			const [query_create_time_start, query_create_time_end] = data.time || [];
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
				query_create_time_start,
				query_create_time_end,

				pro_project_id,
				pageNo,
				pageSize
			});

			tablelist.value = res.records;
			total.value = res.total;
			return res;
		} finally {
			loading.value = false;
		}
	};
	page.form.onReset = async () => {
		refreshList();
	};

	const refreshList = async () => {
		getTreeData();
		// await page.form.onSearch(page.form.value);
	};
	const add = () => {
		createDrawerAsync(
			{ title: "新增", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail moduleList={treeData.value} projectId={selectedNode.value} />
		);
	};
	const handleEdit = (row: any) => {
		createDrawerAsync(
			{ title: "编辑", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail rowInfo={row} moduleList={treeData.value} />
		);
	};
	const handleDetail = (row: any) => {
		createDrawerAsync(
			{ title: "详情", showNext: false, width: "1056px", showConfirm: false },
			{},
			<DetailPage rowId={row.id} />
		);
	};

	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					delData({ id: row.id }).then(() => {
						ElMessage.success("删除成功");
						refreshList();
					});
				}
			}
		});
	};

	const handleRate = (row: any) => {
		createModelAsync(
			{ title: "评分", showNext: false, width: "500px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Rate rowInfo={row} />
		);
	};

	// 定义树的数据
	const treeData = ref([]);

	onMounted(async () => {
		getTreeData();
	});

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	const getTreeData = async () => {
		const res = await markResultcount({});
		treeData.value = res;
		await page.form.onSearch(page.form.value);
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
				{data.markResultNum > 0 && <span class="task-plan-count">{data.markResultNum}</span>}
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
