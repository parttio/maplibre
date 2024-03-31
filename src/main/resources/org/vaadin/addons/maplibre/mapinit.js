var style = $style;
if(!style) {
    style = '$styleUrl';
}
const component = this;
const conf = {
   container: this,
   style: style
}
// center & zoom only if explicitly set, can com from style as well
if($setCenter) {
    conf.center = $GeoJsonHelper.toJs($this.center);
}
if($setZoom) {
 conf.zoom =  $this.zoomLevel;
}

this.map = new maplibregl.Map(conf);

this.map.on('load', () => {
    component.styleloaded = true;
});

this.getViewPort = () => {
    const b = this.map.getBounds();
    return {
        sw : b.getSouthWest(),
        ne : b.getNorthEast(),
        c : this.map.getCenter(),
        bearing : this.map.getBearing(),
        pitch : this.map.getPitch()
    };
}

this.map.addControl(new maplibregl.NavigationControl());
setTimeout(() => this.map.resize(),0);