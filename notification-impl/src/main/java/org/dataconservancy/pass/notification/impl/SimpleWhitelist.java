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

import org.dataconservancy.pass.notification.model.config.RecipientConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class SimpleWhitelist implements Function<Collection<String>, Collection<String>> {

    private RecipientConfig recipientConfig;

    public SimpleWhitelist(RecipientConfig recipientConfig) {
        this.recipientConfig = recipientConfig;
    }

    @Override
    public Collection<String> apply(Collection<String> candidates) {
        // an empty or null whitelist is carries the semantics "any recipient is whitelisted"
        if (recipientConfig.getWhitelist() == null || recipientConfig.getWhitelist().isEmpty()) {
            return candidates;
        }

        return candidates.stream()
                .filter(candidate -> isWhitelisted(candidate.toLowerCase(), recipientConfig.getWhitelist()))
                .collect(Collectors.toSet());
    }

    private static boolean isWhitelisted(String candidate, Collection<String> whitelist) {
        // an empty or null whitelist is carries the semantics "any recipient is whitelisted"
        if (whitelist == null || whitelist.isEmpty()) {
            return true;
        }

        return whitelist.stream().map(String::toLowerCase).anyMatch(candidate::equals);
    }
}
