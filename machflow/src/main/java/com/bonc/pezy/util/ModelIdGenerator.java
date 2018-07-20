package com.bonc.pezy.util;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelIdGenerator implements IdentifierGenerator {
    private final Logger logger = LoggerFactory.getLogger(ModelIdGenerator.class);


    public Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {
        String prefix = "MDL";
        Connection connection = session.connection();
        try {
            PreparedStatement ps = connection
                    .prepareStatement("select _nextval('userid') as nextval");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("nextval");
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