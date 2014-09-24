<%@ page import="com.fatepay.utils.DateUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试[支付接口]页面</title>
</head>
<body>
<div align="center">
    <h2>测试[支付接口]页面</h2>
    <form action="trans.do" method="post">
        <table>
            <tr><td>服务名称：</td><td><input type="text" name="serviceCode" value="pay"></td></tr>
            <tr><td>商户号：</td><td><input type="text" name="merchantCode" value="880001"></td></tr>
            <tr><td>token串：</td><td><input type="text" name="token" value=""></td></tr>
            <tr><td>商户订单号：</td><td><input type="text" name="orderNo" value="M_<%=DateUtil.getNowDate()%>0000000001"></td></tr>
            <tr><td>金额(元)：</td><td><input type="text" name="orderAmount" value="0.01"></td></tr>
            <tr><td>公用回传参数：</td><td><input type="text" name="extraReturnParam" value="公用回传参数"></td></tr>
            <tr><td>商品名称：</td><td><input type="text" name="productName" value="商品"></td></tr>
            <tr><td>返回URL：</td><td><input type="text" name="returnUrl" value="testMerchantResult.jsp"></td></tr>
            <tr><td>通知URL：</td><td><input type="text" name="notifyUrl" value="testMerchantResult.jsp"></td></tr>
            <tr><td colspan="2" align="center"><input type="submit" value="提交"></td></tr>
        </table>
    </form>
</div>
</body>
</html>
