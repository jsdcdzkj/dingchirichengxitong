<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey == 'workFinishDegree'">
				<ElProgress :percentage="Number(row.workFinishDegree) || 0" />
			</template>
			<template v-else-if="column.columnKey == 'projectSchedule'">
				<ElProgress :percentage="Number(row.projectSchedule) || 0" />
			</template>
			<template v-else-if="column.columnKey == 'projectStatus'">
				<ElTag disable-transitions type="info" v-if="row.projectStatus == 1"> 未开始 </ElTag>
				<ElTag disable-transitions type="primary" v-else-if="row.projectStatus == 2"> 进行中 </ElTag>
				<ElTag disable-transitions type="success" v-if="row.projectStatus == 3"> 已完成 </ElTag>
			</template>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createDrawer, createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { deleteById, selectPageList } from "@/api/project/list";
	import { ElMessageBox, ElTag, type Action } from "element-plus";
	import ProjectUser from "./dialog/projectUser.vue";
	import { appInstance } from "@/main";
	import { getListAll } from "@/api/setting/user";

	const formConfig: FormConfig = {
		span: 7,
		expandSpan: 3,
		labelWidth: 100,
		labelPosition: "right",
		notMargn: true,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "projectName",
				label: "项目名称",
				attrs: {
					placeholder: "请输入项目名称",
					onKeydown: (event: any) => {
						if (event.keyCode == 13) {
							pageApi.pageList();
						}
					}
				}
			},

			{
				type: "ElSelect",
				value: "",
				prop: "projectManager",
				label: "项目经理",
				attrs: {
					placeholder: "请输入项目经理",
					filterable: true,
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getListAll({});
						return data;
					},
					list: []
				}
			},
			// {
			// 	type: "ElSelect",
			// 	value: "",
			// 	prop: "businessManager",
			// 	label: "业务负责人",
			// 	select: {
			// 		type: "ElOption",
			// 		label: "username",
			// 		value: "id",
			// 		listApi: async () => {
			// 			const data = await getUserList({
			// 				pageNo: 1,
			// 				pageSize: 10
			// 			});
			// 			return data.records;
			// 		},
			// 		list: []
			// 	}
			// },
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "截止时间",
				attrs: {
					type: "daterange",
					rangeSeparator: "至",
					startPlaceholder: "开始时间",
					endPlaceholder: "结束时间",
					valueFormat: "YYYY-MM-DD"
				}
			}
		]
	};

	const { page, pageApi } = usePage({
		formConfig,
		title: "项目列表",
		listApi: async (data) => {
			const [startTime, endTime] = data.time || [];
			const params = {
				...data
			};
			delete params.time;
			return await selectPageList({ ...params, startTime, endTime });
		},
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{
							title: "新增",
							showNext: false,
							width: "800px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<Detail />
					).then(() => {
						pageApi.pageList();
						appInstance.refreshProjectList();
					});
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
			dataSource: [],

			attrs: {
				fit: true,
				border: true,
				rowClassName: ({ rowIndex }) => {
					if (rowIndex % 2 != 0) {
						return "warning-row";
					} else {
						return "success-row";
					}
				}
			},
			columns: [
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					columnKey: "projectName",
					label: "项目名称",
					prop: "projectName",
					align: "center",
					minWidth: "200px"
				},
				{
					columnKey: "projectStatus",
					label: "项目状态",
					prop: "projectStatus",
					align: "center",
					width: "100px"
				},
				{
					columnKey: "projectManagerName",
					label: "项目经理",
					prop: "projectManagerName",
					align: "center",
					width: "90px"
				},
				{
					columnKey: "completionStatus",
					label: "已完成任务/全部任务",
					prop: "completionStatus",
					align: "center",
					minWidth: "160px"
				},
				{
					columnKey: "workFinishDegree",
					label: "任务完成度",
					prop: "workFinishDegree",
					align: "center",
					minWidth: "160px"
				},
				{
					columnKey: "projectSchedule",
					label: "项目进度",
					prop: "projectSchedule",
					align: "center",
					minWidth: "160px"
				},
				// {
				// 	columnKey: "iterationNum",
				// 	label: "迭代数量",
				// 	prop: "iterationNum",
				// 	align: "center"
				// },
				{
					columnKey: "projectStartTime",
					label: "计划开始日期",
					prop: "projectStartTime",
					align: "center",
					width: "120px"
				},
				{
					columnKey: "projectEndTime",
					label: "计划完成日期",
					prop: "projectEndTime",
					align: "center",
					width: "120px"
				},

				{
					columnKey: "usedManHour",
					label: "已使用工时",
					prop: "usedManHour",
					align: "center",
					minWidth: "160px"
				},
				{
					columnKey: "forecastManHour",
					label: "总预估工时",
					prop: "forecastManHour",
					align: "center",
					minWidth: "160px"
				},

				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "250px",
					align: "center",
					fixed: "right"
				}
			],
			actions: [
				{
					text: "删除",
					size: "small",
					danger: true,
					type: "danger",
					plain: true,
					onClick: ({ row }) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									deleteById(row.id).then(() => {
										pageApi.pageList();
										appInstance.refreshProjectList();
									});
								}
							}
						});
					}
				},
				{
					text: "编辑",
					type: "primary",
					size: "small",
					plain: true,

					onClick: async ({ row }) => {
						createDrawerAsync(
							{
								title: "编辑",
								showNext: false,
								width: "800px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<Detail id={row.id} />
						).then(() => {
							pageApi.pageList();
						});
					}
				},
				{
					text: "成员维护",
					size: "small",
					danger: true,
					type: "primary",
					plain: true,
					onClick: ({ row }) => {
						createModelAsync(
							{
								title: "成员维护",
								showNext: false,
								width: "480px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
							{},
							<ProjectUser id={row.id} userIds={row.userIds} />
						).then(() => {
							pageApi.pageList();
						});
					}
				}
			]
		}
	});

	// pageApi.getFormInstance()?.resetFields()
</script>
