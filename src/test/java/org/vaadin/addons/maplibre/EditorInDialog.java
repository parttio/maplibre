package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class EditorInDialog extends VerticalLayout {

    public EditorInDialog() {
        add("Testing if we could make re-attaching work at least to some extent...");

        var editor = new EditorDialog();

        add(new Button("Edit") {{

            addClickListener(event -> {
                editor.open();
            });

        }});

    }

    class EditorDialog extends Dialog {

        private PolygonField polyField = new PolygonField() {{
            setSizeFull();
        }};

        public EditorDialog() {
            setWidth("60vw");
            setHeight("60vw");
            setHeaderTitle("Editor of polygon");

            add(polyField);
        }
    }
}
