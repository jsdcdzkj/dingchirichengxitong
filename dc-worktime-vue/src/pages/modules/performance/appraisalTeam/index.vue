<template>
	<BasePage v-bind="page" :asideLeftWidth="300">
		<template #pageLeft>
			<ElCard shadow="never" style="border: none" class="group-list">
				<template #header>
					<div class="card-header">
						<span>考核组列表</span>
					</div>
				</template>
				<div
					class="group-item"
					:class="{ active: activeIndex == index }"
					@click="handleChange(item.value,index)"
					v-for="(item, index) in groupList"
					:key="index"
				>
					<div class="textOver">{{ item.label }}</div>
				</div>
			</ElCard>
		</template>
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'name'">
				<!-- <ElLink type="primary" @click="handleJumpDetail(row)"> -->
					{{ row.name }}
				<!-- </ElLink> -->
			</template>
			<template v-if="column.columnKey === 'end_time'">
				<div>{{ row.end_time?.substring(0, 10) }}</div>
			</template>
			<template v-if="column.columnKey === 'description'">
				<div class="desc-img" v-html="row.description"></div>
			</template>
		</template>
		<template #tableActions="scope">
			<!-- <ElButton v-if="scope.row.status == 1" type="primary" plain size="small" @click="handleAssign(scope.row)"
				>指派</ElButton
			>
			<ElButton v-if="scope.row.status == 1 || scope.row.status == 2" type="primary" plain size="small" @click="handleEdit(scope.row)">编辑</ElButton> -->
			<ElButton type="danger" plain size="small" @click="handleDelete(scope.row)">删除</ElButton>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import AddMembers from "./dialog/add-member.vue";
	import { markRaw, onMounted } from "vue";
	import { Plus, CircleCheckFilled, CircleCheck } from "@element-plus/icons-vue";
	import { ElMessageBox, type Action } from "element-plus";
	import { selectPage, del } from "@/api/performance/appraisalTeam";
	import { ref,reactive} from "vue";
    import { appInstance } from "@/main";
    let groupList= reactive([])
	let teamId=ref(0)
    groupList = appInstance.mapDict.examine_team
	teamId.value =groupList[0].value
	const formConfig: FormConfig = {
		span: 6,
		expandSpan: 4,
		labelWidth: 70,
		labelPosition: "right",
		notMargn: false,
		onSubmit: (e) => {
			e.preventDefault();
		},
		formItems: [
			{
				type: "ElInput",
				value: "",
				prop: "name",
				label: "姓名",
				attrs: {
					placeholder: "请输入姓名",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							page.form.onSearch(page.form.value);
						}
					}
				}
			},
			
		]
	};
	const { page, pageApi } = usePage({
		formConfig,
		title: "考核成员",
		listApi: async (data) => {
			let res = {}
			const params = {
			  ...data,
             team_id:teamId.value
			};
            console.log("-------------",data)
			return await selectPage(params);
		},
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{ title: "新增", width: "800px", showNext: false, closeOnClickModal: false,closeOnPressEscape: false },
						{},
						<AddMembers team_id={teamId.value} />
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
				{
					columnKey: "index",
					label: "序号",
					prop: "label",
					width: "80px",
					align: "center"
				},
				{
					columnKey: "name",
					label: "姓名",
					prop: "name",
					align: "center",
					showOverflowTooltip: true,
					minWidth: '180px'
				},
				{
					columnKey: "team_name",
					label: "考核组",
					prop: "team_name",
					align: "center"
				},
				{
					columnKey: "create_time",
					label: "加入时间",
					width: '180px',
					prop: "create_time",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "actions",
					width: "200px",
					align: "center"
				}
			],
			actions: [
				{
					text: "删除",
					size: "small",
					danger: true,
					type: "danger",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									del(row.id).then(() => {
										pageApi.pageList();
									});
								}
							}
						});
					}
				}
			]
		}
	});

	//删除按钮
	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					console.log("9999999999999999",row.id)
					del(row.id).then(() => {
						pageApi.pageList();
					});
				}
			}
		});
	};

	const activeIndex = ref(0);
	const handleChange = (id,index) => {
		activeIndex.value = index;
        teamId.value = id
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
	.group-list {
		height: calc(100vh - 236px);
		overflow-y: auto;
		.group-item {
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 56px;
			height: 56px;
			margin-bottom: 16px;
		}

		.active {
			background: #e8f3ff;
			color: #409eff;
		}
	}

</style>
