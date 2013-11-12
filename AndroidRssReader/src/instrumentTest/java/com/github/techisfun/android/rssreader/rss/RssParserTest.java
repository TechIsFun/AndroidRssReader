package com.github.techisfun.android.rssreader.rss;

import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.github.techisfun.android.rssreader.model.RssItem;
import com.github.techisfun.android.rssreader.rss.RssParser;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by James on 10/5/13.
 */
public class RssParserTest extends TestCase {

    public void testRssParsing() throws Exception {
        String xmlContent = getExampleContent();

        RssParser parser = new RssParser();

        List<RssItem> items = parser.parse(new ByteArrayInputStream(xmlContent.getBytes()));

        RssItem item = items.get(0);
        assertEquals("Union anger at health pay plan", item.getTitle());
        assertEquals("Unions criticise government plans to restrict the pay of NHS staff as health trusts come under pressure to make further savings.",
                item.getDescription());
        assertEquals("http://www.bbc.co.uk/news/uk-24408681#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa", item.getLink());
        //assertEquals("Sat, 05 Oct 2013 06:12:42 GMT", item.getPubDate());
        assertEquals("http://news.bbcimg.co.uk/media/images/70294000/jpg/_70294709_70294669.jpg", item.getMedia());
    }

    private String getExampleContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<?xml-stylesheet title=\"XSL_formatting\" type=\"text/xsl\" href=\"/shared/bsp/xsl/rss/nolsol.xsl\"?>\n" +
                "<rss xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>BBC News - Home</title>\n" +
                "<link>http://www.bbc.co.uk/news/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>\n" +
                "<description>The latest stories from the Home section of the BBC News web site.</description>\n" +
                "<language>en-gb</language>\n" +
                "<lastBuildDate>Sat, 05 Oct 2013 08:13:22 GMT</lastBuildDate>\n" +
                "<copyright>Copyright: (C) British Broadcasting Corporation, see http://news.bbc.co.uk/2/hi/help/rss/4498287.stm for terms and conditions of reuse.</copyright>\n" +
                "<image>\n" +
                "<url>http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif</url>\n" +
                "<title>BBC News - Home</title>\n" +
                "<link>http://www.bbc.co.uk/news/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>\n" +
                "<width>120</width>\n" +
                "<height>60</height>\n" +
                "</image>\n" +
                "<ttl>15</ttl>\n" +
                "<atom:link href=\"http://feeds.bbci.co.uk/news/rss.xml\" rel=\"self\" type=\"application/rss+xml\"/>\n" +
                "<item>\n" +
                "<title>Union anger at health pay plan</title>\n" +
                "<description>Unions criticise government plans to restrict the pay of NHS staff as health trusts come under pressure to make further savings.</description>\n" +
                "<link>http://www.bbc.co.uk/news/uk-24408681#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>\n" +
                "<guid isPermaLink=\"false\">http://www.bbc.co.uk/news/uk-24408681</guid>\n" +
                "<pubDate>Sat, 05 Oct 2013 06:12:42 GMT</pubDate>\n" +
                "<media:thumbnail width=\"66\" height=\"49\" url=\"http://news.bbcimg.co.uk/media/images/70294000/jpg/_70294708_70294669.jpg\"/>\n" +
                "<media:thumbnail width=\"144\" height=\"81\" url=\"http://news.bbcimg.co.uk/media/images/70294000/jpg/_70294709_70294669.jpg\"/>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>US commitment to Asia 'undiminished'</title>\n" +
                "<description>The US remains committed to Asia despite the shutdown that forced President Obama to cancel planned trips, Secretary of State John Kerry says.</description>\n" +
                "<link>http://www.bbc.co.uk/news/world-asia-24410208#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>\n" +
                "<guid isPermaLink=\"false\">http://www.bbc.co.uk/news/world-asia-24410208</guid>\n" +
                "<pubDate>Sat, 05 Oct 2013 08:00:51 GMT</pubDate>\n" +
                "<media:thumbnail width=\"66\" height=\"49\" url=\"http://news.bbcimg.co.uk/media/images/70294000/jpg/_70294952_70294454.jpg\"/>\n" +
                "<media:thumbnail width=\"144\" height=\"81\" url=\"http://news.bbcimg.co.uk/media/images/70294000/jpg/_70294953_70294454.jpg\"/>\n" +
                "</item>\n" +
                "</channel>\n" +
                "</rss>";
    }
}
