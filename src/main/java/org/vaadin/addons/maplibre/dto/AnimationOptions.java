package org.vaadin.addons.maplibre.dto;

public class AnimationOptions extends AbstractDto {
    // AnimationOptions
    private Boolean animate = null;
    private Integer duration = null;

    public Boolean getAnimate() {
        return animate;
    }

    public void setAnimate(Boolean animate) {
        this.animate = animate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
