package com.apcsa.model;

import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

import java.sql.Connection;
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

	public void changePassword(String password) {
		this.setPassword(password);
		try {
			Connection conn = PowerSchool.getConnection();
			PowerSchool.updatePassword(conn, this.getUsername(), password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


    
    
    
    
}