package hometasks.cms;

import java.time.LocalDate;

public class Customer {
	private String name;
	private String address;
	private String email;
	private String ccNo;
	private String ccType;
	private LocalDate maturity;

	public Customer() {
	}

	public Customer(String name, String address, String email, String ccNo, String ccType, LocalDate maturity) {
		super();
		this.name = name;
		this.address = address;
		this.email = email;
		this.ccNo = ccNo;
		this.ccType = ccType;
		this.maturity = maturity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCcNo() {
		return ccNo;
	}

	public void setCcNo(String ccNo) {
		this.ccNo = ccNo;
	}

	public String getCcType() {
		return ccType;
	}

	public void setCcType(String ccType) {
		this.ccType = ccType;
	}

	public LocalDate getMaturity() {
		return maturity;
	}

	public void setMaturity(LocalDate maturity) {
		this.maturity = maturity;
	}
	
}
