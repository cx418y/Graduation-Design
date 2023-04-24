package fudan.design.clone.utils.sagaUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProcessCounter {

    private final int total;
    private final AtomicInteger count;
    private final AtomicInteger rate;
    private final String desc;

    public ProcessCounter(int n, String process) {
        total = n;
        count = new AtomicInteger(0);
        rate = new AtomicInteger(0);
        this.desc = "process " + process + ":";
    }

    public void progress() {
        int curRate = count.incrementAndGet() * 100 / total;
        if (curRate != rate.get()) {
            rate.set(curRate);
            log.info(desc + rate + "%");
        }
    }
}
