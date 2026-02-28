import { GestureEvent } from "../core/gesture";
import BaseScrollController from "./base-scroll";
import SizeBoxManger from "../computed";
interface IHorScrollController {
    downArea: (event: GestureEvent) => void;
}
export default class VerScrollController extends BaseScrollController implements IHorScrollController {
    isCanShow: boolean;
    constructor(boxMgr: SizeBoxManger);
    downArea(event: GestureEvent): boolean;
    scroll(event: GestureEvent): void;
    private computingCanDragRect;
    setScrollX(distance: number): void;
}
export {};
