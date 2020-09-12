package com.personal.api.users.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name= "users")
public class UserEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6755648826702679360L;
	
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false, length=50)
	private String firstName;
	
	@Column(nullable = false, length=50)
	private String lastName;
	
	@Column(nullable = false, length=120, unique = true)
	private String email;
	
	@Column(nullable = false, unique = true)
	private String userId;
	
	@Column(nullable = false, unique = true)
	private String encryptedPassword;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES", joinColumns = {
            @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID") })
    private Set<Role> roles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}	
}
