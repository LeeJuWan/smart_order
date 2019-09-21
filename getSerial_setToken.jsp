<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<%@ include file="header.jsp" %>
<%
	try{
		int serialNumber = Integer.parseInt(request.getParameter("serialNumber"));
		
		String sql = "select token from USER where workplace_num=?";
		pstmt= conn.prepareStatement(sql);
		pstmt.setInt(1, serialNumber);
		rs = pstmt.executeQuery();
		String token = "";
		if(rs.next()){
			token = rs.getString("token");
		}
		out.clear();
		//-1이면 token을 가져오지 못함.
		out.println(token);
		out.flush();
		
		
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}




%>