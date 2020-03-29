# Registration Service

Running the application
---

* In the root folder of your project, run `mvn clean install` to build the application. The app was tested on OpenJDK ver. `1.8.0_212` and Maven `3.5.0`.
* Start the application with `mvn spring-boot:run`.
* The following API will be available at `http://localhost:8090`:

| URI                        | HTTP Method |
|----------------------------|-------------|
| /registration/account      | POST        |

For example, you can create a new account in the system by making a `POST` request to `http://localhost:8090/registration/account` with the header `Content-Type: application/json` and the following content:
```json
{
    "username": "MyLogin",
    "password": "My_P@ssw0rd",
    "dob": "1987-06-23",
    "paymentCardNumber": "1111222233334444"
}
```
There is a list of blocked issuer identification numbers (IINs) initialized with a few predefined values on application start. Should you need to modify that list, please add one 6-digit IIN per line in this file: `resources/blocked_iins.txt`.