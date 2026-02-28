import SizeBoxManger from "../computed";
export type TableHeaerOptions = {
    height: number;
    offsetLeftWidth: number;
};
export default class TableHeaderRender {
    boxMgr: SizeBoxManger;
    constructor(boxMgr: SizeBoxManger);
    render(ctx: CanvasRenderingContext2D, scrollx: number): void;
    dynamicCalcSize(): void;
    zoom(num: number): void;
}
