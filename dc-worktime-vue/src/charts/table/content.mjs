import { Col } from "../data/col.mjs";
import { Legend } from "../data/legend.mjs";
import { Row } from "../data/row.mjs";
import { Span } from "../data/span.mjs";
import { computedDate } from "../data/data.mjs";
export default class TableContentRender {
  boxMgr;
  cols = [];
  rows = [];
  spans = [];
  legends = [];
  constructor(boxMgr) {
    this.boxMgr = boxMgr;
  }
  // 渲染头部
  render(ctx, scrollx, scrolly, maxRow, datas) {
    const startCol = this.boxMgr.startCol;
    const startRow = this.boxMgr.startRow;
    const showRowNum = this.boxMgr.rows;
    const endCol = startCol + this.boxMgr.cols;
    const endRow = Math.min(startRow + showRowNum, maxRow);
    const dataList = computedDate(datas);
    this.cols = [];
    this.rows = [];
    this.spans = [];
    this.legends = [];
    for (let i = startCol; i <= endCol; i++) {
      const col = this.renderCol(i, showRowNum);
      col.render(ctx, scrollx, this.boxMgr.box.offsetLeftWidth);
      this.cols.push(col);
      for (let j = startRow; j < endRow; j++) {
        const span = this.renderSpan(i, j, scrollx, scrolly);
        span.render(ctx);
        this.spans.push(span);
      }
    }
    for (let i = 0; i < dataList.length; i++) {
      if (dataList[i]) {
        const {
          startCol: legendStartCol,
          endCol: legendEndCol,
          row: legendRow,
          type,
          progress
        } = dataList[i];
        if (!(legendStartCol > endCol || legendEndCol < startCol)) {
          const legend = new Legend(
            [legendStartCol, legendRow],
            [legendEndCol, legendRow],
            progress,
            type
          );
          legend.render(
            ctx,
            this.boxMgr.box.cellWidth,
            this.boxMgr.box.cellHeight,
            this.boxMgr.box.offsetLeftWidth,
            this.boxMgr.box.headerHeight,
            scrollx,
            scrolly
          );
          this.legends.push(legend);
        } else {
          this.legends.push(null);
        }
      }
    }
    for (let j = startRow; j < endRow; j++) {
      const row = this.renderRow(j, this.boxMgr.cols);
      row.render(
        ctx,
        this.boxMgr.box.offsetLeftWidth,
        this.boxMgr.box.headerHeight,
        scrolly
      );
      this.rows.push(row);
    }
  }
  _createRow(col, colWidth, cellHeight) {
    return new Row(col, colWidth, cellHeight);
  }
  createCol(col, cellWidth, colHeight) {
    return new Col(col, cellWidth, colHeight);
  }
  renderCol(col, colCount) {
    return this.createCol(
      col,
      this.boxMgr.box.cellWidth,
      colCount * this.boxMgr.box.cellHeight
    );
  }
  renderRow(row, rowCount) {
    return this._createRow(
      row,
      rowCount * this.boxMgr.box.cellWidth,
      this.boxMgr.box.cellHeight
    );
  }
  renderSpan(i, j, scrollx, scrolly) {
    const x = this.boxMgr.box.offsetLeftWidth + i * this.boxMgr.box.cellWidth - scrollx;
    const y = this.boxMgr.box.headerHeight + j * this.boxMgr.box.cellHeight - scrolly;
    const span = new Span(
      {
        x,
        y,
        w: this.boxMgr.box.cellWidth,
        h: this.boxMgr.box.cellHeight
      },
      [i, j]
      // `${i},${j}`
    );
    return span;
  }
  // 动态计算
  dynamicCalcSize() {
  }
}
