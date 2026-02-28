import SizeBoxManger from "../computed";
export type TableHeaerOptions = {
    height: number;
    offsetLeftWidth: number;
};
export default class TableFixedRender {
    boxMgr: SizeBoxManger;
    constructor(boxMgr: SizeBoxManger);
    render(ctx: CanvasRenderingContext2D, scrollY: number): void;
    renderHeaderBg(ctx: CanvasRenderingContext2D): void;
    renderContentBg(ctx: CanvasRenderingContext2D): void;
    renderHeader(ctx: CanvasRenderingContext2D, scrollY: number): void;
    dynamicCalcSize(): void;
}
