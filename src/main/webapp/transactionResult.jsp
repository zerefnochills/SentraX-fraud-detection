<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.synapsecalyx.sentrax.model.Transaction" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | Result</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="nav.jspf" %>

<main class="container narrow">
    <%
        Transaction t = (Transaction) request.getAttribute("scoredTransaction");
    %>
    <h2>Evaluation Result</h2>
    <div class="result-card result-<%= t.getRiskLevel().name().toLowerCase() %>">
        <p class="result-label"><%= t.getRiskLevel().getLabel() %></p>
        <p class="result-score">Risk score: <%= String.format("%.1f", t.getRiskScore()) %> / 100</p>
        <p>Transaction #<%= t.getId() %> &middot; <%= String.format("%.2f", t.getAmount()) %> <%= t.getCurrency() %></p>
        <p>Status: <%= t.getStatus() %></p>
    </div>
    <a class="link-btn" href="<%= request.getContextPath() %>/transaction/new">Submit another &rarr;</a>
    <a class="link-btn" href="<%= request.getContextPath() %>/dashboard">Back to dashboard &rarr;</a>
</main>
</body>
</html>
