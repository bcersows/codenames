package de.bcersows.codenames.backend.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPopulateTestData() throws Exception {
        assertFalse("Null is invalid.", GameService.isGameNameValid(null));
        assertFalse("Empty is invalid.", GameService.isGameNameValid(""));
        assertFalse("4 chars or less is invalid.", GameService.isGameNameValid("1234"));
        assertFalse("Invalid chars.", GameService.isGameNameValid("validnameoh%no"));
        assertFalse("Invalid chars.", GameService.isGameNameValid("valid name right"));
        assertFalse("more than 20 chars.", GameService.isGameNameValid("123456789012345678901"));

        assertTrue("5 chars.", GameService.isGameNameValid("12345"));
        assertTrue("Cool name.", GameService.isGameNameValid("thisnameisv"));
        assertTrue("20 chars.", GameService.isGameNameValid("12345678901234567890"));
    }

}
