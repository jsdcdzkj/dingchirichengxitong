import { reactive } from "vue";
type ButtonType =
	| "primary"
	| "success"
	| "warning"
	| "danger"
	| "info"
	| "text";
export type SizeType = "large" | "default" | "small" | "";
export type ToolbarConfig = {
	onClick?: (e: Event) => void;
	icon?: any;
	text?: boolean;
	label: string;
	type?: ButtonType;
	size?: SizeType;
	disabled?: boolean;
	[key: string]: any;
};

export type TableColumnType = {
	type?: "selection" | "index" | "expand" | string;
	index?: number | ((index: number) => number);
	label: string;
	columnKey: string;
	prop: string;
	width?: string | number;
	minWidth?: string | number;
	fixed?: boolean | "left" | "right" | string;
	sortable?: boolean | string;
	sortMethod?: (a: any, b: any) => void;
	className?: string;
	labelClassName?: string;
	[key: string]: any;
};

export type ActionsConfig = {
	onClick?: (e: RowRcordType) => void;
	icon?: string;
	text: string;
	type?: ButtonType;
	danger?: boolean;
	size?: SizeType;
	plain?: boolean;
	link?: boolean;
	[key: string]: any;
};

export type RowRcordType = {
	text: string;
	value: string;
	row: Record<string, any>;
	index: number;
	column: TableColumnType;
};

export type ApiFunc = (data?: any) => any | any[];
export type ApiAsyncFunc = (data?: any) => Promise<any | any[]>;
export type PageProps = {
	pageSize: number;
	currentPage: number;
	pageSizes?: number[];
	total: number;
	showSizeChanger?: boolean;
	layout?: string;
	background?: boolean;
	onChange?: (page: number, pageSize: number) => void;
	onSizeChange?: (page: number) => void;
	onCurrentChange?: (page: number) => void;
};

export interface TableAttrsType {
	height?: string | number;
	maxHeight?: string | number;
	stripe?: boolean;
	border?: boolean;
	size?: SizeType;
	fit?: boolean;
	rowClassName?:
		| string
		| (({ row, rowIndex }: { row: any; rowIndex: number }) => string);
	emptyText?: string;

	[key: string]: any;
}

export interface TableConfig {
	toolbar?: ToolbarConfig[];
	listApi?: (data?: any) => Promise<any>;
	actions?: ActionsConfig[];
	title?: string;
	pagination?: PageProps | undefined;
	columns?: TableColumnType[];
	bordered?: boolean;
	attrs?: TableAttrsType;
	dataSource: any[];
}

export interface TableAllConfig {
	toolbar?: ToolbarConfig[];
	listApi: (data?: any) => Promise<any>;
	actions?: ActionsConfig[];
	title?: string;
	props: {
		pagination?: PageProps | boolean;
		// 表格
		columns?: TableColumnType[];
		rowKey: string;
		dataSource: any[];
		bordered?: boolean;
	};
}

export const useTable = (tableConfig: TableConfig) => {
	const _tableConfig = reactive({
		actions: tableConfig?.actions || [],
		attrs: tableConfig?.attrs || {},
		pagination: tableConfig?.pagination,
		columns: tableConfig?.columns || [],
		dataSource: tableConfig?.dataSource || []
	});

	const methods = {
		setDataSource(data: any[]) {
			_tableConfig.dataSource = data;
		}
	};

	return { tableConfig: _tableConfig, tableApi: methods };
};
