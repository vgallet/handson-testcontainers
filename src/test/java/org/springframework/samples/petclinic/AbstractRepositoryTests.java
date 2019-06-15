package org.springframework.samples.petclinic;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.lang.invoke.MethodHandles;

@RunWith(SpringRunner.class)
@DataJpaTest(
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Repository.class)
)
@TestPropertySource(locations="classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractRepositoryTests {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GenericContainer genericContainer;

    @Before
    public void setUp() {
        genericContainer = new GenericContainer("mysql-petclinic");
        genericContainer.setExposedPorts(Lists.newArrayList(3306));
        genericContainer.setPortBindings(Lists.newArrayList("3306:3306"));
        genericContainer.waitingFor(Wait.forListeningPort());
        // print container log to LOGGER
        genericContainer.withLogConsumer(outputFrame ->
            LOGGER.debug(((OutputFrame)outputFrame).getUtf8String())
        );

        genericContainer.start();
    }

    @After
    public void tearDown() throws Exception {
        if(genericContainer != null) {
            genericContainer.stop();
        }
    }
}
