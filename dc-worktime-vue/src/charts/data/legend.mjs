const colorList = [
  {
    bgColor: "#f1f1f1",
    color: "#989898"
  },
  {
    bgColor: "#FEEFE6",
    color: "#F9885B"
  },
  {
    bgColor: "#E7F5E8",
    color: "#57B97F"
  },
  {
    bgColor: "#1B86FF",
    color: "#1682FB"
  },
  {
    bgColor: "#1B86FF",
    color: "#1682FB"
  }
];
export class Legend {
  startPoint;
  endPoint;
  progress;
  rect;
  type;
  constructor(startPoint, endPoint, progress, type) {
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.progress = progress;
    this.type = type;
    this.rect = {
      x: 0,
      y: 0,
      w: 0,
      h: 0,
      x1: 0,
      y1: 0
    };
  }
  render(ctx, cellWidth, cellHeight, offsetLeftWidth, headerHeight, scrollX, scrolly) {
    const [start, row] = this.startPoint;
    const [end] = this.endPoint;
    const x = offsetLeftWidth + start * cellWidth - scrollX;
    const y = headerHeight + row * cellHeight + (cellHeight / 2 - 10) - scrolly;
    const w = offsetLeftWidth + end * cellWidth - scrollX - x;
    ctx.save();
    const progress = this.progress / 100;
    const colorIndex = Number(this.type - 1);
    const { color, bgColor } = colorList[colorIndex];
    this.drawRoundedRect(ctx, x, y, w, 20, 4, bgColor);
    this.drawRoundedRect(ctx, x, y, w, 20, 4, color, true);
    this.drawRoundedRect(ctx, x, y, w * progress, 20, 4, color);
    ctx.restore();
    this.rect.x = x;
    this.rect.y = y;
    this.rect.w = w;
    this.rect.h = 20;
    this.rect.x1 = x + w;
    this.rect.y1 = y + 20;
  }
  clicInBox(downX, downY) {
    const { x1, x, y, y1 } = this.rect;
    return downX > x && downX < x1 && downY > y && downY < y1;
  }
  drawRoundedRect(ctx, x, y, width, height, radius, color, stroke = false) {
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.lineTo(x + width - radius, y);
    ctx.arcTo(x + width, y, x + width, y + radius, radius);
    ctx.lineTo(x + width, y + height - radius);
    ctx.arcTo(
      x + width,
      y + height,
      x + width - radius,
      y + height,
      radius
    );
    ctx.lineTo(x + radius, y + height);
    ctx.arcTo(x, y + height, x, y + height - radius, radius);
    ctx.lineTo(x, y + radius);
    ctx.arcTo(x, y, x + radius, y, radius);
    ctx.closePath();
    if (stroke) {
      ctx.lineWidth = 1;
      ctx.strokeStyle = color;
      ctx.stroke();
      ctx.clip();
    } else {
      ctx.fillStyle = color;
      ctx.fill();
    }
  }
  drawRoundedRect2(ctx, x, y, width, height, radius) {
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.lineTo(x + width - radius, y);
    ctx.arcTo(x + width, y, x + width, y + radius, radius);
    ctx.lineTo(x + width, y + height - radius);
    ctx.arcTo(
      x + width,
      y + height,
      x + width - radius,
      y + height,
      radius
    );
    ctx.lineTo(x + radius, y + height);
    ctx.arcTo(x, y + height, x, y + height - radius, radius);
    ctx.lineTo(x, y + radius);
    ctx.arcTo(x, y, x + radius, y, radius);
    ctx.closePath();
    ctx.fillStyle = "#F9885B";
    ctx.fill();
  }
  drawRoundedRect3(ctx, x, y, width, height, radius) {
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.lineTo(x + width - radius, y);
    ctx.arcTo(x + width, y, x + width, y + radius, radius);
    ctx.lineTo(x + width, y + height - radius);
    ctx.arcTo(
      x + width,
      y + height,
      x + width - radius,
      y + height,
      radius
    );
    ctx.lineTo(x + radius, y + height);
    ctx.arcTo(x, y + height, x, y + height - radius, radius);
    ctx.lineTo(x, y + radius);
    ctx.arcTo(x, y, x + radius, y, radius);
    ctx.closePath();
    ctx.fillStyle = "#FEEFE6";
    ctx.fill();
  }
}
