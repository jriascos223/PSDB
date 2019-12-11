package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.data.QueryUtils;
import com.apcsa.model.Administrator;
import com.apcsa.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.InputMismatchException;

public class Application {

    private Scanner in;
    private User activeUser;

    /**
     * Creates an instance of the Application class, which is responsible for interacting
     * with the user via the command line interface.
     */

    public Application() {
        this.in = new Scanner(System.in);

        try {
            PowerSchool.initialize(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the PowerSchool application.
     */

    public void startup() {
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");
        boolean login = false;

        // continuously prompt for login credentials and attempt to login

        while (true) {
            System.out.print("\nUsername: ");
            String username = in.next();

            System.out.print("Password: ");
            String password = in.next();
            // if login is successful, update generic user to administrator, teacher, or student

            if (login(username, password)) {
            	login = true;
                activeUser = activeUser.isAdministrator()
                    ? PowerSchool.getAdministrator(activeUser) : activeUser.isTeacher()
                    ? PowerSchool.getTeacher(activeUser) : activeUser.isStudent()
                    ? PowerSchool.getStudent(activeUser) : activeUser.isRoot()
                    ? activeUser : null;
                
                if (isFirstLogin() && !activeUser.isRoot()) {
                	System.out.println(((Administrator) activeUser).getLastName());
                    System.out.print("As a new user, you must change your password. \nEnter your new password: ");
                    String tempPassword = in.next();
                    activeUser.setPassword(tempPassword);
                    String hashedPassword = Utils.getHash(tempPassword);
                    
                    try {
						Connection conn = PowerSchool.getConnection();
						int success = PowerSchool.updatePassword(conn, activeUser.getUsername(), hashedPassword);
						if (success == 1) {
							System.out.println("Success!");
						}else if (success == -1) {
							System.out.println("Something went wrong.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
                }
                
                System.out.printf("Hello again, %s!\n", activeUser.getFirstName());

                while (login) {
                	login = this.requestSelectionLoop(activeUser);
                }
            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        }
    }
    
    /*
     * Request selection loop.
     */
    public boolean requestSelectionLoop(User user) {
    	if (user.isAdministrator()) {
    		switch(getAdminSelection()) {
    			case 1:
    				Administrator.viewFaculty();
    				return true;
    			case 2:
    				Administrator.viewFacultyByDept();
    				return true;
    			case 3: 
    				Administrator.viewStudentEnrollment();
    				return true;
    			case 4:
    				Administrator.viewStudentEnrollmentByGrade();
    				return true;
    			case 5:
    				Administrator.viewStudentEnrollmentByCourse();
    				return true;
    			case 6:
    				return true;
    			case 7:
    				return false;
    		}
    	}else if (user.isTeacher()) {
    		
    	}else if (user.isStudent()) {
    		
    	}else if (user.isRoot()) {
    		
    	}
    	
    	return true;
    }
    
    public int getAdminSelection() {
    	int output = 0;
		do {
			System.out.println("\n[1] View faculty.");
			System.out.println("[2] View faculty by department.");
			System.out.println("[3] View student enrollment.");
			System.out.println("[4] View student enrollment by grade.");
			System.out.println("[5] View student enrollment by course.");
			System.out.println("[6] Change password.");
			System.out.println("[7] Logout.");
			try {
				output = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("\nYour input was invalid. Please try again.\n");
			}
			in.nextLine(); // clears the buffer
		} while (output < 1 || output > 7);
		
		return output;
    }

    /**
     * Logs in with the provided credentials.
     *
     * @param username the username for the requested account
     * @param password the password for the requested account
     * @return true if the credentials were valid; false otherwise
     */

    public boolean login(String username, String password) {
        activeUser = PowerSchool.login(username, password);

        return activeUser != null;
    }

    /**
     * Determines whether or not the user has logged in before.
     *
     * @return true if the user has never logged in; false otherwise
     */

    public boolean isFirstLogin() {
        return activeUser.getLastLogin().equals("0000-00-00 00:00:00.000");
    }

    /////// MAIN METHOD ///////////////////////////////////////////////////////////////////

    /*
     * Starts the PowerSchool application.
     *
     * @param args unused command line argument list
     */

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}