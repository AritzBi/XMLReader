package com.example.xmlreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
		while(parser.next() != XmlPullParser.END_TAG);
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
	    String title = null;
	    String summary = null;
	    String link = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("title")) {
	            title = readTitle(parser);
	        } else if (name.equals("summary")) {
	            summary = readSummary(parser);
	        } else if (name.equals("link")) {
	            link = readLink(parser);
	        } else {
	            skip(parser);
	        }
	    }
	    return new Entry(title, summary, link);
	}
}

