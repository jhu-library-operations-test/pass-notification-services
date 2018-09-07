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
import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.model.User;
import org.dataconservancy.pass.notification.dispatch.DispatchService;
import org.dataconservancy.pass.notification.model.Notification;
import org.dataconservancy.pass.notification.model.config.NotificationConfig;
import org.dataconservancy.pass.notification.model.config.template.TemplatePrototype;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class EmailDispatchImpl implements DispatchService {

    private NotificationConfig notificationConfig;

    private PassClient passClient;

    private TemplateResolver templateResolver;

    private ObjectMapper mapper;

    private Handlebars handlebars;

    private Mailer mailer;

    public EmailDispatchImpl(NotificationConfig notificationConfig, PassClient passClient,
                             TemplateResolver templateResolver, ObjectMapper mapper, Handlebars handlebars,
                             Mailer mailer) {
        this.notificationConfig = notificationConfig;
        this.passClient = passClient;
        this.templateResolver = templateResolver;
        this.mapper = mapper;
        this.handlebars = handlebars;
        this.mailer = mailer;
    }

    @Override
    public void dispatch(Notification notification) {

        Notification.Type notificationType = notification.getType();

        // resolve templates for subject, body, footer based on notification type

        TemplatePrototype template = notificationConfig.getTemplates().stream()
                .filter(candidate -> candidate.getNotificationType() == notificationType)
                .findAny()
                .orElseThrow(() ->
                        new RuntimeException("Missing notification template for mode '" + notificationType + "'"));

        Map<TemplatePrototype.Name, InputStream> templates =
            template.getRefs()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> templateResolver.resolve(entry.getKey(), entry.getValue())));

        // perform pararmeterization on all templates

        Map<TemplatePrototype.Name, String> parameterized =
                templates.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            InputStream in = entry.getValue();
                            try {
                                Map<Notification.Param, String> paramMap = notification.getParameters();
                                String model = StringEscapeUtils.unescapeJson(mapper.writeValueAsString(paramMap));
                                Template t = handlebars.compileInline(IOUtils.toString(in, "UTF-8"));
                                return t.apply(model);
                            } catch (IOException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        }));

        // compose email

        String emailToAddress = String.join(",", parseRecipientUris(notification.getRecipients()
                .stream().map(URI::create).collect(Collectors.toSet()), passClient));

        Email email = EmailBuilder.startingBlank()
                .from(notification.getSender())
                .to(emailToAddress)
                .cc(String.join(",", notification.getCc()))
                .withSubject(parameterized.getOrDefault(TemplatePrototype.Name.SUBJECT, ""))
                .withPlainText(String.join("\n\n",
                        parameterized.getOrDefault(TemplatePrototype.Name.BODY, ""),
                        parameterized.getOrDefault(TemplatePrototype.Name.FOOTER, "")))
                .buildEmail();

        // send email

        mailer.sendMail(email);

    }

    static Collection<String> parseRecipientUris(Collection<URI> recipientUris, PassClient passClient) {
        return recipientUris.stream().map(recipientUri -> {
            if (recipientUri.getScheme().equalsIgnoreCase("mailto")) {
                String to = recipientUri.getSchemeSpecificPart();
                int i;
                if ((i = to.indexOf('?')) > -1) {
                    to = to.substring(0, i);
                }
                return to;
            }

            User u = passClient.readResource(recipientUri, User.class);
            return u.getEmail();
        }).collect(Collectors.toSet());
    }
}
