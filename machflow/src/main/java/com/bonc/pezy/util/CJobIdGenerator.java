package com.bonc.pezy.util;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CJobIdGenerator implements IdentifierGenerator {
    private final Logger logger = LoggerFactory.getLogger(CJobIdGenerator.class);


    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {
        String prefix = "CJOBID";
        Connection connection = session.connection();
        try {
            PreparedStatement ps = connection
                    .prepareStatement("select _nextval('currentjobid') as cJobnextval");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("cJobnextval");
                String code = prefix + StringUtils.leftPad("" + id,5, '0');
                //logger.debug("Generated Stock Code: " + code);
                System.out.println(code);
                return code;
            }
        } catch (SQLException e) {
            logger.error(null, e);
            throw new HibernateException(
                    "Unable to generate Stock Code Sequence");
        }
        return null;
    }
}