/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.util.List;
import java.util.Map;

import com.vmware.vip.common.utils.LocaleUtils;

/**
 * This class represents a wrapper DTO which contains the updated translation DTO
 *
 */
public class UpdateTranslationDTO {

    // Indicate where the updated translation comes from
    private String requester = "";

    // a DTO contains the updated translation
    private UpdateTranslationDataDTO data;

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public UpdateTranslationDataDTO getData() {
        return data;
    }

    public void setData(UpdateTranslationDataDTO data) {
        this.data = data;
    }

    /**
     * This class represents the update DTO which contains the updated translation DTO lists,
     * include all components.
     *
     */
    public static class UpdateTranslationDataDTO extends BaseDTO {
        // creation information
        private CreationDTO creation;

        // A translation collection which contains each component's translation
        private List<TranslationDTO> translation;

        public CreationDTO getCreation() {
            return creation;
        }

        public void setCreation(CreationDTO creation) {
            this.creation = creation;
        }

        public List<TranslationDTO> getTranslation() {
            return translation;
        }

        public void setTranslation(List<TranslationDTO> translation) {
            this.translation = translation;
        }

        /**
         * This class represents translation DTO which contains one component's translation.
         *
         */
        public static class TranslationDTO {
            private String component = "";
            private String locale = "";
            private Map<String, String> messages;

            public String getComponent() {
                return component;
            }

            public void setComponent(String component) {
                this.component = component;
            }

            public String getLocale() {
                return locale;
            }

            public void setLocale(String locale) {
                this.locale = LocaleUtils.normalizeToLanguageTag(locale);
            }

            public Map<String, String> getMessages() {
                return messages;
            }

            public void setMessages(Map<String, String> messages) {
                this.messages = messages;
            }
        }

        /**
         * This class represents creation DTO which contains creation information.
         *
         */
        public static class CreationDTO {
            private String operationid = "";

            public String getOperationid() {
                return operationid;
            }

            public void setOperationid(String operationid) {
                this.operationid = operationid;
            }
        }
    }

}
