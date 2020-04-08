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
    public HttpStatus addUser(@RequestBody AddUserData addUserData) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        System.err.println("[*DBG*] /addUser: " +
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
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (rowsUpdated > 0) {
            status = HttpStatus.OK;
        }

        return status;
    }


    @CrossOrigin
    @RequestMapping(value = "/addAcct", method = RequestMethod.POST)
    public HttpStatus addAcct(@RequestBody AddAcctData addAcctData) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        System.err.println("[*DBG*] /addAcct: " +
                "'" + addAcctData.getUserId() + "'," +
                "'" + addAcctData.getFees() + "',"   +
                "'" + addAcctData.getCreditLimit() + "'," +
                "'" + addAcctData.getInterestRate() + "'"
        );

        String queryStr = "INSERT INTO acct_info (user_id, fees, credit_limit, interest_rate, balance " +
                "VALUES (" +
                addAcctData.getUserId() + ", " +
                addAcctData.getFees() + ", "   +
                addAcctData.getCreditLimit() + ", " +
                addAcctData.getInterestRate() + ", " +
                0 + "  " +
                ");";
        System.err.println("\n[*DBG*] SqlString to send:\n" + queryStr);

        int rowsUpdated = jdbcTemplate.update(queryStr);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (rowsUpdated > 0) {
            status = HttpStatus.OK;
        }

        return status;
    }


    @CrossOrigin
    @RequestMapping(value = "/makePurchase", method = RequestMethod.POST)
    public HttpStatus makePurchase(@RequestBody AddTransactionData addTransactionData) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        int acctNum = addTransactionData.getAcctNum();
        int year = addTransactionData.getYear();
        int month = addTransactionData.getMonth();
        int day = addTransactionData.getDay();
        String descr = addTransactionData.getDescription();
        double amount = addTransactionData.getAmount();

        // Debug code - would be removed in future
        System.err.println("[*DBG*] /makePurchase: " +
                "'" + addTransactionData.getAcctNum() + "'," +
                "'" + addTransactionData.getYear() + "',"   +
                "'" + addTransactionData.getMonth() + "'," +
                "'" + addTransactionData.getDay() + "'," +
                "'" + addTransactionData.getDescription() + "'," +
                "'" + addTransactionData.getAmount() + "'"
        );

        // First validate if the date is ok
        if (!Validator.isDateValid(year, month, day)) {
            return status;
        }

        // Retrieve credit limit and balance fromm acct_info
        String queryStr = "SELECT credit_limit, balance FROM acct_info WHERE  acct_num = " +
                acctNum  + " LIMIT 1;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        if (sqlRowSet.next()) {
            double creditLimit = sqlRowSet.getDouble("credit_limit");
            double balance = sqlRowSet.getDouble("balance");
            System.err.println("[*DBG*] makePurchase: Acct_info: acct_num: " + acctNum + ", credit limit: " + creditLimit + ", current balance: " + balance);

            double newBalance = balance + amount;
            if (newBalance >  creditLimit) {
                // The new balance would exceed the credit limit - hence the transaction cannot be allowed
                return status;
            }
            // Update the balance in the acct_info table in the database to the new balance
            queryStr = "UPDATE acct_info SET balance = " + newBalance + " WHERE acct_num = " + acctNum + ";";
            System.err.println("\n[*DBG*] makePurchase: Update acct_info SqlString to send:\n" + queryStr);

            int rowsUpdated = jdbcTemplate.update(queryStr);
            if (rowsUpdated == 0) {
                // Balance was not updated - report the transaction as failure
                return status;
            }

            // Everything successful so far - add a new transaction record - with is_credit as false i.e. 0
            queryStr = "INSERT INTO transactions_info (acct_num, year, month, day, description, is_credit, amount ) " +
                    "VALUES (" + acctNum + ", " + year + ", " + month + ", " + day + ", '" + descr + "', 0, " + amount +
                    ");";
            System.err.println("\n[*DBG*] makePurchase: Insert trans SqlString to send:\n" + queryStr);
            rowsUpdated = jdbcTemplate.update(queryStr);

            if (rowsUpdated > 0)  {
                status = HttpStatus.OK;
            }

        } else {
            // Log error - so admin can send us useful info for debugging
            System.err.println("makePurchase: Acct_info: acct_num: " + acctNum + " could not be retrieved");
            return status;
        }

        return status;
    }

    @CrossOrigin
    @RequestMapping(value = "/makePayment", method = RequestMethod.POST)
    public HttpStatus makePayment(@RequestBody AddTransactionData addTransactionData) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        int acctNum = addTransactionData.getAcctNum();
        int year = addTransactionData.getYear();
        int month = addTransactionData.getMonth();
        int day = addTransactionData.getDay();
        String descr = addTransactionData.getDescription();
        double amount = addTransactionData.getAmount();

        // Debug code - would be removed in future
        System.err.println("[*DBG*] /makePayment: " +
                "'" + addTransactionData.getAcctNum() + "'," +
                "'" + addTransactionData.getYear() + "',"   +
                "'" + addTransactionData.getMonth() + "'," +
                "'" + addTransactionData.getDay() + "'," +
                "'" + addTransactionData.getDescription() + "'," +
                "'" + addTransactionData.getAmount() + "'"
        );

        // First validate if the date is ok
        if (!Validator.isDateValid(year, month, day)) {
            return status;
        }

        // Retrieve balance fromm acct_info
        String queryStr = "SELECT balance FROM acct_info WHERE  acct_num = " +
                acctNum  + " LIMIT 1;";
        System.err.println("\n[*DBG*] makePayment: SELECT balance from acct info SqlString to send:\n" + queryStr);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        if (sqlRowSet.next()) {
            double balance = sqlRowSet.getDouble("balance");
            System.err.println("[*DBG*] makePayment: Acct_info: acct_num: " + acctNum +  ",  balance: " + balance);

            double newBalance = balance - amount; // Note - it is OK for this to go negative. i.e. payment > balance

            // Update the  balance in the acct_info table in the database to the new balance
            queryStr = "UPDATE acct_info SET balance = " + newBalance + " WHERE acct_num = " + acctNum + ";";
            System.err.println("\n[*DBG*] makePayment: Update acct_info SqlString to send:\n" + queryStr);

            int rowsUpdated = jdbcTemplate.update(queryStr);
            if (rowsUpdated == 0) {
                // Balance was not updated - report the transaction as failure
                return status;
            }

            // Everything successful so far - add a new transaction record - with is_credit as true i.e. 1
            queryStr = "INSERT INTO transactions_info (acct_num, year, month, day, description, is_credit, amount ) " +
                    "VALUES (" + acctNum + ", " + year + ", " + month + ", " + day + ", '" + descr + "', 1, " + amount +
                    ");";
            System.err.println("\n[*DBG*] makePayment: Insert trans SqlString to send:\n" + queryStr);
            rowsUpdated = jdbcTemplate.update(queryStr);

            if (rowsUpdated > 0)  {
                status = HttpStatus.OK;
            }

        } else {
            // Log error - so admin can send us useful info for debugging
            System.err.println("makePayment: Acct_info: acct_num: " + acctNum + " could not be retrieved");
            return status;
        }

        return status;
    }


    @CrossOrigin
    @RequestMapping(value = "/assessFees", method = RequestMethod.POST)
    public HttpStatus assessFees(@RequestBody AcctNum acctNumber) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        int acctNum = acctNumber.getAcctNum();

        // First retrieve interest rate and balance from acct_info for fees calculation
        String queryStr = "SELECT interest_rate, balance FROM acct_info WHERE  acct_num = " +
                acctNum  + " LIMIT 1;";
        System.err.println("\n[*DBG*] assessFees: Select interest rate, balamce acct_info SqlString to send:\n" + queryStr);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        if (sqlRowSet.next()) {
            double interestRate = sqlRowSet.getDouble("interest_rate");
            double balance = sqlRowSet.getDouble("balance");
            System.err.println("[*DBG*] assessFees: Acct_info: acct_num: " + acctNum +
                    ", interest rate: " + interestRate + ", current balance: " + balance
                    );

            // To keep things simple we will calculate fees as monthly interest on the current balance if balance > 0
            double fees = (balance > 0) ? ((balance * (interestRate/100.0 ) ) / 12.0) : 0;

            // Update the fees in the acct_info table for this account
            queryStr = "UPDATE acct_info SET fees = " + fees + " WHERE acct_num = " + acctNum + ";";
            System.err.println("\n[*DBG*] assessFees: Update acct_info SqlString to send:\n" + queryStr);

            int rowsUpdated = jdbcTemplate.update(queryStr);
            if (rowsUpdated == 0) {
                // Balance was not updated - report the transaction as failure
                return status;
            }
            // Everything went fine
            status = HttpStatus.OK;

        } else {
            // Log error - so admin can send us useful info for debugging
            System.err.println("assessFees: Acct_info: acct_num: " + acctNum + " could not be retrieved");
            return status;
        }

        return status;
    }


    @CrossOrigin
    @RequestMapping(value = "/printStatement", method = RequestMethod.POST)
    public String printStatement(@RequestBody UserId userIdNum) {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        StringBuilder resultStr = new StringBuilder();
        String emptyLine =
                "\n============================================================================================\n";

        int userId = userIdNum.getUserId();

        // First retrieve the user info
        String queryStr = "SELECT first_name, last_name, addr, phone, email FROM user_info WHERE  user_id = " +
                userId  + " LIMIT 1;";
        System.err.println("\n[*DBG*] printStatement: Select user_info SqlString to send:\n" + queryStr);

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        if (sqlRowSet.next()) {
            // User was found
            resultStr.append(sqlRowSet.getString("first_name")).append("  ")
                    .append(sqlRowSet.getString("last_name")).append("\n\t\t")
                    .append(sqlRowSet.getString("addr")).append("\n\t\t")
                    .append(sqlRowSet.getString("phone")).append("\n\t\t")
                    .append(sqlRowSet.getString("email")).append("\n\n");

            // Retrieve the user's account info
            queryStr = "SELECT acct_num, fees, balance FROM acct_info WHERE  user_id = " +
                    userId + " LIMIT 1;";
            System.err.println("\n[*DBG*] printStatement: Select acct_info SqlString to send:\n" + queryStr);
            sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);

            if (sqlRowSet.next()) {
                // Account was found
                int acctNum = sqlRowSet.getInt("acct_num");
                double fees = sqlRowSet.getDouble("fees");
                double balance = sqlRowSet.getDouble("balance");
                System.err.println("[*DBG*] printStatement: Acct_info: acct_num: " + acctNum +
                        ", fees: " + fees + ", balance: " + balance
                );

                double fullPayment = fees + balance;
                resultStr.append("\n\nAccount Summary:\n");
                resultStr.append("-----------------\n");
                resultStr.append("\n\tBalance:\t\t").append(balance);
                resultStr.append("\n\tMinimum Payment Due:\t").append(fees);
                resultStr.append("\n\tFull Balance Due:\t").append(fullPayment);
                resultStr.append("\n\nTransactions:");
                resultStr.append("\n-------------\n\n");
                resultStr.append("\tDate\t\tDescription\t\t\t\tType\t\tAmount\n\n");

                // Now retrieve transactions for the account from the transactions table
                queryStr = "SELECT year, month, day, description, is_credit, amount from transactions_info WHERE acct_num = " +
                        acctNum + ";";
                System.err.println("\n[*DBG*] printStatement: Select transactions_info SqlString to send:\n" + queryStr);
                sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
                while (sqlRowSet.next()) {
                    resultStr.append("\t").append(sqlRowSet.getString("month")).append("/")
                            .append(sqlRowSet.getString("day")).append("/")
                            .append(sqlRowSet.getString("year")).append("\t")
                            .append(sqlRowSet.getString("description")).append("\t\t");
                    if (sqlRowSet.getBoolean("is_credit")) {
                        resultStr.append("Deposit\t\t");
                    } else {
                        resultStr.append("Payment\t\t");
                    }
                    resultStr.append(sqlRowSet.getDouble("amount"));
                    resultStr.append("\n");
                }
            } else {
                return "\nCredit Card Account Statement For:\n\t\t" + resultStr + "\n\nUser has no credit accounts\n";
            }


        } else {
            return "\nCredit Card Account Statement cannot be made for a non-existent user";
        }

        return emptyLine + "\nCredit Card Account Statement For:\n\t\t" + resultStr + emptyLine;
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
                    .append(sqlRowSet.getString("fees")).append(", ")
                    .append(sqlRowSet.getString("credit_limit")).append(", ")
                    .append(sqlRowSet.getString("interest_rate")).append(", ")
                    .append(sqlRowSet.getString("balance"))
                    .append("\n");
        }
        return ("SELECT * from acct_info:\n" + resultStr);
    }

    // THIS IS FOR ACADEMIC REFERENCE ONLY FOR HOW TO RETURN HTTP STATUS WITH DATA - PLEASE IGNORE
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

    // THIS IS FOR ACADEMIC REFERENCE ONLY FOR POST METHOD - PLEASE IGNORE
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
