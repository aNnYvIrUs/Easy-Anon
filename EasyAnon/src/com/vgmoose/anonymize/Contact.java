package com.vgmoose.anonymize;

//import java.util.ArrayList;


public class Contact {
    private String name;
    private String altName;
    private String number;
    private int type;
    
//    private ArrayList<String> numbers;

    // returns the appropriate name
    public String getName() {
    	if (AnonymousCall.sortAlt())
    		return altName;
    	else
    		return name;
    }
    
    public void setAltName(String ln) {
        this.altName = ln;
    }
    
    public void setName(String fn) {
        this.name = fn;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String contactNum) {
        number = contactNum;
    }

	public void setType(int type) {
		this.type = type;
		
	}
	
	public int getType()
	{
		return type;
	}
	
}