/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.factory;

import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroidpoc.data.model.RssFeed;
import com.foxykeep.datadroidpoc.data.model.RssItem;

import android.text.TextUtils;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Factory used to parse the RSS feed.
 *
 * @author Foxykeep
 */
public final class RssFactory {

    private static final String TAG = RssFactory.class.getSimpleName();

    private RssFactory() {
        // No public constructor
    }

    public static RssFeed parseResult(String wsResponse) throws DataException {
        RssHandler parser = new RssHandler();

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            xr.setContentHandler(parser);
            StringReader sr = new StringReader(wsResponse);
            InputSource is = new InputSource(sr);
            xr.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "ParserConfigurationException", e);
            throw new DataException(e);
        } catch (SAXException e) {
            Log.e(TAG, "SAXException", e);
            throw new DataException(e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            throw new DataException(e);
        }

        return parser.mRssFeed;
    }

    private static class RssHandler extends DefaultHandler {
        private static final String DATE_FORMAT_1 = "ccc',' dd MMM yyyy HH:mm:ss Z";
        private static final String DATE_FORMAT_2 = "ccc',' dd MMM yyyy";

        private static final SimpleDateFormat SIMPLE_DATE_FORMAT_1 =
                new SimpleDateFormat(DATE_FORMAT_1, Locale.US);
        private static final SimpleDateFormat SIMPLE_DATE_FORMAT_2 =
                new SimpleDateFormat(DATE_FORMAT_2, Locale.US);

        private StringBuilder mSb = new StringBuilder();
        public RssFeed mRssFeed = new RssFeed();
        private RssItem mCurrentRssItem = null;
        private boolean mIsInItem = false;
        private boolean mIsInImage = false;
        private ArrayList<String> mSkipDayList = new ArrayList<String>();
        private ArrayList<String> mSkipHourList = new ArrayList<String>();

        @Override
        public void startElement(String namespaceURI, String localName, String qName,
                Attributes atts) throws SAXException {
            mSb.setLength(0);

            if (TextUtils.isEmpty(namespaceURI)) {
                if (localName.equals("item")) {
                    mIsInItem = true;
                    mCurrentRssItem = new RssItem();
                } else if (localName.equals("image")) {
                    mIsInImage = true;
                    mRssFeed.imageWidth = 31; // Default value from W3Schools
                    mRssFeed.imageHeight = 88; // Default value from W3Schools
                } else if (localName.equals("guid")) {
                    String isPermaLink = atts.getValue("isPermaLink");
                    mCurrentRssItem.isGuidPermaLink = isPermaLink != null
                            && isPermaLink.equals("true");
                } else if (localName.equals("source")) {
                    mCurrentRssItem.sourceLink = atts.getValue("url");
                } else if (localName.equals("enclosure")) {
                    mCurrentRssItem.enclosureLink = atts.getValue("url");
                    mCurrentRssItem.enclosureSize = Integer.parseInt(atts.getValue("length"));
                    mCurrentRssItem.enclosureMimeType = atts.getValue("type");
                }
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {
            if (TextUtils.isEmpty(namespaceURI)) {
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
                    mSkipDayList.add(mSb.toString().toLowerCase(Locale.US));
                } else if (localName.equals("skipDays")) {
                    int skipDayListSize = mSkipDayList.size();
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
                    mSkipHourList.add(mSb.toString().toLowerCase(Locale.US));
                } else if (localName.equals("skipHours")) {
                    int skipHourListSize = mSkipHourList.size();
                    mRssFeed.skipHourArray = new int[skipHourListSize];
                    for (int i = 0; i < skipHourListSize; i++) {
                        mRssFeed.skipHourArray[i] = Integer.parseInt(mSkipHourList.get(i));
                    }
                } else if (localName.equals("ttl")) {
                    mRssFeed.ttl = Integer.parseInt(mSb.toString());
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            mSb.append(ch, start, length);
        }

        /**
         * Method used to get the milliseconds corresponding to the 2 common formats for the date.
         *
         * @param date The given date.
         * @return The timestamp corresponding to the given date.
         * @throws SAXException Exception thrown if the given date doesn't follow one of the 2
         *             common formats.
         */
        private long getMillisFromDate(String date) throws SAXException {
            long millis = -1;

            try {
                millis = SIMPLE_DATE_FORMAT_1.parse(date).getTime();
            } catch (ParseException e) {
                // fall through
            }

            if (millis != -1) {
                return millis;
            }

            try {
                return SIMPLE_DATE_FORMAT_2.parse(date).getTime();
            } catch (ParseException e) {
                throw new SAXException(e);
            }
        }
    }
}
