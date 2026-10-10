<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.synapsecalyx.sentrax.model.Alert, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | Alerts</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="nav.jspf" %>

<main class="container">
    <h2>Unresolved Alerts</h2>
    <table>
        <thead>
        <tr><th>Transaction</th><th>Rule</th><th>Message</th><th>Raised</th><th></th></tr>
        </thead>
        <tbody>
        <%
            List<Alert> alerts = (List<Alert>) request.getAttribute("alerts");
            for (Alert a : alerts) {
        %>
        <tr>
            <td>#<%= a.getTransactionId() %></td>
            <td><%= a.getRuleName() %></td>
            <td><%= a.getMessage() %></td>
            <td><%= a.getCreatedAt() %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/alerts">
                    <input type="hidden" name="alertId" value="<%= a.getId() %>">
                    <button type="submit">Resolve</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</main>
</body>
</html>
