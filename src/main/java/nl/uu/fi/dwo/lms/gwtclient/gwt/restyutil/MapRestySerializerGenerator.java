package nl.uu.fi.dwo.lms.gwtclient.gwt.restyutil;

import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import java.util.Map;
import org.fusesource.restygwt.rebind.JsonEncoderDecoderClassCreator;
import org.fusesource.restygwt.rebind.RestyJsonSerializerGenerator;

/**
 *
 * @author Gert van der Plas
 */
public class MapRestySerializerGenerator implements RestyJsonSerializerGenerator {

    @Override
    public Class<? extends JsonEncoderDecoderClassCreator> getGeneratorClass() {
        return MapSerializerGenerator.class;
    }

    @Override
    public JType getType(TypeOracle typeOracle) {
        return typeOracle.findType(Map.class.getName());
    }

}