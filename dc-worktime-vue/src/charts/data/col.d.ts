import { Span } from "./span";
export type ColAttrs = {
    date: string;
};
export declare class Col {
    spans: Span[];
    col: number;
    colWidth: number;
    colHeight: number;
    constructor(col: number, colWidth: number, colHeight: number);
    pushSpan(span: Span): void;
    render(ctx: CanvasRenderingContext2D, scrollX: number, offsetLeftWidth: number): void;
}
