package nl.uu.fi.dwo.rest.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelUtilTest {

	String uuid() { return UUID.randomUUID().toString(); }
	DomStudentModelStructure structure;
	StudentModelUtil util;
	List<DomStudentModelObj> objs;
	
	@Before
	public void setUp() throws Exception {
		structure = new DomStudentModelStructure();
		DomStudentModelCategory cat = new DomStudentModelCategory();
		structure.setCategories(Collections.singletonList(cat));
		structure.setInfo(new DomStudentModelContextInfo());
		structure.getInfo().setId(uuid());
		cat.setInfo(new DomStudentModelContextInfo());
		cat.getInfo().setId(uuid());
		objs = new ArrayList<>();
		cat.setObjectives(objs);		
		util = new StudentModelUtil();
	}

	@After
	public void tearDown() throws Exception {
	}

	DomStudentModelObj create() { 
		DomStudentModelObj o = new DomStudentModelObj();
		o.setInfo(new DomStudentModelContextInfo());
		o.getInfo().setId(uuid());
		o.setObjectives(null);
		return o;
	}
	
	@Test
	public void testSetStudentModelStructure() {
		createObjs();
		util.setStudentModelStructure(structure);
		
		assertEquals(3, util.foreknowledge.size());
		
	}

	private void createObjs() {
		DomStudentModelObj o1,o2,o3;
		o1 = create();
		o2 = create();
		o3 = create();
		
		String i1,i2,i3;
		i1 = o1.getInfo().getId();
		i2 = o2.getInfo().getId();
		i3 = o3.getInfo().getId();
		o1.getInfo().setVoorkennis(Collections.singletonList(i2));
		o2.getInfo().setVoorkennis(Collections.singletonList(i3));
		objs.add(o1);
		objs.add(o2);
		objs.add(o3);
	}

	@Test
	public void testSetStudentModelScore() {
		createObjs();
		DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
		util.setStudentModelStructure(structure);
		util.setStudentModelScore(score);
		
		assertEquals(3, util.scores.size());
		
		
	}

	@Test
	public void testCalculate() {
		createObjs();
		DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
		List<DomStudentModelObjectiveScore> list = score.getCategories().get(0).getObjectives();
		list.forEach(item -> {item.setScore(0.8); item.setChildren(null);});
		util.setStudentModelStructure(structure);
		util.setStudentModelScore(score);
		DomStudentModelStructureScore result = util.calculate();
		
		assertEquals(3L, result.getTotalCount());
	}

	@Test
	public void testIsCorrect() {
		DomStudentModelObjectiveScore s = new DomStudentModelObjectiveScore();
		String uuid = uuid();
		s.setId(uuid);
		util.scores.put(uuid, s);
		boolean b = util.isCorrect(uuid);
		assertFalse("no score", b);
		s.setScore(0.8);
		b = util.isCorrect(uuid);
		assertTrue("score" , b);
	}

}
