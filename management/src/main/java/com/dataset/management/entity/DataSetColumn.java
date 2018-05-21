package com.dataset.management.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "dataset_column_info")
public class DataSetColumn implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false,name = "column_Name")
    private static String columnName;
    @Column(nullable = false,name = "datasetId")
    private static String datasetId;
    @Column(nullable = false,name = "datatype")
    private static String datatype;
    @Column(nullable = false,name = "comment")
    private static String comment;
    @ManyToOne
    @JoinColumn(name = "datasetId",insertable = false,updatable = false)
    private DataSystem dataSystem;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        DataSetColumn.columnName = columnName;
    }

    public String getDatatype() {
        return datatype;
    }
    public void setDatatype(String datatype) {
        DataSetColumn.datatype = datatype;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        DataSetColumn.comment = comment;
    }

    public static String getDatasetId() {
        return datasetId;
    }
    public static void setDatasetId(String datasetId) {
        DataSetColumn.datasetId = datasetId;
    }

    public DataSystem getDataSystem() {
        return dataSystem;
    }
    public void setDataSystem(DataSystem dataSystem) {
        this.dataSystem = dataSystem;
    }



}
