package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.Route;

import java.net.URI;

@Route
public class PopoverOnMarker extends VerticalLayout {

    public PopoverOnMarker() {
        MapLibre map = new MapLibre();
        map.setHeight("400px");
        map.setWidth("100%");
        Marker m = map.addMarker(22.300, 60.452);

        Popover popover = m.getPopover(() -> {
            // Popover is a (new) Vaadin component, that can contain essentially anything
            // That content is created lazily, when the popover is opened
            return new VerticalLayout(
                    new Button("Hola!!", e -> Notification.show("Hola!")),
                    new DatePicker(),
                    new Checkbox("Check me"),
                    new Button("Close", e -> e.getSource().findAncestor(Popover.class).close())
            );
        });

        add(map);

    }
}
