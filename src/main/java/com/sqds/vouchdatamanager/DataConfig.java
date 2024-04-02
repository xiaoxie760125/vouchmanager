package com.sqds.vouchdatamanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.TransactionManager;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "vouchentityManagerFactory",
        transactionManagerRef = "vouchtransactionManager",
        basePackages = {"com.sqds.vouchdatamanager.registroy","com.sqds.vouchdatamanager.model","com.sqds.vouchdatamanager.Help"},
        repositoryImplementationPostfix ="Help"
)

@EntityScan("com.sqds.vouchdatamanager.model")

public class DataConfig {
    @Value("jdbc:sqlserver://10.18.8.32:1433;databaseName=newsvouchs2025;encrypt=false;")
    private String url;
    @Value("sa")
    private String username;
    @Value("zxw123")
    private String password;
    @Value("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    private  String driverclass;


    @Bean("vouchdatasource")
    public DataSource vouchDataSource(){
        DriverManagerDataSource dataSource=new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverclass);

        return  dataSource;
    }
    @Bean("vouchentityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("vouchdatasource") DataSource dataSource,
                                                                       @Qualifier("vouchvendorAdapter") JpaVendorAdapter vendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean=new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.sqds.vouchdatamanager.registroy","com.sqds.vouchdatamanager.model","com.sqds.vouchdatamanager.Help");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("vouchpersistenceunit");
        return  localContainerEntityManagerFactoryBean;

    }
    @Bean("vouchvendorAdapter")
    public  JpaVendorAdapter jpaVendorAdapter()
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter=new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServerDialect");
        return  hibernateJpaVendorAdapter;
    }
    @Bean(value ="vouchtransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("vouchentityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager=new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return  jpaTransactionManager;
    }

}
