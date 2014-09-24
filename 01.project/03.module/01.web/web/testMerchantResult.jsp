<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>商户结果页面</title>
</head>
<body>
<div align="center">
    <h2>商户结果页面</h2>
    <table border="1">
        <tr><th>字段</th><th>值</th></tr>
        <tr><td>商户订单号</td><td><%=StringUtils.trimToEmpty(request.getParameter("orderNo"))%></td></tr>
        <tr><td>交易金额(元)</td><td><%=StringUtils.trimToEmpty(request.getParameter("orderAmount"))%></td></tr>
        <tr><td>交易状态</td><td><%=StringUtils.trimToEmpty(request.getParameter("tradeState"))%></td></tr>
        <tr><td>交易状态描述</td><td><%=StringUtils.trimToEmpty(request.getParameter("tradeDesc"))%></td></tr>
        <tr><td>U支付流水</td><td><%=StringUtils.trimToEmpty(request.getParameter("tradeNo"))%></td></tr>
        <tr><td>U支付时间</td><td><%=StringUtils.trimToEmpty(request.getParameter("tradeDateTime"))%></td></tr>
        <tr><td>公用回传参数</td><td><%=StringUtils.trimToEmpty(request.getParameter("extraReturnParam"))%></td></tr>
    </table>
</div>
</body>
</html>