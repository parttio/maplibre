this.init = function(styleObject, styleUrl, center, zoom) {
    var style = styleObject;
    if(!style) {
        style = styleUrl;
    }
    const component = this;
    this.map = new maplibregl.Map({
      container: this,
      style: style,
      center: center,
      zoom: zoom
    });
    this.map.on('load', () => {
        component.styleloaded = true;
    });
    this.map.addControl(new maplibregl.NavigationControl());
    setTimeout(() => this.map.resize(),10);
}

