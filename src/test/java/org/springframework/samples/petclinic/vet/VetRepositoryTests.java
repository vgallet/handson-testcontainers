package org.springframework.samples.petclinic.vet;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.AbstractRepositoryTests;
import org.springframework.samples.petclinic.service.EntityUtils;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class VetRepositoryTests extends AbstractRepositoryTests {

    @Autowired
    protected VetRepository vets;

    @Test
    public void shouldFindVets() {
        Collection<Vet> vets = this.vets.findAll();

        Vet vet = EntityUtils.getById(vets, Vet.class, 3);
        assertThat(vet.getLastName()).isEqualTo("Douglas");
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
        assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }
}
