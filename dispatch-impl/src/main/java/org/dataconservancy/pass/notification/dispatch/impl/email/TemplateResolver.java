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

import java.io.InputStream;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@FunctionalInterface
public interface TemplateResolver {

    /**
     * Resolve the template.  {@code template} may be a {@code URI}, or the {@code String} itself may be an in-line
     * template.  If the former, the URI is resolved to the template content, if the latter, an InputStream is returned
     * over the inline content.
     *
     * @param template a URI for the template location, or the content of an inline template
     * @return the content of the template
     */
    InputStream resolve(String template);

}
