import SizeBoxManger from "../computed";
export type TableHeaerOptions = {
    height: number;
    offsetLeftWidth: number;
};
export declare enum ClickType {
    Cell = 0,
    SPA = 1,
    HEADE_COL = 2
}
export type SpanClick = (type: ClickType, data: any, event: Event & {
    changeNum?: number;
    index?: number;
}) => void;
export default class FixedLeftTableRender {
    boxMgr: SizeBoxManger;
    container: HTMLDivElement;
    _data: any[];
    bodyContainer: HTMLDivElement | null;
    onClick: SpanClick | null;
    collapseId: any[];
    selectContainer: HTMLDivElement;
    _showLength: number;
    i: number;
    list: never[];
    tooltip: HTMLDivElement;
    isColAll: boolean;
    constructor(boxMgr: SizeBoxManger, container: HTMLDivElement, data: any[]);
    render(): void;
    setOnClick(onClick: SpanClick): void;
    renderHeader(): void;
    renderBody(): void;
    updateBodyCell(bodyContainer: HTMLDivElement): void;
    createTooltip(): HTMLDivElement;
    hiddenTooltip(): void;
    moveTooltip(top: number, left: number, data: any): void;
    renderLegend(index: number, downX: number, downY: number): void;
    loopAppendCell(tree: any, index: number, bodyContainer2: HTMLDivElement): number;
    renderCellAndReturnIndex(tree: any[], bodyContainer2: HTMLDivElement): number;
    createSelect(index: number): HTMLDivElement;
    createCell(index: number, data: any): HTMLDivElement;
    selectCell(index: number, scrollY: number): void;
    hiddenSelect(): void;
    scrollContainer(distance: number): void;
    collectedCollpaseIds(data: any): void;
    createCellSpan(data: any): HTMLDivElement[];
    dynamicCalcSize(): void;
    get showLength(): number;
}
