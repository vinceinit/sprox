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

package nl.ulso.sprox;

import org.junit.Test;

import static nl.ulso.sprox.SproxTests.testProcessor;
import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;

public class ExceptionalSituationsTest {

    @Test(expected = IllegalStateException.class)
    public void testThatCreatingAProcessorWithZeroControllersFails() throws Exception {
        XmlProcessorFactory.createXmlProcessorBuilder(Void.class).buildXmlProcessor();
    }

    @Test(expected = IllegalStateException.class)
    public void testThatCreatingAProcessorWithControllersWithoutAnnotationsFails() throws Exception {
        XmlProcessorFactory.createXmlProcessorBuilder(Void.class).addControllerObject("").buildXmlProcessor();
    }

    @Test(expected = XmlProcessorException.class)
    public void testThatParseExceptionResultsInFailure() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerObject(new BrokenNodeProcessor("test"))
                .addParser(new Parser<String>() {
                    @Override
                    public String fromString(String value) throws ParseException {
                        throw new ParseException(String.class, value);
                    }
                }).buildXmlProcessor();
        testProcessor("", "<root><node>value</node></root>", processor);
    }

    @Test(expected = IllegalStateException.class)
    public void testThatClassInstantiationOnAControllerWithoutNoArgConstructorFails() throws Exception {
        final XmlProcessor<String> processor = createXmlProcessorBuilder(String.class)
                .addControllerClass(BrokenNodeProcessor.class)
                .buildXmlProcessor();
        testProcessor("", "<root/>", processor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatTheAControllerClassCanBeRegisteredOnlyOnce() throws Exception {
        createXmlProcessorBuilder(String.class)
                .addControllerClass(BrokenNodeProcessor.class)
                .addControllerClass(BrokenNodeProcessor.class);
    }

    public static final class BrokenNodeProcessor {

        private final String name;

        public BrokenNodeProcessor(String name) {
            this.name = name;
        }

        @Node("root")
        public String root(String node) {
            return node;
        }

        @Node("node")
        public String node(@Node("node") String value) {
            return value;
        }
    }
}
