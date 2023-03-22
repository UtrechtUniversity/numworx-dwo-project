package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import org.junit.Assert;

public class ClassManagerTest {

	private Validator validator;
	private ValidatorFactory factory;
	@Before
	public void before() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@After
	public void after() {
		factory.close();
	}
	@Test
	public void testokay() {
		PersistentSchoolClass sc;
		sc = new PersistentSchoolClass(1L, 1L, "okay");
		Set<ConstraintViolation<PersistentSchoolClass>> result = validator.validate(sc);
		Assert.assertTrue(result.isEmpty());
	}
	@Test
	public void testempty() {
		PersistentSchoolClass sc;
		sc = new PersistentSchoolClass(1L, 1L, "");
		Set<ConstraintViolation<PersistentSchoolClass>> result = validator.validate(sc);
		System.out.println(result);
		Assert.assertFalse(result.isEmpty());
	}

}
