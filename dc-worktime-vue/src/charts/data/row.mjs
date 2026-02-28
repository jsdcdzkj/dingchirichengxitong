export class Row {
  spans = [];
  row;
  rowWidth;
  rowHeight;
  constructor(row, rowWidth, rowHeight) {
    this.row = row;
    this.rowWidth = rowWidth;
    this.rowHeight = rowHeight;
  }
  pushSpan(span) {
    this.spans.push(span);
  }
  render(ctx, leftWidth = 0, headerHeight = 0, scrollY = 0) {
    ctx.save();
    const y = (this.row + 1) * this.rowHeight + headerHeight - scrollY - 1;
    ctx.beginPath();
    ctx.lineWidth = 1;
    ctx.moveTo(leftWidth, y + 0.5);
    ctx.lineTo(this.rowWidth + leftWidth, y + 0.5);
    ctx.strokeStyle = "#eee";
    ctx.lineCap = "butt";
    ctx.stroke();
    ctx.restore();
  }
}
