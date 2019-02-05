import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;

public class DisruptorRunner {

    private static Logger LOG = LogManager.getLogger(DisruptorRunner.class);

    public static void main(String[] args) {
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;

        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<ValueEvent> disruptor
                = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY,
                                            32,
                                            threadFactory,
                                            ProducerType.SINGLE,
                                            waitStrategy);

        disruptor.handleEventsWith(new SingleEventPrintConsumer().getEventHandler());


        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        StopWatch stopWatch = new StopWatch( );
        stopWatch.start();

        // Loop a bunch of times to simulate a stream of events coming in.
        for (int eventCount = 0; eventCount < 200000; eventCount++) {

            long nextSequence = ringBuffer.next();
            ValueEvent valueEvent = ringBuffer.get(nextSequence);
            valueEvent.setValue(eventCount);
            ringBuffer.publish(nextSequence);

            LOG.info("Publishing eventCount [{}] sequence [{}]", eventCount, nextSequence);
        }

        stopWatch.stop();

        LOG.info("Elapsed time [{}]", stopWatch.getTime());
    }
}
