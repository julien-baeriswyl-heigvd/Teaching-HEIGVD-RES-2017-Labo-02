/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Nathan
 */
public class RouletteV2Blade7foldTest {
    
    @Rule
    public EphemeralClientServerPair roulettePair2 = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
    
    /**
     * Test of clearDataStore method, of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Ignore
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void theMethodShouldClearCorrectlyTheStudentsInTheServer() throws IOException {
        System.out.println("Clear Data Store");
        RouletteV2ClientImpl instance = new RouletteV2ClientImpl();
        instance.loadStudent("JeSaisPas");
        instance.loadStudent("QuelNom");
        instance.loadStudent("MettreIci");
        instance.clearDataStore();
        List<Student> result = instance.listStudents();
        assertTrue(result.isEmpty());
    }

    /**
     * Test of listStudents method, of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Ignore
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void theServerShouldReturnTheCorrectListOfStudents() throws IOException {
        System.out.println("List Students");
        RouletteV2ClientImpl instance = new RouletteV2ClientImpl();
        StudentsList sl = new StudentsList();
        List<Student> expResult = sl.getStudents();
        List<Student> result = instance.listStudents();
        assertEquals(expResult, result);
    }
    
    /**
     * Test the correct version of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair2.getClient().getProtocolVersion());
    }
    
    /**
     * Test of info command method with number of students, 
     * of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Ignore
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void theServerShouldReturnTheCorrectNumberOFStudents() throws IOException {
        RouletteV2ClientImpl usingINFO = new RouletteV2ClientImpl();
        int nbOfStudents = roulettePair2.getClient().getNumberOfStudents();
        int result = usingINFO.getNumberOfStudents();
        assertEquals(nbOfStudents, result);
    }
}
