package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.ScoManagementPanel.IconDialog;

public class LogoIconAction extends AbstractAction implements Action {

	Logger LOG = Logger.getLogger(getClass().getName());
	private final Icon scoIcon = new ImageIcon(DwoHelper.getResourceImage("resources/activiteit_numworx.png"));
	private IconDialog iconDial;
	private boolean update;
	private byte[] imageData;
	private String imageUrl;
	
	class LogoIcon implements Icon {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.green);
			g.fillOval(x, y, getIconWidth(), getIconHeight());			
		}

		@Override
		public int getIconWidth() {
			return 60;
		}

		@Override
		public int getIconHeight() {
			return 60;
		}
		
	}

	public LogoIconAction(Sco sco) {
		this();
		PersistenceId id = PersistentScoContext.buildPersistenceId(Long.valueOf(sco.getScoID()));
		DomScoContext domScoId = new DomScoContext();
		domScoId.setId(id);
		PublicScoContextManager.getAsync(domScoId, DWO.getDwoProfile(), null)
		.then(p ->
		{
			String u = p.getValue().getImage();
			if( u != null) {
				final URL url = new URL(u);
				SwingUtilities.invokeLater( () -> {
				Image img = Toolkit.getDefaultToolkit().getImage(url);
				ReducedImageIcon icon = new ReducedImageIcon(img);
		        setIcon(icon);
				}
				);
			}
			return null;
		} ,
		 p ->
			LOG.log(Level.INFO, "image failed", p.getFailure())
		);
	
	}

	public LogoIconAction(Course course) {
		this();
	}
	
	public LogoIconAction() {
		super("");
        setIcon(scoIcon);
	}

	private void setIcon(Icon icon) {
		putValue(SMALL_ICON, icon);
	}

	public void importScoLogo(ActionEvent e) throws IOException {
		String naam;
        Image reduced;
	       if (iconDial == null) iconDial = new IconDialog();
	        iconDial.setDialogTitle(TextMapper.format(TextMapper.GUIS_LOAD_LOGO, new Object[]{toString()}));
            iconDial.setCurrentDirectory(DwoHelper.getCurrentDirectory());
	        int r = iconDial.showOpenDialog((Component) e.getSource());
	        File file = iconDial.getSelectedFile();
			naam = (r == JFileChooser.CANCEL_OPTION || file == null) ? null : file.getName();
	        if(naam == null && r == JFileChooser.APPROVE_OPTION) {
	            DwoHelper.setCurrentDirectory(iconDial.getCurrentDirectory());
	        	naam = iconDial.url.getText();
	        	URL url = new URL(naam);
	        	setImageUrl(naam);
	        	BufferedImage img0 = ImageIO.read(url);
				byte[] data = reduceToFit(img0);
	            reduced = Toolkit.getDefaultToolkit().createImage(data);
	            setImageData(data);
	            setIcon(new ImageIcon(reduced));
	        	setUpdate(true);
	        	return;
	        }
	        if (naam != null) {
	            BufferedImage img0 = ImageIO.read(file);
				byte[] data = reduceToFit(img0);
	            reduced = Toolkit.getDefaultToolkit().createImage(data);
	            setImageData(data);
	            setIcon(new ImageIcon(reduced));
	            setImageUrl("");
	            setUpdate(true);
	        }
	}

	private byte[] reduceToFit(BufferedImage img0) throws IOException {
		BufferedImage img = img0;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Image reduced;
		int w = img.getWidth();
		int h = img.getHeight();
		if (w <= 252 && h <= 160) {
		    reduced = img;
		} else {
			float scalex = w/252f;
			float scaley = h/160f;
			float scale = Math.max(scalex, scaley);
			w = Math.round(w/scale);
			h = Math.round(h/scale);
		    reduced = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		}
		if (reduced instanceof BufferedImage) {
		    img = (BufferedImage) reduced;
		} else {
		    img = new BufferedImage(Math.min(252, img.getWidth()), Math.min(160, img.getHeight()), BufferedImage.TYPE_INT_ARGB);
		    img.createGraphics().drawImage(reduced, 0, 0, null);
		}
		ImageIO.write(img, "png", output);
		output.close();
		byte[] data = output.toByteArray();
		return data;
	}


	/**
	 * @return the imageData
	 */
	byte[] getImageData() {
		return imageData;
	}

	/**
	 * @param imageData the imageData to set
	 */
	void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	/**
	 * @return the imageUrl
	 */
	String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			importScoLogo(ev);
		} catch(Exception ex) {
			LOG.log(Level.WARNING, "import failed", ex);
		}
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
}