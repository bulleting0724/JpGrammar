package org.jpgrammar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
@Slf4j
public class AppTest 
    extends TestCase
{
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

    /**
     * test split sentence by punctuation mark "、"
     */
    public void testSplitByPunctuation()
    {
        PunctuationSplit punctuationSplit = new PunctuationSplit();
        log.info(Arrays.toString(punctuationSplit.split("お父さんが買ったのは、バナナです")));
    }
    /**
     * test split sentence by punctuation mark "、"
     */
    public void testCalculateParticlePosInSentence()
    {
        ParticleSplit particleSplit = new ParticleSplit();
        log.info(Arrays.toString(particleSplit.split(
                "NASA探査機ジュノーがとらえた最新画像")));
    }

    /**
     * test to parse jmDict xml"、"
     */
    public void testParseJmDictXml() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        JmdictHandler jmdictHandler = new JmdictHandler();
        saxParser.parse("src/test/resources/staff.xml", jmdictHandler);

        JmDict result = jmdictHandler.getWebsite();

        assertNotNull(result);
        List<Entry> entryList = result.getEntryList();

       log.info(entryList.toString());

    }

}
