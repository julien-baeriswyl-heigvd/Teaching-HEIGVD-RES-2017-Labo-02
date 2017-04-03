/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.client.RouletteV2ClientImpl;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
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

public class RouletteV2MoufbluTest
{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"Moufblu", "jimmyVerdasca"})
    public void theServerShouldBeAbleToClearData() throws IOException
    {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"Moufblu", "jimmyVerdasca"})
    public void theServerShouldGiveAnEmptyList() throws IOException
    {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        List students = client.listStudents();
        assertEquals(0, students.size());
    }

    @Test
    @TestAuthor(githubId = {"Moufblu", "jimmyVerdasca"})
    public void theServerShouldGiveAListContainingSutdents() throws IOException
    {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        List<Student> studentsToAdd = new LinkedList<>();
        studentsToAdd.add(new Student("Nadir"));
        studentsToAdd.add(new Student("Jimmy"));
        studentsToAdd.add(new Student("Miguel"));
        client.loadStudents(studentsToAdd);
        List students = client.listStudents();
        assertEquals(studentsToAdd.size(), students.size());
    }
    
    @Test
    @TestAuthor(githubId = {"Moufblu", "jimmyVerdasca"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException
    {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
}
