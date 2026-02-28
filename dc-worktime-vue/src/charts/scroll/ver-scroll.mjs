import BaseScrollController from "./base-scroll.mjs";
export default class VerScrollController extends BaseScrollController {
  isCanShow = false;
  constructor(boxMgr) {
    super(boxMgr);
  }
  // 手指是否落在滚动区域
  downArea(event) {
    this.isCanShow = this.computingCanDragRect(
      event.gesture.downX,
      event.gesture.downY
    );
    return this.isCanShow;
  }
  // 滚动
  scroll(event) {
    this.setScrollX(this.scrollx - (event.gesture.offsetY || 0));
  }
  // 计算是否为拖拽滚动区域
  computingCanDragRect(downX = 0, downY = 0) {
    const { headerHeight, height, offsetLeftWidth, width } = this.boxMgr.box;
    const x = offsetLeftWidth;
    const y = headerHeight;
    const x1 = width;
    const y1 = height;
    return downX > x && downX < x1 && downY > y && downY < y1;
  }
  setScrollX(distance) {
    const [min, max] = this.boundary;
    const { height } = this.boxMgr.box;
    const scroll = height + distance;
    if (min == max) {
      this._scrollX = distance;
    } else {
      if (max < height) {
        return;
      }
      if (distance < min) {
        this._scrollX = Math.max(distance, min);
      } else if (scroll > max) {
        this._scrollX = max - height;
      } else {
        this._scrollX = Math.min(distance, max);
      }
    }
  }
}
