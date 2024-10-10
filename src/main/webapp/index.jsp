<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %></h1>
<br/>
<form action="addEmployee?action=addEmployee" method="post">
  First Name: <input type="text" name="firstName"><br>
  Last Name: <input type="text" name="lastName"><br>
  Phone Number: <input type="text" name="phoneNumber"><br>
  Salary: <input type="text" name="salary"><br>
  Birthday: <input type="date" name="birthday"><br>
  Hire Date: <input type="date" name="hireDate"><br>
  Position: <input type="text" name="position"><br>
  Kids Number: <input type="text" name="kidsNum"><br>
  Total Salary: <input type="text" name="totalSalary"><br>
  Situation: <input type="text" name="situation"><br>
  Department: <input type="text" name="department"><br>
  Email: <input type="email" name="email"><br>
  Password: <input type="password" name="password"><br>
  <button type="submit"> add employee</button>
</form>
</body>
</html>