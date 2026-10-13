import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final String URL = "jdbc:mysql://localhost:3306/smart_attendance";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD)) {
            System.out.println("=== SMART ATTENDANCE SYSTEM ===");
            if (!login(con)) {
                System.out.println("Invalid login. Exiting...");
                return;
            }

            int choice;
            do {
                System.out.println("\\n1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Mark Attendance");
                System.out.println("4. View Attendance Report");
                System.out.println("5. Delete Student");
                System.out.println("0. Logout");
                System.out.print("Enter choice: ");
                choice = readInt();

                switch (choice) {
                    case 1 -> addStudent(con);
                    case 2 -> viewStudents(con);
                    case 3 -> markAttendance(con);
                    case 4 -> attendanceReport(con);
                    case 5 -> deleteStudent(con);
                    case 0 -> System.out.println("Logged out successfully.");
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } while (choice != 0);
        } catch (SQLException e) {
            System.out.println("Database connection/error: " + e.getMessage());
            System.out.println("Check MySQL is running, schema.sql is executed, and credentials in Main.java are correct.");
        }
    }

    private static boolean login(Connection con) throws SQLException {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addStudent(Connection con) throws SQLException {
        System.out.print("Register number: ");
        String regNo = sc.nextLine().trim();
        System.out.print("Student name: ");
        String name = sc.nextLine().trim();
        System.out.print("Department: ");
        String department = sc.nextLine().trim();

        String sql = "INSERT INTO students(reg_no, name, department) VALUES(?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, name);
            ps.setString(3, department);
            ps.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("That register number already exists.");
        }
    }

    private static void viewStudents(Connection con) throws SQLException {
        String sql = "SELECT id, reg_no, name, department FROM students ORDER BY reg_no";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\\nID | Register No | Name | Department");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s%n",
                    rs.getInt("id"), rs.getString("reg_no"),
                    rs.getString("name"), rs.getString("department"));
            }
        }
    }

    private static void markAttendance(Connection con) throws SQLException {
        System.out.print("Student ID: ");
        int studentId = readInt();
        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();
        System.out.print("Status (Present/Absent): ");
        String status = sc.nextLine().trim();

        if (!status.equalsIgnoreCase("Present") && !status.equalsIgnoreCase("Absent")) {
            System.out.println("Status must be Present or Absent.");
            return;
        }

        String sql = "INSERT INTO attendance(student_id, attendance_date, status) VALUES(?,?,?) " +
                     "ON DUPLICATE KEY UPDATE status=VALUES(status)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase());
            ps.executeUpdate();
            System.out.println("Attendance saved.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date. Use YYYY-MM-DD.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Student ID not found.");
        }
    }

    private static void attendanceReport(Connection con) throws SQLException {
        String sql = "SELECT s.reg_no, s.name, a.attendance_date, a.status " +
                     "FROM attendance a JOIN students s ON a.student_id=s.id " +
                     "ORDER BY a.attendance_date DESC, s.reg_no";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\\nRegister No | Name | Date | Status");
            while (rs.next()) {
                System.out.printf("%s | %s | %s | %s%n",
                    rs.getString("reg_no"), rs.getString("name"),
                    rs.getDate("attendance_date"), rs.getString("status"));
            }
        }
    }

    private static void deleteStudent(Connection con) throws SQLException {
        System.out.print("Enter student ID to delete: ");
        int id = readInt();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Student deleted." : "Student ID not found.");
        }
    }

    private static int readInt() {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
