<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
</br>
<div class="row-fluid">
	<div class="hero-unit">
		<ul class="thumbnails">
			<li class="span4">
				<figure>
					<a href="${request.contextPath}/hdfsBrowser.html"><img src="${request.contextPath}/resources/img/hdfs-browser.png" alt="HDFS Browser" class="img-rounded"/></a>
					<figcaption>
						<p><center><span class="label label-important">Step 1</span>&nbsp;Select your cluster and your data in HDFS Browser</center></p>
					</figcaption>
				</figure>
			</li>
			<li class="span4">
				<figure>
					<img src="${request.contextPath}/resources/img/index-file.png" alt="Index your file" class="img-rounded"/>
					<figcaption>
						<p><center><span class="label label-important">Step 2</span>&nbsp;Index your files in <a href="http://www.elasticsearch.org/">ElasticSearch</a></center></p>
					</figcaption>
				</figure>
			</li>
			<li class="span4">
				<figure>
					<a href="${request.contextPath}/search/index.html"><img src="${request.contextPath}/resources/img/search-file.png" alt="Search in your index"  class="img-rounded"/></a>
					<figcaption>
						<p><center><span class="label label-important">Step 3</span>&nbsp;Search and define facets</center></p>
					</figcaption>
				</figure>
			</li>
		</ul>
	</div>
</div>