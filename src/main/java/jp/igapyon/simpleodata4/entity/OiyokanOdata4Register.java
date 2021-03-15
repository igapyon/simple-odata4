package jp.igapyon.simpleodata4.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OData v4 server を Spring Boot の Servlet として動作させるクラス.
 *
 * EdmProvider と EntityCollectionProcessor を OData に結びつけてパスに登録.
 */
@RestController
public class OiyokanOdata4Register {
    /**
     * OData v4 server を Spring Boot の Servlet として動作させるエントリポイント.
     * 
     * @param req  HTTPリクエスト.
     * @param resp HTTPレスポンス.
     * @throws ServletException サーブレット例外.
     */
    @RequestMapping("/simple.svc/*")
    private void serv(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {
        String uri = req.getRequestURI();
        if (req.getQueryString() != null) {
            try {
                // Query String を uri に追加.
                // TODO URLDecoder より頑丈な URI デコードの実装を探して置き換えたい.
                uri += "?" + URLDecoder.decode(req.getQueryString(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalArgumentException("デコード失敗:" + ex.toString(), ex);
            }
        }

        System.err.println("OData v4: URI: " + uri);
        try {
            OData odata = OData.newInstance();

            // EdmProvider を登録.
            ServiceMetadata edm = odata.createServiceMetadata(OiyokanEdmProvider.getInstance(), new ArrayList<>());
            ODataHttpHandler handler = odata.createHandler(edm);

            // EntityCollectionProcessor を登録.
            handler.register(new SimpleEntityCollectionProcessor());

            // Spring と Servlet の挙動を調整.
            handler.process(new HttpServletRequestWrapper(req) {
                @Override
                public String getServletPath() {
                    return "/simple.svc";
                }
            }, resp);
        } catch (RuntimeException ex) {
            System.err.println("OData v4: Unexpected Server Error: " + ex.toString());
            throw new ServletException(ex);
        }
    }
}