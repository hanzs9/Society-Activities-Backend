package com.yiibai.springmvc;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityMember {
	private HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

	public HashMap<String, ArrayList<String>> getMap() {
		return map;
	}

	public void setMap(HashMap<String, ArrayList<String>> map) {
		this.map = map;
	}
}
