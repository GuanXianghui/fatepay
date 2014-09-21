<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>测试[获取token接口]页面</title>
</head>
<body>
<div align="center">
    <h2>测试[获取token接口]页面</h2>
    <form action="trans.do" method="post">
        商户号：<input type="text" name="merchantCode" value="880001"><br>
        服务名称：<input type="text" name="serviceCode" value="getToken"><br>
        版本号：<input type="text" name="versionCode" value="1.0"><br>
        编码类型：<input type="text" name="charsetType" value="UTF-8"><br>
        签名方式：<input type="text" name="signType" value="MD5"><br>
        签名：<input type="text" name="sign" value="0d1eabfd316e570eca576e4550adebb1"><br>
        <input type="submit" value="提交">
    </form>
</div>
</body>
</html>
