package jp.igapyon.simpleodata4.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        final ODataHttpHandler handler = getHandler();
        final ODataRequest req = new ODataRequest();
        req.setMethod(HttpMethod.GET);
        req.setRawBaseUri("http://localhost:8080/simple.svc");
        req.setRawODataPath("/MyProducts");
        req.setRawQueryPath("$top=6&$search=macbook&$count=true&$select=ID");
        req.setRawRequestUri(req.getRawBaseUri() + req.getRawODataPath() + "?" + req.getRawQueryPath());

        final ODataResponse resp = handler.process(req);
        assertEquals(200, resp.getStatusCode());
        assertEquals("{\"@odata.context\":\"$metadata#MyProducts\",\"value\":[{\"ID\":1},{\"ID\":2}]}",
                stream2String(resp.getContent()));
    }

    ////////////////////////////////////////////////////////
    // 以降は共通コード.

    private ODataHttpHandler getHandler() throws Exception {
        final OData odata = OData.newInstance();

        // EdmProvider を登録.
        final ServiceMetadata edm = odata.createServiceMetadata(SimpleEdmProvider.getInstance(), new ArrayList<>());
        final ODataHttpHandler handler = odata.createHandler(edm);

        // EntityCollectionProcessor を登録.
        handler.register(new SimpleEntityCollectionProcessor());
        return handler;
    }

    /**
     * InputStream を String に変換.
     * 
     * @param inStream 入力ストリーム.
     * @return 文字列.
     * @throws IOException 入出力例外が発生した場合.
     */
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
