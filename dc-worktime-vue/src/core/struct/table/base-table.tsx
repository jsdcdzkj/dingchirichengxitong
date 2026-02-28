import { ElButton, ElDivider, ElPagination, ElTable, ElTableColumn, type TableInstance } from "element-plus";
import { defineComponent, Fragment, h, nextTick, ref, watch } from "vue";
import { array, bool, object, string } from "vue-types";
import type { ActionsConfig, TableColumnType, TableAttrsType, PageProps, ToolbarConfig } from "./use-table";

export default defineComponent({
	props: {
		columns: array<TableColumnType>().def([]),
		actions: array<ActionsConfig>().def([]),
		attrs: object<TableAttrsType>().def({}),
		dataSource: array().def([]),
		pagination: object<PageProps>().def()
	},
	inheritAttrs: false,
	emits: ["refresh"],
	setup(props, { slots }) {
		const table = ref<TableInstance>();
		const excluldColumn = ref<Array<string>>(props.columns.map((item) => item.columnKey));
		const size = ref<"" | "small" | "default" | "large">("default");
		// const handleCommand = (e: "" | "small" | "default" | "large") => {
		// 	size.value = e;
		// };

		return () => {
			const renderActionBtns = (pRow: any) => {
				// 操作
				const len = props.actions.length - 1;
				return h("div", { class: "flex flex-items-center flex-justify-center" }, [
					...props.actions.map((item, index) => {
						const props = {
							...item,
							onClick: async () => {
								await item.onClick?.(pRow);
							},
							text: false
						};
						if (index < len) {
							return (
								<Fragment key={index}>
									<ElButton {...props}>{item.text}</ElButton>
									<ElDivider direction="vertical" />
								</Fragment>
							);
						} else {
							return <ElButton {...props}>{item.text}</ElButton>;
						}
					})
				]);
			};

			const renderBodyActions = (pRow: any) => {
				if (pRow.column.columnKey == "actions") {
					if (slots.actions) {
						return slots.actions(pRow);
					}
					return renderActionBtns(pRow);
				} else if (pRow.column.columnKey == "index") {
					if (props.pagination) {
						return (props.pagination.currentPage - 1) * props.pagination.pageSize + pRow.$index + 1;
					}
					return pRow.$index + 1;
				} else if (slots.bodyCell) {
					return slots.bodyCell(pRow);
				}

				return pRow.row[pRow.column.property];
			};

			const tableColumn = props.columns.map((item) => {
				if (!excluldColumn.value.includes(item.columnKey)) return null;
				return (
					// @ts-ignore
					<ElTableColumn {...item}>
						{{
							default: renderBodyActions
						}}
					</ElTableColumn>
				);
			});

			return (
				<ElTable
					data={props.dataSource}
					class="w-100%"
					ref={table}
					tableLayout="auto"
					size={size.value}
					maxHeight={"calc(100% + 48px)"}
					{...props.attrs}
				>
					{tableColumn}
				</ElTable>
			);
		};
	}
});

/**
 * // <div class="flex-1 custom-table h-100% flex flex-col w-100%">
				// 	<div
				// 		class="flex-1 flex flex-col w-100% p-10px"
				// 		style="overflow: hidden;"
				// 	>
				// 		<TableHeader
				// 			title={props.title}
				// 			columns={props.columns}
				// 			v-model:value={excluldColumn.value}
				// 			onCommand={handleCommand}
				// 		>
				// 			{{
				// 				default: renderToolbar
				// 			}}
				// 		</TableHeader>
						// <ElTable
						// 	data={props.dataSource}
						// 	class="w-100%"
						// 	{...props.attrs}
						// 	ref={table}
						// 	tableLayout="auto"
						// 	size={size.value}
						// 	maxHeight={"calc(100% + 48px)"}
						// >
						// 	{tableColumn}
						// </ElTable>
					 </div>

					{props.pagination ? (
						<div class="flex flex-items-center p-10px flex-justify-center p-t-0">
							<ElPagination
								{...props.pagination}
								v-model:currentPage={
									props.pagination.currentPage
								}
								v-model:pageSize={props.pagination.pageSize}
							/>
						</div>
					) : null}
				</div> 
 */
