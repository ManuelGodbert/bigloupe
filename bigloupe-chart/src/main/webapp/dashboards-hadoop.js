var graphite_url = "http://localhost:2006/graphite";  // enter your graphite url, e.g. http://your.graphite.com

var dashboards = 
[
  { "name": "KARMA Qualification Hadoop cluster NameNode",  // give your dashboard a name (required!)
    "refresh": 1000000,  // each dashboard has its own refresh interval (in ms)
    // add an (optional) dashboard description. description can be written in markdown / html.
    "description": "JMX lh-hadoopkarmaqua-st_france_airfrance_fr",
    "metrics":  // metrics is an array of charts on the dashboard
    [
	 {
    	"alias": "System Load average",  // display name for this metric
    	"target": "servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.com_sun_management_UnixOperatingSystem.SystemLoadAverage",  
     	"renderer": "line",
  	  },
//	  {
//        "alias": "Add block operations",  // display name for this metric
//        "target": "servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.org_apache_hadoop_hdfs_server_namenode_metrics_NameNodeActivtyMBean.NameNodeActivity.AddBlockOps",  
//		"renderer": "line",
//      },
//      {
//        "alias": "Threads name node",
//        "targets": "servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.org_apache_hadoop_hdfs_server_namenode_FSNamesystem.NameNodeInfo.Threads",
//        "renderer": "line",  
//      },
//      {
//          "alias": "Capacity Total - Capacity Used",
//          "targets": ["servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.NameNode.FSNamesystemState.CapacityTotal", "servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.NameNode.FSNamesystemState.CapacityUsed"],  
//          "renderer": "line",
//          "unstack": true,
//        },
//      {
//        "alias": "Total physical memory size",
//        "targets": "servers.lh-hadoopkarmaqua-st_france_airfrance_fr_8005.com_sun_management_UnixOperatingSystem.TotalPhysicalMemorySize",  
//        "renderer": "line",  
//      },
    ]
  },
  { "name": "Documentation",
    "refresh": 10000,
    // you can use any rickshaw supported color scheme.
    // Enter palette name as string, or an array of colors
    // (see var scheme at the bottom).
    // Schemes can be configured globally (see below), per-dashboard, or per-metric
    "scheme": "classic9",   // this is a dashboard-specific color palette
    "description": "#Visual settings <img class='pull-right' src='img/giraffe.png' />"
                +"\n"
                +"\nGiraffe is using the [Rickshaw](http://code.shutterstock.com/rickshaw/) d3 charting library."
                +"\nYou can therefore configure almost any visual aspect supported by rickshaw/d3 inside giraffe."
                +"\n"
                +"\nThis includes: [color scheme](https://github.com/shutterstock/rickshaw#rickshawcolorpalette), interpolation, [renderer](https://github.com/shutterstock/rickshaw#renderer) and more."
                +"\n"
                +"\n##Top panel"
                +"\n"
                +"\nThe top panel allows toggling between time ranges (not visible on the demo, but should work fine with graphite)."
                +"\nIt also supports toggling the legend <i class='icon-list-alt'></i>, grid <i class='icon-th'></i>"
                +"\n, X labels <i class='icon-comment'></i> and tooltips <i class='icon-remove-circle'></i>"
                +"\n"
                +"\n##<i class='icon-list-alt'></i> Legend"
                +"\n"
                +"\nClicking on the legend will show the legend under each chart. The legend includes summary information for each series, "
                +"\n &Sigma;: Total; <i class='icon-caret-down'></i>: Minimum; <i class='icon-caret-up'></i>: Maximum; and <i class='icon-sort'></i>: Average"
                +"\n"
                +"\n##Summary"
                +"\n"
                +"\nIn addition to the legend, each chart can have one `summary` value displayed next to its title."
                +"\nThe summary is calculated over the entire series and can be one of `[sum|max|min|avg|last|<function>]`"
                +"\n"
                +"\n"
                +"\n"
                ,
    "metrics": 
    [
      {
        "alias": "cpu utilization",
        "target": "aliasByNode(derivative(servers.system.cpu.*),4)",  // target can use any graphite-supported wildcards
        "annotator": 'events.deployment',  // a simple annotator will track a graphite event and mark it as 'deployment'.
                                           // enter your graphite target as a string
        "description": "cpu utilization on production (using linear interpolation). Summary displays the average across all series",
        "interpolation": "linear",  // you can use different rickshaw interpolation values
        "summary": "avg",
      },
      {
        "alias": "proc mem prod",
        "targets": ["aliasByNode(derivative(servers.system.cpu.user),4)",  // targets array can include strings, 
                                                                           // functions or dictionaries
                   {target: 'alias(derivative(servers.system.cpu.system,"system utilization")',
                    alias: 'system utilization',                           // if you use a graphite alias, specify it here
                    color: '#f00'}],                                       // you can also specify a target color this way
                                                                           // (note that these values are ignored on the demo)
        // annotator can also be a dictionary of target and description.
        // However, only one annotator is supported per-metric.
        "annotator": {'target' : 'events.deployment',
                      'description' : 'deploy'},
        "description": "main process memory usage on production (different colour scheme and interpolation)",
        "interpolation": "step-before",
        "scheme": "munin",  // this is a metric-specific color palette
      },
      {
        "alias": "sys mem prod",
        "target": "aliasByNode(derivative(servers.system.cpu.*),4)",
        "events": "*",  // instead of annotator, if you use the graphite events feature
                        // you can retrieve events matching specific tag(s) -- space separated
                        // or use * for all tags. Note you cannot use both annotator and events.
        "description": "main system memory usage on production (cardinal interpolation, line renderer)",
        "interpolation": "cardinal",
        "renderer": "line",
      },
    ]
  },

];

var scheme = [
              '#423d4f',
              '#4a6860',
              '#848f39',
              '#a2b73c',
              '#ddcb53',
              '#c5a32f',
              '#7d5836',
              '#963b20',
              '#7c2626',
              ].reverse();

function relative_period() { return (typeof period == 'undefined') ? 1 : parseInt(period / 7) + 1; }
function entire_period() { return (typeof period == 'undefined') ? 1 : period; }
function at_least_a_day() { return entire_period() >= 1440 ? entire_period() : 1440; }
