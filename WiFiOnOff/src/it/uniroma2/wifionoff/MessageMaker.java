package it.uniroma2.wifionoff;

public class MessageMaker {
	
	private static MessageMaker instance;
	
	private MessageMaker(){
		
	}
	
	public static MessageMaker getInstance(){
		
		if(instance==null)
			instance=new MessageMaker();
		
		return instance;
	}
	
	
	public String BuildMessage(){
		
		String msg="";
		
		msg+=OnOffService.Device+"\n";
		
		for(int i=0; i< OnOffService.ConnectedApp.size(); i++)
			msg+=(OnOffService.ConnectedApp.get(i)).getName()+"\n";
			
		msg+="ThankYou\n";
		
		return msg;
	}
	

}
