package tdt4140.gr1800.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;

public class DbAccessHelper {

	private Connection dbConnection;
	private Statement dbStatement;
	
	public DbAccessHelper(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public DbAccessHelper(String connectionUri, String user, String pass) throws SQLException {
		this(DriverManager.getConnection(connectionUri, user, pass));
	}

	public DbAccessHelper(String connectionUri) throws SQLException {
		this(connectionUri, "SA", "");
	}
	
	public Connection getDbConnection() {
		return dbConnection;
	}
	
	private Statement getDbStatement() throws SQLException {
		if (dbStatement == null) {
			dbStatement = getDbConnection().createStatement();
		}
		return dbStatement;
	}
	
	protected void throwException(SQLException e) throws RuntimeException {
		throw new RuntimeException(e);
	}
	
	protected void executeStatement(String statement) {
		try {
			getDbStatement().execute(statement);
		} catch (SQLException e) {
			throwException(e);
		}
	}

	protected ResultSet executeQuery(String statement) {
		ResultSet result = null;
		try {
			result = getDbStatement().executeQuery(statement);
		} catch (SQLException e) {
			throwException(e);
		}
		return result;
	}

	protected PreparedStatement prepareStatement(String statement, Object... args) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = dbConnection.prepareStatement(statement);
			for (int argNum = 1; argNum <= args.length; argNum++) {
				Object arg = args[argNum - 1];
				if (arg == null) {					
					preparedStatement.setNull(argNum, Types.VARCHAR);
				} else if (arg instanceof String)  {
					preparedStatement.setString(argNum, (String) arg);
				} else if (arg instanceof Double) {
					preparedStatement.setDouble(argNum, (Double) arg);
				} else if (arg instanceof Integer) {
					preparedStatement.setInt(argNum, (Integer) arg);
				} else if (arg instanceof Boolean) {
					preparedStatement.setBoolean(argNum, (Boolean) arg);
				}
			}
		} catch (SQLException e) {
			throwException(e);
		}
		return preparedStatement;
	}

	protected void executeDbStatement(String statement, Object... args) {
		PreparedStatement preparedStatement = prepareStatement(statement, args);
		executeStatement(preparedStatement);
	}

	protected int executeDbInsertGettingIdentity(String statement, Object... args) {
		executeDbStatement(statement, args);
		// TODO
		// https://stackoverflow.com/questions/1915166/how-to-get-the-insert-id-in-jdbc/1915197#1915197
		ResultSet result = executeQuery(String.format("CALL IDENTITY()"));
		try {
			if (result.next()) {
				int id = result.getInt(1);
				return id;
			} else {
				throw new RuntimeException("Couldn't get id after " + statement);
			}
		} catch (SQLException e) {
			throwException(e);
		}
		return -1;
	}

	protected void executeStatement(PreparedStatement preparedStatement) {
		try {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throwException(e);
		}
	}
	
	protected ResultSet executeQuery(PreparedStatement preparedStatement) {
		ResultSet result = null;
		try {
			result = preparedStatement.executeQuery();
		} catch (SQLException e) {
			throwException(e);
		}
		return result;
	}
	
	protected ResultSet executeQuery(String statement, Object... args) {
		PreparedStatement preparedStatement = prepareStatement(statement, args);
		return executeQuery(preparedStatement);
	}
}
