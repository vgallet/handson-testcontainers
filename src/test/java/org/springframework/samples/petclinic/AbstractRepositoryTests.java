package org.springframework.samples.petclinic;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.AfterClass;
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
import org.testcontainers.containers.wait.strategy.Wait;

import java.lang.invoke.MethodHandles;

@RunWith(SpringRunner.class)
@DataJpaTest(
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Repository.class)
)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractRepositoryTests {

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static GenericContainer genericContainer;

    static {
        genericContainer = new GenericContainer("mysql-petclinic")
            .withExposedPorts(3306)
            .waitingFor(Wait.forListeningPort())
            .withCreateContainerCmdModifier(
                createContainerCmd -> ((CreateContainerCmd) createContainerCmd).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306))
                ));
        genericContainer.start();
    }

    // clean container
    @AfterClass
    public static void tearDown() {
        if (genericContainer != null) {
            genericContainer.stop();
        }
    }
}
