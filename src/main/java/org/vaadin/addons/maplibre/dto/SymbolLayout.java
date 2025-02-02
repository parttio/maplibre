package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

import javax.swing.*;

public class SymbolLayout extends AbstractKebabCasedDto {

    public enum IconAnchor {
        center, left, right, top, bottom, topLeft, topRight, bottomLeft, bottomRight;

        @Override
        @JsonValue
        public String toString() {
            return camelToKebabCase(name());
        }
    }

    private String textField;
    private String[] textFont;
    private TextRotationAlignment textRotationAlignment;
    private TextTransform textTransform;
    private Object textSize;
    private Integer textRotate;
    private Boolean textAllowOverlap;
    private SymbolPlacement symbolPlacement;
    private String iconImage;
    private Object iconSize;
    private Object iconRotate;
    private IconAnchor iconAnchor;
    private TextRotationAlignment iconRotationAlignment;
    private Boolean iconAllowOverlap;

    public TextRotationAlignment getIconRotationAlignment() {
        return iconRotationAlignment;
    }

    public void setIconRotationAlignment(TextRotationAlignment iconRotationAlignment) {
        this.iconRotationAlignment = iconRotationAlignment;
    }

    public Object getIconSize() {
        return iconSize;
    }

    public Object getIconRotate() {
        return iconRotate;
    }

    public void setIconRotate(Number iconRotate) {
        this.iconRotate = iconRotate;
    }

    public void setIconRotate(Expression iconRotate) {
        this.iconRotate = iconRotate;
    }

    public void setIconSize(Number iconSize) {
        this.iconSize = iconSize;
    }

    public void setIconSize(Expression iconSize) {
        this.iconSize = iconSize;
    }

    public IconAnchor getIconAnchor() {
        return iconAnchor;
    }

    public void setIconAnchor(IconAnchor iconAnchor) {
        this.iconAnchor = iconAnchor;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String[] getTextFont() {
        return textFont;
    }

    public void setTextFont(String... textFont) {
        this.textFont = textFont;
    }

    public TextRotationAlignment getTextRotationAlignment() {
        return textRotationAlignment;
    }

    public void setTextRotationAlignment(TextRotationAlignment textRotationAlignment) {
        this.textRotationAlignment = textRotationAlignment;
    }

    public TextTransform getTextTransform() {
        return textTransform;
    }

    public void setTextTransform(TextTransform textTransform) {
        this.textTransform = textTransform;
    }

    public Object getTextSize() {
        return textSize;
    }

    public void setTextSize(Double textSize) {
        this.textSize = textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public void setTextSize(Expression textSize) {
        this.textSize = textSize;
    }

    public SymbolPlacement getSymbolPlacement() {
        return symbolPlacement;
    }

    public void setSymbolPlacement(SymbolPlacement symbolPlacement) {
        this.symbolPlacement = symbolPlacement;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public Integer getTextRotate() {
        return textRotate;
    }

    public void setTextRotate(Integer textRotate) {
        this.textRotate = textRotate;
    }

    public Boolean getIconAllowOverlap() {
        return iconAllowOverlap;
    }

    public void setIconAllowOverlap(Boolean iconAllowOverlap) {
        this.iconAllowOverlap = iconAllowOverlap;
    }

    public Boolean getTextAllowOverlap() {
        return textAllowOverlap;
    }

    public void setTextAllowOverlap(Boolean textAllowOverlap) {
        this.textAllowOverlap = textAllowOverlap;
    }

    /**
     * https://maplibre.org/maplibre-style-spec/layers/#text-rotation-alignment
     */
    public enum TextRotationAlignment {
        map, viewport, viewportGlyph, auto;

        @Override
        @JsonValue
        public String toString() {
            return camelToKebabCase(name());
        }
    }

    /**
     * https://maplibre.org/maplibre-style-spec/layers/#icon-rotation-alignment
     */
    public enum RotationAlignment {
        map, viewport, auto;

        @Override
        @JsonValue
        public String toString() {
            return camelToKebabCase(name());
        }
    }

    /**
     * https://maplibre.org/maplibre-style-spec/layers/#layout-symbol-text-transform
     */
    public enum TextTransform {none, uppercase, lowercase}

    /**
     * https://maplibre.org/maplibre-style-spec/layers/#layout-symbol-symbol-placement
     */
    public enum SymbolPlacement {
        point, line, lineCenter;

        @Override
        @JsonValue
        public String toString() {
            return camelToKebabCase(name());
        }
    }
}
