package com.aitd.library_common.encrypt.aes;

/**
 * 时间记录记录仪
 *
 * @author wuyy
 */
public class TimeRecorder {

    private Long beginMillis;
    private Long endMillis;

    private TimeRecorder() {
    }

    public static TimeRecorder start() {
        TimeRecorder recorder = new TimeRecorder();
        recorder.begin();
        return recorder;
    }

    private void begin() {
        this.beginMillis = System.currentTimeMillis();
    }

    public void end() {
        this.endMillis = System.currentTimeMillis();
    }

    public Long getRunMillis() {
        return endMillis - beginMillis;
    }
}
