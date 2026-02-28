<template>
	<BasePage v-bind="page">
		<template #pageTitle>
			<div class="flex flex-items-center">
				<ElRadioGroup v-model="meetinTime" @change="handleChange">
					<ElRadioButton label="今天" value="1" />
					<ElRadioButton label="本周" value="2" />
					<ElRadioButton label="下周" value="3" />
					<ElRadioButton label="自定义会议时间" value="4" />
				</ElRadioGroup>
				<div style="margin-left: 16px;width: 220px">
					<ElDatePicker
						@change="onChangeTime"
						v-if="meetinTime == 4"
						value-format="YYYY-MM-DD"
						format="YYYY-MM-DD"
						v-model="time"
						type="daterange"
						start-placeholder="开始时间"
						end-placeholder="结束时间"
					/>
				</div>
				
			</div>
		</template>
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'title'">
				<ElLink type="primary" @click="handleJumpDetail(row)">
					{{ row.title }}
				</ElLink>
			</template>
			<template v-if="column.columnKey === 'statusName'">
				<ElTag disable-transitions type="warning" v-if="row.status == 0"> {{row.statusName}} </ElTag>
				<ElTag disable-transitions type="primary" v-if="row.status == 1"> {{row.statusName}} </ElTag>
				<ElTag disable-transitions type="success" v-if="row.status == 2"> {{row.statusName}} </ElTag>
				<ElTag disable-transitions type="danger" v-if="row.status == 3"> {{row.statusName}} </ElTag>
				<ElTag disable-transitions type="info" v-if="row.status == 4"> {{row.statusName}} </ElTag>
			</template>
			<template v-if="column.columnKey === 'planing_hours'">
				{{ (row.planing_hours === 0 || row.planing_hours) ? row.planing_hours + '分' : '--' }}
			</template>
			
		</template>
		<template #tableActions="scope">
			<ElButton type="primary" plain size="small" @click="handleEdit(scope.row)">编辑</ElButton>
			<ElButton type="danger" plain size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createDrawerAsync, createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import AddMeetingTask from "./dialog/add-meeting-task.vue";
	import Detail from "./dialog/detail.vue";
	import { markRaw, onMounted, reactive } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { ElMessageBox, type Action } from "element-plus";
	import { getPageList, delEntityById } from "@/api/task/meeting-task";
	import { appInstance } from "@/main";
	import { ref } from "vue";
	import DetailPage from "../task-detail/index.vue";
	import { selectPageList } from "@/api/project/list";
	import { getUserList } from "@/api/common/login";
	import dayjs from "dayjs";

	const projectName = ref("")
	const projectList = ref([])
	const queryParam = reactive({
		pageNo:1,
		pageSize: 20,
	})

	const meetinTime = ref('1')
	const time = ref('')
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
				type: "ElSelect",
				value: "",
				prop: "userIdList",
				label: "参会人员",
				attrs: {
					placeholder: "请选择参会人员",
					clearable: true,
					filterable: true,
					multiple:true,
				},
				select: {
					type: "ElOption",
					label: "realName",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "createUserIdList",
				label: "创建人",
				attrs: {
					placeholder: "请选择创建人",
					clearable: true,
					filterable: true,
					multiple:true,
				},
				select: {
					type: "ElOption",
					label: "realName",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "statusList",
				label: "状态",
				attrs: {
					placeholder: "请选择状态",
					clearable: true,
					multiple:true,
				},
				select: {
					type: "ElOption",
					label: "label",
					value: "value",
					list: [
						{
							value: 0,
							label: "待开始"
						},
						{
							value: 1,
							label: "进行中"
						},
						{
							value: 2,
							label: "已完成"
						},
						{
							value: 3,
							label: "已挂起"
						},
						{
							value: 4,
							label: "已暂停"
						}
					]
				}
			},
			{
				type: "ElSelect",
				value: "",
				prop: "project_id",
				label: "关联项目",
				attrs: {
					placeholder: "请选择关联项目",
					clearable: true
				},
				select: {
					type: "ElOption",
					label: "projectName",
					value: "id",
					listApi: async () => {
						const data = await selectPageList({
							pageNo: 1,
							pageSize: 100
						});
						return data.records;
					},
					list: []
				}
			},
		]
	};
	
	const { page, pageApi } = usePage({
		formConfig,
		title: "会议任务列表",
		listApi: async (data) => {
			const date = dayjs()
			let start_time = date.format('YYYY-MM-DD')
			let end_time = date.format('YYYY-MM-DD')
			if(meetinTime.value == 2) {
				start_time = date.startOf('week').format('YYYY-MM-DD')
				end_time = date.endOf('week').format('YYYY-MM-DD')
			} else if(meetinTime.value == 3){
				start_time = date.add(7, 'day').startOf('week').format('YYYY-MM-DD')
				end_time = date.add(7, 'day').endOf('week').format('YYYY-MM-DD')
			} else if(meetinTime.value == 4) {
				start_time = time.value?.[0] ? time.value?.[0] + ' 00:00:00' : ''
				end_time = time.value?.[1] ? time.value?.[1] + ' 23:59:59' : ''
			}
			const params = {
				...data,
				userIdList: data.userIdList ? data.userIdList : [],
				createUserIdList: data.createUserIdList ? data.createUserIdList : [],
				statusList: data.statusList ? data.statusList : [],
				"pageNo": 1,
    			"pageSize": 20,
				start_time,
				end_time,
			};
			return await getPageList(params);
		},
		toolbar: [
			{
				label: "新建会议任务",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{ title: "创建会议任务", width: "800px", showNext: false, closeOnClickModal: false,closeOnPressEscape: false },
						{},
						<AddMeetingTask />
					).then(() => {
						pageApi.pageList();
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
				// {
				// 	columnKey: "index",
				// 	label: "序号",
				// 	prop: "label",
				// 	width: "80px",
				// 	align: "center"
				// },
				{
					columnKey: "title",
					label: "任务标题",
					prop: "title",
					align: "center",
					showOverflowTooltip: true,
					minWidth: '180px',
					showOverflowTooltip: true,
				},
				{
					columnKey: "project_name",
					label: "关联项目名称",
					prop: "project_name",
					align: "center",
					minWidth: '180px',
					showOverflowTooltip: true,
				},
				{
					columnKey: "plan_name",
					label: "关联项目计划/里程碑",
					prop: "plan_name",
					align: "center",
					minWidth: '180px',
					showOverflowTooltip: true,
				},
				{
					columnKey: "meet_time",
					label: "会议时间",
					width: '180px',
					prop: "meet_time",
					align: "center"
				},
				{
					columnKey: "planing_hours",
					label: "会议时长",
					prop: "planing_hours",
					align: "center",
					width: '120px'
				},
				{
					columnKey: "statusName",
					label: "状态",
					prop: "statusName",
					align: "center",
					width: '100',
				},
				{
					columnKey: "userNames",
					label: "参会人",
					prop: "userNames",
					align: "center",
					width: '180px',
					showOverflowTooltip: true,
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
					width: "200px",
					align: "center"
				}
			],
		}
	});

	// 编辑按钮
	const handleEdit = (row: any) => {
		createModelAsync(
			{ title: "编辑", width: '800px', showNext: false, closeOnClickModal: false,closeOnPressEscape: false },
			{},
			<AddMeetingTask id={row.id} />
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
	console.log('555555555555555pageApi', pageApi)

	// 改变日期选择
	const handleChange = (val) => {
		if (val !=4) {
			pageApi.pageList()
			time.value = ''
		}
		meetinTime.value = val
	}

	// 日期改变
	const onChangeTime = (val) => {
		if(val) {
			pageApi.pageList()
		}
		
	}
	// 跳转到详情
	const handleJumpDetail = (row: any) => {
		createModelAsync({ title: row.title, width: '1100px', showNext: false, showConfirm: false }, {}, <Detail id={row.id} />);
	};
</script>
<style lang="scss" scoped>
	:deep(.el-popper) {
		max-width: 500px !important; /* 设置最大宽度 */
		line-height: 1.5 !important; /* 可选：调整行高 */
	}
</style>
