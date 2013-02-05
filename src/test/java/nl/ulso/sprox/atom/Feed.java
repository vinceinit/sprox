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

package nl.ulso.sprox.atom;

import java.util.*;

public class Feed {
    private final Text title;
    private final Text subtitle;
    private final Author author;
    private final List<Entry> entries;

    public Feed(Text title, Text subtitle, Author author, List<Entry> entries) {
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public Text getTitle() {
        return title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
