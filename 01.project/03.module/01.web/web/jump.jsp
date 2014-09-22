<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Iterator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>跳转页面</title>
    <meta http-equiv="Expires" CONTENT="0">
    <meta http-equiv="Cache-Control" CONTENT="no-cache">
    <meta http-equiv="Cache-Control" CONTENT="no-store">
    <meta http-equiv="Pragma" CONTENT="no-cache">
    <script type="text/javascript">
        window.onload = init;
        function init() {
            //document.forms["jumpForm"].submit();
        }
    </script>
    <%
        request.setCharacterEncoding("UTF-8");
        String jumpUrl = (String)request.getAttribute("jumpUrl");
        Properties properties = (Properties)request.getAttribute("properties");
    %>
</head>
<body>
<div align="center">
    <form name="jumpForm" method="post" action="<%=jumpUrl%>">
        <%
            Iterator iterator = properties.keySet().iterator();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                String value = properties.getProperty(key);
        %>
        <input type="hidden" name="<%=key%>" value="<%=value%>">
        <%
            }
        %>
        <input type="submit" value="submit">
    </form>
</div>
</body>
</html>