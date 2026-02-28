export class Span {
  rect;
  position = [0, 0];
  text;
  constructor(rect, position, text = "") {
    this.rect = rect;
    this.position = position;
    this.text = text;
  }
  render(ctx) {
    ctx.save();
    ctx.font = "12px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    if (this.text) {
      ctx.fillText(
        this.text,
        this.rect.x + this.rect.w / 2,
        this.rect.y + this.rect.h / 2
      );
    }
    ctx.restore();
  }
}
export class FixedSpan {
  rect;
  position = [0, 0];
  data;
  image;
  constructor(rect, position, data = "") {
    this.rect = rect;
    this.position = position;
    this.data = data;
    this.image = new Image();
    this.image.src = this.data.icon;
  }
  render(ctx) {
    ctx.save();
    ctx.font = "12px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    if (this.data.icon) {
      ctx.drawImage(
        this.image,
        this.rect.x + this.rect.w / 2,
        this.rect.y + this.rect.h / 2,
        16,
        16
      );
    }
    if (this.data.children && this.data.children.length) {
      ctx.drawImage(
        this.image,
        this.rect.x + this.rect.w,
        this.rect.y + this.rect.h / 2,
        16,
        16
      );
    }
    ctx.fillText(
      this.data.title,
      this.rect.x + this.rect.w + 60,
      this.rect.y + this.rect.h / 2 + 10
    );
    ctx.restore();
  }
}
export class HeaderSpan extends Span {
  renderMonth(ctx, year, month, day) {
    if (day == "01") {
      ctx.save();
      ctx.font = "10px Arial";
      ctx.textBaseline = "hanging";
      ctx.fillText(`${year}-${month}`, this.rect.x + 20, this.rect.y + 5);
      ctx.restore();
    }
  }
  render(ctx) {
    ctx.save();
    ctx.font = "12px Arial";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    if (this.position[1] == 0) {
      ctx.fillStyle = "#f1f1f1";
      ctx.fillRect(this.rect.x, this.rect.y, this.rect.w, this.rect.h);
      ctx.fillStyle = "#000";
      if (this.text) {
        const [year, month, day] = this.text.split("-");
        ctx.fillText(
          day,
          this.rect.x + this.rect.w / 2,
          this.rect.y + this.rect.h / 2 + 10
        );
        this.renderMonth(ctx, year, month, day);
      }
    }
    ctx.restore();
  }
}
