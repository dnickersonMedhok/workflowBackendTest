package com.medhok.workflowServer.utils;

import java.util.List;

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
	static final String nextStepId = "nextStepId";
	static final String tasks = "tasks";
	static final String taskId = "taskId";
	static final String decisionGroup = "decisionGroup";
	static final String criteria = "criteria";
	static final String criteriaOperator = "criteriaOperator";
	static final String decisionField = "decisionField";
	static final String decision = "decision";
	static final String decisions = "decisions";
	static final String decisionValue = "decisionValue";
	static final String decisionOperator = "decisionOperator";
	static final String decisionArray = "decisionArray";
	
	/**
	 * This is the entry point for finding the next step id when an entry is submitted
	 * 
	 * 
	 * @param workflowModel
	 * @param currentStepId
	 * @param entityDTO
	 * @return next step id or null
	 */
	public String getNextStepId(JSONObject workflowModel, String currentStepId, JSONObject entityDTO) {
		String foundNextStepId = null;
		JSONArray decisionsArray = null;
		
		JSONObject stepNode = findStepNode(workflowModel, currentStepId);
		if(stepNode == null) {
			logger.error("Step not found for step id " + currentStepId);
			return foundNextStepId;
		}
		
		try {
			decisionsArray = stepNode.getJSONArray(decisions);
		} catch (JSONException e) {
			logger.error("No decisions found for step id " + currentStepId);
			return foundNextStepId;
		}
		
		boolean found = false;
		for (int i = 0; i < decisionsArray.length() && !found; i++) {
			JSONObject thisDecision = null;
			try {
				thisDecision = decisionsArray.getJSONObject(i);
			} catch (JSONException e) {
				logger.error("Malformed JSON", e);
				continue;
			}
			if(thisDecision != null) {
				found = evaluateDecisionGroup(thisDecision, entityDTO);
				if(found) {
					try {
						foundNextStepId = thisDecision.getString(nextStepId);
					} catch (JSONException e) {
						logger.error("No next step id for current step id " + currentStepId);
					}			
				}
			}
		}
		
		return foundNextStepId;
	}
	
	/**
	 * Recursive method to evaluate the decision for a workflow step.
	 * 
	 * example workflow decision model:
	 * ((a == 1 && b == 2) || (c == 3)) 
	 * 
	 * definitions:
	 * a, b and c are all in the decision for this workflow step
	 * a and b are in the first decision group, c is in the second decision group
	 * a is in the first criteria, and b is in the second criteria of the first decision group.
	 * c is the only criteria of the second decision group.
	 * The decisionOperator is ||
	 * The criteriaOperator for the only 2 criteria in the first decision group is &&
	 * When the decisionOperator and second decisionGroup are found then the method
	 * would be recursively called with the node for the second decision group.
	 * 
	 * Note: If there is no decision array but there is a next step id then evaluate
	 * 		 as always true.  This would mean that we ALWAYS go to that step
	 * 
	 * @param node - This would represent the entire decision for a workflow step upon the
	 * 				first cycle. All other cycles would represent decision groups within the decision
	 * @param entityDTO - The values entered for this entity
	 * @return truth value for the decision group. For the first cycle this would represent the
	 * 			decision evaluation.
	 */
	public Boolean evaluateDecisionGroup(JSONObject node, JSONObject entityDTO) {
		Boolean returnValue = false;

		if(node == null || entityDTO == null) {
			logger.error("malformed workflow");
			return returnValue;
		}
		JSONArray thisDecisionArray = null;
		try {
			thisDecisionArray = node.getJSONArray(decisionArray);
		} catch (JSONException e) {
			//There's no decision array, either there's no decision
			//and we always go to the next step id or the json is malformed
			//continue for now
		}
		if(thisDecisionArray == null) {
			//We never use nextStep id, just testing here if
			//it exists
			String foundNextStepId = null;
			try {
				foundNextStepId = node.getString(nextStepId);
			} catch (JSONException e) {
				logger.error("No decisions or next step id in the step node. Malformed JSON");
				return returnValue;
			}
			//there's no decision array but there IS a next step id
			//so assume that we should ALWAYS go to that step
			return true;
		}
		//All decision groups have a decision array
		for(int i = 0; i < thisDecisionArray.length();i++) {
			JSONObject thisCriteria = null;
			try {
				thisCriteria = thisDecisionArray.getJSONObject(i);
			} catch (JSONException e) {
				logger.error("malformed workflow node", e);
				continue;
			}
			if(thisCriteria != null) {
				Boolean criteriaValue = evaluateCriteria(thisCriteria, entityDTO);
				String operator = null;
				try {
					operator = thisCriteria.getString(criteriaOperator);
				} catch (JSONException e) {
					//First criteria won't have this element
					returnValue = criteriaValue;
					continue;
				}
				if(operator == null) {
					returnValue = criteriaValue;
					continue;
				}
				
				if(operator.equalsIgnoreCase("and")) {
					returnValue = criteriaValue && returnValue;
				} else {
					returnValue = criteriaValue || returnValue;
				}				
			}
		}

		JSONObject thisDecisionGroup = null;
		String thisDecisionOperator = null;
		try {
			thisDecisionGroup = node.getJSONObject(decisionGroup);
			thisDecisionOperator = node.getString(decisionOperator);
		} catch (JSONException e) {
			//May not have these elements, if we don't then done for this cycle
			return returnValue;
		}
		if(thisDecisionGroup != null && thisDecisionOperator != null) {
			//recursive call for the nested decision group
			Boolean thisDecisionGroupValue = evaluateDecisionGroup(thisDecisionGroup, entityDTO);
			if(thisDecisionOperator.equalsIgnoreCase("and")) {
				returnValue = thisDecisionGroupValue && returnValue;
			} else {
				returnValue = thisDecisionGroupValue || returnValue;
			}
		}
		
		return returnValue;
	}
	
	public Boolean evaluateCriteria(JSONObject decisionNode, JSONObject entityDTO) {
		Boolean returnValue = false;
		
		if(decisionNode == null || entityDTO == null) {
			logger.error("invalid args to evaluateCriteria");
			return returnValue;
		}

		try {
			String thisFieldType = decisionNode.getString(fieldType);
			String thisFieldName = decisionNode.getString(decisionField);
			switch(thisFieldType) {
			case "boolean":
				boolean thisBFieldValue = decisionNode.getBoolean(decisionValue);
				boolean fieldValueFromDTO = getBooleanValueForField(entityDTO, thisFieldName);
				returnValue = (thisBFieldValue == fieldValueFromDTO);
				break;
			default://default to String
				String thisSFieldValue = decisionNode.getString(decisionValue);
				String sFieldValueFromDTO = getStringValueForField(entityDTO, thisFieldName);
				returnValue = thisSFieldValue.trim().equalsIgnoreCase(sFieldValueFromDTO.trim());
			}
		} catch (JSONException e) {
			logger.error("Malformed json");
		}

		return returnValue;
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
		String returnValue = "";
		if(entityDTO == null || Strings.isNullOrEmpty(fieldName)) return returnValue;

		try {
			JSONArray fieldArray = entityDTO.getJSONArray("fields");
			if(fieldArray != null) {
				boolean found = false;
				for(int i = 0; i < entityDTO.length() && !found; i++) {
					JSONObject thisFieldJson = fieldArray.getJSONObject(i);
					if(thisFieldJson != null) {
						String thisFieldName = null;
						try {
							thisFieldName = thisFieldJson.getString(decisionField);
						} catch (JSONException e) {
							logger.debug("No field name in this entity ");
						}
						
						if((thisFieldName != null) && fieldName.equals(thisFieldName)) { 
								returnValue = thisFieldJson.getString(decisionValue);
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
		Boolean returnValue = false;
		if(entityDTO == null || Strings.isNullOrEmpty(fieldName)) return returnValue;

		try {
			JSONArray fieldArray = entityDTO.getJSONArray("fields");
			if(fieldArray != null) {
				boolean found = false;
				for(int i = 0; i < fieldArray.length() && !found; i++) {
					JSONObject thisFieldJson = fieldArray.getJSONObject(i);
					String thisFieldName = null;
					try {
						thisFieldName = thisFieldJson.getString(decisionField);
					} catch (JSONException e) {
						logger.debug("No such field name in this entity: " + fieldName);
					}
					if(thisFieldName != null && thisFieldJson != null) {
						if(fieldName.equals(thisFieldName)) { 
								returnValue = thisFieldJson.getBoolean(decisionValue);
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