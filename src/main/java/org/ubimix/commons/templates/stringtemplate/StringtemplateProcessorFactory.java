/*******************************************************************************
 * Copyright (c) 2005,2006 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.ubimix.commons.templates.stringtemplate;

import java.util.Properties;

import org.ubimix.commons.templates.ITemplateProcessor;
import org.ubimix.commons.templates.ITemplateProcessorFactory;
import org.ubimix.commons.templates.ITemplateProvider;
import org.ubimix.commons.templates.TemplateException;

/**
 * The factory used to create {@link StringtemplateProcessor} instances.
 * 
 * @author kotelnikov
 */
public class StringtemplateProcessorFactory
    implements
    ITemplateProcessorFactory {

    /**
     * @see com.cogniumsystems.commons.templates.ITemplateProcessorFactory#newTemplateProcessor(org.semanticdesktop.common.ITemplateProvider.IResourceProvider,
     *      java.util.Properties)
     */
    public ITemplateProcessor newTemplateProcessor(
        ITemplateProvider templateProvider,
        Properties properties) throws TemplateException {
        return new StringtemplateProcessor(templateProvider, properties);
    }

}
