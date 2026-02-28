import { IFixedTableCellDate } from "../table";
export declare const getDate: () => {
    startDate: string;
    endDate: string;
    row: number;
}[];
export declare const getTreeList: (data: any[], list: any[]) => any[];
export declare const coverDataLoop: (data: IFixedTableCellDate[], list: any[], margin: number) => any[];
export declare const coverDataLoopNodeLen: (data: any[]) => number;
export declare const coverData: (data: IFixedTableCellDate[]) => any[];
export declare const getChildrenIds: (data: any, list: any[]) => void;
export declare const getNotChildrenIds: (data: any, list: any[]) => void;
export declare const computedDate: (dataList: any) => any;
