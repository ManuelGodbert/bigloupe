
MongoDB
mongod --dbpath D:\Workspace\Workspace-KARMA\websolr\mongodb\data\db


http://www.gdal.org/
Geospatial Data Abstraction Library
Outil pour convertir different format de fichiers géographiques

http://www.ngdc.noaa.gov/mgg/shorelines/data/gshhs/version2.2.0/
Base de données géographique
5 niveaux de détail (c,f,h,i,j) - 4 zones géographiques

ogr2ogr -f "GeoJSON" myGeoFile.json myGeoFile-polygons-ms1.shp --debug on
ogr2ogr.exe -f "GeoJSON" myfile.json D:\PortableApps\maptool.org\GSHHS_shp\c\GSHHS_c_L1.shp
copy myfile.json 

*********************************************
Fonctions JSTL traitement CC
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

${fn:length(pageDividers)}

*********************************************
Ajax jQuery

    load(): Load a piece of html into a container DOM.
    $.getJSON(): Load a JSON with GET method.
    $.getScript(): Load a JavaScript.
    $.get(): Use this if you want to make a GET call and play extensively with the response.
    $.post(): Use this if you want to make a POST call and don’t want to load the response to some container DOM.
    $.ajax(): Use this if you need to do something when XHR fails, or you need to specify ajax options (e.g. cache: true) on the fly.

	
*********************************************
Date format with JSTL
	
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="balance" value="120000.2309" />
<p>Formatted Number (1): <fmt:formatNumber value="${balance}" 
            type="currency"/></p>
<p>Formatted Number (2): <fmt:formatNumber type="number" 
            maxIntegerDigits="3" value="${balance}" /></p>
<p>Formatted Number (3): <fmt:formatNumber type="number" 
            maxFractionDigits="3" value="${balance}" /></p>
<p>Formatted Number (4): <fmt:formatNumber type="number" 
            groupingUsed="false" value="${balance}" /></p>
<p>Formatted Number (5): <fmt:formatNumber type="percent" 
            maxIntegerDigits="3" value="${balance}" /></p>
<p>Formatted Number (6): <fmt:formatNumber type="percent" 
            minFractionDigits="10" value="${balance}" /></p>
<p>Formatted Number (7): <fmt:formatNumber type="percent" 
            maxIntegerDigits="3" value="${balance}" /></p>
<p>Formatted Number (8): <fmt:formatNumber type="number" 
            pattern="###.###E0" value="${balance}" /></p>
<p>Currency in USA :
<fmt:setLocale value="en_US"/>
<fmt:formatNumber value="${balance}" type="currency"/></p>

**********************************************
Date format with JODA-Time

<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>

Datatable and Bootstrap 
http://datatables.net/blog/Twitter_Bootstrap_2