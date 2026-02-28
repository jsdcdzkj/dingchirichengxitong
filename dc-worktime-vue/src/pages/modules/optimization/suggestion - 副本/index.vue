<template>
	<BasePage v-bind="page" :asideLeftWidth="320">
		<template #pageLeft>
			<div class="flex-1 flex flex-col w-100% p-10px">
				<div class="flex p-b-10px flex-items-center flex-justify-between m-t-8px p-l-10px p-r-10px">
					<div class="font-size-18px font-b font-bold">工作组类型</div>
				</div>
			</div>
			<div class="el-divider el-divider--horizontal m-t-0px m-b-0px"></div>
			<!-- 工作组列表 -->
			<div class="team-list-con">
				<div
					v-for="(group, index) in workGroups"
					:key="index"
					:class="{
						'team-item': true,
						active: selectedGroupIndex === index
					}"
					@click="selectGroup(index)"
				>
					<div class="team-icon"></div>
					<span class="team-name">{{ group.dictLabel }}</span>
					<div class="team-bot">
						<div class="team-btn" @click="editGroup(index)">
							<ElIcon>
								<EditPen />
							</ElIcon>

							<span>编辑</span>
						</div>
						<div class="team-btn" @click="deleteGroup(index)">
							<ElIcon>
								<Delete />
							</ElIcon>

							<span>删除</span>
						</div>
					</div>
				</div>
				<!-- 新增工作组 -->
				<div class="add-team" @click="openAddGroupDialog">
					<ElIcon>
						<Plus />
					</ElIcon>
					<span class="font-size-24px">新建组类型</span>
				</div>
			</div>
		</template>
		<template #pageHeader></template>
		<template #pageSearch></template>
		<template #pageTitle>{{ groupName ? groupName : "工作组列表" }}</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { ref, onMounted, computed } from "vue";
	import { createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import TeamName from "./dialog/team-name.vue";
	import { markRaw } from "vue";
	import { Plus, EditPen, Delete } from "@element-plus/icons-vue";
	import { selectPage, delData } from "@/api/setting/team";
	import { selectDictList, deleteDict } from "@/api/setting/dictionary";
	import { ElIcon, ElMessageBox, type Action } from "element-plus";

	const formConfig: FormConfig = {
		span: 6,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 6,
		notMargn: true,
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "组名称",
				prop: "name",
				attrs: {
					placeholder: "请输入"
				}
			},
			{
				type: "ElDatePicker",
				value: "",
				prop: "time",
				label: "创建时间",
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
		listApi: async (data) => {
			const [query_create_time_start, query_create_time_end] = data.time || [];
			const params = {
				...data
			};
			delete params.time;
			return await selectPage({
				...params,
				query_create_time_start,
				query_create_time_end
			});
		},
		toolbar: [
			{
				label: "新增",
				type: "primary",
				size: "default",
				disabled: computed(() => workGroups.value.length === 0),
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{ title: "新增", showNext: false, width: "500px", closeOnClickModal: false,closeOnPressEscape: false },
						{},
						<Detail groupName={groupName.value} groupType={groupType.value} />
					).then((res) => {
						pageApi.pageList();
					});
				}
			}
		],
		pagination: {
			currentPage: 1,
			pageSize: 10,
			total: 15,
			background: true,
			layout: "prev, pager, next, jumper, ->, total",
			onChange(page: number, pageSize: number) {
				console.log(page, pageSize);
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
					label: "组名称",
					prop: "name",
					align: "center"
				},

				{
					columnKey: "leader_name",
					label: "组长",
					prop: "leader_name",
					align: "center"
				},
				{
					columnKey: "sysGroupUserList",
					label: "组员",
					prop: "sysGroupUserList",
					align: "center"
				},
				{
					columnKey: "create_time",
					label: "创建时间",
					prop: "create_time",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "150px",
					align: "center"
				}
			],
			actions: [
				{
					text: "编辑",
					type: "primary",
					size: "small",
					plain: false,
					onClick: async ({ row }) => {
						createModelAsync(
							{ title: "编辑", showNext: false, width: "500px", closeOnClickModal: false,closeOnPressEscape: false },
							{},
							<Detail rowInfo={row} />
						).then((res) => {
							pageApi.pageList();
						});
					}
				},
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
									delData(row.id).then(() => {
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

	// groupName
	const groupName = ref("工作组类型");

	// 工作组列表
	const workGroups = ref([
		// { name: "工作组1" },
		// { name: "工作组2" }
		// 可以添加更多初始工作组
	]);

	const groupType = ref("");
	const selectedGroupIndex = ref(-1); // 初始化选中的组索引为 -1

	onMounted(async () => {
		const data = await selectDictList({ dictSign: "sysGroup" });
		workGroups.value = data;
		// 判断是否有数据 如果有数据 则将groupName设置为第一个数据的dictLabel 并取其id赋值给groupType
		if (data.length > 0) {
			groupName.value = data[0].dictLabel;
			groupType.value = data[0].id;
			// 选择第一个选项
			selectedGroupIndex.value = 0;
		}
	});

	// 打开新增工作组对话框
	const openAddGroupDialog = () => {
		createModelAsync({ title: "新增", showNext: false, width: "500px", closeOnClickModal: false,closeOnPressEscape: false }, {}, <TeamName />).then((res) => {
			// 刷新工作组列表
			selectDictList({ dictSign: "sysGroup" }).then((data) => {
				workGroups.value = data;
			});
		});
	};

	// 编辑工作组
	const editGroup = (index: number) => {
		// 获取当前选中的工作组对象
		const currentGroup = workGroups.value[index];
		createModelAsync(
			{ title: "编辑", showNext: false, width: "500px", closeOnClickModal: false,closeOnPressEscape: false },
			{},
			// 将当前选中的工作组对象作为 props 传递给 TeamName 组件
			<TeamName rowInfo={currentGroup} />
		).then((res) => {
			selectDictList({ dictSign: "sysGroup" }).then((data) => {
				workGroups.value = data;
			});
		});
	};

	// 删除工作组
	const deleteGroup = (index: number) => {
		// 从 workGroups 中获取当前要删除的组的信息
		const currentGroup = workGroups.value[index];
		ElMessageBox.confirm("是否删除该工作组类型", "提示", {
			confirmButtonText: "确认",
			cancelButtonText: "取消",
			type: "warning"
		})
			.then(() => {
				// 调用删除接口
				return deleteDict(currentGroup.id);
			})
			.then(() => {
				// 删除成功后，重新获取工作组列表
				return selectDictList({ dictSign: "sysGroup" });
			})
			.then((data) => {
				// 更新本地的工作组列表
				workGroups.value = data;
			})
			.catch((error) => {
				// 处理删除过程中出现的错误
				console.error("删除工作组失败:", error);
			});
	};
	// 选择组
	const selectGroup = (index: number) => {
		selectedGroupIndex.value = index;
		groupName.value = workGroups.value[index].dictLabel;
		groupType.value = workGroups.value[index].id;
	};
</script>
<style scoped lang="scss">
	.team-list-con {
		padding: 24px 32px;
		height: calc(100% - 68px);
		box-sizing: border-box;
		overflow-y: auto;
		.team-item {
			display: flex;
			align-items: center;
			flex-direction: column;
			margin-bottom: 16px;
			background: #ffffff;
			box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			&.active {
				background: linear-gradient(180deg, #ffffff 0%, #eff6ff 100%);
				box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(73, 158, 255, 0.52);
			}
			&:hover {
				background: linear-gradient(180deg, #ffffff 0%, #eff6ff 100%);
				box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(73, 158, 255, 0.52);
			}
			.team-icon {
				margin: 16px 0 10px 0;
				width: 98px;
				height: 56px;
				background: url("@/assets/team.png") no-repeat center;
				background-size: contain;
			}
			.team-name {
				margin-bottom: 12px;
				font-weight: bold;
				font-size: 14px;
				color: #333;
				line-height: 22px;
				text-align: left;
			}
			.team-bot {
				display: flex;
				width: 100%;
				border-top: 1px solid rgba(0, 0, 0, 0.06);
				.team-btn {
					flex: 1;
					display: flex;
					align-items: center;
					justify-content: center;
					height: 48px;
					line-height: 48px;
					text-align: center;
					font-size: 14px;
					color: #666;
					cursor: pointer;
					&:first-child {
						border-right: 1px solid rgba(0, 0, 0, 0.06);
					}
					span {
						margin-left: 4px;
					}
				}
			}
		}
		.add-team {
			padding: 30px 16px 16px;
			display: flex;
			align-items: center;
			flex-direction: column;
			margin-bottom: 16px;
			background: #ffffff;
			box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			font-size: 20px;
			color: #666;
			font-weight: bold;
			cursor: pointer;
			box-sizing: border-box;
			&:hover {
				background: linear-gradient(180deg, #ffffff 0%, #eff6ff 100%);
				box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(73, 158, 255, 0.52);
			}
			span {
				margin-top: 28px;
				font-size: 14px;
				color: #666;
				line-height: 22px;
			}
		}
	}
</style>
