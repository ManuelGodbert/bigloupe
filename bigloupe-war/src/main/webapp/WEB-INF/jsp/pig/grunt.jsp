<%@ page contentType="application/x-java-jnlp-file"%>
<?xml version="1.0" encoding="utf-8"?>
<jnlp spec="1.0+" codebase="${codebaseUrl}" href="${codebaseUrl}/grunt.jnlp">

<information>
    <title>Grunt</title>
    <vendor>BigLoupe</vendor>
    <description>Launcher Grunt</description>
    <homepage href="${codebaseUrl}"/>

	<shortcut online="true">
    	<desktop/>
    	<menu submenu="Apache Pig : Grunt console"/>
  	</shortcut>
	<icon href="icon.png" kind="default" />
	<icon href="splash.png" kind="splash" />

</information>

<security>
     <all-permissions/>
     <j2ee-application-client-permissions/>
</security>

<resources>
    <j2se version="1.6+" href="http://java.sun.com/products/autodl/j2se" />
    <jar main="true" href="${codebaseUrl}${request.contextPath}/pig/lib/pig-0.10.0-withouthadoop.jar" />

</resources>

<application-desc main-class="org.apache.pig.Main"/>

</jnlp>
