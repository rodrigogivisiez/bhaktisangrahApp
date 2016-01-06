package com.goldenant.bhaktisangrah.model;

public class NotificationRecord extends MasterModel
{
	private static final long serialVersionUID = 6104048947898684570L;

	private String content;
	private String date;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content) 
	{
		this.content = content;
	}
	
	public String getDate() 
	{
		return date;
	}
	
	public void setDate(String date) 
	{
		this.date = date;
	}
	

}