package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * コンテナに関するローカル情報構造.
 */
public class SimpleContainerInfo {
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

    public String getInternalNamespace() {
        ensureInit();
        return namespace;
    }

    public void setInternalNamespace(String namespace) {
        ensureInit();
        this.namespace = namespace;
    }

    public String getInternalContainerName() {
        ensureInit();
        return containerName;
    }

    public void setInternalContainerName(String containerName) {
        ensureInit();
        this.containerName = containerName;
    }

    ///////////////////////////////
    /////////////////

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntityName(String entityName) {
        ensureInit();
        for (OiyokanCsdlEntitySet look : localEntityInfoList) {
            if (look.getInternalEntityName().equals(entityName)) {
                return look;
            }
        }
        return null;
    }

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntitySetName(String entitySetName) {
        ensureInit();
        for (OiyokanCsdlEntitySet look : localEntityInfoList) {
            if (look.getInternalEntitySetName().equals(entitySetName)) {
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
        return localEntityInfoList;
    }

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getInternalContainerFQN() {
        ensureInit();
        return new FullQualifiedName(getInternalNamespace(), getInternalContainerName());
    }
}
