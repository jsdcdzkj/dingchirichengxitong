import { Scroller } from "../core/scroller.mjs";
export default class BaseScrollController extends Scroller {
  _scrollX = 0;
  boxMgr;
  boundary = [0, 0];
  constructor(boxMgr) {
    super();
    this.boxMgr = boxMgr;
  }
  scrollCenter() {
    this._scrollX = -(this.boxMgr.box.width - this.boxMgr.box.offsetLeftWidth) / 2;
  }
  setBoundary(min, max) {
    this.boundary = [min, max];
  }
  setScrollX(distance) {
    const [min, max] = this.boundary;
    if (min == max) {
      this._scrollX = distance;
    } else {
      if (distance < min) {
        this._scrollX = Math.max(distance, min);
      } else {
        this._scrollX = Math.min(distance, max);
      }
    }
  }
  get scrollx() {
    return this._scrollX;
  }
}
