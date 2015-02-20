package fi.dwo.dwojapplet.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.util.Properties;

import fi.dwo.dwojapplet.domain.DwoHelper;

class PrinterExport extends Exporter {

	private String jobtitle = "";
	private Properties props = new Properties();
	
	
	private class PrinterBuffer extends ExportBuffer {

		private PrintJob job;
		Dimension size;
		Graphics g;
		int y;
		
                @Override
		protected void export() {
			job.end();
		}
				
                @Override
		protected void export(String[] line) {
			int x = 0;
			y += g.getFontMetrics().getHeight();
			for (int i = 0; i < line.length; i++) {
				g.drawString(line[i], x, y);
				x += size.width/line.length;
			}
		}
		
                @Override
		protected void exportHeader(String[] line)
		{
			job = Toolkit.getDefaultToolkit().getPrintJob(DwoHelper.getFrameForComponent(DwoHelper.getApplet()), jobtitle, props);
			size = job.getPageDimension();
			g = job.getGraphics();
			super.exportHeader(line);
		}
	}

	private static Exporter _instance;

	/**
	 * 
	 */
	private PrinterExport() {
		super();

	}

        @Override
	protected ExportBuffer createExportBuffer() {
		return new PrinterBuffer();
	}

	/**
     * Singleton pattern
     * @return the exporter.
     */
	public static Exporter instance() {
		if(_instance == null)
			_instance = new PrinterExport();
		return _instance;
	}

}
