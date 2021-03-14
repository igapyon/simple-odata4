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

    ///////////////////////////////
    /////////////////

    public SimpleEntityInfo getLocalEntityInfoByEntityName(String entityName) {
        System.err.println("TRACE: getLocalEntityInfoByEntityName(): " + entityName);
        return localEntityInfo;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntitySetName(String entitySetName) {
        System.err.println("TRACE: getLocalEntityInfoByEntitySetName(): " + entitySetName);
        return localEntityInfo;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        System.err.println("TRACE: getLocalEntityInfoByEntityNameFQN(): " + entityNameFQN);
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
