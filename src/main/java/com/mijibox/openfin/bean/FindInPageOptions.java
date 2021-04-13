package com.mijibox.openfin.bean;

public class FindInPageOptions extends FinJsonBean {

	private Boolean forward;
	private Boolean findNext;
	private Boolean matchCase;
	private Boolean wordStart;
	private Boolean medialCapitalAsWordStart;

	public Boolean getForward() {
		return forward;
	}

	public void setForward(Boolean forward) {
		this.forward = forward;
	}

	public Boolean getFindNext() {
		return findNext;
	}

	public void setFindNext(Boolean findNext) {
		this.findNext = findNext;
	}

	public Boolean getMatchCase() {
		return matchCase;
	}

	public void setMatchCase(Boolean matchCase) {
		this.matchCase = matchCase;
	}

	public Boolean getWordStart() {
		return wordStart;
	}

	public void setWordStart(Boolean wordStart) {
		this.wordStart = wordStart;
	}

	public Boolean getMedialCapitalAsWordStart() {
		return medialCapitalAsWordStart;
	}

	public void setMedialCapitalAsWordStart(Boolean medialCapitalAsWordStart) {
		this.medialCapitalAsWordStart = medialCapitalAsWordStart;
	}

}
