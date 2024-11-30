package org.vaadin.addons.maplibre.dto;

public abstract class AbstractMapSource extends AbstractDto {
    private final String type;

    protected AbstractMapSource(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
