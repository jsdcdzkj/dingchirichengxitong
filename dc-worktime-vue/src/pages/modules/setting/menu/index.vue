<template>
	<BasePage v-bind="page">
		<template #pageTableCell="{ column, row }">
			<template v-if="column.columnKey === 'isShow'">
				<ElTag :type="row.isShow === 1 ? 'success' : 'info'">
					{{ row.isShow === 1 ? "显示" : "隐藏" }}
				</ElTag>
			</template>
			<template v-if="column.columnKey == 'menuType'">
				<span>
					{{ row.menuType === 1 ? "菜单" : "按钮" }}
				</span>
			</template>
		</template>
		<!-- <template #pageHeader></template> -->
		<template #pageSearch></template>
		<template #pageTitle>菜单列表</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus, Promotion } from "@element-plus/icons-vue";
	import { getMenuTree, delMenu } from "@/api/setting/menu";
	import { ElMessageBox } from "element-plus";

	const formConfig: FormConfig = {
		span: 6,
		labelWidth: 100,
		labelPosition: "right",
		expandSpan: 4,
		notMargn: true,
		onSubmit: (e) => {
			e.preventDefault();
		},
		formItems: [
			{
				type: "ElInput",
				value: "",
				label: "菜单名称",
				prop: "title",
				attrs: {
					placeholder: "请输入菜单名称",
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
		listApi: async (data) => {
			return await getMenuTree({ ...data });
		},
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
			}
		],

		tableConfig: {
			dataSource: [{ label: 1 }],

			attrs: {
				fit: true,
				border: true,
				height: "100%",
				// rowClassName: ({ rowIndex }) => {
				// 	if (rowIndex % 2 != 0) {
				// 		return "warning-row";
				// 	} else {
				// 		return "success-row";
				// 	}
				// },
				rowKey: "id",
				treeProps: { children: "children", hasChildren: "hasChildren" }
			},
			columns: [
				{
					columnKey: "title",
					label: "菜单名称",
					prop: "title",
					align: "center"
				},
				{
					columnKey: "routerName",
					label: "路由名称",
					prop: "routerName",
					align: "center"
				},
				{
					columnKey: "routerUrl",
					label: "路由链接",
					prop: "routerUrl",
					align: "center"
				},
				{
					columnKey: "vueUrl",
					label: "vue文件路径",
					prop: "vueUrl",
					align: "center"
				},
				{
					columnKey: "menuType",
					label: "菜单类型",
					prop: "menuType",
					align: "center",
					width: "90px"
				},
				{
					columnKey: "icon",
					label: "图标",
					prop: "icon",
					align: "center",
					width: "180px"
				},
				{
					columnKey: "isShow",
					label: "是否显示",
					prop: "isShow",
					align: "center",
					width: "90px"
				},
				{
					columnKey: "sort",
					label: "排序",
					prop: "sort",
					align: "center",
					width: "60px"
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
					danger: true,
					plain: true,
					type: "danger",
					onClick: ({ row }) => {
						ElMessageBox.alert("是否删除该条数据", "提示", {
							confirmButtonText: "确认",
							callback: (action: Action) => {
								if (action == "confirm") {
									delMenu({ id: row.id }).then(() => {
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
