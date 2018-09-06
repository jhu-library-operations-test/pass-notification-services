/*
 * Copyright 2018 Johns Hopkins University
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
 * limitations under the License.
 */
package org.dataconservancy.pass.notification.dispatch.impl.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class CompositeResolver implements TemplateResolver {

    private static Logger LOG = LoggerFactory.getLogger(CompositeResolver.class);

    private List<TemplateResolver> resolvers;

    public CompositeResolver(List<TemplateResolver> resolvers) {
        Objects.requireNonNull(resolvers, "Template resolvers must not be null");
        this.resolvers = resolvers;
    }

    @Override
    public InputStream resolve(String template) {
        InputStream in = null;
        List<Exception> loggedEx = new ArrayList<>(2);
        for (TemplateResolver resolver : resolvers) {
            try {
                in = resolver.resolve(template);
            } catch (Exception e) {
                LOG.debug("Unable to resolve template {}: " + e.getMessage(), e);
                loggedEx.add(e);
            }
        }

        if (in == null) {
            StringBuilder msg = new StringBuilder("Unable to resolve template '" + template + "':\n");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintStream ps = new PrintStream(baos)) {
                loggedEx.forEach(ex -> ex.printStackTrace(ps));
            }

            msg.append(new String(baos.toByteArray()));

            throw new RuntimeException(msg.toString());
        }

        return in;
    }
}