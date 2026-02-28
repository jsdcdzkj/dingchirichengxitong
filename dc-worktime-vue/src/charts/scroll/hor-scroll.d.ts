import { GestureEvent } from "../core/gesture";
import BaseScrollController from "./base-scroll";
import SizeBoxManger from "../computed";
interface IHorScrollController {
    downArea: (event: GestureEvent) => void;
}
export default class HorScrollController extends BaseScrollController implements IHorScrollController {
    isCanShow: boolean;
    boundary: number[];
    constructor(boxMgr: SizeBoxManger);
    downArea(event: GestureEvent): boolean;
    scroll(event: GestureEvent): void;
    private computingCanDragRect;
}
export {};
