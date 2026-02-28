import { GestureEvent } from "../core/gesture";
export type ZoomOptions = {
    maxZoom?: number;
    minZoom?: number;
    zoom?: number;
    zoomStep?: number;
};
export type ZoomSelfOptions = {
    maxZoom: number;
    minZoom: number;
    zoom: number;
    zoomStep: number;
};
export default class ZoomController {
    optons: ZoomSelfOptions;
    constructor(options?: ZoomOptions);
    computedZoom(event: GestureEvent, callback: (zoom: number, newZoom: number, event: GestureEvent) => void): void;
    get zoom(): number;
}
