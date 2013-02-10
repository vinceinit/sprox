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

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

final class AttributeParameter implements Parameter {
    private final QName name;
    private final Class type;
    private final boolean required;

    AttributeParameter(QName name, Class type, boolean required) {
        this.name = new QName(name.getLocalPart());
        this.type = type;
        this.required = required;
    }

    @Override
    public boolean isValidStartElement(StartElement node) {
        return !(node.getAttributeByName(name) == null && required);
    }

    @Override
    public void pushToExecutionContext(StartElement node, ExecutionContext context) {
        final Attribute attribute = node.getAttributeByName(name);
        if (attribute != null) {
            context.pushAttribute(name, attribute.getValue());
        }
    }

    @Override
    public Object resolveMethodParameter(ExecutionContext context) {
        final String value = context.getAttributeValue(name);
        if (value != null) {
            //noinspection unchecked
            return context.parseString(value, type);
        }
        return null;
    }

    @Override
    public boolean isRequired() {
        return required;
    }
}