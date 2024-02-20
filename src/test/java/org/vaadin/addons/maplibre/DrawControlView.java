package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route
public class DrawControlView extends VVerticalLayout {

    public DrawControlView() {
        MapLibre map = new MapLibre("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU");
        DrawControl drawControl = new DrawControl(map);
        addAndExpand(map);

        var toolbar = new VHorizontalLayout();

        // TODO disabled items in Viritin
        // EnumSelect<DrawControl.DrawMode> modeSelect = new EnumSelect<>(DrawControl.DrawMode.class);
        RadioButtonGroup<DrawControl.DrawMode> modeSelect = new RadioButtonGroup<>();
        modeSelect.setItems(DrawControl.DrawMode.values());
        modeSelect.setItemEnabledProvider(item -> {
            if(item == DrawControl.DrawMode.DIRECT_SELECT) {
                return false; // needs id
            }
            return true;
        });
        modeSelect.addValueChangeListener(e -> {
            if(e.isFromClient())
                drawControl.setMode(e.getValue());
        });
        modeSelect.setValue(DrawControl.DrawMode.SIMPLE_SELECT);

        // TODO figure out what is wrong with selecting drawing
        // modeSelect.setValue(DrawControl.DrawMode.DRAW_POLYGON);
        // drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        // TODO figure out by setting mode with keyboard shortcut fails

        toolbar.add(modeSelect);

        drawControl.addModeChangeListener(e -> {
            modeSelect.setValue(e.getDrawMode());
        });

        toolbar.add(new VButton("Show geometries", buttonClickEvent -> {
            drawControl.getAll().thenAccept(geometryCollection ->  {
                Notification.show("Drawn geometries:" + geometryCollection.toText());
            });

        }));

        add(toolbar);
    }
}
