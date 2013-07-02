[Mkarma@qvipkar5 ~]$ sudo su - karma
karma@:/app/KARMA > ssh qvipkfs5
Warning: Permanently added 'qvipkfs5,10.60.128.140' (RSA) to the list of known hosts.
Last login: Thu Oct  4 08:18:34 2012 from qvipsvx2.france.airfrance.fr

*******************************************************************************
*               Bluehat 2.0                                                   *
*                                                                             *
* Note: Air France-KLM internal systems must be used only for conducting      *
*       Air France-KLM business, or for purposes authorized by                *
*       Air France-KLM management.                                            *
*       Use is subject to audit at any time by Air France-KLM management      *
*                                                                             *
*******************************************************************************

karma@:/app/KARMA > cd /app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current
karma@:/app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current > ls
edits  fsimage  fstime  VERSION
karma@:/app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current > ls -al
total 13734
drwxr-xr-x 2 hadoop hadoop       96 Oct 10 15:07 .
drwxr-xr-x 4 hadoop hadoop     1024 Oct 10 15:07 ..
-rw-r--r-- 1 hadoop hadoop        4 Oct 10 15:07 edits
-rw-r--r-- 1 hadoop hadoop 14058716 Oct 10 15:07 fsimage
-rw-r--r-- 1 hadoop hadoop        8 Oct 10 15:07 fstime
-rw-r--r-- 1 hadoop hadoop      101 Oct 10 15:07 VERSION
karma@:/app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current > scp
usage: scp [-1246BCpqrv] [-c cipher] [-F ssh_config] [-i identity_file]
           [-l limit] [-o ssh_option] [-P port] [-S program]
           [[user@]host1:]file1 [...] [[user@]host2:]file2
karma@:/app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current > scp fsimage Mkarma@qvipkar5 /tmp/karma-search
/tmp/karma-search: No such file or directory
karma@:/app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current > scp fsimage Mkarma@qvipkar5:/tmp/karma-search
