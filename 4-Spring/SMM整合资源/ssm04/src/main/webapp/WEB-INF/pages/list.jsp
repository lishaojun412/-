<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
dshfhdsh

<table>
    <c:forEach items="${list}" var="user">
        <tr>
            <td>${user.name}</td>
            <td>${user.sex}</td>
            <td>${user.address}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
