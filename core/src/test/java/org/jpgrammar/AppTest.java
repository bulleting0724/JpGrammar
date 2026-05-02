package org.jpgrammar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.logging.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private static final Logger log = Logger.getLogger(AppTest.class.getName());

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testGrammarAnalyzer() {
        log.info(GrammarAnalyzer.analyze(
                "第二次大戦の対ドイツ戦勝80年を記念する一連の行事を終えたことを受けたメディア向け声明を発表し").toString());
    }

}
