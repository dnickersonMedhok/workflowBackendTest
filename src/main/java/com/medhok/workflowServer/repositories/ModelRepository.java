package com.medhok.workflowServer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.medhok.workflowServer.models.Models;

@Repository
public interface ModelRepository extends JpaRepository<Models, Integer> {

	@Query("from Models where model_type_id = :modelTypeId")
	public List<Models> getModelsByTypeId(@Param("modelTypeId") Integer ModelTypeId);
	
}
