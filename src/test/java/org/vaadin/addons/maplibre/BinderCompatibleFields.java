package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.time.LocalTime;

@Route
public class BinderCompatibleFields extends VVerticalLayout {

    private static String basemapStyle = "https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU";
    private final Binder<Dto> binder;
    private TextField name = new TextField("Name (just as a reference)");
    private PolygonField polygon = new PolygonField("Polygon (org.locationtech.jts.geom.Polygon)")
            .withAllowCuttingHoles(true);
    private LineStringField line = new LineStringField("LineString");
    private PointField point = new PointField("Point");

    public BinderCompatibleFields() {

        // Typically you should set this in some configuration bean
        VaadinService.getCurrent().getContext().setAttribute(BaseMapConfigurer.class, map -> map.initStyle(basemapStyle));

        configure(polygon);
        configure(line);
        configure(point);

        add(new H1("Binder compatible UI fields to edit basic JTS geometry types"));
        add(name);
        add(new VHorizontalLayout(polygon, line, point).withFullWidth());

        binder = new Binder<>(Dto.class);
        binder.bindInstanceFields(this);

        Dto dto = new Dto();

        binder.setBean(dto);

        Pre pre = new Pre();

        add(
                new VHorizontalLayout(
                        new Button("Display DTO value", e -> {
                            pre.setText(binder.getBean().toString());
                        }),
                        new Button("Reset dto", e->{
                            setDtoWithValues();
                        })
                ),
                pre);
    }

    private void configure(CustomField f) {
        f.setHeight("300px");
        f.setWidthFull();
    }

    public void setDtoWithValues() {

        try {
            Dto dto = new Dto();
            dto.setName("New dto " + LocalTime.now());

            GeoJsonReader geoJsonReader = new GeoJsonReader();
            WKTReader wktReader = new WKTReader();
            dto.setPolygon((Polygon) wktReader.read("POLYGON ((14.414062500000057 30.78316572944169, 11.953125000000199 17.610779566064465, 34.453125000000114 24.523876245928008, 14.414062500000057 30.78316572944169))"));
            dto.setLine((LineString) wktReader.read("LINESTRING (-19.3359374999998 34.307143856287084, -2.460937499999858 18.64624514267028, 16.17187500000071 -4.565473550710905, 30.585937500000796 -0.3515602939934723, 26.718750000000398 21.61657933674043, 11.249999999999972 31.052933985704698, 22.85156250000003 39.09596293630463)"));
            dto.setPoint((Point) wktReader.read("POINT (22.432676442827614 60.218838054023394)"));
            binder.setBean(dto);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
