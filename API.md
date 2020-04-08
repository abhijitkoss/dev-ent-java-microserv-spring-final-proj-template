# Credit Card User Microservices API


**addAcct**
----
Add a credit card account for a user already created by the bank's user database.

* **URL**

    /addAcct  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `userId=[int]`     
     `fees=[double]`     
     `creditLimit=[double]`     
     `interestRate=[double]`     

* **Success Response:**
  
  * **Code:** 200 OK <br />
    
 
* **Error Response:**

  * **Code:** 400 BAD_REQUEST <br />

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"userId\": \"3\", \"fees\": \"10\" , \"creditLimit\": \"15000\" , \"interestRate\": \"4.5\"}" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/addAcct
 

* **Notes:**

  Note the balance is not input in the data params - a new account always starts with a zero balance. The fees should also have been started with zero for every new account but originally had planned a different usage for the fees field and hence was exposed as a param - in future revisions this API may be changed to remove fees as an input param.  
  

**addUser**
----
Add a user's details to the bank's user database.

* **URL**

    /addUser  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `firstName=[String]`     
     `lastName=[String]`     
     `address=[String]`     
     `email=[String]`     

* **Success Response:**
  
  * **Code:** 200 OK <br />
    
 
* **Error Response:**

  * **Code:** 400 BAD_REQUEST <br />

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"firstName\": \"Mickey\", \"lastName\": \"Mouse\", \"address\": \"1313 Disneyland Dr\", \"email\": \"mickey@gmail.com\"}" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/addUser
 

* **Notes:**

  Not all the parameters of the user have been added in this method currently. It can be easily extended but this method was provided in the sample code and is not the main part of our project. We can easily add users with all the parameters or fix the fields added by this API using SQL queries.  
  


**assessFees**
----
Called monthly to calculate the fees for the account. This API should be called just before calling printStatement otherwise printStatement may not accurately reflect the payments due from the user.

* **URL**

    /assessFees  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `acctNum=[int]`     

* **Success Response:**
  
  * **Code:** 200 OK <br />
    
 
* **Error Response:**

  * **Code:** 400 BAD_REQUEST <br />

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"acctNum\": \"2\"}" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/assessFees
 

* **Notes:**

  Note the fees calculation here is not how a bank will assess fees or interest. A bank will assess fees on the last statement balance minus the payments made before the due date of the billing cycle. However for the sake of simplicity, the fees will be assessed as the interest on the balance at the time when the monthly statement is issued.  
 


**makePurchase**
----
Make a purchase using the credit card as long as the new balance does not exceed the credit limit. Called at the point of purchase (cash register, website payment gateway, etc.).

* **URL**

    /makePurchase  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `acctNum=[integer]`     
     `year=[integer]`     
     `month=[integer]`     
     `day=[integer]`     
     `description=[String]`     
     `amount=[double]`

* **Success Response:**
  
  * **Code:** 200 OK <br />
    
 
* **Error Response:**

  * **Code:** 400 BAD_REQUEST <br />

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"acctNum\": \"2\", \"year\": \"2020\" , \"month\": \"2\" , \"day\": \"6\", \"description\": \"Costco Great Oaks Blvd San Jose\",   \"amount\": \"25\" }" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/makePurchase 

* **Notes:**

  This operation may take some time to execute as this accesses and updates two different tables in the database. 
  

**makePayment**
----
Make a payment towards the balance on the credit card 

* **URL**

    /makePayment  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `acctNum=[integer]`    
     `year=[integer]`     
     `month=[integer]`     
     `day=[integer]`     
     `description=[String]`     
     `amount=[double]`

* **Success Response:**
  
  * **Code:** 200 OK <br />
    
 
* **Error Response:**

  * **Code:** 400 BAD_REQUEST <br />

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"acctNum\": \"2\", \"year\": \"2020\" , \"month\": \"2\" , \"day\": \"28\", \"description\": \"Online Payment\",   \"amount\": \"100\"}" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/makePayment 

* **Notes:**

  This operation may take some time to execute as this accesses and updates two different tables in the database. If the user posts payment greater than the balance, the balance will go negative - which is OK (and what all the credit cards do). 
  
**printAllAccts**
----
A utility method to print all the records in the acct_info table.

* **URL**

    /printAllAccts  

* **Method:**
   
   `GET`
  
*  **URL Params**

   `None`

* **Success Response:**
  
  * Prints all the records in the acct_info table.
    
 
* **Error Response:**

  * Prints only an empty message about the acct_info table but does not print it.

* **Sample Call:**

  curl http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/printAllAccts
 

* **Notes:**

  Note this has minor changes to the supplied method - but also serves as an example GET method.  
 


**printAllUsers**
----
A utility method to print all the records in the user_info table.

* **URL**

    /printAllUsers  

* **Method:**
   
   `GET`
  
*  **URL Params**

   `None`

* **Success Response:**
  
  * Prints all the records in the user_info table.
    
 
* **Error Response:**

  * Prints only an empty message about the user_info table but does not print it.

* **Sample Call:**

  curl http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/printAllUsers
 

* **Notes:**

  Note this is the same as the supplied method.  
 
  
  
**printStatement**
----
Called to print the statement for the account. This API should be called just after calling assessFees otherwise the payments due from the user may not be accurate.

* **URL**

    /printStatement  

* **Method:**
   
   `POST`
  
*  **URL Params**

   `None`

* **Data Params**

     `userId=[int]`     

* **Success Response:**
  
  * Full statement is printed.
    
 
* **Error Response:**

  * User will be told the reason why the statement could not be printed. e.g. if the user ID is non existent or the user has no credit card accounts.

* **Sample Call:**

  curl -i -H "Accept: application/json" -H "Content-Type:application/json" -X POST --data "{\"userId\": \"9\"}" http://ec2-34-217-62-106.us-west-2.compute.amazonaws.com:8080/spring-proj-template/printStatement
 

* **Notes:**

  This operation may take some time to execute as this accesses three different tables in the database.   
 
