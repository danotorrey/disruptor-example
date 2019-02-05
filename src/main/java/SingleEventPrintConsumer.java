import com.lmax.disruptor.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleEventPrintConsumer {

    private static Logger LOG = LogManager.getLogger(SingleEventPrintConsumer.class);

    public EventHandler<ValueEvent>[] getEventHandler() {

        EventHandler<ValueEvent> eventHandler = (event, sequence, endOfBatch)
                -> print(event.getValue(), sequence);

        return new EventHandler[]{eventHandler};
    }

    private void print(int id, long sequenceId) {
        LOG.info("Id [{}] sequence id [{}]", id, sequenceId);
    }
}
