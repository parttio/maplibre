package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route
public class CustomMarkerField extends VVerticalLayout {

    private static String basemapStyle = "https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU";
    private PointField point = new PointField("Point");

    public CustomMarkerField() {

        point.setMarkerFormatter(marker -> formatMarker(marker, 0));

        configure(point);
        add(point);
        point.getMap().setCursor("crosshair");

        add(new IntegerField("Angle") {{
            setValue(0);
            addValueChangeListener(e -> {
                point.setMarkerFormatter(marker -> formatMarker(marker, e.getValue()));
            });
            setStep(45);
            setStepButtonsVisible(true);
        }});
    }

    private void formatMarker(Marker marker, int rotation) {
        marker.setHtml("""
                <div style="width: 30px; height: 60px; background:rgba(0,255,0,0.2);">
                <svg
                   width="30"
                   height="30"
                   viewBox="0 0 0.5625 0.5625"
                   version="1.1"
                   xmlns="http://www.w3.org/2000/svg"
                   xmlns:svg="http://www.w3.org/2000/svg">
                    <path style="_S_"
                       d="M 0.28125,0 0.09375,0.421875 0.1640625,0.3730591 c 0,0 0.0410234,-0.028497 0.094043,-0.0355591 l 0.001648,0.225 h 0.046875 L 0.3049805,0.3375 C 0.3584728,0.344261 0.4003784,0.3710815 0.4003784,0.3710815 L 0.46875,0.41601562 Z m 0,0.11524658 0.0918091,0.20115967 C 0.347609,0.30373425 0.3327629,0.28905025 0.28125,0.28905025 c -0.0496471,0 -0.0619519,0.0140353 -0.0878906,0.027356 z" />
                </svg>
                </div>
                """);
        marker.setOffset(0, 0);
        marker.setRotationAlignment(SymbolLayout.RotationAlignment.map);
        marker.setRotation(rotation);
        marker.setAnchor(Marker.PositionAnchor.CENTER);
    }

    private void configure(CustomField f) {
        f.setHeight("300px");
        f.setWidthFull();
    }

}
