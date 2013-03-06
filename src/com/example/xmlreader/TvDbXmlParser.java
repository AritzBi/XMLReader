package com.example.xmlreader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.util.Xml;

public class TvDbXmlParser {
	private static final String ns=null;
	
	public List parse (InputStream in) throws XmlPullParserException, IOException{
		try{
			XmlPullParser parser=Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
			parser.setInput(in,null);
			parser.nextTag();
			return readFeed(parser);
		}finally{
			in.close();
		}
	}
	private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
		List entries=new ArrayList();
		
		parser.require(XmlPullParser.START_TAG, ns, "Data");
		while(parser.next() != XmlPullParser.END_TAG){
		if(parser.getEventType()!= XmlPullParser.START_TAG){
			continue;
		}
		String name=parser.getName();
		if(name.equals("Series")){
			entries.add(readEntry(parser));
		}
		else{
			skip(parser);
		}
		}
		return entries;
	}
	
	public static class Serie {
		public final String title;
		public final int id;
		public final String overview;
		public final Date premiere;
		
		private Serie(int id, String title, String overview, Date premiere){
			this.id=id;
			this.title=title;
			this.overview=overview;
			this.premiere=premiere;
		}
	}
	
	private Serie readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "Series");
	    int id=0;
	    String title = null;
	    String overview= null;
	    Date premiere= null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("seriesid")) {
	            id= readID(parser);
	        } else if (name.equals("SeriesName")) {
	            title = readTitle(parser);
	        } else if (name.equals("Overview")) {
	            overview = readOverview(parser);
	        } else if (name.equals("FirstAired")){
	            premiere=readPremiere(parser);
	        }
	        else{
	        	skip(parser);
	        }
	    }
	    return new Serie(id, title, overview,premiere);
	}
	
	// Processes title tags in the feed.
	private int readID(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "seriesID");
	    int id = Integer.parseInt(readText(parser));
	    parser.require(XmlPullParser.END_TAG, ns, "seriesID");
	    return id;
	}
	 
	// Processes summary tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "SeriesName");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "SeriesName");
	    return title;
	}
	private String readOverview(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "Overview");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "Overview");
	    return title;
	}
	
	@SuppressLint("SimpleDateFormat")
	private Date readPremiere(XmlPullParser parser) throws IOException, XmlPullParserException {
		try {
		    parser.require(XmlPullParser.START_TAG, ns, "FirstAired");
		    String dateS = readText(parser);
		    SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date premiere;
			premiere = textFormat.parse(dateS);
		    parser.require(XmlPullParser.END_TAG, ns, "FirstAired");
		    return premiere;
		} catch (ParseException e) {
		}
		return new Date();

	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}

