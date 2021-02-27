package jp.igapyon.simpleodata4.entity;

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
 * OData を Spring Boot の Servlet として動作.
 *
 * EdmProvider と EntityCollectionProcessor を OData に結びつけてパスに登録.
 */
@RestController
public class SimpleOdata4Register {
    /**
     * サーブレットのエントリポイント.
     * 
     * @param req  HTTPリクエスト.
     * @param resp HTTPレスポンス.
     * @throws ServletException サーブレット例外.
     */
    @RequestMapping("/simple.svc/*")
    private void serv(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {
        try {
            OData odata = OData.newInstance();

            // EdmProvider を登録.
            ServiceMetadata edm = odata.createServiceMetadata(new SimpleEdmProvider(), new ArrayList<>());
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
            System.err.println("Server Error: " + ex.toString());
            throw new ServletException(ex);
        }
    }
}