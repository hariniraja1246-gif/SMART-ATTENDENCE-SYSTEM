# Smart Attendance System (Java + JDBC + MySQL)

A simple console-based implementation based on the Smart Attendance System PPT.

## Features
- Login
- Add and view students
- Mark attendance as Present/Absent
- View attendance report
- Delete student
- MySQL database using JDBC prepared statements

## Requirements
- JDK 17 or later
- MySQL Server
- MySQL Connector/J JAR

## Setup
1. Open MySQL Workbench or MySQL command line.
2. Run the complete `schema.sql` file.
3. Open `Main.java` and change `DB_PASSWORD` from `YOUR_MYSQL_PASSWORD` to your local MySQL password.
4. Download MySQL Connector/J and add its JAR to your project's classpath.
5. Compile and run.

### Windows example (replace the JAR path with your downloaded file)
```bash
javac -cp ".;mysql-connector-j-8.4.0.jar" Main.java
java -cp ".;mysql-connector-j-8.4.0.jar" Main
```

### macOS/Linux example
```bash
javac -cp ".:mysql-connector-j-8.4.0.jar" Main.java
java -cp ".:mysql-connector-j-8.4.0.jar" Main
```

Demo login:
- Username: `admin`
- Password: `admin123`

This demo stores the sample login password as plain text for simplicity. Do not use this authentication approach in a real application; use password hashing and stronger access controls.
