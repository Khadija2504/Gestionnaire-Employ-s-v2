<%--
  Created by IntelliJ IDEA.
  User: safiy
  Date: 10/9/2024
  Time: 8:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="addApplication" method="post" enctype="multipart/form-data">
  <input name="candidateName" type="text">
  <input name="candidateEmail" type="email">
  <input name="phoneNumber" type="text">
  <input name="resume" type="file" id="resume">
  <input type="hidden" name="jobOfferId" value="${param.jobOfferId}">
  <button type="submit">add application</button>
</form>
</body>
</html>
