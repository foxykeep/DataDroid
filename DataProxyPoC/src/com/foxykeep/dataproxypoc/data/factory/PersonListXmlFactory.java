package com.foxykeep.dataproxypoc.data.factory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.foxykeep.dataproxypoc.config.XMLTag;
import com.foxykeep.dataproxypoc.data.model.Person;

public class PersonListXmlFactory {

    public static ArrayList<Person> parseResult(final String wsContent) throws ParserConfigurationException,
            SAXException, IOException {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();

        PersonHandler parser = new PersonHandler();
        xr.setContentHandler(parser);
        StringReader sr = new StringReader(wsContent);
        InputSource is = new InputSource(sr);
        xr.parse(is);

        return parser.mPersonList;
    }

}

class PersonHandler extends DefaultHandler {

    private StringBuilder mSb = new StringBuilder();
    public ArrayList<Person> mPersonList = new ArrayList<Person>();
    public Person mCurrentPerson = null;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) throws SAXException {
        mSb.setLength(0);

        if (localName.equals(XMLTag.TAG_PERSON)) {
            mCurrentPerson = new Person();
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {

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
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        super.characters(ch, start, length);
        mSb.append(ch, start, length);
    }
}
