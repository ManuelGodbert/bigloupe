package org.bigloupe.web.controller;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.net.InetAddress;
import java.net.URL;

import org.apache.log4j.Logger;
import org.bigloupe.web.BigLoupeConfiguration;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractElasticSearchController extends
		AbstractConfigurationController implements InitializingBean {

	private static Logger logger = Logger
			.getLogger(AbstractElasticSearchController.class);

	private static boolean elasticSearchLocal;

	private static Node node;
	private static Client client;

	public Node getNode() {
		return node;
	}

	public Client getClient() {
		if (!elasticSearchLocal && ((node != null) && node.isClosed())) {
			node = nodeBuilder().client(true).node();
			client = node.client();
		}
		return client;
	}

	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// Test if elasticSearch is installed locally or remotely
		String elasticSearchNodeUrl = BigLoupeConfiguration
				.getConfigurationElasticSearchNodeUrl();

		try {
			// Unique connection
			if ((client == null) || ((node != null && node.isClosed()))) {

				if (elasticSearchNodeUrl.startsWith("http://")) {
					URL elasticSearchServerNodeUrl = new URL(
							elasticSearchNodeUrl);

					// Local elasticsearch
//					if (InetAddress.getLocalHost() == InetAddress
//							.getByName(elasticSearchServerNodeUrl.getHost())
//							|| elasticSearchServerNodeUrl.getHost().equals(
//									"localhost")) {
//
//						logger.info("BigLoupe & ElasticSearch run on the same server");
//
//						// Bigloupe is not a node of elasticsearch
//						node = nodeBuilder().client(true).node();
//
//						// Bigloupe is a node of elasticsearch
//						//node = nodeBuilder().client(false).node();
//						client = node.client();
//
//						testClusterHealth(client);
//						
//						setElasticSearchLocal(false);
//
//					} else {
						logger.info("BigLoupe & ElasticSearch run on different servers");
						// Remote elasticsearch
						ImmutableSettings.Builder settings = ImmutableSettings
								.settingsBuilder();
						// ignore name of cluster
						settings.put("client.transport.ignore_cluster_name",
								"true");
						settings.put("client.transport.sniff", true);
						client = new TransportClient(settings)
								.addTransportAddress(new InetSocketTransportAddress(
										elasticSearchServerNodeUrl.getHost(),
										elasticSearchServerNodeUrl.getPort()));

						testClusterHealth(client);

						setElasticSearchLocal(true);
//					}

				}
			}
		} catch (Exception e) {
			logger.warn("BigLoupe can't access to " + elasticSearchNodeUrl);
			if (client != null) {
				try {
					client.close();
				} catch (Exception stopException) {
				}
			}
			client = null;
			//e.printStackTrace();
		} finally {

			if (client == null) {
				logger.error("Properties 'elasticsearch.node.url' has been not well defined in bigloupe.properties or elasticsearch is not available at '"
						+ elasticSearchNodeUrl + "'");
				logger.info("Start an elasticsearch node inside BigLoupe (Warning : Not to be used in production)");

				// Start elasticsearch in BigLoupe - only for webhosting
				ImmutableSettings.Builder settings = ImmutableSettings
						.settingsBuilder();
				// ignore name of cluster
				settings.put("node.name", "BigLoupe");
				settings.put("node.master", true);
				settings.put("node.data", true);
				settings.put("index.number_of_shards", 1);
				settings.put("index.number_of_replicas", 0);
				settings.put("http.enabled", false);
				settings.put("discovery.zen.ping.multicast.enabled", false);

				// Start a local elasticsearch
				NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder()
						.settings(settings).loadConfigSettings(false);
				node = nodeBuilder.build();
				client = node.client();

				setElasticSearchLocal(true);
				configuration.setConfigurationElasticSearchNodeUrl("embedded");
			}
		}

	}

	/**
	 * Test availability of elasticsearch
	 * 
	 * @param client
	 */
	private void testClusterHealth(Client client) {
		ClusterHealthRequest request = new ClusterHealthRequest();
		ClusterHealthResponse response = client.admin().cluster()
				.health(request).actionGet();
		ClusterHealthStatus status = response.getStatus();
		logger.info(status.toString());

	}

	public boolean isElasticSearchLocal() {
		return elasticSearchLocal;
	}

	public void setElasticSearchLocal(boolean elasticSearchLocal) {
		this.elasticSearchLocal = elasticSearchLocal;
	}
}
