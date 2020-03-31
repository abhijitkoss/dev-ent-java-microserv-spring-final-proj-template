package com.hinkmond.finalproj;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;



@RestController
public class JDBCController {
    private final static String KEYFILEPATH = "./keyFile.key";

    @CrossOrigin
    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    public String printCryptTest() {
        AESUtils aesUtils = new AESUtils();

        String encryptedStr = aesUtils.encrypt("Hello World!", KEYFILEPATH);
        return ("Decrypt = " + aesUtils.decrypt(encryptedStr, KEYFILEPATH));
    }

    @CrossOrigin
    @RequestMapping(value = "/printAllUsers", method = RequestMethod.GET)
    public String printAllUsers() {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        StringBuilder resultStr = new StringBuilder();

        String queryStr = "SELECT * from user_info;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        while (sqlRowSet.next()) {
            resultStr.append(sqlRowSet.getString("user_id")).append(", ")
                    .append(sqlRowSet.getString("first_name")).append(", ")
                    .append(sqlRowSet.getString("last_name")).append(", ")
                    .append(sqlRowSet.getString("addr")).append(", ")
                    .append(sqlRowSet.getString("phone")).append(", ")
                    .append(sqlRowSet.getString("email")).append(", ")
                    .append(sqlRowSet.getString("created_at"))
                    .append("\n");
        }
        return ("SELECT * from user_info:\n" + resultStr);
    }

    @CrossOrigin
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String addUser(@RequestBody AddUserData addUserData) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        System.err.println("/addUser" +
                        "'" + addUserData.getFirstName() + "'," +
                        "'" + addUserData.getLastName() + "'," +
                        "'" + addUserData.getAddress() + "'," +
                        "'" + addUserData.getEmail() + "'"
                );

        String queryStr = "INSERT INTO user_info (first_name, last_name, addr, email) " +
                "VALUES (" +
                "'" + addUserData.getFirstName() + "'," +
                "'" + addUserData.getLastName() + "'," +
                "'" + addUserData.getAddress() + "'," +
                "'" + addUserData.getEmail() + "'" +
                ");";
        int rowsUpdated = jdbcTemplate.update(queryStr);
        return ("Rows updated: " + rowsUpdated);
    }

    @CrossOrigin
    @RequestMapping(value = "/printAllAccts", method = RequestMethod.GET)
    public String printAllAccts() {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        StringBuilder resultStr = new StringBuilder();

        String queryStr = "SELECT * from acct_info;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        while (sqlRowSet.next()) {
            resultStr.append(sqlRowSet.getString("user_id")).append(", ")
                    .append(sqlRowSet.getString("acct_num")).append(", ")
                    .append(sqlRowSet.getString("balance"))
                    .append("\n");
        }
        return ("SELECT * from acct_info:\n" + resultStr);
    }

    @CrossOrigin
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();

        // Default: Error
        userInfo.setHttpStatus(HttpStatus.BAD_REQUEST);

        // Processing Logic (override HttpsStatus if everything is OK)
        userInfo.setUserId("12345678");
        userInfo.setFirstName("J.");
        userInfo.setLastName("Jones");
        userInfo.setEmail("jjones@gmail.com");
        userInfo.setHttpStatus(HttpStatus.OK);

        return (userInfo);
    }

    @CrossOrigin
    @SuppressWarnings("SqlResolve")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public HttpStatus updateUserInfo() {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        String queryStr = "INSERT INTO user_info (first_name, last_name, addr, email) " +
                "VALUES (" +
                "'Jim'," +
                "'Doe'," +
                "'789 Front St.'," +
                "'jimdoe@gmail.com'" +
                ");";
        int rowsUpdated = jdbcTemplate.update(queryStr);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (rowsUpdated > 0) {
            status = HttpStatus.OK;
        }
        return (status);
    }
}
