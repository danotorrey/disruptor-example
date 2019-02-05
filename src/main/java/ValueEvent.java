import com.lmax.disruptor.EventFactory;

/**
 * The object which is being pushed to the ring buffer.
 */
class ValueEvent {

    private int value;

    final static EventFactory EVENT_FACTORY = new EventFactory() {
        @Override
        public Object newInstance() {
            return new ValueEvent();
        }
    };

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }
}