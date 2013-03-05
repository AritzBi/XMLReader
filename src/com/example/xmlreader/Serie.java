package com.example.xmlreader;

import java.util.Date;

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
