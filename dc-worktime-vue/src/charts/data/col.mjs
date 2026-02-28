export class Col {
  spans = [];
  col;
  colWidth;
  colHeight;
  constructor(col, colWidth, colHeight) {
    this.col = col;
    this.colWidth = colWidth;
    this.colHeight = colHeight;
  }
  pushSpan(span) {
    this.spans.push(span);
  }
  render(ctx, scrollX, offsetLeftWidth) {
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      offsetLeftWidth + this.col * this.colWidth - scrollX + 0.5,
      0
    );
    ctx.lineTo(
      offsetLeftWidth + this.col * this.colWidth - scrollX + 0.5,
      this.colHeight
    );
    ctx.strokeStyle = "#eee";
    ctx.lineWidth = 1;
    ctx.stroke();
    if (Math.abs(this.col) % 2 == 0) {
      ctx.fillStyle = "#f7f7f7";
      ctx.fillRect(
        offsetLeftWidth + this.col * this.colWidth - scrollX,
        0,
        this.colWidth,
        this.colHeight
      );
    }
    ctx.restore();
  }
}
