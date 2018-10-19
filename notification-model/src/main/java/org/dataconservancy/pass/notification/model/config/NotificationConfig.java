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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dataconservancy.pass.notification.model.Notification;
import org.dataconservancy.pass.notification.model.config.smtp.SmtpServerConfig;
import org.dataconservancy.pass.notification.model.config.template.TemplatePrototype;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NotificationConfig {

    /**
     * Runtime mode of Notification Services (e.g. "disabled", "demo", "production")
     */
    private Mode mode;

    /**
     * Each Notification type has a set of templates
     */
    private Collection<TemplatePrototype> templates;

    /**
     * Each Notification Service mode has a recipientConfig
     */
    @JsonProperty("recipient-config")
    private Collection<RecipientConfig> recipientConfigs;

    /**
     * Global Notification Service SMTP server configuration
     */
    @JsonProperty("smtp")
    private SmtpServerConfig smtpConfig;
    
    /** 
     * User invitation token encryption key.
     */
    @JsonProperty("user-token-generator")
    private UserTokenGeneratorConfig tokenConfig;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Collection<TemplatePrototype> getTemplates() {
        return templates;
    }

    public void setTemplates(Collection<TemplatePrototype> templates) {
        this.templates = templates;
    }

    public Collection<RecipientConfig> getRecipientConfigs() {
        return recipientConfigs;
    }

    public void setRecipientConfigs(Collection<RecipientConfig> recipientConfigs) {
        this.recipientConfigs = recipientConfigs;
    }

    public SmtpServerConfig getSmtpConfig() {
        return smtpConfig;
    }

    public void setSmtpConfig(SmtpServerConfig smtpConfig) {
        this.smtpConfig = smtpConfig;
    }
    
    public UserTokenGeneratorConfig getUserTokenGeneratorConfig() {
        return tokenConfig;
    }
    
    public void setUserTokenGeneratorConfig(UserTokenGeneratorConfig config) {
        this.tokenConfig = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NotificationConfig that = (NotificationConfig) o;
        return Objects.equals(mode, that.mode) &&
                Objects.equals(templates, that.templates) &&
                Objects.equals(recipientConfigs, that.recipientConfigs) &&
                Objects.equals(smtpConfig, that.smtpConfig) &&
                Objects.equals(tokenConfig, that.tokenConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, templates, recipientConfigs, smtpConfig, tokenConfig);
    }

    @Override
    public String toString() {
        return "NotificationConfig{" +
                "mode='" + mode + '\'' +
                ", templates=" + templates +
                ", recipientConfigs=" + recipientConfigs +
                ", smtpConfig=" + smtpConfig +
                ", tokenConfig=" + tokenConfig +
                '}';
    }
}
