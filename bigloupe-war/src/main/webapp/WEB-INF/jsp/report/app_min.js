function selectTotalAmount(){$("#sumtype_total").click()};
function selectPerPerson(){$("#sumtype_perperson").click()};
function updatePlayBtn(){var a=intervalID==null?"play":"pause";
console.log("intervalID  "+a),$("#playBtn").removeClass(),$("#playBtn").addClass(a)}
function toogleAnim(){var a=intervalID==null?"STOP":"START";intervalID!=null?stopUpdate():startUpdate()}
function refresh(){d3.selectAll("#states path").attr("d",path)}
function selectFunction(a){_selectedFunctionID=a,update();
var b=_selectedFunctionID==null?"tutte le funzioni":"Funzione "+a}
function showVis(a){var b=d3.select("#main");b.style("visibility",a==!0?"visible":"hidden");
var b=d3.select("#loading");b.style("display",a==!1?"":"none")}
function loadGeo(a){max=populationByRegionByYear[a].max,min=populationByRegionByYear[a].min,d3.json("data/map/italy.json",function(a){_geoData=a;
var b=d3.select("#states").selectAll("path").data(_geoData.features).enter();
populateMap(b,currentYear),createBarCharts(),refresh(),update(),showVis(!0)})}
function loadPopulationByYear(a){var b="data/popolazione/dati_"+a+"/regioni.csv";
d3.csv(b,function(b){var c=d3.nest().key(function(a){
	return formatRegionId(a.Regione)}).rollup(function(a){
		return formatPopulationItemData(a)}).map(b),d=[],e=regIndex=[];
for(var f in c)d.push(c[f].value),e[c[f].ID]=c[f].label;var g=d3.max(d,function(a){return a}),h=d3.min(d,function(a){return a});populationByRegionByYear[a]==null&&(populationByRegionByYear[a]={}),populationByRegionByYear[a].max=g,populationByRegionByYear[a].min=h,populationByRegionByYear[a].data=c,populationByRegionByYear[a].IDs=e,loadedData++,checkDataLaoded()})}
function loadPopulation(){
	for(var a=0;a<availableYears.length;a++)
		loadPopulationByYear(availableYears[a])}
function start(){
	showVis(!1),$("#type").change(function(){
		changeCurrentMode($(this).val())}),currentMode=MODE_PER_PERSON,currentYear=availableYears[0],updatePlayBtn(),
		loadPopulation()}
function changeCurrentMode(a){currentMode=a,update()}
function update(){updateBarChart(),updateColors();if(_selectedRegion!=null||regSelected!=null){var a=regSelected;_selectedRegion!=null&&(a=_selectedRegion.ID),showRegionInfo(a)}else showCountryInfo();updateNavigationBar()}function changeYear(a){currentYear=a,update()}function loadBalance(){var a="data/bilancio/bilancio_italia_1996_2008.csv";d3.csv(a,function(a){balanceByYearAndByRegion=d3.nest().key(function(a){return a.date}).key(function(a){return formatRegionId(a.to_label)}).rollup(function(a){return formatBalanceItemData(a)}).map(a),balanceByYearByType=d3.nest().key(function(a){return a.date}).key(function(a){return a.function_id}).rollup(function(a){return formatBalanceItemData(a)}).map(a),balanceByYearByTypeByRegion=d3.nest().key(function(a){return a.date}).key(function(a){return formatRegionId(a.to_label)}).key(function(a){return a.function_id}).rollup(function(a){return formatBalanceItemData(a)}).map(a),calculateDataPerPerson(),loadGeo(currentYear)})}function elaborateData(){}function getAmountByYearByRegionPerPerson(a,b){return getAmountByYearByRegion(a,b)/getPopulationByYearByRegion(a,b)}function getAmountByYearByRegion(a,b){return balanceByYearAndByRegion[a][b].value}function getPopulationByYearByRegion(a,b){if(populationByRegionByYear[a].data[b]==null){console.log("ERRO"+b);return 1}return populationByRegionByYear[a].data[b].value}function formatBalanceItemData(a){var b={},c=formatRegionId(a[0].to_label);b.label=c,b.functionlabel=a[0].functionlabel,b.functionID=a[0].function_id,b.ID=mapIDs[c],b.value=getTotalAmount(a);return b}function getTotalAmount(a){var b=0;a.forEach(function(a){b+=parseFloat(a.amount)});return b}function checkDataLaoded(){_dataLoaded=loadedData==maxTotalData?!0:!1,_dataLoaded&&loadBalance()}function formatPopulationItemData(a){var b={},c=formatRegionId(a[0].Regione);b.label=c,b.ID=mapIDs[c],b.value=getTotal(a);return b}function formatRegionId(a){var b=a,c=b.toString().toLowerCase();b=c.replace("'",""),b=b.replace("/",""),b=b.replace(/\s+/g,""),b=b.replace("friuliveneziagiulia","friulivenezia"),b=b.replace("provinciaauton","trentinoaltoadige"),b=b.replace("valledaostavalleedaoste","valledaosta");return b}function formatValueShort(a){var b=1e3,c=1e6,d=1e9,e=" \u20ac",f="0";if(a>d)e="<br/> MLD \u20ac",a=a/d,a=Math.round(100*a)/100;else if(a>c)e="<br/> MLN \u20ac",a=a/c;else{var g=a.toString().substring(0,h);a>b?e=" \u20ac":a=Math.round(100*a)/100}var g=a.toString(),h=a.toString().indexOf(".");h>-1&&(g=a.toString().substring(0,h)),f=g.substring(0,g.length-3)+"."+g.substring(g.length-3,g.length),a<b&&(f=g+","+a.toString().substring(h+1,h+3));return f+e}function formatRegionName(a){var b=a;switch(b){case"friulivenezia":b="friuli V.G.";break;case"trentinoaltoadige":b="trentino A.A.";break;case"valledaosta":b="valle d'aosta";break;case"emiliaromagna":b="emilia romagna"}return capitalizeString(b)}function getTotal(a){var b=0;a.forEach(function(a){b+=parseInt(a["Totale Maschi"])+parseInt(a["Totale Femmine"])});return b}
function createBarCharts(){updateBarChart()}function updateBarChart(){var a=regIndex[regSelected],b=regSelected==null?balanceByYearByType[currentYear]:balanceByYearByTypeByRegion[currentYear][a],c=d3.entries(b),d=d3.max(c,function(a){return a.value.value}),e=d3.min(c,function(a){return a.value.value}),f=[];f.data=[],f.byYear=[];var g=[];g.data=[],g.byYear=[];var h=d3.entries(regSelected==null?balanceByYearByType:balanceByYearByTypeByRegion);h.forEach(function(b){if(b.key>=availableYears[0]&&b.key<=availableYears[availableYears.length-1]){var c=d3.entries(regSelected==null?b.value:b.value[a]),d=d3.max(c,function(a){return a.value.value});f.data.push(d),f.byYear[b.key]=f.data.length-1;var e=d3.min(c,function(a){return a.value.value});g.data.push(e),g.byYear[b.key]=g.data.length-1}});var i=d3.max(f.data,function(a){return a}),j=d3.min(g.data,function(a){return a}),k=_useAbsoluteValues?i:d,l=_useAbsoluteValues?j:e,m=c.sort(function(a,b){if(a.value.functionlabel==null||b.value.functionlabel==null)return 0;var c=a.value.functionlabel.toLowerCase(),d=b.value.functionlabel.toLowerCase();if(c<d)return-1;if(c>d)return 1;return 0}),n=d3.selectAll("#functionsKey").selectAll("div.functionalArea").data(m),o=n.enter().append("div");o.attr("id",function(a,b){return"barchart_"+a.value.functionID+"_text"}).attr("class","functionalArea").on("mouseover",function(a,b){return mouseOverBarChart(a,b,!0)}).on("mouseout",function(a,b){return mouseOutBarChart(a,b,!0)}).on("click",function(a,b){return clickBarChart(a,b)}),o.append("div").attr("id",function(a,b){return"funcchart_"+a.value.functionID}).attr("class","bar"),o.append("div").attr("class","label"),d3.selectAll("#functionsKey div.label").data(m).style("font-weight",function(a){return a.value.functionID==_selectedFunctionID?"bold":"normal"}).style("color",function(a){return a.value.functionID==_selectedFunctionID?"#000000":"#999999"}).text(function(a){return a.value.functionlabel!=null?a.value.functionlabel:""}),d3.selectAll("#functionsKey div.bar").data(m).style("background-color",function(a){return a.value.functionID==_selectedFunctionID?"black":"#cccccc"}).transition(300).call(function(a){var b=function(a){return Math.max(1,(a.value.value-l)/k*100)};a.style("width",function(a){return b(a)+"px"}),a.style("left",function(a){return-10-b(a)+"px"})}),n.exit().remove()}function hideRegionInfo(){d3.select("#infobox").style("display","none")}function calculateAverage(){var a=0,b=0,c=populationByRegionByYear[currentYear].data,d=curRegDataset,e=_selectedFunctionID;a=getTotalCountryAmount();var f=0;d3.entries(c).forEach(function(a){b+=a.value.value||0,f++}),currentMode==MODE_TOTAL&&(b=f);return a/b}function getTotalCountryAmount(){var a=0,b=_selectedFunctionID;d3.entries(curRegDataset).forEach(function(c){b?a+=c.value[b].value||0:a+=c.value.value||0});return a}function updateInfoDesc(){d3.select("#infoDesc").text(currentMode==MODE_PER_PERSON?"budget pro capite":"budget totale")}function showCountryInfo(){if(_selectedRegion==null){d3.select("#infoTitle").text("Italia");var a=formatValueShort(currentMode==MODE_PER_PERSON?calculateAverage():getTotalCountryAmount());d3.select("#infoValue").style("font-size",a.length>8?70:40).html(a),d3.select("#infoAvgBox").style("visibility","hidden"),d3.select("#infobox").style("background-color","#f0f0f0"),updateInfoDesc()}}function showRegionInfo(a){var b=curRegDataset[regIndex[a]];if(b!=null){var c=_selectedFunctionID==null?b:b[_selectedFunctionID],d=getMaxAndMinValues(!1),e=d.curMax,f=d.curMin,g=d.prop,h=curRegDataset,i=calculateAverage(),j=d3.scale.linear().domain([f,i,e]).range([0,50,100]),k=d3.scale.linear().domain([0,i]).rangeRound([-100,0]);d3.select("#infoTitle").text(formatRegionName(regIndex[a]));var l=formatValueShort(c[g]);d3.select("#infoValue").html(l),d3.select("#infoAvgBox").style("visibility","visible"),d3.select("#infoBar").transition(500).style("width",190*j(c[g])/100+"px"),l=k(c[g]),d3.select("#infoAvgText").text("su media nazionale: "+(l>0?"+"+l:l)+" % "),updateInfoDesc()}d3.select("#infobox").style("background-color",regOverColor).style("display","")}function updateNavigationBar(){var a=availableYears.length,b=d3.entries(availableYears),c=d3.select("#yearnavbar");c.text("");
var d=0;b.forEach(
		function(b){
			if(currentYear==b.value)c.append("span").attr("class","yearselect").style("float","left").text(b.value);
			else{var e="yearsel_"+b.value;c.append("div").attr("id",e).style("float","left").append("a").attr("href","javascript:changeYear("+b.value+")").text(b.value).on("mouseover",function(){return changeYear(this.innerHTML)})}++d!=a&&c.append("span").style("float","left").style("margin-left","5px").style("margin-right","5px").text(" | ")})}function mouseOutBarChart(a,b){_clickedFunction==null&&selectFunction(null)}function clickBarChart(a,b){_clickedFunction==a.value.functionID?_clickedFunction=null:_clickedFunction=a.value.functionID,selectFunction(_clickedFunction)}function mouseOverBarChart(a,b,c){_clickedFunction==null&&selectFunction(a.value.functionID)}function updateColors(){var a=getMaxAndMinValues(),b=a.curMax,c=a.curMin,d=a.prop,e=curRegDataset;getRegionColor=function(a,b,c,f,g){var h=parseInt(a.properties.ID)-1,i=populationByRegionByYear[currentYear].IDs[h],j=e;if(j[i]!=null){_selectedFunctionID!=null&&j[i][_selectedFunctionID]==null&&(j[i][_selectedFunctionID]=[],j[i][_selectedFunctionID][d]=0,c=0);var k=_selectedFunctionID==null?j[i][d]:j[i][_selectedFunctionID][d],l=k-c}if(_selectedRegion!=null&&g==!1&&_selectedRegion.ID==h)return"#000000";if(_overRegion!=null&&_overRegion.ID==h&&g==!1)return"#FF0000";return interp(l/(f-c))};var f=d3.selectAll("#states path");if(_overRegion!=null||prevItemRegione||_selectedRegion){var g=_selectedRegion==null?prevItemRegione==null?_overRegion.ID:prevItemRegione:_selectedRegion.ID;f=d3.select("#states path.regione_"+g)}_overRegion!=null&&_selectedRegion==null&&(regOverColor=getRegionColor(_overRegion.data,_overRegion.ID,c,b,!0)),_selectedRegion!=null&&(regOverColor=getRegionColor(_selectedRegion.data,_selectedRegion.ID,c,b,!0)),f.transition(500).style("fill",function(a,d){return getRegionColor(a,d,c,b,!1)}),prevItemRegione!=null?prevItemRegione=null:prevItemRegione=g}function getMaxAndMinValues(a,b){var c=curRegDataset=_selectedFunctionID==null?balanceByYearAndByRegion[currentYear]:balanceByYearByTypeByRegion[currentYear],d=d3.entries(c),e=b==null?currentMode:b,f=e==MODE_PER_PERSON?"valuePerPerson":"value",g=function(a){_selectedFunctionID!=null&&a.value[_selectedFunctionID]==null&&(a.value[_selectedFunctionID]=[],a.value[_selectedFunctionID][f]=0);return _selectedFunctionID==null?a.value[f]:a.value[_selectedFunctionID][f]},h=_selectedFunctionID==null?balanceByYearAndByRegion:balanceByYearByTypeByRegion,i=d3.entries(h),j=[];j.data=[],j.byYear=[];var k=[];k.data=[],k.byYear=[];var l=0,m=0;i.forEach(function(a){if(a.key>=availableYears[0]&&a.key<=availableYears[availableYears.length-1]){var b=d3.entries(a.value),c=d3.max(b,function(a){return g(a)});j.data.push(c),j.byYear[a.key]=j.data.length-1;var d=d3.min(b,function(a){return g(a)});k.data.push(d),k.byYear[a.key]=k.data.length-1}}),l=d3.max(j.data,function(a){return a}),m=d3.max(k.data,function(a){return a});var n=d3.max(d,function(a){return g(a)}),o=d3.min(d,function(a){return g(a)}),p=a==null?_useAbsoluteValues:a,q=p?l:n,r=p?m:o;return{curMax:q,curMin:r,prop:f}}function calculateDataPerPerson(){var a=d3.entries(balanceByYearAndByRegion);a.forEach(function(a,b){if(a.key>=availableYears[0]&&a.key<=availableYears[availableYears.length-1]){var c=d3.entries(a.value);c.forEach(function(b){var c=a.key,e=b.value.label,f=b.value.value;balanceByYearAndByRegion[c][e].valuePerPerson=f/getPopulationByYearByRegion(c,e)})}});var a=d3.entries(balanceByYearByTypeByRegion);a.forEach(function(a,b){var c=a.key;if(a.key>=availableYears[0]&&a.key<=availableYears[availableYears.length-1]){var d=d3.entries(a.value);d.forEach(function(a){var b=a.key,d=d3.entries(a.value),e="";d.forEach(function(a){var d=a.key,e=a.value.value;balanceByYearByTypeByRegion[c][b][d].valuePerPerson=e/getPopulationByYearByRegion(c,b)})})}})}var kk=11,xy=d3.geo.mercator().translate([-18*kk,kk*152]).scale(kk*1e3);path=d3.geo.path().projection(xy);var svg=d3.select("#vis #map").append("svg:svg").attr("id","svg"),states=svg.append("svg:g").attr("id","states"),mapIDs=[];mapIDs.sardegna=19,mapIDs.sicilia=18,mapIDs.calabria=17,mapIDs.basilicata=16,mapIDs.puglia=15,mapIDs.campania=14,mapIDs.molise=13,mapIDs.abruzzo=12,mapIDs.lazio=11,mapIDs.marche=10,mapIDs.umbria=9,mapIDs.toscana=8,mapIDs.emiliaromagna=7,mapIDs.liguria=6,mapIDs.friulivenezia=5,mapIDs.veneto=4,mapIDs.trentinoaltoadige=3,mapIDs.lombardia=2,mapIDs.valledaosta=1,mapIDs.piemonte=0;var regIndex=[],curRegDataset,_useAbsoluteValues=!0,_selectedFunctionID=null,interp=d3.interpolateRgb(d3.rgb(255,207,134),d3.rgb(43,140,190)),availableYears=[2002,2003,2004,2005,2006,2007,2008],_dataLoaded=!1,loadedData=0,maxTotalData=availableYears.length,regSelected,prevOver=null,balanceByYearAndByRegion=[],balanceByYearAndByRegion_values=[],balanceByYearByType=[],balanceByYearByTypeByRegion=[],_geoData={},MODE_PER_PERSON="PER PERSON",MODE_TOTAL="TOTAL",populationByRegionByYear={},intervalID=null,counter=0;updateYear=function(){counter>=availableYears.length-1&&stopUpdate(),changeYear(availableYears[counter]),counter++},startUpdate=function(){stopUpdate(),counter=0,intervalID=setInterval(updateYear,1e3),updatePlayBtn()},stopUpdate=function(){clearInterval(intervalID),intervalID=null,updatePlayBtn()};var prevItemRegione,timeId=null,_clickedFunction=null,_prevSelectedFunction=null,regOverColor=null,overFunction=function(a,b){_selectedRegion==null&&(regSelected=b,_overRegion={},_overRegion.data=a,_overRegion.ID=b,update())},_selectedRegion=null,_overRegion=null,clickFunction=function(a,b){_overRegion=null,_selectedRegion!=null&&_selectedRegion.ID==b?(_selectedRegion=null,outFunction(),update()):(_selectedRegion=null,outFunction(),_selectedRegion={},_selectedRegion.ID=b,_selectedRegion.data=a,regSelected=_selectedRegion.ID,update())},outFunction=function(a,b){_selectedRegion==null&&(regSelected=null,_overRegion=null,update())},populateMap=function(a,b){a.append("svg:path").attr("d",path).attr("class",function(a,b){return"regione_"+b}).style("stroke",function(a,b){return"#000000"}).style("stroke-width",function(a,b){return 0}).on("mouseover",function(a,b){overFunction(a,b)}).on("mouseout",function(a,b){outFunction(a,b)}).on("click",function(a,b){clickFunction(a,b)});return a},capitalizeString=function(a){return a.replace(/(^|\s)([a-z])/g,function(a,b,c){return b+c.toUpperCase()})};$("#playBtn").click(function(){toogleAnim()}),$("#sumtype_perperson").click(function(){changeCurrentMode(MODE_PER_PERSON)}),$("#sumtype_total").click(function(){changeCurrentMode(MODE_TOTAL)}),start()