import com.lmax.disruptor.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This class consumes data from the
class SequentialWorkHandler implements EventHandler<ValueEvent> {

    private static Logger LOG = LogManager.getLogger(SequentialWorkHandler.class);

    @Override
    public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) throws Exception {

        // Sleep for a bit to simulate a delay writing to disk.
        Thread.sleep(100);
        LOG.info("Consumed object with Id [{}] sequence id [{}]", event.getValue(), sequence);
    }
}
