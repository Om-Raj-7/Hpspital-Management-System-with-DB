import java.sql.*; 
import java.util.Scanner;

public class HospitalManagement {
    static final String DB_URL = "jdbc:mysql://localhost:3306/hospitaldb";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "roots"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //Loads the MYSQL JDBC DRIVER
            try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){  //Establishes the connection to the mysql

            while (true) {
                System.out.println("\n--- Hospital Management ---");
                System.out.println("1. Add Patient");
                System.out.println("2. View All Patients");
                System.out.println("3. Search Patient by ID");
                System.out.println("4. Delete Patient by ID");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> addPatient(scanner, conn);
                    case 2 -> viewAllPatients(conn);
                    case 3 -> searchPatientById(scanner, conn);
                    case 4 -> deletePatientById(scanner, conn);
                    case 5 -> {
                        System.out.println("Exiting... 👋");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            }
        }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    static void addPatient(Scanner scanner, Connection conn) throws SQLException {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter gender (Male/Female): ");
        String gender = scanner.nextLine();
        System.out.print("Enter diagnosis: ");
        String diagnosis = scanner.nextLine();

        String sql = "INSERT INTO patients (name, age, gender, diagnosis) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) { // repeated execution with different parameter values.
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, diagnosis);
            ps.executeUpdate();
            System.out.println("✅ Patient added successfully.");
        }
    }

    static void viewAllPatients(Connection conn) throws SQLException {
        String sql = "SELECT * FROM patients";
        try (Statement stmt = conn.createStatement(); //create a statement object (stmt)
             ResultSet rs = stmt.executeQuery(sql)) { // sends the SQL query (SELECT * FROM patients) to the database and executes it.
            System.out.println("\n--- All Patients ---");
            while (rs.next()) { //The rs.next() moves the cursor to the next row in the ResultSet
                System.out.printf("ID: %d, Name: %s, Age: %d, Gender: %s, Diagnosis: %s, Admitted: %s%n", /* %s is used for string it is format specifiers and %d is used for strings */ 
                        rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                        rs.getString("gender"), rs.getString("diagnosis"), rs.getTimestamp("admission_date"));
            }
        }
    }

    static void searchPatientById(Scanner scanner, Connection conn) throws SQLException {
        System.out.print("Enter Patient ID: ");
        int id = scanner.nextInt();

        String sql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.printf("ID: %d, Name: %s, Age: %d, Gender: %s, Diagnosis: %s, Admitted: %s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                        rs.getString("gender"), rs.getString("diagnosis"), rs.getTimestamp("admission_date"));
            } else {
                System.out.println(" No patient found with that ID.");
            }
        }
    }

    static void deletePatientById(Scanner scanner, Connection conn) throws SQLException {
        System.out.print("Enter Patient ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Patient deleted successfully.");
            } else {
                System.out.println("No patient found with that ID.");
            }
        }
    }
}

