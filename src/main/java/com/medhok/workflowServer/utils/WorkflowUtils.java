package com.medhok.workflowServer.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class WorkflowUtils {

	private Logger logger = LoggerFactory.getLogger(WorkflowUtils.class);

	/**
	 *  Given the workflow model, the current step id and the value of
	 *  the decision rule find the next step id.
	 *  
	 *  Assumption is that there is a decision rule for the given step
	 *  otherwise there would be no ruleValue to pass in.
	 * 
	 * TODO: Only boolean values are supporting at this time but
	 * much more needs to be supported
	 * 
	 * @param workFlowModel
	 * @param thisStepId - the current step that has been completed
	 * @param ruleValue - boolean value for the decision rule
	 * @return - The next step id or null
	 * @throws JSONException
	 */
	public String getNextStepFromRule(JSONObject workFlowModel, String thisStepId, Boolean ruleValue)  {
		String nextStepId = null;
		if(workFlowModel == null || thisStepId == null || ruleValue == null) {
			return nextStepId;
		}
		JSONObject questionNode = null;
		Map<String, String> rulesMap = new HashMap<>();
		String ruleId = null;

		try {
			JSONArray rulesJsonAry = workFlowModel.getJSONArray("rules");
			for(int i = 0; i < rulesJsonAry.length(); i++) {
				JSONObject thisRuleNode = rulesJsonAry.getJSONObject(i);
				Iterator<String> keys = thisRuleNode.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					rulesMap.put(key, thisRuleNode.getString(key));
				}
			}

			JSONArray nodesJsonAry = workFlowModel.getJSONArray("nodes");
			if(nodesJsonAry == null) {
				return nextStepId;
			}
			boolean found = false;
			for(int i = 0; i < nodesJsonAry.length() && !found; i++) {
				try {

					JSONObject thisNode = nodesJsonAry.getJSONObject(i);
					String stepId = thisNode.getString("stepId");
					if(stepId.equals(thisStepId) && thisNode.getString("type").equals("question")) {
						questionNode = thisNode;
						ruleId = questionNode.getString("rule");
						found = true;
					}
				} catch (JSONException e) {}//Didn't have that value in this node
			}
			if(found = false) {
				return nextStepId;//didn't find it return null
			}

			JSONArray edgesJsonAry = workFlowModel.getJSONArray("edges");
			if(edgesJsonAry == null) {
				return nextStepId;//no edges defined return null
			}
			found = false;
			for(int i = 0; i < edgesJsonAry.length() && !found; i++) {
				JSONObject thisEdge = edgesJsonAry.getJSONObject(i);
				if(thisEdge.getString("source").equals(questionNode.getString("id"))) {

					if(thisEdge.getBoolean(ruleId) == ruleValue) {
						nextStepId = thisEdge.getString("target");
						found = true;
					}
				}
			}
		} catch (JSONException e) {
			logger.error("Error parsing workflow model", e);
		}

		return nextStepId;
	}


}