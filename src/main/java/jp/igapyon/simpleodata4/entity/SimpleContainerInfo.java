package jp.igapyon.simpleodata4.entity;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class SimpleContainerInfo {
    /**
     * ネームスペース名.
     */
    private String namespace = "Igapyon.Simple";

    /**
     * EDMコンテナ名.
     */
    private String containerName = "Container";

    private SimpleEntityInfo localEntityInfo = null;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public SimpleEntityInfo getLocalEntityInfo() {
        return localEntityInfo;
    }

    public void setLocalEntityInfo(SimpleEntityInfo localEntityInfo) {
        this.localEntityInfo = localEntityInfo;
    }

    ///////////////////////////////
    /////////////////

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getContainerFQN() {
        return new FullQualifiedName(getNamespace(), getContainerName());
    }
}
