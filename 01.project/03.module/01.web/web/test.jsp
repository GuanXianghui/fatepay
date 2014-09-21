<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试页面</title>
</head>
<body>
<div align="center">
    <%
        String message = (String) request.getAttribute("message");
        if (StringUtils.isNotBlank(message)) {
    %>
    提示信息：<%=message%>
    <%
        }
    %>
    <form action="test.do" method="post">
        用户名：<input type="text" name="userName"><br>
        密码：<input type="password" name="password"><br>
        <input type="submit" value="提交">
    </form>
</div>
</body>
</html>
