/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.factory;

import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroidpoc.config.XMLTag;
import com.foxykeep.datadroidpoc.data.model.Person;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public final class PersonListXmlFactory {

    private static final String TAG = PersonListXmlFactory.class.getSimpleName();

    private PersonListXmlFactory() {
        // No public constructor
    }

    public static ArrayList<Person> parseResult(String wsContent) throws DataException {
        PersonHandler parser = new PersonHandler();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp;
            sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            xr.setContentHandler(parser);
            StringReader sr = new StringReader(wsContent);
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
        return parser.mPersonList;
    }

    private static class PersonHandler extends DefaultHandler {

        private StringBuilder mSb = new StringBuilder();
        public ArrayList<Person> mPersonList = new ArrayList<Person>();
        public Person mCurrentPerson = null;

        @Override
        public void startElement(String namespaceURI, String localName, String qName,
                Attributes atts) throws SAXException {
            mSb.setLength(0);

            if (localName.equals(XMLTag.TAG_PERSON)) {
                mCurrentPerson = new Person();
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {

            if (localName.equals(XMLTag.TAG_PERSON)) {
                mPersonList.add(mCurrentPerson);
            } else if (localName.equals(XMLTag.TAG_PERSON_FIRST_NAME)) {
                mCurrentPerson.firstName = mSb.toString();
            } else if (localName.equals(XMLTag.TAG_PERSON_LAST_NAME)) {
                mCurrentPerson.lastName = mSb.toString();
            } else if (localName.equals(XMLTag.TAG_PERSON_EMAIL)) {
                mCurrentPerson.email = mSb.toString();
            } else if (localName.equals(XMLTag.TAG_PERSON_CITY)) {
                mCurrentPerson.city = mSb.toString();
            } else if (localName.equals(XMLTag.TAG_PERSON_POSTAL_CODE)) {
                mCurrentPerson.postalCode = Integer.parseInt(mSb.toString());
            } else if (localName.equals(XMLTag.TAG_PERSON_AGE)) {
                mCurrentPerson.age = Integer.parseInt(mSb.toString());
            } else if (localName.equals(XMLTag.TAG_PERSON_IS_WORKING)) {
                mCurrentPerson.isWorking = Integer.parseInt(mSb.toString()) == 1;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            super.characters(ch, start, length);
            mSb.append(ch, start, length);
        }
    }
}
