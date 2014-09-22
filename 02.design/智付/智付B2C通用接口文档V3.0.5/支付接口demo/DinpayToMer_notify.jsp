<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*, org.apache.commons.codec.digest.DigestUtils" %>
<%
/* *
 功能：智付页面跳转同步通知页面
 版本：3.0
 日期：2013-08-01
 说明：
 以下代码仅为了方便商户安装接口而提供的样例具体说明以文档为准，商户可以根据自己网站的需要，按照技术文档编写。

 * */
	//获取智付反馈的信息
	//商户号
	String merchant_code	= request.getParameter("merchant_code");

	//通知类型
	String notify_type = request.getParameter("notify_type");

	//通知校验ID
	String notify_id = request.getParameter("notify_id");

	//接口版本
	String interface_version = request.getParameter("interface_version");

	//签名方式
	String sign_type = request.getParameter("sign_type");

	//签名
	String dinpaySign = request.getParameter("sign");

	//商家订单号
	String order_no = request.getParameter("order_no");

	//商家订单时间
	String order_time = request.getParameter("order_time");

	//商家订单金额
	String order_amount = request.getParameter("order_amount");

	//回传参数
	String extra_return_param = request.getParameter("extra_return_param");

	//智付交易定单号
	String trade_no = request.getParameter("trade_no");

	//智付交易时间
	String trade_time = request.getParameter("trade_time");

	//交易状态 SUCCESS 成功  FAILED 失败
	String trade_status = request.getParameter("trade_status");

	//银行交易流水号
	String bank_seq_no = request.getParameter("bank_seq_no");


	/**
	 *签名顺序按照参数名a到z的顺序排序，若遇到相同首字母，则看第二个字母，以此类推，
	*同时将商家支付密钥key放在最后参与签名，组成规则如下：
	*参数名1=参数值1&参数名2=参数值2&……&参数名n=参数值n&key=key值
	**/


	//组织订单信息
	StringBuilder signStr = new StringBuilder();
	if(null != bank_seq_no && !bank_seq_no.equals("")) {
		signStr.append("bank_seq_no=").append(bank_seq_no).append("&");
	}
	if(null != extra_return_param && !extra_return_param.equals("")) {
		signStr.append("extra_return_param=").append(extra_return_param).append("&");
	}
	signStr.append("interface_version=V3.0").append("&");
	signStr.append("merchant_code=").append(merchant_code).append("&");
	if(null != notify_id && !notify_id.equals("")) {
		signStr.append("notify_id=").append(notify_id).append("&notify_type=").append(notify_type).append("&");
	}

	signStr.append("order_amount=").append(order_amount).append("&");
	signStr.append("order_no=").append(order_no).append("&");
	signStr.append("order_time=").append(order_time).append("&");
	signStr.append("trade_no=").append(trade_no).append("&");
	signStr.append("trade_status=").append(trade_status).append("&");
	if(null != trade_time && !trade_time.equals("")) {
		signStr.append("trade_time=").append(trade_time).append("&");
	}
	String key="123456789a123456789_";
	signStr.append("key=").append(key);
	String signInfo = signStr.toString();
	//将组装好的信息MD5签名
	String sign = DigestUtils.md5Hex(signInfo); //注意与支付签名不同  此处对String进行加密
	//比较智付返回的签名串与商家这边组装的签名串是否一致
	if(dinpaySign.equals(sign)) {
		//验签成功
		/**
		此处进行商户业务操作
		业务结束
		*/
		PrintWriter pw = resp.getWriter();
		pw.print("SUCCESS");
	}else
        {
		//验签失败 业务结束
	}

%>