package com.mijibox.openfin.fdc3;

import com.mijibox.openfin.bean.FinJsonBean;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.fdc3.channel.ContextChannel;

public class Fdc3Event extends FinJsonBean {
	
	private Identity identity;
	private ContextChannel channel;
	private ContextChannel previousChannel;
	private String type;
	public Identity getIdentity() {
		return identity;
	}
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	public ContextChannel getChannel() {
		return channel;
	}
	public void setChannel(ContextChannel channel) {
		this.channel = channel;
	}
	public ContextChannel getPreviousChannel() {
		return previousChannel;
	}
	public void setPreviousChannel(ContextChannel previousChannel) {
		this.previousChannel = previousChannel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
