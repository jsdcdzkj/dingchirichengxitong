export default class SizeBoxManger {
  box;
  canvas;
  cols = 0;
  rows = 0;
  startCol = 0;
  startRow = 0;
  _defaultCellWidth = 0;
  constructor(canvas, options = {}) {
    const { x, y, width, height, left, right } = canvas.getBoundingClientRect();
    this.canvas = canvas;
    this.box = Object.assign(
      {},
      {
        x,
        y,
        width,
        height,
        left,
        right,
        headerHeight: 48,
        offsetLeftWidth: 400,
        cellWidth: 48,
        cellHeight: 48
      },
      options
    );
    this.setClientBox(this.box.cellWidth, this.box.cellHeight);
  }
  onBoxSizeChange() {
    const { x, y, width, height } = this.canvas.getBoundingClientRect();
    this.box.x = x;
    this.box.y = y;
    this.box.width = width;
    this.box.height = height;
  }
  setHeaderHeight(height) {
    this.box.headerHeight = height;
  }
  setClientBox(cellWidth, cellHeight) {
    this.setCellWidth(cellWidth);
    this.setCellheight(cellHeight);
    this._defaultCellWidth = cellWidth;
  }
  setCellWidth(width) {
    this.box.cellWidth = width;
    this.cols = this.getVisibleCols();
  }
  setCellheight(height) {
    this.box.cellHeight = height;
    this.rows = this.getVisibleRows();
  }
  calc(scrollX, scrollY) {
    this.rows = this.getVisibleRows();
    this.cols = this.getVisibleCols();
    this.startCol = Math.floor(scrollX / this.box.cellWidth);
    this.startRow = Math.floor(scrollY / this.box.cellHeight);
  }
  resize(scrollX, scrollY) {
    const { x, y, width, height } = this.canvas.getBoundingClientRect();
    this.box.x = x;
    this.box.y = y;
    this.box.width = width;
    this.box.height = height;
    this.calc(scrollX, scrollY);
  }
  // 计算可见行列数（动态获取）
  getVisibleCols() {
    return Math.ceil(
      (this.box.width - this.box.offsetLeftWidth) / this.box.cellWidth
    ) + 1;
  }
  // 计算可见行列数（动态获取）
  getVisibleRows() {
    return Math.ceil(this.box.height / this.box.cellHeight) + 1;
  }
  dynamicCalcSize(zoom) {
    this.box.cellWidth = this._defaultCellWidth * zoom;
  }
}
