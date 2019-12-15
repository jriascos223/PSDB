package com.apcsa.model;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;
import com.apcsa.data.QueryUtils;

import java.util.ArrayList;
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
    public String jobTitle;
    
    public Administrator(User user, ResultSet rs) throws SQLException {
		super(user);
		
		this.administratorId = rs.getInt("administrator_id");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
		this.jobTitle = rs.getString("job_title");
		
	}

	//Constructor with only resultset
	public Administrator(ResultSet rs) throws SQLException {
		super(rs.getInt("user_id"), rs.getString("account_type"), rs.getString("username"), rs.getString("auth"), rs.getString("last_login"));
		//user id, account type, username, password, last login
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
		ArrayList<Teacher> faculty = new ArrayList<Teacher>();
		faculty = PowerSchool.getFaculty();
		for (int i = 0; i < faculty.size(); i++) {
			System.out.println(faculty.get(i).getLastName() + ", " +  faculty.get(i).getFirstName() + " / " + faculty.get(i).getDepartmentName());
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
					System.out.println(rs.getString("Phrase"));
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

		ArrayList<Teacher> faculty = new ArrayList<Teacher>();
		faculty = PowerSchool.getFaculty();
		for (int i = 0; i < faculty.size(); i++) {
			if (faculty.get(i).getDepartmentId() ==  selection) {
				System.out.println(faculty.get(i).getLastName() + ", " +  faculty.get(i).getFirstName() + " / " + faculty.get(i).getDepartmentId());
			}
		}
	}

	public static void viewStudentEnrollment() {
		ArrayList<Student> students = new ArrayList<Student>();
		students = PowerSchool.getStudents();
		for (int i = 0; i < students.size(); i++) {
			System.out.println(students.get(i).getLastName() + ", " + students.get(i).getFirstName() + " / " + students.get(i).getGraduation());
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
		currentPassword = Utils.getHash(currentPassword);

    	if (currentPassword.equals(this.getPassword())) {
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