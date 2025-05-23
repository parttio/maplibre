package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.color.HexColor;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import in.virit.color.Color;
import org.vaadin.firitin.components.RichText;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Route
public class GpxAnimation extends VerticalLayout {
    private LineLayer tail;
    private ScheduledFuture<?> scheduledFuture = null;
    private Marker yourPosition;

    public GpxAnimation() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        add(new RichText().withMarkDown("""
                # Read GPX file on server, animate the route with marker and tail
                """));
        try {
            GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT).read(getClass().getResourceAsStream("/strava_default.gpx"));

            GeometryFactory gf = new GeometryFactory();
            Track track = gpx.getTracks().get(0);
            add(new H3("Track name: " + track.getName().get()));

            MapLibre map = new MapLibre(new URI("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU"));
            map.setHeight("400px");
            map.setWidth("100%");

            TrackSegment trackSegment = track.getSegments().get(0);
            List<WayPoint> points = trackSegment.getPoints();
            WayPoint start = points.get(0);
            Instant startTime = start.getTime().get();
            WayPoint end = points.get(points.size() - 1);
            Instant endTime = end.getTime().get();
            Marker marker = map.addMarker(start.getLongitude().doubleValue(), start.getLatitude().doubleValue());
            map.flyTo(marker.getGeometry(), 14.0);


            double speedUp = 100;
            double framerate = 10;
            long animationStart = System.currentTimeMillis();
            AtomicInteger currentPoint = new AtomicInteger();
            AtomicInteger firstTailPointInClient = new AtomicInteger();
            UI ui = UI.getCurrent();
            var tailpoints = new ArrayList<Coordinate>();
            tailpoints.add(toCoordinate(start));
            scheduledFuture = executor.scheduleWithFixedDelay(() -> {
                long msFromStart = (long) ((System.currentTimeMillis() - animationStart) * speedUp);
                Instant scrollTo = startTime.plusMillis(msFromStart);
                WayPoint wayPoint = points.get(currentPoint.get());
                int newPoints = 0;
                while (wayPoint.getTime().get().isBefore(scrollTo)) {
                    currentPoint.getAndIncrement();newPoints++;
                    if(currentPoint.get() >= points.size()) {
                        scheduledFuture.cancel(true);
                        return;
                    } else {
                        wayPoint = points.get(currentPoint.get());
                        tailpoints.add(toCoordinate(wayPoint));
                    }
                }
                int skip = tailpoints.size() - newPoints;
                Coordinate[] tailToDraw = tailpoints.stream().skip(skip).toArray(Coordinate[]::new);
                WayPoint current = wayPoint;
                ui.access(() -> {
                    marker.setPoint(gf.createPoint(new Coordinate(current.getLongitude().doubleValue(), current.getLatitude().doubleValue())));
                    if(tail == null) {
                        LineString lineString = gf.createLineString(tailToDraw);
                        var greenTranslucent = new LinePaint(HexColor.of("#00ff00"), 2.0);
                        // Testing the supplemental JSON API here, this gets merged
                        // to the LinePaint DTO
                        greenTranslucent.setSupplementalJson("""
                            {
                                "line-opacity": 0.4
                            }
                        """);
                        tail = map.addLineLayer(lineString, greenTranslucent);
                    } else {
                        tail.addCoordinates(0, tailToDraw);
                    }
                    //map.flyTo(marker.getGeometry());
                });
            }, (long) (1000 / framerate), (long) (1000 / framerate), TimeUnit.MILLISECONDS);
            add(map);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Coordinate toCoordinate(WayPoint wp) {
        return new Coordinate(wp.getLongitude().doubleValue(), wp.getLatitude().doubleValue());
    }
}
