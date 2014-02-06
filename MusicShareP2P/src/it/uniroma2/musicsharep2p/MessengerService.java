package it.uniroma2.musicsharep2p;

public class MessengerService {

	
		static final int MSG_REGISTER_CLIENT = 1;

	    /**
	     * Command to the service to unregister a client, ot stop receiving callbacks
	     * from the service.  The Message's replyTo field must be a Messenger of
	     * the client as previously given with MSG_REGISTER_CLIENT.
	     */
	    static final int MSG_UNREGISTER_CLIENT = 2;

	    /**
	     * Command to service to set a new value.  This can be sent to the
	     * service to supply a new value, and will be sent by the service to
	     * any registered clients with the new value.
	     */
	    static final int MSG_SET_VALUE = 3;


}
