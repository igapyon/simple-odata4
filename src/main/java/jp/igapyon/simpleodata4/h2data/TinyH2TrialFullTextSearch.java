package jp.igapyon.simpleodata4.h2data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

public class TinyH2TrialFullTextSearch {
    public void process(Connection conn, EdmEntitySet edmEntitySet, UriInfo uriInfo, EntityCollection eCollection) {
        try {
            SearchOptionImpl searchOpt = (SearchOptionImpl) uriInfo.getSearchOption();

            // 想定: $top=6&$search=増産&$count=true&$select=ID
            // http://localhost:8080/simple.svc/MyProducts?$search=PixelSense&$count=true&$select=ID
            // ただしh2は日本語ダメかも。

            int topValue = 100;
            if (uriInfo.getTopOption() != null) {
                topValue = uriInfo.getTopOption().getValue();
            }
            int offsetValue = 0;
            if (uriInfo.getSkipOption() != null) {
                offsetValue = uriInfo.getSkipOption().getValue();
            }

            try (PreparedStatement stmt = conn
                    .prepareStatement("SELECT QUERY,SCORE FROM FT_SEARCH(?, " + topValue + ", " + offsetValue + ")")) {
                System.err.println("TRACE: FT_SEARCH(?," + topValue + "," + offsetValue + "): " + searchOpt.getText());

                stmt.setString(1, searchOpt.getText());
                ResultSet rset = stmt.executeQuery();
                for (; rset.next();) {
                    String valQuery = rset.getString(1);
                    // System.err.println("QUERY:" + valQuery);
                    if (valQuery.contains("MyProducts") == false) {
                        continue;
                    }

                    final Entity ent = new Entity();

                    // TODO たぶんこれだとだめ。検索結果のIDから、select から与えられた指定の項目を取る必要あり。
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT ID FROM " + valQuery)) {
                        ResultSet rset2 = stmt2.executeQuery();
                        // TODO 戻り値チェック.
                        rset2.next();

                        ent.addProperty( //
                                new Property(null, "ID", ValueType.PRIMITIVE, //
                                        rset2.getInt(1)));
                        eCollection.getEntities().add(ent);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Unexpected: SQL Error:" + ex.toString(), ex);
        }
    }
}
