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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.dataconservancy.pass.notification.model.Notification;
import org.dataconservancy.pass.notification.model.config.template.TemplatePrototype;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class HandlebarsParameterizer implements TemplateParameterizer {

    private ObjectMapper mapper;

    private Handlebars handlebars;

    @Override
    public String parameterize(TemplatePrototype.Name templateName, Map<Notification.Param, String> paramMap,
                               InputStream template) {
        String parameterizedTemplate = null;
        try {
            String model = StringEscapeUtils.unescapeJson(mapper.writeValueAsString(paramMap));
            Template t = handlebars.compileInline(IOUtils.toString(template, "UTF-8"));
            parameterizedTemplate = t.apply(model);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return parameterizedTemplate;
    }
}
