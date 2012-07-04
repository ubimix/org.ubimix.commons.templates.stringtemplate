import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.GroupLexer;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.misc.ErrorType;

/**
 * 
 */

/**
 * @author kotelnikov
 */
public class StringTemplateTest extends TestCase {

    private final static Logger log = Logger.getLogger(StringTemplateTest.class
        .getName());

    /**
     * @param name
     */
    public StringTemplateTest(String name) {
        super(name);
    }

    private RuntimeException handleError(String msg, Throwable e) {
        log.log(Level.FINE, msg, e);
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(msg, e);
    }

    public void test() throws Exception {
        STGroup group = new STGroup() {

            protected CompiledST getCompiledST(String name, String content) {
                GroupParser parser = null;
                try {
                    ANTLRStringStream fs = new ANTLRStringStream(content);
                    fs.name = name;
                    GroupLexer lexer = new GroupLexer(fs);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    parser = new GroupParser(tokens);
                    // no prefix since this group file is the entire group,
                    // nothing lives
                    // beneath it.
                    parser.group(this, name);
                } catch (Exception e) {
                    errMgr.IOError(
                        null,
                        ErrorType.CANT_LOAD_GROUP_FILE,
                        e,
                        "<string>");
                }
                return rawGetTemplate(name);
            }

            @Override
            protected CompiledST load(String name) {
                String content = null;
                if ("toto".equals(name)) {
                    content = "import abc; Hello $name$ !";
                } else if ("abc".equals(name)) {
                    content = "You are '$name$'!";
                }
                if (content == null) {
                    return null;
                }
                CompiledST result = getCompiledST(name, content);
                return result;
            }

            @Override
            public CompiledST loadTemplateFile(
                String arg0,
                String arg1,
                CharStream arg2) {
                // TODO Auto-generated method stub
                return super.loadTemplateFile(arg0, arg1, arg2);
            }
        };
        ST hello = group.getInstanceOf("toto");
        // "import toto; Hello, <name>");
        hello.add("name", "World");
        System.out.println(hello.render());
    }
}
