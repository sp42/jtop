# Introduction

https://github.com/andrewglowacki/jtop
https://blog.csdn.net/iamzhongyong/article/details/9297557

# Usage

在排查线上问题的时候，我们经常使用jstat、jstack、jmap查看GC、内存、线程的信息，
但是有时候我们需要获取一个整体的信息来帮助我们快速定位，类似linux中自带的top命令，简单而全面的系统信息，
在网上找了些资料，发现之前有人写了个jtop的工具，能够全面获取java应用的信息.
原理是使用了java中的MBean，借这个机会，把MBean的东西简单了解一下

查看占用CPU前三的线程栈信息（sudo -u admin java -cp jtop.jar jtop -thread 3 -stack 50 --summaryoff  PID）

Get jtop:
Get `jtop.jar` from

Usage:

```
$ java -jar jtop.jar 
[ERROR] pid is not assigned.
Usage[b121209]:
java -jar jtop.jar [options] <pid> [<interval> [<count>]]
-OR-
java -cp jtop.jar jtop [options] <pid> [<interval> [<count>]]
    -size <B|K|M|G|H>             Size, case insensitive (default: B, H for human)
    -thread <N>                   Thread Top N (default: 5)
    -stack <N>                    Stacktrace Top N (default: 8)
    -excludes                     Excludes (string.contains)
    -includes                     Includes (string.contains, excludes than includes)
    --color                       Display color (default: off)
    --sortmem                     Sort by memory allocted (default: off)
    --summaryoff                  Do not display summary (default: off)
    --advanced                    Do display like 'top' (default: off)
```

Use this command to view the eclipse's top:

```
$ java -jar jtop.jar -size h -thread 3 -stack 4 387
```

You would see:

```
NEW ROUND ================================================== 
Heap Memory: INIT=40.00M  USED=145.16M  COMMITED=278.57M  MAX=379.88M
NonHeap Memory: INIT=23.19M  USED=74.11M  COMMITED=118.62M  MAX=304.00M
GC ParNew  VALID  [Par Eden Space, Par Survivor Space]  GC=103  GCT=1532
GC ConcurrentMarkSweep  VALID  [Par Eden Space, Par Survivor Space, CMS Old Gen, CMS Perm Gen]  GC=7  GCT=75
ClassLoading LOADED=8776  TOTAL_LOADED=8780  UNLOADED=4
Total threads: 24  CPU=30 (1.49%)  USER=21 (1.09%)
NEW=0  RUNNABLE=7  BLOCKED=0  WAITING=10  TIMED_WAITING=7  TERMINATED=0  
main  TID=1  STATE=RUNNABLE  CPU_TIME=20 (1.03%)  USER_TIME=13 (0.68%)
        org.eclipse.swt.internal.cocoa.OS.objc_msgSend_bool(Native Method)
        org.eclipse.swt.internal.cocoa.NSRunLoop.runMode(NSRunLoop.java:42)
        org.eclipse.swt.widgets.Display.sleep(Display.java:4193)
        org.eclipse.ui.application.WorkbenchAdvisor.eventLoopIdle(WorkbenchAdvisor.java:364)

RMI TCP Connection(2)-10.18.214.175  TID=102  STATE=RUNNABLE  CPU_TIME=9 (0.45%)  USER_TIME=8 (0.40%)
        sun.management.ThreadImpl.dumpThreads0(Native Method)
        sun.management.ThreadImpl.dumpAllThreads(ThreadImpl.java:433)
        me.hatter.tools.jtop.rmi.RmiServer.listThreadInfos(RmiServer.java:104)
        sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)

Poller SunPKCS11-Darwin  TID=11  STATE=TIMED_WAITING  CPU_TIME=0 (0.01%)  USER_TIME=0 (0.00%)
        java.lang.Thread.sleep(Native Method)
        sun.security.pkcs11.SunPKCS11$TokenPoller.run(SunPKCS11.java:692)
        java.lang.Thread.run(Thread.java:680)
```

Output like 'top' use arguemnt --A or --advanced:

```
$ java -jar jtop.jar --A --C 387
-OR-
$ java -jar jtop.jar ---AC 387
```

![jtop.png](https://bitbucket.org/repo/E9aogx/images/19642114-jtop.png)

# Case

* [CASE01](https://bitbucket.org/hatterjiang/jtop/wiki/CASE01)%     