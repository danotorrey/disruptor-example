import com.lmax.disruptor.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This class consumes data from the
class SingleEventPrintConsumer {

    private static Logger LOG = LogManager.getLogger(SingleEventPrintConsumer.class);

    public EventHandler<ValueEvent>[] getEventHandler() {

        // This is the event handler.
        EventHandler<ValueEvent> eventHandler = new EventHandler<ValueEvent>() {
            @Override
            public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) throws Exception {
                print(event.getValue(), sequence);
            }
        };

        return new EventHandler[]{eventHandler};
    }

    private void print(int id, long sequenceId) {
        try {
            // Sleep for a bit to simulate a delay writing to disk.
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info("Consumed object with Id [{}] sequence id [{}]", id, sequenceId);
    }
}
