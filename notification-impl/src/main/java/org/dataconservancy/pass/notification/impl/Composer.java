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
package org.dataconservancy.pass.notification.impl;

import org.apache.commons.text.StringEscapeUtils;
import org.dataconservancy.pass.model.Submission;
import org.dataconservancy.pass.model.SubmissionEvent;
import org.dataconservancy.pass.notification.model.Notification;
import org.dataconservancy.pass.notification.model.SimpleNotification;
import org.dataconservancy.pass.notification.model.config.NotificationConfig;
import org.dataconservancy.pass.notification.model.config.RecipientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class Composer implements BiFunction<Submission, SubmissionEvent, Notification> {

    private static final Logger LOG = LoggerFactory.getLogger(Composer.class);

    private RecipientConfig recipientConfig;

    private Function<Collection<String>, Collection<String>> whitelist;

    public Composer(NotificationConfig config, Function<Collection<String>, Collection<String>> whitelist) {
        Objects.requireNonNull(config, "NotificationConfig must not be null.");
        Objects.requireNonNull(whitelist, "Whitelist must not be null.");

        this.whitelist = whitelist;

        recipientConfig = config.getRecipientConfigs().stream()
                .filter(rc -> config.getMode() == rc.getMode()).findAny()
                .orElseThrow(() ->
                        new RuntimeException("Missing recipient configuration for Mode '" + config.getMode() + "'"));
    }

    @Override
    public Notification apply(Submission submission, SubmissionEvent event) {
        SimpleNotification notification = new SimpleNotification();
        HashMap<Notification.Param, String> params = new HashMap<>();
        notification.setParameters(params);

        notification.setEventUri(event.getId());
        params.put(Notification.Param.EVENT_METADATA, "");  // TODO: invoke adapter to produce JSON representation of event?  why not just use the event URI

        Collection<String> cc = recipientConfig.getGlobalCc();
        if (cc != null && !cc.isEmpty()) {
            notification.setCc(cc);
            params.put(Notification.Param.CC, String.join(",", cc));
        }

        notification.setResourceUri(submission.getId());
        params.put(Notification.Param.RESOURCE_METADATA,
                StringEscapeUtils.UNESCAPE_JSON.translate(submission.getMetadata()));

        String from = recipientConfig.getFromAddress();
        notification.setSender(from);
        params.put(Notification.Param.FROM, from);

        switch (event.getEventType()) {
            case APPROVAL_REQUESTED_NEWUSER: {
                notification.setType(Notification.Type.SUBMISSION_APPROVAL_INVITE);
                // to: submission.getSubmitter() // the AS
                Collection<String> whitelistedRecipient =
                        whitelist.apply(singleton(submission.getSubmitter().toString()));
                notification.setRecipient(whitelistedRecipient);
                params.put(Notification.Param.TO, String.join(",", whitelistedRecipient));
                // TODO: generate invite link, attach to parameters map
                break;
            }

            case APPROVAL_REQUESTED: {
                notification.setType(Notification.Type.SUBMISSION_APPROVAL_REQUESTED);
                // to: submission.getSubmitter() // the AS
                Collection<String> whitelistedRecipient =
                        whitelist.apply(singleton(submission.getSubmitter().toString()));
                notification.setRecipient(whitelistedRecipient);
                params.put(Notification.Param.TO, String.join(",", whitelistedRecipient));
                // TODO: generate approval requested link, attach to parameters map
                break;
            }

            case CHANGES_REQUESTED: {
                notification.setType(Notification.Type.SUBMISSION_CHANGES_REQUESTED);
                // to: submission.preparers
                Collection<String> preparersAsStrings =
                        whitelist.apply(submission.getPreparers().stream().map(URI::toString).collect(toSet()));
                notification.setRecipient(preparersAsStrings);
                params.put(Notification.Param.TO, String.join(",", preparersAsStrings));
                // TODO: generate changes requested link, attach to parameters map
                break;
            }

            case SUBMITTED: {
                notification.setType(Notification.Type.SUBMISSION_SUBMISSION_SUBMITTED);
                // to: submission.preparers
                Collection<String> preparersAsStrings = whitelist.apply(
                        submission.getPreparers().stream().map(URI::toString).collect(toSet()));
                notification.setRecipient(preparersAsStrings);
                params.put(Notification.Param.TO, String.join(",", preparersAsStrings));
                // TODO: generate submission submitted link, attach to parameters map
                break;
            }

            case CANCELLED: {
                notification.setType(Notification.Type.SUBMISSION_SUBMISSION_CANCELLED);
                String performedBy = event.getPerformedBy().toString();
                Collection<String> recipients;
                if (submission.getSubmitter().toString().equals(performedBy)) {
                    recipients =
                            whitelist.apply(submission.getPreparers().stream().map(URI::toString).collect(toSet()));
                } else {
                    recipients =
                            whitelist.apply(singleton(submission.getSubmitter().toString()));
                }
                notification.setRecipient(recipients);
                params.put(Notification.Param.TO, String.join(",", recipients));
                break;
            }

            default: {
                LOG.warn("Unhandled SubmissionEvent type {}", event.getEventType());
                return null;
            }
        }

        return notification;
    }
}
