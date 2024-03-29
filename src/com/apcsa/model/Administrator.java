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

	public void viewFaculty() {
		System.out.print("\n");
		ArrayList<Teacher> faculty = new ArrayList<Teacher>();
		faculty = PowerSchool.getFaculty();
		for (int i = 0; i < faculty.size(); i++) {
			System.out.println(faculty.get(i).getLastName() + ", " +  faculty.get(i).getFirstName() + " / " + faculty.get(i).getDepartmentName());
		}
	}

	public void viewFacultyByDept(Scanner in) {
		
		System.out.println("\nChoose a department:\n");
		int departmentCount = 0;
		int selection = 0;
		ArrayList<String> departments = new ArrayList<String>();
		
		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_DEPARTMENTS);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getString("Phrase"));
					departments.add((rs.getString("Phrase")).replaceAll("\\[\\d\\]\\s", ""));
					departmentCount++;
				}
			} catch (SQLException e){
				PowerSchool.shutdown(true);
			}
			
		} catch (SQLException e) {
			PowerSchool.shutdown(true);
		}

		System.out.print("\n::: ");
		
		do {
			try {
				selection = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("\nYour input was invalid. Please try again.\n");
			} finally {
				in.nextLine();
			}
		} while (selection < 0 || selection > departmentCount);

		System.out.print("\n");
		ArrayList<Teacher> faculty = new ArrayList<Teacher>();
		faculty = PowerSchool.getFaculty();
		for (int i = 0; i < faculty.size(); i++) {
			if (faculty.get(i).getDepartmentId() ==  selection) {
				System.out.println(faculty.get(i).getLastName() + ", " +  faculty.get(i).getFirstName() + " / " + departments.get(i));
			}
		}
	}

	public void viewStudentEnrollment(){
		System.out.print("\n");
		ArrayList<Student> students = new ArrayList<Student>();
		students = PowerSchool.getStudents();
		for (int i = 0; i < students.size(); i++) {
			System.out.println(students.get(i).getLastName() + ", " + students.get(i).getFirstName() + " / " + students.get(i).getGraduation());
		}
	}

	public void viewStudentEnrollmentByGrade(Scanner in) {
		System.out.println("\nChoose a grade:\n");
		System.out.println("[1] Freshman.");
		System.out.println("[2] Sophomore.");
		System.out.println("[3] Junior.");
		System.out.println("[4] Senior.");
		System.out.print("\n::: ");
		int selection = 0;

		do {
			try {
				selection = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("\nYour input was invalid. Please try again.");
			} finally {
				in.nextLine();
			}
		} while (selection < 0 || selection > 4);

		selection += 8;

		System.out.print("\n");

		ArrayList<Student> students = new ArrayList<Student>();
		students = PowerSchool.getStudents();
		int count = 0;


		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getGradeLevel() == selection) {
				System.out.println(students.get(i).getLastName() + ", " + students.get(i).getFirstName() + " / " + students.get(i).getClassRank());
				count++;
			}
		}

		if (count == 0) {
			System.out.println("No students from that year.");
		}

		
	}

	public void viewStudentEnrollmentByCourse(Scanner in) {
		System.out.print("\nCourse No.: ");
		String selection = "";

		try {
			selection = in.nextLine();
		} catch (InputMismatchException e) {
			PowerSchool.shutdown(true);
		}

		ArrayList<String> course_nos = PowerSchool.getCourseNos();

		if (!course_nos.contains(selection)) {
			System.out.println("\nInvalid course number.");
		}


		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ENROLLMENT_BY_COURSE_NO);
			stmt.setString(1, selection);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getString("last_name") + ", " + rs.getString("first_name") + " / " + rs.getDouble("gpa"));
				}
			}
		} catch (SQLException e) {
			PowerSchool.shutdown(true);
		}
		
	}

	public void changePassword(Scanner in) {
		System.out.print("\nEnter current password: ");
		String currentPassword = in.nextLine();
		currentPassword = Utils.getHash(currentPassword);

    	if (currentPassword.equals(this.getPassword())) {
    		System.out.print("\nEnter a new password: ");
    		String password = Utils.getHash((in.nextLine()));
    		this.setPassword(password);
        	try {
        		Connection conn = PowerSchool.getConnection();
        		PowerSchool.updatePassword(conn, this.getUsername(), password);
        	} catch (SQLException e){
        		PowerSchool.shutdown(true);
        	}
    	}else {
    		System.out.println("\nIncorrect current password.");
		}
		
		System.out.println("\nPassword changed.");
		
	}


    
    
    
    
}