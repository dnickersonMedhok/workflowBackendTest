package com.medhok.workflowServer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import com.medhok.workflowServer.models.GenericTable1;
import com.medhok.workflowServer.repositories.GenericTable1Repository;
import com.medhok.workflowServer.utils.WorkflowUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages={"com.medhok.workflowServer"})
public class WorkflowUtilsTest {
	
	private Logger logger = LoggerFactory.getLogger(WorkflowUtilsTest.class);
	
	@Autowired
	private WorkflowUtils utils;
	
	@Autowired
	GenericTable1Repository g1repo;
	
	final String testWorkflowModelStr = "{\n" + 
			"  \"step1\": {\n" + 
			"    \"task1\": {\n" + 
			"      \"parentFormId\": \"1\",\n" + 
			"      \"workFlowStatus\": \"1\",\n" + 
			"      \"decision\": [\n" + 
			"        {\n" + 
			"          \"multiDecision\": {\n" + 
			"            \"decisionArray\": [\n" + 
			"              {\n" + 
			"                \"descisionField\": \"Status\",\n" + 
			"                \"value\": \"Cancelled\"\n" + 
			"              },\n" + 
			"              {\n" + 
			"                \"descisionField\": \"Submitted By\",\n" + 
			"                \"value\": \"Provider\"\n" + 
			"              }\n" + 
			"            ],\n" + 
			"            \"nextStepId\": 2\n" + 
			"          }\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"descisionField\": \"Status\",\n" + 
			"          \"value\": \"In Progress\",\n" + 
			"          \"nextScreenId\": 3\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    },\n" + 
			"    \"stepId\": \"1\"\n" + 
			"  },\n" + 
			"  \"step2\": {\n" + 
			"    \"stepId\": 2,\n" + 
			"    \"task1\": {\n" + 
			"      \"workFlowStatus\": \"2\",\n" + 
			"      \"decision\": [\n" + 
			"        {\n" + 
			"          \"descisionField\": \"Status Reason\",\n" + 
			"          \"value\": \"Data Entry Error\",\n" + 
			"          \"nextScreenId\": 4\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"descisionField\": \"Status Reason\",\n" + 
			"          \"value\": \"Carelink\",\n" + 
			"          \"nextScreenId\": 5\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    }\n" + 
			"  },\n" + 
			"  \"step3\": {\n" + 
			"    \"stepId\": 3,\n" + 
			"    \"task1\": {\n" + 
			"      \"workFlowStatus\": \"2\"\n" + 
			"    }\n" + 
			"  },\n" + 
			"  \"step4\": {\n" + 
			"    \"stepId\": 4,\n" + 
			"    \"task1\": {\n" + 
			"      \"workFlowStatus\": \"3\"\n" + 
			"    }\n" + 
			"  },\n" + 
			"  \"step5\": {\n" + 
			"    \"stepId\": 5,\n" + 
			"    \"task1\": {\n" + 
			"      \"workFlowStatus\": \"3\"\n" + 
			"    }\n" + 
			"  },\n" + 
			"      \"connections\": [\n" + 
			"      {\n" + 
			"        \"sourceId\": \"1\",\n" + 
			"        \"targetId\": \"2\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"sourceId\": \"2\",\n" + 
			"        \"targetId\": \"3\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"data\": {\n" + 
			"          \"condition\": \"true\"\n" + 
			"        },\n" + 
			"        \"sourceId\": \"3\",\n" + 
			"        \"targetId\": \"4\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"data\": {\n" + 
			"          \"condition\": \"false\"\n" + 
			"        },\n" + 
			"        \"sourceId\": \"3\",\n" + 
			"        \"targetId\": \"5\"\n" + 
			"      }\n" + 
			"    ],\n" + 
			"    \"nodes\": [\n" + 
			"      {\n" + 
			"        \"step\": \"step1\",\n" + 
			"        \"config\": {\n" + 
			"          \"label\": \"Step 1\",\n" + 
			"          \"type\": \"transform\"\n" + 
			"        },\n" + 
			"        \"id\": \"1\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"step\": \"step2\",\n" + 
			"        \"config\": {\n" + 
			"          \"label\": \"Step 2\",\n" + 
			"          \"type\": \"transform\"\n" + 
			"        },\n" + 
			"        \"id\": \"2\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"step\": \"step3\",\n" + 
			"        \"config\": {\n" + 
			"          \"label\": \"Conditional logic\",\n" + 
			"          \"type\": \"condition\"\n" + 
			"        },\n" + 
			"        \"id\": \"3\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"step\": \"step4\",\n" + 
			"        \"config\": {\n" + 
			"          \"label\": \"Step 3\",\n" + 
			"          \"type\": \"transform\"\n" + 
			"        },\n" + 
			"        \"id\": \"4\"\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"step\": \"step5\",\n" + 
			"        \"config\": {\n" + 
			"          \"label\": \"Step 4\",\n" + 
			"          \"type\": \"transform\"\n" + 
			"        },\n" + 
			"        \"id\": \"5\"\n" + 
			"      }\n" + 
			"    ]\n" + 
			"}";
	
	
	final String testJsonStr = "{\n" + 
			"  \"rules\": [\n" + 
			"    {\n" + 
			"      \"rule1\": \"isChecked\"\n" + 
			"    }\n" + 
			"  ],\n" + 
			"  \"nodes\": [\n" + 
			"    {\n" + 
			"      \"id\": \"start\",\n" + 
			"      \"stepId\": \"step1\",\n" + 
			"      \"type\": \"start\",\n" + 
			"      \"text\": \"Start\",\n" + 
			"      \"left\": 50,\n" + 
			"      \"top\": 50,\n" + 
			"      \"w\": 100,\n" + 
			"      \"h\": 70\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"id\": \"question1\",\n" + 
			"      \"type\": \"question\",\n" + 
			"      \"stepId\": \"step1\",\n" + 
			"      \"rule\": \"rule1\",\n" + 
			"      \"text\": \"Do Something?\",\n" + 
			"      \"left\": 316,\n" + 
			"      \"top\": 61,\n" + 
			"      \"w\": 150,\n" + 
			"      \"h\": 150\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"id\": \"decide\",\n" + 
			"      \"type\": \"action\",\n" + 
			"      \"text\": \"Make Decision\",\n" + 
			"      \"left\": 590,\n" + 
			"      \"top\": 273,\n" + 
			"      \"w\": 120,\n" + 
			"      \"h\": 120\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"id\": \"something\",\n" + 
			"      \"type\": \"output\",\n" + 
			"      \"text\": \"Do Something\",\n" + 
			"      \"left\": 827,\n" + 
			"      \"top\": 414,\n" + 
			"      \"w\": 120,\n" + 
			"      \"h\": 50\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"id\": \"question2\",\n" + 
			"      \"type\": \"question\",\n" + 
			"      \"text\": \"Do Nothing?\",\n" + 
			"      \"left\": 215,\n" + 
			"      \"top\": 293,\n" + 
			"      \"w\": 150,\n" + 
			"      \"h\": 150\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"id\": \"nothing\",\n" + 
			"      \"type\": \"output\",\n" + 
			"      \"text\": \"Do Nothing\",\n" + 
			"      \"left\": 396,\n" + 
			"      \"top\": 548,\n" + 
			"      \"w\": 100,\n" + 
			"      \"h\": 50\n" + 
			"    }\n" + 
			"  ],\n" + 
			"  \"edges\": [\n" + 
			"    {\n" + 
			"      \"source\": \"start\",\n" + 
			"      \"target\": \"question1\",\n" + 
			"      \"data\": {}\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"question1\",\n" + 
			"      \"target\": \"decide\",\n" + 
			"      \"rule1\": true,\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"yes\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      },\n" + 
			"      \"geometry\": {\n" + 
			"        \"segments\": [\n" + 
			"          [\n" + 
			"            496,\n" + 
			"            136\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            650,\n" + 
			"            136\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            650,\n" + 
			"            243\n" + 
			"          ]\n" + 
			"        ],\n" + 
			"        \"source\": [\n" + 
			"          466,\n" + 
			"          136,\n" + 
			"          1,\n" + 
			"          0.5,\n" + 
			"          1,\n" + 
			"          0\n" + 
			"        ],\n" + 
			"        \"target\": [\n" + 
			"          650,\n" + 
			"          273,\n" + 
			"          0.5,\n" + 
			"          0,\n" + 
			"          0,\n" + 
			"          -1\n" + 
			"        ]\n" + 
			"      }\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"question1\",\n" + 
			"      \"target\": \"question2\",\n" + 
			"      \"rule1\": false,\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"no\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      }\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"decide\",\n" + 
			"      \"target\": \"nothing\",\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"Can't Decide\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      },\n" + 
			"      \"geometry\": {\n" + 
			"        \"segments\": [\n" + 
			"          [\n" + 
			"            650,\n" + 
			"            423\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            650,\n" + 
			"            630\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            453,\n" + 
			"            630\n" + 
			"          ]\n" + 
			"        ],\n" + 
			"        \"source\": [\n" + 
			"          650,\n" + 
			"          393,\n" + 
			"          0.5,\n" + 
			"          1,\n" + 
			"          0,\n" + 
			"          1\n" + 
			"        ],\n" + 
			"        \"target\": [\n" + 
			"          423,\n" + 
			"          630,\n" + 
			"          1,\n" + 
			"          0.5,\n" + 
			"          1,\n" + 
			"          0\n" + 
			"        ]\n" + 
			"      }\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"decide\",\n" + 
			"      \"target\": \"something\",\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"Decision Made\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      }\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"question2\",\n" + 
			"      \"target\": \"decide\",\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"no\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      }\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"source\": \"question2\",\n" + 
			"      \"target\": \"nothing\",\n" + 
			"      \"data\": {\n" + 
			"        \"label\": \"yes\",\n" + 
			"        \"type\": \"connection\"\n" + 
			"      },\n" + 
			"      \"geometry\": {\n" + 
			"        \"segments\": [\n" + 
			"          [\n" + 
			"            185,\n" + 
			"            368\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            185,\n" + 
			"            630\n" + 
			"          ],\n" + 
			"          [\n" + 
			"            293,\n" + 
			"            630\n" + 
			"          ]\n" + 
			"        ],\n" + 
			"        \"source\": [\n" + 
			"          215,\n" + 
			"          368,\n" + 
			"          0,\n" + 
			"          0.5,\n" + 
			"          -1,\n" + 
			"          0\n" + 
			"        ],\n" + 
			"        \"target\": [\n" + 
			"          323,\n" + 
			"          630,\n" + 
			"          0,\n" + 
			"          0.5,\n" + 
			"          -1,\n" + 
			"          0\n" + 
			"        ]\n" + 
			"      }\n" + 
			"    }\n" + 
			"  ],\n" + 
			"  \"ports\": [],\n" + 
			"  \"groups\": []\n" + 
			"}\n" + 
			"";
	
	final String inputEntityString = "{\n" + 
			"  \"entityName\": \"asdfsd\",\n" + 
			"  \"parentTable\": \"\",\n" + 
			"  \"fields\": [\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f1\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f2\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f3\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f4\",\n" + 
			"      \"fieldType\": \"checkbox\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f5\",\n" + 
			"      \"fieldType\": \"checkbox\"\n" + 
			"    }\n" + 
			"  ]\n" + 
			"}";
	
	final String inputEntityValueString = "{\n" + 
			"  \"entityName\": \"asdfsd\",\n" + 
			"  \"parentTable\": \"\",\n" + 
			"  \"table\": \"GENERIC_TABLE_1\",\n" + 
			"  \"fields\": [\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"column\": \"varchar_45_1\",\n" + 
			"      \"value\": \"field f value\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f1\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"column\": \"varchar_45_2\",\n" + 
			"      \"value\": \"field f1 value\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f2\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"column\": \"varchar_45_3\",\n" + 
			"      \"value\": \"field f2 value\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f3\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"column\": \"varchar_45_4\",\n" + 
			"      \"value\": \"field f3 value\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f4\",\n" + 
			"      \"fieldType\": \"checkbox\",\n" + 
			"      \"column\": \"checkbox_1\",\n" + 
			"      \"value\": \"1\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f5\",\n" + 
			"      \"fieldType\": \"checkbox\",\n" + 
			"      \"column\": \"varchar_45_1\",\n" + 
			"      \"value\": \"1\"\n" + 
			"    }\n" + 
			"  ]\n" + 
			"}";
	
	final String testEntityDTOStr = "{\n" + 
			"  \"entityName\": \"asdfsd\",\n" + 
			"  \"parentTable\": \"\",\n" + 
			"  \"table\": \"GENERIC_TABLE_1\",\n" + 
			"  \"fields\": [\n" + 
			"    {\n" + 
			"      \"fieldName\": \"Status\",\n" + 
			"      \"column\": \"varchar451\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"value\": \"Cancelled\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"Submitted by\",\n" + 
			"      \"column\": \"varchar452\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"value\": \"Provider\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"Status Reason\",\n" + 
			"      \"column\": \"varchar453\",\n" + 
			"      \"fieldType\": \"text\",\n" + 
			"      \"value\": \"Data Entry Error\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f2\",\n" + 
			"      \"column\": \"varchar454\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f\",\n" + 
			"      \"column\": \"varchar455\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"f2\",\n" + 
			"      \"column\": \"varchar456\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"d6\",\n" + 
			"      \"column\": \"varchar457\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"jhjh\",\n" + 
			"      \"column\": \"varchar458\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"123\",\n" + 
			"      \"column\": \"varchar459\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"fieldName\": \"ert\",\n" + 
			"      \"column\": \"varchar4510\",\n" + 
			"      \"fieldType\": \"text\"\n" + 
			"    }\n" + 
			"  ],\n" + 
			"  \"table\": \"GENERIC_TABLE_1\"\n" + 
			"}";
	
	final static String testDecision = "        {\n" + 
			"          \"decision\": [\n" + 
			"            {\n" + 
			"              \"decisionField\": \"Status\",\n" + 
			"              \"decisionValue\": \"Cancelled\",\n" + 
			"              \"fieldType\": \"String\"\n" + 
			"            },\n" + 
			"            {\n" + 
			"              \"decisionField\": \"Status Reason\",\n" + 
			"              \"decisionValue\": \"Data Entry Error\",\n" + 
			"              \"fieldType\": \"String\"\n" + 
			"            }\n" + 
			"          ],\n" + 
			"          \"decisionOperator\": \"and\"\n" + 
			"        }";
	
	@Test
	public void testGetValueForField() throws Exception {
		JSONObject entityDTO = new JSONObject(testEntityDTOStr);
		
		String valueStr = utils.getStringValueForField(entityDTO, "Status");
		assertEquals(valueStr, "Cancelled");
		valueStr = utils.getStringValueForField(entityDTO, "Submitted by");
		assertEquals(valueStr, "Provider");
		valueStr = utils.getStringValueForField(entityDTO, "Doesn't exist");
		assertNull(valueStr);
	}
	
	
	@Test
	public void testEvaluateDecisionNode() throws Exception {
		JSONObject testDecisionJson = new JSONObject(testDecision);
		JSONObject testEntityDTO = new JSONObject(testEntityDTOStr);
		assertTrue(utils.evaluateDecisionNode(testDecisionJson, testEntityDTO));
	}
	
	
	@Test
	public void testGetNextStep() throws Exception {
		String thisStepId = "step1";
		Boolean value = true;
		JSONObject testJson = new JSONObject(testJsonStr);
		
		String nextStep = utils.getNextStepFromRule(testJson, thisStepId, value);
		assertEquals(nextStep, "decide");
		value = false;
		nextStep = utils.getNextStepFromRule(testJson, thisStepId, value);
		assertEquals(nextStep, "question2");
		nextStep = utils.getNextStepFromRule(testJson, thisStepId, null);
		assertNull(nextStep);
	}
	
	@Ignore
	@Test
	public void testPopulateEntity() throws Exception {
		JSONObject inputJson = new JSONObject(inputEntityString);

		JSONObject outputTestJson = utils.populateEntityModel(inputJson);
		//might be 1 or 2
		//assertEquals(outputTestJson.getString("table"), "GENERIC_TABLE_1");
		JSONArray testArray = outputTestJson.getJSONArray("fields");
		
		JSONObject obj1 = testArray.getJSONObject(0);
		assertEquals(obj1.getString("column"), "varchar451");
		JSONObject obj2 = testArray.getJSONObject(1);
		assertEquals(obj2.getString("column"), "varchar452");
		JSONObject obj3 = testArray.getJSONObject(2);
		assertEquals(obj3.getString("column"), "varchar453");
		JSONObject obj4 = testArray.getJSONObject(3);
		assertEquals(obj4.getString("column"), "varchar454");
		JSONObject obj5 = testArray.getJSONObject(4);
		assertEquals(obj5.getString("column"), "checkbox1");
		JSONObject obj6 = testArray.getJSONObject(5);
		assertEquals(obj6.getString("column"), "checkbox2");		
	}
	
	@Ignore
	@Test
	public void testSaveValues() throws Exception {
		
		JSONObject entityDTO = new JSONObject(inputEntityValueString);
		assertTrue(utils.saveEntityValues(entityDTO));
	}
	
}