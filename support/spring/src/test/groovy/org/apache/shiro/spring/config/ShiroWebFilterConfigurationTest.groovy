package org.apache.shiro.spring.config

import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.spring.testconfig.RealmTestConfiguration
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration
import org.apache.shiro.web.filter.mgt.FilterChainManager
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests
import org.springframework.test.context.web.WebAppConfiguration

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.MatcherAssert.assertThat

/**
 * Test ShiroWebFilterConfiguration creates a ShiroFilterFactoryBean that contains Servlet filters that are available for injection.
 */
@WebAppConfiguration
@ContextConfiguration(classes = [RealmTestConfiguration, FilterConfiguration, ShiroConfiguration, ShiroWebFilterConfiguration])
class ShiroWebFilterConfigurationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean

    @Test
    void testShiroFilterFactoryBeanContainsSpringFilters() {

        assertThat shiroFilterFactoryBean, notNullValue()

        // create the filter chain manager
        FilterChainManager filterChainManager = shiroFilterFactoryBean.createFilterChainManager()
        // lookup the chain by name
        assertThat filterChainManager.getChain("/test-me"), contains(instanceOf(ExpectedTestFilter))
    }

    @Configuration
    static class FilterConfiguration {

        // random custom filter, which will be looked up via the shiroFilterFactoryBean
        @Bean
        Filter testFilter() {
            return new ExpectedTestFilter()
        }

        @Bean
        ShiroFilterChainDefinition shiroFilterChainDefinition() {
            DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition()
            chainDefinition.addPathDefinition("/test-me", "testFilter") // def matches the bean name
            chainDefinition.addPathDefinition("/**", "authc")
            return chainDefinition
        }
    }

    static class ExpectedTestFilter implements Filter {
        @Override
        void init(FilterConfig filterConfig) throws ServletException {}

        @Override
        void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {}

        @Override
        void destroy() {}
    }
}