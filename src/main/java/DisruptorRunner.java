import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;

public class DisruptorRunner {

    private static Logger LOG = LogManager.getLogger(DisruptorRunner.class);

    public static void main(String[] args) {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;

        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<ValueEvent> disruptor
                = new Disruptor<>(
                ValueEvent.EVENT_FACTORY,
                16,
                threadFactory,
                ProducerType.SINGLE,
                waitStrategy);

        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        for (int eventCount = 0; eventCount < 32; eventCount++) {
            long sequenceId = ringBuffer.next();
            ValueEvent valueEvent = ringBuffer.get(sequenceId);
            valueEvent.setValue(eventCount);
            ringBuffer.publish(sequenceId);
            LOG.info("Publishing eventCount [{}] sequenceId [{}]", eventCount, sequenceId);
        }

        LOG.info("Buffer size [{}]", ringBuffer.getBufferSize());
    }
}
