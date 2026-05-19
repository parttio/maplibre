package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.Projection;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.util.VStyleUtil;

import java.net.URI;
import java.net.URISyntaxException;

@Route
public class MarkerMap extends VerticalLayout {

    public static String svgArrowTemplate = """
    <svg width="30px" height="30px" viewBox="0 0 24 24" fill="none">
      <path
         d="M 3.16496,19.5025 10.5275,2.99281 c 1.518289,-2.88445928 1.511073,-2.88445928 2.945,0 L 20.835,19.5025 c 2.312172,4.237686 -0.8141,3.0474 -2.2019,2.3065 l -5.9037,-3.152 c -0.4592,-0.2452 -0.9996,-0.2452 -1.4588,0 L 5.36689,21.809 C 3.97914,22.5499 0.93718072,23.908912 3.16496,19.5025 Z"
         style="%s" />
    </svg>
    """;

    public String svgMarker = """
            <svg width="50px" height="50px" viewBox="0 0 15 15" version="1.1" id="marker" xmlns="http://www.w3.org/2000/svg">
              <path style="fill:blue;" id="p" d="M7.5,0C5.0676,0,2.2297,1.4865,2.2297,5.2703&#xA;&#x9;C2.2297,7.8378,6.2838,13.5135,7.5,15c1.0811-1.4865,5.2703-7.027,5.2703-9.7297C12.7703,1.4865,9.9324,0,7.5,0z"/>
                <text style="font-size: 6px;fill:white; font-weight:bold;" x="50%" y="40%" dominant-baseline="middle" text-anchor="middle">_T_</text>
            </svg>
    """;

    public MarkerMap() {
        add(new RichText().withMarkDown("""
        # Markers
        """));
        try {
            MapLibre map = new MapLibre(new URI("https://demotiles.maplibre.org/style.json"));
            map.setHeight("400px");
            map.setWidth("100%");
            map.setCenter(24.945831, 60.192059);
            map.setZoomLevel(3);
            add(map);

            Marker marker;

            marker = map.addMarker(24.945831, 60.192059);
            marker.addClickListener(() -> {
                Notification.show("Clicked");
            });
            marker.setPopover(() -> new Paragraph("You won!"));

            marker = map.addMarker(24.945831, 60.192059);
            marker.setHtml("<div class='e1'>E1</div>");
            marker.setOffset(0,0);
            marker.addClickListener(() -> Notification.show("That's E1!"));

            marker = map.addMarker(25.945831, 60.192059);
            marker.setHtml("Mike's Hardware - 123 Fake Street, Anytown, USA");

            marker = map.addMarker(22.945831, 60.192059);
            marker.setHtml(svgArrowTemplate.formatted("fill:blue"));

            marker = map.addMarker(20.945831, 60.192059);
            marker.setHtml(svgMarker.replace("_T_", "66"));
            marker.addClickListener(() -> Notification.show("That's 66!"));


            VStyleUtil.injectAsFirst("""
                .e1 {
                  border: 1px solid gray;
                  display: inline-block;
                  border-radius: 1em;
                  padding:0.25em;
                  line-height:1;
                }
            """);


            //map.addMarker(20.945831, 60.192059);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
