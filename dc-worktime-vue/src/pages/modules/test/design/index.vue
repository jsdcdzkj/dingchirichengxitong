<template>
	<BasePage v-bind="page" :asideLeftWidth="280">
		<template #pageLeft>
			<div class="flex-1 flex flex-col w-100% p-10px">
				<div class="flex p-b-10px flex-items-center flex-justify-between m-t-8px p-l-10px p-r-10px">
					<!-- 项目名称下拉选 -->
					<ElSelect v-model="projectName" placeholder="请选择" clearable filterable @change="handleChange">
						<ElOption v-for="item in options" :key="item.id" :label="item.projectName" :value="item.id">
							<template #default>
								<div class="option-wrapper">
									<span>{{ item.projectName }}</span>
									<span v-if="item.severity_no > 0" class="red-dot-value">{{
										item.severity_no
									}}</span>
								</div>
							</template>
						</ElOption>
					</ElSelect>
				</div>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<!-- <div>
				<div class="status-btn-box">
					<div class="status-btn" @click="expandAll">
						<ElIcon color="rgba(0, 0, 0, 0.65)" size="16">
							<Download />
						</ElIcon>
						<span class="m-l-4px">展开全部节点</span>
					</div>
					<div class="status-btn" @click="collapseAll">
						<ElIcon color="rgba(0, 0, 0, 0.65)" size="16">
							<Upload />
						</ElIcon>
						<span class="m-l-4px">折叠全部节点</span>
					</div>
				</div>
			</div> -->
			<div class="flex flex-justify-between p-18px">
				<ElButton :icon="Finished" plain style="width: 116px" @click="handleAllCheck(!isAllCheck)">
					{{ isAllCheck ? "全不选" : "全选" }}
				</ElButton>
				<!-- <ElButton :icon="Upload" style="width: 100px" @click="collapseAll">折叠</ElButton>
				<ElButton :icon="Download" style="width: 100px" @click="expandAll">展开</ElButton> -->
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
					node-key="id"
				></ElTree>
			</div>
		</template>
		<template #pageHeader></template>
		<template #pageSearch></template>
		<template #pageTitle>设计问题列表</template>
		<template #pageBody>
			<div
				class="flex flex-justify-between flex-items-center m-t-0px m-b-24px p-b-10px"
				style="border-bottom: 1px solid rgba(0, 0, 0, 0.06)"
			>
				<div class="title font-size-18px font-b font-bold">设计问题列表</div>
				<div>
					<ElButton type="danger" :icon="Delete" @click="batchDelete"> 删除 </ElButton>
					<ElButton type="success" :icon="CirclePlus" @click="add"> 提问题 </ElButton>
				</div>
			</div>

			<div class="statistics-box">
				<div class="statistics-item">
					<div class="statistics-item-icon icon-bug1"></div>
					<div class="statistics-item-content">
						<div class="statistics-item-value">{{ severityCounts?.severity_1 || "0" }}</div>
						<div class="statistics-item-title">轻微问题数量</div>
					</div>
				</div>
				<div class="statistics-item">
					<div class="statistics-item-icon icon-bug2"></div>
					<div class="statistics-item-content">
						<div class="statistics-item-value">{{ severityCounts?.severity_2 || "0" }}</div>
						<div class="statistics-item-title">一般问题数量</div>
					</div>
				</div>
				<div class="statistics-item">
					<div class="statistics-item-icon icon-bug3"></div>
					<div class="statistics-item-content">
						<div class="statistics-item-value">{{ severityCounts?.severity_3 || "0" }}</div>
						<div class="statistics-item-title">严重问题数量</div>
					</div>
				</div>
				<div class="statistics-item">
					<div class="statistics-item-icon icon-bug4"></div>
					<div class="statistics-item-content">
						<div class="statistics-item-value">{{ severityCounts?.severity_no || "0" }}</div>
						<div class="statistics-item-title">未完成数量</div>
					</div>
				</div>
			</div>

			<!-- 表格 -->
			<ElTable
				:data="tablelist"
				border
				v-loading="loading"
				:row-key="(row) => row.id"
				:row-class-name="rowClassName"
				stripe
				@selection-change="handleSelectionChange"
				@row-click="handleRowClick"
			>
				<ElTableColumn label="" align="center" width="50">
					<template #default="{ row }">
						<div class="to-top-box">
							<span
								class="to-top"
								:class="{ active: row.isTop == 1 }"
								@click.stop="changeTop(row)"
							></span>
						</div>
					</template>
				</ElTableColumn>
				<ElTableColumn type="selection" :selectable="checkSelectable" align="center"></ElTableColumn>
				<!-- 置顶 -->

				<ElTableColumn label="序号" width="80" align="center">
					<template #default="{ $index }">
						<!-- 计算当前页的起始序号 -->
						{{ (page.pagination.currentPage - 1) * page.pagination.pageSize + $index + 1 }}
					</template>
				</ElTableColumn>
				<ElTableColumn prop="source" label="来源" align="center" width="80">
					<template #default="{ row }">
						<template v-if="row.source == 1">任务</template>
						<template v-if="row.source == 2">评审</template>
						<template v-if="row.source == 3">联调</template>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="bug_title" label="标题" align="center" min-width="180">
					<template #default="{ row }">
						<!-- 可以点击查看详情 link-->
						<ElLink type="primary" @click.stop="handleDetail(2, row, true)">
							{{ row.bug_title }}
						</ElLink>
					</template>
				</ElTableColumn>

				<ElTableColumn prop="assigned_to_user_name" label="责任人" align="center" width="100"> </ElTableColumn>
				<ElTableColumn prop="severity" label="严重程度" align="center" width="90">
					<template #default="{ row }">
						<template v-if="row.severity == 1">
							<div class="severity icon-bug">
								<span></span>
								<i>轻微</i>
							</div>
						</template>
						<template v-if="row.severity == 2">
							<div class="severity icon-bug1">
								<span></span>
								<i>一般</i>
							</div>
						</template>
						<template v-if="row.severity == 3">
							<div class="severity icon-bug2">
								<span></span>
								<i>严重</i>
							</div>
						</template>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="end_time" label="截止日期" align="center" width="120"></ElTableColumn>
				<ElTableColumn prop="start_time" label="开始时间" align="center" width="180"></ElTableColumn>
				<ElTableColumn prop="completion_time" label="完成时间" align="center" width="180"></ElTableColumn>
				<!-- 解决类型 -->
				<ElTableColumn prop="statesName" label="解决类型" align="center" width="120"></ElTableColumn>

				<ElTableColumn prop="statusName" label="状态" align="center" width="100">
					<template #default="{ row }">
						<!-- <template v-if="row.status == 1">待指派</template>
						<template v-if="row.status == 2"
							>待开始/未解决</template
						>
						<template v-if="row.status == 3">进行中</template>
						<template v-if="row.status == 4"
							>待复测/已完成</template
						>
						<template v-if="row.status == 5">已解决</template>
						<template v-if="row.status == 6">已挂起</template>
						<template v-if="row.status == 7">已暂停</template> -->
						<!-- {{ statusText[row.status] }} -->
						<template v-if="row.status == 5 && row.retestsType == 2">这不是问题</template>
						<template v-else>{{ statusText[row.status] }}</template>
					</template>
				</ElTableColumn>
				<ElTableColumn prop="actions" label="操作" width="180px" align="center" fixed="right">
					<template #default="{ row }">
						<!-- 编辑按钮移到最前面 -->
						<template v-if="row.status !== '4' && row.status !== '5'">
							<ElButton size="small" type="primary" plain @click.stop="handleEdit(row)">编辑</ElButton>
						</template>
						<template v-if="row.status == '4'">
							<ElButton size="small" type="success" plain @click.stop="handleResolved(row.id)"
								>已解决</ElButton
							>
							<ElButton size="small" type="danger" plain @click.stop="handleUnsolved(row)"
								>未解决</ElButton
							>
						</template>

						<template v-else-if="row.status == '2'">
							<ElButton size="small" type="danger" plain @click.stop="handleDelete(row.id)"
								>删除</ElButton
							>
						</template>
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
	import Resolved from "./dialog/resolved.vue";
	import Unsolved from "./dialog/unsolved.vue";
	import { markRaw } from "vue";
	import { Plus, EditPen, Delete, CirclePlus, Download, Upload, Finished } from "@element-plus/icons-vue";
	import {
		selectModuleTree,
		selectListCount,
		selectDesignPage,
		delDataById,
		batchDeletion,
		top,
		topCancel,
		severityCount,
		severityCountBean,
		getTypeUserList
	} from "@/api/test/issues";
	import { projectUserList } from "@/api/project/list";
	import { ElIcon, ElMessageBox, type Action, ElTree, ElMessage } from "element-plus";
	import fileIcon from "@/assets/file.png";
	import pageIcon from "@/assets/page.png";
	import { appInstance } from "@/main";
	import DetailPage from "../../task/task-detail/index.vue";
	import { de } from "element-plus/es/locale/index.mjs";
	// 定义下拉选的数据
	const userList = ref([]);
	const options = ref([]);
	const projectName = ref(appInstance.selectProjectTestIndex);
	const tablelist = ref([]);
	const multipleSelection = ref([]);
	const query_module_id = ref<number[]>([]);
	const total = ref(0);
	const loading = ref(false);
	// 1.待指派 2.待开始 3.进行中 4.待复测 5.已解决 6.已挂起 7.已暂停
	const statusText = {
		// 1: "待指派",
		2: "待开始",
		3: "进行中",
		4: "待复测",
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
				label: "标题",
				prop: "bug_title",
				attrs: {
					placeholder: "请输入标题",
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
				prop: "states",
				label: "解决类型",
				attrs: {
					placeholder: "请选择解决类型",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "已解决",
							id: "1"
						},
						{
							label: "未复现",
							id: "2"
						},
						{
							label: "不是我的",
							id: "3"
						}
					]
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
						// 1.待指派 2.待开始/未解决 3.进行中 4.待复测/已完成 5.已解决 6.已挂起 7.已暂停
						// {
						// 	label: "待指派",
						// 	id: "1"
						// },
						{
							label: "待开始",
							id: "2"
						},
						{
							label: "进行中",
							id: "3"
						},
						{
							label: "待复测",
							id: "4"
						},
						{
							label: "已解决",
							id: "5"
						},
						{
							label: "已挂起",
							id: "6"
						},
						{
							label: "已暂停",
							id: "7"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "source",
				label: "来源",
				attrs: {
					placeholder: "请选择来源",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "id",
					list: [
						{
							label: "任务",
							id: "1"
						},
						{
							label: "评审",
							id: "2"
						},
						{
							label: "联调",
							id: "3"
						}
					]
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "截止时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始日期",
					endPlaceholder: "结束日期",
					valueFormat: "YYYY-MM-DD"
				}
			},
			// {
			// 	type: "ElSelect",
			// 	value: "",
			// 	prop: "assigned_to_user_type",
			// 	label: "指派至",
			// 	attrs: {
			// 		placeholder: "请选择被指派人",
			// 		clearable: true
			// 	},
			// 	select: {
			// 		type: "ElOption",
			// 		label: "label",
			// 		value: "id",
			// 		list: [
			// 			{
			// 				label: "前端",
			// 				id: "1"
			// 			},
			// 			{
			// 				label: "后端",
			// 				id: "2"
			// 			}
			// 		]
			// 	}
			// },
			{
				type: "ElSelect",
				value: "",
				prop: "assigned_to_user_id",
				label: "责任人",
				attrs: {
					placeholder: "请选择责任人",
					clearable: true,
					filterable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					list: userList
				}
			},
			// {
			// 	type: "ElDatePicker",
			// 	value: "",
			// 	prop: "time2",
			// 	label: "响应时间",
			// 	attrs: {
			// 		type: "daterange",
			// 		rangeSeparator: "至",
			// 		startPlaceholder: "开始日期",
			// 		endPlaceholder: "结束日期",
			// 		valueFormat: "YYYY-MM-DD"
			// 	}
			// },
			{
				type: "ElDatePicker",
				value: "",
				prop: "time3",
				label: "开始时间",
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
					startPlaceholder: "开始时间",
					endPlaceholder: "时间",
					valueFormat: "YYYY-MM-DD"
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
	const checkSelectable = (row) => {
		return row.status == 2 && row.start_time == null;
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
		const ids = multipleSelection.value.map((item) => item.id).join(",");
		try {
			await batchDeletion(ids);
			ElMessage.success("批量删除成功");
			refreshList();
		} catch (error) {
			console.error("批量删除失败:", error);
		}
	};
	const add = () => {
		createDrawerAsync(
			{ title: "提问题", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail moudleList={treeData} projectId={projectName.value} userList={userList.value} />
		);
	};
	const handleEdit = (row: any) => {
		// 处理编辑逻辑
		createDrawerAsync(
			{ title: "编辑", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Detail moudleList={treeData} projectId={projectName.value} rowInfo={row} userList={userList.value} />
		);
	};
	const handleResolved = (id: any) => {
		createModelAsync(
			{ title: "已解决", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Resolved projectId={id} />
		);
	};
	const handleUnsolved = (row: any) => {
		createModelAsync(
			{ title: "未解决", showNext: false, width: "1056px", closeOnClickModal: false, closeOnPressEscape: false },
			{ refreshList },
			<Unsolved rowInfo={row} userList={userList.value} />
		);
	};

	const refreshParentParentPage = async () => {
		await page.form.onSearch(page.form.value);
	};

	const handleDetail = (index: any, row: any, showBtn: any) => {
		createDrawerAsync(
			{ title: row.bug_title, width: "80%", showNext: false, showConfirm: false },
			{ refreshParentParentPage },
			<DetailPage type={index} id={row.id} showBtn={showBtn} testType={row.assigned_to_user_type} />
		);
	};
	// const handleDetail = (row: any) => {
	// 	appInstance.router.push({
	// 		path: "/test/issues-detail",
	// 		query: {
	// 			id: row.id,
	// 			type: 2,
	// 			showBtn: true
	// 		}
	// 	});
	// 	// appInstance.router.push({
	// 	// 	path: "/task/task-detail",
	// 	// 	query: {
	// 	// 		id: row.id,
	// 	// 		type: 2
	// 	// 	}
	// 	// });
	// };
	const handleDelete = (id: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					delDataById({ id: id }).then(() => {
						ElMessage.success("删除成功");
						page.form.onSearch(page.form.value);
						// getSeverityCount();
					});
				}
			}
		});
	};
	const handleChange = async (value) => {
		projectName.value = value;
		query_module_id.value = [];
		appInstance.setSelectProjectTestIndex(value);
		// await getSeverityCount();
		await getTreeData();
		await page.form.onSearch(page.form.value);
	};

	page.form.onSearch = async (data) => {
		loading.value = true;
		try {
			const [query_end_time_start, query_end_time_end] = data.time || [];
			const [query_response_time_start, query_response_time_end] = data.time2 || [];
			const [query_start_time_start, query_start_time_end] = data.time3 || [];
			const [query_completion_time_start, query_completion_time_end] = data.time4 || [];
			const params = {
				...data,
				// 添加 query_module_id 作为查询条件
				query_module_id: query_module_id.value.join(",")
			};
			delete params.time;
			delete params.time2;
			delete params.time3;
			delete params.time4;
			// const pro_project_id = 1;
			const pro_project_id = projectName.value;
			const pageNo = page.pagination.currentPage;
			const pageSize = page.pagination.pageSize;
			const isTemporarily = 0; //1是 0否 是否暂存
			const res = await selectDesignPage({
				...params,
				query_end_time_start,
				query_end_time_end,
				query_response_time_start,
				query_response_time_end,
				query_start_time_start,
				query_start_time_end,
				query_completion_time_start,
				query_completion_time_end,
				pro_project_id,
				pageNo,
				pageSize,
				isTemporarily
			});
			tablelist.value = res.records;
			total.value = res.total;

			getSeverityCount({
				...params,
				query_end_time_start,
				query_end_time_end,
				query_response_time_start,
				query_response_time_end,
				query_start_time_start,
				query_start_time_end,
				query_completion_time_start,
				query_completion_time_end,
				pro_project_id
			});
			return res;
		} finally {
			loading.value = false;
		}
	};
	page.form.onReset = async () => {
		await page.form.onSearch(page.form.value);
	};
	const refreshList = async () => {
		await page.form.onSearch(page.form.value);
		// await getSeverityCount();
	};

	// 定义树的数据
	const treeData = ref([]);

	const severityCounts = ref([]);
	const getSeverityCount = async (val) => {
		try {
			const res = await severityCountBean({
				...val,
				pro_project_id: projectName.value,
				assigned_to_user_type: 3
			});
			severityCounts.value = res[0];
		} catch (error) {
			severityCounts.value = [];
		}
	};

	onMounted(async () => {
		const res = await selectListCount({ assigned_to_user_type: 3 });
		options.value = res;
		// const data2 = await projectUserList({ projectId: appInstance.projectItem.id });
		// userList.value = data2;

		// 默认选中第一个选项
		if (options.value.length > 0 && projectName.value == -1) {
			projectName.value = options.value[0].id;
			appInstance.setSelectProjectTestIndex(options.value[0].id);
		}
		// 如果有选中的项目ID，检查列表中是否包含该项目ID 如果没有 则默认选中第一个项目ID
		if (options.value.length > 0 && !options.value.some((item) => item.id == projectName.value)) {
			projectName.value = options.value[0].id;
			appInstance.setSelectProjectTestIndex(options.value[0].id);
		}

		getTreeData();
		getSeverityCount({});
	});

	const tree = ref<InstanceType<typeof ElTree> | null>(null);

	// 获取所有节点ID并全选
	const getAllNodeIds = (nodes: any[]): number[] => {
		if (!nodes || nodes.length === 0) {
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
	const getTreeData = async () => {
		if (!projectName.value) return;

		const res = await selectModuleTree({ project_id: projectName.value }); // 使用动态project_id
		// const res = await selectModuleTree({ project_id: 1 }); // 使用动态project_id
		treeData.value = res;
		markNodeLevel(treeData.value);

		nextTick(() => {
			if (tree.value) {
				const allIds = getAllNodeIds(treeData.value);
				tree.value.setCheckedKeys(allIds); // 设置全选
				query_module_id.value = allIds; // 存入query_module_id
			}
		});

		// 重新获取用户列表
		const data2 = await projectUserList({ projectId: projectName.value });
		userList.value = data2;

		// await page.form.onSearch(page.form.value);
	};

	// 自动标记层级关系
	const markNodeLevel = (nodes, level = 1) => {
		if (!nodes || nodes.length === 0) {
			return [];
		}
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

	// 复选框变化处理
	const handleCheckChange = (data, checked) => {
		const checkedKeys = tree.value?.getCheckedKeys() || [];
		query_module_id.value = checkedKeys as number[];
		page.pagination.currentPage = 1;
		// 如果全不选，清空 query_module_id
		if (checkedKeys.length === 0) {
			query_module_id.value = [-1];
		}
		// console.log("选中的节点ID：", query_module_id.value);
		page.form.onSearch(page.form.value);
	};

	// 存储当前选中的节点 id
	const selectedNodeId = ref(null);

	// 行点击事件处理函数
	const handleRowClick = async (row) => {
		if (tree.value) {
			const targetNode = findNodeById(treeData.value, row.module_id);
			if (targetNode) {
				expandNodeById(tree.value, targetNode.id);
				// 更新选中的节点 id
				selectedNodeId.value = targetNode.id;

				// 模拟节点点击事件
				await nextTick(() => {
					// 增加延迟时间到 500 毫秒
					setTimeout(() => {
						// 修改获取节点元素的方式
						const nodeElement = document.querySelector(`[data-key="${targetNode.id}"]`);
						if (!nodeElement) {
							return;
						}
						const contentElement = nodeElement.querySelector(".el-tree-node__content");
						if (contentElement) {
							// 设置 tabindex 属性
							contentElement.setAttribute("tabindex", "-1");
							// 再次延迟聚焦
							setTimeout(() => {
								contentElement.focus();
								// 触发 focus 事件
								const focusEvent = new Event("focus");
								contentElement.dispatchEvent(focusEvent);
							}, 100);
						} else {
							console.log("未找到节点内容元素", targetNode.id);
						}
					}, 500);
				});
			} else {
				console.log("未找到目标节点，row.module_id:", row.module_id);
			}
		}
	};

	// 递归查找节点
	const findNodeById = (nodes, id) => {
		for (let i = 0; i < nodes.length; i++) {
			const node = nodes[i];
			if (node.id === id) {
				return node;
			}
			if (node.children) {
				const found = findNodeById(node.children, id);
				if (found) {
					return found;
				}
			}
		}
		return null;
	};

	// 展开指定节点
	const expandNodeById = (treeInstance, id) => {
		const node = treeInstance.store.nodesMap[id];
		if (node) {
			node.expanded = true;
			// 递归展开父节点
			let parent = node.parent;
			while (parent) {
				parent.expanded = true;
				parent = parent.parent;
			}
		}
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

	// 渲染函数
	const renderTreeNode = (h, { node, renderParams, store }) => {
		// 防御性检查
		if (!node?.data) return null;

		// 正确解构数据
		const { data } = node;

		// 检查是否为根节点
		// const isRoot = data?.isRoot ?? false;
		const isFolder = data.hasChildren; // 根据标记判断是否为文件夹

		return (
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
		);
	};

	// 渲染函数
	// const renderTreeNode = (h, { node, renderParams, store }) => {
	// 	// 防御性检查
	// 	if (!node?.data) return null;

	// 	// 正确解构数据
	// 	const { data } = node;

	// 	// 检查是否为根节点
	// 	// const isRoot = data?.isRoot ?? false;
	// 	const isFolder = data.hasChildren; // 根据标记判断是否为文件夹

	// 	return (
	// 		<div class="tree-node">
	// 			<img
	// 				src={isFolder ? fileIcon : pageIcon}
	// 				style="width: 16px; height: 16px; margin-right: 8px;vertical-align: text-bottom;"
	// 			/>
	// 			<span>{data.moduleName}</span>
	// 		</div>
	// 	);
	// };
	// 展开/折叠节点时更新 expandedKeys
	const expandedKeys = ref([]);
	// 全选
	const isAllCheck = ref(true);
	const handleAllCheck = (val: Boolean) => {
		if (val == true) {
			isAllCheck.value = true;
			const allIds = getAllNodeIds(treeData.value);
			tree.value.setCheckedKeys(allIds); // 设置全选
		} else {
			isAllCheck.value = false;
			tree.value.setCheckedKeys([]); // 设置全选
		}
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
	const changeTop = (row) => {
		// 置顶接口top 取消置顶接口topCancel
		// isTop 为0 时调用置顶接口，为1时调用取消置顶接口
		if (row.isTop == 0) {
			top({ id: row.id }).then(() => {
				ElMessage.success("置顶成功");
				refreshList();
			});
		} else {
			topCancel({ id: row.id }).then(() => {
				ElMessage.success("取消置顶成功");
				refreshList();
			});
		}
	};
	const rowClassName = ({ row }) => {
		if (row.severity == 3) {
			return "severity-3-row";
		}
		return "";
	};
	// 新增一个响应式变量来记录当前的展开状态
	const isExpanded = ref(false);

	// 新增一个函数来处理按钮点击事件
	const toggleExpand = () => {
		if (isExpanded.value) {
			collapseAll();
		} else {
			expandAll();
		}
		// 切换展开状态
		isExpanded.value = !isExpanded.value;
	};
</script>
<style scoped lang="scss">
	.tree-list-con {
		padding: 16px 12px;
		height: calc(100% - 140px);
		box-sizing: border-box;
		overflow-y: auto;
		max-width: 340px;
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

	:deep(.el-tree-node__content) {
		height: 48px;
	}
	/* 自定义聚焦样式 */
	:deep(.el-tree-node__content:focus) {
		background-color: var(--el-tree-node-hover-bg-color);
	}

	.status-btn-box {
		margin: 12px;
		display: flex;
		align-items: center;
		justify-content: space-around;
		.status-btn {
			width: 156px;
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
	.to-top-box {
		display: flex;
		align-items: center;
		justify-content: space-around;
	}
	.to-top {
		display: inline-block;
		width: 20px;
		height: 20px;
		background: url("@/assets/pushpin-line.png") no-repeat center;
		background-size: contain;
		cursor: pointer;
		&.active {
			background: url("@/assets/pushpin-fill.png") no-repeat center;
			background-size: contain;
		}
	}
	:deep(.severity-3-row) {
		background: #fef0f0 !important;
		// &:hover {
		// 	background: rgba(255, 0, 0, 0.24) !important;
		// }
		// &.hover-row td {
		// 	background: rgba(255, 0, 0, 0.24) !important;
		// }
	}
	:deep(.el-table--striped .el-table__body tr.severity-3-row td.el-table__cell) {
		background: #fef0f0 !important;
	}

	.statistics-box {
		margin-bottom: 16px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		.statistics-item {
			padding: 16px;
			width: calc(25% - 12px);
			display: flex;
			align-items: center;
			background: #ffffff;
			border-radius: 12px 12px 12px 12px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			.statistics-item-icon {
				width: 48px;
				height: 48px;
				margin-right: 20px;
				&.icon-bug1 {
					background: url("@/assets/bug/success.png") no-repeat center;
					background-size: contain;
				}
				&.icon-bug2 {
					background: url("@/assets/bug/warning.png") no-repeat center;
					background-size: contain;
				}
				&.icon-bug3 {
					background: url("@/assets/bug/danger.png") no-repeat center;
					background-size: contain;
				}
				&.icon-bug4 {
					background: url("@/assets/bug/info.png") no-repeat center;
					background-size: contain;
				}
			}
			.statistics-item-content {
				flex: 1;
				text-align: left;
				.statistics-item-title {
					margin-top: 4px;
					font-size: 14px;
					color: rgba(0, 0, 0, 0.65);
					line-height: 22px;
				}
				.statistics-item-value {
					font-weight: bold;
					font-size: 18px;
					color: rgba(0, 0, 0, 0.85);
					line-height: 22px;
				}
			}
		}
	}
	.severity {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 64px;
		height: 24px;
		background: #f0f9eb;
		border-radius: 4px 4px 4px 4px;
		border: 1px solid #85ce61;
		font-size: 14px;
		color: #67c23a;
		line-height: 22px;
		span {
			margin-right: 4px;
			display: inline-block;
			width: 14px;
			height: 14px;
			background: url("@/assets/bug/bug-fill.png") no-repeat center;
			background-size: cover;
		}
		i {
			font-style: normal;
			font-size: 14px;
		}
		&.icon-bug1 {
			color: #f5a250;
			background: #fef6ed;
			border: 1px solid #f7b573;
			span {
				background: url("@/assets/bug/bug-fill-2.png") no-repeat center;
				background-size: cover;
			}
		}
		&.icon-bug2 {
			color: #f56c6c;
			background: #fef0f0;
			border: 1px solid #f78989;
			span {
				background: url("@/assets/bug/bug-fill-1.png") no-repeat center;
				background-size: cover;
			}
		}
	}
	:deep(.el-table .cell) {
		line-height: 24px;
	}
	.option-wrapper {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.red-dot-value {
			display: inline-flex;
			justify-content: center;
			align-items: center;
			min-width: 18px;
			height: 18px;
			background-color: #f56c6c;
			color: #fff;
			border-radius: 10px;
			font-size: 12px;
			padding: 0 4px;
			box-sizing: border-box;
			transform: scale(0.833);
			transform-origin: center;
		}
	}
</style>
