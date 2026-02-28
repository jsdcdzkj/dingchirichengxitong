export default class ZoomController {
  optons = {
    maxZoom: 4,
    zoom: 1,
    zoomStep: 0.1,
    minZoom: 0.5
  };
  constructor(options) {
    this.optons = Object.assign({}, this.optons, { ...options });
  }
  computedZoom(event, callback) {
    const delta = (event.e.deltaY || 0) > 0 ? -this.optons.zoomStep : this.optons.zoomStep;
    const newZoom = Math.min(
      5,
      Math.max(this.optons.minZoom, this.optons.zoom + delta)
    );
    callback(this.optons.zoom, newZoom, event);
    this.optons.zoom = newZoom;
  }
  get zoom() {
    return this.optons.zoom;
  }
}
