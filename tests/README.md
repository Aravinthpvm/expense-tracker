# Test Suite Directory

To comply with the assignment's folder layout requirements while maintaining the idiomatic structure of a Java/Spring Boot project, this directory serves as a pointer to the main test suite.

In Java/Maven projects, test code must reside in the standard Maven directory structure to compile and run properly with `./mvnw test` out of the box.

* The test suite is located at: [src/test/java/com/diligent/expensetracker/ExpenseControllerTest.java](../src/test/java/com/diligent/expensetracker/ExpenseControllerTest.java)
* The tests can be executed at the root of the project using:
  ```bash
  ./mvnw test
  ```
  *(On Windows, use `mvnw.cmd test` instead)*
