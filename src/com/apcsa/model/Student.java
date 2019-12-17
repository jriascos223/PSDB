package com.apcsa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.apcsa.controller.Utils;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

public class Student extends User {

	private int studentId;
	private int classRank;
	private int gradeLevel;
	private int graduationYear;
	private double gpa;
	private String firstName;
	private String lastName;
    
    public Student(User user, ResultSet rs) throws SQLException {
    	super(user);
    	
    	this.studentId = rs.getInt("student_id");
    	this.classRank = rs.getInt("class_rank");
    	this.gradeLevel = rs.getInt("grade_level");
    	this.graduationYear = rs.getInt("graduation");
    	this.gpa = rs.getDouble("gpa");
    	this.firstName = rs.getString("first_name");
    	this.lastName = rs.getString("last_name");
	}
	
	public Student(ResultSet rs) throws SQLException {
		//user id, account type, username, password, last login
		super(rs.getInt("user_id"), rs.getString("account_type"), rs.getString("username"), rs.getString("auth"), rs.getString("last_login"));

		this.studentId = rs.getInt("student_id");
    	this.classRank = rs.getInt("class_rank");
    	this.gradeLevel = rs.getInt("grade_level");
    	this.graduationYear = rs.getInt("graduation");
    	this.gpa = rs.getDouble("gpa");
    	this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
	}
    
    public String getFirstName() {
    	return firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public int getGraduation() {
		return this.graduationYear;
	}

	public int getGradeLevel() {
		return this.gradeLevel;
	}

	public int getStudentId() {
		return this.studentId;
	}
    
    
    /*
     * Function that both changes the property of the object as well as the data in the database.
     */

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

	public void viewCourseGrades() {
		System.out.print("\n");
		try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("SELECT course_grades.course_id, courses.title, students.student_id, students.first_name, students.last_name grade FROM course_grades INNER JOIN courses ON course_grades.course_id = courses.course_id INNER JOIN students ON students.student_id = course_grades.student_id WHERE students.student_id = ?");
			stmt.setInt(1, this.getStudentId());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getString("title") + " / " + rs.getInt("grade"));
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	

}