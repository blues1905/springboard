<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>목록</title>
<style type="text/css">
	li {list-style: none; float: left; padding: 6px;}
</style>
</head>
<body>
	<table border="1">
		<tr>
			<th>NO</th>
			<th>제목</th>
			<th>작성자</th>
			<th>작성일</th>
			<th>조회수</th>
		</tr>
		<c:forEach var="board" items="${boardList}" varStatus="loop">
			<tr>
				<td>${board.seq}</td>
				<td><a href="<c:url value="/board/read/${board.seq}" />">
						${board.title}</a></td>
				<td>${board.writer}</td>
				<td>${board.regDate}</td>
				<td>${board.cnt}</td>
			</tr>
		</c:forEach>
	</table>
	<a href="<c:url value="/board/write" />">새글</a>
	<div>
		<ul>
			<c:if test="${pageMaker.prev}">
				<li><a href="list${pageMaker.makeQuery(pageMaker.startPage - 1)}">이전</a></li>
			</c:if>
			
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="idx">
				<li><a href="list${pageMaker.makeQuery(idx)}">${idx}</a></li>
			</c:forEach>
			
			<c:if test="${pageMaker.next && pageMaker.endPage > 0}">
				<li><a href="list${pageMaker.makeQuery(pageMaker.endPage + 1)}">다음</a></li>
			</c:if>
		</ul>
	</div>
</body>
</html>