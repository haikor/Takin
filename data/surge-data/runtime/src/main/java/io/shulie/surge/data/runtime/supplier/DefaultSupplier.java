package io.shulie.surge.data.runtime.supplier;

import com.google.common.collect.Lists;
import io.shulie.surge.data.common.lifecycle.LifecycleObserver;
import io.shulie.surge.data.runtime.processor.DataQueue;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;


/**
 * @author vincent 
 */
class DefaultSupplier implements Supplier {

    private final CopyOnWriteArrayList<LifecycleObserver<Supplier>> observers =
            Lists.newCopyOnWriteArrayList();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private DataQueue queue;

    /**
     * 开始获取数据
     *
     * @throws IllegalStateException Queue 尚未设置时抛出此异常
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        checkState(queue != null, "set queue before start supplier");
        queue.start();
    }

    /**
     * 停止获取数据
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        queue.stop();
    }

    /**
     * 检查当前是否在运行状态
     */
    @Override
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 设置使用的队列
     *
     * @param queue
     * @throws IllegalStateException 已经执行了 {@link #start()} 时抛出此异常
     */
    @Override
    public void setQueue(DataQueue queue) throws IllegalStateException {
        checkState(!running.get(), "supplier has been started");
        this.queue = queue;
    }

    @Override
    public void addObserver(LifecycleObserver<Supplier> observer) {
        observers.add(observer);
    }
}