package org.springframework.samples.petclinic;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.assertj.core.util.Lists;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@TestPropertySource(locations="classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractIntegrationTests {

    private static GenericContainer genericContainer;

//    static {
//        genericContainer = new GenericContainer("mysql-petclinic");
//        genericContainer.setExposedPorts(Lists.newArrayList(3306));
//        genericContainer.setPortBindings(Lists.newArrayList("3306:3306"));
//        genericContainer.waitingFor(Wait.forListeningPort());
//
//        genericContainer.start();
//    }

//    @Rule
//    public GenericContainer mysql = new GenericContainer("mysql-petclinic")
//        .withExposedPorts(3306)
//        .waitingFor(Wait.forListeningPort())
//        .withCreateContainerCmdModifier(
//            new Consumer<CreateContainerCmd>() {
//                @Override
//                public void accept(CreateContainerCmd createContainerCmd) {
//                    createContainerCmd.withPortBindings(
//                        new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306))
//                    );
//                }
//        });


//    @BeforeClass
//    public static void setUp() {
//        genericContainer = new GenericContainer("mysql-petclinic");
//        genericContainer.setExposedPorts(Lists.newArrayList(3306));
//        genericContainer.setPortBindings(Lists.newArrayList("3306:3306"));
//        genericContainer.waitingFor(Wait.forListeningPort());
//
//        genericContainer.start();
//    }
//
//    @AfterClass
//    public static void tearDown() throws Exception {
//        genericContainer.stop();
//    }
}
