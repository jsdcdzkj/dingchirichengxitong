export type SpanRectType = {
    x: number;
    y: number;
    w: number;
    h: number;
};
export declare class Span {
    rect: SpanRectType;
    position: [number, number];
    text: string;
    constructor(rect: SpanRectType, position: [number, number], text?: string);
    render(ctx: CanvasRenderingContext2D): void;
}
export declare class FixedSpan {
    rect: SpanRectType;
    position: [number, number];
    data: string;
    image: HTMLImageElement;
    constructor(rect: SpanRectType, position: [number, number], data?: string);
    render(ctx: CanvasRenderingContext2D): void;
}
export declare class HeaderSpan extends Span {
    renderMonth(ctx: CanvasRenderingContext2D, year: string, month: string, day: string): void;
    render(ctx: CanvasRenderingContext2D): void;
}
