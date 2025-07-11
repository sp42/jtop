package me.hatter.tools.commons.args;

// pid, interval, count arguments
public class PICArgs {
    private Integer pid;
    private Long interval;
    private Integer count;
    private Long defInterval;
    private Integer defCount;

    public PICArgs() {
    }

    public PICArgs(long defInterval, int defcount) {
        this.defInterval = defInterval;
        this.defCount = defcount;
    }

    public void parseArgs(String[] args) {
        if (args.length > 0)
            pid = Integer.parseInt(args[0]);

        if (args.length > 1)
            interval = Long.parseLong(args[1]);

        if (args.length > 2)
            count = Integer.parseInt(args[2]);
    }

    public boolean isPidSetted() {
        return (pid != null);
    }

    public boolean isIntervalSetted() {
        return (interval != null);
    }

    public boolean isDefIntervalSetted() {
        return (defInterval != null);
    }

    public boolean isCountSetted() {
        return (count != null);
    }

    public boolean isDefCountSetted() {
        return (defCount != null);
    }

    public int getPid() {
        if (!isPidSetted())
            throw new RuntimeException("JVM pid is not set.");

        return pid;
    }

    public long getInterval() {
        if (isIntervalSetted())
            return interval;

        if (defInterval != null)
            return defInterval;

        throw new RuntimeException("Interval is not set.");
    }

    public int getCount() {
        if (isCountSetted())
            return count;

        if (defCount != null)
            return defCount;

        throw new RuntimeException("Count is not set.");
    }
}
