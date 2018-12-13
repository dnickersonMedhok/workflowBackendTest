package com.medhok.workflowServer.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.medhok.workflowServer.models.GenericTable1;
import com.medhok.workflowServer.models.GenericTable2;
import com.medhok.workflowServer.models.ParentGenericTableModel;
import com.medhok.workflowServer.repositories.GenericTable1Repository;
import com.medhok.workflowServer.repositories.GenericTable2Repository;
import com.medhok.workflowServer.repositories.ModelRepository;



@Component
public class WorkflowUtils {

	private Logger logger = LoggerFactory.getLogger(WorkflowUtils.class);
	
	@Autowired
	GenericTable1Repository g1Repo;
	@Autowired
	GenericTable2Repository g2Repo;
	
	//json node names
	final String table = "table";
	final String fields = "fields";
	final String fieldType = "fieldType";
	final String column = "column";
	final String value = "value";
	final String genericTable1 = "GENERIC_TABLE_1";
	final String genericTable2 = "GENERIC_TABLE_2";
	public static final Integer ENTITY = 1;
	public static final Integer FORM = 2;
	public static final Integer WORKFLOW = 3;
	static final String steps = "steps";
	static final String stepId = "stepId";
	static final String tasks = "tasks";
	static final String taskId = "taskId";
	static final String rulesGroups = "rulesGroups";
	static final String rule = "rule";
	static final String decisionGroups = "decisionGroups";
	static final String decisionGroup = "decisionGroup";
	static final String criteria = "criteria";
	static final String decisionField = "decisionField";
	static final String decision = "decision";
	static final String decisionValue = "decisionValue";
	static final String decisionOperator = "decisionOperator";
	static final String decisionGroupOperator = "decisionGroupOperator";
	
	
	/**
	 * 
	 * TODO: preliminary work.  Needs to recursively evaluate decision nodes to
	 * support an infinite nesting of decisions
	 * 
	 * 
	 * Find  the next step id for the given workflow model, entityDTO and current step id.
	 * The conditional logic in the workflow model will be evaluated using the given
	 * data in the DTO. 
	 * 
	 * example workflow model:
	 * ((a == 1 && b == 2) || (c == 3)) 
	 * 
	 * definitions:
	 * a, b and c are all in rules group 1
	 * a and b are in decision group 1, c is in decision group 2
	 * a is in the first decision, and b is in the second decision of decision group 1.
	 * c is in the only decision of decision group 2.
	 * The rules group operator is ||
	 * The decision group operator for decision group 1 is &&
	 * 
	 * If the values in the given entity DTO match the entire equation above then the
	 * nextStepId for that rules groups is returned. 
	 * 
	 * If there are no decision groups for a given rule node then assume that the next step
	 * id is always used, return the nextStepId if found
	 * 
	 * TODO: currently only supports fields.  Will have to support a lot more than that. 
	 * 
	 * @param workflowModel
	 * @param entityDTO
	 * @param stepId
	 * @return next step id or null
	 */
	public String findNextStepId(JSONObject workflowModel, JSONObject entityDTO, String currentStepId) {
		String nextStepId = null;
		boolean rulevalue = true;//if at any time this becomes false drop out of that rules group
		int decisionGroupSize = 10;

		if(workflowModel == null || entityDTO == null || Strings.isNullOrEmpty(currentStepId)) return nextStepId;

		JSONObject stepNode = findStepNode(workflowModel, currentStepId);
		if(stepNode == null) return nextStepId;
		//At this point we found the correct step

		JSONArray currentRulesGroups = null;
		try {
			currentRulesGroups = stepNode.getJSONArray(rulesGroups);
		} catch (JSONException e) {
			logger.error("No rules groups found for stepId " + stepId);
			return nextStepId;
		}

		//loop through the rules groups array
		for(int i = 0; i < currentRulesGroups.length(); i++) {
			try {
				JSONObject thisRuleGroup = null;
				try {
					thisRuleGroup = currentRulesGroups.getJSONObject(i);
				} catch (JSONException e) {
					logger.error("Malformed json");
					continue;
				}
				JSONArray currentDecisionGroups = null;
				try {
					currentDecisionGroups = thisRuleGroup.getJSONArray(decisionGroups);
				} catch (JSONException e) {
					logger.error("Malformed json");
					continue;
				}
				for(int l = 0; l < currentDecisionGroups.length(); l++) {
					JSONObject thisDecisionGroup = null;
					boolean decisionGroupValue = true;
					try {
						thisDecisionGroup = currentDecisionGroups.getJSONObject(l);
					} catch (JSONException e) {
						logger.error("Malformed json");
						continue;
					}
					JSONArray decisionArray = null;
					try {
						decisionArray = thisDecisionGroup.getJSONArray(criteria);
					} catch (JSONException e) {
						logger.error("Malformed json");
						continue;				
					}

					Boolean[] decisionValues = new Boolean[decisionGroupSize];
					//loop through the decision array as long as no decisions have
					//been evaluated as false
					for(int m = 0; m < decisionArray.length(); m++) {
						JSONObject thisDecision = null;
						try {
							thisDecision = decisionArray.getJSONObject(m);
						} catch (JSONException e) {
							logger.error("Malformed json");
							continue;		
						}
						String thisFieldName = null;
						try {
							thisFieldName = thisDecision.getString(decisionField);
						} catch (JSONException e) {
							logger.error("Malformed json");
							continue;						
						}
						if(!Strings.isNullOrEmpty(thisFieldName)) {
							String thisFieldType = thisDecision.getString(fieldType);
							//TODO: have to support more than strings and booleans from field values
							switch(thisFieldType) {
							case "boolean":
								boolean thisBFieldValue = thisDecision.getBoolean(decisionValue);
								boolean fieldValueFromDTO = getBooleanValueForField(entityDTO, thisFieldName);
								decisionValues[m] = thisBFieldValue == fieldValueFromDTO;
								break;
							default://default to String
								String thisSFieldValue = thisDecision.getString(decisionValue);
								String sFieldValueFromDTO = getStringValueForField(entityDTO, thisFieldName);
								decisionValues[m] = thisSFieldValue.equals(sFieldValueFromDTO);
							}
						}

					}//end decisionArray loop
					String decisionGroupOperator = null;
					try {
						thisDecisionGroup.getString("decisionGroupOperator");
					} catch (JSONException e) {
						logger.error("malformed json");
						continue;
					}
					decisionGroupValue = evaluate(decisionValues[0], decisionValues[1], decisionGroupOperator);

				}




			} catch (JSONException e) {
				logger.error("Task id not found " + taskId);
				return nextStepId;
			}
		}

		//call getNextStepFromRule using the model, stepid, and rulevalue

		return nextStepId;
	}
	
	/**
	 * Must assume that the Boolean array contains only 2 members
	 * 
	 * @param values
	 * @param operator
	 * @return truth value
	 */
	private boolean evaluate(Boolean value1, Boolean value2, String operator) {
		if(Strings.isNullOrEmpty(operator) || value1 == null || value2 == null) return false;
		if(operator.equalsIgnoreCase("and")) {
			return value1 && value2; 
		} 
		return value1 || value2;
	}
	
	/**
	 * Find the step node from the workflow model for the given step id
	 * if found otherwise null
	 * 
	 * @param workflowModel
	 * @param stepId
	 * @return the step node or null
	 */
	private JSONObject findStepNode(JSONObject workflowModel, String currentStepId) {
		JSONObject stepNode = null;

		if(workflowModel == null || Strings.isNullOrEmpty(currentStepId)) return stepNode;

		JSONArray stepArray = null;
		try {
			stepArray = workflowModel.getJSONArray(steps);
		} catch (JSONException e) {
			logger.error("Couldn't find any steps");
			return stepNode;
		}
		//Find the given stepId
		for(int j = 0; j < stepArray.length() && stepNode == null; j++) {
			JSONObject thisStepNode = null;
			try {
				thisStepNode = stepArray.getJSONObject(j);
			} catch (JSONException e) {
				logger.error("malformed JSON");
				continue;
			}
			if(thisStepNode == null) continue;
			String thisStepId = null;
			try {
				thisStepId = thisStepNode.getString(stepId);
			} catch(JSONException e) {
				logger.error("malformed JSON");
				continue;
			}
			if(!Strings.isNullOrEmpty(thisStepId)) {
				if(thisStepId.equals(currentStepId)) {
					stepNode = thisStepNode;
				}
			}
		}
		return stepNode;
	}
	
	/**
	 * Get the String value for the given field from the DTO.
	 * 
	 * @param entityDTO
	 * @param fieldName
	 * @return field value or null
	 */
	public String getStringValueForField(JSONObject entityDTO, String fieldName) {
		String returnValue = null;
		if(entityDTO == null || Strings.isNullOrEmpty(fieldName)) return returnValue;

		try {
			JSONArray fieldArray = entityDTO.getJSONArray("fields");
			if(fieldArray != null) {
				boolean found = false;
				for(int i = 0; i < entityDTO.length() && !found; i++) {
					JSONObject thisFieldJson = fieldArray.getJSONObject(i);
					if(thisFieldJson != null) {
						if(fieldName.equals(thisFieldJson.getString("fieldName"))) { 
								returnValue = thisFieldJson.getString("value");
								found = true;
						}
					}
				}
			}
		} catch (JSONException e) {
			logger.error("Error getting fields from DTO ", e);
		}
		return returnValue;
	}
	
	/**
	 * Get the boolean value for the given field from the DTO.
	 * 
	 * @param entityDTO
	 * @param fieldName
	 * @return field value or null
	 */
	public Boolean getBooleanValueForField(JSONObject entityDTO, String fieldName) {
		Boolean returnValue = null;
		if(entityDTO == null || Strings.isNullOrEmpty(fieldName)) return returnValue;

		try {
			JSONArray fieldArray = entityDTO.getJSONArray("fields");
			if(fieldArray != null) {
				boolean found = false;
				for(int i = 0; i < entityDTO.length() && !found; i++) {
					JSONObject thisFieldJson = fieldArray.getJSONObject(i);
					if(thisFieldJson != null) {
						if(fieldName.equals(thisFieldJson.getString("fieldName"))) { 
								returnValue = thisFieldJson.getBoolean("value");
								found = true;
						}
					}
				}
			}
		} catch (JSONException e) {
			logger.error("Error getting fields from DTO ", e);
		}
		return returnValue;
	}
	
	/**
	 * Find the truth value for a decision node based on the entityDTO.
	 * 
	 * Assumption: no more than 2 things can be compared at a time,
	 * therefore return false and log an error if there are more than
	 * 2 statements to compare
	 * 
	 * 
	 * @param decisionNode
	 * @param entityDTO
	 * @return boolean
	 */
	public boolean evaluateDecisionNode(JSONObject decisionNode, JSONObject entityDTO) {
		boolean returnValue = false;
		int size = 10;
		JSONArray decisionArray = null;
		try {
			decisionArray = decisionNode.getJSONArray(decision);
			if(decisionArray.length() > 2) {
				logger.error("Malformed decision node");
				return returnValue;
			}
			Boolean[] decisionValues = new Boolean[size];
			for(int i = 0; i < decisionArray.length();i++) {
				JSONObject thisDecision = decisionArray.getJSONObject(i);
				String thisFieldType = thisDecision.getString(fieldType);
				String thisFieldName = thisDecision.getString(decisionField);
				switch(thisFieldType) {
				case "boolean":
					boolean thisBFieldValue = thisDecision.getBoolean(decisionValue);
					boolean fieldValueFromDTO = getBooleanValueForField(entityDTO, thisFieldName);
					decisionValues[i] = (thisBFieldValue == fieldValueFromDTO);
					break;
				default://default to String
					String thisSFieldValue = thisDecision.getString(decisionValue);
					String sFieldValueFromDTO = getStringValueForField(entityDTO, thisFieldName);
					decisionValues[i] = thisSFieldValue.equals(sFieldValueFromDTO);
				}
			}

			String thisOperator = decisionNode.getString(decisionOperator);
			if(thisOperator.equalsIgnoreCase("or")) {
				returnValue = decisionValues[0] || decisionValues[1];
			}
			returnValue = decisionValues[0] && decisionValues[1];

		} catch (JSONException e) {
			logger.error("Malformed JSON");
			return returnValue;
		}		

		return returnValue;
	}
	
	
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
	
	public JSONObject populateEntityModel(JSONObject entityJson) {
		if(entityJson == null) return null;

		
		long g1Records = g1Repo.count();
		long g2Records = g2Repo.count();
		List<String> textFieldList = null;
		List<String> checkboxFieldList = null;

		String genericTableName = g1Records > g2Records?"GENERIC_TABLE_2":"GENERIC_TABLE_1";
		try {
			entityJson.put(table, genericTableName);


			switch(genericTableName) {
			case "GENERIC_TABLE_1":
				textFieldList = GenericTable1.getTextFieldList();
				checkboxFieldList = GenericTable1.getCheckboxList();
				break;
			case "GENERIC_TABLE_2":
				textFieldList = GenericTable2.getTextFieldList();
				checkboxFieldList = GenericTable2.getCheckboxList();
				break;
			}
			JSONArray fields = entityJson.getJSONArray("fields");
			
			for(int i = 0; i < fields.length(); i++) {
				JSONObject thisNode = fields.getJSONObject(i);
				String type = thisNode.getString(fieldType);
				switch(type) {
					case "text" :
						thisNode.put(column, textFieldList.get(0));
						textFieldList.remove(0);
						break;
					case "checkbox" :
						thisNode.put(column, checkboxFieldList.get(0));
						checkboxFieldList.remove(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error populating entity model", e);
			return null;
		}	
		return entityJson;
	}
	
	public String populateEntityModelStr(String entityModelStr) {
		if(entityModelStr == null) return null;
		String returnJsonStr = null;
		try {
			JSONObject entityJson = new JSONObject(entityModelStr);
			JSONObject populatedJson = populateEntityModel(entityJson);
			if(populatedJson != null) {
				returnJsonStr = populatedJson.toString();
			}
			
		} catch (JSONException e) {
			logger.error("Error populating entity model", e);
		}
		return returnJsonStr;
	}
	
	
	
	public boolean saveEntityValues(String entityDTO) {
		if(entityDTO == null) return false;
		JSONObject entityJson = null;
		try {
			entityJson = new JSONObject(entityDTO);
		} catch (JSONException e) {
			logger.error("Error creating entityDTO json", e);
			return false;
		}
		return saveEntityValues(entityJson);
	}
	
	/**
	 * Save the values from the entity DTO
	 * into the appropriate generic table
	 *  
	 * @param entityDTO
	 * 
	 */
	public boolean saveEntityValues(JSONObject entityDTO) {
		if(entityDTO == null ) return false;
		ParentGenericTableModel tableModel = null;
		String tableName = null;
		JSONArray fieldArray = null;
		try {
			tableName = entityDTO.getString(table);
			fieldArray = entityDTO.getJSONArray(fields);
		} catch (JSONException e) {
			logger.error("Can't create entity DTO", e);
			return false;
		}
		
		switch(tableName) {
		case genericTable1 :
			tableModel = new GenericTable1();
			break;
		case genericTable2 :
			tableModel = new GenericTable2();
		
		}
		
		
		String columnStr = null;
		String valueStr = null;
		for(int i = 0; i < fieldArray.length(); i++) {
			try {
				JSONObject thisNode = fieldArray.getJSONObject(i); 
				columnStr = thisNode.getString(column);
				valueStr = fieldArray.getJSONObject(i).getString(value);
			} catch (JSONException e) {
				logger.debug("no value for column");
				continue;
			}
			if(columnStr != null && valueStr != null) {
				populateColumn(tableModel, columnStr, valueStr);
			}
			
		}
			
		switch(tableName) {
			case genericTable1 :
				g1Repo.save((GenericTable1)tableModel);
				break;
			case genericTable2 :
				tableModel = g2Repo.save((GenericTable2)tableModel);
		}
		
		return tableModel.getId() == 0?false:true;
	}
	
	/**
	 * Side effect: populate the given column with the given value.
	 * 
	 * @param tableModel
	 * @param column
	 * @param value
	 */
	private void populateColumn(ParentGenericTableModel tableModel, String column, String value) {
		
		switch(column.toUpperCase()) {
		case "VARCHAR_45_1" :
			tableModel.setVarchar451(value);
			break;
		case "VARCHAR_45_2" :
			tableModel.setVarchar452(value);
			break;
		case "VARCHAR_45_3" :
			tableModel.setVarchar453(value);
			break;
		case "VARCHAR_45_4" :
			tableModel.setVarchar454(value);
			break;
		case "VARCHAR_45_5" :
			tableModel.setVarchar455(value);
			break;
		case "VARCHAR_45_6" :
			tableModel.setVarchar456(value);
			break;
		case "VARCHAR_45_7" :
			tableModel.setVarchar457(value);
			break;
		case "VARCHAR_45_8" :
			tableModel.setVarchar458(value);
			break;
		case "VARCHAR_45_9" :
			tableModel.setVarchar459(value);
			break;
		case "VARCHAR_45_10" :
			tableModel.setVarchar4510(value);
			break;
		case "CHECKBOX_1" :
			tableModel.setCheckbox1(Integer.parseInt(value));
			break;
		case "CHECKBOX_2" :
			tableModel.setCheckbox2(Integer.parseInt(value));
			break;
		case "CHECKBOX_3" :
			tableModel.setCheckbox3(Integer.parseInt(value));
			break;
		case "CHECKBOX_4" :
			tableModel.setCheckbox4(Integer.parseInt(value));
			break;
		case "CHECKBOX_5" :
			tableModel.setCheckbox5(Integer.parseInt(value));
			break;
		case "CHECKBOX_6" :
			tableModel.setCheckbox6(Integer.parseInt(value));
			break;
		case "CHECKBOX_7" :
			tableModel.setCheckbox7(Integer.parseInt(value));
			break;
		case "CHECKBOX_8" :
			tableModel.setCheckbox8(Integer.parseInt(value));
			break;
		case "CHECKBOX_9" :
			tableModel.setCheckbox9(Integer.parseInt(value));
			break;
		case "CHECKBOX_10" :
			tableModel.setCheckbox10(Integer.parseInt(value));
			break;
		
		}		
	}


}