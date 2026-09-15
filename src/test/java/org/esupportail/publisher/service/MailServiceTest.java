/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.esupportail.publisher.config.ESUPPublisherProperties;
import org.esupportail.publisher.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    private static final String FROM = "publisher@example.org";

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @Mock
    private MessageSource messageSource;

    @Mock
    private SpringTemplateEngine templateEngine;

    private MailService mailService;

    @BeforeEach
    void setUp() {
        ESUPPublisherProperties properties = new ESUPPublisherProperties();
        properties.getMail().setFrom(FROM);
        properties.getMail().setBaseUrl("https://publisher.example.org");
        mailService = spy(new MailService(properties, javaMailSender, messageSource, templateEngine));
    }

    @Test
    void sendEmailFromTemplate_ProcessesLocalizedTemplateAndDelegatesEmail() {
        User user = new User("user", "User");
        user.setEmail("recipient@example.org");
        user.setLangKey("fr-FR");
        Locale locale = Locale.forLanguageTag("fr-FR");
        when(templateEngine.process(eq("mail/custom"), org.mockito.ArgumentMatchers.any(Context.class))).thenReturn("Rendered message");
        when(messageSource.getMessage("email.custom.title", null, locale)).thenReturn("Localized subject");
        doNothing().when(mailService).sendEmail("recipient@example.org", "Localized subject", "Rendered message", false, true);

        mailService.sendEmailFromTemplate(user, "mail/custom", "email.custom.title");

        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("mail/custom"), context.capture());
        assertEquals(locale, context.getValue().getLocale());
        assertEquals(user, context.getValue().getVariable("user"));
        assertEquals("https://publisher.example.org", context.getValue().getVariable("baseUrl"));
        verify(messageSource).getMessage("email.custom.title", null, locale);
        verify(mailService).sendEmail("recipient@example.org", "Localized subject", "Rendered message", false, true);
    }

    @ParameterizedTest
    @MethodSource("templateEmailMethods")
    void templateEmailMethods_DelegateExpectedTemplateAndTitleKey(String method, String templateName, String titleKey) {
        User user = new User("user", "User");
        doNothing().when(mailService).sendEmailFromTemplate(user, templateName, titleKey);

        switch (method) {
            case "activation" -> mailService.sendActivationEmail(user);
            case "creation" -> mailService.sendCreationEmail(user);
            case "reset" -> mailService.sendPasswordResetMail(user);
            default -> throw new IllegalArgumentException("Unexpected email method: " + method);
        }

        verify(mailService).sendEmailFromTemplate(user, templateName, titleKey);
    }

    private static Stream<Arguments> templateEmailMethods() {
        return Stream.of(
            Arguments.of("activation", "mail/activationEmail", "email.activation.title"),
            Arguments.of("creation", "mail/creationEmail", "email.activation.title"),
            Arguments.of("reset", "mail/passwordResetEmail", "email.reset.title")
        );
    }

    @Test
    void sendEmailFromTemplate_NoRecipientEmail_DoesNotSendOrProcessTemplate() {
        User user = new User("user", "User");

        mailService.sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");

        verifyNoInteractions(javaMailSender, messageSource, templateEngine);
    }

    @Test
    void sendEmail_Success_DelegatesPreparedMessageToMailSender() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendEmail("recipient@example.org", "A subject", "A message", false, false);

        verify(javaMailSender).send(mimeMessage);
        assertEquals("recipient@example.org", mimeMessage.getRecipients(RecipientType.TO)[0].toString());
        assertEquals(FROM, mimeMessage.getFrom()[0].toString());
        assertEquals("A subject", mimeMessage.getSubject());
        assertEquals("A message", mimeMessage.getContent());
    }

    @Test
    void sendEmail_MailSenderFails_SwallowsException() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP unavailable")).when(javaMailSender).send(mimeMessage);

        assertDoesNotThrow(() -> mailService.sendEmail("recipient@example.org", "A subject", "A message", false, false));

        verify(javaMailSender).send(mimeMessage);
    }
}
