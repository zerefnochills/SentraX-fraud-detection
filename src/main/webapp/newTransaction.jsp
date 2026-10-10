<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.synapsecalyx.sentrax.model.Customer, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>SentraX | New Transaction</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<%@ include file="nav.jspf" %>

<main class="container narrow">
    <h2>Submit Transaction</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error-text"><%= request.getAttribute("error") %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/transaction/new" class="form-card">
        <label for="customerId">Customer</label>
        <select id="customerId" name="customerId" required>
            <%
                List<Customer> customers = (List<Customer>) request.getAttribute("customers");
                for (Customer c : customers) {
            %>
            <option value="<%= c.getId() %>"><%= c.getFullName() %> (#<%= c.getId() %>)</option>
            <% } %>
        </select>

        <label for="amount">Amount</label>
        <input type="number" step="0.01" id="amount" name="amount" required>

        <label for="currency">Currency</label>
        <input type="text" id="currency" name="currency" value="INR" required>

        <label for="merchant">Merchant</label>
        <input type="text" id="merchant" name="merchant" required>

        <label for="location">Location</label>
        <input type="text" id="location" name="location" required>

        <label for="deviceId">Device ID</label>
        <input type="text" id="deviceId" name="deviceId" required>

        <button type="submit">Evaluate Transaction</button>
    </form>
</main>
</body>
</html>
