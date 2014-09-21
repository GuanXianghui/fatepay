<%@ page import="com.fatepay.utils.TokenUtil" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.fatepay.utils.DateUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>查看所有服务器端token</title>
</head>
<body>
<div align="center">
    <h2>查看所有服务器端token</h2>
    <%
        Map<String, Date> tokenMap = TokenUtil.getInstance().getTokenMap();
    %>
    <table border="1">
        <tr>
            <th colspan="3" align="center">
                总数：<%=tokenMap.size()%>
            </th>
        </tr>
        <tr>
            <th>商户号</th>
            <th>token</th>
            <th>失效时间</th>
        </tr>
    <%
        Iterator iterator = tokenMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            int index = key.indexOf("_");
            String merchantCode = key.substring(0, index);
            String token = key.substring(index + 1);
            String expireTime = DateUtil.getLongDate(tokenMap.get(key)) + " " + DateUtil.getLongTime(tokenMap.get(key));
    %>
        <tr>
            <td><%=merchantCode%></td>
            <td><%=token%></td>
            <td><%=expireTime%></td>
        </tr>
    <%
        }
    %>
    </table>
</div>
</body>
</html>