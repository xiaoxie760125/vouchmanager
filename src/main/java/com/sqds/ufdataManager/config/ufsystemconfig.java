package com.sqds.ufdataManager.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages ={"com.sqds.ufdataManager.registory.ufsystem"},
        entityManagerFactoryRef ="ufsystmeEntityFactory",
        transactionManagerRef ="ufsystemTransactionManager"
)


public class ufsystemconfig {
    @Value("jdbc:sqlserver://10.18.8.10:1433;DataBaseName=Ufsystem;encrypt=false;")
    private String url;
    @Value("sa")
    private String username;
    @Value("zxw123")
    private String password;
    @Value("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    private String driverClassName;


    @Bean("ufsystemdatasource")
    public DataSource ufsystemDatasource()
    {
        return DataSourceBuilder.create().url(url).username(username).password(password).driverClassName(driverClassName).build();


    }

    @Bean("ufsystemjpa")
    public JpaVendorAdapter ufsystemjpa()
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter=new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServerDialect");
        return  hibernateJpaVendorAdapter;

    }
    @Bean("ufsystmeEntityFactory")
    public LocalContainerEntityManagerFactoryBean ufsystemEntityFactory(
            @Qualifier("ufsystemdatasource") DataSource ufdatasource,
            @Qualifier("ufsystemjpa") JpaVendorAdapter ufsystemjpa)
    {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean=new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(ufdatasource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(ufsystemjpa);
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("ufsystem");
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.sqds.ufdataManager.model.ufsystem");
        return localContainerEntityManagerFactoryBean;

    }
    @Bean(value = "ufsystemtranctionmannager",autowireCandidate = false)
    public PlatformTransactionManager ufsystemTransactionManager(@Qualifier("ufsystmeEntityFactory") EntityManagerFactory ufdatasource)
    {
        JpaTransactionManager jpaTransactionManager=new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(ufdatasource);
        return jpaTransactionManager;
    }





}
