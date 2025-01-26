package org.vaadin.addons.maplibre.dto;

public class SymbolPaint extends AbstractKebabCasedDto {
    private String textColor;
    private String textHaloColor;
    private Number textHaloWidth;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextHaloColor() {
        return textHaloColor;
    }

    public void setTextHaloColor(String textHaloColor) {
        this.textHaloColor = textHaloColor;
    }

    public Number getTextHaloWidth() {
        return textHaloWidth;
    }

    public void setTextHaloWidth(Double textHaloWidth) {
        this.textHaloWidth = textHaloWidth;
    }

    public void setTextHaloWidth(Integer textHaloWidth) {
        this.textHaloWidth = textHaloWidth;
    }

}
