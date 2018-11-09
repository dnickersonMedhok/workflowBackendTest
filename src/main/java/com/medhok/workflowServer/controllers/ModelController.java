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
import com.medhok.workflowServer.repositories.ModelRepository;

@RestController
public class ModelController {
	
	private static Logger logger = LoggerFactory.getLogger(ModelController.class);
	
	@Autowired
	private ModelRepository modelRepo;
	
	@GetMapping("/models")
	public List<Models> retrieveAllStudents() {
		return modelRepo.findAll();
	}
	
	@GetMapping("/models/{id}")
	public Models retrieveModel(@PathVariable int id) {
		Optional<Models> model = modelRepo.findById(id);

		if (!model.isPresent()) {
			logger.error("Model id " + id + " not found.");//TODO: throw back to the client
		}

		return model.get();
	}
	@PostMapping(value = "/saveModel")
	public ResponseEntity<Object> createStudent(@RequestBody Models model) {
		Models savedModel = modelRepo.save(model);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedModel.getId()).toUri();

		return ResponseEntity.created(location).build();

	}

	@GetMapping("/getModelsByTypeId/{modelTypeId}")
	public List<Models> getModelsByTypeId(@PathVariable Integer modelTypeId) {
		List<Models> models = modelRepo.getModelsByTypeId(modelTypeId);
/*		List<Models> modelList = new ArrayList<>();
		models.forEach(thisModel -> {
			modelList.add(thisModel);
		});*/

		return models;
	}	
}
