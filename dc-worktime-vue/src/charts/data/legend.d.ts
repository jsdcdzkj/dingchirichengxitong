export type LegendPoint = [number, number];
export type BoxRect = {
    x: number;
    y: number;
    w: number;
    h: number;
    x1: number;
    y1: number;
};
export declare class Legend {
    startPoint: LegendPoint;
    endPoint: LegendPoint;
    progress: number;
    rect: BoxRect;
    type: number;
    constructor(startPoint: LegendPoint, endPoint: LegendPoint, progress: number, type: number);
    render(ctx: CanvasRenderingContext2D, cellWidth: number, cellHeight: number, offsetLeftWidth: number, headerHeight: number, scrollX: number, scrolly: number): void;
    clicInBox(downX: number, downY: number): boolean;
    drawRoundedRect(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number, color: string, stroke?: boolean): void;
    drawRoundedRect2(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number): void;
    drawRoundedRect3(ctx: CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number): void;
}
