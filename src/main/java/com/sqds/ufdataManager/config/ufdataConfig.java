package com.sqds.ufdataManager.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages ={"com.sqds.ufdataManager.registory.ufdata"},
        entityManagerFactoryRef ="ufdataEntityManagerFactory",
        transactionManagerRef ="ufdataTransactionManager",
        repositoryImplementationPostfix ="Help"

)

public class ufdataConfig {
    @Value("jdbc:sqlserver://10.18.8.10:1433;DataBaseName=Ufdata_009_2023;encrypt=false;")
    private String url;
    @Value("sa")
    private String username;
    @Value("zxw123")
    private String password;
    @Value("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    private String DriveClassName;
    @Bean("ufdataDataSource")
    public DataSource ufdataDataSource(){
    DriverManagerDataSource dataSource=new DriverManagerDataSource();
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setUrl(url);
    dataSource.setDriverClassName(DriveClassName);
    return  dataSource;

    }
    @Bean("ufdatajpavendor")
    public JpaVendorAdapter ufdatajapvendor()
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter=new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);

        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServerDialect");
        return hibernateJpaVendorAdapter;
    }
    @Bean("ufdataEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ufdataEntityManagerFactory(
            @Qualifier("ufdataDataSource") DataSource ufdataDataSource,@Qualifier("ufdatajpavendor") JpaVendorAdapter ufdatajapvendor)
    {
        LocalContainerEntityManagerFactoryBean ufdatabean=new LocalContainerEntityManagerFactoryBean();
        ufdatabean.setDataSource(ufdataDataSource);
        ufdatabean.setJpaVendorAdapter(ufdatajapvendor);
        ufdatabean.setPersistenceUnitName("ufdata");
        ufdatabean.setPackagesToScan("com.sqds.ufdataManager.model.ufdata");
        return  ufdatabean;
    }
    @Bean(value="ufdataTransactionManager")
    public PlatformTransactionManager ufdataTransactionManager(@Qualifier("ufdataEntityManagerFactory") EntityManagerFactory ufdataEntityManagerFactory)
    {
        JpaTransactionManager jpaTransactionManager=new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(ufdataEntityManagerFactory);
        jpaTransactionManager.setNestedTransactionAllowed(true);
        jpaTransactionManager.setPersistenceUnitName("ufdata");
        return jpaTransactionManager;
    }

}
