package com.thanggun99.btfilemanager.model;

public class Item implements Comparable<Item>{
	private String name;
	private String data;
	private String date;
	private String path;
	private int image;

	public Item(String name, String data, String date, String path, int image) {
		this.name = name;
		this.data = data;
		this.date = date;
		this.path = path;
		this.image = image;
	}

	public String getName()
	{
		return name;
	}
	public String getData()
	{
		return data;
	}
	public String getDate()
	{
		return date;
	}
	public String getPath()
	{
		return path;
	}
	public int getImage() {
		return image;
	}

	public int compareTo(Item o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		else
			throw new IllegalArgumentException();
	}
}
