package org.ubimix.commons.templates.stringtemplate;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.stringtemplate.v4.ST;
import org.ubimix.commons.templates.ITemplateProcessor;
import org.ubimix.commons.templates.TemplateException;
import org.ubimix.commons.templates.providers.StringBasedResourceProvider;
import org.ubimix.commons.templates.stringtemplate.ProviderBasedSTGroup;
import org.ubimix.commons.templates.stringtemplate.StringtemplateProcessor;

/**
 * 
 */

/**
 * @author kotelnikov
 */
public class StringTemplateTest extends TestCase {

    /**
     * @param name
     */
    public StringTemplateTest(String name) {
        super(name);
    }

    public void test() {
        ST hello = new ST("Hello, <name>");
        hello.add("name", "World");
        String result = hello.render();
        assertEquals("Hello, World", result);

        ST st = new ST("<b>$u.id$</b>: $u.name$", '$', '$');
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "123");
        map.put("name", "John Smith");
        st.add("u", map);
        result = st.render();
        assertEquals("<b>123</b>: John Smith", result);

    }

    public void testGroups() throws Exception {
        {
            StringBasedResourceProvider provider = new StringBasedResourceProvider();
            provider.register("toto.st", "toto(name) ::= <<Hello, $name$!>>");
            testGroups(provider, "toto", "Hello, World!", "name", "World");
        }

        {
            StringBasedResourceProvider provider = new StringBasedResourceProvider();
            provider.register("page.st", "page(content) ::= <<\n"
                + "<html>\n"
                + "<body>\n"
                + "$searchbox()$\n"
                + "$content$\n"
                + "</body>\n"
                + "</html>\n"
                + ">>\n");
            provider
                .register(
                    "searchbox.st",
                    "searchbox() ::= \"<form method=get action=/search><input /></form>\"");
            String control = ""
                + "<html>\n"
                + "<body>\n"
                + "<form method=get action=/search><input /></form>\n"
                + "Hello, World!\n"
                + "</body>\n"
                + "</html>";
            testGroups(provider, "page", control, "content", "Hello, World!");
        }
    }

    private void testGroups(
        StringBasedResourceProvider provider,
        String templateName,
        String control,
        Object... params) throws TemplateException {
        ProviderBasedSTGroup group = new ProviderBasedSTGroup(
            "test",
            provider,
            '$',
            '$');
        ST template = group.getInstanceOf(templateName);
        HashMap<String, Object> mapParams = new HashMap<String, Object>();
        for (int i = 0; i < params.length;) {
            String key = params[i++] + "";
            Object value = i < params.length ? params[i++] : null;
            template.add(key, value);
            mapParams.put(key, value);
        }
        String test = template.render();
        assertEquals(control, test);

        Properties properties = new Properties();
        ITemplateProcessor processor = new StringtemplateProcessor(
            provider,
            properties);
        StringWriter writer = new StringWriter();
        processor.render(templateName, mapParams, writer);
        writer.flush();
        assertEquals(control, writer.toString());
    }
}
