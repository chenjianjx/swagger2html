<#escape x as (x!)?html>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<html>
		<head>
			<style type="text/css">
				body {background-color: rgb(246,246,246); }
				h1 { color: rgb(0,51,102); font-family: helvetica; font-size: 15pt; font-weight: bold; }
				h2 { color: rgb(0,51,102); font-family: helvetica; font-size: 13pt; font-weight: bold; }
				h3 { color: rgb(0,51,102); font-family: helvetica; font-size: 9pt; font-weight: bold; }
				p { color: rgb(0,51,102); font-family: helvetica; font-size: 10pt; font-weight: normal; }
				a { color: rgb(0,51,102); font-family: helvetica; font-size: 10pt; font-weight: normal; }
				a.th { color: rgb(246,246,246); background-color: rgb(102,102,102); font-family: helvetica; font-size: 10pt; font-weight: bold; }
				p.error { color: rgb(255,0,0); font-family: helvetica; font-size: 10pt; font-weight: normal; }
				th.color { color: rgb(246,246,246); background-color: rgb(102,102,102); font-family: helvetica; font-size: 10pt; font-weight: bold; }
				th.no_color { color: rgb(0,51,102); font-family: helvetica; font-size: 10pt; font-weight: bold; }
				td.color { color: rgb(0,51,102); background-color: rgb(255,225,225); font-family: helvetica; font-size: 10pt; font-weight: normal; }
				td.color_pre { color: rgb(0,51,102); background-color: rgb(255,225,225); font-family: helvetica; font-size: 10pt; font-weight: normal; white-space: pre }
				td.no_color { color: rgb(0,51,102); font-family: helvetica; font-size: 10pt; font-weight: normal; }
				td.no_color_pre { color: rgb(0,51,102); font-family: helvetica; font-size: 10pt; font-weight: normal; white-space: pre }
				.methodBody {padding-top: 50px}
			</style>
		
		
			 
			<title>
				${sw.getTitle()}
			</title>
		</head>	


		<body>
			<h1>${sw.getTitle()}</h1>			
			
			<hr/>
			
			
			
			
			
			<h2>Table of Contents</h2>		
			<#if sw.swagger.getTags()??>
			<#list sw.swagger.getTags() as tag>
			
			<h3>${tag.getName()}</h3>
			<h4>${tag.getDescription()}</h4>
									
				<table cellspacing="1" cellpadding="1" border="0">
					<tbody>
							<tr valign="top">
								<th align="left" class="color">Index</th>								
								<th align="left" class="color">Path</th>																														
							</tr>						
						 				
							
							<#list sw.getOperationIdsOfTag(tag.getName()) as operationId>
								<tr valign="top">
									<td align="left" class="color"><a href="#${operationId.serialize()}">${operationId_index + 1}</a></td>				
											
									<td align="left" class="color"><a href="#${operationId.serialize()}">${operationId.getMethod()} ${operationId.getPath()}</a></td>							 					
								</tr>								
							</#list>		

						
					</tbody>
				</table>	
				
			</#list>
			</#if>
			
			<hr/>
			
			
			
			<!-- One tag after another-->			
			
			<#if sw.swagger.getTags()??>
			<#list sw.swagger.getTags() as tag>					
				<h2>${tag.getName()}</h2>
					<#list sw.getOperationIdsOfTag(tag.getName()) as operationId>
					
						<!-- every operation -->
						<h3><a name="${operationId.serialize()}">${operationId.getMethod()} ${operationId.getPath()}</a></h3>
						<#assign operation = sw.getOperation(operationId)>
						<h4>Summary: ${operation.getSummary()}</h4>
						<h4>Notes: ${operation.getDescription()}</h4>
						<h4>Consumes: ${displayList(operation.getConsumes())}</h4>					
						<h4>Produces: ${displayList(operation.getProduces())}</h4>
						
						<h4>Parameters</h4>						
						<table cellspacing="1" cellpadding="1" border="0">
							<tbody>
									<tr valign="top">
										<th align="left" class="color">Name</th>										
										<th align="left" class="color">In</th>	
										<th align="left" class="color">Type</th>																					
										<th align="left" class="color">Required</th>		
										<th align="left" class="color">Description</th>																														
									</tr>	
									<#if operation.getParameters()??>
		 							<#list operation.getParameters() as param>
										<tr valign="top">																	
											<td align="left" class="color">${param.getName()}</td>
											<td align="left" class="color">${param.getIn()}</td>
											<td align="left" class="color">${paramType(param)}</td>											
											<td align="left" class="color">${param.getRequired()?string('yes', 'no')}</td>
											<td align="left" class="color">${param.getDescription()}</td>
										</tr>								 					
									</#list>
									</#if>
								</tr>										
																			
							</tbody>						
												
						</table>			





						<hr/>									
					</#list>			
					
					
											
			</#list>
			</#if> 	
			
			
		</body>			

	</html>  
</#escape>


