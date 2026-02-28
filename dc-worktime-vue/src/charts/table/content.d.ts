import SizeBoxManger from "../computed";
import { Col } from "../data/col";
import { Legend } from "../data/legend";
import { Row } from "../data/row";
import { Span } from "../data/span";
export type TableHeaerOptions = {
    height: number;
    offsetLeftWidth: number;
};
export default class TableContentRender {
    boxMgr: SizeBoxManger;
    cols: Col[];
    rows: Row[];
    spans: Span[];
    legends: (Legend | null)[];
    constructor(boxMgr: SizeBoxManger);
    render(ctx: CanvasRenderingContext2D, scrollx: number, scrolly: number, maxRow: number, datas: any[]): void;
    private _createRow;
    private createCol;
    renderCol(col: number, colCount: number): Col;
    renderRow(row: number, rowCount: number): Row;
    renderSpan(i: number, j: number, scrollx: number, scrolly: number): Span;
    dynamicCalcSize(): void;
}
