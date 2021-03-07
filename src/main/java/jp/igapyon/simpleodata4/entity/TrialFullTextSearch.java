package jp.igapyon.simpleodata4.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

public class TrialFullTextSearch {
    public void process(Connection conn, EdmEntitySet edmEntitySet, UriInfo uriInfo, EntityCollection eCollection) {
        try {
            SearchOptionImpl searchOpt = (SearchOptionImpl) uriInfo.getSearchOption();
            System.err.println("Trying:$search:" + searchOpt.toString());

            // 想定: $top=6&$search=増産&$count=true&$select=ID
            // http://localhost:8080/simple.svc/MyProducts?$search=PixelSense&$count=true&$select=ID
            // ただしh2は日本語ダメかも。

            System.err.println("search:[" + searchOpt.getText() + "]");

            int topValue = 100;
            if (uriInfo.getTopOption() != null) {
                topValue = uriInfo.getTopOption().getValue();
            }
            int offsetValue = 0;
            if (uriInfo.getSkipOption() != null) {
                offsetValue = uriInfo.getSkipOption().getValue();
            }

            if (false) {
                // だめだ。java.lang.NoClassDefFoundError:
                // org/apache/lucene/index/IndexFormatTooOldException] with root cause
                ResultSet rset = org.h2.fulltext.FullTextLucene.search(conn, searchOpt.getText(), topValue,
                        offsetValue);
                ResultSetMetaData rsmeta = rset.getMetaData();
                for (; rset.next();) {
                    for (int idxColumn = 1; idxColumn <= rsmeta.getColumnCount(); idxColumn++) {
                        System.err.println(rset.getString(idxColumn));
                    }
                }
            }

            try (PreparedStatement stmt = conn
                    .prepareStatement("SELECT QUERY,SCORE FROM FT_SEARCH(?, " + topValue + ", " + offsetValue + ")")) {
                System.err.println("TRACE: FT_SEARCH(?," + topValue + "," + offsetValue + ")");
                stmt.setString(1, searchOpt.getText());
                ResultSet rset = stmt.executeQuery();
                ResultSetMetaData rsmeta = rset.getMetaData();
                for (; rset.next();) {
                    final Entity ent = new Entity();

// TODO たぶんこれだとだめ。検索結果のIDから、selectで指定の項目を取る必要あり。

                    String valQuery = rset.getString(1);
                    System.err.println("QUERY:" + valQuery);
                    String[] queryParts = valQuery.split("=");
                    System.err.println("val:" + queryParts[1]);
                    ent.addProperty( //
                            new Property(null, "ID", ValueType.PRIMITIVE, //
                                    Integer.parseInt(queryParts[1])));
                    eCollection.getEntities().add(ent);

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Unexpected: SQL Error:" + ex.toString(), ex);
        }
    }
}
