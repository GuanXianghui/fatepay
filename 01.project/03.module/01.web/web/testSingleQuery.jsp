<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试[单笔交易查询接口]页面</title>
</head>
<body>
<div align="center">
    <h2>测试[单笔交易查询接口]页面</h2>
    <form action="trans.do" method="post">
        商户号：<input type="text" name="merchantCode" value="880001"><br>
        服务名称：<input type="text" name="serviceCode" value="singleQuery"><br>
        token串：<input type="text" name="token" value="123123123"><br>
        商户订单号：<input type="text" name="orderNo" value="2014091900000001"><br>
        <input type="submit" value="提交">
    </form>
</div>
</body>
</html>
