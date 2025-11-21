package com.pantavanij.sourcingreq.services.config.db;


import javax.sql.DataSource;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "sourcingReqEntityManager",
  transactionManagerRef = "sourcingReqTransactionManager",
  basePackages = "com.pantavanij.sourcingreq.services.repository.sourcingreq"
)
public class SourcingReqDbConfig {

    @Value("${spring.datasource.driverClassName}")
    private String diverClass;
    @Value("${spring.datasource.jdbc-url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Primary
    @LiquibaseDataSource
    @Bean(name = "sourcingReqDataSource")
    public DataSource SourcingReqDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(diverClass);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Primary
    @Bean(name = "sourcingReqEntityManager")
    public LocalContainerEntityManagerFactoryBean sourcingReqEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(SourcingReqDataSource());
        em.setPackagesToScan("com.pantavanij.sourcingreq.services.domain.entity.sourcingreq");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.hibernate.ddl-auto", "none");
        properties.put("spring.jpa.hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
        properties.put("spring.jpa.hibernate.naming.implicit-strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");
        properties.put("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "sourcingReqTransactionManager")
    public PlatformTransactionManager sourcingReqTransaction() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(sourcingReqEntityManager().getObject());
        return transactionManager;
    }

}
