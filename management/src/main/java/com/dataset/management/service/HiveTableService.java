package com.dataset.management.service;

import com.dataset.management.entity.DataSet;
import com.dataset.management.entity.HiveTableMeta;
import com.dataset.management.entity.User;

public interface HiveTableService {
    boolean createTable(HiveTableMeta tableMeta, User user, DataSet dataSet);
}
