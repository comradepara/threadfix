<%@ include file="/common/taglibs.jsp"%>

<table class="table table-striped">
	<thead>
		<tr>
			<th class="first"></th>
			<th>File Name</th>
			<th>Type</th>
			<th>Upload Date</th>
			<th class="centered">Download</th>
			<c:if test="${ canModifyVulnerabilities }">
				<th class="centered last">Delete</th>
			</c:if>
			<th></th>
		</tr>
	</thead>
	<tbody id="wafTableBody">
	<c:if test="${ empty vulnerability.documents }">
		<tr class="bodyRow">
			<td colspan="7" style="text-align:center;">No files found.</td>
		</tr>
	</c:if>
	<c:forEach var="document" items="${ vulnerability.documents }" varStatus="status">
		<tr class="bodyRow">
			<td id="docNum${ status.count }"><c:out value="${ status.count }" /></td>
			<td id="name${ status.count }"><c:out value="${ document.name }"/></td>
			<td id="type${ status.count }"><c:out value="${ document.type }"/></td>
			<td id="uploadDate${ status.count }" >
				<fmt:formatDate value="${ document.createdDate }" pattern="hh:mm:ss MM/dd/yyyy"/>
			</td>			
			<td class="centered"> 
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/download" var="downloadUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
                <a class="btn docDownload" data-download-form="downloadForm${ document.id }">Download</a>
				<form id="downloadForm${ document.id }" method="POST" action="${ fn:escapeXml(downloadUrl) }"></form>				
			</td>			
			<c:if test="${ canModifyVulnerabilities }">
			<td class="centered">
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/delete" var="deleteUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
                <a class="btn btn-danger docDelete" data-delete-form="deleteForm${ document.id }">Delete</a>
				<form id="deleteForm${ document.id }" method="POST" action="${ fn:escapeXml(deleteUrl) }"></form>					
			</td>
			<td>
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/view" var="viewUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
				<a href="<c:out value="${ viewUrl }"/>" target="_blank">View File</a>
			</td>
			</c:if>
		</tr>
	</c:forEach>
	</tbody>
</table>
<c:if test="${ canModifyVulnerabilities }">
		<div style="margin-top:10px;margin-bottom:7px;">
			<a id="uploadDocVulnModalLink${ vulnerability.id }" href="#uploadDocVuln${ vulnerability.id }" role="button" class="btn" data-toggle="modal">Add File</a>
			<%@ include file="/WEB-INF/views/applications/modals/uploadDocVulnModal.jsp" %>
		</div>	
</c:if>