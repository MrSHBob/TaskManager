package taskmanager.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

@Configuration
@EntityScan(basePackages = "taskmanager.dao")
@EnableJpaRepositories(basePackages = "taskmanager.repo")
public class AppConfig {

    @Autowired
    ConfigProperties configProp;

    @Bean
    public DriverManagerDataSource conferenceDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(configProp.getConfigValue("db.mysql.driver"));
        ds.setUrl(configProp.getConfigValue("db.mysql.url"));
        ds.setUsername(configProp.getConfigValue("db.mysql.username"));
        ds.setPassword(configProp.getConfigValue("db.mysql.password"));
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setDataSource(conferenceDataSource());
        emFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProp = new Properties();
        jpaProp.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        emFactory.setJpaProperties(jpaProp);
        emFactory.setPackagesToScan("taskmanager");
        return emFactory;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
