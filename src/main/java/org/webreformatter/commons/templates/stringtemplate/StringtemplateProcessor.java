/**
 * Copyright (c) 2006-2009, NEPOMUK Consortium
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the 
 * 	documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of the NEPOMUK Consortium nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 * 	this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 **/
package org.webreformatter.commons.templates.stringtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.webreformatter.commons.templates.ITemplateProcessor;
import org.webreformatter.commons.templates.ITemplateProvider;
import org.webreformatter.commons.templates.TemplateException;
import org.webreformatter.commons.templates.TemplateProcessor;

/**
 * This is a Stringtemplate-based ({@linkplain link
 * http://www.stringtemplate.org/}) implementation of the
 * {@link ITemplateProcessor} interface.
 * 
 * @author kotelnikov
 */
public class StringtemplateProcessor extends TemplateProcessor {

    private StringTemplateGroup fGroup;

    /**
     * @param templateProvider
     * @param properties
     * @param logSystem
     * @throws TemplateException
     */
    public StringtemplateProcessor(
        final ITemplateProvider templateProvider,
        Properties properties) throws TemplateException {
        super(templateProvider, properties);
        StringTemplateLoader loader = new StringTemplateLoader();
        Class<DefaultTemplateLexer> lexer = DefaultTemplateLexer.class;
        fGroup = new StringTemplateGroup("root", null, lexer) {
            @Override
            public String getFileNameFromTemplateName(String templateName) {
                return templateName;
            }

            @Override
            protected StringTemplate loadTemplateFromBeneathRootDirOrCLASSPATH(
                String fileName) {
                try {
                    InputStream input = templateProvider.getTemplate(fileName);
                    if (input == null) {
                        return null;
                    }
                    BufferedReader br = new BufferedReader(
                        getInputStreamReader(input));
                    try {
                        StringTemplate template = loadTemplate(name, br);
                        return template;
                    } finally {
                        br.close();
                    }
                } catch (IOException ioe) {
                    error("Problem reading template file: " + fileName, ioe);
                    return null;
                }
            }
        };
    }

    /**
     * @see com.cogniumsystems.commons.templates.ITemplateProcessor#close()
     */
    public void close() {
        fGroup = null;
    }

    /**
     * @see com.cogniumsystems.commons.templates.ITemplateProcessor#render(java.lang.String,
     *      java.util.Map, java.io.Writer)
     */
    public void render(
        String templateName,
        Map<String, Object> params,
        Writer writer) throws TemplateException {
        try {
            StringTemplate template = fGroup.lookupTemplate(null, templateName);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                template.setAttribute(name, value);
            }
            String result = template.toString();
            writer.write(result);
        } catch (Exception e) {
            throw new TemplateException(e);
        }
    }

}
