<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>前台用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#mobile").val("");
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	</ul><br/>
	<form:form id="inputForm" modelAttribute="platUser" action="${ctx}/plat/platUser/update/changeMobile" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">修改前：</label>
			<div class="controls">
				<span class="help-inline"><font color="red">${platUser.mobile}</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">修改为：</label>
			<div class="controls">
				<form:input path="mobile" htmlEscape="false" maxlength="11" id="mobile" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">原因：</label>
			<div class="controls">
				<form:textarea path="reason" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
			</div>
		</div>
	</form:form>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th>类型</th>
			<th>内容</th>
			<th>原因</th>
			<th>操作时间</th>
			<th>操作人</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${jsPlatUserOplogList}" var="jsPlatUserOplog">
			<tr>
				<td>
						${fns:getDictLabel(jsPlatUserOplog.type, 'PlatUserOplogType', '')}
				</td>
				<td>
						${jsPlatUserOplog.content}
				</td>
				<td>
						${jsPlatUserOplog.reason}
				</td>
				<td>
					<fmt:formatDate value="${jsPlatUserOplog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${jsPlatUserOplog.createBy.loginName}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>