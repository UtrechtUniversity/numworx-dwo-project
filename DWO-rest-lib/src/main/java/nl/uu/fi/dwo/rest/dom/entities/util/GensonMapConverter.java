/** Copyrighted Jan 31, 2018 */
package nl.uu.fi.dwo.rest.dom.entities.util;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author plas0006
 */
public class GensonMapConverter  implements Converter<Map<String,String>> {

    @Override
    public void serialize(Map map, ObjectWriter writer, Context ctx) throws Exception {
        writer.beginObject();
        map.forEach((k,v)->writer.writeString((String) k,(String) v));
    writer.endObject();
    }

    /**
     *
     * @param object
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,String> deserialize(ObjectReader object, Context ctx) throws Exception {
    Map<String,String> map = new HashMap<>();
    object.beginObject();
    while (object.hasNext()) {
      object.next();
      map.put(object.name(),object.valueAsString());
    }

    object.endObject();
    return map;
    }
    
}
