<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | Error</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="centered">
<div class="auth-card">
    <h1>Something went wrong</h1>
    <p><%= exception != null ? exception.getMessage() : "Unknown error." %></p>
    <a class="link-btn" href="<%= request.getContextPath() %>/dashboard">Back to dashboard</a>
</div>
</body>
</html>
