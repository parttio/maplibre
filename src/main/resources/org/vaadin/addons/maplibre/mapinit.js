var style = $style;
if(!style) {
    style = '$styleUrl';
}
const component = this;
this.map = new maplibregl.Map({
  container: this,
  style: style,
  center: $GeoJsonHelper.toJs($this.center),
  zoom: $this.zoomLevel
});
this.map.on('load', () => {
    component.styleloaded = true;
});
this.map.addControl(new maplibregl.NavigationControl());
setTimeout(() => this.map.resize(),0);