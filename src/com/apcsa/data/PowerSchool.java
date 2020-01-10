package com.apcsa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.apcsa.controller.*;
import com.apcsa.model.*;

public class PowerSchool {

    private final static String PROTOCOL = "jdbc:sqlite:";
    private final static String DATABASE_URL = "data/powerschool.db";

    /**
     * Initializes the database if needed (or if requested).
     *
     * @param force whether or not to force-reset the database
     * @throws Exception
     */

    public static void initialize(boolean force) {
        if (force) {
            reset(); // force reset
        } else {
            boolean required = false;

            // check if all tables have been created and loaded in database

            try (Connection conn = getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(QueryUtils.SETUP_SQL)) {

                while (rs.next()) {
                    if (rs.getInt("names") != 9) {
                        required = true;
                    }
                }
            } catch (SQLException e) {
                shutdown(true);
            }

            // build database if needed

            if (required) {
                reset();
            }
        }
    }


    /**
     * Retrieves the User object associated with the requested login.
     *
     * @param username the username of the requested User
     * @param password the password of the requested User
     * @return the User object for valid logins; null for invalid logins
     */

    public static User login(String username, String password) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(QueryUtils.LOGIN_SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, Utils.getHash(password));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = new Timestamp(new Date().getTime());
                    int affected = PowerSchool.updateLastLogin(conn, username, ts);

                    if (affected != 1) {
                        System.err.println("Unable to update last login (affected rows: " + affected + ").");
                    }
                    return new User(rs);
                }
            }
        } catch (SQLException e) {
            shutdown(true);
        }

        return null;
    }

    /**
     * Returns the administrator account associated with the user.
     *
     * @param user the user
     * @return the administrator account if it exists
     */

    public static User getAdministrator(User user) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ADMIN_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Administrator(user, rs);
                }
            }
        } catch (SQLException e) {
            shutdown(true);
        }

        return user;
    }

    /**
     * Returns the teacher account associated with the user.
     *
     * @param user the user
     * @return the teacher account if it exists
     */

    public static User getTeacher(User user) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(user, rs);
                }
            }
        } catch (SQLException e) {
            shutdown(true);
        }

        return user;
    }

    /**
     * Returns the student account associated with the user.
     *
     * @param user the user
     * @return the student account if it exists
     */

    public static User getStudent(User user) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(user, rs);
                }
            }
        } catch (SQLException e) {
            shutdown(true);
        }

        return user;
    }

    /*
     * Creates an arraylist of the teachers in the database and returns it.
     */

    public static ArrayList<Teacher> getFaculty() {
        ArrayList<Teacher> faculty = new ArrayList<Teacher>();
         try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_FACULTY);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    faculty.add(new Teacher(rs));
                }
            }
         } catch (SQLException e) {
             shutdown(true);
         }

         return faculty;
     }

     /*
      * Creates an arraylist of students in the database and returns it.
      */
    public static ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<Student>();
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(rs));
                }
            }
         } catch (SQLException e) {
             shutdown(true);
         }

         return students;
    }



    /*
     * Establishes a connection to the database.
     *
     * @return a database Connection object
     * @throws SQLException
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROTOCOL + DATABASE_URL);
    }

    /*
     * Updates the last login time for the user.
     *
     * @param conn the current database connection
     * @param username the user's username
     * @param ts the current timestamp
     * @return the number of affected rows
     */

    private static int updateLastLogin(Connection conn, String username, Timestamp ts) {
        try (PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_LAST_LOGIN_SQL)) {

            conn.setAutoCommit(false);
            stmt.setString(1, ts.toString());
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();

                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            shutdown(true);

            return -1;
        }
    }
    
    /*
     * Updates the password for the user.
     * 
     * @param conn the current database connection
     * @param username the user's username
     * @param hasehdPassword the password to update 
     */
    
    public static int updatePassword(Connection conn, String username, String hashedPassword) {
    	try (PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_PASSWORD_SQL)) {
        	stmt.setString(1, hashedPassword);
        	stmt.setString(2, username);
        	conn.setAutoCommit(false);
        	if (stmt.executeUpdate() == 1) {
        		conn.commit();
        		
        		return 1;
        	}else {
        		conn.rollback();
        		
        		return -1;
        	}
        } catch (SQLException e) {
			shutdown(true);
			return -1;
		}
    }

    /*
     * Builds the database. Executes a SQL script from a configuration file to
     * create the tables, setup the primary and foreign keys, and load sample data.
     */

    private static void reset() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(new File("config/setup.sql")))) {

            String line;
            StringBuffer sql = new StringBuffer();

            // read the configuration file line-by-line to get SQL commands

            while ((line = br.readLine()) != null) {
                sql.append(line);
            }

            // execute SQL commands one-by-one

            for (String command : sql.toString().split(";")) {
                if (!command.strip().isEmpty()) {
                    stmt.executeUpdate(command);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to load SQL configuration file.");
            shutdown(true);
        } catch (IOException e) {
            System.err.println("Error: Unable to open and/or read SQL configuration file.");
            shutdown(true);
        } catch (SQLException e) {
            System.err.println("Error: Unable to execute SQL script from configuration file.");
            shutdown(true);
        }
    }

    public static boolean isResultSetEmpty(ResultSet resultSet) throws SQLException {
        return !resultSet.first();
    }


	public static void resetPassword(Scanner in) {
        System.out.print("\nUsername: ");
        String username = "";
        try {
            username = in.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for username, please try again.");
            resetPassword(in);
        }
        String password = Utils.getHash(username);

        try (Connection conn = PowerSchool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET auth = ? WHERE username = ?");
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }catch (SQLException e){
            shutdown(true);
        }

        System.out.println("\nReset password.");
        PowerSchool.resetTimestamp(username);

	}


	private static void resetTimestamp(String username) {
        
        try (Connection conn = PowerSchool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET last_login = '1111-11-11 11:11:11.111' WHERE username = ?");
            stmt.setString(1, username);
            stmt.executeUpdate();
        }catch (SQLException e){
            shutdown(true);
        }

    }

	public static void shutdown(boolean error) {
        if (error) {
            System.out.println("\nA fatal error has occurred. Shutting down...");
            Application.running = false;
            return;
        }
        System.out.println("\nShutting down...");
        Application.running = false;
	}
}
