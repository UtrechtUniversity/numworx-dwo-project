/*
 * Created on Mar 7, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;
import fi.beans.tekstobjects.TekstArea;
import fi.dwo.client.domain.DwoHelper;

/**
 * This class represents a panel that can be used as a header for the results
 * table.
 * 
 * @author M.J.B. Kupers
 * @deprecated wordt niet gebruikt
 */
class ResultTableHeader extends Panel implements ActionListener, ToolTipIF {

    public final static int HORIZONTAL = 1;

    public final static int VERTICAL = 2;

    public final static int ACT_SORT_ASC = 1;

    public final static int ACT_SORT_DESC = 2;

    public final static int ACT_ZOOM_IN = 3;

    public final static int ACT_ZOOM_OUT = 4;

    private String text;

    private boolean showOrder = false;

    private boolean showZoomIn = false;

    private boolean showZoomOut = false;

    private boolean shrinkWidth = false;

    private int direction = HORIZONTAL;

    private LinkedImagePanel sortAsc;

    private LinkedImagePanel sortDesc;

    private LinkedImagePanel zoomIn;

    private LinkedImagePanel zoomOut;
    
    private ToolTippedLabel headerLabel; 
    
    private TekstArea headerLabelExtra;

    private Vector actionListeners = new Vector();
    
    private String toolTip;

    /**
     * Creates a new ResultTableHeader with the specified caption in horizontal
     * direction, and no other buttons.
     * 
     * @param text The caption of the header-panel.
     */
    public ResultTableHeader(String text) {
        super(null);
        this.text = text;
    }

    /**
     * Creates a new ResultTableHeader with the specified caption and direction,
     * and no other buttons.
     * 
     * @param text The caption of the header-panel.
     * @param direction The direction of the header-panel.
     */
    public ResultTableHeader(String text, int direction) {
        super(null);
        this.text = text;
        this.direction = direction;
    }

    /**
     * Creates a new ResultTableHeader with the specified parameters.
     * 
     * @param text The caption of the header-panel.
     * @param direction The direction of the header-panel. For example
     *            <code>VERTICAL</code> means that the characters of the
     *            caption will be layed-out vertically.
     * @param showOrder Indicate to show the order-buttons or not.
     * @param showZoomIn Indicate to show a zoom-in button or not.
     * @param showZoomOut Indicate to show a zoom-out button or not.
     * @param shrinkWidth Indicate if the width (or height if the direction is
     *            vertically) must be shrinked. If true, the buttons are placed
     *            above the caption, so the width will be smaller (but the
     *            height will be bigger).
     */
    public ResultTableHeader(String text, int direction, boolean showOrder,
            boolean showZoomIn, boolean showZoomOut, boolean shrinkWidth, boolean multiLine) {
        super(null);
        this.text = text;
        this.direction = direction;
        this.showOrder = showOrder;
        this.showZoomIn = showZoomIn;
        this.showZoomOut = showZoomOut;
        this.shrinkWidth = shrinkWidth;

        Image image;
        MediaTracker tr;
        FontMetrics fm;

        if (showOrder) {
            image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.RESULTS_ORDER_ASC);
            tr = new MediaTracker(this);
            tr.addImage(image, 0);
    		try {
    		    tr.waitForAll();
    		} catch (Exception e) {
    		}
            
            
            sortAsc = new LinkedImagePanel(image);

            sortAsc.addActionListener(this);

            image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.RESULTS_ORDER_DESC);
            tr = new MediaTracker(this);
            tr.addImage(image, 0);
    		try {
    		    tr.waitForAll();
    		} catch (Exception e) {
    		}
            
            sortDesc = new LinkedImagePanel(image);

            sortDesc.addActionListener(this);
        }
        if (showZoomIn) {
            image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.RESULTS_ZOOM_IN);
            tr = new MediaTracker(this);
            tr.addImage(image, 0);
    		try {
    		    tr.waitForAll();
    		} catch (Exception e) {
    		}
            
            zoomIn = new LinkedImagePanel(image);

            zoomIn.addActionListener(this);
        }

        if (showZoomOut) {
            image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.RESULTS_ZOOM_OUT);
            tr = new MediaTracker(this);
            tr.addImage(image, 0);
    		try {
    		    tr.waitForAll();
    		} catch (Exception e) {
    		}
            
            zoomOut = new LinkedImagePanel(image);

            zoomOut.addActionListener(this);
        }
        

        int height = 0;
        int width = 0;
        if ((direction == HORIZONTAL) && (!shrinkWidth)) {
            headerLabel = new ToolTippedLabel(text);
            headerLabel.setFont(GuiConstants.NORMAL_TEXT);
            fm = headerLabel.getFontMetrics(headerLabel.getFont());
            if (showOrder) {
                width += sortAsc.getSize().width + sortDesc.getSize().width + 6;
                if (height < sortAsc.getSize().height) {
                    height = sortAsc.getSize().height;
                }
                if (height < sortDesc.getSize().height) {
                    height = sortDesc.getSize().height;
                }
            }

            if (showZoomIn) {
                width += zoomIn.getSize().width + 4;
                if (height < zoomIn.getSize().height) {
                    height = zoomIn.getSize().height;
                }
            }

            if (showZoomOut) {
                width += zoomOut.getSize().width + 4;
                if (height < zoomOut.getSize().height) {
                    height = zoomOut.getSize().height;
                }
            }

            width += fm.stringWidth(headerLabel.getText()) + 14;

            if (height < fm.getHeight()) {
                height = fm.getHeight();
            }

            width += 4;

            setSize(width, height);

            int x = 2;

            headerLabel.setBounds(x, height / 2 - fm.getHeight() / 2, fm.stringWidth(headerLabel.getText()) + 10, fm.getHeight());
            headerLabel.setVisible(false);
            this.add(headerLabel);
            headerLabel.setVisible(true);

            x += headerLabel.getSize().width;

            if (showZoomOut) {
                x += 4;
                zoomOut.setLocation(x, height / 2 - zoomOut.getSize().height
                        / 2);
                zoomOut.setVisible(false);
                this.add(zoomOut);
                zoomOut.setVisible(true);
                x += zoomOut.getSize().width;
            }

            if (showZoomIn) {
                x += 4;
                zoomIn.setLocation(x, height / 2 - zoomIn.getSize().height / 2);
                zoomIn.setVisible(false);
                this.add(zoomIn);
                zoomIn.setVisible(true);
                x += zoomIn.getSize().width;
            }

            if (showOrder) {
                x += 4;
                sortAsc.setLocation(x, height / 2 - sortAsc.getSize().height
                        / 2);
                sortAsc.setVisible(false);
                this.add(sortAsc);
                sortAsc.setVisible(true);
                x += sortAsc.getSize().width + 2;

                sortDesc.setLocation(x, height / 2 - sortDesc.getSize().height
                        / 2);
                sortDesc.setVisible(false);
                this.add(sortDesc);
                sortDesc.setVisible(true);
                x += sortDesc.getSize().width;
            }
        } else if ((direction == HORIZONTAL) && (shrinkWidth)) {

            int height2 = 0;
            int width2 = 0;
            headerLabel = new ToolTippedLabel(text);
            headerLabel.setFont(GuiConstants.NORMAL_TEXT);
            fm = headerLabel.getFontMetrics(headerLabel.getFont());
            
            headerLabelExtra = new TekstArea();
			headerLabelExtra.setBounds(0,60,100,40);
			headerLabelExtra.setAllignment(TekstArea.CENTER);
			headerLabelExtra.setText(text);
			headerLabelExtra.resize();
			
            if (showOrder) {
                width2 += sortAsc.getSize().width + sortDesc.getSize().width
                        + 6;
                if (height2 < sortAsc.getSize().height) {
                    height2 = sortAsc.getSize().height;
                }
                if (height2 < sortDesc.getSize().height) {
                    height2 = sortDesc.getSize().height;
                }
            }

            if (showZoomIn) {
                width2 += zoomIn.getSize().width + 4;
                if (height2 < zoomIn.getSize().height) {
                    height2 = zoomIn.getSize().height;
                }
            }

            if (showZoomOut) {
                width2 += zoomOut.getSize().width + 4;
                if (height2 < zoomOut.getSize().height) {
                    height2 = zoomOut.getSize().height;
                }
            }

                        
            if(!multiLine) {
            	width += fm.stringWidth(headerLabel.getText()) + 14;
            
            	if (height < fm.getHeight()) {
            		height = fm.getHeight();
            	}
            }
            
            else {
            	width += headerLabelExtra.getSize().width;
				if (height < headerLabelExtra.getSize().height - 20) {
                	height = headerLabelExtra.getSize().height - 20;
            	}
			}
            width += 4;

            if (width2 > width) {
                width = width2;
            }
            width += 4;

            height += height2 + 2;
            setSize(width, height);

            int x = 2;

            headerLabel.setBounds(x, (height - height2 - 2) / 2 - fm.getHeight() / 2
                    + height2 + 2, fm.stringWidth(headerLabel.getText()) + 10, fm.getHeight());
            headerLabel.setVisible(false);
            if(!multiLine)this.add(headerLabel);
            headerLabel.setVisible(true);
			
			
            
            headerLabelExtra.setBounds(x, height2 , 100, headerLabelExtra.getSize().height);
            headerLabelExtra.setVisible(false);
            if(multiLine)this.add(headerLabelExtra);
            headerLabelExtra.setVisible(true);
            
            x = 2;

            if (showZoomOut) {
                x += 4;
                zoomOut.setLocation(x, height2 / 2 - zoomOut.getSize().height
                        / 2 + 1);
                zoomOut.setVisible(false);
                this.add(zoomOut);
                zoomOut.setVisible(true);
                x += zoomOut.getSize().width;
            }

            if (showZoomIn) {
                x += 4;
                zoomIn.setLocation(x, height2 / 2 - zoomIn.getSize().height / 2
                        + 1);
                zoomIn.setVisible(false);
                this.add(zoomIn);
                zoomIn.setVisible(true);
                x += zoomIn.getSize().width;
            }

            if (showOrder) {
                x += 4;
                sortAsc.setLocation(x, height2 / 2 - sortAsc.getSize().height
                        / 2 + 1);
                sortAsc.setVisible(false);
                this.add(sortAsc);
                sortAsc.setVisible(true);
                x += sortAsc.getSize().width + 2;

                sortDesc.setLocation(x, height2 / 2 - sortDesc.getSize().height
                        / 2 + 1);
                sortDesc.setVisible(false);
                this.add(sortDesc);
                sortDesc.setVisible(true);
                x += sortDesc.getSize().width;
            }
        } else if ((direction == VERTICAL) && (!shrinkWidth)) {
            headerLabel = new ToolTippedLabel(text);
            headerLabel.setFont(GuiConstants.NORMAL_TEXT);
            fm = headerLabel.getFontMetrics(headerLabel.getFont());
            if (showOrder) {
                if (sortAsc.getSize().height > sortDesc.getSize().height) {
                    height += sortAsc.getSize().height;
                } else {
                    height += sortDesc.getSize().height;
                }

                if (width < sortAsc.getSize().width + sortDesc.getSize().width
                        + 2) {
                    width = sortAsc.getSize().width + sortDesc.getSize().width
                            + 2;
                }
            }

            if (showZoomIn) {
                height += zoomIn.getSize().height + 4;
                if (width < zoomIn.getSize().width) {
                    width = zoomIn.getSize().width;
                }
            }

            if (showZoomOut) {
                height += zoomOut.getSize().height + 4;
                if (width < zoomOut.getSize().width) {
                    width = zoomOut.getSize().width;
                }
            }

            height += (fm.getHeight() + 1) * text.length() + 4;

            if (width < fm.stringWidth("M") + 4) {
                width = fm.stringWidth("M") + 4;
            }

            height += 4;
            width += 4;

            setSize(width, height);

            int y = 2;

            if (showOrder) {
                y += 4;
                sortAsc.setLocation(width
                        / 2
                        - (sortAsc.getSize().width + sortDesc.getSize().width + 2)
                        / 2, y);
                sortAsc.setVisible(false);
                this.add(sortAsc);
                sortAsc.setVisible(true);

                sortDesc.setLocation(sortAsc.getLocation().x
                        + sortAsc.getSize().width + 2, y);
                sortDesc.setVisible(false);
                this.add(sortDesc);
                sortDesc.setVisible(true);

                if (sortAsc.getSize().height > sortDesc.getSize().height) {
                    y += sortAsc.getSize().height;
                } else {
                    y += sortDesc.getSize().height;
                }
            }

            if (showZoomOut) {
                y += 4;
                zoomOut.setLocation(width / 2 - zoomOut.getSize().width / 2, y);
                zoomOut.setVisible(false);
                this.add(zoomOut);
                zoomOut.setVisible(true);
                y += zoomOut.getSize().height;
            }

            if (showZoomIn) {
                y += 4;
                zoomIn.setLocation(width / 2 - zoomIn.getSize().width / 2, y);
                zoomIn.setVisible(false);
                this.add(zoomIn);
                zoomIn.setVisible(true);
                y += zoomIn.getSize().height;
            }

            y += 4;

            for (int i = 0; i < text.length(); i++) {
                if (i + 1 == text.length()) {
                    headerLabel = new ToolTippedLabel(text.substring(i));
                } else {
                    headerLabel = new ToolTippedLabel(text.substring(i, i + 1));
                }

                headerLabel.setBounds(width / 2 - (fm.stringWidth(headerLabel.getText()) + 2) / 2, y, fm.stringWidth(headerLabel.getText()) + 2, fm.getHeight());
                headerLabel.setVisible(false);
                this.add(headerLabel);
                headerLabel.setVisible(true);
                y += fm.getHeight() + 1;

            }

        }

    }

    /**
     * Adds the specified action listener to receive action events from this
     * button. Action events occur when a user presses or releases the mouse
     * over this button. If l is null, no exception is thrown and no action is
     * performed.
     * 
     * @param l the action listener.
     * @see fi.dwo.client.gui.CourseIconIF#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        if (l != null) {
            actionListeners.addElement(l);
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int action = -1;
        if (e.getSource() == sortAsc) {
            action = ACT_SORT_ASC;
        } else if (e.getSource() == sortDesc) {
            action = ACT_SORT_DESC;
        } else if (e.getSource() == zoomIn) {
            action = ACT_ZOOM_IN;
        } else if (e.getSource() == zoomOut) {
            action = ACT_ZOOM_OUT;
        }
        if (action != -1) {
            for (int i = 0; i < actionListeners.size(); i++) {
                ((ActionListener) actionListeners.elementAt(i)).actionPerformed(new ActionEvent(this, action, ""));
            }
        }
    }

    public void setFont(Font f) {
        super.setFont(f);
        headerLabel.setFont(f);
    }
    
    /**
     * Sets the tooltip of this component.
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        ToolTipManager.registerComponent(this);
    }

    /**
     * Returns the tooltip of this component.
     * @return The tooltip of this component. 
     * @see fi.beans.tooltip.ToolTipIF#getToolTip()
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Returns this component.
     * @return This component.
     * @see fi.beans.tooltip.ToolTipIF#getComponent()
     */
    public Component getComponent() {
        return this;
    }
    
    public void setToolTipZoomIn(String toolTip) {
        if(showZoomIn) {
            zoomIn.setToolTip(toolTip);
        }
    }
    
    public void setToolTipZoomOut(String toolTip) {
        if(showZoomOut) {
            zoomOut.setToolTip(toolTip);
        }
        
    }
    
    public void setToolTipSortAsc(String toolTip) {
        if(showOrder) {
            sortAsc.setToolTip(toolTip);
        }
    }
    
    public void setToolTipSortDesc(String toolTip) {
        if(showOrder) {
            sortDesc.setToolTip(toolTip);
        }        
    }
    
    public void setToolTipLabel(String toolTip) {
        if((text != null) && (!text.equals(""))) {
            headerLabel.setToolTip(toolTip);
        }
    }

}