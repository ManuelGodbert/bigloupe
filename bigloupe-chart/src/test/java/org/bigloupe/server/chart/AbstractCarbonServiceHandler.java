package org.bigloupe.server.chart;

import java.net.SocketAddress;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.service.impl.DatabaseChartServiceImpl;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Msg examples
 * 
 * @author bigloupe
 *
 */
public abstract class AbstractCarbonServiceHandler  {
	
	@Autowired
	protected DatabaseChartServiceImpl chartService;

	protected CarbonGraphiteServiceHandler carbonGraphiteServiceHandler;
	
	protected String[] msgs = new String[] {
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.Threads 37 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.CapacityTotal 22720111927296 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.SystemLoadAverage 2.08 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.AddBlockOps 0 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.CapacityUsed 16028289978368 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.Used 16028289978368 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.CapacityRemaining 5460623380480 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.Total 22720111927296 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.AvailableProcessors 16 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.fsImageLoadTime 11006 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.TotalLoad 14 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.Free 5460623380480 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.FilesRenamed 0 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.TotalPhysicalMemorySize 33601904640 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.FreePhysicalMemorySize 11989331968 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.SyncsNumOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.NonDfsUsedSpace 1231198568448 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.BlocksTotal 946348 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.PercentUsed 70.5467 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.TotalSwapSpaceSize 37681618944 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.FilesTotal 1135360 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.SyncsAvgTime 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.SyncsMinTime 0 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.PendingReplicationBlocks 0 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.FreeSwapSpaceSize 37675958272 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.PercentRemaining 24.034317 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.OpenFileDescriptorCount 94 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.UnderReplicatedBlocks 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.TotalBlocks 946348 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.TotalFiles 1135360 1362080724",
			"servers.namenode_8005.com_sun_management_UnixOperatingSystem.MaxFileDescriptorCount 8192 1362080724",
			"servers.namenode_8005.NameNode.FSNamesystemState.ScheduledReplicationBlocks 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.SyncsMaxTime 79 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.JournalTransactionsBatchedInSync 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.FileInfoOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.CreateFileOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.GetListingOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.TransactionsNumOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.TransactionsAvgTime 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.TransactionsMinTime 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.TransactionsMaxTime 27 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.GetBlockLocations 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.BlocksCorrupted 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.FilesInGetListingOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.SafemodeTime 77315 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.FilesCreated 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.FilesAppended 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.DeleteFileOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.blockReportNumOps 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.blockReportAvgTime 0 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.blockReportMinTime 68 1362080724",
			"servers.namenode_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.blockReportMaxTime 1340 1362080724" };

	@Before
	public void setUp() throws Exception {
		carbonGraphiteServiceHandler = new CarbonGraphiteServiceHandler(
				chartService);
	}
	
	public void cleanDatabase() throws Exception {
		for (String msg : msgs) {
			String[] msgSplitted = StringUtils.split(msg);
			Series series = chartService.getTimeSeriesBySimpleKey(msgSplitted[0]);
			if ((series != null) && (series.getId() != null))
				chartService.deleteTimeSeries(msgSplitted[0]);
		}
	}
	
	@Test
	public void testSendMessage() throws Exception {
		for (final String msg : msgs) {
			MessageEvent event = new MessageEvent() {

				@Override
				public ChannelFuture getFuture() {
					return null;
				}

				@Override
				public Channel getChannel() {
					return null;
				}

				@Override
				public SocketAddress getRemoteAddress() {
					return null;
				}

				@Override
				public Object getMessage() {
					return msg;
				}
			};
			carbonGraphiteServiceHandler.messageReceived(null, event);
		}
		long count = 0;
		for (final String msg : msgs) { 
			String[] msgSplitted = StringUtils.split(msg);
			Series series = chartService.getTimeSeriesBySimpleKey(msgSplitted[0]);
			if (series != null)
				count++;
		}
		Assert.assertEquals(msgs.length, count);
	}
}
