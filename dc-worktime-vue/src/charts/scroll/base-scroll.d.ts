import { Scroller } from "../core/scroller";
import SizeBoxManger from "../computed";
export type ScrollOptions = {
    rect: DOMRect;
};
export default abstract class BaseScrollController extends Scroller {
    _scrollX: number;
    boxMgr: SizeBoxManger;
    boundary: number[];
    constructor(boxMgr: SizeBoxManger);
    scrollCenter(): void;
    setBoundary(min: number, max: number): void;
    setScrollX(distance: number): void;
    get scrollx(): number;
}
