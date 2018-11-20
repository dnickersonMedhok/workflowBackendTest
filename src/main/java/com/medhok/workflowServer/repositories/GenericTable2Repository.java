package com.medhok.workflowServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medhok.workflowServer.models.GenericTable2;

@Repository
public interface GenericTable2Repository  extends JpaRepository<GenericTable2, Integer> {

}
