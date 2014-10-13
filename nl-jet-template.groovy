<!--
Email Generated using nl-jet template https://github.com/sarod/nl-jet
-->
<style>
table, td, th{
  font-family:Verdana,Helvetica,sans serif;
  font-size:11px;
  color:black;
}


</style>
<body style="font-family:Verdana,Helvetica,sans serif;font-size:11px;color:black;">

<!--
<% 

def divStyleBase = "padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px;"
def dangerColors = "background-color: #F2DEDE; color: #A94442;border-color: #EBCCD1;"
def divStyleDanger = divStyleBase + dangerColors
def divStyleSuccess = divStyleBase + "background-color: #DFF0D8; border-color: #D6E9C6;color: #3C763D;"
def divStyleWarning = divStyleBase + "background-color: #FCF8E3; border-color: #FAEBCC;color: #8A6D3B;"
def divStyleInfo = divStyleBase + "background-color: #D9EDF7;border-color: #BCE8F1;color: #31708F;"
def consoleStyle = "font-family:Courier New, Courier, monospace; font-size:11px;color:black;";


def totalCount = 0;
def skipCount = 0;
def failCount = 0;

it.JUnitTestResult.each{ junitResult ->
    junitResult.getChildren().each { packageResult ->
      totalCount += packageResult.getTotalCount()
      skipCount += packageResult.getSkipCount()
      failCount += packageResult.getFailCount()
  }
}
def successCount = totalCount - skipCount - failCount;


def changeSet = build.changeSet
def hadChanges = false
def changesCount = 0
def authors = []
if(changeSet != null) {
  changeSet.each() { cs ->
    hadChanges = true
    if (!authors.contains(cs.author)) {
        authors.add(cs.author)
    }
    changesCount ++
  }
}
def resultTrend = hudson.model.ResultTrend.getResultTrend(build);


def h2style = " font-size:120%; text-transform: capitalize;"
%>
-->
<div style="
  <%= switch(build.result.toString()) {    
    case "FAILURE":
    case "NOT_BUILT": 
    case "ABORTED":
      return divStyleDanger    
    case "SUCCESS":
      return divStyleSuccess
    case "UNSTABLE":
      return divStyleWarning
    default:
      return ""   
    }%>">
    <div style="font-size:150%; text-transform: capitalize;"> Build #${build.number} ${resultTrend.getDescription()}</div>    
    <% if (build.result.toString() == 'FAILURE' || build.result.toString() == 'ABORTED') { %><a href="#consoleOutput">Console output</a><% } %>
    <% if (build.result.toString() == 'SUCCESS') { %> ${successCount} test(s) succeeded (<a href="#all-tests">tests detail</a>) <% } %>
    <% if (build.result.toString() == 'UNSTABLE') { %>${failCount} test(s) failed (<a href="#failed-tests">failures detail</a>) <% } %>
</div>

<a name="summary"/>
<h2 style="${h2style}">Summary</h2>

<table >
  <tr><td>Build URL</td><td ><A href="${rooturl}${build.url}">${rooturl}${build.url}</A></td></tr>
  <tr><td>Project:</td><td >${project.name}</td></tr>
  <tr><td>Date of build:</td><td>${it.timestampString}</td></tr>
  <tr><td>Build duration:</td><td >${build.durationString}</td></tr>

  <tr>
    <td >Tests:</td>
    <td ><% if (totalCount == 0) {%> No Tests<% } else {%> ${totalCount} Executed, ${failCount} Failed<% } %>
    (<a href="#tests">tests details</a>)</td></tr>  

  <tr><td>Changes:</td><td>
    <%if (changesCount == 0) {%>No Changes<%} else { %> ${changesCount} change(s) by ${authors.size} author(s): ${authors} <a href="#changes">(changes details)</a><%}%></td></tr>  
</table>


<!-- CHANGE SET -->
<% 
if(hadChanges) {	%>
  <a name="changes"/>
  <h2 style="${h2style}">Changes</h2>	
<% 	changeSet.each() { cs -> %>		
      <div style="${divStyleBase} border-color: #AAAAAA;background-color: #EEEEEE">
        <b>Revision <%= cs.metaClass.hasProperty('commitId') ? cs.commitId : cs.metaClass.hasProperty('revision') ? cs.revision : 
        cs.metaClass.hasProperty('changeNumber') ? cs.changeNumber : "" %> by
           <%= cs.author %>: </b>
        <br>
          ${cs.msgAnnotated}
        <br><br>
        <b>Affected Files</b> 
        <table width="100%">
        <%    cs.affectedFiles.each() { p -> %>
        <tr>
          <td width="10%">&nbsp;&nbsp;${p.editType.name}</td>
          <td>${p.path}</td>
        </tr>
        <%  } %>
        </table>
	 </div>
<% }
} %>

<!-- Global Artifacts -->


<% def artifacts = build.artifacts
if(artifacts != null && artifacts.size() > 0) { %>
   <a name="artifacts"/>
  <h2 style="${h2style}">Artifacts</h2>
  <table width="100%">
    <tr>
      <td>
<% 		artifacts.each() { f -> %>		
      	    <a href="${rooturl}${build.url}artifact/${f}">${f}</a>      	  
<%		} %>
      </td>
    </tr>
  </table>
<% } %>

<!-- MAVEN ARTIFACTS -->
<% 
try {
  def mbuilds = build.moduleBuilds
  if(mbuilds != null && mbuild.size() > 0) { %>
     <a name="module-artifacts"/>
  <h2 style="${h2style}">Module Artifacts</h2>
  <table width="100%">
<% 
    try {  
        mbuild.each() { m -> %>	  
        <tr><td class="tableTitle"><B>${m.key.displayName}</B></td></tr>
<%		m.value.each() { mvnbld ->
			def artifactz = mvnbld.artifacts
			if(artifactz != null && artifactz.size() > 0) { %>			
      <tr>
        <td>
<%				artifactz.each() { f -> %>			
      	    <li>
      	      <a href="${rooturl}${mvnbld.url}artifact/${f}">${f}</a>
      	    </li>
<%				} %>		
      	</td>
      </tr>
<%			} 
		}
       }
    } catch(e) {
	// we don't do anything
    }  %>      
  </table>
<% } 

}catch(e) {
	// we don't do anything
}
%>

<!-- JUnit TEMPLATE -->
<% 
def junitResultList = it.JUnitTestResult

// Failed tests
if (failCount > 0) { %>
  <a name="failed-tests"/>
  <h2 style="${h2style}">Failed Tests</h2>
  <% it.JUnitTestResult.each{ junitResult ->
      junitResult.getChildren().each { packageResult -> 
        if (packageResult.getFailCount() >0) {
        %>
        <a name="failed-tests-${packageResult.getName()}"/>

        <% packageResult.getFailedTests().each{ failed_test -> %>
          <div style="${divStyleDanger}">
            <div style="font-size=150%;">${failed_test.getFullName()}</div>
          
          <% if (failed_test.getErrorDetails() != null) { %>
            <div>
              Message:
              <pre style="${consoleStyle}">
              ${org.apache.commons.lang.StringEscapeUtils.escapeHtml(failed_test.getErrorDetails())}
              </pre>
            </div>
          <% } %>
          <% if (failed_test.getErrorStackTrace() != null) { %>
            <div>
              StackTrace:
              <pre style="${consoleStyle}">
              ${org.apache.commons.lang.StringEscapeUtils.escapeHtml(failed_test.getErrorStackTrace())}
              </pre>
            </div>
          <% } %>
          </div>
        <% }
        }
      }
    }
} %>
</table>

<% 
// All tests
if (junitResultList.size() > 0) { %>
  <a name="all-tests"/>
  <h2 style="${h2style}">All Tests</h2>
  <table width="100%">
    <th>
      <td>Failed</td>
      <td>Passed</td>
      <td>Skipped</td>
      <td>Total</td>
    </th>
    <% it.JUnitTestResult.each{ junitResult ->
      junitResult.getChildren().each { packageResult -> %>
        <tr <%=(packageResult.getFailCount()>0)?"style=\"background-color: #F2DEDE;\"":""%>>
          <td >${packageResult.getName()}</td>
          <td>
          <% if (packageResult.getFailCount() > 0) {%>
            <a href="failed-tests-${packageResult.getName()}">${packageResult.getFailCount()}</a>
          <%} else {%>
            ${packageResult.getFailCount()}
          <%}%>
          </td>
          <td >${packageResult.getPassCount()}</td>
          <td>${packageResult.getSkipCount()}</td>
          <td>${packageResult.getPassCount()+packageResult.getFailCount()+packageResult.getSkipCount()}</td>
          </tr>
        <% 
      }
    }
} %>
  </table>


<!-- CONSOLE OUTPUT -->
<% if(build.result.toString() == 'FAILURE' || build.result.toString() == 'ABORTED') { %>
<a name="consoleOutput"/>
<h2 style="${h2style}">Console Output</h2>
<table width="100%" cellpadding="0" cellspacing="0">
<% 	build.getLog(800).each() { line -> %>
	<tr><td > <pre style="${consoleStyle}">
             ${org.apache.commons.lang.StringEscapeUtils.escapeHtml(line)}</pre></td></tr>
<% 	} %>
</table>
<% } %>

</body>

