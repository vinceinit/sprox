/*
 * Copyright 2013 Vincent Oostindië
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

import nl.ulso.sprox.ParseException;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

final class ListParameter implements Parameter {
    private final Class elementClass;
    private final QName sourceNode;
    private final boolean required;

    ListParameter(Class elementClass, QName sourceNode, boolean required) {
        this.elementClass = elementClass;
        this.sourceNode = sourceNode;
        this.required = required;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return true;
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
    }

    @Override
    public Object resolveMethodParameter(ExecutionContext context) throws ParseException {
        return context.popMethodResults(sourceNode, elementClass);
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}
