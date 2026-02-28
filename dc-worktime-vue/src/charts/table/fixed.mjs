export default class TableFixedRender {
  boxMgr;
  constructor(boxMgr) {
    this.boxMgr = boxMgr;
  }
  // 渲染头部
  render(ctx, scrollY) {
    this.renderHeader(ctx, scrollY);
  }
  renderHeaderBg(ctx) {
    ctx.save();
    ctx.fillStyle = "#f1f1f1";
    ctx.fillRect(
      0,
      0,
      this.boxMgr.box.offsetLeftWidth,
      this.boxMgr.box.cellHeight
    );
    ctx.restore();
  }
  renderContentBg(ctx) {
    ctx.save();
    ctx.fillStyle = "#FFFFFF";
    ctx.fillRect(
      0,
      this.boxMgr.box.headerHeight,
      this.boxMgr.box.offsetLeftWidth,
      this.boxMgr.box.height
    );
    ctx.restore();
  }
  renderHeader(ctx, scrollY) {
    ctx.save();
    this.renderContentBg(ctx);
    this.renderHeaderBg(ctx);
    ctx.restore();
  }
  // 动态计算
  dynamicCalcSize() {
  }
}
