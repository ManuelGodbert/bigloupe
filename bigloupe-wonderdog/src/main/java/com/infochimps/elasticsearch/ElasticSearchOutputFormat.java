package com.infochimps.elasticsearch;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.infochimps.elasticsearch.hadoop.util.HadoopUtils;

/**
 * Hadoop OutputFormat for writing arbitrary MapWritables (essentially HashMaps)
 * into Elasticsearch. Records are batched up and sent in a one-hop manner to
 * the elastic search data nodes that will index them.
 */
public class ElasticSearchOutputFormat extends
		OutputFormat<NullWritable, MapWritable> implements Configurable {

	static Log LOG = LogFactory.getLog(ElasticSearchOutputFormat.class);
	private Configuration conf = null;

	private boolean elasticSearchLocal;

	public ElasticSearchOutputFormat(boolean elasticSearchLocal) {
		super();
		this.elasticSearchLocal = elasticSearchLocal;
	}

	protected class ElasticSearchRecordWriter extends
			RecordWriter<NullWritable, MapWritable> {

		private Node node;
		private Client client;
		private String indexName;
		private int bulkSize;
		private int idField;
		private String idFieldName;
		private String objType;
		private String[] fieldNames;

		// Used for bookkeeping purposes
		private AtomicLong totalBulkTime = new AtomicLong();
		private AtomicLong totalBulkItems = new AtomicLong();
		private Random randgen = new Random();
		private long runStartTime = System.currentTimeMillis();

		// For hadoop configuration
		private static final String ES_CONFIG_NAME = "elasticsearch.yml";
		private static final String ES_PLUGINS_NAME = "plugins";
		private static final String ES_INDEX_NAME = "elasticsearch.index.name";
		private static final String ES_BULK_SIZE = "elasticsearch.bulk.size";
		private static final String ES_ID_FIELD_NAME = "elasticsearch.id.field.name";
		private static final String ES_ID_FIELD = "elasticsearch.id.field";
		private static final String ES_OBJECT_TYPE = "elasticsearch.object.type";
		private static final String ES_CONFIG = "es.config";
		private static final String ES_PLUGINS = "es.path.plugins";

		// Other string constants
		private static final String COMMA = ",";
		private static final String SLASH = "/";
		private static final String NO_ID_FIELD = "-1";

		private volatile org.elasticsearch.action.bulk.BulkRequestBuilder currentRequest;

		/**
		 * Instantiates a new RecordWriter for Elasticsearch
		 * <p>
		 * The properties that <b>MUST</b> be set in the hadoop Configuration
		 * object are as follows:
		 * <ul>
		 * <li><b>elasticsearch.index.name</b> - The name of the elasticsearch
		 * index data will be written to. It does not have to exist ahead of
		 * time</li>
		 * <li><b>elasticsearch.bulk.size</b> - The number of records to be
		 * accumulated into a bulk request before writing to elasticsearch.</li>
		 * <li><b>elasticsearch.is_json</b> - A boolean indicating whether the
		 * records to be indexed are json records. If false the records are
		 * assumed to be tsv, in which case <b>elasticsearch.field.names</b>
		 * must be set and contain a comma separated list of field names</li>
		 * <li><b>elasticsearch.object.type</b> - The type of objects being
		 * indexed</li>
		 * <li><b>elasticsearch.config</b> - The full path the
		 * elasticsearch.yml. It is a local path and must exist on all machines
		 * in the hadoop cluster.</li>
		 * <li><b>elasticsearch.plugins.dir</b> - The full path the
		 * elasticsearch plugins directory. It is a local path and must exist on
		 * all machines in the hadoop cluster.</li>
		 * </ul>
		 * <p>
		 * The following fields depend on whether <b>elasticsearch.is_json</b>
		 * is true or false.
		 * <ul>
		 * <li><b>elasticsearch.id.field.name</b> - When
		 * <b>elasticsearch.is_json</b> is true, this is the name of a field in
		 * the json document that contains the document's id. If -1 is used then
		 * the document is assumed to have no id and one is assigned to it by
		 * elasticsearch.</li>
		 * <li><b>elasticsearch.field.names</b> - When
		 * <b>elasticsearch.is_json</b> is false, this is a comma separated list
		 * of field names.</li>
		 * <li><b>elasticsearch.id.field</b> - When <b>elasticsearch.is_json</b>
		 * is false, this is the numeric index of the field to use as the
		 * document id. If -1 is used the document is assumed to have no id and
		 * one is assigned to it by elasticsearch.</li>
		 * </ul>
		 */
		public ElasticSearchRecordWriter(TaskAttemptContext context) {
			Configuration conf = context.getConfiguration();
			this.indexName = conf.get(ES_INDEX_NAME);
			this.bulkSize = Integer.parseInt(conf.get(ES_BULK_SIZE));
			this.idFieldName = conf.get(ES_ID_FIELD_NAME);
			if (idFieldName.equals(NO_ID_FIELD)) {
				LOG.info("Documents will be assigned ids by elasticsearch");
				this.idField = -1;
			} else {
				LOG.info("Using field:[" + idFieldName + "] for document ids");
			}
			this.objType = conf.get(ES_OBJECT_TYPE);

			//
			// Fetches elasticsearch.yml and the plugins directory from the
			// distributed cache, or
			// from the local config.
			//
			try {
				String taskConfigPath = HadoopUtils.fetchFileFromCache(
						ES_CONFIG_NAME, conf);
				LOG.info("Using [" + taskConfigPath + "] as es.config");
				String taskPluginsPath = HadoopUtils.fetchArchiveFromCache(
						ES_PLUGINS_NAME, conf);
				LOG.info("Using [" + taskPluginsPath + "] as es.plugins.dir");
				// System.setProperty(ES_CONFIG, taskConfigPath);
				// System.setProperty(ES_PLUGINS,
				// taskPluginsPath+SLASH+ES_PLUGINS_NAME);
			} catch (Exception e) {
				// System.setProperty(ES_CONFIG,conf.get(ES_CONFIG));
				// System.setProperty(ES_PLUGINS,conf.get(ES_PLUGINS));
			}

			if (elasticSearchLocal)
				start_embedded_client();
			else
				start_transport_client();
			initialize_index(indexName);
			currentRequest = client.prepareBulk();
		}

		/**
		 * Closes the connection to elasticsearch. Any documents remaining in
		 * the bulkRequest object are indexed.
		 */
		public void close(TaskAttemptContext context) throws IOException {
			if (currentRequest.numberOfActions() > 0) {
				try {
					BulkResponse response = currentRequest.execute()
							.actionGet();
				} catch (Exception e) {
					LOG.warn("Bulk request failed: " + e.getMessage());
					throw new RuntimeException(e);
				}
			}
			LOG.info("Closing record writer");
			client.close();
			LOG.info("Client is closed");
			if (node != null) {
				node.close();
			}
			LOG.info("Record writer closed.");
		}

		/**
		 * Writes a single MapWritable record to the bulkRequest object. Once
		 * <b>elasticsearch.bulk.size</b> are accumulated the records are
		 * written to elasticsearch.
		 */
		public void write(NullWritable key, MapWritable fields)
				throws IOException {
			XContentBuilder builder = XContentFactory.jsonBuilder();
			buildContent(builder, fields);
			if (idField == -1) {
				// Document has no inherent id
				currentRequest.add(Requests.indexRequest(indexName)
						.type(objType).source(builder));
			} else {
				try {
					Text mapKey = new Text(idFieldName);
					String record_id = fields.get(mapKey).toString();
					currentRequest.add(Requests.indexRequest(indexName)
							.id(record_id).type(objType).create(false)
							.source(builder));
				} catch (Exception e) {
					LOG.warn("Encountered malformed record");
				}
			}
			processBulkIfNeeded();
		}

		/**
		 * Recursively untangles the MapWritable and writes the fields into
		 * elasticsearch's XContentBuilder builder.
		 */
		private void buildContent(XContentBuilder builder, Writable value)
				throws IOException {
			if (value instanceof Text) {
				builder.value(((Text) value).toString());
			} else if (value instanceof LongWritable) {
				builder.value(((LongWritable) value).get());
			} else if (value instanceof IntWritable) {
				builder.value(((IntWritable) value).get());
			} else if (value instanceof DoubleWritable) {
				builder.value(((DoubleWritable) value).get());
			} else if (value instanceof FloatWritable) {
				builder.value(((FloatWritable) value).get());
			} else if (value instanceof BooleanWritable) {
				builder.value(((BooleanWritable) value).get());
			} else if (value instanceof MapWritable) {
				builder.startObject();
				for (Map.Entry<Writable, Writable> entry : ((MapWritable) value)
						.entrySet()) {
					if (!(entry.getValue() instanceof NullWritable)) {
						builder.field(entry.getKey().toString());
						buildContent(builder, entry.getValue());
					}
				}
				builder.endObject();
			} else if (value instanceof ArrayWritable) {
				builder.startArray();
				Writable[] arrayOfThings = ((ArrayWritable) value).get();
				for (int i = 0; i < arrayOfThings.length; i++) {
					buildContent(builder, arrayOfThings[i]);
				}
				builder.endArray();
			}
		}

		/**
		 * Indexes content to elasticsearch when <b>elasticsearch.bulk.size</b>
		 * records have been accumulated.
		 */
		private void processBulkIfNeeded() {
			totalBulkItems.incrementAndGet();
			if (currentRequest.numberOfActions() >= bulkSize) {
				try {
					long startTime = System.currentTimeMillis();
					BulkResponse response = currentRequest.execute()
							.actionGet();
					totalBulkTime.addAndGet(System.currentTimeMillis()
							- startTime);
					if (randgen.nextDouble() < 0.1) {
						LOG.info("Indexed ["
								+ totalBulkItems.get()
								+ "] in ["
								+ (totalBulkTime.get() / 1000)
								+ "s] of indexing"
								+ "["
								+ ((System.currentTimeMillis() - runStartTime) / 1000)
								+ "s] of wall clock" + " for ["
								+ (float) (1000.0 * totalBulkItems.get())
								/ (System.currentTimeMillis() - runStartTime)
								+ "rec/s]");
					}
				} catch (Exception e) {
					LOG.warn("Bulk request failed: " + e.getMessage());
					throw new RuntimeException(e);
				}
				currentRequest = client.prepareBulk();
			}
		}

		private void initialize_index(String indexName) {
			LOG.info("Initializing index '" + indexName + "'");
			try {
				if (indexName.equals("")) {
					throw new RuntimeException("ElasticSearch can't support an index name empty");
				}
				CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(indexName).execute()
						.actionGet();
				LOG.info("Index [" + indexName + "] acknowledge by all cluster :" + createIndexResponse.acknowledged());
			} catch (Exception e) {
				e.printStackTrace();
				if (ExceptionsHelper.unwrapCause(e) instanceof IndexAlreadyExistsException) {
					LOG.warn("Index [" + indexName + "] already exists");
				}
			}
		}

		//
		// Starts an embedded elasticsearch client (ie. data = false)
		//
		private void start_embedded_client() {
			LOG.info("Starting embedded elasticsearch client ...");

			Properties properties = new Properties();
			properties.put("node.client", "true");
			properties.put("node.data", "false");
			properties.put("node.master", "false");
			properties.put("client.transport.ignore_cluster_name", "true");
			properties.put("network.publish_host", "qvirkar1.france.airfrance.fr");
			properties.put("transport.tcp.port", "9300");
			properties.put("discovery.zen.ping.multicast.enabled" , "false");
			properties.put("discovery.zen.ping.unicast.hosts", "qvirkar1.france.airfrance.fr");
			Builder builder = ImmutableSettings.settingsBuilder().put(properties);

			this.node = NodeBuilder.nodeBuilder().settings(builder).node();

			// this.node = NodeBuilder.nodeBuilder().client(true).node();
			this.client = node.client();
		}

		private void start_transport_client() {
			LOG.info("Starting elasticsearch transport client ...");
	        ImmutableSettings.Builder settings =
	        		ImmutableSettings.settingsBuilder();
	        				settings.put("client.transport.ignore_cluster_name", "true");
	        		        settings.put("client.transport.sniff", true); 
			this.client = new TransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							"qvirkar1.france.airfrance.fr", 9300));
			

			 NodesInfoResponse nodesInfoResponse = this.client.admin().cluster().prepareNodesInfo().execute().actionGet();
			
			LOG.info("Connect to elasticSearch (cluster.name = " + nodesInfoResponse.clusterName() + ")");
		}
	}

	public RecordWriter<NullWritable, MapWritable> getRecordWriter(
			final TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new ElasticSearchRecordWriter(context);
	}

	public void setConf(Configuration conf) {
	}

	public Configuration getConf() {
		return conf;
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException,
			InterruptedException {
		// TODO Check if the object exists?
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new ElasticSearchOutputCommitter();
	}
}
