package com.takirahal.srfgroup.modules.address.processors;

import com.takirahal.srfgroup.modules.address.entities.Address;
import com.takirahal.srfgroup.modules.address.repositories.AddressRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressTransactionItemWriter  implements ItemWriter<Address> {

    @Autowired
    AddressRepository addressRepository;

    @Override
    public void write(Chunk<? extends Address> list) throws Exception {
        System.out.printf("write Address list ");
        addressRepository.saveAll(list);
    }
}
