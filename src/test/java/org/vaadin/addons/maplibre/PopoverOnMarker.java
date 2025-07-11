package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;

@Route
public class PopoverOnMarker extends VerticalLayout {

    public PopoverOnMarker() {
        MapLibre map = new MapLibre();
        map.setHeight("400px");
        map.setWidth("100%");
        Marker m = map.addMarker(22.300, 60.452);

        Popover popover = m.setPopover(() -> {
            // Popover is a (new) Vaadin component, that can contain essentially anything
            // That content is created lazily, when the popover is opened
            return new VerticalLayout(
                    new H3("Hello from Vaadin (on an existing marker)!"),
                    new Button("Hola!!", e -> Notification.show("Hola!")),
                    new DatePicker(),
                    new Checkbox("Check me"),
                    new Button("Close", e -> e.getSource().findAncestor(Popover.class).close())
            );
        });

        add(map);

        map.setCursor("crosshair");

        map.addMapClickListener(e -> {
            Notification.show("Clicked on Map: " + e.getPoint().toString());
            Marker marker = map.addMarker(e.getPoint());
            marker.setHtml("""
                    <svg width="20px" height="20px" viewBox="0 0 32 32" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns">
                        <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" sketch:type="MSPage">
                            <g id="Icon-Set-Filled" sketch:type="MSLayerGroup" transform="translate(-310.000000, -309.000000)" fill="#000000">
                                <path d="M341.207,309.82 C339.961,308.57 337.771,308.863 336.518,310.119 L330.141,316.481 L318.313,312.061 C317.18,311.768 316.039,311.389 314.634,312.798 C313.917,313.516 312.427,315.01 314.634,317.221 L322.744,323.861 L317.467,329.127 L312.543,327.896 C311.813,327.708 311.321,327.855 310.946,328.269 C310.757,328.505 309.386,329.521 310.342,330.479 L316.067,334.933 L320.521,340.658 C321.213,341.352 321.856,340.919 322.735,340.084 C323.292,339.526 323.172,339.239 323.004,338.426 L321.892,333.536 L327.133,328.277 L333.763,336.389 C335.969,338.6 337.46,337.105 338.177,336.389 C339.583,334.979 339.205,333.837 338.912,332.702 L334.529,320.854 L340.88,314.481 C342.133,313.226 342.454,311.069 341.207,309.82" id="airplane" sketch:type="MSShapeGroup">
                    </path>
                            </g>
                        </g>
                    </svg>
""");
            marker.setColor(NamedColor.GREEN);
            // default with the svg above is towards NE, we rotate it to W
            marker.setRotation(-45 - 90);
            // Adjust to be "visually centered" on the spot
            marker.setOffset(3, -2);
            marker.setPopover(() -> new VerticalLayout(
                    new H3("This is a new feature!"),
                    new TextField("Enter something"),
                    new Button("Click me", e2 -> Notification.show("Clicked!")),
                    new Button("Close", e2 -> e2.getSource().findAncestor(Popover.class).close())
            ));
            marker.openPopover();
        });

    }
}
