<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.commons.codec.digest.DigestUtils"%>
<%
		//设置编码 
		request.setCharacterEncoding("UTF-8");
        //获取表单信息
		StringBuilder signSrc = new StringBuilder();
        
		String interface_version = "V3.0";
		String sign_type = "MD5";
		String service_type = "single_trade_query";
		
		String merchant_code = request.getParameter("merchant_code");
		String order_no = request.getParameter("order_no");
		String trade_no = request.getParameter("trade_no");
		String key = request.getParameter("key");
		
		System.out.println("interface_version=" + interface_version);
		System.out.println("merchant_code=" + merchant_code);
		System.out.println("order_no=" + order_no);
		System.out.println("service_type=" + service_type);
		System.out.println("sign_type=" + sign_type);
		System.out.println("trade_no=" + trade_no);
		System.out.println("key=" + key);
		
		signSrc.append("interface_version=").append(interface_version).append("&");
		signSrc.append("merchant_code=").append(merchant_code).append("&");
		signSrc.append("order_no=").append(order_no).append("&");
		signSrc.append("service_type=").append(service_type).append("&");
		if (!"".equals(trade_no)) {
			signSrc.append("trade_no=").append(trade_no).append("&");
		}
		
		signSrc.append("key=").append(key);
		String sign = signSrc.toString();
		
		System.out.println("sign=" + sign);
		
		//sign = MD5Digest.encrypt(sign);
		sign = DigestUtils.md5Hex(sign.getBytes());
		System.out.println(sign);
		
		//String url = "https://query.dinpay.com/query?service_type="+service_type+"&merchant_code="+merchant_code+"&interface_version="+interface_version+"&sign_type="+sign_type+"&sign="+sign+"&order_no="+order_no+"&trade_no="+trade_no;
		//System.out.println(url);
		
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">

</head>
<body onLoad="javascript:document.getElementById('queryForm').submit();">
<form  id="queryForm" action="https://query.dinpay.com/query" method="post">
	<input type="hidden" name="service_type" value="<%=service_type%>" />
    <input type="hidden" name="interface_version" value="<%=interface_version%>" />
	<input type="hidden" name="sign_type" value="<%=sign_type%>" />
    <input type="hidden" name="sign" value="<%=sign%>" />
    <input type="hidden" name="order_no" value="<%=order_no%>" />
	<input type="hidden" name="merchant_code" value="<%=merchant_code%>" />
</form>
</body>
</html>