<template>
	<BasePage v-bind="page" :aside-left-width="900">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey == 'title'">
				<div class="flex flex-items-center flex-justify-center gap-4px cursor-pointer">
					<Icon name="icon-jikou.png" style="margin-top: 2px" :size="{ width: '16px', height: '16px' }" />
					<div v-textEllipsis class="text-ellipsis-1" @click="handleDetail(row)" style="max-width: 300px">
						{{ searchFrom.type == 1 ? row.plan_title : searchFrom.type == 2 ? row.title : row.bug_title }}
					</div>
					<ElIcon size="14px" v-if="searchFrom.type != 3"><Edit @click="handleEditTask(row)" /></ElIcon>
					<ElIcon size="14px" v-if="searchFrom.type != 3"><Delete @click="handleDeleteTask(row)" /></ElIcon>
					<ElIcon size="14px" v-if="searchFrom.type == 1"
						><DocumentCopy @click="handleCopyTask(row)"
					/></ElIcon>
				</div>
			</template>

			<template v-else-if="column.columnKey == 'plan_start_date'">
				{{ searchFrom.type == 1 ? row.plan_start_date : row.start_time }}
			</template>
			<template v-else-if="column.columnKey == 'end_time_str'">
				{{ searchFrom.type == 3 ? row.end_time : row.end_time_str }}
			</template>
			<template v-else-if="column.columnKey == 'plan_end_date'">
				{{ searchFrom.type == 1 ? row.plan_end_date : row.completion_time }}
			</template>
			<template v-else-if="column.columnKey == 'person_in_charge_name'">
				{{
					searchFrom.type == 1
						? row.person_in_charge_name
						: searchFrom.type == 2
							? row.realName
							: row.assigned_to_user_name
				}}
			</template>
			<template v-else-if="column.columnKey == 'priority_name'">
				<ElTag disable-transitions type="primary" v-if="searchFrom.type != 3">{{ row.priority_name }}</ElTag>
				<ElTag disable-transitions type="primary" v-else-if="searchFrom.type == 3 && row.severity == 1"
					>轻微</ElTag
				>
				<ElTag disable-transitions type="warning" v-else-if="searchFrom.type == 3 && row.severity == 2"
					>一般</ElTag
				>

				<ElTag disable-transitions type="danger" v-else-if="searchFrom.type == 3 && row.severity == 3"
					>严重</ElTag
				>
			</template>
			<template v-else-if="column.columnKey == 'task_type_name'">
				<div class="flex items-center justify-center gap-6px" v-if="searchFrom.type != 3">
					{{ searchFrom.type == 1 ? row.task_type_name : row.task_attribute }}
					<ElTooltip placement="top">
						<ElIcon><QuestionFilled /></ElIcon>
						<template #content>
							{{ searchFrom.type == 1 ? tiptop[row.task_type_name] : tiptop[row.task_attribute] }}
						</template>
					</ElTooltip>
				</div>
				<div v-else>问题修复</div>
			</template>
			<template v-else-if="column.columnKey == 'qust_type_name'"> 问题类型 </template>
			<template v-else-if="column.columnKey == 'status'">
				<template v-if="searchFrom.type == 1">
					<ElTag disable-transitions type="info" v-if="row.plan_status == 1">{{
						row.plan_status_name
					}}</ElTag>
					<ElTag disable-transitions type="info" v-else-if="row.plan_status == 2">{{
						row.plan_status_name
					}}</ElTag>
					<ElTag disable-transitions type="primary" v-else-if="row.plan_status == 3">{{
						row.plan_status_name
					}}</ElTag>
					<ElTag disable-transitions type="success" v-else-if="row.plan_status == 4">{{
						row.plan_status_name
					}}</ElTag>
					<ElTag disable-transitions type="danger" v-else-if="row.plan_status == 5">{{
						row.plan_status_name
					}}</ElTag>
					<ElTag disable-transitions type="warning" v-else-if="row.plan_status == 6">{{
						row.plan_status_name
					}}</ElTag>
				</template>
				<template v-else>
					<ElTag disable-transitions type="info" v-if="row.status == 1">{{ row.statusName }}</ElTag>
					<ElTag disable-transitions type="info" v-else-if="row.status == 2">{{ row.statusName }}</ElTag>
					<ElTag disable-transitions type="primary" v-else-if="row.status == 3">{{ row.statusName }}</ElTag>
					<ElTag disable-transitions type="success" v-else-if="row.status == 4">{{ row.statusName }}</ElTag>
					<ElTag disable-transitions type="danger" v-else-if="row.status == 5">{{ row.statusName }}</ElTag>
					<ElTag disable-transitions type="warning" v-else-if="row.status == 6">{{ row.statusName }}</ElTag>
				</template>
			</template>
		</template>
		<template #pageHeader>
			<TableHeader title="任务名称">
				<template #title>
					<div class="flex flex-col gap-10px items-start w-full">
						<div style="white-space: nowrap">任务名称</div>
						<div class="flex justify-between w-full">
							<div class="flex gap-10px">
								<el-radio-group v-model:model-value="searchFrom.type" @change="handleTypeChange">
									<el-radio-button label="项目任务" :value="1">
										<template #default>
											<div class="flex items-center justify-center gap-6px">
												项目任务
												<ElTooltip placement="top">
													<ElIcon><QuestionFilled /></ElIcon>
													<template #content> 项目经理创建的任务</template>
												</ElTooltip>
											</div>
										</template>
									</el-radio-button>
									<el-radio-button
										label="开发任务"
										v-if="planData.currentItem?.plan_type != 4"
										:value="2"
									>
										<template #default>
											<div class="flex items-center justify-center gap-6px">
												开发任务
												<ElTooltip placement="top">
													<ElIcon><QuestionFilled /></ElIcon>
													<template #content> 开发组长创建的任务</template>
												</ElTooltip>
											</div>
										</template>
									</el-radio-button>
									<el-radio-button
										label="问题任务"
										v-if="planData.currentItem?.plan_type == 4"
										:value="3"
									>
										<template #default>
											<div class="flex items-center justify-center gap-6px">
												问题任务
												<ElTooltip placement="top">
													<ElIcon><QuestionFilled /></ElIcon>
													<template #content> 里程碑评审问题</template>
												</ElTooltip>
											</div>
										</template>
									</el-radio-button>
								</el-radio-group>
								<!-- <ElFormItem> -->
								<el-radio-group v-model:model-value="searchFrom.timeType" @change="handleTimerChange">
									<el-radio-button label="本日" :value="1" />
									<el-radio-button label="本周" :value="2" />
									<el-radio-button label="本月" :value="3" />
									<ElPopover width="300px" trigger="click" v-model:visible="searchFrom.showPopover">
										<ElForm>
											<ElFormItem>
												<ElDatePicker
													v-model:model-value="searchFrom.time"
													type="daterange"
													range-separator="至"
													start-placeholder="开始日期"
													end-placeholder="结束日期"
													:teleported="false"
													value-format="YYYY-MM-DD"
												/>
											</ElFormItem>
											<div style="width: 100%">
												<div class="flex w-full">
													<ElButton
														style="flex: 1"
														@click="((searchFrom.time = []), handleSearch())"
														>重置</ElButton
													>
													<ElButton style="flex: 1" type="primary" @click="handleSearch"
														>确认</ElButton
													>
												</div>
											</div>
										</ElForm>
										<template #reference>
											<el-radio-button label="自定义" :value="4" />
										</template>
									</ElPopover>
								</el-radio-group>
								<!-- </ElFormItem> -->
							</div>
							<div>
								<ElButton
									v-if="searchFrom.type == 1 && planData.currentItem?.plan_type != PlanType.MILTONE"
									@click="handleCreateTask"
									type="success"
									:disabled="disabledVersion"
									:icon="Plus"
									>创建任务</ElButton
								>
							</div>
						</div>
					</div>
				</template>
			</TableHeader>
		</template>
		<template #pageLeft>
			<div h="100%" class="p-10px">
				<TableHeader title="项目计划">
					<template #title>
						<div class="flex flex-col gap-10px items-start w-full">
							<div style="white-space: nowrap">项目计划</div>
							<div class="flex justify-between w-full">
								<div class="flex gap-10px">
									<ElSelect
										style="width: 150px"
										v-model:model-value="iteration.currentId"
										placeholder="请选择迭代"
										@change="handleChangeIteration"
									>
										<ElOption
											v-for="item in iteration.list"
											:key="item.id"
											:label="item.iteration_name"
											:value="item.id"
										>
										</ElOption>
										<template #footer>
											<ElButton
												style="width: 100%"
												type="primary"
												size="small"
												@click="handleEditVersionOper"
												>添加迭代</ElButton
											>
										</template>
									</ElSelect>
									<ElButton
										plain
										type="primary"
										@click="handleEditVersion"
										:disabled="disabledVersion"
										>编辑</ElButton
									>
									<ElButton
										style="margin-left: 0"
										:disabled="disabledVersion"
										@click="handleDeleteVersionOper"
										>删除</ElButton
									>
								</div>
								<div>
									<ElButton
										@click="handleAddMiltoneGroup"
										type="primary"
										:disabled="disabledVersion"
										:icon="Plus"
										>添加里程碑</ElButton
									>
									<ElButton
										@click="handleAddPlan"
										type="success"
										:disabled="disabledVersion"
										:icon="Plus"
										>添加计划</ElButton
									>
									<ElButton
										@click="handleAddPlanGroup"
										type="warning"
										:disabled="disabledVersion"
										:icon="Plus"
										>添加阶段</ElButton
									>
								</div>
							</div>
						</div>
					</template>
				</TableHeader>
				<ElTable
					border
					:data="planData.dataSource"
					:tree-props="{
						children: 'children',
						hasChildren: 'hasChildren'
					}"
					row-key="id"
					ref="tableRef"
					class="custom-table-tr"
					highlight-current-row
					@row-click="handleRowClick"
				>
					<!-- @cell-click="handleCellClick" -->
					<ElTableColumn label="标题" width="300px" prop="title" align="center" class-name="custom-cell">
						<template #header>
							<div class="flex gap-10px cursor-pointer flex-items-center">
								<Icon
									@click="handleColed"
									name="icon-menu-col.png"
									:size="{ width: '16px', height: '16px' }"
									v-if="!planData.isColed"
								>
								</Icon>
								<Icon
									v-else
									@click="handleColed"
									name="icon-menu-coled.png"
									:size="{ width: '16px', height: '16px' }"
								>
								</Icon>
								<span>标题</span>
								<Icon name="icon-question.png" :size="{ width: '16px', height: '16px' }"> </Icon>
							</div>
						</template>
						<template #default="scope">
							<div class="custom-cell-flex" @click="handleClickDetail(scope.row)">
								<Icon
									:name="icons[Number(scope.row.plan_type) - 1]"
									:size="{ width: '16px', height: '16px' }"
								/>

								<span
									style="
										white-space: nowrap;
										display: inline-block;
										max-width: 140px;
										overflow: hidden;
										text-overflow: ellipsis;
									"
									v-textEllipsis
									:class="scope.row.plan_type == PlanType.PLAN_GROUP ? '' : 'text-ellipsis-1'"
								>
									{{ scope.row.plan_title }}
								</span>
								<div @click.stop="handleEdit(scope.row)">
									<ElIcon><Edit /></ElIcon>
								</div>

								<div @click.stop="handleDelete(scope.row)">
									<ElIcon><Delete /></ElIcon>
								</div>
							</div>
						</template>
					</ElTableColumn>
					<ElTableColumn label="类型" prop="type" align="center">
						<template #default="scope">
							<span v-if="scope.row.plan_type == 1"> 阶段 </span>
							<span v-else-if="scope.row.plan_type == 2"> 计划 </span>
							<span v-else-if="scope.row.plan_type == 4"> 里程碑 </span>
						</template>
					</ElTableColumn>
					<ElTableColumn label="进度" prop="progress" align="center" width="140px">
						<template #default="scope">
							<div v-if="scope.row.plan_type == 4">
								<ElTag
									disable-transitions
									type="warning"
									v-if="scope.row.milestone_status_name == '未完成'"
									>未完成</ElTag
								>
								<ElTag
									disable-transitions
									type="danger"
									v-else-if="scope.row.milestone_status_name == '未通过'"
									>未通过</ElTag
								>
								<ElTag
									disable-transitions
									type="success"
									v-else-if="scope.row.milestone_status_name == '通过'"
									>通过</ElTag
								>
								<ElTag disable-transitions type="info" v-else>未评审</ElTag>
							</div>
							<div v-else>
								<span style="margin-right: 10px">{{ scope.row.progress }}</span>
								<span>{{ scope.row.finishedCount || 0 }}</span>
								/
								<span>{{ scope.row.taskCount || 0 }}</span>
							</div>
						</template>
					</ElTableColumn>
					<ElTableColumn label="负责人" width="90px" prop="person_in_charge_name" align="center">
						<template #default="scope">
							<span v-if="scope.row.person_in_charge_name">
								{{ scope.row.person_in_charge_name.split(",")[0] }}
								{{ scope.row.person_in_charge_name.split(",").length > 1 ? "等" : "" }}
							</span>
						</template>
					</ElTableColumn>
					<ElTableColumn label="开始日期" prop="plan_start_date" align="center" width="140px">
						<template #default="scope">
							<div class="flex items-center justify-center gap-6px">
								{{ scope.row.plan_start_date }}
								<ElTooltip v-if="scope.row.plan_type == 4 && scope.row.plan_start_date" placement="top">
									<ElIcon><QuestionFilled /></ElIcon>
									<template #content>约定完成时间 </template>
								</ElTooltip>
							</div>
						</template>
					</ElTableColumn>
					<ElTableColumn label="完成日期" prop="plan_end_date" width="140px" align="center">
						<template #default="scope">
							<div class="flex items-center justify-center gap-6px">
								{{ scope.row.plan_end_date }}
								<ElTooltip placement="top" v-if="scope.row.plan_type == 4 && scope.row.plan_end_date">
									<ElIcon><QuestionFilled /></ElIcon>
									<template #content>评审完成时间 </template>
								</ElTooltip>
							</div>
						</template>
					</ElTableColumn>
					<!-- <ElTableColumn label="状态" prop="status" align="center"> </ElTableColumn> -->
				</ElTable>
				<!-- <div w="100%" class="add-table-item" @click="handleAddItem">
					<ElButton :disabled="disabledVersion" text link :icon="Plus" type="primary">新建</ElButton>
				</div> -->
				<div class="add-table-col" v-if="planData.showAddItem">
					<div class="add-group-type">
						<ElInput v-model:model-value="planData.addObj.plan_title" @blur="handleBlur">
							<template #prepend>
								<ElPopover v-model:visible="planData.showSelect" trigger="click">
									<template #reference>
										<div class="flex flex-items-center" @click="handleSlect">
											<Icon
												:name="planData.selectType == 1 ? `icon-wenjian.png` : `icon-plan.png`"
												:size="{
													width: '16px',
													height: '16px'
												}"
											/>
											<ElIcon class="m-l-6px">
												<CaretTop v-if="planData.showSelect" />
												<CaretBottom v-else />
											</ElIcon>
										</div>
									</template>
									<ElMenu class="custom-menupop" @select="handleSelectMenu">
										<ElMenuItem index="1">
											<Icon
												name="icon-wenjian.png"
												:size="{
													width: '16px',
													height: '16px'
												}"
											/>
											<span class="m-l-10px">阶段</span>
										</ElMenuItem>
										<ElMenuItem index="2">
											<Icon
												name="icon-plan.png"
												:size="{
													width: '16px',
													height: '16px'
												}"
											/>
											<span class="m-l-10px">计划任务</span>
										</ElMenuItem>
									</ElMenu>
								</ElPopover>
							</template>
						</ElInput>
					</div>
					<div class="add-group-item">计划</div>
					<div class="add-group-item">0%</div>
					<div class="add-group-item">--</div>
					<div class="add-group-item" w="100px">
						<ElDatePicker
							style="width: 100px"
							v-model:model-value="planData.addObj.plan_start_date"
							value-format="YYYY-MM-DD"
						/>
					</div>
					<div class="add-group-item" w="100px">
						<ElDatePicker
							style="width: 100px"
							v-model:model-value="planData.addObj.plan_end_date"
							value-format="YYYY-MM-DD"
						/>
					</div>
					<!-- <div class="add-group-item">--</div> -->
				</div>
			</div>
		</template>
		<template #pageBodySearch>
			<div class="flex justify-end">
				<ElForm :inline="true" :model="searchFrom" class="flex">
					<!-- <ElRow :gutter="4"> -->
					<!-- <ElCol :span="10"> -->

					<!-- </ElCol> -->
					<!-- <ElCol :span="4"> -->
					<ElFormItem>
						<ElInput
							clearable
							v-model:model-value="searchFrom.plan_title"
							placeholder="请输入标题"
							@keydown.enter="handleSearch"
						/>
					</ElFormItem>
					<!-- </ElCol> -->
					<!-- <ElCol :span="4"> -->
					<!-- <ElFormItem>
						<ElSelect
							style="width: 150px"
							v-model:model-value="searchFrom.person_in_charge"
							placeholder="请选择负责人"
							clearable
						>
							<ElOption
								:label="item.username"
								:value="item.id"
								v-for="item in searchFrom.userList"
								:key="item.id"
							/>
						</ElSelect>
					</ElFormItem> -->
					<!-- </ElCol> -->
					<!-- <ElCol :span="1"> -->
					<ElFormItem>
						<ElButton @click="handleSearch" type="primary">查询</ElButton>
						<ElButton @click="handleReset">重置</ElButton>
					</ElFormItem>
					<!-- </ElCol> -->
					<!-- </ElRow> -->
				</ElForm>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import { Edit, Plus, Delete, CaretTop, CaretBottom, QuestionFilled, DocumentCopy } from "@element-plus/icons-vue";
	import { Icon } from "@/components/icon/icon";
	import { nextTick, reactive, ref, computed, watch } from "vue";
	import TableHeader from "@/core/struct/table/table-header";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import AddPlanGroup from "./dialog/add-plan-group.vue";
	import AddPlan from "./dialog/add-plan.vue";
	import AddPlanTask from "./dialog/add-plan-task.vue";
	import TaskDetail from "../../task/task-detail/index.vue";
	import PlanDetail from "../plan-detail/index.vue";
	import {
		createQuiklyRapid,
		delEntityById,
		deletePlan,
		PlanType,
		selectPlanById,
		selectPlanTree
	} from "@/api/project/plan";
	import { appInstance } from "@/main";
	import { ElMessage, ElMessageBox, type Action, type TableInstance, ElForm, ElFormItem } from "element-plus";
	import { useVersion } from "./dialog/version";
	import AddMilestone from "../milestone/dialog/add-milestone.vue";
	import { usePlanSearch } from "./dialog/search";
	import { deleteById } from "@/api/project/milestone";
	import MilestoneDetail from "../milestone-detail/index.vue";
	import EditTask from "./dialog/edit-task.vue";
	import DetailPage from "@/pages/modules/task/task-detail/index.vue";
	defineOptions({
		name: "Plan"
	});
	const { searchFrom, handleTimeChange, getSearchData, handleSearchFormReset } = usePlanSearch();
	const tiptop = {
		前端: "交给前端组长去分配功能点的任务",
		后端: "交给后端组长去分配功能点的任务",
		测试: "交给测试组长去分配功能点的任务",
		产品: "项目经理直接发给产品人员去做的任务",
		售前: "项目经理直接发给售前人员去做的任务"
	};
	const {
		handleAddVersion,
		handleDeleteVersion,
		iteration,
		getVersionList,
		setSKey,
		disabledVersion,
		handleEditVersion
	} = useVersion();
	const icons = [`icon-wenjian.png`, `icon-plan.png`, "", `icon-zu.png`];

	const tableRef = ref<TableInstance>();

	const planData = reactive({
		dataSource: [],
		showSelect: false,
		selectType: 1,
		showAddItem: false,
		addObj: {
			plan_title: "",
			plan_type: PlanType.PLAN_GROUP,
			progress: 0,
			manger: "",
			plan_start_date: "",
			plan_end_date: "",
			status: ""
		},
		currentItem: null as any,
		isColed: true
	});

	const columns1 = [
		{
			label: "标题",
			prop: "title",
			columnKey: "title",
			align: "center",
			minWidth: "350px"
		},
		{
			label: "任务类型",
			prop: "task_type_name",
			columnKey: "task_type_name",
			align: "center",
			width: "140px",
			filters: appInstance.mapDict.task_attribute?.map((item) => {
				return {
					text: item.label,
					value: item.value
				};
			}),
			filterMethod: (e: number, row: any) => {
				if (e == -1) return true;
				return row.task_type == e;
			}
		},
		{
			label: "负责人",
			prop: "person_in_charge_name",
			columnKey: "person_in_charge_name",
			align: "center",
			width: "120px",
			filters: [],
			filterMethod: (e: number, row: any) => {
				if (searchFrom.type == 3) {
					return row.assigned_to_user_id == e;
				} else {
					return row.person_in_charge == e;
				}
			}
		},
		{
			label: "开始日期",
			prop: "plan_start_date",
			columnKey: "plan_start_date",
			align: "center",
			width: "180px"
		},
		{
			label: "完成日期",
      prop: "plan_end_date",
      columnKey: "plan_end_date",
			align: "center",
			width: "180px"
		},
		{
			label: "状态",
			prop: "status",
			columnKey: "status",
			align: "center",
			width: "100px"
		},
		{
			label: "优先级",
			prop: "priority_name",
			columnKey: "priority_name",
			align: "center",
			width: "120px"
		},
		{
			label: "截止日期",
      prop: "end_time_str",
      columnKey: "end_time_str",
			align: "center",
			showOverflowTooltip: true,
			width: "180px"
		}
	];

	watch(
		() => searchFrom.userList,
		(val) => {
			columns1[2].filters = val.map((item) => {
				return {
					text: item.username,
					value: item.id
				};
			});
		}
	);

	const { page } = usePage({
		title: "计划任务",
		pagination: {
			pageSize: 20,
			currentPage: 1,
			total: 15,
			background: true,
			layout: "prev, pager, next, jumper, ->, total",
			onChange(pageNo: number, pageSize: number) {
				if (page.pagination) {
					page.pagination.currentPage = pageNo;
					page.pagination.pageSize = pageSize;
					selectPlanById({
						id: planData.currentItem.id,
						pageSize: page.pagination?.pageSize,
						pageNo: page.pagination?.currentPage,
						...getSearchData()
					}).then(({ records, total }) => {
						if (page.table) {
							page.table.dataSource = records;
							if (page.pagination) {
								page.pagination.total = total;
							}
						}
					});
				}
			}
		},
		tableConfig: {
			dataSource: [],
			bordered: true,
			attrs: {
				fit: true,
				border: true,
				// height: "100%",
				rowClassName: ({ rowIndex }) => {
					if (rowIndex % 2 != 0) {
						return "warning-row";
					} else {
						return "success-row";
					}
				}
			},
			columns: columns1
		},
		toolbar: [
			// {
			// 	type: "text",
			// 	label: "共n条",
			// 	text: true,
			// 	link: true
			// },
			// {
			// 	type: "success",
			// 	icon: markRaw(Plus),
			// 	label: "创建计划任务",
			// 	onClick: () => {
			// 	},
			// 	disabled: planData.currentItem == null
			// }
		]
	});
	const isGroup = computed(() => {
		return planData.currentItem && planData.currentItem.plan_type == PlanType.PLAN_GROUP;
	});

	const handleSearch = () => {
		searchFrom.showPopover = false;
		if (page.pagination) {
			page.pagination.currentPage = 1;
			selectPlanById({
				id: planData.currentItem.id,
				pageSize: page.pagination.pageSize,
				pageNo: page.pagination.currentPage,
				...getSearchData(),
				milestoneId: Math.abs(planData.currentItem.id),
				assignedToUserId: null
			}).then(({ records, total }) => {
				if (page.table) {
					page.table.dataSource = records;
					if (page.pagination) page.pagination.total = total;
				}
			});
		}
	};

	const handleTypeChange = () => {
		if (page.table) {
			page.table.dataSource = [];
		}
		if (searchFrom.type == 3) {
			page.table.columns[6].label = "严重程度";
			page.table.columns[1].filters = [{ text: "问题修复", value: -1 }];
		} else {
			page.table.columns[6].label = "优先级";
			page.table.columns[1].filters = appInstance.mapDict.task_attribute?.map((item) => {
				return {
					text: item.label,
					value: item.value
				};
			});
		}

		handleSearch();
	};

	const handleReset = () => {
		handleSearchFormReset();
		handleSearch();
	};

	const handleTimerChange = (e: number) => {
		if (handleTimeChange(e)) handleSearch();
	};

	const handleColed = () => {
		planData.dataSource.forEach((item) => {
			tableRef.value?.toggleRowExpansion(item);
		});
		planData.isColed = !planData.isColed;
	};

	const handleClickDetail = (row: any) => {
		if (row.plan_type == 2) {
			createDrawerAsync(
				{
					title: "计划详情",
					width: "1280px"
				},
				{},
				<PlanDetail id={row.id} query_detail_status={1} />
			);
		} else if (row.plan_type == 4) {
			createDrawerAsync(
				{ title: "里程碑详情", width: "1280px", showConfirm: false, showCancel: false },
				{},
				<MilestoneDetail id={row.milestone_id} dialog />
			);
		}
	};

	const handleAddMiltoneGroup = () => {
		createModelAsync(
			{
				title: "添加里程碑",
				width: "1080px",
				showNext: false,
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddMilestone
				iterationId={iteration.currentId}
				groupId={isGroup.value ? planData.currentItem?.id : null}
				limitStartTime={isGroup.value ? planData.currentItem?.plan_start_date : null}
				limitEneTime={isGroup.value ? planData.currentItem?.plan_end_date : null}
			/>
		).then(() => {
			refreshPlanTree();
			// refreshProjectStatus();
			// refreshTable();
			// countMilestone(appInstance.projectItem.id).then(({ data }) => {
			// 	pageData.count = data;
			// });
		});
	};
	// 计划树表格行点击
	const handleRowClick = (row: any) => {
		tableRef.value?.setCurrentRow(row);
		planData.currentItem = row;

		searchFrom.type = 1;
		handleSearch();
	};

	const handleCellClick = (row: any, cell: any) => {
		const { property } = cell;
		tableRef.value?.setCurrentRow(row);
		if (["plan_start_date", "plan_end_date", "person_in_charge_name"].includes(property)) {
		}
		planData.currentItem = row;
		handleSearch();
	};

	const handleCreateTask = () => {
		createDrawerAsync(
			{
				title: "添加计划任务",
				width: "1400px",
				showNext: true,
				closeOnClickModal: false,
				closeOnPressEscape: false,
				nextNotRest: ["person_in_charge", "priority", "end_time"]
			},
			{},
			<AddPlanTask
				row={planData.currentItem}
				iterationId={iteration.currentId}
				groupId={planData.currentItem?.id}
				limitStartTime={planData.currentItem?.plan_start_date}
				limitEneTime={planData.currentItem?.plan_end_date}
			/>
		).then(() => {
			selectPlanById({
				id: planData.currentItem.id,
				pageSize: page.pagination?.pageSize,
				pageNo: page.pagination?.currentPage,
				...getSearchData()
			}).then(({ records, total }) => {
				if (page.table) {
					page.table.dataSource = records;
					if (page.pagination) page.pagination.total = total;
				}
			});
		});
	};

	// 切换迭代
	const handleChangeIteration = (e: number) => {
		setSKey(e);
		if (page.table) page.table.dataSource = [];
		getPlanTree(e);
	};
	// 刷新计划树
	const refreshPlanTree = async () => {
		await getVersionList();
		if (iteration.list.length != 0) {
			await getPlanTree(iteration.currentId);
		}
	};
	// 根据迭代id获取计划树
	const getPlanTree = async (iteration_id: number | string) => {
		const planTreeData = await selectPlanTree({
			project_id: appInstance.projectItem.id,
			iteration_id
		});

		planData.dataSource = planTreeData;
		if (planTreeData.length != 0) {
			nextTick(() => {
				// if (!planData.currentItem) {
				handleRowClick(planData.dataSource[0]);
				// }
				// if (page.toolbar) {
				// 	page.toolbar[0].disabled = false;
				// }
			});
		} else {
			// if (page.toolbar) {
			// 	page.toolbar[0].disabled = true;
			// }
		}
	};
	const handleSelectMenu = (e: any) => {
		planData.selectType = e;
		planData.addObj.plan_type = e;
		planData.showSelect = false;
	};
	const handleAddItem = () => {
		if (disabledVersion.value) return;
		planData.showAddItem = !planData.showAddItem;
	};
	const handleBlur = () => {
		if (planData.addObj.plan_title) {
			if (planData.addObj.plan_start_date && planData.addObj.plan_end_date) {
				createQuiklyRapid({
					plan_type: planData.addObj.plan_type,
					plan_end_date: planData.addObj.plan_end_date,
					plan_start_date: planData.addObj.plan_start_date,
					plan_title: planData.addObj.plan_title,
					project_id: appInstance.projectItem.id
				}).then(() => {
					planData.addObj.plan_start_date = "";
					planData.addObj.plan_end_date = "";
					planData.addObj.plan_title = "";
					refreshPlanTree();
					planData.showAddItem = !planData.showAddItem;
				});
			} else {
				ElMessage.warning("请选择开始日期和结束日期");
			}
		} else {
			planData.showAddItem = !planData.showAddItem;
		}
	};
	const handleSlect = () => {};
	// 添加计划
	const handleAddPlan = () => {
		createModelAsync(
			{
				title: "添加计划",
				width: "1080px",
				showNext: true,
				closeOnClickModal: false,
				closeOnPressEscape: false,
				nextNotRest: ["parent_id", "plan_start_date"]
			},
			{},
			<AddPlan
				iterationId={iteration.currentId}
				groupId={isGroup.value ? planData.currentItem?.id : null}
				limitStartTime={isGroup.value ? planData.currentItem?.plan_start_date : null}
				limitEneTime={isGroup.value ? planData.currentItem?.plan_end_date : null}
			/>
		).then(() => {
			refreshPlanTree();
		});
	};
	// 添加计划组
	const handleAddPlanGroup = () => {
		createModelAsync(
			{
				title: "添加阶段",
				width: "1080px",
				showNext: true,
				// 不需要重置
				nextNotRest: ["person_in_charge", "plan_start_date"],
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddPlanGroup iterationId={iteration.currentId} />
		).then(() => {
			refreshPlanTree();
		});
	};
	// 删除
	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					if (row.plan_type == PlanType.MILTONE) {
						deleteById({ id: row.milestone_id }).then(() => {
							refreshPlanTree();
						});
					} else {
						deletePlan({ id: row.id }).then(() => {
							refreshPlanTree();
						});
					}
				}
			}
		});
	};
	// 编辑计划/阶段/里程碑
	const handleEdit = (row: any) => {
		if (row.plan_type == PlanType.PLAN) {
			let currentItem: any | null = null;
			for (let i = 0; i < planData.dataSource.length; i++) {
				if (planData.dataSource[i].id == row.parent_id) {
					currentItem = planData.dataSource[i];
					break;
				}
			}
			createModelAsync(
				{
					title: "编辑计划",
					width: "1080px",
					showNext: false,
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddPlan
					id={row.id}
					iterationId={iteration.currentId}
					limitStartTime={currentItem?.plan_start_date}
					limitEneTime={currentItem?.plan_end_date}
				/>
			).then(() => {
				refreshPlanTree();
			});
		} else if (row.plan_type == PlanType.PLAN_GROUP) {
			createModelAsync(
				{
					title: "编辑阶段",
					width: "1080px",
					showNext: false,
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddPlanGroup id={row.id} iterationId={iteration.currentId} />
			).then(() => {
				refreshPlanTree();
			});
		} else if (row.plan_type == PlanType.MILTONE) {
			let currentItem: any | null = null;
			for (let i = 0; i < planData.dataSource.length; i++) {
				if (planData.dataSource[i].id == row.parent_id) {
					currentItem = planData.dataSource[i];
					break;
				}
			}
			createModelAsync(
				{
					title: "编辑里程碑",
					width: "1080px",
					showNext: false,
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddMilestone
					id={row.milestone_id}
					iterationId={iteration.currentId}
					limitEneTime={currentItem?.plan_end_date}
				/>
			).then(() => {
				refreshPlanTree();
				// refreshProjectStatus();
				// refreshTable();
				// countMilestone(appInstance.projectItem.id).then(({ data }) => {
				// 	pageData.count = data;
				// });
			});
		}
	};

	// 编辑计划任务
	const handleCopyTask = async (row: any) => {
		createDrawerAsync(
			{
				title: "任务复制",
				width: "1400px",
				showNext: false,
				closeOnClickModal: false,
				closeOnPressEscape: false
			},
			{},
			<AddPlanTask
				row={planData.currentItem}
				limitStartTime={planData.currentItem?.plan_start_date}
				limitEneTime={planData.currentItem?.plan_end_date}
				iterationId={iteration.currentId}
				id={row.id}
				isCopy
			/>
		).then(() => {
			selectPlanById({
				id: planData.currentItem.id,
				pageSize: page.pagination?.pageSize,
				pageNo: page.pagination?.currentPage,
				...getSearchData()
			}).then(({ records, total }) => {
				if (page.table) {
					page.table.dataSource = records;
					if (page.pagination) {
						page.pagination.total = total;
					}
				}
			});
		});
	};

	// 编辑计划任务
	const handleEditTask = async (row: any) => {
		if (searchFrom.type == 1) {
			createDrawerAsync(
				{
					title: "编辑计划任务",
					width: "1400px",
					showNext: false,
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddPlanTask
					row={planData.currentItem}
					limitStartTime={planData.currentItem?.plan_start_date}
					limitEneTime={planData.currentItem?.plan_end_date}
					iterationId={iteration.currentId}
					id={row.id}
				/>
			).then(() => {
				selectPlanById({
					id: planData.currentItem.id,
					pageSize: page.pagination?.pageSize,
					pageNo: page.pagination?.currentPage,
					...getSearchData()
				}).then(({ records, total }) => {
					if (page.table) {
						page.table.dataSource = records;
						if (page.pagination) {
							page.pagination.total = total;
						}
					}
				});
			});
		} else {
			createDrawerAsync(
				{
					title: "编辑",
					showNext: false,
					width: "1056px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<EditTask id={row.task_info_id} planId={row.task_plan_id} iterationId={iteration.currentId} />
			).then(() => {
				handleSearch();
			});
		}
	};
	// 删除计划任务
	const handleDeleteTask = (row: any) => {
		if (searchFrom.type == 1) {
			ElMessageBox.alert("是否删除该条数据", "提示", {
				confirmButtonText: "确认",
				callback: (action: Action) => {
					if (action == "confirm") {
						deletePlan({ id: row.id }).then(() => {
							selectPlanById({
								id: planData.currentItem.id,
								pageSize: page.pagination?.pageSize,
								pageNo: page.pagination?.currentPage,
								...getSearchData()
							}).then((res) => {
								if (page.table) {
									page.table.dataSource = res.records;
								}
							});
						});
					}
				}
			});
		} else {
			ElMessageBox.alert("是否删除该条数据", "提示", {
				confirmButtonText: "确认",
				callback: (action: Action) => {
					if (action == "confirm") {
						delEntityById({ id: row.task_info_id }).then(() => {
							handleSearch();
						});
					}
				}
			});
		}
	};
	// 任务详情
	const handleDetail = (row: any) => {
		// appInstance.router.push({
		// 	path: "/task/task-detail",
		// 	query: { id: row.task_info_id, type: 1 }
		// });

		if (searchFrom.type == 1) {
			createDrawerAsync(
				{ width: "1280px", showConfirm: false, title: row.plan_title },
				{},
				<PlanDetail id={row.id} type={"1"} query_detail_status={2} />
			);
		} else if (searchFrom.type == 3) {
			createDrawerAsync(
				{ title: row.bug_title, width: "1100px", showNext: false, showConfirm: false },
				{},
				<DetailPage type={2} id={row.id} />
			);
		} else {
			createDrawerAsync(
				{ width: "1280px", showConfirm: false, title: row.title },
				{},
				<TaskDetail id={row.task_info_id} type={"1"} />
			);
		}
	};
	// 删除迭代
	const handleDeleteVersionOper = async () => {
		await handleDeleteVersion();
		if (iteration.currentId) {
			await getPlanTree(iteration.currentId);
		} else {
			planData.dataSource = [];
			if (page.table) {
				page.table.dataSource = [];
			}

			planData.currentItem = null;
		}
	};
	// 添加迭代
	const handleEditVersionOper = async () => {
		await handleAddVersion();
		if (iteration.currentId) {
			await getPlanTree(iteration.currentId);
		}
	};

	refreshPlanTree();
</script>

<style lang="scss">
	.custom-cell {
		.cell {
			display: flex;
			align-items: center;
		}
	}
</style>

<style lang="scss" scoped>
	.text-ellipsis-1 {
		font-size: 14px;
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
		color: var(--el-color-primary);
		text-decoration: underline;
	}
	.custom-cell-flex {
		display: flex;
		align-items: center;
		gap: 4px;
		cursor: pointer;
		div {
			display: flex;
			align-items: center;
		}
	}
	.el-form--inline .el-form-item {
		margin-right: 10px;
	}
	.add-table-item {
		border-left: 1px solid var(--el-border-color);
		border-right: 1px solid var(--el-border-color);
		border-bottom: 1px solid var(--el-border-color);
		padding: 10px;
	}
	.add-table-col {
		background: var(--el-menu-c-hover);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		height: 48px;
		margin-top: 10px;
		display: flex;
		align-items: center;
		.add-group-type {
			width: 300px;
			padding: 0 10px;
		}
		.add-group-item {
			flex: 1;
			text-align: center;
			padding: 0 4px;
		}
	}
	.el-table tr {
		cursor: pointer;
	}
</style>
