package nl.ulso.sprox.impl;

import nl.ulso.sprox.XmlProcessorException;

/**
 * Internal run-time exception used to simplify coding within the implementation.
 */
final class UncheckedXmlProcessorException extends RuntimeException {
    private final XmlProcessorException exception;

    private UncheckedXmlProcessorException(XmlProcessorException exception) {
        this.exception = exception;
    }

    static UncheckedXmlProcessorException unchecked(XmlProcessorException exception) {
        return new UncheckedXmlProcessorException(exception);
    }

    XmlProcessorException checked() throws XmlProcessorException {
        throw exception;
    }
}
