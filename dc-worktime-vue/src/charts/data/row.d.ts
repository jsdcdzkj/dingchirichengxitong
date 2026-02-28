import { Span } from "./span";
export declare class Row {
    spans: Span[];
    row: number;
    rowWidth: number;
    rowHeight: number;
    constructor(row: number, rowWidth: number, rowHeight: number);
    pushSpan(span: Span): void;
    render(ctx: CanvasRenderingContext2D, leftWidth?: number, headerHeight?: number, scrollY?: number): void;
}
