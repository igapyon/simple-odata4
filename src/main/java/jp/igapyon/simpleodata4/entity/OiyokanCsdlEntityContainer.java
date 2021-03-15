package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

public class OiyokanCsdlEntityContainer extends CsdlEntityContainer {

    /**
     * ネームスペース名.
     */
    private String namespace = "Igapyon.Simple";

    /**
     * EDMコンテナ名.
     */
    private String containerName = "Container";

    private List<OiyokanCsdlEntitySet> localEntityInfoList = new ArrayList<>();

    private void ensureInit() {
        if (localEntityInfoList.size() == 0) {
            localEntityInfoList.add(new OiyokanCsdlEntitySet(this, "MyProducts", "MyProduct", "MyProducts"));
            localEntityInfoList.add(new OiyokanCsdlEntitySet(this, "ODataAppInfos", "ODataAppInfo", "ODataAppInfos"));
        }
    }

    /**
     * 名前空間を取得します。これが存在するととても便利なため、これを追加。
     * 
     * @return 名前空間名.
     */
    public String getNamespace() {
        return namespace;
    }

    public String getInternalContainerName() {
        return containerName;
    }

    ///////////////////////////////
    /////////////////

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntityName(String entityName) {
        for (CsdlEntitySet entitySet : this.getEntitySets()) {
            OiyokanCsdlEntitySet look = (OiyokanCsdlEntitySet) entitySet;
            if (look.getInternalEntityName().equals(entityName)) {
                return look;
            }
        }
        
        return null;
    }

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        ensureInit();
        for (OiyokanCsdlEntitySet look : localEntityInfoList) {
            if (look.getInternalEntityNameFQN().equals(entityNameFQN)) {
                return look;
            }
        }
        return null;
    }

    ///////////////////////////////
    /////////////////

    public List<OiyokanCsdlEntitySet> getLocalEntityInfoList() {
        ensureInit();
        return localEntityInfoList;
    }

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getInternalContainerFQN() {
        return new FullQualifiedName(getNamespace(), getInternalContainerName());
    }
}
