/*
 * Copyright (C) 2015
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.core.common.ws.transport.http.ntlm;

import org.cleverbus.core.common.ws.transport.http.CloseableHttpComponentsMessageSender;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.util.Assert;

/**
 * {@code WebServiceMessageSender} implementation that uses <a href="http://hc.apache.org/httpcomponents-client">Apache
 * HttpClient</a> ({@link CloseableHttpClient}) to execute POST requests.
 * <p/>
 * Allows to use a pre-configured HttpClient instance, potentially with authentication, HTTP connection pooling, etc.
 * with NTLM authentication support.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
public class NtlmCloseableHttpComponentsMessageSender extends CloseableHttpComponentsMessageSender {

    private String ntlmUsername;
    private String ntlmPassword;
    private String ntlmDomain;

    /**
     * Create a new instance of the {@code HttpClientMessageSender} with a default {@link HttpClient}
     * with added support for NTLM.
     *
     * @param ntlmUsername the NTLM username (without domain name)
     * @param ntlmPassword the NTLM password
     * @param ntlmDomain   the NTLM domain
     */
    public NtlmCloseableHttpComponentsMessageSender(String ntlmUsername, String ntlmPassword, String ntlmDomain) {
        super();

        Assert.hasText(ntlmUsername, "the ntlmUsername must not be empty");
        Assert.hasText(ntlmPassword, "the ntlmPassword must not be empty");
        Assert.hasText(ntlmDomain, "the ntlmDomain must not be empty");

        this.ntlmUsername = ntlmUsername;
        this.ntlmPassword = ntlmPassword;
        this.ntlmDomain = ntlmDomain;

        // Register NTLMSchemeFactory with the HttpClient instance you want to NTLM enable.
        Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                .build();
        getClientBuilder().setDefaultAuthSchemeRegistry(authSchemeRegistry);

        NTCredentials credentials = new NTCredentials(ntlmUsername, ntlmPassword, MACHINE_NAME, ntlmDomain);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        setCredentials(credentials);
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        // register NTLM to HttpClient
        setCredentialsProvider(credentialsProvider);

    }

    /**
     * Gets the NTLM username.
     * @return the NTLM username
     */
    public String getNtlmUsername() {
        return ntlmUsername;
    }

    /**
     * Gets the NTLM password.
     * @return NTLM password
     */
    public String getNtlmPassword() {
        return ntlmPassword;
    }

    /**
     * Gets the NTLM domain.
     * @return NTLM domain
     */
    public String getNtlmDomain() {
        return ntlmDomain;
    }
}