package jp.igapyon.simpleodata4.entity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
            return new URI(entitySetName + "-" + String.valueOf(id));
        } catch (URISyntaxException ex) {
            throw new ODataRuntimeException("Fail to create ID EntitySet name: " + entitySetName, ex);
        }
    }
}
