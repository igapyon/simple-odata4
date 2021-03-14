package jp.igapyon.simpleodata4.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.junit.jupiter.api.Test;

class DataVariationTest {
    @Test
    void test01() throws Exception {
        final OData odata = OData.newInstance();

        // EdmProvider を登録.
        final ServiceMetadata edm = odata.createServiceMetadata(SimpleEdmProvider.getInstance(), new ArrayList<>());
        final ODataHttpHandler handler = odata.createHandler(edm);

        // EntityCollectionProcessor を登録.
        handler.register(new SimpleEntityCollectionProcessor());

        final ODataRequest request = new ODataRequest();
        request.setMethod(HttpMethod.GET);
        request.setRawBaseUri("http://localhost:8080/simple.svc");
        request.setRawODataPath("/MyProducts");
        request.setRawQueryPath("$top=6&$search=macbook&$count=true&$select=ID");
        request.setRawRequestUri(request.getRawBaseUri() + request.getRawODataPath() + "?" + request.getRawQueryPath());
        ODataResponse response = handler.process(request);
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"@odata.context\":\"$metadata#MyProducts\",\"value\":[{\"ID\":1},{\"ID\":2}]}",
                stream2String(response.getContent()));
    }

    private static String stream2String(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"))) {
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
