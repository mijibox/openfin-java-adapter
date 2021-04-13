package com.mijibox.openfin.fdc3.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinJsonBean;
import com.mijibox.openfin.fdc3.ContextListener;
import com.mijibox.openfin.fdc3.FinDesktopAgent;

public class ContextChannel extends FinJsonBean {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private FinDesktopAgent fdc3Client;
    private ContextListener contextListener;

    private final static String ChannelGetMembers = "CHANNEL-GET-MEMBERS";
    private final static String ChannelAddContextListener = "CHANNEL-ADD-CONTEXT-LISTENER";
    private final static String ChannelJOIN = "CHANNEL-JOIN";
    private final static String ChannelGetCurrentContext = "CHANNEL-GET-CURRENT-CONTEXT";

    private String id;
    private String type;
    
    public ContextChannel() {
    	
    }
    
    public ContextChannel(String id, String type, FinDesktopAgent fdc3Client) {
    	this.id = id;
    	this.type = type;
        this.fdc3Client = fdc3Client;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    
    

//    public void getCurrentContext(AsyncCallback<Context> callback) {
//        JSONObject payload = createPayload();
//        this.fdc3Client.serviceDispatch(ChannelGetCurrentContext, payload, new AckListener() {
//            @Override
//            public void onSuccess(Ack ack) {
//                Context c = new Context(ack.getJsonObject());
//                callback.onSuccess(c);
//            }
//            @Override
//            public void onError(Ack ack) {
//                callback.onSuccess(null);
//            }
//        });
//    }
//
//    public void join(AckListener listener) {
//        join(null, listener);
//    }
//
//    private void join(WindowIdentity identity, AckListener listener) {
//        JSONObject payload = createPayload();
//        if (identity != null) {
//            payload.put("identity", identity.toJsonObject());
//        }
//        this.fdc3Client.serviceDispatch(ChannelJOIN, payload, listener);
//    }
//
//    private void setContextListener(ContextListener contextListener, AckListener listener) {
//        if (this.contextListener == null) {
//            this.fdc3Client.addChannelContextListener(this.id, contextListener);
//            this.contextListener = contextListener;
//            JSONObject payload = createPayload();
//            this.fdc3Client.serviceDispatch(ChannelAddContextListener, payload, listener);
//        } else {
//            logger.warn(String.format("Channel listener already set %s", this.id));
//        }
//    }
//
//    private JSONObject createPayload() {
//        JSONObject payload = new JSONObject();
//        payload.put("id", this.id);
//        return payload;
//    }
	
}
