# Credit Card User Microservices

### Spring Final Project (Work In Progress)

This project seeks to improve our understanding of Spring Micro Services by extending the skeleton Credit Card User code. Beyond the existing printAllUsers and addUser microservices the following microservices will be implemented:

#### makePurchase
###### POST Method
This microservice will be called by the merchant's system at the point of sale (PoS) when the user decides to make a purchase. It will check if the user has available credit i.e. the account balance has not exceeded the user's credit limit.

#### makePayment
###### POST Method
This microservice will be called by the user's credit card app or from the credit card account at the bank website to make a payment towards the account balance.

#### printStatement
###### POST Method
This microservice will be called by the user's credit card app or from the credit card account at the bank website to print the user's statement.
 
