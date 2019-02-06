import com.lmax.disruptor.WorkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcurrentWorkHandler implements WorkHandler<ValueEvent> {

    private static Logger LOG = LogManager.getLogger(SequentialWorkHandler.class);

    @Override
    public void onEvent(ValueEvent event) throws Exception {

        Thread.sleep(100);
        LOG.info("Consuming item [{}]", event.getValue());
    }
}