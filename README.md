# Credit Card User Microservices

### Summary

This project seeks to improve our understanding of Spring Micro Services by extending the skeleton Credit Card User code. We will be providing the microservices below which can be used to provide a credit card servicing backend software for a bank.

The following workflow could be used for a typical user who gets approved for a credit card:

#### Step 1: addUser
This microservice should be used to add new users to the bank's credit card database. This will include the user's name and contact details (address, email, phone) but can be extended to include other details like the user's credit score, etc.

#### Step 2: addAcct
This microservice should be used to add a credit card account for the newly created user. The credit card account will have information about the balance, the credit limit for the account, the interest rate charged for this particular user, etc.  

#### Step 3: makePurchase
The user can start making purchases with the newly issued credit card after activation. This microservice will be called by the merchant's system at the point of sale (PoS) when the user decides to make a purchase. It will check if the user has available credit i.e. the account balance has not exceeded the user's credit limit.

#### Step 4: assessFees
This microservice will be called by the credit card servicing backend software at the end of the monthly billing cycle to assess the monthly fees/interest. The backend software should also send a bill available notification to the user's email or to the address based on the user's paper billing preferences. However the bill available notification should be a separate microservice.

#### Step 5: printStatement
Upon seeing the bill available notification, the user invokes the credit card app or logs on to the credit card account at the bank website to check the user's statement which is rendered by the printStatement microservice. 
 
#### Step 6: makePayment
Upon seeing the bill, the user pays it via this microservice. The microservice will be called by the user's credit card app or from the credit card account at the bank website to make a payment towards the account balance.

<br>

### Not Currently Implemented 

* sendBillAvailableNotification Microservice
* Back-end periodic jobs to issue assessFees, sendBillAvailableNotification, etc. 
* Last Month Balance 
* This is a partial list. There may be more things needed to be implemented.

<br>

### SQL Tables Used 
Important Fields only.

#### user_info

*  user_id 
*  first_name
*  last_name
*  addr
*  phone
*  email
 
 
#### acct_info
 
 * user_id 
 * acct_num
 * fees
 * credit_limit
 * interest_rate
 * balance
 
#### transactions_info
 * trans_id
 * acct_num
 * year
 * month
 * day
 * description
 * is_credit
 * amount
 