package org.vaadin.addons.maplibre.dto;

public abstract class AbstractUrlMapSource extends AbstractMapSource {

    private String url;

    AbstractUrlMapSource(String type, String url) {
        super(type);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
