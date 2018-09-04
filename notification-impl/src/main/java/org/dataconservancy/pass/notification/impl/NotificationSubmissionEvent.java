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

import org.dataconservancy.pass.model.PassEntity;

import java.net.URI;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NotificationSubmissionEvent extends PassEntity {

    public enum Event {
        approval_requested_newuser,
        approval_requested,
        changes_requested,
        cancelled,
        submitted
    }

    private URI submissionUri;

    private Event eventType;

    private URI performedBy;

    private String performerRole;

    private String comment;


    public URI getSubmissionUri() {
        return submissionUri;
    }

    public void setSubmissionUri(URI submissionUri) {
        this.submissionUri = submissionUri;
    }

    public Event getEventType() {
        return eventType;
    }

    public void setEventType(Event eventType) {
        this.eventType = eventType;
    }

    public URI getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(URI performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformerRole() {
        return performerRole;
    }

    public void setPerformerRole(String performerRole) {
        this.performerRole = performerRole;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
