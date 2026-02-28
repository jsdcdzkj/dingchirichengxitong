<template>
	<BasePage v-bind="page">
		<!-- <template #pageHeader></template> -->
		<template #pageSearch></template>
		<template #pageTitle>字典列表</template>
	</BasePage>
</template>

<script setup lang="tsx">
	import { createModelAsync } from "@/core/dialog";
	import { type FormConfig } from "@/core/struct/form/use-base-form";
	import { usePage } from "@/core/struct/page";
	import BasePage from "@/core/struct/page/base-page";
	import Detail from "./dialog/detail.vue";
	import { markRaw } from "vue";
	import { Plus } from "@element-plus/icons-vue";
	import { getPage, deleteDict } from "@/api/setting/dictionary";
	import { ElMessageBox, type Action } from "element-plus";

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
				label: "字典名称",
				prop: "dictLabel",
				attrs: {
					placeholder: "请输入字典名称",
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
				label: "字典标识",
				prop: "dictSign",
				attrs: {
					placeholder: "请输入字典标识",
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
		listApi: getPage,
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
					columnKey: "dictLabel",
					label: "字典名称",
					prop: "dictLabel",
					align: "center"
				},
				{
					columnKey: "dictValue",
					label: "字典值",
					prop: "dictValue",
					align: "center"
				},
				{
					columnKey: "dictSign",
					label: "字典标识",
					prop: "dictSign",
					align: "center"
				},
				{
					columnKey: "memo",
					label: "描述",
					prop: "memo",
					align: "center"
				},
				{
					columnKey: "sort",
					label: "排序",
					prop: "sort",
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
									deleteDict(row.id).then(() => {
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
