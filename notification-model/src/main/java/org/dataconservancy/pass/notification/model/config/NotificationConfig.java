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
package org.dataconservancy.pass.notification.model.config;

import org.dataconservancy.pass.notification.model.Notification;
import org.dataconservancy.pass.notification.model.config.smtp.SmtpServerConfig;
import org.dataconservancy.pass.notification.model.config.template.TemplatePrototype;

import java.util.Map;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NotificationConfig {

    /**
     * Mode of Notification Services (e.g. "disabled", "demo", "production")
     */
    private String mode;

    /**
     * Each Notification type has a set of templates
     */
    private Map<Notification.Type, TemplatePrototype> templates;

    /**
     * Each Notification Service mode has a recipientConfig (makes the most sense for "demo" mode)
     */
    private Map<Mode.MODE, RecipientConfig> recipientConfigs;

    /**
     * Global Notification Service SMTP server configuration
     */
    private SmtpServerConfig smtpConfig;

}
