<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="centered">
<div class="auth-card">
    <h1>SentraX</h1>
    <p class="subtitle">AI-Assisted Fraud Detection Dashboard</p>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error-text"><%= request.getAttribute("error") %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/login">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" required autofocus>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Log in</button>
    </form>
    <p class="hint">Demo login: <strong>analyst</strong> / <strong>analyst123</strong></p>
</div>
</body>
</html>
