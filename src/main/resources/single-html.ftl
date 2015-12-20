<#escape x as (x!)?html>


	<!DOCTYPE html>
	<html lang="en">
	  <head>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    
	
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">		
		<title>
			${sw.getTitle()}
		</title>
	
	    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	    <!--[if lt IE 9]>
	      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	    <![endif]-->
	  </head>
	  <body>
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>	    
	    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
	    <div class="container">
			<h1>${sw.getTitle()}</h1>			
			
			<hr/>
			
			<h2>Table of Contents</h2>		
			<#if sw.swagger.getTags()??>
			<#list sw.swagger.getTags() as tag>
			
				<h3>${tag.getName()}</h3>
				<h4>${tag.getDescription()}</h4>
				
				<div>						
					<table class="table table-bordered table-condensed">
						<tbody>
								<tr>
									<th>Index</th>								
									<th>Path</th>		
									<th>Summary</th>																												
								</tr>						
															 												
								<#list sw.getOperationIdsOfTag(tag.getName()) as operationId>
									<tr>
										<td><a href="#${operationId.serialize()}">${operationId_index + 1}</a></td>																
										<td><a href="#${operationId.serialize()}">${operationId.getMethod()} ${operationId.getPath()}</a></td>
										<td>
											<#if sw.getOperation(operationId)??>
												${sw.getOperation(operationId).getSummary()}
											</#if>											
										</td>											 					
									</tr>								
								</#list>		
	
							
						</tbody>
					</table>	
				</div>	
				
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
						
						
						<div>						
							<table class="table table-bordered table-condensed">
								<tbody>
										
										<tr>
											<td>Summary</td> <td>${operation.getSummary()}</td>
										</tr>
										<tr>											
											<td>Notes</td> <td>${operation.getDescription()}</td>
										</tr>
										<tr>											
											<td>Consumes</td> <td>${displayList(operation.getConsumes())}</td>
										</tr>
										<tr>											
											<td>Produces</td> <td>${displayList(operation.getProduces())}</td>																																						 				
										</tr>								
												
			
									
								</tbody>
							</table>	
						</div>	
														
				 
						<h4>Parameters</h4>		
						
						<div>				
							<table class="table table-bordered">
								<tbody>
										<tr>
											<th>Name</th>										
											<th>Parameter Type</th>	
											<th>Data Type</th>																					
											<th>Required</th>		
											<th>Description</th>																														
										</tr>	
										<#if operation.getParameters()??>
			 							<#list operation.getParameters() as param>
											<tr>																	
												<td>${param.getName()}</td>
												<td>${param.getIn()}</td>
												<td>${paramType(param)}</td>											
												<td>${param.getRequired()?string('yes', 'no')}</td>
												<td>${param.getDescription()}</td>
											</tr>								 					
										</#list>
										</#if>
									</tr>										
																				
								</tbody>						
													
							</table>		
						</div>	


						<h4>Responses</h4>		
						<div>				
							<table class="table table-bordered">
								<tbody>
										<tr>
											<th>HTTP Status Code</th>										
											<th>Reason</th>	
											<th>Response Model</th>																																																													
										</tr>	
										<#if operation.getResponses()??>
			 							<#list operation.getResponses()?keys as httpCode>
			 								<#assign response = operation.getResponses()[httpCode] >
											<tr>																	
												<td>${httpCode}</td>
												<td>${response.getDescription()}</td>
												<td>${responseType(response)}</td>											
											</tr>								 					
										</#list>
										</#if>
									</tr>										
																				
								</tbody>						
													
							</table>	
						</div>
		


						<hr/>									
					</#list>			
					
					
											
			</#list>
			</#if> 	
		</div>		    
	  </body>
	</html>

 
</#escape>


