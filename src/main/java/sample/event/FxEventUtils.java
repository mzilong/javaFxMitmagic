package sample.event;

import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用全局事件注册和触发
 */
public class FxEventUtils {
    /**
     * 触发事件
     *
     * @param baseEvent 事件对象
     */
    public static void fire(BaseEvent baseEvent) {
        Collection<EventHandler> handlers = eventListeners.getOrDefault(baseEvent.getEventType(), Collections.emptyList());
        handlers.forEach(handler -> handler.handle(baseEvent));
    }

    /**
     * 注册事件侦听
     *
     * @param eventType 事件类型
     * @param handler   侦听器
     * @param <T>       事件对象类型
     */
    public static <T extends BaseEvent> void addEventHandler(EventType<T> eventType, EventHandler<? super T>handler) {
        Collection<EventHandler> consumers = eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        if(!consumers.contains(handler)){
            consumers.add(handler);
        }
    }

    /**
     * 移除事件侦听
     *
     * @param eventType 事件类型
     * @param handler   侦听器
     * @param <T>       事件对象类型
     */
    public static <T extends BaseEvent> void removeEventHandler(EventType<T> eventType, EventHandler<? super T>handler) {
        eventListeners.computeIfPresent(eventType, (k, v) -> {
            if(v.contains(handler)){
                v.remove(handler);
            }
            return v.isEmpty() ? null : v;
        });
    }

    /**
     * 移除指定类型的所有事件侦听
     *
     * @param eventType 事件类型
     * @param <T>       事件对象类型
     */
    public static <T extends BaseEvent> void removeAllEventHandler(EventType<T> eventType) {
        Collection<EventHandler> consumers = eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        consumers.clear();
    }

    /**
     * 清空所有事件侦听
     */
    public static void clearEventHandler() {
        if(!eventListeners.isEmpty()){
            eventListeners.clear();
        }
    }

    private static final Map<EventType, Collection<EventHandler>> eventListeners = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        FxEventUtils.addEventHandler(BaseEvent.BASE_EVENT, event -> {
            System.out.println("EVENT data: " + event.getData());
        });
        EventHandler<BaseEvent> handler = event -> {
            String data = event.getData();
            System.out.println("EVENT data2: " + data);
        };
        FxEventUtils.addEventHandler(BaseEvent.BASE_EVENT, handler);

//        FxEventUtils.removeEventHandler(BaseEvent.BASE_EVENT,handler);
//        FxEventUtils.removeAllEventHandler(BaseEvent.BASE_EVENT);

        FxEventUtils.fire(new BaseEvent(BaseEvent.BASE_EVENT,BaseEvent.BASE_EVENT.getName()));
    }
}
