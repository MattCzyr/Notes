package com.chaosthedude.notes.util;

public class WrappedString {

	private String text;
	private boolean wrapped;

	public WrappedString(String text, boolean wrapped) {
		this.text = text;
		this.wrapped = wrapped;
	}

	public String getText() {
		return text;
	}

	public boolean isWrapped() {
		return wrapped;
	}

}
