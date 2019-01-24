<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/12/14
  Time: 13:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mmall</title>
</head>
<br>
    <h1>Tomcat1</h1>
    <h1>Tomcat1</h1>
    <h1>Tomcat1</h1>
    springmvc上传文件
<form name="form1" method="post" action="/manage/product/upload.do" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springMvc上传文件"/>
</form>
富文本图片上传
<form name="form1" method="post" action="/manage/product/richtext_img_upload.do" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本图片上传"/>
</form>
</body>
</html>
