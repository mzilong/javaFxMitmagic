package sample.event;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.*;

/**
 * Event Dispatch bus,
 * For details, please refer to {@link javafx.scene.Node} event dispatch.
 *
 * Use example similar to EventBus：
 * <ul>
 * <li>Usually in the initialization needs to meet <font color="#1E90FF;"> {@code public xxclass implements EventHandler<ControllerEvent>}</font> add monitoring events at the beginning:<br/>
 * <b>
 *     <font color="#1E90FF;"> {@code FxEventBus.getDefault().addEventHandler(BaseEvent.BASE_EVENT, this);}</font>
 * </b>
 * <li>trigger listening event:<br/>
 * <b>
 *     <font color="#1E90FF;"> {@code FxEventBus.getDefault().fireEvent(new BaseEvent(BaseEvent.BASE_EVENT,"event between different controllers"));}</font>
 * </b>
 * <li>remove the listener event at the end:<br/>
 * <b>
 *     <font color="#1E90FF;"> {@code FxEventBus.getDefault().removeEventHandler(BaseEvent.BASE_EVENT, this);}</font>
 * </b>
 * </ul>
 *
 * @author mzl
 * @date Create in  2019/6/21 0021 16:41
 * @description
 */
public class FxEventBus implements EventTarget {
    static volatile FxEventBus defaultInstance;


    public static FxEventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (FxEventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new FxEventBus();
                }
            }
        }
        return defaultInstance;
    }
    public FxEventBus() {
        initializeInternalEventDispatcher();
    }

    /**
     * Specifies the event dispatcher for this controller. The default event
     * dispatcher sends the received events to the registered event handlers and
     * filters. When replacing the value with a new {@code EventDispatcher},
     * the new dispatcher should forward events to the replaced dispatcher
     * to maintain the node's default event handling behavior.
     */
    private ObjectProperty<EventDispatcher> eventDispatcher;

    public final void setEventDispatcher(EventDispatcher value) {
        eventDispatcherProperty().set(value);
    }

    public final EventDispatcher getEventDispatcher() {
        return eventDispatcherProperty().get();
    }

    public final ObjectProperty<EventDispatcher> eventDispatcherProperty() {
        return eventDispatcher;
    }

    private BaseEventDispatcher internalEventDispatcher;

    /**
     * Registers an event handler to this controller. The handler is called when the
     * controller receives an {@code Event} of the specified type during the bubbling
     * phase of event delivery.
     *
     * @param <T> the specific event class of the handler
     * @param eventType the type of the events to receive by the handler
     * @param eventHandler the handler to register
     * @throws NullPointerException if the event type or handler is null
     */
    public final <T extends Event> void addEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher().getEventHandlerManager()
                .addEventHandler(eventType, eventHandler);
    }

    /**
     * Unregisters a previously registered event handler from this node. One
     * handler might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the handler.
     *
     * @param <T> the specific event class of the handler
     * @param eventType the event type from which to unregister
     * @param eventHandler the handler to unregister
     * @throws NullPointerException if the event type or handler is null
     */
    public final <T extends Event> void removeEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher()
                .getEventHandlerManager()
                .removeEventHandler(eventType, eventHandler);
    }

    /**
     * Registers an event filter to this controller. The filter is called when the
     * controller receives an {@code Event} of the specified type during the capturing
     * phase of event delivery.
     *
     * @param <T> the specific event class of the filter
     * @param eventType the type of the events to receive by the filter
     * @param eventFilter the filter to register
     * @throws NullPointerException if the event type or filter is null
     */
    public final <T extends Event> void addEventFilter(
            final EventType<T> eventType,
            final EventHandler<? super T> eventFilter) {
        getInternalEventDispatcher().getEventHandlerManager()
                .addEventFilter(eventType, eventFilter);
    }

    /**
     * Unregisters a previously registered event filter from this node. One
     * filter might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the filter.
     *
     * @param <T> the specific event class of the filter
     * @param eventType the event type from which to unregister
     * @param eventFilter the filter to unregister
     * @throws NullPointerException if the event type or filter is null
     */
    public final <T extends Event> void removeEventFilter(
            final EventType<T> eventType,
            final EventHandler<? super T> eventFilter) {
        getInternalEventDispatcher().getEventHandlerManager()
                .removeEventFilter(eventType, eventFilter);
    }

    /**
     * Sets the handler to use for this event type. There can only be one such handler
     * specified at a time. This handler is guaranteed to be called as the last, after
     * handlers added using {@link #addEventHandler(EventType, EventHandler)}.
     * This is used for registering the user-defined onFoo event handlers.
     *
     * @param <T> the specific event class of the handler
     * @param eventType the event type to associate with the given eventHandler
     * @param eventHandler the handler to register, or null to unregister
     * @throws NullPointerException if the event type is null
     */
    public final <T extends Event> void setEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher().getEventHandlerManager()
                .setEventHandler(eventType, eventHandler);
    }

    private BaseEventDispatcher getInternalEventDispatcher() {
        return internalEventDispatcher;
    }

    private void initializeInternalEventDispatcher() {
        if (internalEventDispatcher == null) {
            internalEventDispatcher = new BaseEventDispatcher(this);
            eventDispatcher = new SimpleObjectProperty<>(
                    this,
                    "eventDispatcher",
                    internalEventDispatcher);
        }
    }

    /**
     * Construct an event dispatch chain for this node. The event dispatch chain
     * contains all event dispatchers from the stage to this node.
     *
     * @param tail the initial chain to build from
     * @return the resulting event dispatch chain for this node
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(
            EventDispatchChain tail) {
        if (eventDispatcher != null) {
            final EventDispatcher eventDispatcherValue = eventDispatcher.get();
            if (eventDispatcherValue != null) {
                tail = tail.prepend(eventDispatcherValue);
            }
        }
        return tail;
    }

    /**
     * Fires the specified event. By default the event will travel through the
     * hierarchy from the stage to this controller. Any event filter encountered will
     * be notified and can consume the event. If not consumed by the filters,
     * the event handlers on this controller are notified. If these don't consume the
     * event either, the event will travel back the same path it arrived to
     * this controller. All event handlers encountered are called and can consume the
     * event.
     * <p>
     * This method must be called on the FX user thread.
     *
     * @param event the event to fire
     */
    public final void fireEvent(Event event) {
        Event.fireEvent(this, event);
    }
}
