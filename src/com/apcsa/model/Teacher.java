package com.apcsa.model;

import java.sql.*;
import java.util.*;

import com.apcsa.model.User;
import com.apcsa.data.*;
import com.apcsa.controller.*;

public class Teacher extends User {

    private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;
    private String departmentName;
    


    public Teacher(User user, ResultSet rs) throws SQLException {
    	super(user);
    	
    	this.teacherId = rs.getInt("teacher_id");
    	this.departmentId = rs.getInt("department_id");
    	this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
    }

    public Teacher(ResultSet rs) throws SQLException {
		super(rs.getInt("user_id"), rs.getString("account_type"), rs.getString("username"), rs.getString("auth"), rs.getString("last_login"));
		this.teacherId = rs.getInt("teacher_id");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
        this.departmentName = rs.getString("title");
        this.departmentId = rs.getInt("department_id");
	}
    
    public String getFirstName() {
    	return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    /*
     * METHODS THAT DO ALL DATABASE MANIPULATION
     */ 

     public void enrollment(Scanner in) {
        System.out.print("\n");
		
        int input = 0;
        boolean assignments = false;
        ArrayList<String> course_nos = getTeacherCourseSelection();

		try {
			input = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        System.out.print("\n");
        try (Connection conn = PowerSchool.getConnection()) {
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ENROLLMENT_BY_COURSE);
             stmt.setString(1, course_nos.get(input - 1));
             try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    //Checks to see if assignments exist, as if they do, the grade must be shown.
                    //If they don't exist, just "--" is put in place of the grade. 
                    try (Connection conn2 = PowerSchool.getConnection()) {
                        PreparedStatement stmt2 = conn2.prepareStatement("SELECT * FROM assignment_grades WHERE student_id = ?");
                        stmt2.setInt(1, rs.getInt("STUDENT_ID"));
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            if (rs2.next()) {
                                assignments = true;
                            }
                        }
                    }
                    
                    if (assignments) {
                        System.out.println(rs.getString("last_name") + ", " + rs.getString("first_name") + " / " + rs.getInt("grade"));
                    } else {
                        System.out.println(rs.getString("last_name") + ", " + rs.getString("first_name") + " / " + "--");
                    }

                assignments = false;
                }
             }
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        
     }

     public void addAssignment(Scanner in) {
        System.out.print("\n");

        int input = 0;
        ArrayList<String> course_nos = getTeacherCourseSelection();

        try {
			input = in.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\nYour input was invalid. Please try again.");
		} finally {
			in.nextLine();
        }

        int mp = getMarkingPeriodSelection(in);

        addAssignmentHelper(in, mp);


        
     }

     public void deleteAssignment() {

     }

     public void enterGrade() {

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
    
    private ArrayList<String> getTeacherCourseSelection() {
        ArrayList<String> course_nos = new ArrayList<String>();
		
        int count = 1;
        
        try (Connection conn = PowerSchool.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_COURSES);
			stmt.setInt(1, this.getTeacherId());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					System.out.println("[" + count + "] " + rs.getString("course_no"));
					count++;
					course_nos.add(rs.getString("course_no"));
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} catch (SQLException e) {
			System.out.println(e);
        }


        return course_nos;
    }

    private int getMarkingPeriodSelection(Scanner in) {
        int output = 0;
        do {
            System.out.println("[1] MP1 Assignment.");
            System.out.println("[2] MP2 Assignment.");
            System.out.println("[3] MP3 Assignment.");
            System.out.println("[4] MP3 Assignment.");
            System.out.println("[5] Midterm exam.");
            System.out.println("[6] Final exam.");

            try {
                output = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("\nYour input was invalid. Please try again.\n");
            }
            in.nextLine();
        } while (output < 1 || output > 6);

        

        return output;
    }

    private void addAssignmentHelper(Scanner in, int mp) {
        String assignmentTitle = "";
        int pointValue = -1;


        System.out.println("Assignment Title: ");
        try {
            assignmentTitle = in.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Your input was invalid. Please try again.");
            addAssignmentHelper(in, mp);
        }

        System.out.println("Point Value: ");
        while (pointValue > 100 || pointValue < 1) {
            try {
                pointValue = in.nextInt();
            } catch (Exception e){
                System.out.println("Point values must be between 1 and 100.");
            }

            if (pointValue > 100 || pointValue < 1) {
                System.out.println("Point values must be between 1 and 100.");
            }
        }
        
    }

}

