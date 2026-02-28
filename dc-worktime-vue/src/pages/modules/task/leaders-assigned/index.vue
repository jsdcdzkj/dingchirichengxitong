<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'title'">
				<ElLink type="primary" @click="handleJumpDetail(row)">
					{{ row.title }}
				</ElLink>
			</template>
			<template v-if="column.columnKey === 'completion_time'">
				<div>{{ row.completion_time?.substring(0, 10) }}</div>
			</template>
			<template v-if="column.columnKey === 'end_time'">
				<div>{{ row.end_time?.substring(0, 10) }}</div>
			</template>
			<template v-if="column.columnKey === 'urgency_name'">
				<span class="degree" v-if="row.urgency == 1"
					><i class="degree-3"></i>普通</span
				>
				<span class="degree" v-if="row.urgency== 2"
					><i class="degree-4"></i>紧急</span
				>
				<span class="degree" v-if="row.urgency == 3"
					><i class="degree-5"></i>非常紧急</span
				>
			</template>
			<template v-if="column.columnKey === 'task_rating'">
				<div>{{ row.task_rating ? row.task_rating + '分' : '' }}</div>
			</template>
			<template v-if="column.columnKey === 'fileList'">
				<div v-for="item in row.fileList" :key="item.id">
					<ElLink type="primary" @click="handleDownload(item)">
						{{ item.fileName }}
					</ElLink>
				</div>
			</template>
		</template>
		<template #tableActions="scope">
			<!-- <ElButton v-if="scope.row.status == 1" type="info" plain size="small" @click="handleAssign(scope.row)"
				>指派</ElButton
			> -->
			<ElButton  v-if="scope.row.status != 7" type="primary" size="small" plain @click="handleProgress(scope.row)">跟踪进度</ElButton>
			<ElButton
				v-if="['1', '2', '3', '5', '8', '9'].includes(scope.row.status)"
				type="primary"
				size="small"
				plain
				@click="handleAbort(scope.row)"
				>终止</ElButton
			>
			<ElButton v-if="scope.row.status == 4" type="primary" plain size="small" @click="handleRate(scope.row)"
				>评分</ElButton
			>
			<ElButton v-if="scope.row.status == 4" type="danger" plain size="small" @click="handleReject(scope.row)"
				>打回</ElButton
			>
			<ElButton v-if="['1', '2', '5'].includes(scope.row.status)" type="primary"  plain size="small" @click="handleEdit(scope.row)">编辑</ElButton>
			<ElButton v-if="['1', '2', '5'].includes(scope.row.status)" type="danger"  plain size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import AddTask from "./dialog/add-task.vue";
	import AddScore from "./dialog/add-score.vue";
	import AddReject from "./dialog/add-reject.vue";
	import TrackProgress from "./dialog/track-progress.vue";
	import DesignateTask from "./dialog/designate-task.vue";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { getPageList, delEntityById, abortTask } from "@/api/task/leaders-assigned";
	import { ElMessageBox, type Action } from "element-plus";
	import { appInstance } from "@/main";
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import DetailPage from "../task-detail/index.vue";

	const formConfig: FormConfig = {
		span: 6,
		expandSpan: 4,
		labelWidth: 80,
		labelPosition: "right",
		notMargn: false,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "title",
				label: "任务标题",
				attrs: {
					placeholder: "请输入任务标题",
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
				prop: "assigned_to_user_name",
				label: "指派至",
				attrs: {
					placeholder: "请输入被指派人",
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
				prop: "status",
				label: "状态",
				attrs: {
					placeholder: "请选择状态",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: [
						{
							value: 1,
							label: "待指派"
						},
						{
							value: 2,
							label: "待开始"
						},
						{
							value: 3,
							label: "进行中"
						},
						{
							value: 4,
							label: "已完成"
						},
						{
							value: 5,
							label: "被打回"
						},
						{
							value: 6,
							label: "已终止"
						},
						{
							value: 7,
							label: "已评分"
						},
						{
							value: 8,
							label: "已挂起"
						},
						{
							value: 9,
							label: "已暂停"
						}
					]
				}
			},
			{
				label: "紧急程度",
				value: "",
				type: "ElSelect",
				prop: "urgency",
				attrs: {
					clearable: true
				},
				select: {
					type: "ElOption",
					list: appInstance.mapDict.urgency
				}
			},
			{
				type: "ElInput",
				value: "",
				prop: "proposer_user_name",
				label: "提出人",
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
				type: "ElInput",
				value: "",
				prop: "createUserName",
				label: "创建人",
				attrs: {
					placeholder: "请输入创建人",
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
				prop: "time1",
				label: "创建时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "时间",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time2",
				label: "开始时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "时间",
					valueFormat: "YYYY-MM-DD"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time3",
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
		title: "领导交办任务列表",
		listApi: async (data) => {
			const [createStartTime, createEndTime] = data.time1 || [];
			const [startStartTime, startEndTime] = data.time2 || [];
			const [completionStartTime, completionEndTime] = data.time3 || [];
			const params = {
				...data,
				createStartTime,
				createEndTime,
				startStartTime,
				startEndTime,
				completionStartTime,
				completionEndTime
			};
			delete params.time1;
			delete params.time2;
			delete params.time3;
			return await getPageList(params);
		},
		toolbar: [
			{
				label: "创建任务",
				type: "success",
				size: "default",
				// icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync({ title: "创建任务", width: "1000px", showNext: false, closeOnClickModal: false,closeOnPressEscape: false }, {}, <AddTask />).then(
						() => {
							pageApi.pageList();
						}
					);
				}
			}
		],
		pagination: {
			currentPage: 1,
			pageSize: 20,
			total: 15,
			background: true,
			layout: "prev, pager, next, jumper, ->, total",
			onChange(pageNo: number, pageSize: number) {
				if (page.pagination) {
					page.pagination.currentPage = pageNo;
					page.pagination.pageSize = pageSize;
					pageApi.pageList();
				}
			}
		},
		tableConfig: {
			dataSource: [{ label: 1 }],

			attrs: {
				fit: true,
				border: true,
				height: "100%",
				rowClassName: ({ rowIndex }) => {
					if (rowIndex % 2 != 0) {
						return "warning-row";
					} else {
						return "success-row";
					}
				}
			},
			columns: [
				// {
				// 	columnKey: "check",
				// 	label: "",
				// 	type: "selection",
				// 	prop: "check",
				// 	width: "80px",
				// 	align: "center"
				// },
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					columnKey: "urgency_name",
					label: "紧急程度",
					prop: "urgency_name",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "proposer_user_name",
					label: "提出人",
					prop: "proposer_user_name",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "title",
					label: "任务标题",
					prop: "title",
					align: "center",
					showOverflowTooltip: true,
					minWidth: '180px'
				},
				// {
				// 	columnKey: "description",
				// 	label: "任务描述",
				// 	prop: "description",
				// 	align: "center"
				// },
				// {
				// 	columnKey: "fileList",
				// 	label: "任务附件",
				// 	prop: "fileList",
				// 	align: "center"
				// },
				{
					columnKey: "assigned_to_user_name",
					label: "指派至",
					prop: "assigned_to_user_name",
					align: "center"
				},
				// {
				// 	columnKey: "response_time",
				// 	label: "响应时间",
				// 	prop: "response_time",
				// 	align: "center"
				// },
				{
					columnKey: "start_time",
					label: "开始时间",
					prop: "start_time",
					align: "center",
					width: "180px",
				},
				{
					columnKey: "completion_time",
					label: "完成/终止时间",
					prop: "completion_time",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "end_time",
					label: "截止时间",
					prop: "end_time",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "task_rating",
					label: "任务评分",
					prop: "task_rating",
					align: "center",
					width: '100px'
				},
				{
					columnKey: "statusName",
					label: "状态",
					prop: "statusName",
					align: "center"
				},
				{
					columnKey: "createUserName",
					label: "创建人",
					prop: "createUserName",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "340px",
					align: "center"
				}
			]
		}
	});

	// 下载文件
	const handleDownload = () => {};

	// 指派按钮
	const handleAssign = (row: any) => {
		createModelAsync({ title: "指派", showNext: false, closeOnClickModal: false,closeOnPressEscape: false }, {}, <DesignateTask row={row} />).then(() => {
			pageApi.pageList();
		});
	};

	// 跟踪进度
	const handleProgress = (row: any) => {
		console.log("777777777777777777row", row);
		createDrawerAsync(
			{ title: "跟踪进度", showNext: false, width: '800px', showConfirm: false },
			{},
			<TrackProgress id={row.id} />
		).then(() => {
			pageApi.pageList();
		});
	};

	// 终止
	const handleAbort = (row: any) => {
		ElMessageBox.alert("任务终止后，被指派人不再处理相关任务，状态置为已终止，请谨慎操作！", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					abortTask(row.id).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	};

	// 评分
	const handleRate = (row: any) => {
		createModelAsync({ title: "评分", showNext: false, closeOnClickModal: false,closeOnPressEscape: false }, {}, <AddScore row={row} id={row.id} />).then(() => {
			pageApi.pageList();
		});
	};

	// 打回
	const handleReject = (row: any) => {
		createModelAsync({ title: "任务打回", showNext: false, closeOnClickModal: false,closeOnPressEscape: false }, {}, <AddReject id={row.id} />).then(() => {
			pageApi.pageList();
		});
	};

	// 编辑按钮
	const handleEdit = (row: any) => {
		createDrawerAsync({ title: "编辑", showNext: false, width: '1000px', closeOnClickModal: false,closeOnPressEscape: false }, {}, <AddTask id={row.id} />).then(() => {
			pageApi.pageList();
		});
	};

	// 删除按钮
	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					delEntityById(row.id).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	};

	// 跳转到详情
	const handleJumpDetail = (row: any) => {
		// appInstance.router.push({
		// 	path: "/task/task-detail",
		// 	query: {
		// 		id: row.id,
		// 		type: 4
		// 	}
		// });
		createDrawerAsync({ title: row.title, width: '1100px', showNext: false, showConfirm: false }, {}, <DetailPage type="4" id={row.id} />);
	};
</script>
<style lang="scss" scoped>
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
	.degree {
	display: inline-block;
	width: 88px;
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
