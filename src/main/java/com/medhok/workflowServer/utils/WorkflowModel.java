package com.medhok.workflowServer.utils;

public enum WorkflowModel {
	
	entity,
	form,
	workflow;
	
	
	public static int getNum(WorkflowModel wfModel) {
		Integer id = null;
		switch (wfModel) {
		case entity:
			id = 1;
			break;
		case form:
			id = 2;
			break;
		case workflow:
			id = 3;
		}
		return id;
	}
}
