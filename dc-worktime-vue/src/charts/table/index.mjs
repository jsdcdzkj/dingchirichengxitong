import HorScrollController from "../scroll/hor-scroll.mjs";
import VerScrollController from "../scroll/ver-scroll.mjs";
import ZoomController from "../zoom/index.mjs";
import TableHeaderRender from "./header.mjs";
import TableContentRender from "./content.mjs";
import TableFixedRender from "./fixed.mjs";
import { Gesture, GestureEnum } from "../core/gesture.mjs";
import SizeBoxManger from "../computed.mjs";
import FixedLeftTableRender, { ClickType } from "./fixed-table.mjs";
import { coverData } from "../data/data.mjs";
class BaseTableRender {
  cols = [];
  rows = [];
  _canvas;
  _ctx;
  boxMgr;
  data = [];
  _container;
  constructor(container, data, options) {
    this._container = container;
    this._canvas = this.initContainer(container);
    this.data = data;
    const dpr = window.devicePixelRatio || 1;
    this._canvas.width = this._canvas.clientWidth * dpr;
    this._canvas.height = this._canvas.clientHeight * dpr;
    this.boxMgr = new SizeBoxManger(this._canvas, options?.box);
    this._ctx = this._canvas.getContext("2d");
    if (!this._ctx) {
      throw new Error("ctx, not get");
    }
    this._ctx.scale(dpr, dpr);
    this._ctx.imageSmoothingEnabled = false;
  }
  initContainer(container) {
    const canvas = document.createElement("canvas");
    const { width, height } = container.getBoundingClientRect();
    canvas.style.width = `${width}px`;
    canvas.style.height = `${height}px`;
    canvas.width = width;
    canvas.height = height;
    container.appendChild(canvas);
    return canvas;
  }
  resize() {
    const { width, height } = this._container.getBoundingClientRect();
    this._canvas.style.width = `${width}px`;
    this._canvas.style.height = `${height}px`;
    this._canvas.width = width;
    this._canvas.height = height;
  }
  drawTableBorder(ctx, x, y, x2, y2) {
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.lineTo(x2, y2);
    ctx.strokeStyle = "black";
    ctx.lineWidth = 1;
    ctx.stroke();
    ctx.restore();
  }
  getTableColReact() {
    const { x = 0, y = 0 } = this._canvas.getBoundingClientRect();
    return [y + this.boxMgr.box.cellHeight, x + this._canvas.width];
  }
  clearCanvas() {
    this._ctx?.clearRect(0, 0, this._canvas.width, this._canvas.height);
    this._ctx?.save();
  }
}
export class TableRender extends BaseTableRender {
  // 横向滚动控制
  horScroll;
  // 垂直滚动控制
  verScroll;
  // 缩放控制
  zoomCtl;
  // 头部渲染
  headerRender;
  // 内容渲染
  contentRender;
  // 左侧fixed渲染
  fixedRender;
  // 左侧table渲染
  fixedTable;
  // 手势
  gesture;
  resieObserver;
  constructor(container, data, options) {
    super(container, coverData(data), options);
    this.contentRender = new TableContentRender(this.boxMgr);
    this.fixedRender = new TableFixedRender(this.boxMgr);
    this.headerRender = new TableHeaderRender(this.boxMgr);
    this.horScroll = new HorScrollController(this.boxMgr);
    this.verScroll = new VerScrollController(this.boxMgr);
    this.zoomCtl = new ZoomController();
    this.gesture = new Gesture({
      container: this._canvas,
      callback: this._onGestureCallback.bind(this),
      preventDefault: true,
      stopPropagation: true
    });
    this.gesture.registerEvent();
    this.horScroll.onFlingCallback(this.horFlingCallback.bind(this));
    this.verScroll.onFlingCallback(this.verFlingCallback.bind(this));
    this._canvas.style.backgroundColor = "#fff";
    this.fixedTable = new FixedLeftTableRender(
      this.boxMgr,
      container,
      this.data
    );
    this.fixedTable.setOnClick((type, _, e) => {
      if (type == ClickType.SPA) {
        if (this.verScroll.scrollx > 0) {
          this.verScroll.setScrollX(
            Math.min(
              0,
              this.verScroll.scrollx - (e.changeNum || 0) * this.boxMgr.box.cellHeight
            )
          );
        }
      } else if (type == ClickType.Cell) {
        this.fixedTable.selectCell(
          (e.index || 0) + 1,
          this.verScroll.scrollx
        );
      } else if (type == ClickType.HEADE_COL) {
        if (this.verScroll.scrollx > 0) {
          this.verScroll.setScrollX(
            Math.min(
              0,
              this.verScroll.scrollx - (e.changeNum || 0) * this.boxMgr.box.cellHeight
            )
          );
        }
      }
      this.render();
    });
    this._onCanvasMover();
    this.resieObserver = new ResizeObserver(() => {
      this.resize();
      this.boxMgr.onBoxSizeChange();
      this.render();
    });
    this.resieObserver.observe(container);
  }
  horFlingCallback(offset) {
    this.horScroll.setScrollX(this.horScroll.scrollx - offset);
    this.render();
  }
  verFlingCallback(offset) {
    this.verScroll.setScrollX(this.verScroll.scrollx - offset);
    this.render();
  }
  zoomChange(zoom, newZoom, event) {
    this.boxMgr.dynamicCalcSize(newZoom);
  }
  _onGestureCallback(event) {
    const downY = event.gesture.downY || 0;
    const index = this.clicInBox(downY);
    switch (event.status) {
      // case GestureEnum.over:
      // 	console.log("over");
      // 	break;
      // case GestureEnum.leave:
      // 	console.log("leave");
      // 	break;
      case GestureEnum.move:
        this.fixedTable.hiddenSelect();
        if (this.horScroll.isCanScroll && this.horScroll.isCanShow) {
          this.horScroll.setFv(
            event.gesture?.offsetX || 0,
            event.gesture?.t || 0
          );
          this.horScroll.scroll(event);
        }
        if (this.verScroll.isCanScroll && this.verScroll.isCanShow) {
          this.verScroll.setFv(
            event.gesture?.offsetY || 0,
            event.gesture?.t || 0
          );
          this.verScroll.scroll(event);
        }
        requestAnimationFrame(this.render.bind(this));
        break;
      case GestureEnum.wheel:
        if (event.e && event.e.ctrlKey) {
          this.zoomCtl.computedZoom(
            event,
            this.zoomChange.bind(this)
          );
          requestAnimationFrame(this.render.bind(this));
        }
        break;
      case GestureEnum.click:
        if (index > 0 && index <= this.fixedTable.showLength) {
          this.fixedTable.selectCell(index, this.verScroll.scrollx);
        }
        break;
      case GestureEnum.up:
        if (this.horScroll.isCanShow) {
          this.horScroll.isCanShow = false;
          this._canvas.style.cursor = "default";
          this.horScroll.fling();
        } else if (this.verScroll.isCanShow) {
          this.verScroll.isCanShow = false;
          this._canvas.style.cursor = "default";
          this.verScroll.fling();
        }
        break;
      case GestureEnum.down:
        if (this.horScroll.downArea(event)) {
          this._canvas.style.cursor = "move";
        }
        if (this.verScroll.downArea(event)) {
          this._canvas.style.cursor = "move";
        }
        break;
    }
  }
  _onCanvasMover() {
    this._canvas.onmousemove = (e) => {
      const { clientX, clientY } = e;
      for (let i = 0; i < this.contentRender.legends.length; i++) {
        const legend = this.contentRender.legends[i];
        if (legend && legend.clicInBox(
          clientX - this.boxMgr.box.x,
          clientY - this.boxMgr.box.y
        )) {
          this.fixedTable.renderLegend(
            i,
            clientX - this.boxMgr.box.x,
            clientY - this.boxMgr.box.y
          );
          return;
        }
      }
      this.fixedTable.hiddenTooltip();
    };
  }
  clicInBox(downY) {
    const { y, cellHeight, headerHeight } = this.boxMgr.box;
    return Math.ceil((downY - y - headerHeight) / cellHeight) + Math.floor(this.verScroll.scrollx / cellHeight);
  }
  render() {
    this.fixedTable.scrollContainer(this.verScroll.scrollx);
    this.clearCanvas();
    this.boxMgr.calc(this.horScroll.scrollx, this.verScroll.scrollx);
    this.verScroll.setBoundary(
      0,
      this.boxMgr.box.cellHeight * this.fixedTable.showLength + this.boxMgr.box.headerHeight
    );
    this.contentRender.render(
      this._ctx,
      this.horScroll.scrollx,
      this.verScroll.scrollx,
      this.fixedTable.showLength,
      this.fixedTable.list
    );
    this.headerRender.render(this._ctx, this.horScroll.scrollx);
    this.fixedRender.render(this._ctx, this.verScroll.scrollx);
    const showRowNum = this.boxMgr.rows;
    const { cellHeight, width, offsetLeftWidth } = this.boxMgr.box;
    this.drawTableBorder(
      this._ctx,
      offsetLeftWidth,
      showRowNum * cellHeight,
      width,
      showRowNum * cellHeight
    );
    this._ctx.restore();
  }
  zoom() {
  }
  jumpToDate() {
  }
  destroy() {
    this.data = [];
  }
}
