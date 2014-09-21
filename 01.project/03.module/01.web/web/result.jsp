<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>U支付结果页面</title>
</head>
<body>
<div align="center">
    <%
        String message = (String) request.getAttribute("message");
    %>
    <h2>U支付结果页面</h2>
    结果信息：<%=StringUtils.trimToEmpty(message)%>
</div>
</body>
</html>
