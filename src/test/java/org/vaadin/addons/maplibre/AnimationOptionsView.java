package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.vaadin.addons.maplibre.dto.AnimationOptions;
import org.vaadin.addons.maplibre.dto.FitBoundsOptions;
import org.vaadin.addons.maplibre.dto.FlyToOptions;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.VButton;

@Route
public class AnimationOptionsView extends VerticalLayout {


    public AnimationOptionsView() throws ParseException {

        Polygon finland = (Polygon) new WKTReader().read("""
                POLYGON ((20.318714757643562 69.06130446171758, 22.79763521551746 67.79356000014633, 23.983205869284774 65.26320682313252, 21.719843712094473 63.77998276819781, 19.99537730661561 61.08872462246134, 21.0731688100403 59.97542193736851, 26.785463778186624 59.65025984566799, 30.773292340855818 60.827118401765404, 32.49775874633295 63.00772430481814, 31.20440894222466 65.48774695877438, 30.557734040170516 67.04874476218345, 30.12661743879997 68.3171525012769, 30.12661743879997 69.32927072742024, 29.156605085719605 70.11362126286264, 27.97103443195229 70.40484829303603, 26.031009725789858 70.223315051452, 24.95321822236687 68.98413516597049, 23.336530967230658 68.98413516597049, 21.827622862437096 69.51866435751643, 20.426493907986185 69.40522835432839, 20.318714757643562 69.06130446171758))
                """);


        add(new RichText().withMarkDown("""
                # flyTo options
                
                """));
        MapLibre map = new MapLibre();
        map.setHeight("400px");
        map.setWidth("100%");
        map.setCenter(24.945831, 60.192059);
        map.setZoomLevel(3);
        add(map);
        IntegerField duration = new IntegerField("Animation duration (ms)");
        duration.setValue(5000);
        add(duration);
        add(new VButton("Fly to zlevel 10", () -> {
            FlyToOptions flyToOptions = new FlyToOptions();
            flyToOptions.setZoom(10.0);
            flyToOptions.setAnimate(true);
            flyToOptions.setDuration(duration.getValue());
            map.flyTo(flyToOptions);
        }));

        add(new VButton("ZoomToFinland", () -> {
            FitBoundsOptions animationOptions = new FitBoundsOptions();
            animationOptions.setDuration(duration.getValue());
            map.fitBounds(finland, animationOptions);
        }));

        add(new VButton("ZoomToFinland without animation", () -> {
            FitBoundsOptions animationOptions = new FitBoundsOptions();
            animationOptions.setAnimate(false);
            map.fitBounds(finland, animationOptions);
        }));
    }
}
