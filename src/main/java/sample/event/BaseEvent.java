package sample.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
/**
 * Event data.
 * For details, please refer to {@link FxEventBus} event dispatch.
 * @author mzl
 * @Description BaseEvent
 * @Data  2020/10/26 17:13
 */
public class BaseEvent extends Event{
    public static final EventType<BaseEvent> BASE_EVENT = new EventType<>(Event.ANY, "BASE_EVENT");
    public static final EventType<BaseEvent> CONTROLLERACTION = new EventType<>(Event.ANY, "CONTROLLERACTION");

    private Object data;

    public BaseEvent(Object data) {
        this(BASE_EVENT,data);
    }

    public BaseEvent(EventType<? extends Event> eventType,Object data) {
        this(null,null,eventType,data);
    }

    public BaseEvent(Object source, EventTarget target, EventType<? extends Event> eventType, Object data) {
        super(source,target,eventType);
        this.data = data;
    }

    public <E> E getData() {
        return (E)data;
    }
}
