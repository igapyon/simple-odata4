package jp.igapyon.simpleodata4.entity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

/**
 * 実際に返却するデータ本体を組み上げるクラス.
 */
public class SimpleEntityDataBuilder {
    private SimpleEntityDataBuilder() {
    }

    /**
     * 指定のEDM要素セットに対応する要素コレクションを作成.
     * 
     * @param edmEntitySet EDM要素セット.
     * @return 要素コレクション.
     */
    public static EntityCollection buildData(EdmEntitySet edmEntitySet) {
        EntityCollection eCollection = new EntityCollection();

        if (!SimpleEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
            // 処理対象外の要素セットです. 処理せずに戻します.
            return eCollection;
        }

        // いくつかサンプルデータを作成.
        final Entity e1 = new Entity() //
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[0], ValueType.PRIMITIVE, 1))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[1], ValueType.PRIMITIVE, "MacBookPro16,2"))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[2], ValueType.PRIMITIVE,
                        "MacBook Pro (13-inch, 2020, Thunderbolt 3ポートx 4)"));
        e1.setId(createId(SimpleEdmProvider.ES_PRODUCTS_NAME, 1));
        eCollection.getEntities().add(e1);

        final Entity e2 = new Entity() //
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[0], ValueType.PRIMITIVE, 2))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[1], ValueType.PRIMITIVE, "MacBookPro E2015"))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[2], ValueType.PRIMITIVE,
                        "MacBook Pro (Retina, 13-inch, Early 2015)"));
        e2.setId(createId(SimpleEdmProvider.ES_PRODUCTS_NAME, 2));
        eCollection.getEntities().add(e2);

        final Entity e3 = new Entity() //
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[0], ValueType.PRIMITIVE, 3))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[1], ValueType.PRIMITIVE, "Surface Laptop 2"))
                .addProperty(new Property(null, SimpleEdmProvider.FIELDS[2], ValueType.PRIMITIVE,
                        "Surface Laptop 2, 画面:13.5 インチ PixelSense ディスプレイ, インテル Core"));
        e3.setId(createId(SimpleEdmProvider.ES_PRODUCTS_NAME, 3));
        eCollection.getEntities().add(e3);

        return eCollection;
    }

    /**
     * 与えられた情報をもとにURIを作成.
     * 
     * @param entitySetName 要素セット名.
     * @param id            ユニーク性を実現するId.
     * @return 要素セット名およびユニーク性を実現するIdをもとにつくられた部分的なURI.
     */
    public static URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException ex) {
            throw new ODataRuntimeException("Fail to create ID EntitySet name: " + entitySetName, ex);
        }
    }

    public static void buildH2database() {
        try (Connection conn = getH2Connection()) {
            try (PreparedStatement stmt = conn.prepareStatement("sql")) {

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * h2 データベースへのDB接続を取得します。
     * 
     * @return データベース接続。
     */
    public static Connection getH2Connection() {
        Connection conn;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        final var jdbcConnStr = "jdbc:h2:mem:product;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
        System.err.println("[connect jdbc] " + jdbcConnStr);
        try {
            conn = DriverManager.getConnection(//
                    jdbcConnStr, "sa", "");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }

        setupTable(conn);

        return conn;
    }

    /**
     * SQL検索プレースホルダの文字列を生成します。
     * 
     * @param count プレースホルダ数。
     * @return プレースホルダ文字列。
     */
    public static String getQueryPlaceholderString(int count) {
        String queryPlaceholder = "";
        for (int col = 0; col < count; col++) {
            if (col != 0) {
                queryPlaceholder += ",";
            }
            queryPlaceholder += "?";
        }

        return queryPlaceholder;
    }

    /**
     * 情報を格納するためのテーブルをセットアップします。
     * 
     * @param conn データベース接続。
     */
    public static void setupTable(final Connection conn) {
        System.err.println("TRACE: 作業用データベーステーブルを作成");
        /**
         * 主テーブル.
         */
        try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " //
                + "Products (" //
                + "ID VARCHAR(80) NOT NULL" //
                + ",Name VARCHAR(80)" //
                + ",Desc VARCHAR(250)" // 
                + ",PRIMARY KEY(ID)" //
                + ")")) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("テーブル作成に失敗: " + ex.toString(), ex);
        }
    }
}
