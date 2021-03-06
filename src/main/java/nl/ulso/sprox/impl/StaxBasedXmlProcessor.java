package nl.ulso.sprox.impl;

import nl.ulso.sprox.Parser;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static nl.ulso.sprox.impl.UncheckedXmlProcessorException.unchecked;

/**
 * Default implementation of the {@link nl.ulso.sprox.XmlProcessor} interface on top of the JDKs built-in StAX
 * parser.
 * <p>
 * On construction, a processor is initialized with an initial list of event handlers, all based on annotated
 * controller
 * methods. When the processor goes through a document, it implements the following algorithm:
 * <p>
 * <ul>
 * <li>Copy the initial list of event handlers to a new list, specifically for this execution</li>
 * <li>Go through the document, event by event</li>
 * <li>For every event, check if it matches one of the event handlers in the list, from back to front. If so:
 * <ul>
 * <li>Remove the event handler from the list.</li>
 * <li>Let the event handler process the event.</li>
 * <li>Add the resulting event handler to the back of the list, giving it the highest priority.</li>
 * </ul>
 * </li>
 * </ul>
 */
final class StaxBasedXmlProcessor<T> implements XmlProcessor<T> {
    private final Class<T> resultClass;
    private final Map<Class, ControllerProvider> controllerProviders;
    private final XMLInputFactory inputFactory;
    private final List<EventHandler> initialEventHandlers;
    private final Map<Class<?>, Parser<?>> parsers;

    StaxBasedXmlProcessor(Class<T> resultClass, Map<Class, ControllerProvider> controllerProviders,
                          List<EventHandler> eventHandlers, Map<Class<?>, Parser<?>> parsers, XMLInputFactory inputFactory) {
        this.resultClass = resultClass;
        this.controllerProviders = unmodifiableMap(new HashMap<>(controllerProviders));
        this.initialEventHandlers = unmodifiableList(new ArrayList<>(eventHandlers));
        this.parsers = unmodifiableMap(new HashMap<>(parsers));
        this.inputFactory = inputFactory;
    }

    @Override
    public T execute(Reader reader) throws XmlProcessorException {
        try {
            return processEventReader(inputFactory.createXMLEventReader(reader));
        } catch (XMLStreamException e) {
            throw new XmlProcessorException(e);
        } catch (UncheckedXmlProcessorException e) {
            throw e.checked();
        }
    }

    @Override
    public T execute(InputStream inputStream) throws XmlProcessorException {
        try {
            return processEventReader(inputFactory.createXMLEventReader(inputStream));
        } catch (XMLStreamException e) {
            throw new XmlProcessorException(e);
        } catch (UncheckedXmlProcessorException e) {
            throw e.checked();
        }
    }

    private T processEventReader(XMLEventReader eventReader) throws XMLStreamException {
        final List<EventHandler> eventHandlers = new ArrayList<>(initialEventHandlers);
        final ExecutionContext<T> context = new ExecutionContext<>(resultClass, provideControllers(), parsers);
        while (eventReader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                context.increaseDepth();
            }
            final EventHandler handler = popFirstMatchingEventHandler(eventHandlers, event, context);
            if (handler != null) {
                final EventHandler nextEventHandler = handler.process(event, context);
                eventHandlers.add(0, nextEventHandler);
            }
            if (event.isEndElement()) {
                context.decreaseDepth();
            }
        }
        eventReader.close();
        return createReturnValue(context);
    }

    private Map<Class, Object> provideControllers() {
        return controllerProviders.entrySet().stream().collect(
                HashMap::new,
                (map, entry) -> map.put(entry.getKey(), entry.getValue().getController()),
                Map::putAll
        );
    }

    private EventHandler popFirstMatchingEventHandler(List<EventHandler> eventHandlers, XMLEvent event,
                                                      ExecutionContext executionContext) {
        final Iterator<EventHandler> iterator = eventHandlers.iterator();
        while (iterator.hasNext()) {
            final EventHandler handler = iterator.next();
            if (handler.matches(event, executionContext)) {
                iterator.remove();
                return handler;
            }
        }
        return null;
    }

    private T createReturnValue(ExecutionContext<T> context) {
        final Optional<T> result = context.getResult();
        if (result.isPresent()) {
            return result.get();
        }
        if (!Void.class.equals(resultClass)) {
            throw unchecked(new XmlProcessorException("No result collected of type " + resultClass.getName()));
        }
        return null;
    }
}
