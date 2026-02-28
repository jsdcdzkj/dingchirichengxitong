import { Col } from "../data/col";
import { Row } from "../data/row";
import SizeBoxManger, { BoxSizeType } from "../computed";
export interface TableRenderInterface {
    zoom: (zoom: number) => void;
    jumpToDate: (date: number, zoom?: boolean) => void;
}
export type Options = {
    box: Partial<BoxSizeType>;
};
declare class BaseTableRender {
    cols: Col[];
    rows: Row[];
    _canvas: HTMLCanvasElement;
    _ctx: CanvasRenderingContext2D;
    boxMgr: SizeBoxManger;
    data: any;
    _container: HTMLDivElement;
    constructor(container: HTMLDivElement, data: any[], options?: Options);
    initContainer(container: HTMLDivElement): HTMLCanvasElement;
    resize(): void;
    drawTableBorder(ctx: CanvasRenderingContext2D, x: number, y: number, x2: number, y2: number): void;
    getTableColReact(): number[];
    clearCanvas(): void;
}
export type TableOptions = {
    cellHeight?: number;
    cellWidth?: number;
};
export type IFixedTableCellDate = {
    startDate: string;
    endDate: string;
    type: string | number;
    title: string;
    progress: number;
    children?: IFixedTableCellDate[];
} & Record<string, any>;
export declare class TableRender extends BaseTableRender implements TableRenderInterface {
    private horScroll;
    private verScroll;
    private zoomCtl;
    private headerRender;
    private contentRender;
    private fixedRender;
    private fixedTable;
    private gesture;
    private resieObserver;
    constructor(container: HTMLDivElement, data: IFixedTableCellDate[], options?: Options);
    private horFlingCallback;
    private verFlingCallback;
    private zoomChange;
    private _onGestureCallback;
    private _onCanvasMover;
    private clicInBox;
    render(): void;
    zoom(): void;
    jumpToDate(): void;
    destroy(): void;
}
export {};
