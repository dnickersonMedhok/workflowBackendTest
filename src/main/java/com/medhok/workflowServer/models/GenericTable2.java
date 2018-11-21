package com.medhok.workflowServer.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GENERIC_TABLE_2")
public class GenericTable2  extends ParentGenericTableModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	@Column(name = "VARCHAR_45_1", length = 45)
	private String varchar451;
	@Column(name = "VARCHAR_45_2", length = 45)
	private String varchar452;
	@Column(name = "VARCHAR_45_3", length = 45)
	private String varchar453;
	@Column(name = "VARCHAR_45_4", length = 45)
	private String varchar454;
	@Column(name = "VARCHAR_45_5", length = 45)
	private String varchar455;
	@Column(name = "VARCHAR_45_6", length = 45)
	private String varchar456;
	@Column(name = "VARCHAR_45_7", length = 45)
	private String varchar457;
	@Column(name = "VARCHAR_45_8", length = 45)
	private String varchar458;
	@Column(name = "VARCHAR_45_9", length = 45)
	private String varchar459;
	@Column(name = "VARCHAR_45_10", length = 45)
	private String varchar4510;
	@Column(name = "CHECKBOX_1")
	private int checkbox1;
	@Column(name = "CHECKBOX_2")
	private int checkbox2;
	@Column(name = "CHECKBOX_3")
	private int checkbox3;
	@Column(name = "CHECKBOX_4")
	private int checkbox4;
	@Column(name = "CHECKBOX_5")
	private int checkbox5;
	@Column(name = "CHECKBOX_6")
	private int checkbox6;
	@Column(name = "CHECKBOX_7")
	private int checkbox7;
	@Column(name = "CHECKBOX_8")
	private int checkbox8;
	@Column(name = "CHECKBOX_9")
	private int checkbox9;
	@Column(name = "CHECKBOX_10")
	private int checkbox10;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVarchar451() {
		return varchar451;
	}
	public void setVarchar451(String varchar451) {
		this.varchar451 = varchar451;
	}
	public String getVarchar452() {
		return varchar452;
	}
	public void setVarchar452(String varchar452) {
		this.varchar452 = varchar452;
	}
	public String getVarchar453() {
		return varchar453;
	}
	public void setVarchar453(String varchar453) {
		this.varchar453 = varchar453;
	}
	public String getVarchar454() {
		return varchar454;
	}
	public void setVarchar454(String varchar454) {
		this.varchar454 = varchar454;
	}
	public String getVarchar455() {
		return varchar455;
	}
	public void setVarchar455(String varchar455) {
		this.varchar455 = varchar455;
	}
	public String getVarchar456() {
		return varchar456;
	}
	public void setVarchar456(String varchar456) {
		this.varchar456 = varchar456;
	}
	public String getVarchar457() {
		return varchar457;
	}
	public void setVarchar457(String varchar457) {
		this.varchar457 = varchar457;
	}
	public String getVarchar458() {
		return varchar458;
	}
	public void setVarchar458(String varchar458) {
		this.varchar458 = varchar458;
	}
	public String getVarchar459() {
		return varchar459;
	}
	public void setVarchar459(String varchar459) {
		this.varchar459 = varchar459;
	}
	public String getVarchar4510() {
		return varchar4510;
	}
	public void setVarchar4510(String varchar4510) {
		this.varchar4510 = varchar4510;
	}
	public int getCheckbox1() {
		return checkbox1;
	}
	public void setCheckbox1(int checkbox1) {
		this.checkbox1 = checkbox1;
	}
	public int getCheckbox2() {
		return checkbox2;
	}
	public void setCheckbox2(int checkbox2) {
		this.checkbox2 = checkbox2;
	}
	public int getCheckbox3() {
		return checkbox3;
	}
	public void setCheckbox3(int checkbox3) {
		this.checkbox3 = checkbox3;
	}
	public int getCheckbox4() {
		return checkbox4;
	}
	public void setCheckbox4(int checkbox4) {
		this.checkbox4 = checkbox4;
	}
	public int getCheckbox5() {
		return checkbox5;
	}
	public void setCheckbox5(int checkbox5) {
		this.checkbox5 = checkbox5;
	}
	public int getCheckbox6() {
		return checkbox6;
	}
	public void setCheckbox6(int checkbox6) {
		this.checkbox6 = checkbox6;
	}
	public int getCheckbox7() {
		return checkbox7;
	}
	public void setCheckbox7(int checkbox7) {
		this.checkbox7 = checkbox7;
	}
	public int getCheckbox8() {
		return checkbox8;
	}
	public void setCheckbox8(int checkbox8) {
		this.checkbox8 = checkbox8;
	}
	public int getCheckbox9() {
		return checkbox9;
	}
	public void setCheckbox9(int checkbox9) {
		this.checkbox9 = checkbox9;
	}
	public int getCheckbox10() {
		return checkbox10;
	}
	public void setCheckbox10(int checkbox10) {
		this.checkbox10 = checkbox10;
	}
	
	public static List<String> getTextFieldList() {
		List<String> textFieldList = new ArrayList<>();
		textFieldList.add("varchar451");
		textFieldList.add("varchar452");
		textFieldList.add("varchar453");
		textFieldList.add("varchar454");
		textFieldList.add("varchar455");
		textFieldList.add("varchar456");
		textFieldList.add("varchar457");
		textFieldList.add("varchar458");
		textFieldList.add("varchar459");
		textFieldList.add("varchar4510");
	
		return textFieldList;
	}
	
	public static List<String> getCheckboxList() {
		List<String> checkboxFieldList = new ArrayList<>();
		checkboxFieldList.add("checkbox1");
		checkboxFieldList.add("checkbox2");
		checkboxFieldList.add("checkbox3");
		checkboxFieldList.add("checkbox4");
		checkboxFieldList.add("checkbox5");
		checkboxFieldList.add("checkbox6");
		checkboxFieldList.add("checkbox7");
		checkboxFieldList.add("checkbox8");
		checkboxFieldList.add("checkbox9");
		checkboxFieldList.add("checkbox10");

		
		return checkboxFieldList;
	}
}
