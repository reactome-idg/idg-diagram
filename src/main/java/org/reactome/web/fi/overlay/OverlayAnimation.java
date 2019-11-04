package org.reactome.web.fi.overlay;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class OverlayAnimation extends Animation{
	private int endSize;
    private int startSize;
    private Widget borderElement;

    @Override
    protected void onComplete() {
      if (endSize == 0) {
        borderElement.getElement().getStyle().setDisplay(Display.NONE);
        return;
      }
      borderElement.getElement().getStyle().setHeight(endSize, Unit.PX);
    }

    @Override
    protected void onUpdate(double progress) {
      double delta = (endSize - startSize) * progress;
      double newSize = startSize + delta;
      borderElement.getElement().getStyle().setHeight(newSize, Unit.PX);
    }

    void animateOverlay(int startSize, int endSize, int duration, Widget borderElement) {
      this.borderElement = borderElement;
      this.startSize = startSize;
      this.endSize = endSize;
      if (duration == 0) {
        onComplete();
        return;
      }
      run(duration);
    }

}