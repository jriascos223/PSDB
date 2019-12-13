package com.apcsa.model;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;
import com.apcsa.data.QueryUtils;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrator extends User {
	
	private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    
    public Administrator(User user, ResultSet rs) throws SQLException {
		super(user);
		
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
		try {
			Connection conn = PowerSchool.getConnection();
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_FACULTY);
			
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.print("\n");
				while (rs.next()) {
					System.out.println(rs.getString("Phrase"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e){
			System.out.println(e);
		}
	}

	public static void viewFacultyByDept() {
		
	}

	public static void viewStudentEnrollment() {
		try {
			Connection conn = PowerSchool.getConnection();
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS);
			
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("\n");
				while (rs.next()) {
					System.out.println(rs.getString("Phrase"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public static void viewStudentEnrollmentByGrade() {
		// TODO Auto-generated method stub
		
	}

	public static void viewStudentEnrollmentByCourse() {
		// TODO Auto-generated method stub
		
	}

	public void changePassword(Scanner in) {
		System.out.println("\nEnter current password:");
    	String currentPassword = in.nextLine();
    	
    	if (currentPassword.equals(this.password)) {
    		System.out.println("\nEnter a new password:");
    		String password = Utils.getHash((in.nextLine()));
    		this.setPassword(password);
        	try {
        		Connection conn = PowerSchool.getConnection();
        		PowerSchool.updatePassword(conn, this.getUsername(), password);
        	} catch (SQLException e){
        		System.out.println(e);
        	}
    	}else {
    		System.out.println("\nIncorrect current password.");
    	}
		
	}


    
    
    
    
}