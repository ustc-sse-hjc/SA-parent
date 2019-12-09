<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns:x="urn:schemas-microsoft-com:office:excel">
 <script type="text/javascript">  
      function exportExcel(){  
          window.open('queryData2.jsp?exportToExcel=YES');  
      }  
 </script>

<head>
<!-- 显示网格线 -->    
     <xml>  
                <x:ExcelWorkbook>    
                    <x:ExcelWorksheets>    
                        <x:ExcelWorksheet>    
                            <!-- <x:Name>人员列表</x:Name>   -->
                            <x:WorksheetOptions>    
                                <x:Print>    
                                    <x:ValidPrinterInfo />    
                                </x:Print>    
                            </x:WorksheetOptions>    
                        </x:ExcelWorksheet>    
                    </x:ExcelWorksheets>    
                </x:ExcelWorkbook>    
            </xml>    
    <!-- 显示网格线 -->

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>打印预览</title>
</head>
<body>
<table border=1>
<tr><td>ID</td><td>CPU使用率</td><td>内存使用率</td><td>磁盘使用率</td></tr>
<center>
 <%  
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    java.util.Date currentTime = new java.util.Date();
    String time = formatter.format(currentTime);
    System.out.println(time);
    String exportToExcel = request.getParameter("exportToExcel");  
      if (exportToExcel != null  && exportToExcel.toString().equalsIgnoreCase("YES")) {  
          response.setContentType("application/vnd.ms-excel");  
           response.setHeader("Content-Disposition", "inline; filename="+"server.xls");  
   }  
%>     

<%
 Class.forName("com.mysql.jdbc.Driver").newInstance();
 Connection con=java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/sa?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","0000");
Statement stmt=con.createStatement();

ResultSet rst=stmt.executeQuery("select * from server");

while(rst.next())
 {		
     out.println("<tr>");
     out.println("<td>"+rst.getString("id")+"</td>");
      out.println("<td>"+rst.getDouble("cpu")+"</td>");
     out.println("<td>"+rst.getDouble("mem")+"</td>");
     out.println("<td>"+rst.getDouble("disk")+"</td>"); 
     out.println("</tr>");
 }

 //关闭连接、释放资源
 rst.close();
 stmt.close();
 con.close();

 %>
<%  
            if (exportToExcel == null) {  
%>  
  <center>
 <!--  <a href="javascript:exportExcel();">导出为Excel</a> -->
  <button onclick="window.location.href='javascript:exportExcel();'">导出为Excel</button>
  <div style="width: 900px; height: 5px;"></div>
  </center>
<%  
            }  
%>
</table>
</body>
</html>