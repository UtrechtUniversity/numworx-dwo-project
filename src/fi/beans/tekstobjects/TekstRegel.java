package fi.beans.tekstobjects;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class TekstRegel extends TekstElement implements MouseListener, MouseMotionListener,KeyListener 
{	
	private FontMetrics fm;
	private boolean caretVisible = false;
	private KnipperDraad kd;
	private boolean selectable = true;
	private boolean editable = true;
	private boolean hasFocus = true;
	private boolean selected = false;
	private boolean aan = true;
	private int caretX = 0;
	private int caretPos = 0;
	private int kc;
	
	private int startx = 0;
	private int starty = 0;
	
	private boolean eersteKeer=true;
	private boolean terug=true;
		
	Color bgColor = new Color(255,255,255);
	
	public TekstRegel(TekstVak tv)
	{	bgColor = getBackground();
		tekstVak = tv;
		editable = tv.isEditable();
		selectable = tv.isSelectable();
		
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		//addKeyListener(this);
		//addFocusListener(this);
		
		setFont(tv.getFont());
		fm = getFontMetrics(getFont());
		setSize(fm.getAscent()/2,7*fm.getFont().getSize()/5);
		ashoogte = fm.getAscent()/2;
		
	}
	
	public void setFont(Font f)
	{	super.setFont(f);
		fm = getFontMetrics(getFont());
		int b=1;
		int h1=0;
		int h2=0;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	int hoogte = getComponent(i).getSize().height;
			int ash = ((TekstElement)getComponent(i)).ashoogte;
			if(ash>h1)h1=ash;
			if(hoogte-ash>h2)h2=hoogte-ash;
		}
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	getComponent(i).setLocation(b,h1-((TekstElement)getComponent(i)).ashoogte);
			b += getComponent(i).getSize().width;
		}
		if(getComponentCount()>0)
		{	setSize(b,h1+h2 + 2*fm.getFont().getSize()/5);
			ashoogte = h1;
		}
		else 
		{	setSize(fm.getAscent()/2, 7*fm.getFont().getSize()/5);
			ashoogte = fm.getAscent()/2;
		}
	}
		
	public void paint(Graphics g)
	{	g.setColor(getBackground());
		if(selected)g.setColor(Color.black);
		g.fillRect(0,0,getSize().width-1,getSize().height-1);
		
		if( getComponentCount() ==0)//hasFocus ||
		{	g.setColor(new Color(200,200,200));
			g.drawRect(0,0,getSize().width-1,getSize().height-1);
		}
		super.paint(g);
		//Formuletekens worden direct getekend en niet binnen het eigen component.
		//Bij 'italic' fonts vallen ze namelijk soms buiten het component.
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	if(getComponent(i)instanceof TekstTeken)
			{	TekstTeken tt = (TekstTeken)getComponent(i);
				tt.paint(g,tt.getLocation().x,tt.getLocation().y);
			}
		}
		g.setColor(Color.black);
		if(caretVisible && editable && aan)
		{	g.drawLine(caretX,0,caretX,getSize().height);
		}
	}
	
	public void setEditable(boolean b)
	{	editable = b;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	((TekstElement)getComponent(i)).setEditable(b);
		}	
	}
	
	public void setSelectable(boolean b)
	{	if(selectable && !b)
		{	removeMouseListener(this);
			removeMouseMotionListener(this);
		}
		if(!selectable && b)
		{	addMouseListener(this);
			addMouseMotionListener(this);
		}
		selectable = b;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	((TekstElement)getComponent(i)).setSelectable(b);
		}
	}
	
	public void setSelected(boolean b)
	{	//if(selected!=b)
		{	if(b)caretVisible = false;
			selected = b;
			for(int i=0 ; i<getComponentCount()  ; i++)
			{	((TekstElement)getComponent(i)).setSelected(b);
			}
			repaint();
		}
	}
	
	public boolean isSelected()
	{	return selected;
	}
	
	public void zetMaat()
	{	int b=1;
		int h1=0;
		int h2=0;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	int hoogte = getComponent(i).getSize().height;
			int ash = ((TekstElement)getComponent(i)).ashoogte;
			if(ash>h1)h1=ash;
			if(hoogte-ash>h2)h2=hoogte-ash;
		}
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	getComponent(i).setLocation(b,h1-((TekstElement)getComponent(i)).ashoogte);
			b += getComponent(i).getSize().width;
		}
		if(getComponentCount()>0)
		{	setSize(b,h1+h2 + 2*fm.getFont().getSize()/5);
			ashoogte = h1;
		}
		else 
		{	setSize(fm.getAscent()/2, 7*fm.getFont().getSize()/5);
			ashoogte = fm.getAscent()/2;
		}
		
		if(getParent()instanceof TekstElement)((TekstElement)getParent()).zetMaat();
	}

	public void neemFocus(String richting, TekstElement fe)
	{	requestFocus();
		tekstVak.zetActieveRegel(this);
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	if(getComponent(i)==fe)
			{	if(richting.equals("rechts"))
				{	caretPos = i+1;
					caretX = fe.getLocation().x + fe.getSize().width -1;
				}
				else
				{	caretPos = i;
					caretX = fe.getLocation().x;
				}
			}
		}
	}
	
	public void neemFocus(String richting)
	{	requestFocus();
		tekstVak.zetActieveRegel(this);
		if(richting.equals("rechts"))
		{	caretPos = 0;
			caretX = 0;
		}
		if(richting.equals("links"))
		{	caretPos = getComponentCount();
			if(getComponentCount()==0)caretX=0;
			else caretX = getSize().width - 1;
		}
	}
	
	public void setCaret(int pos)
    {	caretPos = pos;
    	int x = 0;
        for(int i=0 ; i<pos  ; i++)
		{	x = x + getComponent(i) .getSize().width;
		}
		caretX = x;
		repaint();
	}
	
	public void setCaretVisible(boolean b)
	{	if (b)
		{	if(kd!=null)
			{	kd.maakDood();
				kd=null;
			}
			kd = new KnipperDraad();
			kd.start();
			caretVisible = true;
			hasFocus = true;
			repaint();
		} 
		else
		{	if(kd!=null)
			{	kd.maakDood();
				kd=null;
			}
			caretVisible = false;
			hasFocus = false;
			repaint();
		}
	}	
	public void setCaretPosition(int x)
    {	int posX = 0;
		int grensX = 0;
        for(int i=0 ; i<getComponentCount()  ; i++)
		{	grensX = posX + getComponent(i) .getSize().width/2;
			if(x<grensX)
			{	caretPos = i;
				caretX = posX;
				break;
			}
			else
			{	caretPos = i+1;
				caretX = posX + getComponent(i) .getSize().width;
			}
			posX += getComponent(i) .getSize().width;
		}
		deSelect();
		tekstVak.setCaretPosition(this,caretPos);
    }
	
	public void setSelection(int x1, int x2)
    {	int posX = 0;
		int grensX = 0;
		int comp1=0;
		int comp2=-1;
        for(int i=0 ; i<getComponentCount()  ; i++)
		{	grensX = posX + getComponent(i) .getSize().width;
			if(x1<grensX)
			{	comp1 = i;
				break;
			}
			else
			{	comp1 = i+1;
			}
			posX += getComponent(i) .getSize().width;
		}
		posX = 0;
		grensX = 0;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	grensX = posX;// + getComponent(i) .getSize().width/2;
			if(x2<grensX)
			{	comp2 = i-1;
				caretPos = i;
				caretX = posX;
				break;
			}
			else
			{	comp2 = i;
				caretPos = i+1;
				caretX = posX + getComponent(i) .getSize().width;
			}
			posX += getComponent(i) .getSize().width;
			
		}
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	if(i>=comp1 && i<=comp2)
			{	((TekstElement)getComponent(i)).setSelected(true);
				caretVisible = false;
			}
			else ((TekstElement)getComponent(i)).setSelected(false);
		}
		repaint();
    }
	
	public void deSelect()
	{	//caretVisible = true;
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	((TekstElement)getComponent(i)).setSelected(false);
		}
	}
	
	/*public int geefLengteEersteW()
	{	int lengte = 0;
		for(int i=0 ; i<getComponentCount(); i++)
		{	if(((TekstElement)getComponent(i)).isSpatie())
			{	return lengte;
			}
			else
			{	lengte += getComponent(i).getSize().width;
			}
		}
		return lengte;
	}
	
	public void wrapTerug()
	{	TekstElement[] overflow;
		int aantalLetters = 0;
		for(int i=0 ; i<getComponentCount(); i++)
		{	if(((TekstElement)getComponent(i)).isSpatie())
			{	break;
			}
			else
			{	aantalLetters++;
			}
		}
		overflow = new TekstElement[aantalLetters];
		for(int i=0 ; i<aantalLetters; i++)
		{	overflow[i] = (TekstElement)getComponent(0);
			remove(getComponent(0));
		}
		//zetMaat();
		tekstVak.wrapTerug(this,overflow);		
		
	}*/
	
	public void deleteSelection()
	{	for(int i=getComponentCount() -1 ; i>-1 ; i--)
		{	if(((TekstElement)getComponent(i)).isSelected())
			{	caretPos--;
				caretX -= getComponent(i) .getSize().width;
				remove(i);
			}
		}
		caretVisible = true;
		zetMaat();
		startx = 0;
		starty = 0;
	}
	
	public void removeAll()
	{	super.removeAll();
		caretPos = 0;
		caretX = 0;
		zetMaat();
	}
	
	public void insert(TekstElement te)
	{	//if(pos<=getComponentCount())
		{	add(te,caretPos);
			zetMaat();
			caretX += getComponent(caretPos) .getSize().width;
			caretPos++;
		}
	}
	
	/*public void wrap()
	{	System.out.println("wrap");
		TekstElement[] overflow;
		int wrapPositie = getComponentCount();
		int spatieIndex1 = 0;
		int spatieIndex2 = 0;
		TekstElement te1 = null;
		TekstElement te2 = null;
		for(int i=getComponentCount()-1 ; i>-1 ; i--)
		{	TekstElement te = (TekstElement)getComponent(i);
			if(((TekstElement)getComponent(i)).isSpatie() && te.getLocation().x > tekstVak.getSize().width)
			{	te1 = te;
				spatieIndex1 = i;
			}
			else if (((TekstElement)getComponent(i)).isSpatie() && te.getLocation().x <= tekstVak.getSize().width)
			{	if(te2==null)
				{	te2 = te;
					spatieIndex2 = i;
				}
			}
		}
		if(te1==null && te2!=null)
		{	wrapPositie = spatieIndex2;
		}
		else if(te1!=null)
		{	wrapPositie = spatieIndex1;
		}
		else return;
		int max = getComponentCount();
		overflow = new TekstElement[max - wrapPositie];
		int aantalOverflow = 0;
		for(int i=wrapPositie ; i<max ; i++)
		{	if(i>wrapPositie)
			{	overflow[aantalOverflow] = (TekstElement)getComponent(wrapPositie);
				aantalOverflow++;
			}
			remove(wrapPositie);
			if(caretPos > getComponentCount())caretPos--;
		}
		caretVisible = true;
		zetMaat();
		//tekstVak.wrap(this,overflow);
	}
	
	public boolean caretAanEind()
	{	return caretPos >= getComponentCount();
	}*/
	
	public void mousePressed(MouseEvent e)
	{	if(selectable)
		{	tekstVak.requestFocus();
			startx = e.getX();
			starty = e.getY();
			if(e.getSource()==this)setCaretPosition(e.getX());
		}		
	}
	
	public void mouseDragged(MouseEvent e)
	{	if(selectable)
		{	/*if(e.getX()<0 || e.getX()>getSize().width || e.getY()<0 || e.getY()>getSize().height)
			{	terug = false;
				
				if(getParent().getParent()instanceof TekstRegel)
				{	MouseEvent en = new MouseEvent((TekstRegel)getParent().getParent(),e.getID(),e.getWhen(),e.getModifiers(), e.getX()+getLocation().x+getParent().getLocation().x,e.getY()+getLocation().y+getParent().getLocation().y,1,false);
					
					if(eersteKeer)
					{	((TekstRegel)getParent().getParent()).mousePressed(en);
						eersteKeer=false;
					}
					((TekstRegel)getParent().getParent()).mouseDragged(en);
				}
			}*/
			if(e.getY()>getSize().height)
			{	TekstRegel volg = tekstVak.geefVolgendeRegel(this);
				setSelection(startx,this.getSize().width);
				MouseEvent en = new MouseEvent(volg,e.getID(),e.getWhen(),e.getModifiers(), 0,0,1,false);
				MouseEvent ed = new MouseEvent(volg,e.getID(),e.getWhen(),e.getModifiers(), e.getX(),e.getY()-getSize().height,1,false);
				
				if(eersteKeer)
				{	volg.mousePressed(en);
					eersteKeer=false;
				}
				volg.mouseDragged(ed);	
			}
			else
			{	terug = true;
				tekstVak.setSelected(false);
				tekstVak.requestFocus();
				tekstVak.zetActieveRegel(this);
				if(e.getX() < startx)setSelection(e.getX(),startx);
				else setSelection(startx,e.getX());
			}
		}
	}
		
	public void mouseClicked(MouseEvent e)
	{	if(selectable)
		{	int clickCount = e.getClickCount();
			if(clickCount>1)
			{	for(int i=0 ; i<getComponentCount()  ; i++)
				{	((TekstElement)getComponent(i)).setSelected(true);
				}
				repaint();
				caretVisible = false;
				caretPos = getComponentCount() ;
				if(getComponentCount() >0)caretX = getSize().width-1;
				else caretX = 0;
			}
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{	eersteKeer=true;
		startx = 0;
		starty = 0;
	}
		
	public void mouseEntered(MouseEvent e){;}
	public void mouseMoved(MouseEvent e){;}
	public void mouseExited(MouseEvent e){;}
	
	public void keyPressed(KeyEvent e)
	{   if (editable)
        {   kc = e.getKeyCode();
           if (kc == KeyEvent.VK_LEFT)
            {   if (caretPos > 0 && getComponent(caretPos-1)instanceof TekstTeken)
                {   caretPos--;
                    caretX -= getComponent(caretPos) .getSize().width;
					deSelect();
                }
				else if(!(caretPos > 0))
				{	if(getParent() instanceof TekstElement)
					{	((TekstElement)getParent()).neemFocus("links",this);
					}
				}
				else
				{	((TekstElement)getComponent(caretPos-1)).neemFocus("links");
				}
			}
            else if (kc == KeyEvent.VK_RIGHT)
            {   if (caretPos < getComponentCount()&& getComponent(caretPos)instanceof TekstTeken)
                {   caretPos++;
                    caretX += getComponent(caretPos-1).getSize().width;
					deSelect();
				}
				else if (!(caretPos < getComponentCount()))
				{	if(getParent() instanceof TekstElement)
					{	((TekstElement)getParent()).neemFocus("rechts",this);
					}
				}
				else
				{	((TekstElement)getComponent(caretPos)).neemFocus("rechts");
				}
            }
			else if (kc == KeyEvent.VK_HOME)
            {   caretPos = 0;
                caretX = 0;
                deSelect();
            }
            else if (kc == KeyEvent.VK_END)
            {   caretPos = getComponentCount() ;
				if(getComponentCount() >0)caretX = getSize().width-1;
				else caretX = 0;
				deSelect();
			}
            else if (kc == KeyEvent.VK_DELETE)
            {	if(caretVisible)
				{	if(caretPos<getComponentCount() )
					{	remove(caretPos);
						zetMaat();
					}
				}
				else
				{	deleteSelection();
				}
            } 
			else if (kc == KeyEvent.VK_BACK_SPACE)
            {   if(caretVisible)
				{	if(caretPos>0)
					{	caretPos--;
						caretX -= getComponent(caretPos) .getSize().width;
						remove(caretPos);
						zetMaat();
					}
				}
				else
				{	deleteSelection();
				}
	  		}
	  		
         	repaint();
         	
		}
	}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e)
    {	if (editable)
		{   // kc initialized by keyPressed
            int kt = e.getKeyChar();
            if (kt == KeyEvent.VK_ENTER)
            {	tekstVak.finish();
			}
			else if ((kt != KeyEvent.VK_ESCAPE) &&
	                (kt != KeyEvent.VK_BACK_SPACE) &&
               		(kc != KeyEvent.VK_ENTER)
                    && (kc != KeyEvent.VK_SHIFT)
                    
                   )
      		{	if(!caretVisible)
				{	deleteSelection();
				}
				add(new TekstTeken((char)kt),caretPos);
				caretX += getComponent(caretPos) .getSize().width;
				caretPos++;
				int nr = caretPos;
            } 
            
            zetMaat();   
            repaint();
		}
	}
	
	/*public void focusGained(FocusEvent e)
    {   if (selectable)
		{	if(kd!=null)
			{	kd.maakDood();
				kd=null;
			}
			kd = new KnipperDraad();
			kd.start();
			caretVisible = true;
			hasFocus = true;
			repaint();
		}  
	}
	public void focusLost(FocusEvent e)
	{   if (selectable)
		{	if(kd!=null)
			{	kd.maakDood();
				kd=null;
			}
			//tekstVak.focusLost(this);
			//caretVisible = false;
			hasFocus = false;
			repaint();
		}  
	}*/
	
	
	public String toString()
	{	String s = "";
		for(int i=0 ; i<getComponentCount()  ; i++)
		{	s = s + ((TekstElement)getComponent(i)).toString();
		}
		return s;
	}
	
	class KnipperDraad extends Thread 
	{	boolean dood = false;
		public void run()
		{	while(!dood)
			{	int delay = 500;
				long t = System.currentTimeMillis();
				try
				{	t = t+delay;
					sleep(Math.max(1, t-System.currentTimeMillis()));
				}
    			catch(InterruptedException e)    // geen ;
				{   };
				if(aan)aan = false;
				else aan = true;
				repaint();
			}
		}
		public void maakDood()
		{	dood = true;
		}
	}
}


