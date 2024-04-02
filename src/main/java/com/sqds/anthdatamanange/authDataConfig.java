package com.sqds.anthdatamanange;

import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef ="authdataJpaAuthFactory",
 transactionManagerRef = "authdataTransactionAuthFactory",
 basePackages = {"com.sqds.anthdatamanange.data"})

@EntityScan("com.sqds.anthdatamanange.data")
public class authDataConfig {
    @Value("jdbc:sqlserver://10.18.8.32:1433;DatabaseName=author;encrypt=false")
    private  String url;
    @Value("sa")
    private  String username;
    @Value("zxw123")
    private  String password;
    @Value("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    private  String  driverclass;
    @Bean("authdatabase")
    @Primary
    public DataSource
    authDataSource(){

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverclass)
                .build();
    }
    @Bean("authdataJpaApter")
    @Primary
    public JpaVendorAdapter jpaVendorAdapter()
    {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter=new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServerDialect");
        hibernateJpaVendorAdapter.setGenerateDdl(false);

        hibernateJpaVendorAdapter.setShowSql(true);
        return  hibernateJpaVendorAdapter;
    }
    @Bean("authdataJpaAuthFactory")
    @Primary
   public LocalContainerEntityManagerFactoryBean authjpaauthfactory(
           @Qualifier("authdatabase")DataSource dataSource,
           @Qualifier("authdataJpaApter")JpaVendorAdapter jpaVendorAdapter)
    {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean=new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("com.sqds.anthdatamanange.data");
        return entityManagerFactoryBean;
    }
    @Bean("authdataTransactionAuthFactory")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("authdataJpaAuthFactory") EntityManagerFactory entityManagerFactory)
    {
       return  new JpaTransactionManager(entityManagerFactory);
    }

}

