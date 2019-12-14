package com.apcsa.model;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;
import com.apcsa.data.QueryUtils;

import java.util.InputMismatchException;
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
					System.out.println(rs.getString("Faculty"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e){
			System.out.println(e);
		}
	}

	public static void viewFacultyByDept(Scanner in) {
		
		System.out.println("\nChoose a department:\n");
		int departmentCount = 0;
		int selection = 0;
		
		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_DEPARTMENTS);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getString("Departments"));
					departmentCount++;
				}
			} catch (SQLException e){
				System.out.println(e);
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		do {
			try {
				selection = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("\nYour input was invalid. Please try again.\n");
			} finally {
				in.nextLine();
			}
		} while (selection < 0 || selection > departmentCount);
		
		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_FACULTY_BY_DEPT);
			stmt.setInt(1, selection);
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.print("\n");
				while (rs.next()) {
					System.out.println(rs.getString("Faculty"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		
	}

	public static void viewStudentEnrollment() {
		try {
			Connection conn = PowerSchool.getConnection();
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS);
			
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("\n");
				while (rs.next()) {
					System.out.println(rs.getString("Students"));
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