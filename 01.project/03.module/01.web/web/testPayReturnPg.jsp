<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试[支付结果前台返回接口]页面</title>
</head>
<body>
<div align="center">
    <h2>测试[支付结果前台返回接口]页面</h2>
    <form action="payReturnPg.do" method="post">
        U支付交易网关流水：<input type="text" name="uPayTradeNo" value="2014092000000001"><br>
        商家号：<input type="text" name="merchant_code" value="000001"><br>
        通知类型：<input type="text" name="notify_type" value="page_notify"><br>
        通知校验ID：<input type="text" name="notify_id" value="asdkofahsidh0f1"><br>
        接口版本：<input type="text" name="interface_version" value="V3.0"><br>
        商户网站唯一订单号：<input type="text" name="order_no" value="2014092000000001"><br>
        商户订单时间：<input type="text" name="order_time" value="2014-09-20 12:12:12"><br>
        商户订单总金额：<input type="text" name="order_amount" value="1.00"><br>
        公用回传参数：<input type="text" name="extra_return_param" value=""><br>
        智付交易订单号：<input type="text" name="trade_no" value="20140920098621"><br>
        智付交易订单时间：<input type="text" name="trade_time" value="2014-09-20 13:13:13"><br>
        交易状态：<input type="text" name="trade_status" value="SUCCESS"><br>
        支付银行代码：<input type="text" name="bank_code" value=""><br>
        网银交易流水号：<input type="text" name="bank_seq_no" value=""><br>
        签名方式：<input type="text" name="sign_type" value="MD5"><br>
        签名：<input type="text" name="sign" value="3e5bb88211b82aa562841254ac0a8198"><br>
        <input type="submit" value="提交">
    </form>
</div>
</body>
</html>
