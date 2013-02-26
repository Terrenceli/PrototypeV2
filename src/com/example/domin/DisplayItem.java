package com.example.domin;

import java.io.Serializable;

public class DisplayItem implements Serializable{
	private int Id;
	private String title;
	private String iconUri;
	private String category;
	private String latitude;
	private String longitude;
	private String distance;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIconUri() {
		return iconUri;
	}
	public void setIconUri(String iconUri) {
		this.iconUri = iconUri;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
}
