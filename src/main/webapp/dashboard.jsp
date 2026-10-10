<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.synapsecalyx.sentrax.model.Transaction, com.synapsecalyx.sentrax.model.Alert, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="nav.jspf" %>

<main class="container">
    <h2>Overview</h2>
    <div class="stat-row">
        <div class="stat-card safe"><span class="stat-value"><%= request.getAttribute("safeCount") %></span><span>Safe</span></div>
        <div class="stat-card suspicious"><span class="stat-value"><%= request.getAttribute("suspiciousCount") %></span><span>Suspicious</span></div>
        <div class="stat-card fraud"><span class="stat-value"><%= request.getAttribute("fraudCount") %></span><span>Fraud</span></div>
        <div class="stat-card alerts"><span class="stat-value"><%= ((List<?>) request.getAttribute("unresolvedAlerts")).size() %></span><span>Open Alerts</span></div>
    </div>

    <div class="panel-row">
        <section class="panel">
            <h3>Recent Transactions</h3>
            <table>
                <thead>
                <tr><th>ID</th><th>Customer</th><th>Amount</th><th>Location</th><th>Risk</th><th>Score</th></tr>
                </thead>
                <tbody>
                <%
                    List<Transaction> txns = (List<Transaction>) request.getAttribute("recentTransactions");
                    for (Transaction t : txns) {
                %>
                <tr class="row-<%= t.getRiskLevel().name().toLowerCase() %>">
                    <td><%= t.getId() %></td>
                    <td><%= t.getCustomerId() %></td>
                    <td><%= String.format("%.2f", t.getAmount()) %> <%= t.getCurrency() %></td>
                    <td><%= t.getLocation() %></td>
                    <td><span class="badge badge-<%= t.getRiskLevel().name().toLowerCase() %>"><%= t.getRiskLevel().getLabel() %></span></td>
                    <td><%= String.format("%.0f", t.getRiskScore()) %></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </section>

        <section class="panel">
            <h3>Open Alerts</h3>
            <ul class="alert-list">
                <%
                    List<Alert> alerts = (List<Alert>) request.getAttribute("unresolvedAlerts");
                    for (Alert a : alerts) {
                %>
                <li><strong><%= a.getRuleName() %></strong> &middot; txn #<%= a.getTransactionId() %><br>
                    <span><%= a.getMessage() %></span></li>
                <% } %>
            </ul>
            <a class="link-btn" href="<%= request.getContextPath() %>/alerts">Manage all alerts &rarr;</a>
        </section>
    </div>
</main>
</body>
</html>
