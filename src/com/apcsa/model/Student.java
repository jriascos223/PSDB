package com.apcsa.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    	super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());
    	
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
    
    
    /*
     * Function that both changes the property of the object as well as the data in the database.
     */
    public void changePassword(String password) {
    	this.setPassword(password);
    	try {
    		Connection conn = PowerSchool.getConnection();
    		PowerSchool.updatePassword(conn, this.getUsername(), password);
    	} catch (SQLException e){
    		System.out.println(e);
    	}
    }
}