<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#test">Test</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

The project is to add functionality for a transfer of money between accounts.
Transfers has been specified by providing:
* accountFrom id
* accountTo id
* amount to transfer between accounts

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- GETTING STARTED -->    
## Getting Started

The challenge was to add functionality for a transfer of money between the accounts.

I have mde below changes as a part of this requirement :-


1. I have created new request object **AmountTransferRequest** with below fields :-
    * fromAccountId
    * toAccountId
    * amount
   
2. In the **AccountsController** I have exposed one endpoint - **/transfer** which is POST type and consumes JSON. Below is th sample request:-

   `{
   "fromAccountId": "Id-123",
   "toAccountId": "Id-456",
   "amount": 500
   }`
   
3. Implemented new method **transferAmount** in **AccountsRepository** make the transaction between two accounts. The input argument for this method is **AmountTransferRequest**.
   I have made **transferAmount** as synchronized so that it can called among multiple threads, so that it can be prevented from deadlock, result in corrupted account state, 
   It will also work efficiently for multiple transfers happening at the same time.

4. Made changes in AccountService and created the method **transferAmount** and calling the **AccountsRepository** transferAmount method.

5. After transferring the amount successfully, I am calling the NotificationService to send the notification to the both Account holders.

6. I have made changes **Account** class and create below two methods:-
    1. **withdrawAmount** - to withdraw amount from source account.
    2. **depositAmount** - to deposit amount in destination account

7. I have handled below exceptional cases :-

    1. **AccountNotFoundException** - In case any of the two accounts are invalid or not present in the system the method will throw this exception.
    2. **InsufficientBalanceException** - If Source account has not enough balance then this exception would be thrown.

8. I have covered all the above changes in Junit test cases.


### Test
To start application use command:
```sh
gradlew test
```
src/test/java - tests sources

To build the application use command:
```sh
gradlew clean build
```

### Start
To start application use command:
```sh
gradlew run
```

#### Sample Request for Account creation

```sh
endpoint - POST http://localhost:18080/v1/accounts

Account 1d-123
{
    "accountId": "Id-123",
    "balance": "500"
}

Account 1d-456
{
    "accountId": "Id-456",
    "balance": "0"
}
```

#### Sample Request to get Account details

```sh
endpoint - GET http://localhost:18080/v1/accounts/Id-123
```

#### Sample Request for Transfer:

```sh
{
    "fromAccountId": "Id-123",
    "toAccountId": "Id-456",
    "amount": 500
}

```






