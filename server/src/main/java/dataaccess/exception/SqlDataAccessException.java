package dataaccess.exception;

import java.sql.SQLException;

public class SqlDataAccessException extends DataAccessException {
    public SqlDataAccessException(String message, SQLException ex) {
        super(message);
    }
}
