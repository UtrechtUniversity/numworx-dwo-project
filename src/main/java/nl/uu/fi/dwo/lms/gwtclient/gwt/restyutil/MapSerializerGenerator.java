package nl.uu.fi.dwo.lms.gwtclient.gwt.restyutil;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderInstanceLocator;

//Specify your RestyJsonSerializerGenerator custom implementation fully qualified class name 
//in the org.fusesource.restygwt.restyjsonserializergenerator configuration property in your 
//GWT module XML file. This property is multi-valued so you can add multiple custom 
//serializer generators. Example:
//
//
//    <extend-configuration-property 
//        name="org.fusesource.restygwt.restyjsonserializergenerator"
//        value="com.example.CustomRestySerializerGenerator1"/>
//    <extend-configuration-property 
//        name="org.fusesource.restygwt.restyjsonserializergenerator"
//        value="com.example.CustomRestySerializerGenerator2"/>

/**
 *
 * @author Gert van der Plas
 */
public class MapSerializerGenerator extends JsonEncoderDecoderClassCreator{
    private JClassType typeArg;
    
    public MapSerializerGenerator(TreeLogger logger, GeneratorContext context, JClassType source) {
        super(logger, context, source);
    }
    
    private JClassType getTypeArg() throws UnableToCompleteException {
        JParameterizedType parameterizedType = source.isParameterized();
        if (parameterizedType == null || parameterizedType.getTypeArgs() == null || parameterizedType.getTypeArgs().length == 0) {
            getLogger().log(ERROR, "Optional types must be parameterized.");
            throw new UnableToCompleteException();
        }
        return parameterizedType.getTypeArgs()[0];
    }
    
    @Override
    public void generate() throws UnableToCompleteException {
        locator = new JsonEncoderDecoderInstanceLocator(context, getLogger());
        generateSingleton(shortName);
        typeArg = getTypeArg();
        generateEncodeMethod();
        generateDecodeMethod();
    }
    
    private void generateEncodeMethod() throws UnableToCompleteException {
        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
        p("return \"{test}\";");
         p();
//        p("public " + JSON_VALUE_CLASS + " encode(" + source.getParameterizedQualifiedSourceName() + " value) {").i(1);
//            p("if (value == null) {").i(1);
//                p("return null;").i(-1);
//            p("}");
//            p("if (!value.isPresent()) {").i(1);
//                p("return null;").i(-1);
//            p("}");
//            p("return " + locator.encodeExpression(typeArg, "value.get()", Json.Style.DEFAULT) + ";").i(-1);
//        p("}");
//        p();
    }

    private void generateDecodeMethod() throws UnableToCompleteException {
        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
        p("return new HashMap<String,String>();");
        p();
//        p("public " + source.getName() + " decode(" + JSON_VALUE_CLASS + " value) {").i(1);
//            p("if (value == null || value.isNull() != null ) {").i(1);
//                p("return Optional.absent();").i(-1);
//            p("}");
//            p("return Optional.of(" + locator.decodeExpression(typeArg, "value", Json.Style.DEFAULT) + ");").i(-1);
//        p("}");
//        p();
    }
}
