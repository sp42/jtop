package me.hatter.tools.commons.jvm;

import com.sun.tools.attach.VirtualMachine;
import me.hatter.tools.commons.LogUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("restriction")
public class HotSpotAttachTool {
    private final String pid;

    private final AtomicReference<VirtualMachine> refVM = new AtomicReference<>(null);

    public HotSpotAttachTool(String pid) {
        this.pid = pid;
    }

    public VirtualMachine getVM() {
        return refVM.get();
    }

    public void attach() {
        try {
            LogUtil.info("Attach to vm: " + pid);
            refVM.set(VirtualMachine.attach(pid));
            Runtime.getRuntime().addShutdownHook(new Thread() {

                public void run() {
                    detach();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void detach() {
        synchronized (refVM) {
            if (refVM.get() == null)
                return;

            VirtualMachine vm = refVM.get();
            refVM.set(null);

            try {
                LogUtil.info("Detach from vm.");
                vm.detach();
            } catch (IOException e) {
                LogUtil.error("Detach from vm failed: " + LogUtil.printStackTrace(e));
            }
        }
    }
}
