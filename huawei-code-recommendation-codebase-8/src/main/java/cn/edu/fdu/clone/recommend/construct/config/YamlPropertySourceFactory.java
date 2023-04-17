package cn.edu.fdu.clone.recommend.construct.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource.getResource());
        factoryBean.afterPropertiesSet();
        Properties ymlProperties = factoryBean.getObject();
        String propertyName = name != null ? name : resource.getResource().getFilename();
        if (propertyName == null || ymlProperties == null){
            throw new IOException("yml file not exist");
        }
        return new PropertiesPropertySource(propertyName, ymlProperties);
    }
}
