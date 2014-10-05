/*
 * Copyright 2013-2014 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox.impl;

import nl.ulso.sprox.ElementNameResolver;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Creates QNames for XML elements from controller class, method and parameter names.
 */
class QNameResolver {

    private final Class<?> controllerClass;
    private final Method method;
    private final NamespaceMap namespaceMap;
    private final ElementNameResolver elementNameResolver;

    QNameResolver(Class<?> controllerClass, Method method, NamespaceMap namespaceMap,
                  ElementNameResolver elementNameResolver) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.namespaceMap = namespaceMap;
        this.elementNameResolver = elementNameResolver;
    }

    QName createQName(String annotation) {
        final ElementReference reference = new ElementReference(annotation);
        final String namespace = reference.resolveNamespace(namespaceMap.getDefaultNamespace());
        final String localPart = reference.resolveLocalPart(
                () -> elementNameResolver.fromMethod(controllerClass, method));
        return new QName(namespace, localPart);
    }

    QName createQName(String annotation, Parameter parameter, QName ownerName) {
        final ElementReference reference = new ElementReference(annotation);
        final String namespace = reference.resolveNamespace(ownerName.getNamespaceURI());
        final String localPart = reference.resolveLocalPart(
                () -> elementNameResolver.fromParameter(controllerClass, method, parameter));
        return new QName(namespace, localPart);
    }

    /**
     * Represents a reference to an XML element, defined in an annotation value.
     * <p>
     * A reference is written as (EBNF): {@code [shorthand ":"] [name]}. In other words: it might be completely empty,
     * it might contain just a shorthand, just a name, or both.
     * </p>
     */
    private final class ElementReference {
        private static final char SEPARATOR = ':';

        private final Optional<String> shorthand;
        private final Optional<String> element;

        ElementReference(String annotation) {
            final int i = annotation.indexOf(SEPARATOR);
            if (i == -1) {
                shorthand = Optional.empty();
                element = annotation.isEmpty() ? Optional.empty() : Optional.of(annotation);
            } else {
                shorthand = Optional.of(annotation.substring(0, i));
                if (annotation.length() > i + 1) {
                    element = Optional.of(annotation.substring(i + 1));
                } else {
                    element = Optional.empty();
                }
            }
        }

        String resolveNamespace(String defaultNamespace) {
            return shorthand.map(namespaceMap::resolveNamespace).orElse(defaultNamespace);
        }

        String resolveLocalPart(Supplier<? extends String> nameResolver) {
            // By using a Supplier, execution is deferred. That means in this case that the - optionally user supplied -
            // ElementNameResolver, that could be expensive to execute, is called only when it really needs to be.
            return element.orElseGet(nameResolver);
        }
    }
}
