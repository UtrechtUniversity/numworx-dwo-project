// Source file: C:\\fi\\dwo\\parameters\\gui\\ParameterComponent.java

package fi.dwo.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Hashtable;
import java.util.Vector;

import fi.beans.scorm.Parameter;
import fi.dwo.client.gui.BorderedPanel;

public class ParameterComponent extends BorderedPanel implements ParameterComponentIF, FocusListener, ComponentListener {
	
    protected static final int LEFT_MARGIN = 5;

    protected static final int COMPONENT_SPACING = 10;

    protected static int MAX_LABEL_LENGTH = 120;

    protected Vector subComponents;

    protected Hashtable defaultValue;

    public static Color SELECTED_COLOR_1 = ParameterConstants.MAIN_COLOR;

    public static Color SELECTED_COLOR_2 = Color.white;

    protected ParameterComponentIF parent;

    protected ParameterComponentIF focussedComponent;

    protected Parameter parameter;

    protected HelpButton helpButton;
    
    protected Label preLabel;
    protected Label postLabel;
    
    protected boolean isSub;
    
    private boolean isResizing;
    
	
	public ParameterComponent(ParameterComponentIF parent, Parameter parameter, Hashtable defaultValue) {
		this(parent, parameter, defaultValue, false);
	}
	
	public ParameterComponent(ParameterComponentIF parent, Parameter parameter, Hashtable defaultValue, boolean isSub) {
		super(null, BorderedPanel.NONE);
		this.parent = parent;
		this.parameter = parameter;
		if(defaultValue == null) {
		    this.defaultValue = new Hashtable();
		} else {
		    this.defaultValue = defaultValue;
		}
		this.isSub = isSub;
		if(parent != null) {
		    this.setBackground(parent.getColor());
		}
		subComponents = new Vector();
		focussedComponent = null;
		this.setLayout(null);
		
		if(parameter != null) {
		    preLabel = new FixedLabel(parameter.getPreLabel() + ":");
		    preLabel.setFont(ParameterConstants.LABEL_FONT);
	        FontMetrics fm = preLabel.getFontMetrics(preLabel.getFont());
	        if(isSub) {
	        	preLabel.setSize(fm.stringWidth(preLabel.getText()) + 10, fm.getHeight());
	        } else {
	        	preLabel.setSize(MAX_LABEL_LENGTH, fm.getHeight());	        	
	        }
	        preLabel.setLocation(LEFT_MARGIN, 1);
	        preLabel.setVisible(false);
	        this.add(preLabel);
	        preLabel.setVisible(true);
	    }
		
		isResizing = false;
	}
	/**
	 * Generates the post-label and help button and sets it after the specified component
	 * It returns the current last component.
	 */
	protected Component generatePostItems(Component after) {
	    if(parameter != null) {
	        String postlbl = parameter.getPostLabel();
	        if(postlbl != null) {
			    postLabel = new FixedLabel(postlbl);
			    postLabel.setFont(ParameterConstants.LABEL_FONT);
		        FontMetrics fm = postLabel.getFontMetrics(postLabel.getFont());
		        postLabel.setSize(fm.stringWidth(postLabel.getText()) + 10, fm.getHeight());
		        postLabel.setLocation(after.getLocation().x + after.getSize().width + 10, 1);
		        this.add(postLabel);
		        after = postLabel;
	        }
	        
	        String help = parameter.getHelpText();
	        if((help != null) && (!help.equals(""))) {
			    helpButton = new HelpButton(help);
			    helpButton.setLocation(after.getLocation().x + after.getSize().width + 10, 1);
		        helpButton.setVisible(false);
		        this.add(helpButton);
		        after = helpButton;
	        }
	    }
	    
	    return after;
	    
	}
	
	protected void addParameter(Hashtable parameters, Object o) {
	    if(parent != null) {
	        parameters.put(parameter.getName() + parent.getSequenceString(this), o);
	    } else {
	        parameters.put(parameter.getName(), o);	        
	    }
	}

	/**
	 * @roseuid 42551A7500A7
	 */
	public void isFocussed(ParameterComponentIF component) {
	    if((focussedComponent != null) && (focussedComponent != component)) {
	        focussedComponent.unFocus();
	    }
		focussedComponent = component;
	}

	/**
	 * @roseuid 42551A7500ED
	 */
	public void unFocus() {
		if (parent != null) {
			this.setBackground(parent.getColor());
			this.setBorders(BorderedPanel.NONE);
			
		}
		if(preLabel != null) {
		    preLabel.setBackground(getBackground());
		}
		if(postLabel != null) {
		    postLabel.setBackground(getBackground());
		}
		if(helpButton != null) {
	        helpButton.setVisible(false);
		    helpButton.repaint();
		}
	}

	/**
	 * @roseuid 42551A750101
	 */
	public Color getColor() {
		return getBackground();
	}

	/**
	 * @roseuid 42551A75010B
	 */
	public void addParameters(Hashtable parameters) {
		ParameterComponentIF com;
		for(int i = 0; i < subComponents.size(); i++) {
			com = (ParameterComponentIF) subComponents.elementAt(i);
			com.addParameters(parameters);
		}
	}

	/**
	 * @roseuid 42551A75015B
	 */
	public String getSequenceString(ParameterComponentIF component) {
		return "";
	}

	/**
	 * @roseuid 42551A750165
	 */
	public void registerComponent(ParameterComponentIF component) {
		subComponents.addElement(component);
	}

	/**
	 * @return Returns the parent.
	 */
	public ParameterComponentIF getParentCom() {
		return parent;
	}

	/**
	 * @param parent
	 *            The parent to set.
	 */
	public void setParentCom(ParameterComponentIF parent) {
		this.parent = parent;
	}
	
	protected void isFocussed() {
		if (parent != null) {
			if(!isSub) {
				/* The color is the other color than the parent */
				if(parent.getColor() == SELECTED_COLOR_1) {
					this.setBackground(SELECTED_COLOR_2);
				} else {
					this.setBackground(SELECTED_COLOR_1);
				}
	
				this.setBorders(BorderedPanel.ALL);
				parent.isFocussed(this);
				if(preLabel != null) {
				    preLabel.setBackground(getBackground());
				}
				if(postLabel != null) {
				    postLabel.setBackground(getBackground());
				}
				if(helpButton != null) {
			        helpButton.setVisible(true);
				    helpButton.repaint();
				}
			} else {
				parent.isFocussed(this);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent evt) {
		isFocussed();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see fi.dwo.parameters.gui.ParameterComponentIF#getComponent()
	 */
	public Component getComponent() {
		return this;
	}

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */
    public void reset() {
		ParameterComponentIF com;
		for(int i = 0; i < subComponents.size(); i++) {
			com = (ParameterComponentIF) subComponents.elementAt(i);
			com.reset();
		}
    }

	/* (non-Javadoc)
	 * @see fi.dwo.parameters.gui.ParameterComponentIF#setColor(java.awt.Color)
	 */
	public void setColor(Color c) {
		this.setBackground(c);
		if(preLabel != null) {
		    preLabel.setBackground(c);
		}
		if(postLabel != null) {
		    postLabel.setBackground(c);
		}
	}

	/**
     * Returns the <i>current</i> size as the minimum size.
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return super.getSize();
    }

    /**
     * Returns the <i>current</i> size as the preferred size.
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return super.getSize();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        if(! isResizing) {
            this.setVisible(false);
            isResizing = true;
            Dimension oldSize, newSize;
            LayoutManager layout = getLayout();
            oldSize = getSize();
            if(layout != null) {
                newSize = layout.preferredLayoutSize(this);
            } else {
                newSize = oldSize;
            }
            if( oldSize.height != newSize.height || oldSize.width != newSize.width)
            {
                setSize(newSize);
                validate();
            }
            this.setVisible(true);
            isResizing = false;
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#setSequenceLabel(int)
     */
    public void setSequenceLabel(int nr) {
        preLabel.setText(parameter.getPreLabel() + " " + nr + ":");
    }
    
    public String toString() {
        String s = "";
        if(parameter != null) {
            s += "This: " + parameter.getPreLabel() + "; " + getLocation() + "; " + getSize() + " {";
        } else {
            s += "This: null {";
        }
        
        ParameterComponent pc;
        for(int i = 0; i < subComponents.size(); i++) {
            pc = (ParameterComponent) subComponents.elementAt(i);
            s += "[" + pc.toString() + "],";
        }
        
        s += "}";
        
        return s;
    }

}
