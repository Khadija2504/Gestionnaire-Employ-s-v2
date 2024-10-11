<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>All Applications</title>
  <style>
    table {
      border-collapse: collapse;
      width: 100%;
    }
    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
    tr:nth-child(even) {
      background-color: #f9f9f9;
    }
    .download-link {
      color: blue;
      text-decoration: underline;
      cursor: pointer;
    }
  </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<h1>All Applications</h1>

<c:choose>
  <c:when test="${not empty applications}">
    <table>
      <thead>
      <tr>
        <th>Candidate Name</th>
        <th>Email</th>
        <th>Phone Number</th>
        <th>Applied Date</th>
        <th>Job Title</th>
        <th>Resume</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${applications}" var="app">
        <tr>
          <td>${app.candidateName}</td>
          <td>${app.candidateEmail}</td>
          <td>${app.candidatePhone}</td>
          <td>${app.appliedDate}</td>
          <td>${app.jobOffer.title}</td>
          <td>
            <c:choose>
              <c:when test="${not empty app.resume}">
                <a href="application?action=downloadResume&applicationId=${app.id}" class="download-link">
                  Download Resume (${app.resume})
                </a>
              </c:when>
              <c:otherwise>
                Resume not available
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </c:when>
  <c:otherwise>
    <p>No applications found for this job offer.</p>
  </c:otherwise>
</c:choose>
</body>
</html>