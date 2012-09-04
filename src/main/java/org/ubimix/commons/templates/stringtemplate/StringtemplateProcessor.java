/**
 * Copyright (c) 2006-2009, NEPOMUK Consortium All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the NEPOMUK Consortium nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission. THIS SOFTWARE
 * IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package org.ubimix.commons.templates.stringtemplate;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.stringtemplate.v4.NoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STWriter;
import org.ubimix.commons.templates.ITemplateProcessor;
import org.ubimix.commons.templates.ITemplateProvider;
import org.ubimix.commons.templates.TemplateException;
import org.ubimix.commons.templates.TemplateProcessor;

/**
 * This is a Stringtemplate-based ({@linkplain link
 * http://www.stringtemplate.org/}) implementation of the
 * {@link ITemplateProcessor} interface.
 * 
 * @author kotelnikov
 */
public class StringtemplateProcessor extends TemplateProcessor {

    private ProviderBasedSTGroup fGroup;

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
        String name = getString("rootName", "root");
        char delimiterStartChar = getChar("delimiterStartChar", '$');
        char delimiterStopChar = getChar("delimiterStopChar", '$');
        fGroup = new ProviderBasedSTGroup(
            name,
            templateProvider,
            delimiterStartChar,
            delimiterStopChar);
    }

    /**
     * @see com.cogniumsystems.commons.templates.ITemplateProcessor#close()
     */
    @Override
    public void close() {
        fGroup = null;
    }

    private char getChar(String key, char defaultValue) {
        char result = defaultValue;
        String str = getString(key, defaultValue + "");
        if (str != null) {
            str = str.trim();
            result = str.charAt(0);
        }
        return result;
    }

    private String getString(String key, String defaultValue) {
        Object value = fProperties.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value + "";
    }

    /**
     * @see com.cogniumsystems.commons.templates.ITemplateProcessor#render(java.lang.String,
     *      java.util.Map, java.io.Writer)
     */
    @Override
    public void render(
        String templateName,
        Map<String, Object> params,
        Writer writer) throws TemplateException {
        try {
            ST template = fGroup.getInstanceOf(templateName);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                template.add(name, value);
            }
            STWriter out = new NoIndentWriter(writer);
            template.write(out);
            writer.flush();
        } catch (Exception e) {
            throw new TemplateException(e);
        }
    }

}
