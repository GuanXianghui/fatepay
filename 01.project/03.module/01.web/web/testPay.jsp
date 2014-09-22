<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试[支付接口]页面</title>
</head>
<body>
<div align="center">
    <h2>测试[支付接口]页面</h2>
    <form action="trans.do" method="post">
        服务名称：<input type="text" name="serviceCode" value="pay"><br>
        商户号：<input type="text" name="merchantCode" value="880001"><br>
        token串：<input type="text" name="token" value=""><br>
        商户订单号：<input type="text" name="orderNo" value="0000000001"><br>
        金额：<input type="text" name="orderAmount" value="0.01"><br>
        公用回传参数：<input type="text" name="extraReturnParam" value="公用回传参数"><br>
        商品名称：<input type="text" name="productName" value="商品"><br>
        返回URL：<input type="text" name="returnUrl" value="http://www.baidu.com/pg.do"><br>
        通知URL：<input type="text" name="notifyUrl" value="http://www.baidu.com/bg.do"><br>
        <input type="submit" value="提交">
    </form>
</div>
</body>
</html>
