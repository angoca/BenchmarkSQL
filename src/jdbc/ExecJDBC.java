import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import jtpcc.jTPCCUtil;

/**
 * Command line program to process SQL DDL statements, from a text input file,
 * to any JDBC Data Source.
 * 
 * @author Denis Lussier - 2004-2016
 * @author Jan Wieck - 2016
 */
public class ExecJDBC {

	public static void main(String[] args) {

		Connection conn = null;
		Statement stmt = null;
		String rLine = null;
		StringBuffer sql = new StringBuffer();

		try {

			Properties ini = new Properties();
			ini.load(new FileInputStream(System.getProperty("prop")));

			// Register jdbcDriver
			Class.forName(ini.getProperty("driver"));

			// make connection
			conn = DriverManager.getConnection(ini.getProperty("conn"), ini.getProperty("user"),
					ini.getProperty("password"));
			conn.setAutoCommit(true);

			// FIXME RDMBS - Adjust for any other one.
			// For oracle : Boolean that indicates whether or not there is a statement ready
			// to be executed.
			Boolean ora_ready_to_execute = false;

			// Create Statement
			stmt = conn.createStatement();

			// Open inputFile
			BufferedReader in = new BufferedReader(new FileReader(jTPCCUtil.getSysProp("commandFile", null)));

			// loop thru input file and concatenate SQL statement fragments
			while ((rLine = in.readLine()) != null) {

				if (ora_ready_to_execute == true) {
					String query = sql.toString();

					execJDBC(stmt, query);
					sql = new StringBuffer();
					ora_ready_to_execute = false;
				}

				String line = rLine.trim();

				if (line.length() != 0) {
					if (line.startsWith("--") && !line.startsWith("-- {")) {
						System.out.println(rLine); // print comment line
					} else {
						if (line.equals("$$")) {
							sql.append(rLine);
							sql.append("\n");
							while ((rLine = in.readLine()) != null) {
								line = rLine.trim();
								sql.append(rLine);
								sql.append("\n");
								if (line.equals("$$")) {
									break;
								}
							}
							continue;
						}

						if (line.startsWith("-- {")) {
							sql.append(rLine);
							sql.append("\n");
							while ((rLine = in.readLine()) != null) {
								line = rLine.trim();
								sql.append(rLine);
								sql.append("\n");
								if (line.startsWith("-- }")) {
									ora_ready_to_execute = true;
									break;
								}
							}
							continue;
						}

						if (line.endsWith("\\;")) {
							sql.append(rLine.replaceAll("\\\\;", ";"));
							sql.append("\n");
						} else {
							sql.append(line.replaceAll("\\\\;", ";"));
							if (line.endsWith(";")) {
								String query = sql.toString();

								execJDBC(stmt, query.substring(0, query.length() - 1));
								sql = new StringBuffer();
							} else {
								sql.append("\n");
							}
						}
					}

				} // end if

			} // end while

			in.close();

		} catch (IOException ie) {
			System.out.println(ie.getMessage());

		} catch (SQLException se) {
			System.out.println(se.getMessage());

		} catch (Exception e) {
			e.printStackTrace();

			// exit Cleanly
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally

		} // end try

	} // end main

	static void execJDBC(Statement stmt, String query) {

		System.out.println(query + ";");

		try {
			stmt.execute(query);
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} // end try

	} // end execJDBCCommand

} // end ExecJDBC Class
