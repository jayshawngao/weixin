<%@ page language="java" contentType="text/html;charset=UTF-8" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
    <table style="margin: 0 auto;" border="1" cellpadding="10">
        <tr>
            <td>openid(id)</td>
            <td>${userInfo.openid}</td>
        </tr>
        <tr>
            <td>nickname(昵称)</td>
            <td>${userInfo.nickname}</td>
        </tr>
        <tr>
            <td>sex(性别)</td>
            <td>
            <c:if test="${userInfo.sex==0}">未知</c:if>
            <c:if test="${userInfo.sex==1}">男</c:if>
            <c:if test="${userInfo.sex==2}">女</c:if>
            </td>
        </tr>
        <tr>
            <td>province(省)</td>
            <td>${userInfo.province}</td>
        </tr>
        <tr>
            <td>city(市)</td>
            <td>${userInfo.city}</td>
        </tr>
        <tr>
            <td>country(国家)</td>
            <td>${userInfo.country}</td>
        </tr>
        <tr>
            <td>headimgurl(头像)</td>
            <td><img alt="头像" style="width: 40px;height: 40px;" src="${userInfo.headimgurl}"></td>
        </tr>
        <tr>
            <td>privilege</td>
            <td>
            <c:forEach items="${userInfo.privilege}" var="p">
            ${p.String }|
            </c:forEach>
            </td>
        </tr>
        <tr>
            <td>unionid(unionid)</td>
            <td>${userInfo.unionid}</td>
        </tr>
    </table>
</body>
</html>