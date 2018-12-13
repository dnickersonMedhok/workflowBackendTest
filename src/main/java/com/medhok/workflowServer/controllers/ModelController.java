package com.medhok.workflowServer.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.medhok.workflowServer.models.Models;
import com.medhok.workflowServer.models.ParentGenericTableModel;
import com.medhok.workflowServer.repositories.ModelRepository;
import com.medhok.workflowServer.utils.WorkflowUtils;

@RestController
public class ModelController {
	
	private static Logger logger = LoggerFactory.getLogger(ModelController.class);
	
	@Autowired
	private ModelRepository modelRepo;
	
	@Autowired
	private WorkflowUtils utils;
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/models")
	public List<Models> retrieveAllModels() {
		return modelRepo.findAll();
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/models/{id}")
	public Models retrieveModel(@PathVariable int id) {
		Optional<Models> model = modelRepo.findById(id);

		if (!model.isPresent()) {
			logger.error("Model id " + id + " not found.");//TODO: throw back to the client
		}

		return model.get();
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(value = "/saveModel")
	public ResponseEntity<Object> saveModel(@RequestBody Models model) {
		//For entity models select the generic table and columns and populate the json
		if(model.getModelTypeId() == WorkflowUtils.ENTITY) {
			String entityJsonStr = model.getContent();
			if(entityJsonStr != null) {
				String populatedEntityStr = utils.populateEntityModelStr(entityJsonStr);
				if(populatedEntityStr != null) {
					model.setContent(populatedEntityStr);
				}
			}
		}

		Models savedModel = modelRepo.save(model);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedModel.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(value = "/saveEntityDTO")
	public boolean saveEntityDTO(@RequestBody Models model) {
		
		String entityJsonStr = model.getContent();
		return utils.saveEntityValues(entityJsonStr);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getModelsByTypeId/{modelTypeId}")
	public List<Models> getModelsByTypeId(@PathVariable Integer modelTypeId) {
		List<Models> models = modelRepo.getModelsByTypeId(modelTypeId);

		return models;
	}	
	
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getFormModelsByEntityId/{referenceEntityId}")
	public List<Models> getFormModelsByEntityId(@PathVariable Integer referenceEntityId) {
		List<Models> models = modelRepo.getFormModelsByEntityId(referenceEntityId);

		return models;
	}	
}
