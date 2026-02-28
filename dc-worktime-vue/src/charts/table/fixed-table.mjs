import {
  iconCol,
  iconDown,
  iconNotCol,
  iconUp,
  TypePlan
} from "../data/base64.mjs";
import {
  coverDataLoopNodeLen,
  getChildrenIds,
  getTreeList
} from "../data/data.mjs";
export var ClickType = /* @__PURE__ */ ((ClickType2) => {
  ClickType2[ClickType2["Cell"] = 0] = "Cell";
  ClickType2[ClickType2["SPA"] = 1] = "SPA";
  ClickType2[ClickType2["HEADE_COL"] = 2] = "HEADE_COL";
  return ClickType2;
})(ClickType || {});
const Type = ["\u8BA1\u5212\u7EC4", "\u8BA1\u5212", "\u4EFB\u52A1", "\u53D1\u5E03", "\u91CC\u7A0B\u7891"];
export default class FixedLeftTableRender {
  boxMgr;
  container;
  _data = [];
  bodyContainer = null;
  onClick = null;
  collapseId = [];
  selectContainer;
  _showLength;
  i = 0;
  list = [];
  tooltip;
  isColAll = false;
  constructor(boxMgr, container, data) {
    this.boxMgr = boxMgr;
    this.container = container;
    this._data = data;
    this._showLength = coverDataLoopNodeLen(data);
    getTreeList(this._data, this.list);
    this.selectContainer = this.createSelect(-10);
    this.tooltip = this.createTooltip();
    this.render();
  }
  // 渲染头部
  render() {
    this.renderHeader();
    this.renderBody();
    this.container.appendChild(this.selectContainer);
  }
  setOnClick(onClick) {
    this.onClick = onClick;
  }
  renderHeader() {
    const headerContaimer = document.createElement("div");
    headerContaimer.style.position = "absolute";
    headerContaimer.style.top = "0px";
    headerContaimer.style.left = "0px";
    headerContaimer.style.height = `${this.boxMgr.box.headerHeight}px`;
    headerContaimer.style.width = `${this.boxMgr.box.offsetLeftWidth}px`;
    headerContaimer.style.display = "flex";
    headerContaimer.style.alignItems = "center";
    headerContaimer.style.boxShadow = "6px 0px 12px 0px rgba(0,0,0,0.08)";
    const span = document.createElement("div");
    span.style.height = `100%`;
    span.style.width = `${300}px`;
    span.style.display = "flex";
    span.style.gap = "10px";
    span.style.alignItems = "center";
    span.style.paddingInline = "10px";
    span.style.cursor = "pointer";
    const colImg2 = document.createElement("img");
    colImg2.style.height = `${16}px`;
    colImg2.style.width = `${16}px`;
    colImg2.src = iconNotCol;
    span.appendChild(colImg2);
    colImg2.onclick = (e) => {
      this.list = [];
      if (this.isColAll) {
        for (const element of this._data) {
          element.col = false;
        }
        colImg2.src = iconNotCol;
        this.isColAll = false;
      } else {
        for (const element of this._data) {
          element.col = true;
        }
        colImg2.src = iconCol;
        this.isColAll = true;
      }
      getTreeList(this._data, this.list);
      if (this.bodyContainer) {
        this.bodyContainer.innerHTML = "";
        this.updateBodyCell(this.bodyContainer);
      }
      if (this.onClick) {
        this.onClick(2 /* HEADE_COL */, null, e);
      }
    };
    const title = document.createElement("div");
    title.style.flex = "1";
    title.style.overflow = "hidden";
    title.style.color = "#000";
    title.style.fontSize = "14px";
    title.style.textAlign = "left";
    title.innerHTML = "\u6807\u9898";
    span.appendChild(colImg2);
    span.appendChild(title);
    const span2 = document.createElement("div");
    span2.style.height = `100%`;
    span2.style.width = `${100}px`;
    span2.style.display = "flex";
    span2.style.gap = "10px";
    span2.style.alignItems = "center";
    span2.style.paddingInline = "10px";
    span2.style.cursor = "pointer";
    const title2 = document.createElement("div");
    title2.style.flex = "1";
    title2.style.overflow = "hidden";
    title2.style.color = "#000";
    title2.style.fontSize = "14px";
    title2.innerHTML = "\u7C7B\u578B";
    title2.style.textAlign = "center";
    span2.appendChild(title2);
    headerContaimer.appendChild(span);
    headerContaimer.appendChild(span2);
    this.container.appendChild(headerContaimer);
  }
  renderBody() {
    const bodyContainer = document.createElement("div");
    bodyContainer.style.position = "absolute";
    bodyContainer.style.top = `${this.boxMgr.box.headerHeight}px`;
    bodyContainer.style.left = "0px";
    bodyContainer.style.height = `${this.boxMgr.box.height - this.boxMgr.box.headerHeight}px`;
    bodyContainer.style.width = `${this.boxMgr.box.offsetLeftWidth}px`;
    bodyContainer.style.overflowY = "hidden";
    bodyContainer.style.overflowX = "hidden";
    bodyContainer.style.boxShadow = "6px 0px 12px 0px rgba(0,0,0,0.08)";
    bodyContainer.style.zIndex = "87";
    this.updateBodyCell(bodyContainer);
    this.bodyContainer = bodyContainer;
  }
  updateBodyCell(bodyContainer) {
    const bodyContainer2 = document.createElement("div");
    bodyContainer2.style.height = `100%`;
    bodyContainer2.style.width = `${this.boxMgr.box.offsetLeftWidth}px`;
    bodyContainer2.style.transition = "none";
    this.container.appendChild(bodyContainer);
    bodyContainer.appendChild(bodyContainer2);
    this._showLength = this.renderCellAndReturnIndex(
      this._data,
      bodyContainer2
    );
  }
  createTooltip() {
    const tooltipContainer = document.createElement("div");
    tooltipContainer.style.position = "absolute";
    tooltipContainer.style.top = "-9999px";
    tooltipContainer.style.left = "-9999px";
    tooltipContainer.style.zIndex = "999";
    tooltipContainer.style.display = "none";
    tooltipContainer.style.width = "200px";
    tooltipContainer.style.height = "188px";
    tooltipContainer.style.backgroundColor = "#fff";
    tooltipContainer.style.borderRadius = "10px";
    tooltipContainer.style.boxShadow = "5px 5px 5px 5px rgba(0,0,0,0.1)";
    tooltipContainer.style.padding = "20px";
    this.container.appendChild(tooltipContainer);
    const title = document.createElement("div");
    title.style.borderBottom = "1px solid #eee";
    title.style.paddingBottom = "10px";
    title.style.color = "#000";
    title.style.fontWeight = "bold";
    title.style.textAlign = "left";
    const dateLimit = document.createElement("div");
    dateLimit.style.marginTop = "10px";
    dateLimit.style.marginBottom = "10px";
    dateLimit.style.fontSize = "12px";
    dateLimit.style.textAlign = "left";
    dateLimit.style.color = "#454545";
    const progress = document.createElement("div");
    progress.style.marginTop = "10px";
    progress.style.marginBottom = "10px";
    progress.style.fontSize = "12px";
    progress.style.color = "#454545";
    progress.style.textAlign = "left";
    const startDate = document.createElement("div");
    startDate.style.marginTop = "10px";
    startDate.style.marginBottom = "10px";
    startDate.style.fontSize = "12px";
    startDate.style.color = "#454545";
    startDate.style.textAlign = "left";
    const endDate = document.createElement("div");
    endDate.style.marginTop = "10px";
    endDate.style.fontSize = "12px";
    endDate.style.color = "#454545";
    endDate.style.textAlign = "left";
    tooltipContainer.appendChild(title);
    tooltipContainer.appendChild(dateLimit);
    tooltipContainer.appendChild(progress);
    tooltipContainer.appendChild(startDate);
    tooltipContainer.appendChild(endDate);
    return tooltipContainer;
  }
  hiddenTooltip() {
    this.tooltip.style.display = "none";
  }
  moveTooltip(top, left, data) {
    this.tooltip.style.display = "block";
    if (document.body.offsetHeight - top < 300) {
      this.tooltip.style.top = `${top - 200}px`;
    } else {
      this.tooltip.style.top = `${top}px`;
    }
    if (document.body.offsetWidth - left < 500) {
      this.tooltip.style.left = `${left - 200}px`;
    } else {
      this.tooltip.style.left = `${left}px`;
    }
    this.tooltip.firstChild.innerHTML = data.title;
    this.tooltip.childNodes[1].innerHTML = "\u6301\u7EED\u65F6\u95F4: 12";
    this.tooltip.childNodes[2].innerHTML = `\u5F53\u524D\u8FDB\u5EA6: ${data.progress}%`;
    this.tooltip.childNodes[3].innerHTML = "\u5F00\u59CB\u65E5\u671F: " + data.startDate;
    this.tooltip.childNodes[4].innerHTML = "\u5B8C\u6210\u65E5\u671F: " + data.endDate;
  }
  renderLegend(index, downX, downY) {
    this.moveTooltip(downY, downX, this.list[index]);
  }
  loopAppendCell(tree, index, bodyContainer2) {
    this.i++;
    if (tree.children && tree.children.length > 0 && !tree.col) {
      const cell = this.createCell(this.i, tree);
      bodyContainer2.appendChild(cell);
      const [span, span2] = this.createCellSpan(tree);
      cell.appendChild(span);
      cell.appendChild(span2);
      for (const element of tree.children) {
        index += this.loopAppendCell(element, index, bodyContainer2);
      }
      return index;
    } else {
      const cell = this.createCell(this.i, tree);
      bodyContainer2.appendChild(cell);
      const [span, span2] = this.createCellSpan(tree);
      cell.appendChild(span);
      cell.appendChild(span2);
      return 1;
    }
  }
  renderCellAndReturnIndex(tree, bodyContainer2) {
    let index = 0;
    this.i = 0;
    for (const element of tree) {
      index += this.loopAppendCell(element, 1, bodyContainer2);
    }
    return index;
  }
  createSelect(index) {
    const bodyContainer = document.createElement("div");
    bodyContainer.style.height = `${this.boxMgr.box.cellHeight + 0.5}px`;
    bodyContainer.style.width = `${this.boxMgr.box.width}px`;
    bodyContainer.style.border = "1px solid #A7D0FF";
    bodyContainer.style.position = "absolute";
    bodyContainer.style.top = `${index * this.boxMgr.box.cellWidth + this.boxMgr.box.headerHeight}px`;
    bodyContainer.style.left = `${0}px`;
    bodyContainer.style.display = "none";
    bodyContainer.style.backgroundColor = "rgba(27,134,255,0.08)";
    return bodyContainer;
  }
  createCell(index, data) {
    const bodyContainer = document.createElement("div");
    bodyContainer.style.height = `${this.boxMgr.box.cellHeight}px`;
    bodyContainer.style.width = `${this.boxMgr.box.offsetLeftWidth}px`;
    bodyContainer.setAttribute("key", index + "");
    bodyContainer.style.borderBottom = "1px solid #eee";
    bodyContainer.style.display = "flex";
    bodyContainer.style.alignItems = "center";
    bodyContainer.onclick = (e) => {
      if (this.onClick) {
        Reflect.set(e, "index", index - 1);
        this.onClick(0 /* Cell */, data, e);
      }
    };
    return bodyContainer;
  }
  selectCell(index, scrollY) {
    this.selectContainer.style.display = "block";
    this.selectContainer.style.top = `${(index - 1) * this.boxMgr.box.cellHeight + this.boxMgr.box.headerHeight - scrollY}px`;
  }
  hiddenSelect() {
    this.selectContainer.style.display = "none";
  }
  scrollContainer(distance) {
    const bodyContent = this.bodyContainer?.firstChild || null;
    if (bodyContent) {
      bodyContent.style.transform = `translateY(-${distance}px)`;
    }
  }
  collectedCollpaseIds(data) {
    const listIds = [];
    getChildrenIds(data, listIds);
    const isDown = this.collapseId.includes(data.id);
    for (const item of listIds) {
      const index = this.collapseId.findIndex((it) => it == item);
      if (isDown) {
        this.collapseId.splice(index, 1);
      } else if (index == -1) {
        this.collapseId.push(item);
      }
    }
  }
  createCellSpan(data) {
    const span = document.createElement("div");
    span.style.height = `100%`;
    span.style.width = `${300}px`;
    span.style.display = "flex";
    span.style.gap = "10px";
    span.style.alignItems = "center";
    span.style.paddingInline = "10px";
    span.style.cursor = "pointer";
    span.style.marginLeft = `${data.margin * 16}px`;
    if (data.children && data.children.length > 0) {
      const colImg = document.createElement("img");
      colImg.style.height = `${16}px`;
      colImg.style.width = `${16}px`;
      colImg.src = data.col ? iconDown : iconUp;
      colImg.onclick = (e) => {
        e.preventDefault();
        e.stopPropagation();
        this.list = [];
        const i = this.i;
        this.selectContainer.style.display = "none";
        this.selectContainer.style.top = "-9999px";
        data.col = !data.col;
        getTreeList(this._data, this.list);
        if (this.bodyContainer) {
          this.bodyContainer.innerHTML = "";
          this.updateBodyCell(this.bodyContainer);
        }
        if (this.onClick) {
          Reflect.set(e, "changeNum", this.i - i);
          this.onClick(1 /* SPA */, data, e);
        }
      };
      span.appendChild(colImg);
    } else {
      const colImg = document.createElement("div");
      colImg.style.height = `${16}px`;
      colImg.style.width = `${16}px`;
      span.appendChild(colImg);
    }
    const colImg2 = document.createElement("img");
    colImg2.style.height = `${16}px`;
    colImg2.style.width = `${16}px`;
    colImg2.src = TypePlan[Number(data.type) - 1];
    span.appendChild(colImg2);
    const title = document.createElement("div");
    title.style.flex = "1";
    title.style.overflow = "hidden";
    title.style.color = "#000";
    title.style.fontSize = "14px";
    title.innerHTML = data.title;
    title.style.textAlign = "left";
    span.appendChild(title);
    const span2 = document.createElement("div");
    span2.style.height = `100%`;
    span2.style.width = `${100}px`;
    span2.style.display = "flex";
    span2.style.gap = "10px";
    span2.style.alignItems = "center";
    span2.style.paddingInline = "10px";
    span2.style.cursor = "pointer";
    span2.style.flexShrink = "0";
    const title2 = document.createElement("div");
    title2.style.flex = "1";
    title2.style.overflow = "hidden";
    title2.style.color = "#000";
    title2.style.fontSize = "14px";
    title2.style.textAlign = "center";
    title2.innerHTML = Type[Number(data.type) - 1];
    span2.appendChild(title2);
    return [span, span2];
  }
  // 动态计算
  dynamicCalcSize() {
  }
  get showLength() {
    return this.list.length;
  }
}
