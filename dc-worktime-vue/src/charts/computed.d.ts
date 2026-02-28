export type BoxSizeType = {
    cellHeight: number;
    cellWidth: number;
    width: number;
    height: number;
    x: number;
    y: number;
    headerHeight: number;
    offsetLeftWidth: number;
    left: number;
    right: number;
};
export default class SizeBoxManger {
    box: BoxSizeType;
    canvas: HTMLCanvasElement;
    cols: number;
    rows: number;
    startCol: number;
    startRow: number;
    _defaultCellWidth: number;
    constructor(canvas: HTMLCanvasElement, options?: Partial<BoxSizeType>);
    onBoxSizeChange(): void;
    setHeaderHeight(height: number): void;
    setClientBox(cellWidth: number, cellHeight: number): void;
    setCellWidth(width: number): void;
    setCellheight(height: number): void;
    calc(scrollX: number, scrollY: number): void;
    resize(scrollX: number, scrollY: number): void;
    private getVisibleCols;
    private getVisibleRows;
    dynamicCalcSize(zoom: number): void;
}
