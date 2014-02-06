package it.uniroma2.wifionoff;

public class AppHelper {
	
	private String AppSend;
	private String AppBroad;
	
	public AppHelper(String AppNameToSend, String AppBroadcast){
		
		this.AppBroad=AppBroadcast;
		this.AppSend=AppNameToSend;
		
	}
	
	public String getBro(){
		
		return this.AppBroad;
		
	}
	
	public String getName(){
		
		return this.AppSend;
		
	}
	

}
