<template>
	<BasePage v-bind="page" :asideLeftWidth="300">
		<template #pageLeft>
			<ElCard shadow="never" style="border: none">
				<template #header>
					<ElInput class="project-search" @keyup.enter="handleBlur" v-model="projectName" clearable @blur="handleBlur" placeholder="请输入项目名称" />
				</template>
				
				<div class="project-wrap" ref="projectEl" @scroll="handleScroll">
					<div class="contentEl" ref="contentEl">
						<div
							class="project-item"
							:class="{ active: activeIndex == index }"
							@click="handleProject(index, item.id)"
							v-for="(item, index) in projectList"
							:key="index"
						>
							<img
								src="@/assets/task/doc-icon.png"
								style="width: 20px; height: 20px; margin-right: 8px"
								alt=""
							/>
							<div class="textOver">{{ item.projectName }}</div>
						</div>
					</div>
				</div>
			</ElCard>
		</template>
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'name'">
				<ElLink type="primary" @click="handleJumpDetail(row)">
					{{ row.name }}
				</ElLink>
			</template>
			<template v-if="column.columnKey === 'end_time'">
				<div>{{ row.end_time?.substring(0, 10) }}</div>
			</template>
			<template v-if="column.columnKey === 'description'">
				<div class="desc-img" v-html="row.description"></div>
			</template>
		</template>
		<template #tableActions="scope">
			<ElButton v-if="scope.row.status == 1" type="primary" plain size="small" @click="handleAssign(scope.row)"
				>指派</ElButton
			>
			<ElButton v-if="scope.row.status == 1 || scope.row.status == 2" type="primary" plain size="small" @click="handleEdit(scope.row)">编辑</ElButton>
			<ElButton v-if="scope.row.status == 1 || scope.row.status == 2" type="danger" plain size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import AddOperationTask from "./dialog/add-operation-task.vue";
	import DesignateTask from "./dialog/designate-task.vue";
	import { markRaw, onMounted, reactive } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { ElMessageBox, type Action } from "element-plus";
	import { getPageList, delEntityById, selectPageList } from "@/api/task/operation-maintenance";
	import { appInstance } from "@/main";
	import { ref } from "vue";
	import DetailPage from "../task-detail/index.vue";

	const projectName = ref("")
	const projectList = ref([])
	const queryParam = reactive({
		pageNo:1,
		pageSize: 20,
	})
	const pages = ref(1)
	
	// 监听滚动
	const projectEl = ref()
	const contentEl = ref()
	// 判断元素是否滑动到了页面底部
	const handleScroll = () => {
      const container = projectEl.value;
      if (container) {
        const { scrollTop, clientHeight, scrollHeight } = container;
        if (scrollTop + clientHeight >= scrollHeight) {
          console.log('已滚动到底部');
		  queryParam.pageNo += 1
		  getProjectList()
          // 在这里添加滚动到底部时要执行的代码
        }
      }
    }

	const handleBlur = () => {
		queryParam.pageNo = 1
		getProjectList()
	}
	const project_id = ref()
	const getProjectList = () => {
		if( pages.value !== 0 && queryParam.pageNo > pages.value) return
		selectPageList({...queryParam,projectName: projectName.value }).then(res => {
			if (queryParam.pageNo == 1) {
				projectList.value = res.records
				// project_id.value = projectList.value[0]?.id
				// activeIndex.value = 0

			} else {
				projectList.value = projectList.value.concat(res.records)
			}
			
			pages.value = res.pages

			if(contentEl.value.clientHeight <= projectEl.value.clientHeight && queryParam.pageNo <= pages.value) {
				queryParam.pageNo += 1
				getProjectList()
			}

		})
	}
	getProjectList()
	const formConfig: FormConfig = {
		span: 6,
		expandSpan: 4,
		labelWidth: 70,
		labelPosition: "right",
		notMargn: false,
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "name",
				label: "任务名称",
				attrs: {
					placeholder: "请输入任务名称",
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
			// 	prop: "description",
			// 	label: "任务描述",
			// 	attrs: {
			// 		placeholder: "请输入任务描述",
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
							label: "已结束"
						},
						{
							value: 5,
							label: "已挂起"
						},
						{
							value: 6,
							label: "已暂停"
						}
					]
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time1",
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
				prop: "time2",
				label: "截止时间",
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
		title: "运维任务列表",
		listApi: async (data) => {
			const [startStartTime, startEndTime] = data.time1 || [];
			const [endStartTime, endEndTime] = data.time2 || [];
			let res = {}
			// if (!project_id.value) {
			// 	res = await selectPageList({pageNo:1,pageSize: 20})
			// }
			const params = {
				...data,
				startStartTime,
				startEndTime,
				endStartTime,
				endEndTime,
				project_id: project_id.value
			};
			delete params.time1;
			delete params.time2;
			return await getPageList(params);
		},
		toolbar: [
			{
				label: "创建运维任务",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createDrawerAsync(
						{ title: "创建运维任务", width: "800px", showNext: false, closeOnClickModal: false,closeOnPressEscape: false },
						{},
						<AddOperationTask projectId={project_id.value} />
					).then(() => {
						pageApi.pageList();
					});
				}
			}
			// {
			// 	label: "任务指派",
			// 	type: "primary",
			// 	size: "default",
			// 	// icon: markRaw(Plus),
			// 	onClick: async () => {
			// 		createModelAsync(
			// 			{ title: "任务指派", width: '1100px', showNext: false },
			// 			{},
			// 			<DesignateTask />
			// 		).then(() => {
			// 			pageApi.pageList();
			// 		});
			// 	}
			// },
			// {
			// 	label: "删除",
			// 	type: "danger",
			// 	size: "default",
			// 	// icon: markRaw(Plus),
			// 	onClick: async (row) => {
			// 		ElMessageBox.alert("是否删除该条数据", "提示", {
			// 				confirmButtonText: "确认",
			// 				callback: (action: Action) => {
			// 					if (action == "confirm") {
			// 						delEntityById(row.id).then(() => {
			// 							pageApi.pageList();
			// 						});
			// 					}
			// 				}
			// 			});
			// 	}
			// }
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
				// 	width: "60px",
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
					columnKey: "name",
					label: "任务名称",
					prop: "name",
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
				{
					columnKey: "assigned_to_user_name",
					label: "指派至",
					prop: "assigned_to_user_name",
					align: "center"
				},
				{
					columnKey: "start_time",
					label: "开始时间",
					width: '180px',
					prop: "start_time",
					align: "center"
				},
				{
					columnKey: "end_time",
					label: "截止时间",
					prop: "end_time",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "statusName",
					label: "状态",
					prop: "statusName",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "200px",
					align: "center"
				}
			],
			actions: [
				// {
				// 	text: "指派",
				// 	size: "small",
				// 	danger: true,
				// 	type: "primary",
				// 	onClick: ({ row }) => {
				// 		console.log(row)
				// 		createModelAsync(
				// 			{ title: "指派", showNext: false },
				// 			{},
				// 			<DesignateTask id={row.id} />
				// 		).then(() => {
				// 			pageApi.pageList();
				// 		});
				// 	}
				// },
				// {
				// 	text: "编辑",
				// 	type: "primary",
				// 	size: "small",
				// 	plain: false,
				// 	onClick: async ({ row }) => {
				// 		createModelAsync(
				// 			{ title: "编辑", width: '1200px', showNext: false },
				// 			{},
				// 			<AddOperationTask id={row.id} />
				// 		).then(() => {
				// 			pageApi.pageList();
				// 		});
				// 	}
				// },
				// {
				// 	text: "删除",
				// 	size: "small",
				// 	danger: true,
				// 	type: "danger",
				// 	onClick: ({ row }) => {
				// 		ElMessageBox.alert("是否删除该条数据", "提示", {
				// 			confirmButtonText: "确认",
				// 			callback: (action: Action) => {
				// 				if (action == "confirm") {
				// 					delEntityById(row.id).then(() => {
				// 						pageApi.pageList();
				// 					});
				// 				}
				// 			}
				// 		});
				// 	}
				// }
			]
		}
	});

	// 指派按钮
	const handleAssign = (row: any) => {
		createModelAsync({ title: "指派", showNext: false, closeOnClickModal: false,closeOnPressEscape: false }, {}, <DesignateTask id={row.id} projectId={project_id.value} row={row}  />).then(() => {
			pageApi.pageList();
		});
	};

	// 编辑按钮
	const handleEdit = (row: any) => {
		createDrawerAsync(
			{ title: "编辑", width: '800px', showNext: false, closeOnClickModal: false,closeOnPressEscape: false },
			{},
			<AddOperationTask id={row.id} projectId={project_id.value} />
		).then(() => {
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
		// 		type: 3
		// 	}
		// });
		createDrawerAsync({ title: row.name, width: '1100px', showNext: false, showConfirm: false }, {}, <DetailPage type="3" id={row.id} />);
	};

	const activeIndex = ref();
	const handleProject = (index, id) => {
		activeIndex.value = index;
		project_id.value = id;
		console.log("34444444444444444444", page, activeIndex.value);
		pageApi.pageList();
	};
</script>
<style lang="scss" scoped>
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
	::v-deep {
		.el-card__header {
			text-align: left;
		}
	}
	.project-wrap {
		height: calc(100vh - 204px);
		overflow: auto;
		.project-item {
			display: flex;
			align-items: center;
			height: 56px;
			background: rgba(255, 255, 255, 0);
			border-radius: 4px 4px 4px 4px;
			padding-left: 16px;
			cursor: pointer;

			.textOver {
				white-space: nowrap;
				overflow:hidden;
				text-overflow: ellipsis;
			}
		}
		.active {
			background: #e8f3ff;
		}
	}

	::v-deep {
		.project-search {
			.el-input__wrapper {
				border: 1px solid rgba(0,0,0,0.15);
			}
		}
		
	}
</style>
