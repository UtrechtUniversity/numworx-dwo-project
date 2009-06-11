package fi.dwo.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.Guest;
import fi.dwo.client.domain.LessonGroup;
import fi.dwo.client.domain.ResultScore;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import junit.framework.TestCase;

public class ClipboardExportTest extends TestCase {
	
	/**
	 * Test method.
	 * @throws InterruptedException 
	 * @throws PersistenceException 
	 * @throws IOException 
	 * @throws UnsupportedFlavorException 
	 */
	public  void testExportSchool() throws InterruptedException, PersistenceException, UnsupportedFlavorException, IOException {
		Exporter clip = ClipboardExport.instance();
		School[] schools = (School[]) PersistenceFacade.instance().get(School.class);
		clip.export(schools);
		dumpClipboard();
	}

	public void testExportUserResultList() throws UnsupportedFlavorException, IOException
	{
		Exporter clip = ClipboardExport.instance();
		UserResultList[] results = new UserResultList[1];
		UserResultList   result = new UserResultList();
		results[0] = result;
		ResultScore scores[] = new ResultScore[1];
		ResultScore score = new ResultScore();
		result.setResultScore(scores);
		scores[0] = score;
		score.setScore(75);
		Course lesson = new Course();
		lesson.setName("course");
		score.setLessonGroup(lesson);
		score.setUserGroup(Guest.instance());
		clip.export(results);
		
		dumpClipboard();
		
	}

	public void testExportFrame() throws PersistenceException, InterruptedException {
		Exporter clip = ClipboardExport.instance();
		((ClipboardExport) clip).useExportFrame();
		School[] schools = (School[]) PersistenceFacade.instance().get(School.class);
		clip.export(schools);
		Thread.sleep(5000*60);
		
	}
	
	
	private void dumpClipboard() throws UnsupportedFlavorException, IOException {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		System.out.println(clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
		
	}
}
