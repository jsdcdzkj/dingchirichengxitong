<template>
	<BasePage v-bind="page">
		<!-- "status": "", //状态 0启用1禁用 -->
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'status'">
				<ElTag :type="row.status == 0 ? 'success' : 'info'">
					{{ row.status == 0 ? "启用" : "禁用" }}
				</ElTag>
			</template>
		</template>
		<!-- <template #pageHeader></template> -->
		<template #pageSearch></template>
		<template #pageTitle>用户列表</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus, Switch } from "@element-plus/icons-vue";
	import { getList, delUser, resetPass, syncAccountInfo } from "@/api/setting/user";
	import { ElMessageBox, ElMessage, type Action } from "element-plus";

	const formConfig: FormConfig = {
		span: 6,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: true,
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "用户名称",
				prop: "username",
				attrs: {
					placeholder: "请输入用户名称",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			},
			{
				type: "ElInput",
				value: "",
				label: "手机号",
				prop: "phone",
				attrs: {
					placeholder: "请输入手机号",
					clearable: true,
					onKeyup: (e: KeyboardEvent) => {
						if (e.key === "Enter") {
							pageApi.pageList();
						}
					}
				}
			}
		]
	};

	const { page, pageApi } = usePage({
		formConfig,
		title: "",
		listApi: getList,
		toolbar: [
			{
				label: "新增",
				type: "success",
				size: "default",
				icon: markRaw(Plus),
				onClick: async () => {
					createModelAsync(
						{
							title: "新增",
							showNext: false,
							width: "500px",
							closeOnClickModal: false,
							closeOnPressEscape: false
						},
						{},
						<Detail />
					).then((res) => {
						pageApi.pageList();
					});
				}
			},
			// 同步
			{
				label: "同步",
				type: "primary",
				size: "default",
				icon: markRaw(Switch),
				onClick: async () => {
					// ElMessageBox.alert("是否删除该条数据", "提示", {
					// 		confirmButtonText: "确认",
					// 		callback: (action: Action) => {
					// 			if (action == "confirm") {
					// 				delUser(row.id).then(() => {
					// 					pageApi.pageList();
					// 				});
					// 			}
					// 		}
					// 	});

					ElMessageBox.confirm("确定同步用户数据？", "提示", {
						type: "warning"
					}).then(async () => {
						await syncAccountInfo({}).then(() => {
							ElMessage.success("同步完成");
						});
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
				// {
				// 	columnKey: "username",
				// 	label: "登录账号",
				// 	prop: "username",
				// 	align: "center"
				// },
				{
					columnKey: "username",
					label: "用户名称",
					prop: "username",
					align: "center"
				},
				{
					columnKey: "realName",
					label: "真实姓名",
					prop: "realName",
					align: "center"
				},
				{
					columnKey: "workId",
					label: "工号",
					prop: "workId",
					align: "center"
				},
				{
					columnKey: "phone",
					label: "手机号",
					prop: "phone",
					align: "center"
				},
				{
					columnKey: "status",
					label: "状态",
					prop: "status",
					align: "center"
				},
				{
					columnKey: "actions",
					label: "操作",
					prop: "label",
					width: "240px",
					align: "center"
				}
			],
			actions: [
				{
					text: "编辑",
					type: "primary",
					size: "small",
					plain: true,
					onClick: async ({ row }) => {
						createModelAsync(
							{
								title: "编辑",
								showNext: false,
								width: "500px",
								closeOnClickModal: false,
								closeOnPressEscape: false
							},
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
					plain: true,
					danger: true,
					type: "danger",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									delUser(row.id).then(() => {
										pageApi.pageList();
									});
								}
							}
						});
					}
				},
				{
					text: "重置密码",
					size: "small",
					plain: true,
					type: "warning",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否重置该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									resetPass(row.id).then(() => {
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
</script>
