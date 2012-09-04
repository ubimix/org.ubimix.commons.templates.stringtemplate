package org.ubimix.commons.templates.stringtemplate;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.GroupLexer;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;
import org.ubimix.commons.templates.ITemplateProvider;


/**
 * @author kotelnikov
 */
public class ProviderBasedSTGroup extends STGroup {

    private String fName;

    private ITemplateProvider fProvider;

    public ProviderBasedSTGroup(
        String name,
        ITemplateProvider provider,
        char delimiterStartChar,
        char delimiterStopChar) {
        super(delimiterStartChar, delimiterStopChar);
        fName = name;
        fProvider = provider;
    }

    @Override
    public String getFileName() {
        return fName;
    }

    @Override
    public String getName() {
        return fName;
    }

    @Override
    public URL getRootDirURL() {
        return null;
    }

    @Override
    public void importTemplates(Token fileNameToken) {
        String fileName = fileNameToken.getText();
        // do nothing upon syntax error
        if (fileName == null || fileName.equals("<missing STRING>")) {
            return;
        }
        fileName = Misc.strip(fileName, 1);
        STGroup g = null;
        try {
            if (fProvider.templateExists(fileName)) {
                g = new STGroup();
                g.setListener(this.getListener());
                InputStream input = fProvider.getTemplate(fileName);
                ANTLRInputStream templateStream = new ANTLRInputStream(
                    input);
                templateStream.name = fileName;
                CompiledST code = g.loadTemplateFile(
                    "",
                    fileName,
                    templateStream);
                if (code == null) {
                    g = null;
                }
            }
        } catch (IOException ioe) {
            errMgr.internalError(null, "can't read from " + fileName, ioe);
            g = null;
        }
        if (g == null) {
            errMgr.compileTimeError(
                ErrorType.CANT_IMPORT,
                null,
                fileNameToken,
                fileName);
        } else {
            importTemplates(g);
        }
    }

    /**
     * Load a template from dir or group file. Group file is given
     * precedence over dir with same name.
     */
    @Override
    protected CompiledST load(String name) {
        String parent = Misc.getPrefix(name);
        try {
            if (fProvider.templateExists(parent + ".stg")) {
                loadGroupFile(parent, parent + ".stg");
            } else if (fProvider.templateExists(name + ".st")) {
                loadTemplateFile(parent, name + ".st");
            }
        } catch (IOException ioe) {
            errMgr.internalError(
                null,
                "can't load template file " + name,
                ioe);
        }
        return rawGetTemplate(name);
    }

    @Override
    public void loadGroupFile(String prefix, String fileName) {
        // System.out.println("load group file prefix="+prefix+", fileName="+fileName);
        GroupParser parser = null;
        try {
            InputStream input = fProvider.getTemplate(fileName);
            ANTLRInputStream fs = new ANTLRInputStream(input, encoding);
            GroupLexer lexer = new GroupLexer(fs);
            fs.name = fileName;
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new GroupParser(tokens);
            parser.group(this, prefix);
        } catch (Exception e) {
            errMgr.IOError(
                null,
                ErrorType.CANT_LOAD_GROUP_FILE,
                e,
                fileName);
        }
    }

    /** Load .st as relative file name relative to root by prefix */
    public CompiledST loadTemplateFile(String prefix, String fileName) {
        ANTLRInputStream fs;
        try {
            InputStream input = fProvider.getTemplate(fileName);
            fs = new ANTLRInputStream(input, encoding);
            fs.name = fileName;
        } catch (IOException ioe) {
            // doesn't exist
            // errMgr.IOError(null, ErrorType.NO_SUCH_TEMPLATE, ioe,
            // fileName);
            return null;
        }
        return loadTemplateFile(prefix, fileName, fs);
    }

}