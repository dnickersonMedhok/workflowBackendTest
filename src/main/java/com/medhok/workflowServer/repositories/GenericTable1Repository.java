package com.medhok.workflowServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medhok.workflowServer.models.GenericTable1;

@Repository
public interface GenericTable1Repository  extends JpaRepository <GenericTable1, Integer> {

}
