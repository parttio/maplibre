package org.vaadin.addons.maplibre.dto;

public class Projection extends AbstractKebabCasedDto {

    public static Projection GLOBE = new Projection("globe");
    public static Projection MERCATOR = new Projection("mercator");

    private String type;

    public Projection(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
