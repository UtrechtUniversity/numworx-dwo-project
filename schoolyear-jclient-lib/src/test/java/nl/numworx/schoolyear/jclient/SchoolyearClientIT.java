package nl.numworx.schoolyear.jclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import nl.numworx.schoolyear.jclient.dto.Content;
import nl.numworx.schoolyear.jclient.dto.Element;
import nl.numworx.schoolyear.jclient.dto.ExamDTO;
import nl.numworx.schoolyear.jclient.dto.Vault;
import nl.numworx.schoolyear.jclient.dto.WebPageEntireDomain;
import nl.numworx.schoolyear.jclient.dto.Workspace;

public class SchoolyearClientIT {

	@Test
	public void test() throws IOException {
		SchoolyearClient client = new SchoolyearClient.Builder().build();
		ExamDTO input = new ExamDTO();
		input.display_name = "test examen";
		input.start_time = new Date();
		input.end_time = new Date(System.currentTimeMillis()+1000L);
		input.expected_workspaces = 23;
		Workspace workspace = new Workspace();
		workspace.vault = new Vault();
		workspace.vault.content = new Content();
		Element element = new Element();
		element.type = "web_page_entire_domain";
		element.url_entire_domain = new WebPageEntireDomain();
		element.url_entire_domain.url = "https://numworx.nl";
		String uuid = UUID.randomUUID().toString();
		workspace.vault.content.elements = Collections.singletonMap(uuid, element);
		input.workspace = workspace;
		
		ExamDTO result = client.createExam(input);
		
		assertNotNull(result);
	}

}
