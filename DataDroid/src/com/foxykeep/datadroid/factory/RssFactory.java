package com.foxykeep.datadroid.factory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.foxykeep.datadroid.model.RssFeed;
import com.foxykeep.datadroid.model.RssItem;

public class RssFactory {

    public static RssFeed parseResult(final String wsResponse) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();

        RssHandler parser = new RssHandler();
        xr.setContentHandler(parser);
        StringReader sr = new StringReader(wsResponse);
        InputSource is = new InputSource(sr);
        xr.parse(is);

        return parser.mRssFeed;
    }

}

class RssHandler extends DefaultHandler {
    private static final String DATE_FORMAT_1 = "ccc',' dd MMM yyyy HH:mm:ss Z";
    private static final String DATE_FORMAT_2 = "ccc',' dd MMM yyyy";

    private StringBuilder mSb = new StringBuilder();
    private SimpleDateFormat mSdf1 = new SimpleDateFormat(DATE_FORMAT_1);
    private SimpleDateFormat mSdf2 = new SimpleDateFormat(DATE_FORMAT_2);
    public RssFeed mRssFeed = new RssFeed();
    private RssItem mCurrentRssItem = null;
    private boolean mIsInItem = false;
    private boolean mIsInImage = false;
    private ArrayList<String> mSkipDayList = new ArrayList<String>();
    private ArrayList<String> mSkipHourList = new ArrayList<String>();

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        mSb.setLength(0);

        if (localName.equals("item")) {
            mIsInItem = true;
            mCurrentRssItem = new RssItem();
        } else if (localName.equals("image")) {
            mIsInImage = true;
            mRssFeed.imageWidth = 31; // Default value from W3Schools
            mRssFeed.imageHeight = 88; // Default value from W3Schools
        } else if (localName.equals("guid")) {
            final String isPermaLink = atts.getValue("isPermaLink");
            mCurrentRssItem.isGuidPermaLink = isPermaLink != null && isPermaLink.equals("true");
        } else if (localName.equals("source")) {
            mCurrentRssItem.sourceLink = atts.getValue("url");
        } else if (localName.equals("enclosure")) {
            mCurrentRssItem.enclosureLink = atts.getValue("url");
            mCurrentRssItem.enclosureSize = Integer.parseInt(atts.getValue("length"));
            mCurrentRssItem.enclosureMimeType = atts.getValue("type");
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {

        if (localName.equals("item")) {
            mRssFeed.rssItemList.add(mCurrentRssItem);
            mIsInItem = false;
        } else if (localName.equals("image")) {
            mIsInImage = false;
        } else if (localName.equals("title")) {
            if (mIsInImage) {
                mRssFeed.imageTitle = mSb.toString();
            } else if (mIsInItem) {
                mCurrentRssItem.title = mSb.toString();
            } else {
                mRssFeed.title = mSb.toString();
            }
        } else if (localName.equals("link")) {
            if (mIsInImage) {
                mRssFeed.imageWebsiteLink = mSb.toString();
            } else if (mIsInItem) {
                mCurrentRssItem.link = mSb.toString();
            } else {
                mRssFeed.link = mSb.toString();
            }
        } else if (localName.equals("description")) {
            if (mIsInImage) {
                mRssFeed.imageDescription = mSb.toString();
            } else if (mIsInItem) {
                mCurrentRssItem.description = mSb.toString();
            } else {
                mRssFeed.description = mSb.toString();
            }
        } else if (localName.equals("category")) {
            if (mIsInItem) {
                mCurrentRssItem.categoryList.add(mSb.toString());
            } else {
                mRssFeed.categoryList.add(mSb.toString());
            }
        } else if (localName.equals("pubDate")) {
            if (mIsInItem) {
                mCurrentRssItem.pubDate = getMillisFromDate(mSb.toString());
            } else {
                mRssFeed.pubDate = getMillisFromDate(mSb.toString());
            }
        } else if (localName.equals("lastBuildDate")) {
            getMillisFromDate(mSb.toString());
        } else if (localName.equals("language")) {
            mRssFeed.language = mSb.toString();
        } else if (localName.equals("copyright")) {
            mRssFeed.copyright = mSb.toString();
        } else if (localName.equals("generator")) {
            mRssFeed.generator = mSb.toString();
        } else if (localName.equals("url")) {
            mRssFeed.imageUrl = mSb.toString();
        } else if (localName.equals("width")) {
            mRssFeed.imageWidth = Integer.parseInt(mSb.toString());
        } else if (localName.equals("height")) {
            mRssFeed.imageHeight = Integer.parseInt(mSb.toString());
        } else if (localName.equals("managingEditor")) {
            mRssFeed.managingEditor = mSb.toString();
        } else if (localName.equals("webMaster")) {
            mRssFeed.webmaster = mSb.toString();
        } else if (localName.equals("author")) {
            mCurrentRssItem.author = mSb.toString();
        } else if (localName.equals("guid")) {
            mCurrentRssItem.guid = mSb.toString();
        } else if (localName.equals("encodedText")) {
            mCurrentRssItem.encodedContext = mSb.toString();
        } else if (localName.equals("source")) {
            mCurrentRssItem.sourceText = mSb.toString();
        } else if (localName.equals("day")) {
            mSkipDayList.add(mSb.toString().toLowerCase());
        } else if (localName.equals("skipDays")) {
            final int skipDayListSize = mSkipDayList.size();
            mRssFeed.skipDayArray = new int[skipDayListSize];
            for (int i = 0; i < skipDayListSize; i++) {
                final String day = mSkipDayList.get(i);
                if (day.equals("monday")) {
                    mRssFeed.skipDayArray[i] = 0;
                } else if (day.equals("tuesday")) {
                    mRssFeed.skipDayArray[i] = 1;
                } else if (day.equals("wednesday")) {
                    mRssFeed.skipDayArray[i] = 2;
                } else if (day.equals("thrusday")) {
                    mRssFeed.skipDayArray[i] = 3;
                } else if (day.equals("friday")) {
                    mRssFeed.skipDayArray[i] = 4;
                } else if (day.equals("saturday")) {
                    mRssFeed.skipDayArray[i] = 5;
                } else if (day.equals("sunday")) {
                    mRssFeed.skipDayArray[i] = 6;
                }
            }
        } else if (localName.equals("hour")) {
            mSkipHourList.add(mSb.toString().toLowerCase());
        } else if (localName.equals("skipHours")) {
            final int skipHourListSize = mSkipHourList.size();
            mRssFeed.skipHourArray = new int[skipHourListSize];
            for (int i = 0; i < skipHourListSize; i++) {
                mRssFeed.skipHourArray[i] = Integer.parseInt(mSkipHourList.get(i));
            }
        } else if (localName.equals("ttl")) {
            mRssFeed.ttl = Integer.parseInt(mSb.toString());
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        mSb.append(ch, start, length);
    }

    /**
     * Method used to get the milliseconds corresponding to the 2 common formats for the date
     * 
     * @param date
     * @return
     * @throws SAXException
     */
    private long getMillisFromDate(final String date) throws SAXException {
        long millis = -1;

        try {
            millis = mSdf1.parse(date).getTime();
        } catch (ParseException e) {
        }

        if (millis != -1) {
            return millis;
        }

        try {
            return mSdf2.parse(date).getTime();
        } catch (ParseException e) {
            throw new SAXException(e);
        }
    }
}
