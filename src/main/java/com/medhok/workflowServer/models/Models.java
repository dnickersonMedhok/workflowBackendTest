package com.medhok.workflowServer.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MODELS")
public class Models {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "ORG_ID")
	private int orgId;
	@Column(name = "MODEL_TYPE_ID")
	private int modelTypeId;
	@Column(name = "NAME", length = 100)
	private String name;
	@Column(name = "CONTENT", length = 1000)
	private String content;
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getOrgId() {
		return orgId;
	}
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}
		
	public Integer getModelTypeId() {
		return modelTypeId;
	}

	public void setModelTypeId(Integer modelTypeId) {
		this.modelTypeId = modelTypeId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
