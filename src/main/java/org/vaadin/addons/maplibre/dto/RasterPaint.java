package org.vaadin.addons.maplibre.dto;

import org.vaadin.addons.maplibre.dto.expressions.Expression;

/**
 * Paint properties for a {@link RasterLayerDefinition}. Property names are
 * serialized to the kebab-cased form MapLibre expects (e.g. {@code rasterOpacity}
 * becomes {@code raster-opacity}).
 */
public class RasterPaint extends AbstractKebabCasedDto {

    private Object rasterOpacity;
    private Double rasterHueRotate;
    private Double rasterBrightnessMin;
    private Double rasterBrightnessMax;
    private Double rasterSaturation;
    private Double rasterContrast;
    private String rasterResampling;
    private Integer rasterFadeDuration;

    public RasterPaint() {
    }

    /**
     * @param rasterOpacity the opacity at which the image is drawn, between 0 and 1
     */
    public RasterPaint(Double rasterOpacity) {
        this.rasterOpacity = rasterOpacity;
    }

    public Object getRasterOpacity() {
        return rasterOpacity;
    }

    /**
     * @param rasterOpacity the opacity at which the image is drawn, between 0 and 1
     */
    public void setRasterOpacity(Double rasterOpacity) {
        this.rasterOpacity = rasterOpacity;
    }

    /**
     * Sets the opacity from an expression, e.g. a zoom based
     * {@link org.vaadin.addons.maplibre.dto.expressions.Interpolate} so the layer
     * fades in/out with the zoom level.
     *
     * @param rasterOpacity an expression evaluating to an opacity between 0 and 1
     */
    public void setRasterOpacity(Expression rasterOpacity) {
        this.rasterOpacity = rasterOpacity;
    }

    public Double getRasterHueRotate() {
        return rasterHueRotate;
    }

    /**
     * @param rasterHueRotate rotates hues around the color wheel, in degrees
     */
    public void setRasterHueRotate(Double rasterHueRotate) {
        this.rasterHueRotate = rasterHueRotate;
    }

    public Double getRasterBrightnessMin() {
        return rasterBrightnessMin;
    }

    /**
     * @param rasterBrightnessMin increases or decreases the brightness of the image,
     *                            this is the minimum brightness, between 0 and 1
     */
    public void setRasterBrightnessMin(Double rasterBrightnessMin) {
        this.rasterBrightnessMin = rasterBrightnessMin;
    }

    public Double getRasterBrightnessMax() {
        return rasterBrightnessMax;
    }

    /**
     * @param rasterBrightnessMax increases or decreases the brightness of the image,
     *                            this is the maximum brightness, between 0 and 1
     */
    public void setRasterBrightnessMax(Double rasterBrightnessMax) {
        this.rasterBrightnessMax = rasterBrightnessMax;
    }

    public Double getRasterSaturation() {
        return rasterSaturation;
    }

    /**
     * @param rasterSaturation increases or decreases the saturation of the image,
     *                         between -1 and 1
     */
    public void setRasterSaturation(Double rasterSaturation) {
        this.rasterSaturation = rasterSaturation;
    }

    public Double getRasterContrast() {
        return rasterContrast;
    }

    /**
     * @param rasterContrast increases or decreases the contrast of the image,
     *                       between -1 and 1
     */
    public void setRasterContrast(Double rasterContrast) {
        this.rasterContrast = rasterContrast;
    }

    public String getRasterResampling() {
        return rasterResampling;
    }

    /**
     * @param rasterResampling the resampling/interpolation method to use,
     *                         either {@code "linear"} or {@code "nearest"}
     */
    public void setRasterResampling(String rasterResampling) {
        this.rasterResampling = rasterResampling;
    }

    public Integer getRasterFadeDuration() {
        return rasterFadeDuration;
    }

    /**
     * @param rasterFadeDuration the duration of the fade-in/out effect in milliseconds
     */
    public void setRasterFadeDuration(Integer rasterFadeDuration) {
        this.rasterFadeDuration = rasterFadeDuration;
    }
}
