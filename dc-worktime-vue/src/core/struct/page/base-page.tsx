import { defineComponent, ref } from "vue";
import BaseForm from "../form/base-form";
import BaseTable from "../table/base-table";
import { array, number, object, string, bool, nullable } from "vue-types";
import type { FormConfig } from "../form/use-base-form";
import type { PageProps, TableConfig, ToolbarConfig } from "../table/use-table";
import TableHeader from "../table/table-header";
import { ElButton, ElPagination } from "element-plus";
/**
 * pageTitle
 * pageActions
 * pagePagination
 * pageBody
 * pageLeft
 * pageRight
 * pageSearch
 * pageTableCell
 * pageHeader
 * tableActions
 */

export default defineComponent({
	props: {
		form: object<FormConfig>().def(),
		table: object<TableConfig>().def(),
		asideLeftWidth: number().def(0),
		asideRightWidth: number().def(0),
		toolbar: array<ToolbarConfig>().def(),
		pagination: object<PageProps>().def(),
		title: string().def(),
		isSpecialLayout: bool().def(false), // 新增属性
		hasGap: bool().def(true)
	},
	inheritAttrs: false,
	setup(props, { slots }) {
		const excluldColumn = ref<Array<string>>(
			props.table ? props.table.columns!!.map((item) => item.columnKey) : []
		);

		const size = ref<"" | "small" | "default" | "large">("default");
		const handleCommand = (e: "" | "small" | "default" | "large") => {
			size.value = e;
		};

		return () => {
			const renderSlotPageSearch = () => {
				if (props.form) {
					return (
						<div class="base-search">
							<BaseForm {...props.form} v-model:value={props.form.value} />
						</div>
					);
				}

				if (slots.pageSearch) {
					return slots.pageSearch();
				}
				return null;
			};

			const renderToolbar = () => {
				if (props.toolbar) {
					return props.toolbar.map((config) => <ElButton {...config}>{config.label}</ElButton>);
				}
				return null;
			};

			const renderSlotPageLeft = () => {
				if (slots.pageLeft) {
					return (
						<div
							class={`overflow-x-hidden overflow-x-hidden h-100% border-rd-12px ovflow-hidden`}
							style={{
								width: `${props.asideLeftWidth}px`,
								backgroundColor: "var(--el-page-color)"
							}}
						>
							{slots.pageLeft()}
						</div>
					);
				}
				return null;
			};

			const renderSlotPageBodySearch = () => {
				if (slots.pageBodySearch) {
					return <div>{slots.pageBodySearch()}</div>;
				}
				return null;
			};

			const renderSlotPageRight = () => {
				if (slots.pageRight) {
					return (
						<div
							class={`overflow-x-hidden overflow-x-hidden h-100% border-rd-12px ovflow-hidden`}
							style={{
								width: `${props.asideRightWidth}px`,
								backgroundColor: "var(--el-page-color)"
							}}
						>
							{slots.pageRight()}
						</div>
					);
				}
				return null;
			};

			const renderSlotPageHeader = () => {
				if (slots.pageHeader) {
					return slots.pageHeader();
				}
				if (props.table?.actions) {
					return (
						<TableHeader
							title={props.title}
							columns={props.table.columns}
							v-model:value={excluldColumn.value}
							onCommand={handleCommand}
						>
							{{
								default: renderToolbar,
								title: slots.pageTitle
							}}
						</TableHeader>
					);
				}

				return null;
			};

			const renderSlotPagination = () => {
				if (props.pagination) {
					return (
						<ElPagination
							{...props.pagination}
							v-model:currentPage={props.pagination.currentPage}
							v-model:pageSize={props.pagination.pageSize}
						/>
					);
				}

				if (slots.pagePagination) {
					return slots.pagePagination();
				}
				return null;
			};

			const renderSlotPageBody = () => {
				if (props.table) {
					return (
						<BaseTable {...props.table}>
							{{
								bodyCell: (row: any) => (slots.pageTableCell ? slots.pageTableCell(row) : slots.null),
								actions: slots.tableActions
							}}
						</BaseTable>
					);
				}

				if (slots.pageBody) {
					return slots.pageBody();
				}

				return null;
			};
			if (props.isSpecialLayout)
				return (
					<div class="h-100% overflow-hidden flex flex-col gap-12px">
						<div class="w-100%">{renderSlotPageSearch()}</div>
						<div class="h-100% flex gap-12px">
							<div class="custom-table h-100%">{renderSlotPageLeft()}</div>
							<div class="flex-1 custom-table h-100% flex flex-col">
								<div class="flex-1 flex flex-col w-100% p-10px " style="overflow: hidden;">
									{renderSlotPageBody()}
								</div>
								{/* {props.table.pagination ? ( */}
								<div class="flex flex-items-center p-10px flex-justify-center p-t-0">
									{renderSlotPagination()}
								</div>
								{/* ) : null} */}
							</div>
						</div>
					</div>
				);

			return (
				<div class="h-100% flex gap-12px">
					{renderSlotPageLeft()}

					<div
						class={
							props.hasGap
								? "h-100% flex flex-col flex-1 overflow-hidden gap-12px"
								: "h-100% flex flex-col flex-1 overflow-hidden"
						}
					>
						{renderSlotPageSearch()}
						<div class="flex-1 custom-table h-100% flex flex-col w-100%">
							<div class="flex-1 flex flex-col w-100% p-10px " style="overflow: hidden;">
								{renderSlotPageHeader()}
								{renderSlotPageBodySearch()}
								{renderSlotPageBody()}
							</div>
							{/* {props.table.pagination ? ( */}
							<div class="flex flex-items-center p-10px flex-justify-center p-t-0">
								{renderSlotPagination()}
							</div>
							{/* ) : null} */}
						</div>
					</div>

					{renderSlotPageRight()}
				</div>
			);
		};
	}
});
