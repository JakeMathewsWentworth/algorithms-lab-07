package edu.wit.cs.comp3370.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.wit.cs.comp3370.LAB7;

import static org.junit.Assert.*;


public class LAB7TestCase{

	@Rule
	public Timeout globalTimeout = Timeout.seconds(15);
	
	@SuppressWarnings("serial")
	private static class ExitException extends SecurityException {}
	
	private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) {}
        
        @Override
        public void checkPermission(Permission perm, Object context) {}
        
        @Override
        public void checkExit(int status) { super.checkExit(status); throw new ExitException(); }
    }
	
	@Before
    public void setUp() throws Exception 
    {
        System.setSecurityManager(new NoExitSecurityManager());
    }
	
	@After
    public void tearDown() throws Exception 
    {
        System.setSecurityManager(null);
    }

	private void _testDyn(String s0, String s1, int expectedLongest) {
		String[] actual = new String[0];
		String act0 = "";
		String act1 = "";
		int actualLongest = 0;
		
		try {
			actual = LAB7.findLCSdyn(s0, s1);
			actualLongest = LAB7.getLongest();
		} catch (ExitException e) {}
		
		assertEquals("Longest value is incorrect.", expectedLongest, actualLongest);
		assertEquals("Length of results should not be different,", actual[0].length(), actual[1].length());
		
		for (int i = 0; i < actual[0].length(); i++) {
			if ((actual[0].charAt(i) != '-') && (actual[1].charAt(i) != '-'))
				assertEquals("Characters at same positions should be the same.", actual[0].charAt(i), actual[1].charAt(i));
			if (actual[0].charAt(i) != '-')
				act0 += actual[0].charAt(i);
			if (actual[1].charAt(i) != '-')
				act1 += actual[1].charAt(i);
		}
		assertEquals("First string doesn't match result.", s0, act0);
		assertEquals("Second string doesn't match result.", s1, act1);
	}
	
	private void _testFileDyn(String f1, String f2, int expectedBest) {
		String t1 = "";
		String t2 = "";
		try {
			t1 = new String(Files.readAllBytes(Paths.get(f1)));
			t2 = new String(Files.readAllBytes(Paths.get(f2)));
		} catch (IOException e) {
		}
		
		_testDyn(t1, t2, expectedBest);
	}
	
	@Test
	public void testSmall() {
		_testDyn("overlap", "overlop", 6);
		_testDyn("sap", "sip", 2);
		_testDyn("ACGGAT", "CCGCTT", 3);
		_testDyn("ACCCGAG", "TCCTACGG", 5);
		_testDyn("big", "even bigger", 3);
	}
	
	@Test
	public void testLarge() {
		_testFileDyn("text/melania.txt", "text/michelle.txt", 331);
		_testDyn("GTCGTTATGCTAGTATACGCCTACCCGTCACCGGCCATCTGTGTGCAGATGGGGCGACGAGTTACTGGCCCTGATTTCTCCGCTTCTAATACCACACACTGGGCAATACGAGCTCAAGCCAGTCTCGCAGTAACGCTCATCAGCTAACGAAAGAGTTAGAGGCTCGCTAATTCGCACTGTCGGGGTCCCTTGGGTGTTTTGCACTAGCGTCAGGTAGGCTAGTATGTGTCTTTCCTTCCAGGGGTATGTGGCTGCGTGGTCAAATGTGCAGCATACGTATTTGCTCGGCGTGCTTGGTCTCTCGTACTTCTCCTGGAGATCAAGGAAATGTTTCTTGTCCAAGCGGACAGCGGTTCTACGGAATGGATCTACGTTACTGCCTGCATAAGGAGAACGGAGTTGCCAAGGACGAAAGCGACTCTAGGTTCTAACCGTCGACTTTGGCGGAAAGGTTTCACTCAGGAAGCAGACACTGATTGACACGGTTTAGCAGAACGTTTGAGGATTAGGTCAAATTGAGTGGTTTAATATCGGTATGTCTGGGATTAAAATATAGTATAGTGCGCTGATCGGAGACGAATTAAAGACACGAGTTCCCAAAACCAGGCGGGCTCGCCACGACGGCTAATCCTGGTAGTTTACGTGAACAATGTTCTGAAGAAAATTTATGAAAGAAGGACCCGTCATCGCCTACAATTACCTACAACGGTCGACCATACCTTCGATTGTCGTGGCCACCCTCGGATTACACGGCAGAGGTGGTTGTGTTCCGATAGGCCAGCATATTATCCTAAGGCGTTACCCCAATCGTTTTCCGTCGGATTTGCTATAGCCCCTGAACGCTACATGCACGAAACCAAGTTATGTATGCACTGGGTCATCAATAGGACATAGCCTTGTAGTTAACATGTAGCCCGGCCGTATTAGTACAGTAGAGCCTTCACCGGCATTCTGTTTATTAAGTTATTTCTACAGCAAAACGATCATATGCAGATCCGCA", "GTGCGCGGTAGAGACACGTCCACCCGGCTGCTCTGTAATAGGGACTAAAAAAGTGATGATTATCATGAGTGCCCCGTTATGGTCGTGTTCGATCAGAGCGCTCTTACGAGCAGTCGTATGCTTTCTCGAATTCCGTGCGGTTAAGCGTGACAGTCCCAGTGAACCCACAAAACGTGATGGCAGTCCATGCGATCATACGCAAGAAGGATGGTCTCCAGACACCGGCGCACCAGTTTTCACGCCGAAAGCATAAACGAGGAGCACAAATGAGAGTGTTTGAACTGGACCTGTAGTTTCTCTACGAAGAACACCTTGAGCTGTTGCGTTGTTGCGCTGCCTAGATGCAGTGTCGCACGTATCACTTTTGCCTCAACGACTGCTGCTTTCGCTGTAACCCTAGACAGACAACAGTAAGCGCCTTTTGTAGGCAAGAGCTCCGCCTGTGACTAACTGCGCCAAAACGTCTTCCAATCCCCTTATCCAATTTAACTCACCGAATTCTTACAATTTAGACCCTAATATCACATCATTAGACACTAATTGCCTCTGCCAAAATTCTGTCCACAAGCGTTTTAGTTCGCCCCAGTAAAGTTGTCAATAACGACCACCAAATCCGCATGTTACGGGACTTCTTATTAATTCTTTTTTCGTGGGGAGCAGCGGATCTTAATGGATGGCGCCAGGTGGTATGGAAGCTAATAGCGCGGGTGAGAGGGTAATCAGCCGTCTCCACCAACACAACGCTATCGGGTCATACTATAAGATTCCGCAATGCGACTACTTATAAGATGCCTTAACGGTATCCGCAACTTGCGATGTGCCTGCTATGCTTAAATGCATATCTCGCCCAGTAGCTTTCCAATATGAGAGCATCAATTGTAGATCGGGCCGGGATAATCATGTCGTCACGGAACTTACTGTAAGAGTAATAATTTAAAAGAGATGTCGGTTTGCTGGTTCACGTAAAGGTCCCTCGCGCTACCTCTAAGTAAGTGAGCGG", 646);
		_testFileDyn("text/znc1.txt", "text/znc2.txt", 3828);
	}

}
