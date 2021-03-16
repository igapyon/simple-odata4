/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package jp.igapyon.simpleodata4.oiyokan;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;

import jp.igapyon.simpleodata4.oiyokan.basic.BasicJdbcEntityTypeBuilder;

/**
 * CsdlEntitySet の Iyokan 拡張
 */
public class OiyokanCsdlEntitySet extends CsdlEntitySet {
    public enum DatabaseType {
        H2
    };

    /**
     * コンテナに関する情報を記憶.
     */
    private OiyokanCsdlEntityContainer csdlEntityContainer = null;

    private DatabaseType dbType = DatabaseType.H2;

    public DatabaseType getDatabaseType() {
        return dbType;
    }

    private CsdlEntityType entityType = null;

    public void setEntityType(CsdlEntityType entityType) {
        this.entityType = entityType;
    }

    public CsdlEntityType getEntityType() {
        return entityType;
    }

    /**
     * 要素型名. さしあたりはリレーショナルデータベースのテーブル名に相当するものと考えて差し支えない.
     * 
     * MyProduct 相当.
     */
    private String entityName = null;

    /**
     * データベース上のテーブル名.
     */
    private String dbTableName = null;

    /**
     * データベース上のテーブル名.
     */
    private String dbTableNameTarget = null;

    /**
     * EntityType生成ツール.
     */
    private BasicJdbcEntityTypeBuilder entityTypeBuilder = null;

    /**
     * エンティティ情報.
     * 
     * @param containerInfo     コンテナ情報.
     * @param entitySetName     MyProducts 相当.
     * @param entityName        MyProduct 相当.
     * @param dbType            データベースタイプ.
     * @param dbTableName       データベース上のテーブル名.
     * @param dbTableNameTarget ターゲットのデータベース上のテーブル名. 通常は null指定.
     */
    public OiyokanCsdlEntitySet(OiyokanCsdlEntityContainer containerInfo, String entitySetName, String entityName,
            DatabaseType dbType, String dbTableName, String dbTableNameTarget) {
        this.csdlEntityContainer = containerInfo;
        this.entityName = entityName;
        this.dbType = dbType;
        this.dbTableName = dbTableName;
        this.dbTableNameTarget = dbTableNameTarget;

        this.setName(entitySetName);
        this.setType(new FullQualifiedName(containerInfo.getNamespaceIyo(), entityName));

        this.entityTypeBuilder = new BasicJdbcEntityTypeBuilder(this);
    }

    /**
     * エンティティ名. MyProduct 相当.
     * 
     * @return エンティティ名. MyProduct 相当.
     */
    public String getEntityNameIyo() {
        return entityName;
    }

    /**
     * エンティティのFQNを取得.
     * 
     * @return エンティティのFQN(完全修飾名).
     */
    public FullQualifiedName getEntityNameFqnIyo() {
        return new FullQualifiedName(csdlEntityContainer.getNamespaceIyo(), getEntityNameIyo());
    }

    /**
     * 独自に追加した項目。
     * 
     * @return DBテーブル名。
     */
    public String getDbTableNameIyo() {
        return dbTableName;
    }

    /**
     * 独自に追加した項目。
     * 
     * @return ターゲットのDBテーブル名。指定がある場合は実データ取得の際にはこちらを利用.
     */
    public String getDbTableNameTargetIyo() {
        return dbTableNameTarget;
    }

    //////////////////////////////////

    /**
     * EntityType生成ツールを取得.
     * 
     * @return EntityType生成ツール.
     */
    public BasicJdbcEntityTypeBuilder getEdmBuilder() {
        return entityTypeBuilder;
    }
}
