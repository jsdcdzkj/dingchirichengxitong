import dayjs from "dayjs";
import { HeaderSpan } from "../data/span.mjs";
export default class TableHeaderRender {
  boxMgr;
  constructor(boxMgr) {
    this.boxMgr = boxMgr;
  }
  // 渲染头部
  render(ctx, scrollx) {
    const startCol = this.boxMgr.startCol;
    const endCol = startCol + this.boxMgr.cols;
    for (let i = startCol; i <= endCol; i++) {
      const x = this.boxMgr.box.offsetLeftWidth + i * this.boxMgr.box.cellWidth - scrollx;
      const y = 0;
      new HeaderSpan(
        {
          x,
          y,
          w: this.boxMgr.box.cellWidth,
          h: this.boxMgr.box.cellHeight
        },
        [i, 0],
        dayjs().add(i, "day").format("YYYY-MM-DD")
      ).render(ctx);
    }
  }
  // 动态计算
  dynamicCalcSize() {
  }
  zoom(num) {
  }
}
