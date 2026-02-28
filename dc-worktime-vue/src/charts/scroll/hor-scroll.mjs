import BaseScrollController from "./base-scroll.mjs";
export default class HorScrollController extends BaseScrollController {
  isCanShow = false;
  boundary = [0, 0];
  constructor(boxMgr) {
    super(boxMgr);
    this.scrollCenter();
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
    this.setScrollX(this.scrollx - (event.gesture.offsetX || 0));
  }
  // 计算是否为拖拽滚动区域
  computingCanDragRect(downX = 0, downY = 0) {
    const { x, y, width, offsetLeftWidth, height } = this.boxMgr.box;
    const w = x + width;
    const h = y + height;
    return downX > x + offsetLeftWidth && downY > y && downX < w && downY < h;
  }
}
