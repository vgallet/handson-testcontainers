/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.service;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.assertj.core.util.Lists;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test of the Service and the Repository layer.
 * <p>
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * ClinicServiceTests#clinicService clinicService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */

//@RunWith(SpringRunner.class)
//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
//@TestPropertySource(locations="classpath:application-test.properties")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClinicServiceTests {

    private static GenericContainer genericContainer;

//    @ClassRule
//    public static GenericContainer mysql = new GenericContainer("mysql-petclinic")
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

    //    @ClassRule
//    public static GenericContainer mysql = new GenericContainer(new ImageFromDockerfile("mysql-petclinic")
//        .withDockerfileFromBuilder(dockerfileBuilder -> {
//            dockerfileBuilder.from("mysql:5.7.8")
//                .env("MYSQL_ROOT_PASSWORD", "root_password")
//                .env("MYSQL_DATABASE", "petclinic")
//                .env("MYSQL_USER", "petclinic")
//                .env("MYSQL_PASSWORD", "petclinic")
//                .add("a_schema.sql", "/docker-entrypoint-initdb.d")
//                .add("b_data.sql", "/docker-entrypoint-initdb.d");
//        })
//        .withFileFromClasspath("a_schema.sql", "db/mysql/schema.sql")
//        .withFileFromClasspath("b_data.sql", "db/mysql/data.sql"))
//        .withExposedPorts(3306)
//        .withCreateContainerCmdModifier(
//            new Consumer<CreateContainerCmd>() {
//                @Override
//                public void accept(CreateContainerCmd createContainerCmd) {
//                    createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306)));
//                }
//            }
//        )
//        .waitingFor(Wait.forListeningPort());




}
