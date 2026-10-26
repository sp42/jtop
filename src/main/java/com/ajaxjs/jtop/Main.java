package com.ajaxjs.jtop;

import com.ajaxjs.jtop.management.JTopMXBean;
import com.ajaxjs.jtop.objects.MainOutput;
import com.ajaxjs.jtop.rmi.RmiClient;
import com.ajaxjs.jtop.rmi.interfaces.*;
import com.ajaxjs.jtop.rmi.interfaces.StackTraceElement;
import com.ajaxjs.jtop.util.ClassLoaderUtil;
import com.ajaxjs.jtop.util.CollectionUtil;
import jline.Terminal;
import jline.TerminalFactory;
import me.hatter.tools.commons.ByteUtil;
import me.hatter.tools.commons.ShutdownSignal;
import me.hatter.tools.commons.args.UnixArgsutil;
import me.hatter.tools.commons.color.Color;
import me.hatter.tools.commons.color.Font;
import me.hatter.tools.commons.color.Text;
import me.hatter.tools.commons.jvm.HotSpotVMUtil;
import me.hatter.tools.commons.screen.Printer;
import me.hatter.tools.commons.screen.TermUtils;
import me.hatter.tools.commons.screen.impl.BatchOutputScreenPrinter;
import me.hatter.tools.commons.screen.impl.NormalPrinter;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: jtop.jar:me/hatter/tools/jtop/main/Main.class */
public class Main {
    private static Object term;
    private static JMemoryInfo oldJMemoryInfo;
    private static JClassLoadingInfo oldJClassLoadingInfo;
    private static final Map<String, JGCInfo> oldJGCInfoMap = new HashMap<>();
    private static final Map<Long, JThreadInfo> oldJThreadInfoMap = new HashMap<>();
    private static final Map<Thread.State, AtomicInteger> oldStateThreadMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            UnixArgsutil.parseGlobalArgs(args);
            boolean advanced = EnvUtil.getAdvanced();
            HotSpotVMUtil.autoAddToolsJarDependency(HotSpotVMUtil.JDKTarget.SYSTEM_CLASSLOADER, HotSpotVMUtil.JDKLib.TOOLS);

            if (advanced)
                ClassLoaderUtil.addResourceToSystemClassLoader("/commons-resources/jline-2.9.jar");

            if (UnixArgsutil.ARGS.args().length == 0) {
                System.out.println("[ERROR] pid is not assigned.");
                usage();
                System.exit(0);
            }
            String pid = UnixArgsutil.ARGS.args()[0];
            RmiClient rc = new RmiClient(pid);
            JTopMXBean jTopMXBean = rc.getJTopMXBean();
            ShutdownSignal shutdownSignal = new ShutdownSignal();
            long lastNano = System.nanoTime();
            MainOutput lastMainOutput = null;
            Map<Long, JThreadInfo> lastJThreadInfoMap = null;
            int dumpcount = advanced ? Integer.MAX_VALUE : EnvUtil.getDumpCount();

            for (int c = -1; c < dumpcount; c++) {
                long nano = System.nanoTime();
                MainOutput mainOutput = new MainOutput(c + 1);
                JThreadInfo[] jThreadInfos = jTopMXBean.listThreadInfos();
                Map<Long, JThreadInfo> jThreadInfoMap = jThreadInfoToMap(jThreadInfos);
                if (lastMainOutput == null) {
                    System.out.println("[INFO] First Round");
                } else {
                    shutdownSignal.acquire();
                    try {
                        Printer printer = new NormalPrinter();
                        if (advanced) {
                            if (term == null) {
                                term = TerminalFactory.create();
                            }
                            printer = new BatchOutputScreenPrinter(((Terminal) term).getWidth(), ((Terminal) term).getHeight());
                        }
                        displayRound(jTopMXBean, lastNano, lastMainOutput, lastJThreadInfoMap, nano, mainOutput, jThreadInfos, printer);
                        shutdownSignal.release();
                    } catch (Throwable th) {
                        shutdownSignal.release();
                        throw th;
                    }
                }
                lastNano = nano;
                lastMainOutput = mainOutput;
                lastJThreadInfoMap = jThreadInfoMap;
                if (c < dumpcount - 1) {
                    Thread.sleep(EnvUtil.getSleepMillis());
                }
            }
            System.out.println("[INFO] Dump Finish");
        } catch (Exception e) {
            System.err.println("[ERROR] unknow error occured: " + e.getMessage());
            e.printStackTrace();
            System.out.print(TermUtils.RESET);
        }
    }

    private static void displayRound(JTopMXBean jTopMXBean, long lastNano, MainOutput lastMainOutput, Map<Long, JThreadInfo> lastJThreadInfoMap, long nano, MainOutput mainOutput, JThreadInfo[] jThreadInfos, Printer printer) {
        if (printer.getClass() == NormalPrinter.class) {
            System.out.println("NEW ROUND =================================================================== ");
        }
        JThreadInfo[] cJThreadInfos = sortJThreadInfos(caculateJThreadInfos(jThreadInfos, lastJThreadInfoMap));
        int threadtopn = EnvUtil.getThreadTopN();
        int stacktracetopn = EnvUtil.getStacktraceTopN();
        long cost = nano - lastNano;
        long totalCpu = 0;
        long totalUser = 0;
        for (JThreadInfo jThreadInfo : cJThreadInfos) {
            totalCpu += jThreadInfo.getCpuTime();
            totalUser += jThreadInfo.getUserTime();
        }
        mainOutput.setTotalThreadCount(cJThreadInfos.length);
        mainOutput.setTotalCpuTime(totalCpu);
        mainOutput.setTotalUserTime(totalUser);
        String size = EnvUtil.getSize();
        DecimalFormat YFF = new DecimalFormat("0.00");
        Map<Thread.State, AtomicInteger> stateMap = new HashMap<>();
        for (JThreadInfo jThreadInfo2 : cJThreadInfos) {
            Thread.State state = jThreadInfo2.getThreadState();
            if (stateMap.containsKey(state)) {
                stateMap.get(state).incrementAndGet();
            } else {
                stateMap.put(state, new AtomicInteger(1));
            }
        }
        if (!UnixArgsutil.ARGS.flags().contains("summaryoff")) {
            JMemoryInfo jMemoryInfo = jTopMXBean.getMemoryInfo();
            mainOutput.setJMemoryInfo(jMemoryInfo);
            printer.print("Heap Memory: ");

            if (!EnvUtil.getColor() || oldJMemoryInfo == null) {
                printer.print("INIT=" + toSize(jMemoryInfo.getHeap().getInit(), size));
                printer.print("  ");
                printer.print("USED=" + toSize(jMemoryInfo.getHeap().getUsed(), size));
                printer.print("  ");
                printer.print("COMMITED=" + toSize(jMemoryInfo.getHeap().getCommitted(), size));
                printer.print("  ");
                printer.print("MAX=" + toSize(jMemoryInfo.getHeap().getMax(), size));
            } else {
                printer.print(Text.createText(getFont(jMemoryInfo.getHeap().getInit(), oldJMemoryInfo.getHeap().getInit()), "INIT=" + toSize(jMemoryInfo.getHeap().getInit(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getHeap().getUsed(), oldJMemoryInfo.getHeap().getUsed()), "USED=" + toSize(jMemoryInfo.getHeap().getUsed(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getHeap().getCommitted(), oldJMemoryInfo.getHeap().getCommitted()), "COMMITED=" + toSize(jMemoryInfo.getHeap().getCommitted(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getHeap().getMax(), oldJMemoryInfo.getHeap().getMax()), "MAX=" + toSize(jMemoryInfo.getHeap().getMax(), size)));
            }
            printer.println();
            printer.print("NonHeap Memory: ");
            if (!EnvUtil.getColor() || oldJMemoryInfo == null) {
                printer.print("INIT=" + toSize(jMemoryInfo.getNonHeap().getInit(), size));
                printer.print("  ");
                printer.print("USED=" + toSize(jMemoryInfo.getNonHeap().getUsed(), size));
                printer.print("  ");
                printer.print("COMMITED=" + toSize(jMemoryInfo.getNonHeap().getCommitted(), size));
                printer.print("  ");
                printer.print("MAX=" + toSize(jMemoryInfo.getNonHeap().getMax(), size));
            } else {
                printer.print(Text.createText(getFont(jMemoryInfo.getNonHeap().getInit(), oldJMemoryInfo.getNonHeap().getInit()), "INIT=" + toSize(jMemoryInfo.getNonHeap().getInit(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getNonHeap().getUsed(), oldJMemoryInfo.getNonHeap().getUsed()), "USED=" + toSize(jMemoryInfo.getNonHeap().getUsed(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getNonHeap().getCommitted(), oldJMemoryInfo.getNonHeap().getCommitted()), "COMMITED=" + toSize(jMemoryInfo.getNonHeap().getCommitted(), size)));
                printer.print("  ");
                printer.print(Text.createText(getFont(jMemoryInfo.getNonHeap().getMax(), oldJMemoryInfo.getNonHeap().getMax()), "MAX=" + toSize(jMemoryInfo.getNonHeap().getMax(), size)));
            }

            printer.println();
            oldJMemoryInfo = jMemoryInfo;
            JGCInfo[] jgcInfos = jTopMXBean.getGCInfos();
            for (JGCInfo jgcInfo : jgcInfos) {
                printer.print("GC ");
                printer.print(jgcInfo.getName());
                printer.print("  ");
                printer.print(jgcInfo.isValid() ? "VALID" : "NOT_VALID");
                printer.print("  ");
                printer.print(Arrays.asList(jgcInfo.getMemoryPoolNames()).toString());
                printer.print("  ");
                JGCInfo oldJGCInfo = oldJGCInfoMap.get(jgcInfo.getName());
                if (!EnvUtil.getColor() || oldJGCInfo == null) {
                    printer.print("GC=" + jgcInfo.getCollectionCount());
                    printer.print("  ");
                    printer.print("GCT=" + jgcInfo.getCollectionTime());
                } else {
                    printer.print(Text.createText(getFont(jgcInfo.getCollectionCount(), oldJGCInfo.getCollectionCount()), "GC=" + jgcInfo.getCollectionCount()));
                    printer.print("  ");
                    printer.print(Text.createText(getFont(jgcInfo.getCollectionTime(), oldJGCInfo.getCollectionTime()), "GCT=" + jgcInfo.getCollectionTime()));
                }
                printer.println();
                oldJGCInfoMap.put(jgcInfo.getName(), jgcInfo);
            }
            JClassLoadingInfo jClassLoadingInfo = jTopMXBean.getClassLoadingInfo();
            printer.print("ClassLoading ");
            if (!EnvUtil.getColor() || oldJClassLoadingInfo == null) {
                printer.print("LOADED=" + jClassLoadingInfo.getLoadedClassCount());
                printer.print("  ");
                printer.print("TOTAL_LOADED=" + jClassLoadingInfo.getTotalLoadedClassCount());
                printer.print("  ");
                printer.print("UNLOADED=" + jClassLoadingInfo.getUnloadedClassCount());
            } else {
                printer.print(Text.createText(getFont(jClassLoadingInfo.getLoadedClassCount(), oldJClassLoadingInfo.getLoadedClassCount()), "LOADED=" + jClassLoadingInfo.getLoadedClassCount()));
                printer.print("  ");
                printer.print(Text.createText(getFont(jClassLoadingInfo.getTotalLoadedClassCount(), oldJClassLoadingInfo.getTotalLoadedClassCount()), "TOTAL_LOADED=" + jClassLoadingInfo.getTotalLoadedClassCount()));
                printer.print("  ");
                printer.print(Text.createText(getFont(jClassLoadingInfo.getUnloadedClassCount(), oldJClassLoadingInfo.getUnloadedClassCount()), "UNLOADED=" + jClassLoadingInfo.getUnloadedClassCount()));
            }
            printer.println();
            oldJClassLoadingInfo = jClassLoadingInfo;
        }
        printer.print("Total threads: ");
        if (!EnvUtil.getColor() || oldStateThreadMap.isEmpty()) {
            printer.print(String.valueOf(cJThreadInfos.length));
            printer.print("  ");
            printer.print("CPU=" + TimeUnit.NANOSECONDS.toMillis(totalCpu) + " (" + YFF.format((totalCpu * 100.0d) / cost) + "%)");
            printer.print("  ");
            printer.print("USER=" + TimeUnit.NANOSECONDS.toMillis(totalUser) + " (" + YFF.format((totalUser * 100.0d) / cost) + "%)");
        } else {
            printer.print(Text.createText(getFont(cJThreadInfos.length, mainOutput.getTotalThreadCount()), String.valueOf(cJThreadInfos.length)));
            printer.print("  ");
            printer.print(Text.createText(getFont(totalCpu, lastMainOutput.getTotalCpuTime()), "CPU=" + TimeUnit.NANOSECONDS.toMillis(totalCpu) + " (" + YFF.format((totalCpu * 100.0d) / cost) + "%)"));
            printer.print("  ");
            printer.print(Text.createText(getFont(totalUser, lastMainOutput.getTotalUserTime()), "USER=" + TimeUnit.NANOSECONDS.toMillis(totalUser) + " (" + YFF.format((totalUser * 100.0d) / cost) + "%)"));
        }
        printer.println();
        for (int i = 0; i < Thread.State.values().length; i++) {
            Thread.State state2 = Thread.State.values()[i];
            AtomicInteger ai = stateMap.get(state2);
            AtomicInteger oldAi = oldStateThreadMap.get(state2);
            AtomicInteger ai2 = ai == null ? new AtomicInteger(0) : ai;
            if (EnvUtil.getColor() || oldAi == null) {
                printer.print(state2 + "=" + ai2.get());
            } else {
                printer.print(Text.createText(getFont(ai2.get(), oldAi.get()), state2 + "=" + ai2.get()));
            }
            if (i < Thread.State.values().length - 1) {
                printer.print("  ");
            }
            oldStateThreadMap.put(state2, ai2);
        }

        printer.println();
        printer.println();

        for (int i2 = 0; i2 < cJThreadInfos.length && i2 < threadtopn; i2++) {
            JThreadInfo jThreadInfo3 = cJThreadInfos[i2];
            JThreadInfo oldJThreadInfo = oldJThreadInfoMap.get(jThreadInfo3.getThreadId());
            printer.print(jThreadInfo3.getThreadName());
            printer.print("  ");
            printer.print("TID=" + jThreadInfo3.getThreadId());
            printer.print("  ");
            printer.print("STATE=" + jThreadInfo3.getThreadState().name());
            printer.print("  ");

            if (!EnvUtil.getColor() || oldJThreadInfo == null) {
                printer.print("CPU_TIME=" + TimeUnit.NANOSECONDS.toMillis(jThreadInfo3.getCpuTime()) + " (" + YFF.format((jThreadInfo3.getCpuTime() * 100.0d) / cost) + "%)");
                printer.print("  ");
                printer.print("USER_TIME=" + TimeUnit.NANOSECONDS.toMillis(jThreadInfo3.getUserTime()) + " (" + YFF.format((jThreadInfo3.getUserTime() * 100.0d) / cost) + "%)");
//                printer.print(AnsiRenderer.CODE_TEXT_SEPARATOR);
                printer.print("Allocted: " + toSize(jThreadInfo3.getAlloctedBytes(), size));
            } else {
                printer.print(Text.createText(getFont(jThreadInfo3.getCpuTime(), oldJThreadInfo.getCpuTime()), "CPU_TIME=" + TimeUnit.NANOSECONDS.toMillis(jThreadInfo3.getCpuTime()) + " (" + YFF.format((jThreadInfo3.getCpuTime() * 100.0d) / cost) + "%)"));
                printer.print("  ");
                printer.print(Text.createText(getFont(jThreadInfo3.getUserTime(), oldJThreadInfo.getUserTime()), "USER_TIME=" + TimeUnit.NANOSECONDS.toMillis(jThreadInfo3.getUserTime()) + " (" + YFF.format((jThreadInfo3.getUserTime() * 100.0d) / cost) + "%)"));
//                printer.print(AnsiRenderer.CODE_TEXT_SEPARATOR);
                printer.print(Text.createText(getFont(jThreadInfo3.getAlloctedBytes(), oldJThreadInfo.getAlloctedBytes()), "Allocted: " + toSize(jThreadInfo3.getAlloctedBytes(), size)));
            }

            printer.println();
            int matchCount = 0;

            for (int j = 0; j < jThreadInfo3.getStackTrace().length && matchCount < stacktracetopn; j++) {
                StackTraceElement stackTrace = jThreadInfo3.getStackTrace()[j];

                if (isMatch(stackTrace)) {
                    matchCount++;
                    printer.println("    " + stackTrace.toString());
                }
            }

            if (matchCount == 0 && jThreadInfo3.getStackTrace().length > 0)
                printer.println("    ---- all filtered ----");

            printer.println();
            oldJThreadInfoMap.put(jThreadInfo3.getThreadId(), jThreadInfo3);
        }

        printer.println();
        printer.finish();
    }

    static boolean isMatch(StackTraceElement element) {
        List<String> excludes = UnixArgsutil.ARGS.kvalues("excludes");
        List<String> includes = UnixArgsutil.ARGS.kvalues("includes");

        if (CollectionUtil.isEmpty(excludes))
            return true;

        boolean isMatch = false;

        for (String e : excludes) {
            if (isMatchOne(element.toString(), e))
                isMatch = true;
        }

        if (!isMatch)
            return true;

        if (!CollectionUtil.isEmpty(includes)) {
            for (String i : includes) {
                if (isMatchOne(element.toString(), i))
                    return true;
            }

            return false;
        }

        return false;
    }

    static boolean isMatchOne(String ele, String pattern) {
        if (pattern.startsWith("^"))
            return ele.startsWith(pattern.substring(1));

        return ele.contains(pattern);
    }

    static JThreadInfo[] sortJThreadInfos(JThreadInfo[] cJThreadInfos) {
        boolean isSortMem = EnvUtil.getSortMem();

        if (isSortMem) {
            Arrays.sort(cJThreadInfos, new Comparator<JThreadInfo>() { // from class: me.hatter.tools.jtop.main.Main.1
                @Override // java.util.Comparator
                public int compare(JThreadInfo o1, JThreadInfo o2) {
                    int rMem = Long.compare(o2.getAlloctedBytes(), o1.getAlloctedBytes());
                    if (rMem != 0)
                        return rMem;

                    return o2.getThreadName().compareTo(o1.getThreadName());
                }
            });
        } else {
            // from class: me.hatter.tools.jtop.main.Main.2
// java.util.Comparator
            Arrays.sort(cJThreadInfos, (o1, o2) -> {
                int rCpu = Long.compare(o2.getCpuTime(), o1.getCpuTime());
                if (rCpu != 0)
                    return rCpu;

                int rUser = Long.compare(o2.getUserTime(), o1.getUserTime());
                if (rUser != 0)
                    return rUser;

                return o2.getThreadName().compareTo(o1.getThreadName());
            });
        }
        return cJThreadInfos;
    }

    private static Font getFont(long v1, long v2) {
        if (!EnvUtil.getColor() || v2 == v1)
            return Font.createFont(null);

        return v2 < v1 ? Font.createFont(Color.getColor(TermUtils.XBack.RED)) : Font.createFont(Color.getColor(TermUtils.XBack.GREEN));
    }

    private static JThreadInfo[] caculateJThreadInfos(JThreadInfo[] jThreadInfos, Map<Long, JThreadInfo> lastJThreadInfoMap) {
        JThreadInfo[] cjThreadInfos = new JThreadInfo[jThreadInfos.length];

        for (int i = 0; i < jThreadInfos.length; i++) {
            JThreadInfo jThreadInfo = jThreadInfos[i];
            JThreadInfo lastJThreadInfo = lastJThreadInfoMap.get(jThreadInfo.getThreadId());

            if (lastJThreadInfo == null)
                cjThreadInfos[i] = jThreadInfo;
            else
                cjThreadInfos[i] = new JThreadInfo(jThreadInfo, jThreadInfo.getCpuTime() - lastJThreadInfo.getCpuTime(), jThreadInfo.getUserTime() - lastJThreadInfo.getUserTime(), jThreadInfo.getAlloctedBytes() - lastJThreadInfo.getAlloctedBytes());

        }

        return cjThreadInfos;
    }

    private static Map<Long, JThreadInfo> jThreadInfoToMap(JThreadInfo[] jThreadInfos) {
        Map<Long, JThreadInfo> jThreadInfoMap = new HashMap<>();
        for (JThreadInfo jThreadInfo : jThreadInfos)
            jThreadInfoMap.put(jThreadInfo.getThreadId(), jThreadInfo);

        return jThreadInfoMap;
    }

    private static String toSize(long b, String s) {
        return ByteUtil.formatBytes(ByteUtil.ByteFormat.fromString(s), b);
    }

    private static void usage() {
        System.out.println("Usage[b121209]:");
        System.out.println("java -jar jtop.jar [options] <pid> [<interval> [<count>]]");
        System.out.println("-OR-");
        System.out.println("java -cp jtop.jar jtop [options] <pid> [<interval> [<count>]]");
        System.out.println("    -size <B|K|M|G|H>             Size, case insensitive (default: B, H for human)");
        System.out.println("    -thread <N>                   Thread Top N (default: 5)");
        System.out.println("    -stack <N>                    Stacktrace Top N (default: 8)");
        System.out.println("    -excludes                     Excludes (string.contains)");
        System.out.println("    -includes                     Includes (string.contains, excludes than includes)");
        System.out.println("    --color                       Display color (default: off)");
        System.out.println("    --sortmem                     Sort by memory allocted (default: off)");
        System.out.println("    --summaryoff                  Do not display summary (default: off)");
        System.out.println("    --advanced                    Do display like 'top' (default: off)");
        System.out.println();
    }
}