package org.reactome.web.fi.common.colorPicker;

/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends SimplePanel implements HasClickHandlers {

  protected List<ColorPickerListener> listeners = new ArrayList<ColorPickerListener>();
  private ColorPickerDialog picker = new ColorPickerDialog();
  private String selectedColor = "#FFF"; //$NON-NLS-1$

  public ColorPicker() {
	
	this.setSize("30px", "30px");
	this.getElement().getStyle().setBackgroundColor(selectedColor);
	this.getElement().setTitle("Click to Choose...");
    this.addClickHandler( new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Rectangle rect = ElementUtils.getSize( ColorPicker.this.getElement() );
	        picker.setPopupPosition( DOM.getAbsoluteLeft( ColorPicker.this.getElement() ), DOM
	            .getAbsoluteTop( ColorPicker.this.getElement() )
	            + rect.height + 2 );
	        picker.show();
		}

    });

  }

  public String getColor() {
    return this.selectedColor;
  }

  public void setColor( String hex ) {
    this.selectedColor = hex;
    this.getElement().getStyle().setProperty( "backgroundColor", hex ); //$NON-NLS-1$

    for ( ColorPickerListener listener : listeners ) {
      listener.colorPicked( this );
    }
  }

  public void showPicker() {
    picker.center();
  }

  public void addColorPickerListener( ColorPickerListener listener ) {
    listeners.add( listener );
  }
  
  public String[] getColors() {
	  return picker.getColors();
  }

  private class ColorPickerDialog extends PopupPanel {
    @SuppressWarnings( "nls" )
    private String[] colors = new String[] { 
      "#0F0F0F", "#993300", "#333300", "#003300", "#003366", "#000080", "#333399", "#333333",

      "#800000", "#ff6600", "#808000", "#008000", "#008080", "#0000ff", "#666699", "#808080",

      "#ff0000", "#ff9900", "#99cc00", "#339966", "#33cccc", "#3366ff", "#800080", "#969696",

      "#ff00ff", "#ffcc00", "#ffff00", "#00ff00", "#00ffff", "#00ccff", "#993366", "#c0c0c0",

      "#ff99cc", "#ffcc99", "#ffff99", "#ccffcc", "#ccffff", "#99ccff", "#cc99ff", "#FFFFFF" };

    public ColorPickerDialog() {
      super( true );
      this.setStyleName( "color-picker-popup" ); //$NON-NLS-1$
      this.getElement().getStyle().setZIndex(3000);

      FlexTable table = new FlexTable();
      table.setCellPadding( 0 );
      table.setCellSpacing( 1 );

      for ( int i = 0, row = 0; i < colors.length; i++, row++ ) {
        for ( int y = 0; y < 7 && i < colors.length; y++, i++ ) {
          table.setWidget( row, y, new ColorBox( this, colors[i] ) );
        }
      }
      SimplePanel panel = new SimplePanel();
      panel.getElement().getStyle().setProperty( "padding", "3px" ); //$NON-NLS-1$ //$NON-NLS-2$
      panel.add( table );

      this.add( panel );
    }
    
    public String[] getColors() {
    	return colors;
    }

  }

  private class ColorBox extends SimplePanel implements HasClickHandlers, ClickHandler {
    String color;
    ColorPickerDialog dialog;

    public ColorBox( ColorPickerDialog dialog, String color ) {
      this.dialog = dialog;
      this.color = color;
      this.setStyleName( "color-swatch" ); //$NON-NLS-1$
      SimplePanel panel = new SimplePanel();

      panel.getElement().getStyle().setProperty( "border", "1px solid #aaa" ); //$NON-NLS-1$ //$NON-NLS-2$
      panel.getElement().getStyle().setProperty( "backgroundColor", color ); //$NON-NLS-1$
      panel.setSize("20px", "20px");
      add( panel );
      panel.setStyleName( "color-swatch-center" ); //$NON-NLS-1$
      this.addClickHandler( this );
    }

    public HandlerRegistration addClickHandler( ClickHandler handler ) {
      return addDomHandler( handler, ClickEvent.getType() );
    }

    public void onClick( ClickEvent event ) {
      ColorPicker.this.setColor( color );
      dialog.hide();
    }

  }
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler( handler, ClickEvent.getType() );
	}
}