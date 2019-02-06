import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleProducerSequencer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

public class DisruptorRunner {

    public static final int RING_BUFFER_SIZE = 128;
    private static Logger LOG = LogManager.getLogger(DisruptorRunner.class);

    public static void main(String[] args) throws InterruptedException {

        // Initialize the disruptor.
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;

        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY,
                                                                    RING_BUFFER_SIZE,
                                                                    threadFactory,
                                                                    ProducerType.SINGLE,
                                                                    new BusySpinWaitStrategy());

        // Process sequentially with a single thread
        // disruptor.handleEventsWith(new SequentialWorkHandler());

        // Process in parallel with multiple threads.
        ConcurrentWorkHandler[] workHandlers = Stream.generate(ConcurrentWorkHandler::new)
                                                     .limit(10) // Thread count
                                                     .toArray(ConcurrentWorkHandler[]::new);
        disruptor.handleEventsWithWorkerPool(workHandlers);

        RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        // Time the publish/consumption.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Loop a bunch of times to simulate a stream of events coming in.
        for (int eventCount = 0; eventCount < 200; eventCount++) {

            long nextSequence = ringBuffer.next();
            ValueEvent valueEvent = ringBuffer.get(nextSequence);
            valueEvent.setValue(eventCount);
            ringBuffer.publish(nextSequence);

            LOG.info("Publishing eventCount [{}] sequence [{}]", eventCount, nextSequence);
        }

        // Wait to exit the main thread until buffer size is zero.
        while (true) {
            Thread.sleep(500);
            if (ringBuffer.remainingCapacity() != RING_BUFFER_SIZE) {
            } else {
                // Log final processing time and Terminate the main thread when done processing.
                LOG.info("** Elapsed time [{}] **", stopWatch.getTime());
                break;
            }
        }
    }
}