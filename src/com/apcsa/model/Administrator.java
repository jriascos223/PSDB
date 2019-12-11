package com.apcsa.model;

import com.apcsa.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrator extends User {
	
	private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    
    public Administrator(User user, ResultSet rs) throws SQLException {
		super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());
		
		this.administratorId = rs.getInt("administrator_id");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
		this.jobTitle = rs.getString("job_title");
		
	}
    
    /*
     * Getters
     */
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public static void viewFaculty() {
		// TODO Auto-generated method stub
		
	}

	public static void viewFacultyByDept() {
		// TODO Auto-generated method stub
		
	}

	public static void viewStudentEnrollment() {
		// TODO Auto-generated method stub
		
	}

	public static void viewStudentEnrollmentByGrade() {
		// TODO Auto-generated method stub
		
	}

	public static void viewStudentEnrollmentByCourse() {
		// TODO Auto-generated method stub
		
	}


    
    
    
    
}